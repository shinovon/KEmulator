import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class EmulatorExe {

	public static void main(String[] args) {
		String path = ".";
		try {
			path = new File("..").getCanonicalPath().replace("\\", "/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(args.length == 0 || args[0].equals("-help")) {
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
			System.out.println("KEmulator nnmod");
			System.out.println("Profile: MIDP-2.1");
			System.out.println("Configuration: CLDC-1.1");
			System.out.println("Optional: JSR75-1.0, JSR82-1.0, JSR120-1.0, JSR135-1.0, JSR177-1.0, JSR179-1.0, JSR205-1.0, JSR234-1.0, JSR256-1.0, NOKIAUI-1.4");
			return;
		}
		if(args[0].equals("-Xquery")) {
			StringBuffer cp = new StringBuffer();
			for(String f: new File(".").list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar") && !name.equals("emulator.jar") && !name.equals("KEmulator.jar");
				}
			})) {
				try {
					cp.append(new File(f).getCanonicalPath().replace("\\", "/") + ",");
				} catch (IOException e) {}
			}
			if(cp.length() > 0) cp.setLength(cp.length() - 1);

			System.out.println("device.list: KEmulator");
			System.out.println("uei.version: 1.0.2");
			System.out.println("uei.arguments: Xquery,Xdebug,Xrunjdwp:transport,Xrunjdwp:address,Xrunjdwp:server,Xdescriptor,Xdevice");
			System.out.println("sdk.home: " + path);
			System.out.println("KEmulator.screen.width: 240");
			System.out.println("KEmulator.screen.height: 320");
			System.out.println("KEmulator.screen.isColor: true");
			System.out.println("KEmulator.screen.bitDepth: 16");
			System.out.println("KEmulator.screen.isTouch: true");
			System.out.println("KEmulator.security.domains: Trusted, Untrusted");
			System.out.println("KEmulator.bootclasspath: " + cp.toString());
//			System.out.println("KEmulator.bootclasspath: " + path + "/KEmulator.jar");
			return;
		}
		ArrayList<String> cmd = new ArrayList<String>();
		if(new File("./jre/bin/java.exe").exists()) {
			cmd.add("./jre/bin/java.exe");
		} else {
			String javahome = System.getProperty("java.home");
			boolean win = System.getProperty("os.name").toLowerCase().startsWith("win");
			cmd.add(javahome == null || javahome.isEmpty() ? "java" : (javahome + "/bin/java" + (win ? ".exe" : "")));
		}

		// classpath
		cmd.add("-cp");
		cmd.add(path + "/KEmulator.jar");

		// jvm args
		boolean xmxSet = false;
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
		}
		if(!xmxSet) {
			cmd.add("-Xmx1G");
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
				cmd.add(jar.contains(";") ? "-cp" : "-jar");
				cmd.add(jar);
				classpathSet = true;
			}
			if(arg.startsWith("-classpath")) {
				String jar = args[i+1];
				cmd.add(jar.contains(";") ? "-cp" : "-jar");
				cmd.add(jar);
				classpathSet = true;
			}
			if(arg.startsWith("-Xdevice:") || arg.startsWith("-Xdomain:")) {
				cmd.add(arg);
			}
		}

		if(!classpathSet) {
			if(jad == null) {
				System.err.println("No classpath set");
				return;
			}
			cmd.add("-jar");
			cmd.add(getMidletJarUrl(jad));
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
				properties.load(new FileInputStream(file));
				String absolutePath = file.getAbsolutePath().replace('\\', '/');
				return absolutePath.substring(0, absolutePath.lastIndexOf('/')) + "/" + new String(properties.getProperty("MIDlet-Jar-URL").getBytes("ISO-8859-1"), "UTF-8");
			}
		} catch (Exception e) {
		}
		return null;
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
