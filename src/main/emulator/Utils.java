package emulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Utils {

	public static final String os = String.valueOf(System.getProperty("os.name")).toLowerCase();
	public static final boolean win = os.startsWith("win");
	public static final boolean linux = os.contains("linux") || os.contains("nix");
	public static final boolean macos = os.contains("mac");
	public static final boolean termux = String.valueOf(System.getProperty("java.vm.vendor")).toLowerCase().contains("termux")
			|| String.valueOf(System.getProperty("user.dir")).toLowerCase().startsWith("/data/data/com.termux/");

	// copied from JDK 24
	public static String translateEscapes(String input) {
		if (input.isEmpty()) {
			return "";
		} else {
			char[] chars = input.toCharArray();
			int length = chars.length;
			int from = 0;
			int to = 0;

			while (from < length) {
				char ch = chars[from++];
				if (ch == '\\') {
					ch = from < length ? chars[from++] : 0;
					switch (ch) {
						case '\n':
							continue;
						case '\r':
							if (from < length && chars[from] == '\n') {
								++from;
							}
							continue;
						case '"':
						case '\'':
						case '\\':
							break;
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
							int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);

							int code;
							for (code = ch - 48; from < limit; code = code << 3 | ch - 48) {
								ch = chars[from];
								if (ch < '0' || '7' < ch) {
									break;
								}

								++from;
							}

							ch = (char) code;
							break;
						case 'b':
							ch = '\b';
							break;
						case 'f':
							ch = '\f';
							break;
						case 'n':
							ch = '\n';
							break;
						case 'r':
							ch = '\r';
							break;
						case 's':
							ch = ' ';
							break;
						case 't':
							ch = '\t';
							break;
						case ':':
							ch = ':';
							break;
						default:
							throw new IllegalArgumentException(input);
					}
				}

				chars[to++] = ch;
			}

			return new String(chars, 0, to);
		}
	}

	public static boolean isPathAbsolute(String path) {
		// posix
		if (path.isEmpty())
			return false;
		if (path.charAt(0) == '/')
			return true;
		// win nt: UNC
		if (path.length() <= 2)
			return false;
		if (path.charAt(0) == '\\' && path.charAt(1) == '\\')
			return true;
		// win nt: local drive
		if (path.length() <= 3)
			return false;
		if (path.charAt(1) == ':') {
			if (path.charAt(2) == '/' || path.charAt(2) == '\\') {
				char letter = Character.toUpperCase(path.charAt(0));
				return letter >= 'A' && letter <= 'Z';
			}
		}

		return false;
	}


	public static int getJavaVersionMajor() {
		try {
			return Integer.parseInt(System.getProperty("java.version").split("\\.")[0]);
		} catch (Throwable e) {
			return 0;
		}
	}

	public static boolean isJava9() {
		try {
			return getJavaVersionMajor() >= 9;
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isJava17() {
		try {
			return getJavaVersionMajor() >= 17;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * Attempts to find JDK near JRE KEmulator is running on.
	 *
	 * @return Null on failure, JDK home on success.
	 */
	public static String getJdkHome() {
		String realHome = System.getProperty("java.home");
		if (Files.exists(Paths.get(realHome, "bin", win ? "javac.exe" : "javac"))) {
			// we run with JDK
			return realHome;
		}
		String parent = Paths.get(realHome).getParent().toString();
		if (Files.exists(Paths.get(parent, "bin", win ? "javac.exe" : "javac"))) {
			// we run with JRE in JDK
			return parent;
		}
		// standalone JRE
		return null;
	}

	public static String getProcessOutput(String[] commandline, boolean errStream) throws IOException {
		Process proc = Runtime.getRuntime().exec(commandline);
		StringBuilder sw = new StringBuilder();
		try (InputStream is = errStream ? proc.getErrorStream() : proc.getInputStream()) {
			int c;
			while ((c = is.read()) != -1)
				sw.append((char) c);
		}
		return sw.toString();
	}

	static String getHWID() {
		try {
			String s = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());
			StringBuffer sb = new StringBuffer();
			byte[] b = md.digest();
			for (byte aByteData : b) {
				String hex = Integer.toHexString(0xff & aByteData);
				if (hex.length() == 1) sb.append('0');
				sb.append(hex);
			}

			return sb.toString();
		} catch (Exception e) {
			return "null";
		}
	}
}
