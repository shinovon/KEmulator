package emulator.ui.swt.devutils.idea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ProjectGenerator {

	public static String create(String location, String projectName, String midletName, String readableName) throws IOException {
		String dir = Paths.get(location, projectName).toAbsolutePath().toString();

		createDirectories(dir);

		// root
		Files.write(Paths.get(dir, ".gitignore"), ProjectConfigGenerator.rootGitignoreFile.getBytes(StandardCharsets.UTF_8));
		generateIML(projectName, dir);
		generateProGuardConfig(dir, projectName);

		// code
		Files.write(Paths.get(dir, "META-INF", "MANIFEST.MF"), ProjectConfigGenerator.buildManifest(projectName, midletName, readableName).getBytes(StandardCharsets.UTF_8));
		String midletCodePath = generateDummyMidlet(location, projectName, midletName, dir);

		// ide config
		generateMiscXmls(projectName, dir);

		// jars
		generateBuildConfigs(projectName, dir);

		// run configs
		generateRunConfigs(dir, projectName, midletName);

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

	private static void generateIML(String projectName, String dir) throws IOException {
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

	private static void generateBuildConfigs(String projectName, String dir) throws IOException {
		Files.write(Paths.get(dir, ".idea", "artifacts", projectName + ".xml"), ProjectConfigGenerator.buildArtifactConfig(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static void generateMiscXmls(String projectName, String dir) throws IOException {
		Files.write(Paths.get(dir, ".idea", "encodings.xml"), ProjectConfigGenerator.encodingFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", "misc.xml"), ProjectConfigGenerator.miscFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", ".gitignore"), ProjectConfigGenerator.ideaGitignoreFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", ".name"), projectName.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", "modules.xml"), ProjectConfigGenerator.buildModulesFile(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static String generateDummyMidlet(String location, String projectName, String midletName, String dir) throws IOException {
		Files.createDirectories(Paths.get(dir + "/src/" + ProjectConfigGenerator.splitByLastDot(midletName)[0].replace('.', '/')));
		String midletCodePath = location + "/" + projectName + "/src/" + midletName.replace('.', '/') + ".java";
		Files.write(Paths.get(midletCodePath), ProjectConfigGenerator.buildDummyMidlet(midletName).getBytes(StandardCharsets.UTF_8));
		return midletCodePath;
	}

	private static void generateProGuardConfig(String dir, String projName) throws IOException {
		Files.write(Paths.get(dir, "proguard.cfg"), ProjectConfigGenerator.buildProguardConfig(dir, projName).getBytes(StandardCharsets.UTF_8));
	}

	//#endregion
}
