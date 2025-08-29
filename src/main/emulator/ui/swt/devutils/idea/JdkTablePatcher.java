package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import emulator.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
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
import java.util.*;
import java.util.stream.Stream;


public class JdkTablePatcher {

	public static final String[] NIKITA_DOCS = new String[]{
			"https://nikita36078.github.io/J2ME_Docs/docs/cldc-1.1",
			"https://nikita36078.github.io/J2ME_Docs/docs/midp-2.0",
			"https://nikita36078.github.io/J2ME_Docs/docs/jsr75",
			"https://nikita36078.github.io/J2ME_Docs/docs/jsr75-pim",
			"https://nikita36078.github.io/J2ME_Docs/docs/jsr82_1.1.1_javadoc",
			"https://nikita36078.github.io/J2ME_Docs/docs/jsr135",
			"https://nikita36078.github.io/J2ME_Docs/docs/jsr179-1_1-mrel-javadoc",
			"https://nikita36078.github.io/J2ME_Docs/docs/jsr184",
			"https://nikita36078.github.io/J2ME_Docs/docs/nokiaapi2"

	};
	public static final String CLDC_DEVTIME = "1.8 CLDC Devtime";
	public static final String CLDC_RUNTIME = "1.8 CLDC Runtime";

	private static List<String> getRuntimeJars() {
		Vector<String> libs = new Vector<>();
		libs.add("KEmulator.jar");
		libs.addAll(Arrays.asList(Emulator.platform.getLwjglLibraryNames()));
		if (Emulator.platform.isX64()) libs.add(Emulator.platform.getSwtLibraryName());
		return libs;
	}

	//#region API

	/**
	 * Checks does the table contain relevant JDKs.
	 *
	 * @param tablePath Path to XML.
	 * @return True if relevant JDKs found.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public static boolean checkJdkTable(String tablePath) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		Element projectJdkTable = getProjectJdkTableElement(loadDocument(tablePath));
		return jdkExists(projectJdkTable, CLDC_DEVTIME) && jdkExists(projectJdkTable, CLDC_RUNTIME);
	}

	/**
	 * Updates IDEA's JDK table with our entries.
	 *
	 * @param configFilePath Path to config xml.
	 * @param localDocs      Path to folder with javadocs. Pass null to use nikita's repo as "online" docs.
	 * @param jdk8home       Path to JDK 1.8 "home".
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public static void updateJdkTable(String configFilePath, String localDocs, String jdk8home) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		Document doc = loadDocument(configFilePath);
		Element projectJdkTable = getProjectJdkTableElement(doc);

		if (jdkExists(projectJdkTable, CLDC_DEVTIME))
			removeJdk(projectJdkTable, CLDC_DEVTIME);
		projectJdkTable.appendChild(createDevTimeJdk(doc, jdk8home, localDocs));
		if (jdkExists(projectJdkTable, CLDC_RUNTIME))
			removeJdk(projectJdkTable, CLDC_RUNTIME);
		projectJdkTable.appendChild(createRuntimeJdk(doc, jdk8home));

		saveDocument(doc, configFilePath);
	}

	//#endregion

	//#region IO

	private static Document loadDocument(String path) throws ParserConfigurationException, IOException, SAXException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		File configFile = new File(path);

		// There is no table - create it.
		if (!configFile.exists() || configFile.length() == 0) {
			Document newDoc = builder.newDocument();
			Element application = newDoc.createElement("application");
			Element component = newDoc.createElement("component");
			component.setAttribute("name", "ProjectJdkTable");

			application.appendChild(component);
			newDoc.appendChild(application);

			saveDocument(newDoc, path);
		}

		Document doc = builder.parse(configFile);
		Element root = doc.getDocumentElement();

		if (!"application".equals(root.getNodeName())) {
			throw new SAXException("Invalid root element: " + root.getNodeName());
		}

		NodeList components = root.getElementsByTagName("component");
		boolean hasJdkTable = false;

		for (int i = 0; i < components.getLength(); i++) {
			Element comp = (Element) components.item(i);
			if ("ProjectJdkTable".equals(comp.getAttribute("name"))) {
				hasJdkTable = true;
				break;
			}
		}

		// Создаем недостающий компонент
		if (!hasJdkTable) {
			Element component = doc.createElement("component");
			component.setAttribute("name", "ProjectJdkTable");
			root.appendChild(component);
		}

		return doc;

	}

	private static void saveDocument(Document doc, String path) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);
	}

	//#endregion

	//#region Implementation

	private static void removeJdk(Element projectJdkTable, String jdkName) {
		NodeList jdks = projectJdkTable.getElementsByTagName("jdk");
		List<Element> toRemove = new ArrayList<>();

		for (int i = 0; i < jdks.getLength(); i++) {
			Element jdk = (Element) jdks.item(i);
			Element nameElement = (Element) jdk.getElementsByTagName("name").item(0);
			if (jdkName.equals(nameElement.getAttribute("value"))) {
				toRemove.add(jdk);
			}
		}

		if (toRemove.isEmpty())
			throw new RuntimeException("Empty list for deletion!");

		for (Element node : toRemove) {
			projectJdkTable.removeChild(node);
		}
	}

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

	private static Element createDevTimeJdk(Document doc, String java8Path, String localDocs) {
		Element jdk = doc.createElement("jdk");
		jdk.setAttribute("version", "2");

		addElement(doc, jdk, "name", "1.8 CLDC Devtime");
		addElement(doc, jdk, "type", "JavaSDK");
		addElement(doc, jdk, "version", "1.8.0");
		addElement(doc, jdk, "homePath", java8Path);

		Element roots = doc.createElement("roots");
		roots.appendChild(createAnnotationsPath(doc));
		roots.appendChild(createClassPath(doc, getDevTimeClassPaths()));
		roots.appendChild(createJavadocPath(doc, localDocs == null ? Arrays.asList(NIKITA_DOCS) : getDevTimeJavadocPaths(localDocs)));
		roots.appendChild(createSourcePath(doc));

		jdk.appendChild(roots);
		return jdk;
	}

	private static Element createRuntimeJdk(Document doc, String java8Path) {
		Element jdk = doc.createElement("jdk");
		jdk.setAttribute("version", "2");

		addElement(doc, jdk, "name", "1.8 CLDC Runtime");
		addElement(doc, jdk, "type", "JavaSDK");
		addElement(doc, jdk, "version", "1.8.0");
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
		Element root = doc.createElement("root");
		root.setAttribute("type", "composite");
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
		Element sourcePath = doc.createElement("sourcePath");
		Element root = doc.createElement("root");
		root.setAttribute("type", "composite");
		sourcePath.appendChild(root);
		return sourcePath;
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

	/**
	 * Gets list of all available UEI libs as absolute paths.
	 */
	public static ArrayList<String> getDevTimeJars() {
		try {
			Path uei = Paths.get(Emulator.getAbsolutePath()).resolve("uei");
			ArrayList<String> paths = new ArrayList<>();
			for (File c : uei.toFile().listFiles()) {
				if (!c.getName().endsWith(".jar"))
					continue;
				if (c.getName().equalsIgnoreCase("emulator.jar"))
					continue;
				paths.add(c.getAbsolutePath().toString());
			}
			return paths;
		} catch (RuntimeException ex) {
			return new ArrayList<>();
		}
	}

	private static List<String> getDevTimeClassPaths() {
		List<String> paths = new ArrayList<>();
		for (String p : getDevTimeJars()) {
			paths.add("jar://" + p.replace("\\", "/") + "!/");
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
					.filter(p -> !(p.toString().contains("security") && p.toString().contains("policy")))
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
			s.filter(Files::isDirectory).forEach(p -> {
				if (p.endsWith("KTF_WIPI_API") || p.endsWith("WIPI_API_1_1_1") || p.endsWith("midp-3_0-fr-javadoc") || p.endsWith("BlackBerry_API_7_1_0"))
					return;
				paths.add("file://" + p.toString().replace("\\", "/"));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return paths;
	}


	//#endregion
}
