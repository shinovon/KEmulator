package emulator.build;

import emulator.Emulator;
import emulator.ui.swt.devutils.ClasspathEntry;
import emulator.ui.swt.devutils.idea.ProjectConfigGenerator;
import emulator.ui.swt.devutils.idea.ProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static emulator.ui.swt.devutils.ClasspathEntryType.*;

public class BuildSystem {

	private final Path projectRoot;
	private final String javac;
	private final String java;
	private final String jar;
	private Path manifestPath;
	private final String workingJarName;
	private final String projectName;
	private ClasspathEntry[] classpath;
	private final String proguardJar;
	private boolean deleteManifest = false;

	// options
	private final boolean obfuscate;
	private final boolean anyres;
	private final boolean runAfter;

	public BuildSystem(Path projectRoot, HashSet<String> args, Properties kemProps) {
		this.projectRoot = projectRoot;
		obfuscate = args.contains("--obf");
		anyres = args.contains("--anyres");
		runAfter = args.contains("--run");

		if (Emulator.isJava9()) {
			System.out.println("Must be run with JDK 1.8.");
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

		System.out.println("Building project at " + projectRoot);

		if (Files.exists(projectRoot.resolve("Application Descriptor"))) {
			System.out.println("Found eclipse manifest, using...");
			manifestPath = projectRoot.resolve("Application Descriptor");
		} else if (Files.exists(projectRoot.resolve("META-INF/MANIFEST.MF"))) {
			System.out.println("Found standard manifest, using...");
			manifestPath = projectRoot.resolve("META-INF/MANIFEST.MF");
		} else if (Files.exists(projectRoot.resolve("MANIFEST.MF"))) {
			System.out.println("Found dangling manifest, using...");
			manifestPath = projectRoot.resolve("MANIFEST.MF");
		} else if (Files.exists(projectRoot.resolve("nbproject/project.properties"))) {
			try {
				String manifest = ProjectGenerator.readManifestFromNetbeans(projectRoot.resolve("nbproject/project.properties"));
				manifestPath = Paths.get(Emulator.getUserPath()).resolve("NB_GENERATED_MANIFEST_" + System.currentTimeMillis() + ".MF");
				deleteManifest = true;
				System.out.println("Found NetBeans configuration. Manifest will be generated based on it.");
				Files.write(manifestPath, manifest.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				System.out.println("Failed to read NetBeans configuration. Exception: " + e);
				System.exit(1);
			}
		} else {
			System.out.println("No manifests found!");
			System.exit(1);
		}

		try {
			classpath = ClasspathEntry.readAnything(projectRoot);
		} catch (Exception e) {
			System.out.println("Failed to obtain classpath, see stacktrace:");
			e.printStackTrace();
			System.exit(1);
		}

		projectName = projectRoot.getFileName().toString();
		workingJarName = "." + File.separator + "deployed" + File.separator + "raw" + File.separator + projectName + ".jar";

		if (kemProps != null && kemProps.getProperty("ProguardPath", null) != null) {
			proguardJar = kemProps.getProperty("ProguardPath");
		} else if (Files.exists(Paths.get(Emulator.getAbsolutePath()).resolve("proguard.jar"))) {
			proguardJar = Paths.get(Emulator.getAbsolutePath()).resolve("proguard.jar").toString();
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
		{
			System.out.println("Collecting sources...");
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
					System.out.println(ex);
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
			System.out.println("Running javac...");
			run("javac", new ProcessBuilder(javacArgs), false);
		}
		// packing classes

		System.out.println("Packing classes");
		run("jar", new ProcessBuilder(jar, "cmf", manifestPath.toAbsolutePath().toString(), workingJarName, "-C", "bin", "."), true);

		// packing resources

		for (ClasspathEntry entry : classpath) {
			if (entry.type == Resource) {
				System.out.println("Packing resources from " + entry.localPath);
				run("jar", new ProcessBuilder(jar, "uf", workingJarName, "-C", entry.localPath, "."), true);
			} else if (entry.isSourceCode() && anyres) {
				System.out.println("Packing resources from " + entry.localPath);
				ArrayList<String> jarArgs = new ArrayList<>();
				jarArgs.add(jar);
				jarArgs.add("uf");
				jarArgs.add(workingJarName);

				try (Stream<Path> walk = Files.walk(projectRoot.resolve(entry.localPath))) {
					walk.filter(x1 -> !Files.isDirectory(x1) && !x1.getFileName().toString().endsWith(".java")).forEach(
							x1 -> {
								Path folder = projectRoot.resolve(entry.localPath);
								String localPath = folder.relativize(x1).toString();
								jarArgs.add("-C");
								jarArgs.add("." + File.separator + entry.localPath);
								jarArgs.add(localPath);
							}
					);
				} catch (IOException ex) {
					System.out.println("Failed to list files in " + entry.localPath);
					System.out.println(ex);
					System.exit(1);
				}
				if (jarArgs.size() > 3) // if 3, no files were appended.
					run("jar", new ProcessBuilder(jarArgs), true);
			}
		}

		// proguard merge & preverify & obfuscation

		try {
			String[] libs = Arrays.stream(classpath).filter(x -> x.type == ExportedLibrary).map(x -> x.localPath).toArray(String[]::new);
			String localConfig = ProjectConfigGenerator.buildLocalProguardConfig(projectRoot.toString(), projectName, libs);
			if (!obfuscate) {
				localConfig += "-dontoptimize -dontshrink -dontobfuscate" + System.lineSeparator() +
						"-keep class *" + System.lineSeparator();
			}
			Files.write(projectRoot.resolve("deployed").resolve("raw").resolve("build.cfg"), localConfig.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			System.out.println("Failed to generate proguard config");
			e.printStackTrace();
			System.exit(1);
		}
		{
			ArrayList<String> proguardArgs = new ArrayList<>();
			proguardArgs.add(java);
			proguardArgs.add("-jar");
			proguardArgs.add(proguardJar);
			proguardArgs.add("@deployed" + File.separator + "raw" + File.separator + "build.cfg");
			if (obfuscate && Files.exists(projectRoot.resolve("proguard.cfg"))) {
				proguardArgs.add("@proguard.cfg");
			}
			run("proguard", new ProcessBuilder(proguardArgs), true);
		}

		// cleanup

		try {
			if(deleteManifest)
				Files.deleteIfExists(manifestPath);
			Files.deleteIfExists(projectRoot.resolve("deployed").resolve("raw").resolve("build.cfg"));
			Files.deleteIfExists(rawJar);
		} catch (IOException ignored) {
		}

		System.out.println("Build OK");

		if (runAfter) {
			System.out.println("Running built JAR...");
			run("kemulator", new ProcessBuilder(java, "-jar", Emulator.getAbsolutePath() + File.separator + "KEmulator.jar", "-jar", projectRoot.resolve("deployed").resolve(projectName + ".jar").toString()), false);
		}

		System.exit(0);
	}

	private void run(String name, ProcessBuilder pb, boolean bufferOutput) {
		try {
			StringBuilder sw = new StringBuilder();
			if (!bufferOutput)
				pb.inheritIO();
			Process proc = pb.directory(projectRoot.toFile()).start();
			if (bufferOutput) {
				try (InputStream is = proc.getInputStream()) {
					int c;
					while ((c = is.read()) != -1)
						sw.append((char) c);
				}
				sw.append(System.lineSeparator());
				try (InputStream is = proc.getErrorStream()) {
					int c;
					while ((c = is.read()) != -1)
						sw.append((char) c);
				}
			}
			int code = proc.waitFor();
			if (code != 0) {
				System.out.println(name + " exited with code " + code);
				if (bufferOutput) {
					System.out.println("Log:");
					System.out.println(sw);
				}
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