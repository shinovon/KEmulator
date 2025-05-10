package emulator.ui.swt.devutils.idea;

import org.eclipse.swt.widgets.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdeaUtilsWindows extends IdeaUtils {
	public IdeaUtilsWindows(Shell parent) {
		super(parent);
	}

	@Override
	protected Set<IdeaInstallation> getIdeaInstallationPath() {
		Set<IdeaInstallation> set = new HashSet<>();
		checkToolboxPaths(set);
		checkStartMenuShortcuts(set);
		return set;
	}

	@Override
	protected String getDefaultJdkTablePath() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected String autoInstallProguard() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//#region Binary pathfinder

	private static void checkStartMenuShortcuts(Set<IdeaInstallation> set) {
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
			try (Stream<Path> stream = Files.walk(dir)) {
				List<String> result = stream.filter(p -> p.getFileName().toString().endsWith(".lnk"))
						.map(p -> {
							try {
								String shortcutPath = p.toString().replace("'", "''");
								Process process = new ProcessBuilder("powershell", "-Command",
										"$sh = New-Object -ComObject WScript.Shell; $sc = $sh.CreateShortcut('" + shortcutPath + "'); $sc.TargetPath"
								).start();
								String targetPath;
								try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
									targetPath = reader.readLine();
								}
								process.waitFor();
								if (targetPath != null && (targetPath.endsWith("idea64.exe") || targetPath.endsWith("idea.exe"))) {
									File exeFile = new File(targetPath);
									if (exeFile.exists()) {
										return targetPath;
									}
								}
							} catch (Exception ignored) {
							}
							return null;
						}).filter(Objects::nonNull).collect(Collectors.toList());
				for (String path : result) {
					try {
						set.add(fromPath(path));
					} catch (Exception ignored) {
					}
				}
			} catch (IOException ignored) {
			}
		}
	}

	private static void checkToolboxPaths(Set<IdeaInstallation> set) {
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
				set.add(fromPath(path));
			} catch (Exception ignored) {
			}
		} catch (IOException ignored) {
		}
	}
	//#endregion
}
