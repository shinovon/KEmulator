/*
Copyright (c) 2025-2026 Fyodor Ryzhov
*/
package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import emulator.ui.swt.devutils.ClasspathEntry;
import emulator.ui.swt.devutils.ClasspathEntryType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

/**
 * Generates various configuration files for projects.
 */
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

	public static String buildLocalProguardConfig(String dir, String name, ClasspathEntry[] classpath) {
		final String nl = System.lineSeparator();
		StringBuilder sb = new StringBuilder();
		sb.append("# This config file is gitignored and reset on each deploy.");
		sb.append(nl);
		sb.append("# Actual configuration should live in another file, \"proguard.cfg\" by default.");
		sb.append(nl);
		for (String l : JdkTablePatcher.getDevTimeJars()) {
			sb.append("-libraryjars '");
			sb.append(l);
			sb.append("'");
			sb.append(nl);
		}
		for (ClasspathEntry c : classpath) {
			if (c.type != ClasspathEntryType.HeaderLibrary)
				continue;
			sb.append("-libraryjars '");
			if (c.isLocalPath)
				sb.append(Paths.get(dir).resolve(c.path));
			else
				sb.append(c.path);
			sb.append("'");
			sb.append(nl);
		}
		sb.append("-printseeds '").append(Paths.get(dir, "deployed", "pro_seeds.txt")).append("'");
		sb.append(nl);
		sb.append("-printmapping '").append(Paths.get(dir, "deployed", "pro_map.txt")).append("'");
		sb.append(nl);
		sb.append("-dontusemixedcaseclassnames -dontnote -defaultpackage '' -microedition -target 1.2 -optimizations !library/*,!code/simplification/object");
		sb.append(nl);
		sb.append("-keep public class * extends javax.microedition.midlet.MIDlet");
		sb.append(nl);
		sb.append("-assumenosideeffects public class java.lang.StringBuffer {");
		sb.append(nl);
		sb.append("    public java.lang.String toString();");
		sb.append(nl);
		sb.append("    public char charAt(int);");
		sb.append(nl);
		sb.append("    public int capacity();");
		sb.append(nl);
		sb.append("    public int codePointAt(int);");
		sb.append(nl);
		sb.append("    public int codePointBefore(int);");
		sb.append(nl);
		sb.append("    public int indexOf(java.lang.String,int);");
		sb.append(nl);
		sb.append("    public int lastIndexOf(java.lang.String);");
		sb.append(nl);
		sb.append("    public int lastIndexOf(java.lang.String,int);");
		sb.append(nl);
		sb.append("    public int length();");
		sb.append(nl);
		sb.append("    public java.lang.String substring(int);");
		sb.append(nl);
		sb.append("    public java.lang.String substring(int,int);");
		sb.append(nl);
		sb.append("}");
		sb.append(nl);
		return sb.toString();
	}

	public static String buildManifest(String projName, String className, String appName) {
		final String nl = System.lineSeparator();
		StringBuilder sb = new StringBuilder();

		sb.append("Manifest-Version: 1.0").append(nl);
		sb.append("MicroEdition-Configuration: CLDC-1.1").append(nl);
		sb.append("MicroEdition-Profile: MIDP-2.0").append(nl);
		sb.append("MIDlet-Vendor: Anonymous developer").append(nl);
		sb.append("MIDlet-Version: 0.0.1").append(nl);
		sb.append("MIDlet-Name: ").append(appName).append(nl);
		sb.append("MIDlet-1: ").append(appName).append(",,").append(className).append(nl);
		sb.append("MIDlet-Jar-URL: ").append(projName).append(".jar").append(nl);

		return sb.toString();
	}

	public static String buildDummyMidlet(String className) {
		final String nl = System.lineSeparator();
		String clsName;
		StringBuilder sb = new StringBuilder();

		if (className.indexOf('.') != -1) {
			String[] splitted = splitByLastDot(className);
			clsName = splitted[1];
			sb.append("package ").append(splitted[0]).append(";").append(nl);
		} else {
			clsName = className;
		}

		sb.append("import javax.microedition.lcdui.Display;").append(nl);
		sb.append("import javax.microedition.lcdui.Form;").append(nl);
		sb.append("import javax.microedition.midlet.MIDlet;").append(nl);
		sb.append(nl);
		sb.append("public class ").append(clsName).append(" extends MIDlet {").append(nl);
		sb.append("    protected void startApp() {").append(nl);
		sb.append("        Form f = new Form(\"").append(clsName).append("\");").append(nl);
		sb.append("        f.append(\"Your MIDlet seems to run.\");").append(nl);
		sb.append("        Display.getDisplay(this).setCurrent(f);").append(nl);
		sb.append("    }").append(nl);
		sb.append(nl);
		sb.append("    protected void pauseApp() {").append(nl);
		sb.append("    }").append(nl);
		sb.append(nl);
		sb.append("    protected void destroyApp(boolean unconditional) {").append(nl);
		sb.append("    }").append(nl);
		sb.append("}").append(nl);

		return sb.toString();
	}

	public static String buildModulesFile(String projectName) {
		final String nl = System.lineSeparator();
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(nl);
		sb.append("<project version=\"4\">").append(nl);
		sb.append("  <component name=\"ProjectModuleManager\">").append(nl);
		sb.append("    <modules>").append(nl);
		sb.append("      <module fileurl=\"file://$PROJECT_DIR$/")
				.append(projectName)
				.append(".iml\" filepath=\"$PROJECT_DIR$/")
				.append(projectName)
				.append(".iml\" />")
				.append(nl);
		sb.append("    </modules>").append(nl);
		sb.append("  </component>").append(nl);
		sb.append("</project>");

		return sb.toString();
	}

	public static String buildArtifactConfig(String projectName, boolean eclipseManifest) {
		final String nl = System.lineSeparator();
		StringBuilder sb = new StringBuilder();

		sb.append("<component name=\"ArtifactManager\">").append(nl);
		sb.append("  <artifact type=\"jar\" name=\"").append(projectName).append("\">").append(nl);
		sb.append("    <output-path>$PROJECT_DIR$/deployed/raw</output-path>").append(nl);
		sb.append("    <root id=\"archive\" name=\"").append(projectName).append(".jar\">").append(nl);
		sb.append("      <element id=\"directory\" name=\"META-INF\">").append(nl);
		if (eclipseManifest)
			sb.append("        <element id=\"file-copy\" output-file-name=\"MANIFEST.MF\" path=\"$PROJECT_DIR$/Application Descriptor\" />").append(nl);
		else
			sb.append("        <element id=\"file-copy\" path=\"$PROJECT_DIR$/META-INF/MANIFEST.MF\" />").append(nl);
		sb.append("      </element>").append(nl);
		sb.append("      <element id=\"module-output\" name=\"").append(projectName).append("\" />").append(nl);
		sb.append("    </root>").append(nl);
		sb.append("  </artifact>").append(nl);
		sb.append("</component>");

		return sb.toString();
	}

	public static String buildKemRunConfig(String moduleName, String midletName, String className, boolean eclipseManifest) {
		String manifestPath = eclipseManifest ? "Application Descriptor" : "META-INF/MANIFEST.MF";
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Launch &quot;" + midletName.replace("&", "&amp;").replace("\"", "&quot;") + "&quot; with KEmulator\" type=\"Application\" factoryName=\"Application\">\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"MAIN_CLASS_NAME\" value=\"emulator.Emulator\" />\n" +
				"    <module name=\"" + moduleName + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"-cp &quot;$OutputPath$&quot; -midlet &quot;" + className + "&quot; -jad &quot;$PROJECT_DIR$/" + manifestPath + "&quot; -uei\" />\n" +
				"    <option name=\"VM_PARAMETERS\" value=\"-XX:+IgnoreUnrecognizedVMOptions -Djna.nosys=true -Dfile.encoding=UTF-8 -XstartOnFirstThread\" />\n" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"" + Emulator.getAbsolutePath() + "\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"Make\" enabled=\"true\" />\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
	}

	public static String buildRestoreRunConfig(String moduleName) {
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Restore project\" type=\"Application\" factoryName=\"Application\">\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"MAIN_CLASS_NAME\" value=\"emulator.Emulator\" />\n" +
				"    <module name=\"" + moduleName + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"-restore &quot;$PROJECT_DIR$&quot;\" />\n" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"" + Emulator.getAbsolutePath() + "\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"Make\" enabled=\"true\" />\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
	}

	public static String buildPackageRunConfig(String artifactName, ArrayList<String> inJars, String outJar, boolean debugBuild) {
		StringBuilder proguardCmdline = new StringBuilder();
		// libraries/target config
		proguardCmdline.append('@');
		proguardCmdline.append(ProjectGenerator.PROGUARD_LOCAL_CFG);
		// inputs
		for (String jar : inJars) {
			proguardCmdline.append(" -injars &quot;");
			proguardCmdline.append(jar);
			proguardCmdline.append("&quot;");
		}
		proguardCmdline.append(" -outjars &quot;");
		proguardCmdline.append(outJar);
		proguardCmdline.append("&quot;");
		// optimization/obfuscation
		if (debugBuild)
			proguardCmdline.append(" -dontoptimize -dontshrink -dontobfuscate -keep class *");
		else {
			proguardCmdline.append(" @");
			proguardCmdline.append(ProjectGenerator.PROGUARD_GLOBAL_CFG);
		}
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Package &quot;" + artifactName + "&quot; (" + (debugBuild ? "development" : "release") + ")\" type=\"JarApplication\">\n" +
				"    <option name=\"JAR_PATH\" value=\"" + Settings.proguardPath + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"" + proguardCmdline + "\" />\n" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"$PROJECT_DIR$\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"BuildArtifacts\" enabled=\"true\">\n" +
				"        <artifact name=\"" + artifactName + "\" />\n" +
				"      </option>\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
	}

	public static ClasspathEntry[] generateIML(Path eclipseClasspath, Path imlFile) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();

		Element module = doc.createElement("module");
		module.setAttribute("type", "JAVA_MODULE");
		module.setAttribute("version", "4");
		doc.appendChild(module);

		Element component = doc.createElement("component");
		component.setAttribute("name", "NewModuleRootManager");
		component.setAttribute("LANGUAGE_LEVEL", "JDK_1_3");
		component.setAttribute("inherit-compiler-output", "true");
		module.appendChild(component);

		Element output = doc.createElement("output");
		output.setAttribute("url", "file://$MODULE_DIR$/bin");
		component.appendChild(output);

		Element outputTest = doc.createElement("output-test");
		outputTest.setAttribute("url", "file://$MODULE_DIR$/bin");
		component.appendChild(outputTest);

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

		ClasspathEntry[] existing;
		if (eclipseClasspath == null) {
			addSourceFolder(doc, content, "src", false);
			addSourceFolder(doc, content, "res", true);
			existing = new ClasspathEntry[0];
		} else {
			if (Files.exists(eclipseClasspath)) {
				existing = ClasspathEntry.readFromEclipse(eclipseClasspath);
			} else {
				existing = ClasspathEntry.readFromConfigless(eclipseClasspath.getParent());
				System.out.println("Warning! Classpath file is missing. Default paths were guessed.");
			}
			for (ClasspathEntry entry : existing) {
				if (!entry.isLocalPath)
					throw new IllegalArgumentException("Non-local paths in classpath are not supported yet. Please restructure your project to not rely on them.");
				switch (entry.type) {
					case Source:
					case LibrarySource:
						addSourceFolder(doc, content, entry.path, false);
						break;
					case Resource:
						addSourceFolder(doc, content, entry.path, true);
						break;
					case HeaderLibrary:
						addLibrary(doc, component, entry.path, false);
						break;
					case ExportedLibrary:
						addLibrary(doc, component, entry.path, true);
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

		return existing;
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
