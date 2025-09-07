import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class EmulatorExe {

	public static final String version = "1.10";
	public static final boolean WINE = false;
	public static final boolean NNX64 = false;

	public static void main(String[] args) {
		String path = ".";
		try {
			path = new File("..").getCanonicalPath().replace("\\", "/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(args.length == 0 || args[0].equals("-help")) {
			if(WINE)
				System.out.println("KEmulator nnmod UEI starter v" + version + " for wine");
			else if(NNX64)
				System.out.println("KEmulator nnx64 UEI starter v" + version);
			else
				System.out.println("KEmulator nnmod UEI starter v" + version);
			System.out.println("Usage: emulator [arguments] <application>");
			System.out.println(" -classpath <jar file> <MIDlet class>   Path to jar file and MIDlet class");
			System.out.println(" -version       Display version information of the Emulator, Profile and Configuration");
			System.out.println(" -Xquery        Display emulator device information");
			System.out.println(" -Xdevice:<device name> Name of the device to be emulated"); // TODO
			System.out.println(" -help          Display a list of valid arguments");
			System.out.println(" -Xdescriptor:<jad file>        The jad file to be executed");
			System.out.println(" -Xdebug        Use a remote debugger");
			System.out.println(" -Xdomain:<domain name> Set the security domain of the MIDlet suite");
			System.out.println(" -Xrunjdwp:[transport=<transport>,address=<address>,server=<y/n>,suspend=<y/n>");
			System.out.println(" -Xrun<library>[:options]       Loads a JVMPI agent library into the JVM");
			System.out.println(" -agentlib:<library>[=<options>] Loads a JVMTI agent library into the JVM");
			return;
		}
		if(args[0].equals("-version")) {
			if(NNX64)
				System.out.println("KEmulator nnx64");
			else
				System.out.println("KEmulator nnmod");
			System.out.println("Profile: MIDP-2.1");
			System.out.println("Configuration: CLDC-1.1");
			System.out.println("Optional: JSR75-1.0, JSR82-1.0, JSR120-1.0, JSR135-1.0, JSR177-1.0, JSR179-1.0, JSR205-1.0, JSR234-1.0, JSR256-1.0, NOKIAUI-1.4, SAMSUNGAPI-1.0, PiglerAPI-1.2");
			return;
		}
		if(args[0].equals("-Xquery")) {
			StringBuffer cp = new StringBuffer();
			for(String f: new File(".").list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar") && !name.equals("emulator.jar") && !name.equals("KEmulator.jar") && !name.equals("_kemulator-debug.jar");
				}
			})) {
				try {
					cp.append(new File(f).getCanonicalPath().replace("\\", "/") + ",");
				} catch (IOException e) {}
			}
			if(cp.length() > 0) cp.setLength(cp.length() - 1);

			File debugJar = new File("./_kemulator-debug.jar");
			
			System.out.print("device.list: KEmulator");
			if (debugJar.exists()) System.out.print(", KEmulatorDebug");
			System.out.println();
			System.out.println("uei.version: 1.0.2");
			System.out.println("uei.arguments: Xquery,Xdebug,Xrunjdwp:transport,Xrunjdwp:address,Xrunjdwp:server,Xdescriptor,Xdevice");
			if(WINE)
				System.out.println("sdk.home: " + path.replace("Z:", ""));
			else
				System.out.println("sdk.home: " + path);
			System.out.println("KEmulator.screen.width: 240");
			System.out.println("KEmulator.screen.height: 320");
			System.out.println("KEmulator.screen.isColor: true");
			System.out.println("KEmulator.screen.bitDepth: 16");
			System.out.println("KEmulator.screen.isTouch: true");
			System.out.println("KEmulator.security.domains: Trusted, Untrusted");
			if(WINE)
				System.out.println("KEmulator.bootclasspath: " + cp.toString().replace("Z:", ""));
			else
				System.out.println("KEmulator.bootclasspath: " + cp.toString());
			
			try {
				if (debugJar.exists()) {
					String s = debugJar.getCanonicalPath().replace("\\", "/") + ","
							+ new File("./cldc.jar").getCanonicalPath().replace("\\", "/");
					System.out.println();
					System.out.println("KEmulatorDebug.screen.width: 240");
					System.out.println("KEmulatorDebug.screen.height: 320");
					System.out.println("KEmulatorDebug.screen.isColor: true");
					System.out.println("KEmulatorDebug.screen.bitDepth: 16");
					System.out.println("KEmulatorDebug.screen.isTouch: true");
					System.out.println("KEmulatorDebug.security.domains: Trusted, Untrusted");
					if(WINE)
						System.out.println("KEmulatorDebug.bootclasspath: " + s.replace("Z:", ""));
					else
						System.out.println("KEmulatorDebug.bootclasspath: " + s);
				}
			} catch (Exception e) {}
//			System.out.println("KEmulator.bootclasspath: " + path + "/KEmulator.jar");
			return;
		}
		ArrayList<String> cmd = new ArrayList<String>();
		String os = System.getProperty("os.name").toLowerCase();
		java: {
			try {
				File file = new File("../jre/bin/java.exe");
				if(file.exists()) {
					cmd.add(file.getCanonicalPath().replace("\\", "/"));
					break java;
				}
			} catch (IOException e) {
			}
			String javahome = System.getProperty("java.home");
			boolean win = os.startsWith("win");
			cmd.add(javahome == null || javahome.isEmpty() ? "java" : (javahome + "/bin/java" + (win ? ".exe" : "")));
		}

		// classpath
		cmd.add("-cp");
		cmd.add(path + "/KEmulator.jar");

		// jvm args
		boolean xmxSet = false;
		boolean encodingSet = false;
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if(arg.startsWith("-Xmx")) {
				cmd.add(arg);
				xmxSet = true;
				continue;
			}
			if(arg.startsWith("-Xm")) {
				cmd.add(arg);
				continue;
			}
			if(arg.startsWith("-Xdebug")) {
				cmd.add(arg);
				continue;
			}
			if(arg.startsWith("-Xrun")) {
				cmd.add("-agentlib:" + arg.substring("-Xrun".length()).replaceFirst(":", "="));
				continue;
			}
			if(arg.startsWith("-agentlib:")) {
				cmd.add(arg);
				continue;
			}
			if (arg.startsWith("-Dfile.encoding=")) {
				cmd.add(arg);
				encodingSet = true;
				continue;
			}
			if(arg.startsWith("-D")) {
				cmd.add(arg);
				continue;
			}
		}
		if(!xmxSet) {
			cmd.add(WINE ? "-Xmx512M" : "-Xmx1G");
		}

		cmd.add("-Djava.library.path=" + path);

		if("false".equals(System.getProperty("sun.java3d.d3d"))) {
			cmd.add("-Dsun.java3d.d3d=false");
		}

		if (os.startsWith("darwin") || os.startsWith("mac os")) {
			cmd.add("-XstartOnFirstThread");
		}

		if (!encodingSet) {
			cmd.add("-Dfile.encoding=UTF-8");
		}

		cmd.add("-javaagent:" + path + "/KEmulator.jar");

		
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

		// main class
		cmd.add("emulator.Emulator");

		// emulator args
		boolean classpathSet = false;
		String jad = null;
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if(arg.startsWith("-Xdescriptor:")) {
				jad = arg.substring("-Xdescriptor:".length());
				cmd.add("-jad");
				cmd.add(jad);
				continue;
			}
			if(arg.startsWith("-Xclasspath:")) {
				String jar = arg.substring("-Xclasspath:".length());
				if(jar.contains(";")) {
					System.err.println("Multiple jars in classpath not supported");
					return;
				}
				cmd.add("-jar");
				cmd.add(jar);
				classpathSet = true;
				continue;
			}
			if(arg.equals("-classpath") || arg.equals("-cp")) {
				String jar = args[i+=1];
				if(jar.contains(";")) {
					System.err.println("Multiple jars in classpath not supported");
					return;
				}
				cmd.add("-jar");
				cmd.add(jar);
				try {
					String midlet = args[i+2];
					cmd.add("-midlet");
					cmd.add(midlet);
					i++;
				} catch (Exception e) {}
				classpathSet = true;
				continue;
			}
			if(arg.startsWith("-Xdevice:") || arg.startsWith("-Xdomain:")) {
				cmd.add(arg);
				continue;
			}
			if(!arg.startsWith("-")) continue;
			switch(arg) {
				case "-awt":
				case "-swt":
				case "-log":
					cmd.add(arg);
					continue;
				case "-rec":
				case "-device":
				case "-devicefile":
				case "-fontname":
				case "-fontsmall":
				case "-fontmedium":
				case "-fontlarge":
				case "-key":
					cmd.add(arg);
					cmd.add(args[i+=1]);
					continue;
				case "-keepinstalled": // ignore
				case "-Xdebug":
					continue;
				default:
					if (!arg.startsWith("-agentlib:") && !arg.startsWith("-Xrun") && !arg.startsWith("-D"))
						System.out.println("Unknown argument: " + arg);
					continue;
			}
		}

		if(!classpathSet) {
			if(jad == null) {
				System.err.println("No classpath set");
				return;
			}
			String jar = getMidletJarUrl(jad);
			if(jar == null) {
				System.err.println("JAD file not found: " + jad);
				return;
			}
			cmd.add("-jar");
			cmd.add(jar);
		}

		cmd.add("-uei");

		try {
			Process process = new ProcessBuilder(new String[0]).directory(new File(path)).command(cmd).inheritIO().start();
			new InputStreamCopier(process.getInputStream(), System.out).start();
			new InputStreamCopier(process.getErrorStream(), System.err).start();
			System.exit(process.waitFor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String getMidletJarUrl(String jadPath) {
		try {
			File file = new File(jadPath);
			if (file.exists()) {
				Properties properties = new Properties();
				properties.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String absolutePath = file.getAbsolutePath().replace('\\', '/');
				return absolutePath.substring(0, absolutePath.lastIndexOf('/')) + "/" + properties.getProperty("MIDlet-Jar-URL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static int getJavaVersionMajor() {
		try {
			return Integer.parseInt(System.getProperty("java.version").split("\\.")[0]);
		} catch (Throwable e) {
			return 0;
		}
	}

	static class InputStreamCopier extends Thread {
		private InputStream input;
		private OutputStream output;

		public InputStreamCopier(InputStream inputStream, OutputStream outputStream) {
			this.input = inputStream;
			this.output = outputStream;
		}

		public void run() {
			try {
				byte[] buffer = new byte[8192];
				int read;
				while ((read = this.input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
					if(Thread.interrupted()) return;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
