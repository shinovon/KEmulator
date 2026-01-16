/*
Copyright (c) 2025-2026 Fyodor Ryzhov
*/
package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Utils;
import emulator.ui.swt.devutils.ClasspathEntry;
import emulator.ui.swt.devutils.ClasspathEntryType;
import emulator.ui.swt.devutils.DevtimeMIDlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Uses {@link ProjectConfigGenerator} to create project structure on disk.
 */
public class ProjectGenerator {

	public static final String PROGUARD_LOCAL_CFG = "proguard-local.cfg";
	public static final String PROGUARD_GLOBAL_CFG = "proguard.cfg";

	public static String create(String location, String projectName, String midletClassName, String readableName) throws IOException, ParserConfigurationException, TransformerException, SAXException {
		Path dir = Paths.get(location, projectName).toAbsolutePath();

		createDirectories(dir);

		// root
		Files.write(dir.resolve(".gitignore"), ProjectConfigGenerator.rootGitignoreFile.getBytes(StandardCharsets.UTF_8));
		ProjectConfigGenerator.generateIML(null, dir.resolve(projectName + ".iml"));
		generateProGuardConfig(dir, projectName, new ClasspathEntry[0]);

		// code
		Files.write(dir.resolve("META-INF").resolve("MANIFEST.MF"), ProjectConfigGenerator.buildManifest(projectName, midletClassName, readableName).getBytes(StandardCharsets.UTF_8));
		String midletCodePath = generateDummyMidlet(dir, midletClassName);

		// ide config
		generateMiscXmls(dir, projectName);

		// jars
		generateBuildConfigs(dir, projectName, false);

		// run configs
		generateRunConfigs(dir, projectName, new DevtimeMIDlet[]{new DevtimeMIDlet(midletClassName, readableName)}, false);

		return midletCodePath;
	}


	public static boolean restore(String dir) throws IOException, InterruptedException {
		Path dirp = Paths.get(dir);
		Path appDecrPath = dirp.resolve("Application Descriptor");
		Path mfPath = dirp.resolve("META-INF").resolve("MANIFEST.MF");
		Path imlPath = ClasspathEntry.findImlAt(dirp);

		createDirectories(dirp);

		DevtimeMIDlet[] midletNames;

		boolean isEclipse = Files.exists(appDecrPath);

		if (Files.exists(mfPath)) {
			if (isEclipse) {
				System.out.println("Warning: both \"Application Descriptor\" and \"MANIFEST.MF\" exist. Run configurations will be updated based on \"Application Descriptor\".");
				fixManifestWithVersion(mfPath);
				fixManifestWithVersion(appDecrPath);
				midletNames = DevtimeMIDlet.readMidletsList(appDecrPath);
			} else {
				fixManifestWithVersion(mfPath);
				midletNames = DevtimeMIDlet.readMidletsList(mfPath);
			}
		} else {
			if (isEclipse) {
				fixManifestWithVersion(appDecrPath);
				midletNames = DevtimeMIDlet.readMidletsList(appDecrPath);
			} else {
				throw new IllegalArgumentException("Neither \"Application Descriptor\" nor \"MANIFEST.MF\" files found!");
			}
		}

		String projectName = dirp.getFileName().toString();

		if (imlPath != null) {
			try {
				ClasspathEntry[] classpath = ClasspathEntry.readFromIml(imlPath);
				generateProGuardConfig(dirp, projectName, classpath);
			} catch (Exception e) {
				System.out.println("Failed to parse IML! No libraries will be exported.");
				generateProGuardConfig(dirp, projectName, new ClasspathEntry[0]);
			}

			// regenerate run configurations only if something is missing
			boolean generate = false;

			Path runConfigsPath = dirp.resolve(".idea").resolve("runConfigurations");
			if (!Files.exists(runConfigsPath)
					|| !Files.exists(runConfigsPath.resolve("Package.xml"))
					|| !Files.exists(runConfigsPath.resolve("Restore_project.xml"))) {
				generate = true;
			}
			for (int i = 1; i <= midletNames.length; ++i) {
				Path runPath = runConfigsPath.resolve("Run_with_KEmulator_" + i + ".xml");
				if (!Files.exists(runPath)) {
					generate = true;
					break;
				}
				if (!checkRunConfiguration(runPath, midletNames[i - 1])) {
					generate = true;
					break;
				}
			}

			if (generate) {
				generateRunConfigs(dirp, projectName, midletNames, isEclipse);
			} else {
				System.out.println("Skipping run configurations generation");
			}
			if (!"1.8 CLDC Devtime".equals(getProjectJdkName(dirp.resolve(".idea").resolve("misc.xml"))))
				System.out.println("For compatibility reasons, it's recommended to name project's JDK as \"1.8 CLDC Devtime\". " +
						"You can rerun IDE setup to bring your configuration to recommended one.");
		} else {
			System.out.println("No IML found! Run configuration will not be created.");
			generateProGuardConfig(dirp, projectName, new ClasspathEntry[0]);
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

		HashSet<String> gitignore = new HashSet<>();
		boolean needStartNl = false;
		Path gitignorePath = dir.resolve(".gitignore");
		if (Files.exists(gitignorePath)) {
			List<String> lines = Files.readAllLines(gitignorePath);
			gitignore = new HashSet<>(lines);
			long size = Files.size(gitignorePath);
			try (SeekableByteChannel channel = Files.newByteChannel(gitignorePath, StandardOpenOption.READ)) {
				ByteBuffer buffer = ByteBuffer.allocate(1);
				channel.position(size - 1);
				channel.read(buffer);
				buffer.flip();
				byte lastByte = buffer.get();
				needStartNl = !(lastByte == '\n' || lastByte == '\r');
			}
		}

		try (BufferedWriter gi = new BufferedWriter(new FileWriter(gitignorePath.toString(), true))) {
			if (needStartNl)
				gi.newLine();
			if (!gitignore.contains(".idea") && !gitignore.contains(".idea/") && !gitignore.contains(".idea/*") && !gitignore.contains(".idea/runConfigurations")) {
				gi.write(".idea/runConfigurations");
				gi.newLine();
			}
			if (!gitignore.contains(PROGUARD_GLOBAL_CFG)) {
				gi.write(PROGUARD_LOCAL_CFG);
				gi.newLine();
			}
		}
		ClasspathEntry[] cp = ProjectConfigGenerator.generateIML(dir.resolve(".classpath"), dir.resolve(projectName + ".iml"));
		generateProGuardConfig(dir, projectName, cp);

		// manifest
		fixManifestWithVersion(Paths.get(appDescriptorPath));

		DevtimeMIDlet[] midlets = DevtimeMIDlet.readMidletsList(dir.resolve("Application Descriptor"));

		// ide config
		generateMiscXmls(dir, projectName);

		// jars
		generateBuildConfigs(dir, projectName, true);

		// run configs
		generateRunConfigs(dir, projectName, midlets, true);

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

	private static void generateRunConfigs(Path dir, String projectName, DevtimeMIDlet[] midletNames, boolean eclipseManifest) throws IOException {
		for (int i = 0; i < midletNames.length; i++) {
			Path configPath = dir.resolve(".idea").resolve("runConfigurations").resolve("Run_with_KEmulator_" + (i + 1) + ".xml");
			String configText = ProjectConfigGenerator.buildKemRunConfig(projectName, midletNames[i].readableName, midletNames[i].className, eclipseManifest);
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

	private static void generateProGuardConfig(Path dir, String projName, ClasspathEntry[] classpath) throws IOException {
		Files.write(dir.resolve(PROGUARD_LOCAL_CFG), ProjectConfigGenerator.buildLocalProguardConfig(dir.toString(), projName, classpath).getBytes(StandardCharsets.UTF_8));
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

	public static String readManifestFromNetbeans(Path projPropsPath) throws IOException {
		List<String> lines = Files.readAllLines(projPropsPath);
		Hashtable<String, String> manifestProps = new Hashtable<>();
		for (String line : lines) {
			if (!line.startsWith("manifest.")) {
				continue;
			}
			String[] split = line.split("=", 2);
			manifestProps.put(split[0].substring("manifest.".length()), Utils.translateEscapes(split[1]));
		}
		return "Manifest-Version: 1.0\n" + manifestProps.get("midlets") + manifestProps.get("apipermissions") + manifestProps.get("others") + manifestProps.get("manifest");
	}

	private static boolean checkRunConfiguration(Path runPath, DevtimeMIDlet midletName) throws IOException {
		List<String> manifest = Files.readAllLines(runPath);
		boolean hasName = false;
		boolean hasParams = false;
		boolean hasWorkingDir = false;
		// NOTE: synchronize with ProjectConfigGenerator.buildKemRunConfig !
		for (String line : manifest) {
			String trimmed = line.trim();
			if (trimmed.startsWith("<configuration default=\"false\" name=\"Run &quot;")) {
				if (!line.contains("&quot;" + midletName.readableName + "&quot;")) {
					return false;
				}
				hasName = true;
				continue;
			}
			if (trimmed.startsWith("<option name=\"PROGRAM_PARAMETERS\" value=\"")) {
				if (!line.contains("-midlet " + midletName.className)) {
					return false;
				}
				hasParams = true;
				continue;
			}
			if (trimmed.startsWith("<option name=\"WORKING_DIRECTORY\" value=\"")) {
				if (!line.contains("\"" + Emulator.getAbsolutePath() + "\"")) {
					return false;
				}
				hasWorkingDir = true;
				continue;
			}
		}
		return hasName && hasParams && hasWorkingDir;
	}

	//#endregion
}
