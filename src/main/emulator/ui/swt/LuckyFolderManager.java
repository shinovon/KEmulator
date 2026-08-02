package emulator.ui.swt;

import emulator.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LuckyFolderManager {

	private static final Random rand = new Random();

	public static String[] getPaths() {
		return Settings.luckyFolderPaths;
	}

	public static boolean[] getModes() {
		return Settings.luckyFolderFavBrowserMode;
	}

	public static int count() {
		return Settings.luckyFolderPaths.length;
	}

	public static void setFolders(String[] paths, boolean[] modes) {
		Settings.luckyFolderPaths = paths;
		Settings.luckyFolderFavBrowserMode = modes;
	}

	public static File pickRandomJar() {
		String[] paths = Settings.luckyFolderPaths;
		List<File> candidates = new ArrayList<File>();
		for (int i = 0; i < paths.length; i++) {
			File dir = new File(paths[i]);
			if (!dir.isDirectory()) continue;
			File[] jars = dir.listFiles();
			if (jars == null) continue;
			for (File f : jars) {
				if (f.isFile() && f.getName().toLowerCase().endsWith(".jar")) {
					candidates.add(f);
				}
			}
		}
		if (candidates.isEmpty()) return null;
		return candidates.get(rand.nextInt(candidates.size()));
	}

	public static List<File> getFavBrowserJars() {
		String[] paths = Settings.luckyFolderPaths;
		boolean[] modes = Settings.luckyFolderFavBrowserMode;
		List<File> result = new ArrayList<File>();
		for (int i = 0; i < paths.length; i++) {
			if (i >= modes.length || !modes[i]) continue;
			File dir = new File(paths[i]);
			if (!dir.isDirectory()) continue;
			File[] jars = dir.listFiles();
			if (jars == null) continue;
			for (File f : jars) {
				if (f.isFile() && f.getName().toLowerCase().endsWith(".jar")) {
					result.add(f);
				}
			}
		}
		return result;
	}
}
