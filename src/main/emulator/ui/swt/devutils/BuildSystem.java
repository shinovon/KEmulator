package emulator.ui.swt.devutils;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.swt.devutils.idea.ProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static emulator.ui.swt.devutils.ClasspathEntryType.*;

public class BuildSystem {

	private final Path projectRoot;
	private final boolean obfuscate;
	private final String javac;
	private final String java;
	private final String jar;
	private final String manifestName;
	private final String workingJarName;
	private ClasspathEntry[] classpath;
	private final String proguardJar;

	public BuildSystem(Path projectRoot, boolean obfuscate) {
		this.projectRoot = projectRoot;
		this.obfuscate = obfuscate;

		if (Emulator.isJava9()) {
			System.out.println("Must be run with JDK 1.8 or lower.");
			System.exit(1);
		}

		String jdkHome = Emulator.getJdkHome();
		if (jdkHome == null) {
			System.out.println("Must be run with JDK 1.8, not with JRE.");
			System.exit(1);
		}

		javac = Paths.get(jdkHome, "bin", Emulator.win ? "javac.exe" : "javac").toString();
		java = Paths.get(jdkHome, "bin", Emulator.win ? "java.exe" : "java").toString();
		jar = Paths.get(jdkHome, "bin", Emulator.win ? "jar.exe" : "jar").toString();

		if (Files.exists(this.projectRoot.resolve("Application Descriptor"))) {
			System.out.println("Found eclipse manifest, using...");
			manifestName = "Application Descriptor";
		} else if (Files.exists(this.projectRoot.resolve("META-INF/MANIFEST.MF"))) {
			System.out.println("Found standard manifest, using...");
			manifestName = "META-INF/MANIFEST.MF";
		} else {
			System.out.println("No manifests found!");
			System.exit(1);
			manifestName = null;
		}

		try {
			classpath = ClasspathEntry.readAnything(projectRoot);
		} catch (Exception e) {
			System.out.println("Failed to obtain classpath, see stacktrace:");
			e.printStackTrace();
			System.exit(1);
		}

		workingJarName = "." + File.separator + "deployed" + File.separator + "raw" + File.separator + projectRoot.getFileName().toString() + ".jar";

		if (Settings.proguardPath != null)
			proguardJar = Settings.proguardPath;
		else if (Files.exists(Paths.get(Emulator.getAbsolutePath(), "proguard.jar"))) {
			proguardJar = Paths.get(Emulator.getAbsolutePath(), "proguard.jar").toString();
		} else {
			System.out.println("Proguard jar is not in settings nor in local folder. Run Intellij IDEA setup and complete first step.");
			proguardJar = null;
			System.exit(1);
		}
	}

	public void build() {

		// create/clean folders

		Path rawJar = projectRoot.resolve(workingJarName);
		try {
			Files.createDirectories(projectRoot.resolve("bin"));
			Files.createDirectories(projectRoot.resolve("deployed").resolve("raw"));
			for (File f : projectRoot.resolve("bin").toFile().listFiles()) {
				String[] args = Emulator.win ? new String[]{"rd", "/s", "/q", f.getAbsolutePath()} : new String[]{"rm", "-rf", f.getAbsolutePath()};
				Runtime.getRuntime().exec(args).waitFor();
			}
			Files.deleteIfExists(rawJar);
		} catch (InterruptedException e) {
			System.out.println("Cancelled.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Failed to prepare project state");
			System.exit(1);
		}

		// javac

		ArrayList<String> javacArgs = new ArrayList<>();
		ArrayList<String> sourceFiles = new ArrayList<>();

		String builtClasspath = String.join(File.pathSeparator, Arrays.stream(classpath).filter(ClasspathEntry::isJar).map(x -> projectRoot.resolve(x.localPath).toString()).toArray(String[]::new));
		String builtSourcepath = String.join(File.pathSeparator, Arrays.stream(classpath).filter(ClasspathEntry::isSourceCode).map(e -> {
			Path folder = projectRoot.resolve(e.localPath);
			try (Stream<Path> walk = Files.walk(folder)) {
				walk.filter(x1 -> !Files.isDirectory(x1) && x1.getFileName().toString().endsWith(".java")).forEach(
						x1 -> sourceFiles.add(x1.toAbsolutePath().toString())
				);
			} catch (IOException ex) {
				System.out.println("Failed to list files in " + folder);
				System.exit(1);
			}
			return folder.toString();
		}).toArray(String[]::new));

		javacArgs.add(javac);
		javacArgs.add("-Xlint:-options");
		javacArgs.add("-bootclasspath");
		javacArgs.add(collectBootClassPath());
		javacArgs.add("-source");
		javacArgs.add("1.3");
		javacArgs.add("-target");
		javacArgs.add("1.3");
		javacArgs.add("-d");
		javacArgs.add("." + File.separator + "bin");
		if (!builtClasspath.isEmpty()) {
			javacArgs.add("-classpath");
			javacArgs.add(builtClasspath);
		}
		javacArgs.add("-sourcepath");
		javacArgs.add(builtSourcepath);
		javacArgs.addAll(sourceFiles);
		run("javac", new ProcessBuilder(javacArgs));

		// packing classes

		run("jar", new ProcessBuilder(jar, "cmf", manifestName, workingJarName, "-C", "bin", "."));

		// packing resources

		for (ClasspathEntry entry : classpath) {
			if (entry.type != Resource)
				continue;
			run("jar", new ProcessBuilder(jar, "uf", workingJarName, "-C", entry.localPath, "."));
		}

		// proguard merge & preverify & obfuscation

		try {
			ProjectGenerator.generateProGuardConfig(projectRoot, projectRoot.getFileName().toString(), Arrays.stream(classpath).filter(x -> x.type == ExportedLibrary).map(x -> x.localPath).toArray(String[]::new));
		} catch (IOException e) {
			System.out.println("Failed to generate proguard config");
			e.printStackTrace();
			System.exit(1);
		}

		ArrayList<String> proguardArgs = new ArrayList<>();
		proguardArgs.add(java);
		proguardArgs.add("-jar");
		proguardArgs.add(proguardJar);
		proguardArgs.add("@proguard-local.cfg");
		if (!obfuscate) {
			proguardArgs.add("-dontoptimize");
			proguardArgs.add("-dontshrink");
			proguardArgs.add("-dontobfuscate");
		} else if (Files.exists(projectRoot.resolve("proguard.cfg"))) {
			proguardArgs.add("@proguard.cfg");
		}
		run("proguard", new ProcessBuilder(proguardArgs));

		System.out.println("OK");
		System.exit(0);
	}

	private void run(String name, ProcessBuilder proc) {
		try {
			int code = proc.directory(projectRoot.toFile()).inheritIO().start().waitFor();
			if (code != 0) {
				System.out.println(name + " exited with code " + code);
				System.exit(1);
			}
		} catch (InterruptedException e) {
			System.out.println("Cancelled.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Failed to run " + name);
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static String collectBootClassPath() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (File f : Paths.get(Emulator.getAbsolutePath()).resolve("uei").toAbsolutePath().toFile().listFiles()) {
			if (!f.getName().endsWith(".jar"))
				continue;
			if (f.getName().equals("emulator.jar"))
				continue;
			if (!first) {
				sb.append(File.pathSeparator);
			}
			first = false;
			sb.append(f.getAbsolutePath());
		}
		return sb.toString();
	}
}
