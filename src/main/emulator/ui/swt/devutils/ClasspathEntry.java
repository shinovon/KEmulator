package emulator.ui.swt.devutils;

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
import java.util.ArrayList;

public class ClasspathEntry {
	public final String localPath;
	public final ClasspathEntryType type;

	public ClasspathEntry(String localPath, ClasspathEntryType type) {
		this.localPath = localPath;
		this.type = type;
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

	// TODO: idea

	// TODO: netbeans

	public static ClasspathEntry[] readFromConfigless(Path projectRoot) {
		ArrayList<ClasspathEntry> list = new ArrayList<>();
		list.add(new ClasspathEntry("src", ClasspathEntryType.Source));
		list.add(new ClasspathEntry("res", ClasspathEntryType.Resource));

		Path libs = projectRoot.resolve("lib");
		if (Files.exists(libs) && Files.isDirectory(libs)) {
			for (File c : libs.toFile().listFiles()) {
				if (c.isFile() && c.getName().endsWith(".jar"))
					list.add(new ClasspathEntry("lib/" + c.getName(), ClasspathEntryType.ExportedLibrary));
				if (c.isDirectory()) {
					if (Files.exists(projectRoot.resolve("lib").resolve(c.getName()).resolve("src")))
						list.add(new ClasspathEntry("lib/" + c.getName() + "/src", ClasspathEntryType.LibrarySource));
					if (Files.exists(projectRoot.resolve("lib").resolve(c.getName()).resolve("res")))
						list.add(new ClasspathEntry("lib/" + c.getName() + "/res", ClasspathEntryType.Resource));
				}
			}
		}

		return list.toArray(new ClasspathEntry[0]);
	}
}
