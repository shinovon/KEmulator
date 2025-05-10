package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


public class JdkTablePatcher {
	public static final String[] DEV_TIME_JARS = new String[]{
			"cldc11.jar",
			"filemanagerapi.jar",
			"javapiglerapi.jar",
			"jsr75_file.jar",
			"jsr75_pim.jar",
			"jsr82.jar",
			"jsr120.jar",
			"jsr135.jar",
			"jsr177_crypto.jar",
			"jsr179.jar",
			"jsr184.jar",
			"jsr211.jar",
			"jsr226.jar",
			"jsr234.jar",
			"jsr256.jar",
			"mascotv3.jar",
			"midp21.jar",
			"nokiaui.jar",
			"notificationapi.jar",
			"rpcapi.jar",
			"samsungapi.jar",
			"siemensio.jar",
	};

	private static String[] getRuntimeJars() {
		return new String[]{
				"KEmulator.jar",
				"lwjgl-glfw-natives-linux.jar",
				"lwjgl-natives-linux.jar",
				"lwjgl-opengl-natives-linux.jar",
				"lwjgl3-swt-linux.jar",
				"m3g_lwjgl.jar",
				"swt-gtk-linux-x86_64.jar"
		};
	}

	//#region API

	public static boolean checkJdkTable(String tablePath) throws ParserConfigurationException, IOException, SAXException {
		Element projectJdkTable = getProjectJdkTableElement(loadDocument(tablePath));
		return jdkExists(projectJdkTable, "1.8 CLDC Devtime") && jdkExists(projectJdkTable, "1.8 CLDC Runtime");
	}

	public static void updateJdkTable(String configFilePath) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		String java8OpenJdkPath = Emulator.getJdkHome();

		Document doc = loadDocument(configFilePath);
		Element projectJdkTable = getProjectJdkTableElement(doc);

		if (!jdkExists(projectJdkTable, "1.8 CLDC Devtime"))
			projectJdkTable.appendChild(createDevTimeJdk(doc, java8OpenJdkPath));
		if (!jdkExists(projectJdkTable, "1.8 CLDC Runtime"))
			projectJdkTable.appendChild(createRuntimeJdk(doc, java8OpenJdkPath));

		saveDocument(doc, configFilePath);
	}

	//#endregion

	//#region IO

	private static Document loadDocument(String path) throws ParserConfigurationException, IOException, SAXException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
	}

	private static void saveDocument(Document doc, String path) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);
	}

	//#endregion

	//#region Implementation

	private static Element getProjectJdkTableElement(Document doc) {
		NodeList components = doc.getElementsByTagName("component");
		for (int i = 0; i < components.getLength(); i++) {
			Element component = (Element) components.item(i);
			if ("ProjectJdkTable".equals(component.getAttribute("name"))) {
				return component;
			}
		}
		throw new RuntimeException("ProjectJdkTable component not found");
	}

	private static boolean jdkExists(Element projectJdkTable, String jdkName) {
		NodeList jdks = projectJdkTable.getElementsByTagName("jdk");
		for (int i = 0; i < jdks.getLength(); i++) {
			Element jdk = (Element) jdks.item(i);
			Element nameElement = (Element) jdk.getElementsByTagName("name").item(0);
			if (jdkName.equals(nameElement.getAttribute("value"))) {
				return true;
			}
		}
		return false;
	}

	private static Element createDevTimeJdk(Document doc, String java8Path) {
		Element jdk = doc.createElement("jdk");
		jdk.setAttribute("version", "2");

		addElement(doc, jdk, "name", "1.8 CLDC Devtime");
		addElement(doc, jdk, "type", "JavaSDK");
		addElement(doc, jdk, "version", "1.8.0_442");
		addElement(doc, jdk, "homePath", java8Path);

		Element roots = doc.createElement("roots");
		roots.appendChild(createAnnotationsPath(doc));
		roots.appendChild(createClassPath(doc, getDevTimeClassPaths()));
		roots.appendChild(createJavadocPath(doc, getDevTimeJavadocPaths(Settings.j2meDocsPath)));
		roots.appendChild(createSourcePath(doc));

		jdk.appendChild(roots);
		return jdk;
	}

	private static Element createRuntimeJdk(Document doc, String java8Path) {
		Element jdk = doc.createElement("jdk");
		jdk.setAttribute("version", "2");

		addElement(doc, jdk, "name", "1.8 CLDC Runtime");
		addElement(doc, jdk, "type", "JavaSDK");
		addElement(doc, jdk, "version", "1.8.0_442");
		addElement(doc, jdk, "homePath", java8Path);

		Element roots = doc.createElement("roots");
		roots.appendChild(createAnnotationsPath(doc));
		roots.appendChild(createClassPath(doc, getRuntimeClassPaths(java8Path)));
		roots.appendChild(createJavadocPath(doc, Collections.singletonList("https://docs.oracle.com/javase/8/docs/api/")));
		roots.appendChild(createSourcePath(doc));

		jdk.appendChild(roots);
		return jdk;
	}

	private static void addElement(Document doc, Element parent, String tagName, String value) {
		Element element = doc.createElement(tagName);
		element.setAttribute("value", value);
		parent.appendChild(element);
	}

	private static Element createAnnotationsPath(Document doc) {
		Element annotationsPath = doc.createElement("annotationsPath");
		Element root1 = doc.createElement("root");
		root1.setAttribute("type", "composite");
		Element childRoot = doc.createElement("root");
		childRoot.setAttribute("url", "jar://$APPLICATION_HOME_DIR$/plugins/java/lib/resources/jdkAnnotations.jar!/");
		childRoot.setAttribute("type", "simple");
		root1.appendChild(childRoot);
		Element root = root1;
		annotationsPath.appendChild(root);
		return annotationsPath;
	}

	private static Element createClassPath(Document doc, List<String> urls) {
		Element classPath = doc.createElement("classPath");
		Element compositeRoot = createCompositeRoot(doc, urls);
		classPath.appendChild(compositeRoot);
		return classPath;
	}

	private static Element createJavadocPath(Document doc, List<String> urls) {
		Element javadocPath = doc.createElement("javadocPath");
		Element compositeRoot = createCompositeRoot(doc, urls);
		javadocPath.appendChild(compositeRoot);
		return javadocPath;
	}

	private static Element createSourcePath(Document doc) {
		return doc.createElement("sourcePath");
	}

	private static Element createCompositeRoot(Document doc, List<String> urls) {
		Element root = doc.createElement("root");
		root.setAttribute("type", "composite");
		for (String url : urls) {
			Element childRoot = doc.createElement("root");
			childRoot.setAttribute("url", url);
			childRoot.setAttribute("type", "simple");
			root.appendChild(childRoot);
		}
		return root;
	}

	private static List<String> getDevTimeClassPaths() {
		String kemnnPath = Emulator.getAbsolutePath();
		List<String> paths = new ArrayList<>();
		for (String jar : DEV_TIME_JARS) {
			paths.add("jar://" + Paths.get(kemnnPath, "uei", jar).toString().replace("\\", "/") + "!/");
		}
		return paths;
	}

	private static List<String> getRuntimeClassPaths(String java8Path) {
		String kemnnPath = Emulator.getAbsolutePath();
		List<String> paths = new ArrayList<>();

		// Add Java 8 OpenJDK JARs
		Path libPath = Paths.get(java8Path, "jre", "lib");
		try (Stream<Path> stream = Files.walk(libPath)) {
			stream.filter(p -> p.toString().endsWith(".jar"))
					.forEach(p -> paths.add("jar://" + p.toString().replace("\\", "/") + "!/"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add KEmnn JARs
		for (String jar : getRuntimeJars()) {
			paths.add("jar://" + Paths.get(kemnnPath, jar).toString().replace("\\", "/") + "!/");
		}

		return paths;
	}

	private static List<String> getDevTimeJavadocPaths(String javadocBase) {
		List<String> paths = new ArrayList<>();
		try (Stream<Path> s = Files.list(Paths.get(javadocBase))) {
			s.filter(Files::isDirectory).forEach(p -> paths.add("file://" + p.toString().replace("\\", "/")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return paths;
	}


	//#endregion
}
