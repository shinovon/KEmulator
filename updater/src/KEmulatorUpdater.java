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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KEmulatorUpdater implements Runnable {
	
	private static final String VERSION = "0.5";
	
	private static final String UPDATE_URL = "https://nnproject.cc/kem/releases/";
	
	private static KEmulatorUpdater inst;

	private static JFrame frame;
	private static JProgressBar progressBar;
	private static JLabel label;

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
			EventQueue.invokeAndWait(inst);

			state("KEmulator Updater v" + VERSION);
			
			// wait for kemulator to close
			Thread.sleep(1000);
			
			kemulatorJar = kemulatorDir.resolve("KEmulator.jar");
//			if (!(Files.exists(kemulatorJar))) {
//				state("KEmulator.jar is missing");
//				exitDelay(3000);
//				return;
//			}

			update: {
				// get latest version
				state("Obtaining latest version info");
				try {
					if ("stable".equals(branch)) {
						int latest = Integer.parseInt(
								getHttpString(UPDATE_URL + branch + "/version.txt"));
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
				
				state("Deleting KEmulator.jar");
				
				try {
					if (Files.exists(kemulatorJar)) {
						Files.delete(kemulatorJar);
					}
				} catch (IOException e) {
					fail("Failed to delete KEmulator.jar", e);
					return;
				}
				
				if (!x64) {
					state("Deleting 3d engines");
					try {
						Files.delete(kemulatorDir.resolve("m3g_lwjgl.jar"));
					} catch (IOException ignored) {}
					try {
						Files.delete(kemulatorDir.resolve("m3g_swerve.jar"));
					} catch (IOException ignored) {}
					try {
						Files.delete(kemulatorDir.resolve("micro3d_gl.jar"));
					} catch (IOException ignored) {}
					try {
						Files.delete(kemulatorDir.resolve("micro3d_dll.jar"));
					} catch (IOException ignored) {}
				}
				
				try {
					state("Downloading KEmulator.jar");
					download(UPDATE_URL
							+ branch + "/" + type
							+ (x64 ? "/KEmulator_x64.jar" : "/KEmulator.jar"), kemulatorJar);
				} catch (IOException e) {
					fail("Failed to download KEmulator.jar", e);
					return;
				}
				
				Path tempZip = Files.createTempFile(null, ".zip");
				try {
					state("Downloading lang.zip");
					download(UPDATE_URL + branch + "/lang.zip", tempZip);
				} catch (IOException e) {
					fail("Failed to download lang.zip", e);
					return;
				}
				
				try {
					state("Extracting lang.zip");
					extract(tempZip, kemulatorDir.resolve("lang"));
				} catch (IOException e) {
					fail("Failed to extract lang.zip", e);
					return;
				}
				
				if (!x64) {
					try {
						state("Downloading m3g_lwjgl.jar");
						download(UPDATE_URL
								+ branch + "/" + type + "/m3g_lwjgl.jar",
								kemulatorDir.resolve("m3g_lwjgl.jar"));
	
						state("Downloading m3g_swerve.jar");
						download(UPDATE_URL
								+ branch + "/" + type + "/m3g_swerve.jar",
								kemulatorDir.resolve("m3g_swerve.jar"));
	
						state("Downloading micro3d_gl.jar");
						download(UPDATE_URL
								+ branch + "/" + type + "/micro3d_gl.jar",
								kemulatorDir.resolve("micro3d_gl.jar"));
	
						state("Downloading micro3d_dll.jar");
						download(UPDATE_URL
								+ branch + "/" + type + "/micro3d_dll.jar",
								kemulatorDir.resolve("micro3d_dll.jar"));

						state("Downloading amrdecoder.dll");
						download(UPDATE_URL
										+ branch + "/" + type + "/amrdecoder.dll",
								kemulatorDir.resolve("amrdecoder.dll"));
					} catch (IOException e) {
						fail("Failed to download libraries", e);
						return;
					}
				} else {
					try {
						state("Downloading lwjgl.zip");
						download(UPDATE_URL + branch + "/lwjgl.zip", tempZip);
					} catch (IOException e) {
						fail("Failed to download lwjgl.zip", e);
						return;
					}

					try {
						state("Extracting lwjgl.zip");
						extract(tempZip, kemulatorDir);
					} catch (IOException e) {
						fail("Failed to extract lwjgl.zip", e);
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
		frame = new JFrame();
		frame.setTitle("KEmulator Updater");
		frame.setBounds(100, 100, 320, 100);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(-1, 20));
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
		} catch (Exception e) {}
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
		} catch (Throwable t) {}
		
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

	
	// http utils
	
	static void download(String url, Path path) throws IOException {
		ReadableByteChannel inChannel = null;
		FileOutputStream fileStream = null;
		FileChannel fileChannel = null;
		try {
			inChannel = Channels.newChannel(getHttpStream(url));
			fileStream = new FileOutputStream(path.toFile());
			
			fileChannel = fileStream.getChannel();
			fileChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
		} finally {
			if (inChannel != null) inChannel.close();
			if (fileChannel != null) fileChannel.close();
			if (fileStream != null) fileStream.close();
		}
	}
	
	static String getHttpString(String url) throws IOException {
		return new String(getHttpBytes(url), "UTF-8");
	}
	
	static byte[] getHttpBytes(String url) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = getHttpStream(url);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[16384];
			int read;
			while ((read = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
			return baos.toByteArray();
		} finally {
			if (inputStream != null) inputStream.close();
		}
	}
	
	static InputStream getHttpStream(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "KEmulatorUpdater/" + VERSION);
		connection.setRequestProperty("Accept-Encoding", "gzip");
		int responseCode = connection.getResponseCode();
		if (responseCode == 404) {
			throw new FileNotFoundException(url);
		}
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
					FileOutputStream fileStream = new FileOutputStream(path.toFile());
					try {
						fileStream.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
					} finally {
						fileStream.close();
					}
				}
				
			}
		} finally {
			zipStream.close();
			inChannel.close();
		}
	}
}
