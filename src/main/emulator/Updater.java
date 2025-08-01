package emulator;

import emulator.custom.ResourceManager;
import emulator.custom.CustomMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class Updater {

	private static final String URL = "https://nnproject.cc/kem/releases/";
	private static final String UPDATER_URL = URL + "updater.jar";

	public static final int STATE_INITIAL = 0;
	public static final int STATE_BUSY = 1;
	public static final int STATE_UPDATE_AVAILABLE = 2;
	public static final int STATE_UP_TO_DATE = 3;
	public static final int STATE_ERROR = -1;

	public static int state;

	public static int checkUpdate() {
		if (state >= STATE_UPDATE_AVAILABLE)
			return state;
		state = STATE_BUSY;

		Emulator.getEmulator().getLogStream().println("Checking for updates");
		boolean revision = "dev".equals(Settings.updateBranch);
		try {
			String s = URL
					+ Settings.updateBranch
					+ (revision ? "/version.mf" : "/version.txt");
			InputStream inputStream = getHttpStream(s);
			try {
				if (revision) {
					Properties p = new Properties();
					p.load(inputStream);
					s = p.getProperty("Git-Revision", "");

					state = Emulator.revision.equals(s) ?
							STATE_UP_TO_DATE : STATE_UPDATE_AVAILABLE;
				} else {
					s = new String(ResourceManager.getBytes(inputStream), "UTF-8");
					if (s.length() == 0) throw new IOException();

					state = Integer.parseInt(s) > Emulator.numericVersion ?
							STATE_UPDATE_AVAILABLE : STATE_UP_TO_DATE;
				}

				return state;
			} finally {
				inputStream.close();
			}
		} catch (Exception e) {
			Emulator.getEmulator().getLogStream().println("Failed to check updates");
			Emulator.getEmulator().getLogStream().println(e);
			return state = STATE_ERROR;
		}
	}

	public static void startUpdater(boolean restart) {
		Emulator.getEmulator().getLogStream().println("Starting updater");

		try {
			downloadUpdater();
		} catch (Exception e) {
			String path = Emulator.getAbsolutePath();
			if (Files.exists(Paths.get(path)) && !Files.isWritable(Paths.get(path))) {
				// emulator is in system folder or on read-only disk.
				if (Emulator.linux && Files.exists(Paths.get("/usr/bin/pacman"))) {
					Emulator.getEmulator().getScreen().showMessage("You are running system-wide installation. Use your package manager (i.e. \"yay -S kemulatornnmod-bin\").");
				} else {
					Emulator.getEmulator().getScreen().showMessage("KEmulator is installed in read-only location. Use external software management tools or restart KEmulator with admin/root permissions.");
				}
				return;
			}
			Emulator.getEmulator().getScreen().showMessage("Failed to download update helper.");
			e.printStackTrace();
			return;
		}

		if (!new File(Emulator.getAbsolutePath() + File.separatorChar + "updater.jar").exists())
			return;

		ArrayList<String> cmd = new ArrayList<String>();
		String javaHome = System.getProperty("java.home");
		cmd.add(javaHome == null || javaHome.length() == 0 ? "java" : (javaHome + (!Emulator.win ? "/bin/java" : "/bin/java.exe")));
		cmd.add("-cp");
		cmd.add(Emulator.getAbsolutePath() + File.separatorChar + "updater.jar");

		cmd.add("KEmulatorUpdater");

		cmd.add("-dir");
		cmd.add(Emulator.getAbsolutePath());

		cmd.add("-currentVersion");
		cmd.add(Integer.toString(Emulator.numericVersion));

		cmd.add("-currentVersionStr");
		cmd.add(Emulator.version);

		cmd.add("-revision");
		cmd.add(Emulator.revision);

		cmd.add("-type");
		cmd.add(Emulator.getPlatform().isX64() ? "x64" : "win32");

		cmd.add("-branch");
		cmd.add(Settings.updateBranch);

		if (restart)
			cmd.add("-run");

		if (Emulator.jadPath != null) {
			cmd.add("-jad");
			cmd.add(Emulator.jadPath);
		}
		if (Emulator.midletJar != null) {
			cmd.add("-jar");
			cmd.add(Emulator.midletJar);
		}

		Emulator.getEmulator().disposeSubWindows();
		Emulator.notifyDestroyed();
		try {
			new ProcessBuilder().directory(new File(Emulator.getAbsolutePath())).command(cmd).inheritIO().start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		CustomMethod.close();
		System.exit(0);
	}

	private static void downloadUpdater() throws IOException {
		ReadableByteChannel inChannel = null;
		FileOutputStream fileStream = null;
		FileChannel fileChannel = null;
		try {
			inChannel = Channels.newChannel(getHttpStream(UPDATER_URL));
			fileStream = new FileOutputStream(Emulator.getAbsolutePath() + File.separatorChar + "updater.jar");

			fileChannel = fileStream.getChannel();
			fileChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
		} finally {
			if (inChannel != null) inChannel.close();
			if (fileChannel != null) fileChannel.close();
			if (fileStream != null) fileStream.close();
		}
	}

	private static InputStream getHttpStream(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "KEmulator/" + Emulator.version);
		connection.setUseCaches(false);
		int responseCode = connection.getResponseCode();
		if (responseCode == 404) {
			throw new FileNotFoundException(url);
		}
		InputStream inputStream = connection.getInputStream();
		if (inputStream == null) {
			throw new IOException("No input stream");
		}
		return inputStream;
	}
}
