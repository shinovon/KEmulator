package emulator.ui.swt.devutils.idea;

import emulator.Utils;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdeaSetupXdgLinux extends IdeaSetup {
	public IdeaSetupXdgLinux(Shell parent) {
		super(parent);
	}

	@Override
	protected Set<String> getIdeaInstallationPath() {
		Set<String> set = new HashSet<>();
		checkPath(set);
		checkDesktopFiles(set);
		//checkStandardLocations(set);
		return set;
	}

	@Override
	protected Path getJetBrainsDataRoot() {
		String cfgFolder = System.getenv("XDG_CONFIG_HOME");
		if (cfgFolder == null) {
			cfgFolder = System.getenv("HOME") + "/.config";
		}
		return Paths.get(cfgFolder + "/JetBrains");
	}

	protected String autoInstallProguard() throws IOException, InterruptedException {
		if (Files.exists(Paths.get("/opt/proguard6.2.2")))
			throw new IllegalArgumentException("Folder \"/opt/proguard6.2.2\" already exists. Maybe you already installed it?");

		String tempZipName = "/tmp/proguard" + System.currentTimeMillis() + ".zip";
		String tempFolderName = "/tmp/proguard" + System.currentTimeMillis() + "_ext";

		appendLog("Downloading with wget, wait...\n");
		downloadWithWget(PROGUARD_URL, tempZipName);

		appendLog("\nExtracting archive...\n");
		unzip(tempZipName, tempFolderName);

		appendLog("\nRunning installation...\n");
		Process pkexec = Runtime.getRuntime().exec(new String[]{"/usr/bin/pkexec", "bash", "-c", "/usr/bin/install -Dm644 " + tempFolderName + "/proguard6.2.2/lib/* -t /opt/proguard6.2.2/"});
		if (pkexec.waitFor() != 0)
			throw new RuntimeException("pkexec or install failed, code: " + pkexec.exitValue());

		appendLog("\nDeleting caches...\n");
		Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", "-rf", tempFolderName}).waitFor();
		Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", tempZipName}).waitFor();

		return PROGUARD_DEFAULT_PATH_UNIX;
	}

	protected String autoInstallDocs() throws IOException, InterruptedException {
		if (Files.exists(Paths.get(JAVADOCS_DEFAULT_PATH)))
			throw new IllegalArgumentException("Folder \"" + JAVADOCS_DEFAULT_PATH + "\" already exists. Maybe you already installed it?");

		String tempZipName = "/tmp/j2medocs" + System.currentTimeMillis() + ".zip";
		String tempFolderName = "/tmp/j2medocs" + System.currentTimeMillis() + "_ext";

		appendLog("Downloading with wget, wait...\n");
		downloadWithWget(JAVADOCS_URL, tempZipName);

		appendLog("\nExtracting archive...\n");
		unzip(tempZipName, tempFolderName);

		appendLog("\nDeleting unneeded docs...\n");
		Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", "-rf", tempFolderName + "/J2ME_Docs-master/docs/BlackBerry_API_7_1_0"}).waitFor();
		Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", "-rf", tempFolderName + "/J2ME_Docs-master/docs/midp-3_0-fr-javadoc"}).waitFor();

		appendLog("\nRunning installation...\n");
		int c = Runtime.getRuntime().exec(new String[]{"/usr/bin/pkexec", "/usr/bin/cp", "-r", tempFolderName + "/J2ME_Docs-master/docs/", JAVADOCS_DEFAULT_PATH}).waitFor();
		if (c != 0)
			throw new RuntimeException("pkexec or cp failed, code: " + c);

		appendLog("\nDeleting caches...\n");
		Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", "-rf", tempFolderName}).waitFor();
		Runtime.getRuntime().exec(new String[]{"/usr/bin/rm", tempZipName}).waitFor();

		return JAVADOCS_DEFAULT_PATH;
	}

	//#region External tools

	private void downloadWithWget(String from, String to) throws IOException, InterruptedException {
		Process wget = Runtime.getRuntime().exec(new String[]{"/usr/bin/wget", "-v", "-O", to, from});
		try (InputStream is = wget.getInputStream()) {
			int c;
			while ((c = is.read()) != -1) {
				appendLog((char) c);
			}
		}
		switch (wget.waitFor()) {
			case 0:
				break;
			case 3:
				throw new IOException("I/O error when writing downloaded file");
			case 4:
				throw new IOException("Network error");
			default:
				throw new RuntimeException("wget failed with exit code: " + wget.exitValue());
		}
	}

	private void unzip(String zip, String target) throws IOException, InterruptedException {
		Process unzip = Runtime.getRuntime().exec(new String[]{"/usr/bin/unzip", "-q", zip, "-d", target});
		try (InputStream is = unzip.getInputStream()) {
			int c;
			while ((c = is.read()) != -1) {
				appendLog((char) c);
			}
		}
		unzip.waitFor();
		if (unzip.exitValue() != 0)
			throw new RuntimeException("unzip failed, code: " + unzip.exitValue());
	}

	//#endregion

	//#region IDEA tools

	private static void checkPath(Set<String> set) {
		String pathEnv = System.getenv("PATH");
		if (pathEnv == null) return;

		for (String root : pathEnv.split(":")) {
			File f = new File(root, "idea");
			if (f.exists()) {
				set.add(f.getAbsolutePath());
			}
		}
	}

	private static void checkDesktopFiles(Set<String> set) {
		Path[] appDirs = new Path[]{
				Paths.get("/usr/share/applications"),
				Paths.get(System.getProperty("user.home"), ".local/share/applications")
		};

		for (Path dir : appDirs) {
			if (!Files.exists(dir)) continue;
			try (Stream<Path> stream = Files.walk(dir)) {
				List<Path> all = stream
						.filter(p -> p.toString().endsWith(".desktop"))
						.collect(Collectors.toList());

				for (Path path : all) {
					String binPath = parseExecFromDesktopFile(path);
					if (binPath == null) continue;
					if (binPath.endsWith("idea") || binPath.endsWith("idea.sh") || binPath.endsWith("idea64.sh")) {
						set.add(binPath);
					}
				}
			} catch (IOException e) {
				// ignored
			}
		}
	}

	private static String parseExecFromDesktopFile(Path desktopFile) {
		try {
			List<String> lines = Files.readAllLines(desktopFile);
			for (String line : lines) {
				if (line.startsWith("Exec=")) {
					String execLine = line.substring(5).split(" ")[0].replace("%f", "").replace("%F", "").trim();

					if (execLine.startsWith("\"")) {
						execLine = execLine.substring(1, execLine.indexOf('"', 1));
						if (execLine.startsWith("/")) {
							File exeFile = new File(execLine);
							if (exeFile.exists()) return exeFile.getAbsolutePath();
						}
					}
				}
			}
		} catch (Exception e) {
			// ignored
		}
		return null;
	}

	private static void checkStandardLocations(Set<String> set) {
		String[] locations = new String[]{
				"/opt",
				System.getProperty("user.home") + "/.local/share/JetBrains",
				System.getProperty("user.home") + "/opt"
		};

		for (String location : locations) {
			File root = new File(location);
			if (!root.exists()) continue;

			try (Stream<Path> stream = Files.walk(root.toPath())) {
				List<String> result = stream
						.filter(p -> p.getFileName() != null && p.getFileName().toString().matches("idea?"))
						.map(p -> p.toAbsolutePath().toString())
						.filter(path -> path.contains("bin"))
						.collect(Collectors.toList());

				for (String path : result) {
					set.add(path);
				}
			} catch (IOException e) {
				// ignored
			}
		}
	}

	protected static String getVersion(String path) throws IOException {
		String[] output = Utils.getProcessOutput(new String[]{path, "--version"}, false).split(System.lineSeparator());
		String ver = null;
		for (String line : output) {
			if (line.startsWith("IntelliJ")) {
				ver = line;
				break;
			}
		}
		if (ver == null) throw new IllegalArgumentException();
		return ver;
	}

	//#endregion
}
