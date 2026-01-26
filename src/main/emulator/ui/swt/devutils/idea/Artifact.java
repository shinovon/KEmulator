package emulator.ui.swt.devutils.idea;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Artifact {
	public String name;
	public String outputPath;
	public String jarName;

	public Artifact(String outputPath, String name, String jarName) {
		this.outputPath = outputPath;
		this.name = name;
		this.jarName = jarName;
	}

	public static Artifact[] extractJarArtifacts(Path projectRoot) throws Exception {
		ArrayList<Artifact> result = new ArrayList<>();

		// Перебираем все XML-файлы в указанной папке
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectRoot.resolve(".idea").resolve("artifacts"), "*.xml")) {
			for (Path xmlPath : stream) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(xmlPath.toFile());
				doc.getDocumentElement().normalize();

				NodeList artifactNodes = doc.getElementsByTagName("artifact");

				for (int i = 0; i < artifactNodes.getLength(); i++) {
					Node node = artifactNodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element artifact = (Element) node;

						String type = artifact.getAttribute("type");
						if ("jar".equals(type)) {
							String name = artifact.getAttribute("name");

							Node outputNode = artifact.getElementsByTagName("output-path").item(0);
							String outputPath = outputNode.getTextContent().trim();

							Element root = (Element) artifact.getElementsByTagName("root").item(0);
							String jarName = root.getAttribute("name");

							result.add(new Artifact(outputPath, name, jarName));
						}
					}
				}
			}
		}

		return result.toArray(new Artifact[0]);
	}
}
