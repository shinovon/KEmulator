/*
Copyright (c) 2024-2025 Arman Jussupgaliyev
*/
package emulator;

import emulator.custom.ResourceManager;
import emulator.custom.CustomMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

public class Updater {

	private static final String URL = "https://nnproject.cc/kem/releases/";
	private static final String UPDATER_URL = URL + "updater.jar";
	@SuppressWarnings("SpellCheckingInspection")
	private static final String PUBLIC =
			"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtZlt6VdFb3lF0HxD88gs" +
			"ToLdrBouEpR1t9fjTb0lpzk1VOgkNRkPkV3z5hfzOTa3qmpGrUGNXVZf8t+ULMv1" +
			"1FuvmI/TAbAgLqjqtOufU3xLxdTP9p3KRiX+rQLfqCa4oq6+F0+DOtJkWgoFeuVW" +
			"Z3eO0GxPhgR2XXcVhj1NFt2XhViI4dKJ4/7J1mMnH9sGaKQAFVLLo39afbCe5MzU" +
			"/OIyWUDbWN7dhhCO+W8PGEFHd9ayQDLeV74SNYNLE6vGRf8J2whDDvr947htHIRE" +
			"9iDBZgXZqpluZ9S9PHHOSTX/qmCaQj+AfYeXCGcfbM8LM4v60IBsrL7o2NAXWNca" +
			"EKh3K8Vq/4TYLulSosDw/vvyB9Ion/mUaMn4P7yzYXk5/jO8et1UO4t6mKbBczj2" +
			"Msj5IQh4yFH09P8gkdm+A0Z3s7gJrbV9oeS5iAKx/eTn3/vY0dEF/Ss6GV7BPeKu" +
			"D/yOqeAmLdGeDVO9NuONOErjtFDO7Z151OHHH3+TetPgqP2r3Ciy/m1fB0dC167n" +
			"LxE2j8GaFGolf+4dq2xmA5+cglLmEPOJDlbxWHDJ9EKo4FNDdvOxj/h6EclSIxPB" +
			"ya602KEMLvcT3j5s41sf8evqJX5yQRDcz0Hc+iPoTJPhyCymdHY7xIEg0uuhJwix" +
			"CnwVAeUoL4MXpCgytCqM2ysCAwEAAQ==";

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
			try (InputStream inputStream = getHttpStream(s)) {
				if (revision) {
					Properties p = new Properties();
					p.load(inputStream);
					s = p.getProperty("Git-Revision", "");

					state = Emulator.revision.equals(s) ?
							STATE_UP_TO_DATE : STATE_UPDATE_AVAILABLE;
				} else {
					s = new String(ResourceManager.getBytes(inputStream), StandardCharsets.UTF_8);
					if (s.isEmpty()) throw new IOException();

					state = Integer.parseInt(s) > Emulator.numericVersion ?
							STATE_UPDATE_AVAILABLE : STATE_UP_TO_DATE;
				}

				return state;
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
				if (Utils.linux && Files.exists(Paths.get("/usr/bin/pacman"))) {
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
		cmd.add(javaHome == null || javaHome.isEmpty() ? "java" : (javaHome + (!Utils.win ? "/bin/java" : "/bin/java.exe")));
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
		if (Emulator.midletJarPath != null) {
			cmd.add("-jar");
			cmd.add(Emulator.midletJarPath);
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

	private static void downloadUpdater() throws Exception {
		Path tmp = Paths.get(Emulator.getAbsolutePath(), "updater.jar.tmp");
		Path signature = Paths.get(Emulator.getAbsolutePath(), "updater.jar.sig");
		download(UPDATER_URL, tmp);
		download(UPDATER_URL + ".sig", signature);
		try {
			if (!verifyFile(tmp, signature)) {
				Files.delete(tmp);
				throw new IOException("Could not verify updater.jar signature");
			}
			Files.move(tmp, Paths.get(Emulator.getAbsolutePath(), "updater.jar"), StandardCopyOption.REPLACE_EXISTING);
		} finally {
			Files.delete(signature);
		}
	}

	private static void download(String url, Path path) throws IOException {
		try (ReadableByteChannel inChannel = Channels.newChannel(getHttpStream(url));
			 FileOutputStream fileStream = new FileOutputStream(path.toFile());
			 FileChannel fileChannel = fileStream.getChannel()) {
			fileChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
		}
	}

	private static InputStream getHttpStream(String url) throws IOException {
		HttpURLConnection connection;
		while (true) {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "KEmulator/" + Emulator.version);
			connection.setUseCaches(false);
			int responseCode = connection.getResponseCode();
			if (responseCode == 404) {
				throw new FileNotFoundException(url);
			}
			if (responseCode == 301 || responseCode == 302) {
				url = connection.getHeaderField("Location");
				continue;
			}
			break;
		}
		InputStream inputStream = connection.getInputStream();
		if (inputStream == null) {
			throw new IOException("No input stream");
		}
		return inputStream;
	}

	private static boolean verifyFile(Path file, Path signaturePath) throws Exception {
		Signature sign = Signature.getInstance("SHA256withRSA");
		sign.initVerify(KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC))));

		try (InputStream is = Files.newInputStream(file)) {
			byte[] buffer = new byte[8192];
			int len;
			while ((len = is.read(buffer)) != -1) {
				sign.update(buffer, 0, len);
			}
		}

		return sign.verify(Base64.getDecoder().decode(Files.readAllBytes(signaturePath)));
	}
}
