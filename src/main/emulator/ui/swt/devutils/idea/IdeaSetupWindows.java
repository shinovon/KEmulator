package emulator.ui.swt.devutils.idea;

import emulator.Emulator;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdeaSetupWindows extends IdeaSetup {
	public IdeaSetupWindows(Shell parent) {
		super(parent);
	}

	@Override
	protected Set<String> getIdeaInstallationPath() {
		Set<String> set = new HashSet<>();
		//checkToolboxPaths(set);
		checkStartMenuShortcuts(set);
		return set;
	}

	@Override
	protected Path getJetBrainsDataRoot() {
		return Paths.get(System.getenv("USERPROFILE"), "AppData", "Roaming", "JetBrains");
	}

	@Override
	protected String autoInstallProguard() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected String autoInstallDocs() throws IOException, InterruptedException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//#region Binary pathfinder

	private static void checkStartMenuShortcuts(Set<String> set) {
		List<Path> startMenuDirs = new ArrayList<>();
		String programData = System.getenv("ProgramData");
		if (programData != null) {
			startMenuDirs.add(Paths.get(programData, "Microsoft", "Windows", "Start Menu", "Programs"));
		}
		String userProfile = System.getenv("USERPROFILE");
		if (userProfile != null) {
			startMenuDirs.add(Paths.get(userProfile, "AppData", "Roaming", "Microsoft", "Windows", "Start Menu", "Programs"));
		}

		for (Path dir : startMenuDirs) {
			if (!Files.exists(dir)) continue;
			try {
				String dirPath = dir.toString().replace("'", "''");
				String powershellCommand = "$shell = New-Object -ComObject WScript.Shell; " +
						"Get-ChildItem -Path '" + dirPath + "' -Recurse -Filter *.lnk | " +
						"ForEach-Object { " +
						"    try { " +
						"        $sc = $shell.CreateShortcut($_.FullName); " +
						"        $targetPath = $sc.TargetPath; " +
						"        if ($targetPath -match '\\\\(idea64|idea)\\.exe$') { " +
						"            if (Test-Path -LiteralPath $targetPath -PathType Leaf) { " +
						"                $targetPath " +
						"            } " +
						"        } " +
						"    } catch {} " +
						"}";

				String output = Emulator.getProcessOutput(new String[]{"powershell", "-Command", powershellCommand}, false);

				for (String i : output.split("[\\r\\n]+")) {
					i = i.trim();
					if (!i.isEmpty())
						set.add(i);
				}
			} catch (Exception ignored) {
			}
		}
	}

	private static void checkToolboxPaths(Set<String> set) {
		String localAppData = System.getenv("LOCALAPPDATA");
		if (localAppData == null)
			return;
		Path toolboxDir = Paths.get(localAppData, "JetBrains", "Toolbox", "apps", "IDEA");
		if (!Files.exists(toolboxDir))
			return;

		try (Stream<Path> stream = Files.list(toolboxDir)) {
			String path = stream
					.filter(Files::isDirectory)
					.map(dir -> dir.resolve("bin").resolve("idea.exe"))
					.filter(Files::exists)
					.map(Path::toString)
					.findFirst()
					.orElse(null);
			try {
				if (path != null)
					set.add(path);
			} catch (Exception ignored) {
			}
		} catch (IOException ignored) {
		}
	}
	//#endregion
}
