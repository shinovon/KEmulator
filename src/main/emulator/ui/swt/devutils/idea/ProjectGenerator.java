package emulator.ui.swt.devutils.idea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ProjectGenerator {

	public static String create(String location, String projectName, String midletName, String readableName) throws IOException {
		Files.createDirectories(Paths.get(location, projectName));
		Files.createDirectories(Paths.get(location, projectName, ".idea"));
		Files.createDirectories(Paths.get(location, projectName, ".idea", "artifacts"));
		Files.createDirectories(Paths.get(location, projectName, ".idea", "runConfigurations"));
		Files.createDirectories(Paths.get(location, projectName, "src"));
		Files.createDirectories(Paths.get(location + "/" + projectName + "/src/" + ProjectConfigGenerator.splitByLastDot(midletName)[0].replace('.', '/')));
		Files.createDirectories(Paths.get(location, projectName, "res"));
		Files.createDirectories(Paths.get(location, projectName, "bin"));
		Files.createDirectories(Paths.get(location, projectName, "deployed"));
		Files.createDirectories(Paths.get(location, projectName, "META-INF"));

		// root
		Files.write(Paths.get(location, projectName, ".gitignore"), ProjectConfigGenerator.rootGitignoreFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, projectName + ".iml"), ProjectConfigGenerator.imlFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, "proguard.cfg"), ProjectConfigGenerator.buildProguardConfig(location + "/" + projectName, projectName).getBytes(StandardCharsets.UTF_8));

		// code
		Files.write(Paths.get(location, projectName, "META-INF", "MANIFEST.MF"), ProjectConfigGenerator.buildManifest(projectName, midletName, readableName).getBytes(StandardCharsets.UTF_8));
		String midletCodePath = location + "/" + projectName + "/src/" + midletName.replace('.', '/') + ".java";
		Files.write(Paths.get(midletCodePath), ProjectConfigGenerator.buildDummyMidlet(midletName).getBytes(StandardCharsets.UTF_8));

		// ide config
		Files.write(Paths.get(location, projectName, ".idea", "encodings.xml"), ProjectConfigGenerator.encodingFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, ".idea", "misc.xml"), ProjectConfigGenerator.miscFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, ".idea", ".gitignore"), ProjectConfigGenerator.ideaGitignoreFile.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, ".idea", ".name"), projectName.getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, ".idea", "modules.xml"), ProjectConfigGenerator.buildModulesFile(projectName).getBytes(StandardCharsets.UTF_8));

		// jars
		Files.write(Paths.get(location, projectName, ".idea", "artifacts", projectName + ".xml"), ProjectConfigGenerator.buildArtifactConfig(projectName).getBytes(StandardCharsets.UTF_8));

		// run configs
		Files.write(Paths.get(location, projectName, ".idea", "runConfigurations", "Run_with_KEmulator.xml"), ProjectConfigGenerator.buildKemRunConfig(projectName, midletName).getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(location, projectName, ".idea", "runConfigurations", "Package.xml"), ProjectConfigGenerator.buildPackageRunConfig(projectName).getBytes(StandardCharsets.UTF_8));

		return midletCodePath;
	}

	public static String fixCloned(String imlPath) throws IOException {
		if (!imlPath.endsWith(".iml"))
			throw new IllegalArgumentException();
		String dir = Paths.get(imlPath).getParent().toString();
		String projName = Paths.get(imlPath).getFileName().toString();
		projName = projName.substring(0, projName.lastIndexOf('.'));

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

		Files.createDirectories(Paths.get(dir, "bin"));
		Files.createDirectories(Paths.get(dir, "deployed"));
		Files.createDirectories(Paths.get(dir, ".idea", "runConfigurations"));

		Files.write(Paths.get(dir, "proguard.cfg"), ProjectConfigGenerator.buildProguardConfig(dir, projName).getBytes(StandardCharsets.UTF_8));

		Files.write(Paths.get(dir, ".idea", "runConfigurations", "Run_with_KEmulator.xml"), ProjectConfigGenerator.buildKemRunConfig(projName, midletName).getBytes(StandardCharsets.UTF_8));
		Files.write(Paths.get(dir, ".idea", "runConfigurations", "Package.xml"), ProjectConfigGenerator.buildPackageRunConfig(projName).getBytes(StandardCharsets.UTF_8));

		return dir;
	}

}
