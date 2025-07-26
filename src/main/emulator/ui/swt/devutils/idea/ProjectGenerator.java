package emulator.ui.swt.devutils.idea;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProjectGenerator {

	public static final String PROGUARD_LOCAL_CFG = "proguard-local.cfg";
	public static final String PROGUARD_GLOBAL_CFG = "proguard.cfg";

	public static String create(String location, String projectName, String midletClassName, String readableName) throws IOException {
		String dir = Paths.get(location, projectName).toAbsolutePath().toString();

		createDirectories(dir);

		// root
		Files.write(Paths.get(dir, ".gitignore"), ProjectConfigGenerator.rootGitignoreFile.getBytes(StandardCharsets.UTF_8));
		generateIML(dir, projectName);
		generateProGuardConfig(dir, projectName);

		// code
		Files.write(Paths.get(dir, "META-INF", "MANIFEST.MF"), ProjectConfigGenerator.buildManifest(projectName, midletClassName, readableName).getBytes(StandardCharsets.UTF_8));
		String midletCodePath = generateDummyMidlet(dir, midletClassName);

		// ide config
		generateMiscXmls(dir, projectName);

		// jars
		generateBuildConfigs(dir, projectName);

		// run configs
		generateRunConfigs(dir, projectName, midletClassName);

		return midletCodePath;
	}


	public static String fixCloned(String imlPath) throws IOException {
		if (!imlPath.endsWith(".iml"))
			throw new IllegalArgumentException();
		String dir = Paths.get(imlPath).getParent().toString();
		String projectName = Paths.get(imlPath).getFileName().toString();
		projectName = projectName.substring(0, projectName.lastIndexOf('.'));
		String midletName = getMidletClassNameFromMF(dir);

		createDirectories(dir);
		generateProGuardConfig(dir, projectName);
		generateRunConfigs(dir, projectName, midletName);

		return dir;
	}

	public static String convertEclipse(String appDescriptorPath) throws IOException {
		String dir = Paths.get(appDescriptorPath).getParent().toString();
		String projectName = Paths.get(appDescriptorPath).getParent().getFileName().toString(); //folder name

		createDirectories(dir);

		// root

		try (BufferedWriter gi = new BufferedWriter(new FileWriter(Paths.get(dir, ".gitignore").toString(), true))) {
			gi.write(".idea/runConfigurations");
			gi.newLine();
			gi.write(PROGUARD_LOCAL_CFG);
			gi.newLine();
		}
		generateIML(dir, projectName);
		generateProGuardConfig(dir, projectName);

		// code
		List<String> manifest = Files.readAllLines(Paths.get(appDescriptorPath));
		boolean hasVersion = false;
		for (String line : manifest) {
			if (line.startsWith("Manifest-Version:")) {
				hasVersion = true;
				break;
			}
		}
		if (!hasVersion) {
			manifest.add(0, "Manifest-Version: 1.0");
		}
		Files.write(Paths.get(dir, "META-INF", "MANIFEST.MF"), manifest);
		String midletClassName = getMidletClassNameFromMF(dir);

		// ide config
		generateMiscXmls(dir, projectName);

		// jars
		generateBuildConfigs(dir, projectName);

		// run configs
		generateRunConfigs(dir, projectName, midletClassName);

		return dir;
	}

	//#region impls

	private static void createDirectories(String dir) throws IOException {
		Files.createDirectories(Paths.get(dir, ".idea"));
		Files.createDirectories(Paths.get(dir, ".idea", "artifacts"));
		Files.createDirectories(Paths.get(dir, ".idea", "runConfigurations"));
		Files.createDirectories(Paths.get(dir, "src"));
		Files.createDirectories(Paths.get(dir, "res"));
		Files.createDirectories(Paths.get(dir, "bin"));
		Files.createDirectories(Paths.get(dir, "deployed"));
		Files.createDirectories(Paths.get(dir, "META-INF"));
	}

	private static void generateIML(String dir, String projectName) throws IOException {
		Files.write(Paths.get(dir, projectName + ".iml"), ProjectConfigGenerator.imlFile.getBytes(StandardCharsets.UTF_8));
	}

	private static String getMidletClassNameFromMF(String dir) throws IOException {
		String midletName = null;
		List<String> manifest = Files.readAllLines(Paths.get(dir, "META-INF", "MANIFEST.MF"), StandardCharsets.UTF_8);
		for (String line : manifest) {
			if (line.startsWith("MIDlet-1:")) {
				String[] split = line.split(",");
				midletName = split[split.length - 1].trim();
				break;
			}
		}
		if (midletName == null)
			throw new IllegalArgumentException("Broken manifest file");
		return midletName;
	}

	private static void generateRunConfigs(String dir, String projectName, String midletName) throws IOException {
		Files.write(Paths.get(dir, ".idea", "runConfigurations", "Run_with_KEmulator.xml"), ProjectConfigGenerator.buildKemRunConfig(projectName, midletName).getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", "runConfigurations", "Package.xml"), ProjectConfigGenerator.buildPackageRunConfig(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static void generateBuildConfigs(String dir, String projectName) throws IOException {
		Files.write(Paths.get(dir, ".idea", "artifacts", projectName + ".xml"), ProjectConfigGenerator.buildArtifactConfig(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static void generateMiscXmls(String dir, String projectName) throws IOException {
		Files.write(Paths.get(dir, ".idea", "encodings.xml"), ProjectConfigGenerator.encodingFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", "misc.xml"), ProjectConfigGenerator.miscFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", ".gitignore"), ProjectConfigGenerator.ideaGitignoreFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", ".name"), projectName.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", "modules.xml"), ProjectConfigGenerator.buildModulesFile(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static String generateDummyMidlet(String dir, String midletName) throws IOException {
		String srcFolder = Paths.get(dir, "src").toString();
		Path midletCodePath;
		if (midletName.indexOf('.') != -1) {
			String[] splitted = ProjectConfigGenerator.splitByLastDot(midletName);
			String[] packagesNames = splitted[0].split("\\.");
			Path midletFolder = Paths.get(srcFolder, packagesNames);
			Files.createDirectories(midletFolder);
			midletCodePath = midletFolder.resolve(splitted[1] + ".java");
			Files.write(midletCodePath, ProjectConfigGenerator.buildDummyMidlet(midletName).getBytes(StandardCharsets.UTF_8));
		} else {
			midletCodePath = Paths.get(srcFolder, midletName + ".java");
			Files.write(midletCodePath, ProjectConfigGenerator.buildDummyMidlet(midletName).getBytes(StandardCharsets.UTF_8));
		}

		return midletCodePath.toString();
	}

	private static void generateProGuardConfig(String dir, String projName) throws IOException {
		Files.write(Paths.get(dir, PROGUARD_LOCAL_CFG), ProjectConfigGenerator.buildLocalProguardConfig(dir, projName).getBytes(StandardCharsets.UTF_8));
		if (!Files.exists(Paths.get(dir, PROGUARD_GLOBAL_CFG))) {
			Files.write(Paths.get(dir, PROGUARD_GLOBAL_CFG), System.lineSeparator().getBytes(StandardCharsets.UTF_8));
		}
	}

	//#endregion
}
