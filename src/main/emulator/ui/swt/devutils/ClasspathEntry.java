/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui.swt.devutils;

import emulator.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClasspathEntry {
	public final String path;
	public final ClasspathEntryType type;
	public final boolean isLocalPath;

	public ClasspathEntry(String path, ClasspathEntryType type, boolean localPath) {
		this.path = path;
		this.type = type;
		this.isLocalPath = localPath;
	}

	public ClasspathEntry(String path, ClasspathEntryType type) {
		this.path = path;
		this.type = type;
		this.isLocalPath = !Utils.isPathAbsolute(path);
	}

	public boolean isSourceCode() {
		return type == ClasspathEntryType.Source || type == ClasspathEntryType.LibrarySource;
	}

	public boolean isJar() {
		return type == ClasspathEntryType.HeaderLibrary || type == ClasspathEntryType.ExportedLibrary;
	}

	public Path getAbsolutePath(Path projectRoot) {
		if (isLocalPath)
			return projectRoot.resolve(path).normalize();
		return Paths.get(path);
	}

	public static ClasspathEntry[] readAnything(Path projectDir) throws ParserConfigurationException, IOException, SAXException {
		Path eclipse = projectDir.resolve(".classpath");
		if (Files.exists(eclipse)) {
			System.out.println("Found eclipse classpath, parsing...");
			return readFromEclipse(eclipse);
		}
		Path iml = findImlAt(projectDir);
		if (iml != null) {
			System.out.println("Found IDEA module, parsing...");
			return readFromIml(iml);
		}
		//TODO netbeans

		System.out.println("Found no configurations, thinking up project structure from files...");
		return readFromConfigless(projectDir);
	}

	public static ClasspathEntry[] readFromEclipse(Path eclipseClasspath) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		ArrayList<ClasspathEntry> list = new ArrayList<>();

		Document inputDoc = builder.parse(eclipseClasspath.toFile());
		NodeList entries = inputDoc.getElementsByTagName("classpathentry");

		for (int i = 0; i < entries.getLength(); i++) {
			Element entry = (Element) entries.item(i);
			String kind = entry.getAttribute("kind");
			String path = entry.getAttribute("path");

			if ("src".equals(kind)) {
				if (path.endsWith("res"))
					list.add(new ClasspathEntry(path, ClasspathEntryType.Resource));
				else if (path.startsWith("lib"))
					list.add(new ClasspathEntry(path, ClasspathEntryType.LibrarySource));
				else
					list.add(new ClasspathEntry(path, ClasspathEntryType.Source));
			} else if ("lib".equals(kind)) {
				if ("true".equals(entry.getAttribute("exported")))
					list.add(new ClasspathEntry(path, ClasspathEntryType.ExportedLibrary));
				else
					list.add(new ClasspathEntry(path, ClasspathEntryType.HeaderLibrary));
			}
		}
		return list.toArray(new ClasspathEntry[0]);
	}

	public static ClasspathEntry[] readFromIml(Path imlPath) throws ParserConfigurationException, IOException, SAXException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(imlPath.toFile());
		ArrayList<ClasspathEntry> list = new ArrayList<>();

		NodeList orderEntries = doc.getElementsByTagName("orderEntry");
		for (int i = 0; i < orderEntries.getLength(); i++) {
			Element orderEntry = (Element) orderEntries.item(i);

			if ("module-library".equals(orderEntry.getAttribute("type"))) {
				ClasspathEntryType entryType;
				if (orderEntry.hasAttribute("exported") && orderEntry.getAttribute("exported").isEmpty()) {
					entryType = ClasspathEntryType.ExportedLibrary;
				} else {
					entryType = ClasspathEntryType.HeaderLibrary;
				}

				Element library = (Element) orderEntry.getElementsByTagName("library").item(0);
				Element classes = (Element) library.getElementsByTagName("CLASSES").item(0);
				NodeList roots = classes.getElementsByTagName("root");

				for (int i1 = 0; i1 < roots.getLength(); i1++) {
					Element root = (Element) roots.item(i1);
					String url = root.getAttribute("url");

					if (url.startsWith("jar://$MODULE_DIR$/")) {
						String path = url.substring("jar://$MODULE_DIR$/".length(), url.length() - 2);
						list.add(new ClasspathEntry(path, entryType, true));
					} else if (url.startsWith("jar://")) {
						String path = url.substring("jar://".length(), url.length() - 2);
						list.add(new ClasspathEntry(path, entryType, false));
					} else {
						throw new RuntimeException("Unknown library entry: " + url);
					}
				}

			}
		}

		NodeList sourceEntries = doc.getElementsByTagName("sourceFolder");
		for (int i = 0; i < sourceEntries.getLength(); i++) {
			Element sourceFolder = (Element) sourceEntries.item(i);
			String url = sourceFolder.getAttribute("url");
			if (url.startsWith("file://$MODULE_DIR$/")) {
				url = url.substring("file://$MODULE_DIR$/".length());
			} else if (url.startsWith("file://")) {
				url = url.substring("file://".length());
			} else {
				throw new IllegalArgumentException("Unknown source folder entry: " + url);
			}
			if (sourceFolder.getAttribute("type").equals("java-resource")) {
				list.add(new ClasspathEntry(url, ClasspathEntryType.Resource));
			} else if (url.startsWith("lib")) {
				list.add(new ClasspathEntry(url, ClasspathEntryType.LibrarySource));
			} else {
				list.add(new ClasspathEntry(url, ClasspathEntryType.Source));
			}
		}

		return list.toArray(new ClasspathEntry[0]);
	}

	// TODO: netbeans

	public static ClasspathEntry[] readFromConfigless(Path projectRoot) {
		ArrayList<ClasspathEntry> list = new ArrayList<>();
		list.add(new ClasspathEntry("src", ClasspathEntryType.Source, true));
		if (Files.exists(projectRoot.resolve("res")))
			list.add(new ClasspathEntry("res", ClasspathEntryType.Resource, true));

		Path libs = projectRoot.resolve("lib");
		File[] files = libs.toFile().listFiles();
		if (files != null) {
			for (File c : files) {
				if (c.isFile() && c.getName().endsWith(".jar"))
					list.add(new ClasspathEntry("lib/" + c.getName(), ClasspathEntryType.ExportedLibrary, true));
				if (c.isDirectory()) {
					if (Files.exists(projectRoot.resolve("lib").resolve(c.getName()).resolve("src")))
						list.add(new ClasspathEntry("lib/" + c.getName() + "/src", ClasspathEntryType.LibrarySource, true));
					if (Files.exists(projectRoot.resolve("lib").resolve(c.getName()).resolve("res")))
						list.add(new ClasspathEntry("lib/" + c.getName() + "/res", ClasspathEntryType.Resource, true));
				}
			}
		}

		return list.toArray(new ClasspathEntry[0]);
	}

	/**
	 * Looks for IDEA module files. Returns null if none found. Throws if there are 2 or more modules.
	 *
	 * @param projectDir Path to project's root.
	 * @return Path to found IML or null.
	 */
	public static Path findImlAt(Path projectDir) {
		Path imlPath = null;
		for (File file : projectDir.toFile().listFiles()) {
			if (file.getName().endsWith(".iml")) {
				if (imlPath != null)
					throw new IllegalArgumentException("KEmulator-based IDEA projects are expected to have single IML file, two found.");
				imlPath = Paths.get(file.getAbsolutePath());
			}
		}

		return imlPath;
	}
}
