package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProjectGenerator {

	public static final String PROGUARD_LOCAL_CFG = "proguard-local.cfg";
	public static final String PROGUARD_GLOBAL_CFG = "proguard.cfg";
	public static final String SYMLINK_FAIL_MSG = "Please do something so symlinks begin to work at your project location. " +
			"This may include tweaking your FS/mount options, moving the project to another FS, enabling Windows Developer Mode, running from admin/sudo, etc. " +
			"Also check git settings for related options.";

	public static String create(String location, String projectName, String midletClassName, String readableName) throws IOException, ParserConfigurationException, TransformerException, SAXException {
		Path dir = Paths.get(location, projectName).toAbsolutePath();

		createDirectories(dir);

		// root
		Files.write(dir.resolve(".gitignore"), ProjectConfigGenerator.rootGitignoreFile.getBytes(StandardCharsets.UTF_8));
		ProjectConfigGenerator.generateIML(null, dir.resolve(projectName + ".iml"));
		generateProGuardConfig(dir, projectName, new String[0]);

		// code
		Files.write(dir.resolve("META-INF").resolve("MANIFEST.MF"), ProjectConfigGenerator.buildManifest(projectName, midletClassName, readableName).getBytes(StandardCharsets.UTF_8));
		String midletCodePath = generateDummyMidlet(dir, midletClassName);

		// ide config
		generateMiscXmls(dir, projectName);

		// jars
		generateBuildConfigs(dir, projectName, false);

		// run configs
		generateRunConfigs(dir, projectName, new String[][]{new String[]{readableName, midletClassName}});

		return midletCodePath;
	}


	public static boolean fixCloned(String dir) throws IOException, InterruptedException {
		File folder = new File(dir);
		String imlPath = null;
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".iml")) {
				imlPath = file.getAbsolutePath();
				break;
			}
		}
		Path dirp = Paths.get(dir);
		Path appDecrPath = dirp.resolve("Application Descriptor");
		Path mfPath = dirp.resolve("META-INF").resolve("MANIFEST.MF");

		createDirectories(dirp);

		String[][] midletNames;

		boolean isEclipse = Files.exists(appDecrPath);

		if (Files.exists(mfPath)) {
			if (isEclipse) {
				System.out.println("Warning: both \"Application Descriptor\" and \"MANIFEST.MF\" exist. Run configurations will be updated based on \"Application Descriptor\".");
				fixManifestWithVersion(mfPath);
				fixManifestWithVersion(appDecrPath);
				midletNames = getMidletsFromMF(appDecrPath);
			} else {
				fixManifestWithVersion(mfPath);
				midletNames = getMidletsFromMF(mfPath);
			}
		} else {
			if (isEclipse) {
				fixManifestWithVersion(appDecrPath);
				midletNames = getMidletsFromMF(appDecrPath);
			} else {
				throw new IllegalArgumentException("Neither \"Application Descriptor\" nor \"MANIFEST.MF\" files found!");
			}
		}

		String projectName = dirp.getFileName().toString();

		if (imlPath != null) {
			try {
				generateProGuardConfig(dirp, projectName, ProjectConfigGenerator.extractLibrariesListFromIML(Paths.get(imlPath)));
			} catch (Exception e) {
				System.out.println("Failed to parse IML! No libraries will be exported.");
				generateProGuardConfig(dirp, projectName, new String[0]);
			}
			generateRunConfigs(dirp, projectName, midletNames);
			if (!"1.8 CLDC Devtime".equals(getProjectJdkName(dirp.resolve(".idea").resolve("misc.xml"))))
				System.out.println("For compatibility reasons, it's recommended to name project's JDK as \"1.8 CLDC Devtime\". " +
						"You can rerun IDE setup to bring your configuration to recommended one.");
		} else {
			System.out.println("No IML found! Run configuration will not be created.");
			generateProGuardConfig(dirp, projectName, new String[0]);
		}

		return imlPath != null;
	}

	public static String convertEclipse(String appDescriptorPath) throws IOException, InterruptedException, ParserConfigurationException, TransformerException, SAXException {
		Path dir = Paths.get(appDescriptorPath).getParent().toAbsolutePath();
		String projectName = dir.getFileName().toString(); //folder name

		if (Files.exists(dir.resolve("META-INF").resolve("MANIFEST.MF"))) {
			System.out.println("MANIFEST.MF found! It will be ignored, converted projects use \"Application Descriptor\".");
		}

		createDirectories(dir);

		// root

		HashSet<String> gitignore = new HashSet<>(Files.readAllLines(dir.resolve(".gitignore")));

		try (BufferedWriter gi = new BufferedWriter(new FileWriter(dir.resolve(".gitignore").toString(), true))) {
			if (!gitignore.contains(".idea") && !gitignore.contains(".idea/") && !gitignore.contains(".idea/*") && !gitignore.contains(".idea/runConfigurations")) {
				gi.write(".idea/runConfigurations");
				gi.newLine();
			}
			if (!gitignore.contains(PROGUARD_GLOBAL_CFG)) {
				gi.write(PROGUARD_LOCAL_CFG);
				gi.newLine();
			}
		}
		String[] libraries = ProjectConfigGenerator.generateIML(dir.resolve(".classpath"), dir.resolve(projectName + ".iml"));
		generateProGuardConfig(dir, projectName, libraries);

		// manifest
		fixManifestWithVersion(Paths.get(appDescriptorPath));

		String[][] midlets = getMidletsFromMF(dir.resolve("Application Descriptor"));

		// ide config
		generateMiscXmls(dir, projectName);

		// jars
		generateBuildConfigs(dir, projectName, true);

		// run configs
		generateRunConfigs(dir, projectName, midlets);

		return dir.toString();
	}

	//#region impls

	private static void fixManifestWithVersion(Path manifestPath) throws IOException {
		List<String> manifest = Files.readAllLines(manifestPath);
		boolean hasVersion = false;
		for (String line : manifest) {
			if (line.startsWith("Manifest-Version:")) {
				hasVersion = true;
				break;
			}
		}
		if (!hasVersion) {
			manifest.add(0, "Manifest-Version: 1.0");
			Files.write(manifestPath, manifest);
		}
	}

	private static void createDirectories(Path dir) throws IOException {
		Files.createDirectories(dir.resolve(".idea"));
		Files.createDirectories(dir.resolve(".idea").resolve("artifacts"));
		Files.createDirectories(dir.resolve(".idea").resolve("runConfigurations"));
		createDirSilently(dir.resolve("src")); // some folders may be symlinks. This is a valid case (but NIO doesn't think so)
		createDirSilently(dir.resolve("res"));
		createDirSilently(dir.resolve("bin"));
		createDirSilently(dir.resolve("deployed"));
		createDirSilently(dir.resolve("META-INF"));
	}

	private static void createDirSilently(Path path) throws IOException {
		try {
			Files.createDirectories(path);
		} catch (FileAlreadyExistsException ignored) {
		}
	}

	private static String[][] getMidletsFromMF(Path manifestPath) throws IOException {
		ArrayList<String[]> names = new ArrayList<>();
		List<String> lines = Files.readAllLines(manifestPath, StandardCharsets.UTF_8);
		HashMap<String, String> manifest = new HashMap<>();
		for (String line : lines) {
			String[] split = line.split(":", 2);
			manifest.put(split[0], split[1]);
		}
		for (int i = 1; true; i++) {
			if (!manifest.containsKey("MIDlet-" + i)) {
				break;
			}
			String[] split = manifest.get("MIDlet-" + i).split(",");
			names.add(new String[]{split[0].trim(), split[2].trim()});
		}
		if (names.isEmpty())
			throw new IllegalArgumentException("Broken manifest file");
		return names.toArray(new String[0][0]);
	}

	private static void generateRunConfigs(Path dir, String projectName, String[][] midletNames) throws IOException {
		for (int i = 0; i < midletNames.length; i++) {
			Path configPath = dir.resolve(".idea").resolve("runConfigurations").resolve("Run_with_KEmulator_" + (i + 1) + ".xml");
			String configText = ProjectConfigGenerator.buildKemRunConfig(projectName, midletNames[i][0], midletNames[i][1]);
			Files.write(configPath, configText.getBytes(StandardCharsets.UTF_8));
		}
		Files.write(dir.resolve(".idea").resolve("runConfigurations").resolve("Package.xml"), ProjectConfigGenerator.buildPackageRunConfig(projectName).getBytes(StandardCharsets.UTF_8));
		Files.write(dir.resolve(".idea").resolve("runConfigurations").resolve("Restore_project.xml"), ProjectConfigGenerator.buildRestoreRunConfig(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static void generateBuildConfigs(Path dir, String projectName, boolean eclipseManifest) throws IOException {
		Path path = dir.resolve(".idea").resolve("artifacts").resolve(projectName + ".xml");
		Files.write(path, ProjectConfigGenerator.buildArtifactConfig(projectName, eclipseManifest).getBytes(StandardCharsets.UTF_8));
	}

	private static void generateMiscXmls(Path dir, String projectName) throws IOException {
		Files.write(dir.resolve(".idea").resolve("encodings.xml"), ProjectConfigGenerator.encodingFile.getBytes(StandardCharsets.UTF_8));
		Files.write(dir.resolve(".idea").resolve("misc.xml"), ProjectConfigGenerator.miscFile.getBytes(StandardCharsets.UTF_8));
		Files.write(dir.resolve(".idea").resolve(".gitignore"), ProjectConfigGenerator.ideaGitignoreFile.getBytes(StandardCharsets.UTF_8));
		Files.write(dir.resolve(".idea").resolve(".name"), projectName.getBytes(StandardCharsets.UTF_8));
		Files.write(dir.resolve(".idea").resolve("modules.xml"), ProjectConfigGenerator.buildModulesFile(projectName).getBytes(StandardCharsets.UTF_8));
	}

	private static String generateDummyMidlet(Path dir, String midletName) throws IOException {
		Path src = dir.resolve("src");
		Path midletCodePath;
		if (midletName.indexOf('.') != -1) {
			String[] splitted = ProjectConfigGenerator.splitByLastDot(midletName);
			Path midletFolder = Paths.get(src.toString(), splitted[0].replace('.', File.separatorChar));
			Files.createDirectories(midletFolder);
			midletCodePath = midletFolder.resolve(splitted[1] + ".java");
			Files.write(midletCodePath, ProjectConfigGenerator.buildDummyMidlet(midletName).getBytes(StandardCharsets.UTF_8));
		} else {
			midletCodePath = src.resolve(midletName + ".java");
			Files.write(midletCodePath, ProjectConfigGenerator.buildDummyMidlet(midletName).getBytes(StandardCharsets.UTF_8));
		}

		return midletCodePath.toString();
	}

	private static void generateProGuardConfig(Path dir, String projName, String[] libs) throws IOException {
		Files.write(dir.resolve(PROGUARD_LOCAL_CFG), ProjectConfigGenerator.buildLocalProguardConfig(dir.toString(), projName, libs).getBytes(StandardCharsets.UTF_8));
		if (!Files.exists(dir.resolve(PROGUARD_GLOBAL_CFG))) {
			Files.write(dir.resolve(PROGUARD_GLOBAL_CFG), System.lineSeparator().getBytes(StandardCharsets.UTF_8));
		}
	}

	public static String getProjectJdkName(Path miscXml) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(miscXml.toFile());
			NodeList components = doc.getElementsByTagName("component");
			for (int i = 0; i < components.getLength(); i++) {
				Element component = (Element) components.item(i);
				if ("ProjectRootManager".equals(component.getAttribute("name"))) {
					return component.getAttribute("project-jdk-name");
				}
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	//#endregion
}
