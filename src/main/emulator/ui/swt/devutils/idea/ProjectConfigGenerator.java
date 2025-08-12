package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.swt.devutils.ClasspathEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ProjectConfigGenerator {

	public static final String encodingFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<project version=\"4\">\n" +
			"  <component name=\"Encoding\" addBOMForNewFiles=\"with BOM under Windows, with no BOM otherwise\" />\n" +
			"</project>";

	public static final String miscFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<project version=\"4\">\n" +
			"  <component name=\"ProjectRootManager\" version=\"2\" languageLevel=\"JDK_1_3\" project-jdk-name=\"1.8 CLDC Devtime\" project-jdk-type=\"JavaSDK\">\n" +
			"    <output url=\"file://$PROJECT_DIR$/bin\" />\n" +
			"  </component>\n" +
			"</project>";

	public static final String rootGitignoreFile = "### IntelliJ IDEA ###\n" +
			"out/\n" +
			"!**/src/main/**/out/\n" +
			"!**/src/test/**/out/\n" +
			ProjectGenerator.PROGUARD_LOCAL_CFG + "\n" +
			".idea/runConfigurations\n" +
			"\n" +
			"### Eclipse ###\n" +
			".apt_generated\n" +
			".classpath\n" +
			".factorypath\n" +
			".project\n" +
			".settings\n" +
			"bin/\n" +
			"deployed/\n" +
			"\n" +
			"### NetBeans ###\n" +
			"/nbproject/private/\n" +
			"/nbbuild/\n" +
			"/dist/\n" +
			"/nbdist/\n" +
			"/.nb-gradle/\n" +
			"\n" +
			"### VS Code ###\n" +
			".vscode/\n" +
			"\n" +
			"### Mac OS ###\n" +
			".DS_Store";

	public static final String ideaGitignoreFile = "# Default ignored files\n" +
			"/shelf/\n" +
			"/workspace.xml\n" +
			"# Editor-based HTTP Client requests\n" +
			"/httpRequests/\n" +
			"# Datasource local storage ignored files\n" +
			"/dataSources/\n" +
			"/dataSources.local.xml\n";

	public static String buildLocalProguardConfig(String dir, String name, String[] libs) {
		StringBuilder sb = new StringBuilder();
		for (String l : JdkTablePatcher.DEV_TIME_JARS) {
			sb.append("-libraryjars '");
			sb.append(Paths.get(Emulator.getAbsolutePath(), "uei", l));
			sb.append("'");
			sb.append(System.lineSeparator());
		}
		sb.append("-injars '").append(Paths.get(dir, "deployed", "raw", name + ".jar")).append("'");
		sb.append(System.lineSeparator());
		for (int i = 0; i < libs.length; i++) {
			sb.append("-injars '").append(Paths.get(dir, libs[i])).append("'");
			sb.append(System.lineSeparator());
		}
		sb.append("-outjars '").append(Paths.get(dir, "deployed", name + ".jar")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-printseeds '").append(Paths.get(dir, "deployed", "pro_seeds.txt")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-printmapping '").append(Paths.get(dir, "deployed", "pro_map.txt")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-dontusemixedcaseclassnames -dontnote -defaultpackage '' -microedition -target 1.3 -optimizations !library/*,!code/simplification/object");
		sb.append(System.lineSeparator());
		sb.append("-keep public class * extends javax.microedition.midlet.MIDlet");
		sb.append(System.lineSeparator());
		sb.append("-assumenosideeffects public class java.lang.StringBuffer {");
		sb.append(System.lineSeparator());
		sb.append("    public java.lang.String toString();");
		sb.append(System.lineSeparator());
		sb.append("    public char charAt(int);");
		sb.append(System.lineSeparator());
		sb.append("    public int capacity();");
		sb.append(System.lineSeparator());
		sb.append("    public int codePointAt(int);");
		sb.append(System.lineSeparator());
		sb.append("    public int codePointBefore(int);");
		sb.append(System.lineSeparator());
		sb.append("    public int indexOf(java.lang.String,int);");
		sb.append(System.lineSeparator());
		sb.append("    public int lastIndexOf(java.lang.String);");
		sb.append(System.lineSeparator());
		sb.append("    public int lastIndexOf(java.lang.String,int);");
		sb.append(System.lineSeparator());
		sb.append("    public int length();");
		sb.append(System.lineSeparator());
		sb.append("    public java.lang.String substring(int);");
		sb.append(System.lineSeparator());
		sb.append("    public java.lang.String substring(int,int);");
		sb.append(System.lineSeparator());
		sb.append("}");
		sb.append(System.lineSeparator());
		return sb.toString();
	}

	public static String buildManifest(String projName, String className, String appName) {
		StringBuilder sb = new StringBuilder();

		sb.append("Manifest-Version: 1.0").append(System.lineSeparator());
		sb.append("MicroEdition-Configuration: CLDC-1.1").append(System.lineSeparator());
		sb.append("MicroEdition-Profile: MIDP-2.0").append(System.lineSeparator());
		sb.append("MIDlet-Vendor: Anonymous developer").append(System.lineSeparator());
		sb.append("MIDlet-Version: 0.0.1").append(System.lineSeparator());
		sb.append("MIDlet-Name: ").append(appName).append(System.lineSeparator());
		sb.append("MIDlet-1: ").append(appName).append(",,").append(className).append(System.lineSeparator());
		sb.append("MIDlet-Jar-URL: ").append(projName).append(".jar").append(System.lineSeparator());

		return sb.toString();
	}

	public static String buildDummyMidlet(String className) {
		String clsName;
		StringBuilder sb = new StringBuilder();

		if (className.indexOf('.') != -1) {
			String[] splitted = splitByLastDot(className);
			clsName = splitted[1];
			sb.append("package ").append(splitted[0]).append(";").append(System.lineSeparator());
		} else {
			clsName = className;
		}

		sb.append("import javax.microedition.lcdui.Display;").append(System.lineSeparator());
		sb.append("import javax.microedition.lcdui.Form;").append(System.lineSeparator());
		sb.append("import javax.microedition.midlet.MIDlet;").append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append("public class ").append(clsName).append(" extends MIDlet {").append(System.lineSeparator());
		sb.append("    protected void startApp() {").append(System.lineSeparator());
		sb.append("        Form f = new Form(\"").append(clsName).append("\");").append(System.lineSeparator());
		sb.append("        f.append(\"Your MIDlet seems to run.\");").append(System.lineSeparator());
		sb.append("        Display.getDisplay(this).setCurrent(f);").append(System.lineSeparator());
		sb.append("    }").append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append("    protected void pauseApp() {").append(System.lineSeparator());
		sb.append("    }").append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append("    protected void destroyApp(boolean unconditional) {").append(System.lineSeparator());
		sb.append("    }").append(System.lineSeparator());
		sb.append("}").append(System.lineSeparator());

		return sb.toString();
	}

	public static String buildModulesFile(String projectName) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(System.lineSeparator());
		sb.append("<project version=\"4\">").append(System.lineSeparator());
		sb.append("  <component name=\"ProjectModuleManager\">").append(System.lineSeparator());
		sb.append("    <modules>").append(System.lineSeparator());
		sb.append("      <module fileurl=\"file://$PROJECT_DIR$/")
				.append(projectName)
				.append(".iml\" filepath=\"$PROJECT_DIR$/")
				.append(projectName)
				.append(".iml\" />")
				.append(System.lineSeparator());
		sb.append("    </modules>").append(System.lineSeparator());
		sb.append("  </component>").append(System.lineSeparator());
		sb.append("</project>");

		return sb.toString();
	}

	public static String buildArtifactConfig(String projectName, boolean eclipseManifest) {
		StringBuilder sb = new StringBuilder();

		sb.append("<component name=\"ArtifactManager\">").append(System.lineSeparator());
		sb.append("  <artifact type=\"jar\" name=\"").append(projectName).append("\">").append(System.lineSeparator());
		sb.append("    <output-path>$PROJECT_DIR$/deployed/raw</output-path>").append(System.lineSeparator());
		sb.append("    <root id=\"archive\" name=\"").append(projectName).append(".jar\">").append(System.lineSeparator());
		sb.append("      <element id=\"directory\" name=\"META-INF\">").append(System.lineSeparator());
		if (eclipseManifest)
			sb.append("        <element id=\"file-copy\" output-file-name=\"MANIFEST.MF\" path=\"$PROJECT_DIR$/Application Descriptor\" />").append(System.lineSeparator());
		else
			sb.append("        <element id=\"file-copy\" path=\"$PROJECT_DIR$/META-INF/MANIFEST.MF\" />").append(System.lineSeparator());
		sb.append("      </element>").append(System.lineSeparator());
		sb.append("      <element id=\"module-output\" name=\"").append(projectName).append("\" />").append(System.lineSeparator());
		sb.append("    </root>").append(System.lineSeparator());
		sb.append("  </artifact>").append(System.lineSeparator());
		sb.append("</component>");

		return sb.toString();
	}

	public static String buildKemRunConfig(String projectName, String midletName, String className) {
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Run &quot;" + midletName.replace("&", "&amp;").replace("\"", "&quot;") + "&quot; with KEmulator\" type=\"Application\" factoryName=\"Application\">\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"MAIN_CLASS_NAME\" value=\"emulator.Emulator\" />\n" +
				"    <module name=\"" + projectName + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"-cp $PROJECT_DIR$/bin/production/" + projectName + " -midlet " + className + " -jad $PROJECT_DIR$/META-INF/MANIFEST.MF -uei\" />\n" +
				"    <option name=\"VM_PARAMETERS\" value=\"-XX:+IgnoreUnrecognizedVMOptions -Djna.nosys=true -Dfile.encoding=UTF-8 -XstartOnFirstThread\" />" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"" + Emulator.getAbsolutePath() + "\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"Make\" enabled=\"true\" />\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
	}

	public static String buildRestoreRunConfig(String projectName) {
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Restore project\" type=\"Application\" factoryName=\"Application\">\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"MAIN_CLASS_NAME\" value=\"emulator.Emulator\" />\n" +
				"    <module name=\"" + projectName + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"-restore $PROJECT_DIR$\" />\n" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"" + Emulator.getAbsolutePath() + "\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"Make\" enabled=\"true\" />\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
	}

	public static String buildPackageRunConfig(String projectName) {
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Package\" type=\"JarApplication\">\n" +
				"    <option name=\"JAR_PATH\" value=\"" + Settings.proguardPath + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"@" + ProjectGenerator.PROGUARD_LOCAL_CFG + " @" + ProjectGenerator.PROGUARD_GLOBAL_CFG + "\" />\n" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"$PROJECT_DIR$\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"RunConfigurationTask\" enabled=\"true\" run_configuration_name=\"Restore project\" run_configuration_type=\"Application\" />" +
				"      <option name=\"BuildArtifacts\" enabled=\"true\">\n" +
				"        <artifact name=\"" + projectName + "\" />\n" +
				"      </option>\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
	}

	public static String[] generateIML(Path eclipseClasspath, Path imlFile) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();

		ArrayList<String> libsList = new ArrayList<>();

		Element module = doc.createElement("module");
		module.setAttribute("type", "JAVA_MODULE");
		module.setAttribute("version", "4");
		doc.appendChild(module);

		Element component = doc.createElement("component");
		component.setAttribute("name", "NewModuleRootManager");
		component.setAttribute("LANGUAGE_LEVEL", "JDK_1_3");
		component.setAttribute("inherit-compiler-output", "true");
		module.appendChild(component);

		component.appendChild(doc.createElement("exclude-output"));

		Element content = doc.createElement("content");
		content.setAttribute("url", "file://$MODULE_DIR$");
		component.appendChild(content);

		Element jdkEntry = doc.createElement("orderEntry");
		jdkEntry.setAttribute("type", "inheritedJdk");
		component.appendChild(jdkEntry);

		Element sourceEntry = doc.createElement("orderEntry");
		sourceEntry.setAttribute("type", "sourceFolder");
		sourceEntry.setAttribute("forTests", "false");
		component.appendChild(sourceEntry);

		if (eclipseClasspath == null) {
			addSourceFolder(doc, content, "src", false);
			addSourceFolder(doc, content, "res", true);
		} else {
			ClasspathEntry[] existing;
			if (Files.exists(eclipseClasspath)) {
				existing = ClasspathEntry.readFromEclipse(eclipseClasspath);
			} else {
				existing = ClasspathEntry.readFromConfigless(eclipseClasspath.getParent());
				System.out.println("Warning! Classpath file is missing. Default paths were guessed.");
			}
			for (ClasspathEntry entry : existing) {
				switch (entry.type) {
					case Source:
					case LibrarySource:
						addSourceFolder(doc, content, entry.localPath, false);
						break;
					case Resource:
						addSourceFolder(doc, content, entry.localPath, true);
						break;
					case HeaderLibrary:
						addLibrary(doc, component, entry.localPath, false);
						libsList.add(entry.localPath);
						break;
					case ExportedLibrary:
						addLibrary(doc, component, entry.localPath, true);
						libsList.add(entry.localPath);
						break;
				}
			}
		}

		Element excludeFolder = doc.createElement("excludeFolder");
		excludeFolder.setAttribute("url", "file://$MODULE_DIR$/deployed");
		content.appendChild(excludeFolder);

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(imlFile.toFile());
		transformer.transform(source, result);

		return libsList.toArray(new String[0]);
	}

	private static void addSourceFolder(Document doc, Element content, String path, boolean isRes) {
		Element sourceFolder = doc.createElement("sourceFolder");
		sourceFolder.setAttribute("url", "file://$MODULE_DIR$/" + path);
		if (isRes)
			sourceFolder.setAttribute("type", "java-resource");
		else
			sourceFolder.setAttribute("isTestSource", "false");
		content.appendChild(sourceFolder);
	}

	private static void addLibrary(Document doc, Element component, String path, boolean exported) {
		Element libEntry = doc.createElement("orderEntry");
		libEntry.setAttribute("type", "module-library");
		if (exported)
			libEntry.setAttribute("exported", "");
		component.appendChild(libEntry);

		Element libRoot = doc.createElement("library");
		libEntry.appendChild(libRoot);

		Element classes = doc.createElement("CLASSES");
		libRoot.appendChild(classes);

		Element jar = doc.createElement("root");
		jar.setAttribute("url", "jar://$MODULE_DIR$/" + path + "!/");
		classes.appendChild(jar);

		libRoot.appendChild(doc.createElement("JAVADOC"));
		libRoot.appendChild(doc.createElement("SOURCES"));
	}

	public static String[] extractLibrariesListFromIML(Path imlPath) throws ParserConfigurationException, IOException, SAXException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(imlPath.toFile());

		ArrayList<String> result = new ArrayList<>();
		NodeList orderEntries = doc.getElementsByTagName("orderEntry");

		for (int i = 0; i < orderEntries.getLength(); i++) {
			Element orderEntry = (Element) orderEntries.item(i);
			String type = orderEntry.getAttribute("type");
			String exported = orderEntry.getAttribute("exported");

			if ("module-library".equals(type) && "".equals(exported)) {
				Element library = (Element) orderEntry.getElementsByTagName("library").item(0);
				Element classes = (Element) library.getElementsByTagName("CLASSES").item(0);
				NodeList roots = classes.getElementsByTagName("root");

				for (int i1 = 0; i1 < roots.getLength(); i1++) {
					Element root = (Element) roots.item(i1);
					String url = root.getAttribute("url");

					String path = url.substring("jar://$MODULE_DIR$/".length(), url.length() - 2);
					result.add(path);
				}
			}
		}
		return result.toArray(new String[0]);
	}

	public static String[] splitByLastDot(String input) {
		int lastDotIndex = input.lastIndexOf('.');
		if (lastDotIndex == -1) {
			return new String[]{input, ""};
		}

		String firstPart = input.substring(0, lastDotIndex);
		String secondPart = input.substring(lastDotIndex + 1);

		return new String[]{firstPart, secondPart};
	}
}
