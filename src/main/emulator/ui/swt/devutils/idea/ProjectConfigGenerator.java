package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

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
			"proguard.cfg\n" +
			".idea/runConfigurations\n" +
			"\n" +
			"### Eclipse ###\n" +
			".apt_generated\n" +
			".classpath\n" +
			".factorypath\n" +
			".project\n" +
			".settings\n" +
			".springBeans\n" +
			".sts4-cache\n" +
			"bin/\n" +
			"deployed/\n" +
			"!**/src/main/**/bin/\n" +
			"!**/src/test/**/bin/\n" +
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

	public static final String imlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<module type=\"JAVA_MODULE\" version=\"4\">\n" +
			"  <component name=\"NewModuleRootManager\" LANGUAGE_LEVEL=\"JDK_1_3\" inherit-compiler-output=\"true\">\n" +
			"    <exclude-output />\n" +
			"    <content url=\"file://$MODULE_DIR$\">\n" +
			"      <sourceFolder url=\"file://$MODULE_DIR$/res\" type=\"java-resource\" />\n" +
			"      <sourceFolder url=\"file://$MODULE_DIR$/src\" isTestSource=\"false\" />\n" +
			"    </content>\n" +
			"    <orderEntry type=\"inheritedJdk\" />\n" +
			"    <orderEntry type=\"sourceFolder\" forTests=\"false\" />\n" +
			"  </component>\n" +
			"</module>";

	public static String buildProguardConfig(String dir, String name) {
		StringBuilder sb = new StringBuilder();
		for (String l : JdkTablePatcher.DEV_TIME_JARS) {
			sb.append("-libraryjars '");
			sb.append(Paths.get(Emulator.getAbsolutePath(), "uei", l));
			sb.append("'");
			sb.append(System.lineSeparator());
		}
		sb.append("-injars '").append(Paths.get(dir, "deployed", "raw", name + ".jar")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-outjars '").append(Paths.get(dir, "deployed", name + "_release.jar")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-printseeds '").append(Paths.get(dir, "deployed", "pro_seeds.txt")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-printmapping '").append(Paths.get(dir, "deployed", "pro_map.txt")).append("'");
		sb.append(System.lineSeparator());
		sb.append("-dontusemixedcaseclassnames -dontnote -defaultpackage '' -microedition -target 1.3 -optimizations !library/*,!code/simplification/object");
		sb.append(System.lineSeparator());
		sb.append("-keep public class * extends javax.microedition.midlet.MIDlet");
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
		sb.append("MIDlet-Jar-URL: ").append(projName).append("_release.jar").append(System.lineSeparator());

		return sb.toString();
	}

	public static String buildDummyMidlet(String className) {
		String[] splitted = splitByLastDot(className);
		return "package " + splitted[0] + ";\n" +
				"import javax.microedition.lcdui.Display;\n" +
				"import javax.microedition.lcdui.Form;\n" +
				"import javax.microedition.midlet.MIDlet;\n" +
				"public class " + splitted[1] + " extends MIDlet {\n" +
				"    protected void startApp() {\n" +
				"        Form f = new Form(\"Test MIDlet\");\n" +
				"        f.append(\"Hello world, I guess?\");\n" +
				"        Display.getDisplay(this).setCurrent(f);\n" +
				"    }\n" +
				"    protected void pauseApp() {}\n" +
				"    protected void destroyApp(boolean b) {}\n" +
				"}\n";
	}

	public static String buildModulesFile(String projectName) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<project version=\"4\">\n" +
				"  <component name=\"ProjectModuleManager\">\n" +
				"    <modules>\n" +
				"      <module fileurl=\"file://$PROJECT_DIR$/" + projectName + ".iml\" filepath=\"$PROJECT_DIR$/" + projectName + ".iml\" />\n" +
				"    </modules>\n" +
				"  </component>\n" +
				"</project>";
	}

	public static String buildArtifactConfig(String projectName) {
		return "<component name=\"ArtifactManager\">\n" +
				"  <artifact type=\"jar\" name=\"" + projectName + "\">\n" +
				"    <output-path>$PROJECT_DIR$/deployed/raw</output-path>\n" +
				"    <root id=\"archive\" name=\"" + projectName + ".jar\">\n" +
				"      <element id=\"directory\" name=\"META-INF\">\n" +
				"        <element id=\"file-copy\" path=\"$PROJECT_DIR$/META-INF/MANIFEST.MF\" />\n" +
				"      </element>\n" +
				"      <element id=\"module-output\" name=\"" + projectName + "\" />\n" +
				"    </root>\n" +
				"  </artifact>\n" +
				"</component>";
	}

	public static String buildKemRunConfig(String projectName, String className) {
		return "<component name=\"ProjectRunConfigurationManager\">\n" +
				"  <configuration default=\"false\" name=\"Run with KEmulator\" type=\"Application\" factoryName=\"Application\">\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8 CLDC Runtime\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"MAIN_CLASS_NAME\" value=\"emulator.Emulator\" />\n" +
				"    <module name=\"" + projectName + "\" />\n" +
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"-cp $PROJECT_DIR$/bin/production/" + projectName + " -midlet " + className + "\" />\n" +
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
				"    <option name=\"PROGRAM_PARAMETERS\" value=\"@proguard.cfg\" />\n" +
				"    <option name=\"WORKING_DIRECTORY\" value=\"$PROJECT_DIR$\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH_ENABLED\" value=\"true\" />\n" +
				"    <option name=\"ALTERNATIVE_JRE_PATH\" value=\"1.8\" />\n" +
				"    <method v=\"2\">\n" +
				"      <option name=\"BuildArtifacts\" enabled=\"true\">\n" +
				"        <artifact name=\"" + projectName + "\" />\n" +
				"      </option>\n" +
				"    </method>\n" +
				"  </configuration>\n" +
				"</component>";
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
