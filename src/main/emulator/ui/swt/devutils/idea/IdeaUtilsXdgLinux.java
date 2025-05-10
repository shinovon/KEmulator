package emulator.ui.swt.devutils.idea;

import emulator.Settings;
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

public class IdeaUtilsXdgLinux extends IdeaUtils {
	public IdeaUtilsXdgLinux(Shell parent) {
		super(parent);
	}

	@Override
	protected Set<IdeaInstallation> getIdeaInstallationPath() {
		Set<IdeaInstallation> set = new HashSet<>();
		checkPath(set);
		checkDesktopFiles(set);
		//checkStandardLocations(set);
		return set;
	}

	@Override
	protected String getDefaultJdkTablePath() throws IOException {
		String cfgFolder = System.getenv("XDG_CONFIG_HOME");
		if (cfgFolder == null) {
			cfgFolder = System.getenv("HOME") + "/.config";
		}
		cfgFolder += "/JetBrains/IntelliJIdea";
		String[] version = fromPath(Settings.ideaPath).version.split(" ")[2].split("\\.");
		cfgFolder += version[0] + "." + version[1];
		return cfgFolder;
	}

	protected String autoInstallProguard() throws IOException, InterruptedException {
		if (Files.exists(Paths.get("/opt/proguard6.2.2")))
			throw new IllegalArgumentException("Folder \"/opt/proguard6.2.2\" already exists. Maybe you already installed it?");
		String tempZipName = "/tmp/proguard" + System.currentTimeMillis() + ".zip";
		String tempFolderName = "/tmp/proguard" + System.currentTimeMillis() + "_ext";
		appendLog("Downloading with wget, wait...\n");
		Process wget = Runtime.getRuntime().exec(new String[]{"/usr/bin/wget", "-O", tempZipName, proguardUrl});
		try (InputStream is = wget.getInputStream()) {
			int c;
			while ((c = is.read()) != -1) {
				appendLog((char) c);
			}
		}
		while (wget.isAlive())
			Thread.sleep(100);
		if (wget.exitValue() != 0)
			throw new RuntimeException("wget failed, code: " + wget.exitValue());

		appendLog("\nExtracting archive...\n");
		Process unzip = Runtime.getRuntime().exec(new String[]{"/usr/bin/unzip", "-q", tempZipName, "-d", tempFolderName});
		try (InputStream is = unzip.getInputStream()) {
			int c;
			while ((c = is.read()) != -1) {
				appendLog((char) c);
			}
		}
		while (unzip.isAlive())
			Thread.sleep(100);
		if (unzip.exitValue() != 0)
			throw new RuntimeException("unzip failed, code: " + wget.exitValue());

		appendLog("\nRunning installation...\n");
		Process pkexec = Runtime.getRuntime().exec(new String[]{"/usr/bin/pkexec", "bash", "-c", "/usr/bin/install -Dm644 " + tempFolderName + "/proguard6.2.2/lib/* -t /opt/proguard6.2.2/"});
		while (pkexec.isAlive())
			Thread.sleep(100);
		if (pkexec.exitValue() != 0)
			throw new RuntimeException("pkexec failed, code: " + pkexec.exitValue());
		return "/opt/proguard6.2.2/proguard.jar";
	}

	//#region Launcher pathfinder

	private static void checkPath(Set<IdeaInstallation> set) {
		String pathEnv = System.getenv("PATH");
		if (pathEnv == null) return;

		for (String root : pathEnv.split(":")) {
			File f = new File(root, "idea");
			if (f.exists()) {
				try {
					set.add(fromPath(f.getAbsolutePath()));
				} catch (IOException ignored) {
				}
			}
		}
	}

	private static void checkDesktopFiles(Set<IdeaInstallation> set) {
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
						try {
							set.add(fromPath(binPath));
						} catch (IOException ignored) {
						}
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

	private static void checkStandardLocations(Set<IdeaInstallation> set) {
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
					try {
						set.add(fromPath(path));
					} catch (IOException ignored) {
					}
				}
			} catch (IOException e) {
				// ignored
			}
		}
	}

	//#endregion
}
