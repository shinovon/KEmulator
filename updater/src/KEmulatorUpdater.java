/*
Copyright (c) 2024-2026 Arman Jussupgaliyev
*/
import javax.swing.*;
import java.awt.*;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KEmulatorUpdater implements Runnable {
	
	private static final String VERSION = "0.7";
	
	private static final String UPDATE_URL = "https://nnproject.cc/kem/releases/";
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

	private static KEmulatorUpdater inst;

	private static JLabel label;
	private static JProgressBar progressBar;

//	private static StringBuilder log = new StringBuilder();

	private static Path kemulatorDir;
	private static Path kemulatorJar;

	private static int currentVersion;
	private static boolean x64;
	private static String type;
	private static String branch = "stable";
	private static boolean runAfterDone;
	private static boolean installed;
	private static String jar;
	private static String jad;

	private static int length;
	private static int progress;

	public static void main(String[] args) {
		kemulatorDir = Paths.get(".");
		try {
			// parse launch arguments
			for (int i = 0; i < args.length; ++i) {
				if (args[i].startsWith("-")) {
					switch (args[i]) {
					case "-dir":
						kemulatorDir = Paths.get(args[++i]);
						break;
					case "-currentVersion":
						currentVersion = Integer.parseInt(args[++i]);
						break;
//					case "-currentVersionStr":
//						currentVersionStr = args[++i];
//						break;
					case "-type":
						type = args[++i];
						x64 = "x64".equals(type);
						break;
					case "-branch":
						branch = args[++i];
						break;
					case "-run":
						runAfterDone = true;
						break;
					case "-installed":
						installed = true;
						break;
					case "-jar":
						jar = args[++i];
						break;
					case "-jad":
						jad = args[++i];
						break;
					}
				}
			}
			
			if (currentVersion == 0 || type == null) {
				fail("Not enough arguments", null);
				exitDelay(0);
				return;
			}
			
			// initialize ui
			inst = new KEmulatorUpdater();
			try {
				EventQueue.invokeAndWait(inst);
			} catch (Exception ignored) {}

			state("KEmulator Updater v" + VERSION);
			
			// wait for kemulator to close
			Thread.sleep(1000);
			
			kemulatorJar = kemulatorDir.resolve("KEmulator.jar");

			update: {
				// get latest version
				state("Obtaining latest version info");
				try {
					if ("stable".equals(branch)) {
						int latest = Integer.parseInt(getHttpString(UPDATE_URL + branch + "/version.txt"));
						if (latest == currentVersion) {
							state("Already up to date!");
							Thread.sleep(3000);
							break update;
						}
					} else {
						String s = getHttpString(UPDATE_URL + branch + "/version.mf");
						// TODO parse and check if up to date
					}
				} catch (Exception e) {
					fail("Failed to get latest version info", e);
					return;
				}
				
				try {
					state("Downloading KEmulator.jar");
					update(UPDATE_URL
							+ branch + "/" + type
							+ (x64 ? "/KEmulator_x64.jar" : "/KEmulator.jar"), "KEmulator.jar");
				} catch (IOException e) {
					fail("Failed to download KEmulator.jar", e);
					return;
				}

				try {
					updateExtract(UPDATE_URL + branch + "/lang.zip", "lang.zip", kemulatorDir);
				} catch (IOException e) {
					fail("Failed to download lang.zip", e);
					return;
				}
				
				if (!x64) {
					try {
						update(UPDATE_URL + branch + "/" + type + "/m3g_lwjgl.jar", "m3g_lwjgl.jar");
						update(UPDATE_URL + branch + "/" + type + "/m3g_swerve.jar", "m3g_swerve.jar");
						update(UPDATE_URL + branch + "/" + type + "/micro3d_gl.jar", "micro3d_gl.jar");
						update(UPDATE_URL + branch + "/" + type + "/micro3d_dll.jar", "micro3d_dll.jar");
						update(UPDATE_URL + branch + "/" + type + "/amrdecoder.dll", "amrdecoder.dll");
					} catch (IOException e) {
						fail("Failed to download libraries", e);
						return;
					}
				} else {
					try {
						updateExtract(UPDATE_URL + branch + "/lwjgl.zip", "lwjgl.zip", kemulatorDir);
					} catch (IOException e) {
						fail("Failed to download lwjgl.zip", e);
						return;
					}
				}
				
				state("Update done");
			}
			
			if (!runAfterDone) {
				exitDelay(1000);
				return;
			}
			
			try {
				start();
			} catch (Exception e) {
				fail("Failed to run KEmulator", e);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(e);
		}
		exitDelay(3000);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void run() {
		JFrame frame = new JFrame();
		frame.setTitle("KEmulator Updater");
		frame.setBounds(100, 100, 320, 100);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(-1, 20));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setIndeterminate(true);
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		label = new JLabel("");
		panel.add(label);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	static void start() throws IOException {
		ArrayList<String> cmd = new ArrayList<String>();
		String os = System.getProperty("os.name").toLowerCase();
		java: {
			Path javaExe = kemulatorDir.resolve("jre").resolve("bin").resolve("java.exe");
			if(Files.exists(javaExe)) {
				cmd.add(javaExe.toString());
				break java;
			}
			String javahome = System.getProperty("java.home");
			boolean win = os.startsWith("win");
			cmd.add(javahome == null || javahome.isEmpty() ? "java" : (javahome + "/bin/java" + (win ? ".exe" : "")));
		}

		// classpath
		cmd.add("-cp");
		cmd.add(kemulatorJar.toString());
		
		cmd.add("-Xmx512M");

		if("false".equals(System.getProperty("sun.java3d.d3d"))) {
			cmd.add("-Dsun.java3d.d3d=false");
		}

		if (os.startsWith("darwin") || os.startsWith("mac os")) {
			cmd.add("-XstartOnFirstThread");
		}
		
		cmd.add("-Djava.library.path=" + kemulatorDir.toString());
		
		cmd.add("-Dfile.encoding=UTF-8");
		
		cmd.add("-javaagent:" + kemulatorJar.toString());
		
		if (getJavaVersionMajor() >= 9) {
			cmd.add("--add-opens");
			cmd.add("java.base/java.lang=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.lang.reflect=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.lang.ref=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.io=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.util=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("jdk.unsupported/sun.misc=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.desktop/com.sun.media.sound=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.desktop/javax.sound.midi=ALL-UNNAMED");
			if (getJavaVersionMajor() >= 17)
				cmd.add("--enable-native-access=ALL-UNNAMED");
		}

		cmd.add("emulator.Emulator");
		
		cmd.add("-s");
		cmd.add("-updated");
		
		if (installed)
			cmd.add("-installed");
		
		if (jar != null) {
			cmd.add("-jar");
			cmd.add(jar);
		}
		
		if (jad != null) {
			cmd.add("-jad");
			cmd.add(jad);
		}
		
		new ProcessBuilder(new String[0])
			.directory(kemulatorDir.toFile())
			.command(cmd)
			.start();
		System.exit(0);
	}
	
	private static void fail(String message, Throwable exception) {
		state(message);
		if (exception != null) {
			log(exception);
		}
		exitDelay(3000);
	}

	private static void exitDelay(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception ignored) {}
		System.exit(0);
	}
	
	static void state(String message) {
		if (label != null)
			label.setText(message);
		log(message, true);
	}
	
	static void log(Throwable exception) {
		String res = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exception.printStackTrace(new PrintStream(baos));
			res = baos.toString();
		} catch (Throwable ignored) {}
		
		if (res == null) return;
		log(res, true);
	}
	
	static void log(String text, boolean show) {
		System.out.println(text);
//		if (textArea == null) return;
//		log.append(text).append('\n');
//		if (show) textArea.setText(log.toString());
	}
	
	static int getJavaVersionMajor() {
		try {
			return Integer.parseInt(System.getProperty("java.version").split("\\.")[0]);
		} catch (Throwable e) {
			return 0;
		}
	}

	static void update(String url, String name) throws Exception {
		state("Downloading " + name);
		Path dest = kemulatorDir.resolve(name);
		Path tmp = kemulatorDir.resolve(name + ".tmp");
		download(url, tmp);
		state("Verifying " + name);
		if (!verifyFile(tmp, getHttpBytes(url + ".sig"))) {
			Files.delete(tmp);
			throw new Exception("Could not verify " + name + " signature");
		}
		Files.move(tmp, dest, StandardCopyOption.REPLACE_EXISTING);
	}

	static void updateExtract(String url, String name, Path dir) throws Exception {
		state("Downloading " + name);
		Path tmp = Files.createTempFile(null, ".zip");
		try {
			download(url, tmp);
			state("Verifying " + name);
			if (!verifyFile(tmp, getHttpBytes(url + ".sig"))) {
				Files.delete(tmp);
				throw new Exception("Could not verify " + name + " signature");
			}
			state("Extracting " + name);
			extract(tmp, dir);
		} finally {
			Files.delete(tmp);
		}
	}

	
	// http utils
	
	static void download(String url, Path path) throws IOException {
		if (progressBar != null) {
			EventQueue.invokeLater(() -> {
				progressBar.setValue(0);
				progressBar.setIndeterminate(false);
			});
		}

		length = -1;
		progress = 0;

		try (InputStream in = getHttpStream(url);
			 FileOutputStream fileStream = new FileOutputStream(path.toFile())) {
			if (progressBar == null) {
				length = -1;
			} else if (length == -1) {
				EventQueue.invokeLater(() -> {
					progressBar.setIndeterminate(true);
				});
			}

			byte[] buffer = new byte[64 * 1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				fileStream.write(buffer, 0, read);
				if (length != -1) {
					progress += read;
					EventQueue.invokeLater(() -> {
						progressBar.setValue(Math.min(100, progress * 100 / length));
					});
				}
			}
		} finally {
			if (progressBar != null) {
				EventQueue.invokeLater(() -> {
					progressBar.setValue(100);
					progressBar.setIndeterminate(true);
				});
			}
		}
	}

	static String getHttpString(String url) throws IOException {
		return new String(getHttpBytes(url), StandardCharsets.UTF_8);
	}
	
	static byte[] getHttpBytes(String url) throws IOException {
		try (InputStream inputStream = getHttpStream(url)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[16384];
			int read;
			while ((read = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
			return baos.toByteArray();
		}
	}
	
	static InputStream getHttpStream(String url) throws IOException {
		HttpURLConnection connection;
		while (true) {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "KEmulatorUpdater/" + VERSION);
			connection.setRequestProperty("Accept-Encoding", "gzip");
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
		length = connection.getContentLength();
		InputStream inputStream = connection.getInputStream();
		if (inputStream == null) {
			throw new IOException("No input stream");
		}
		String encoding = connection.getContentEncoding();
		if ("gzip".equalsIgnoreCase(encoding)) {
			return new GZIPInputStream(inputStream);
		}
		return inputStream;
	}

	private static boolean verifyFile(Path file, byte[] signature) throws Exception {
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

		return sign.verify(Base64.getDecoder().decode(signature));
	}
	
	// zip
	
	static void extract(Path archive, Path outDir) throws IOException {
		ZipInputStream zipStream = new ZipInputStream(Files.newInputStream(archive));
		ReadableByteChannel inChannel = Channels.newChannel(zipStream);
		try {
			ZipEntry zipEntry;
			while ((zipEntry = zipStream.getNextEntry()) != null) {
				String name = zipEntry.getName();
				boolean isDir = zipEntry.isDirectory();
				Path path = outDir.resolve(name);
				if (isDir) {
					if (Files.notExists(path)) {
						Files.createDirectory(path);
					}
				} else {
					try (FileOutputStream fileStream = new FileOutputStream(path.toFile())) {
						fileStream.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
					}
				}
			}
		} finally {
			zipStream.close();
			inChannel.close();
		}
	}
}
