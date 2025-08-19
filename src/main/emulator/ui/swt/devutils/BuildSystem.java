package emulator.ui.swt.devutils;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.swt.devutils.idea.ProjectConfigGenerator;
import emulator.ui.swt.devutils.idea.ProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static emulator.ui.swt.devutils.ClasspathEntryType.*;
import static emulator.ui.swt.devutils.idea.ProjectGenerator.PROGUARD_GLOBAL_CFG;
import static emulator.ui.swt.devutils.idea.ProjectGenerator.PROGUARD_LOCAL_CFG;

public class BuildSystem {

	private final Path projectRoot;
	private final String javac;
	private final String java;
	private final String jar;
	private final String manifestName;
	private final String workingJarName;
	private final String projectName;
	private ClasspathEntry[] classpath;
	private final String proguardJar;

	// options
	private final boolean obfuscate;
	private final boolean anyres;
	private final boolean runAfter;

	public BuildSystem(String[] args) {
		if (args.length < 2)
			throw new IllegalArgumentException("When invoking build system, at least two arguments are required, trigger key and path to project directory.");
		if (!"-build".equals(args[0]))
			throw new IllegalArgumentException("When invoking build system, first argument must be string \"-build\".");


		projectRoot = Paths.get(args[1]).toAbsolutePath();
		if (!Files.exists(projectRoot)) {
			System.out.println("Project directory does not exist: " + projectRoot);
			System.exit(1);
		}

		HashSet<String> opts = new HashSet<>(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));

		obfuscate = opts.contains("-obf");
		anyres = opts.contains("-anyres");
		runAfter = opts.contains("-run");

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

		System.out.println("Building project at " + projectRoot);

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

		projectName = projectRoot.getFileName().toString();
		workingJarName = "." + File.separator + "deployed" + File.separator + "raw" + File.separator + projectName + ".jar";

		if (Settings.proguardPath != null) {
			proguardJar = Settings.proguardPath;
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
		run("jar", new ProcessBuilder(jar, "cmf", manifestName, workingJarName, "-C", "bin", "."), true);

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
			String localConfig = ProjectConfigGenerator.buildLocalProguardConfig(projectRoot.toString(), projectName, projectName, libs);
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
			if (!obfuscate) {
				proguardArgs.add("-dontoptimize");
				proguardArgs.add("-dontshrink");
				proguardArgs.add("-dontobfuscate");
			} else if (Files.exists(projectRoot.resolve("proguard.cfg"))) {
				proguardArgs.add("@proguard.cfg");
			}
			run("proguard", new ProcessBuilder(proguardArgs), true);
		}

		// cleanup

		try {
			Files.deleteIfExists(projectRoot.resolve("deployed").resolve("raw").resolve("build.cfg"));
			Files.deleteIfExists(rawJar);
		} catch (IOException ignored) {
		}

		System.out.println("Build OK");

		if (runAfter) {
			System.out.println("Running built JAR...");
			Emulator.main(new String[]{"-jar", projectRoot.resolve("deployed").resolve(projectName + ".jar").toString()});
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
