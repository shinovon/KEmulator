package emulator.custom;

import emulator.AppSettings;
import emulator.Emulator;
import emulator.Settings;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public final class CustomClassLoader extends ClassLoader {
	public CustomClassLoader(final ClassLoader classLoader) {
		super(classLoader);
	}

	public final synchronized Class loadClass(final String s, final boolean b) throws ClassNotFoundException {
		boolean midlet = Emulator.jarClasses.contains(s);
		if (!midlet && isProtected(s, true)) {
			Emulator.getEmulator().getLogStream().println("Protected class: " + s);
			throw new ClassNotFoundException("Protected class: " + s);
		}
		final Class<?> loadedClass;
		if ((loadedClass = this.findLoadedClass(s)) != null) {
			return loadedClass;
		}
		if (!midlet) {
			try {
				return super.loadClass(s, b);
			} catch (ClassNotFoundException ex) {
				final Class method806;
				if ((method806 = this.loadLibraryClass(s)) == null) {
					throw ex;
				}
				return method806;
			}
		}
		final Class class1 = this.findClass(s);
		if (b) {
			this.resolveClass(class1);
		}
		return class1;
	}

	private Class loadLibraryClass(final String s) {
		Class<?> defineClass = null;
		try {
			for (int i = 0; i < Emulator.jarLibrarys.size(); ++i) {
				final ZipFile zipFile;
				final ZipEntry entry;
				if ((entry = (zipFile = new ZipFile((String) Emulator.jarLibrarys.get(i))).getEntry(s.replace('.', '/') + ".class")) != null) {
					final ClassReader classReader = new ClassReader(zipFile.getInputStream(entry));
					final ClassWriter classWriter = new ClassWriter(0);
					classReader.accept(new ClassVisitor(Opcodes.ASM4, classWriter) {}, AppSettings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
					final byte[] byteArray = classWriter.toByteArray();
					defineClass = this.defineClass(s, byteArray, 0, byteArray.length);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return defineClass;
	}

	protected final Class findClass(final String s) throws ClassNotFoundException {
		Class<?> defineClass;
		try {
			byte[] bytes;
			try {
				bytes = load(s);
			} catch (ArrayIndexOutOfBoundsException e) {
				if (AppSettings.asmSkipDebug) throw e;
				AppSettings.asmSkipDebug = true;
				bytes = load(s);
			}
			try {
				defineClass = this.defineClass(s, bytes, 0, bytes.length);
			} catch (ClassFormatError e) {
				if (AppSettings.asmSkipDebug) throw e;
				AppSettings.asmSkipDebug = true;
				bytes = load(s);
				defineClass = this.defineClass(s, bytes, 0, bytes.length);
			}
		} catch (ClassNotFoundException e) {
			return super.findClass(s);
		} catch (Exception e) {
			e.printStackTrace();
			return super.findClass(s);
		}
		return defineClass;
	}

	private byte[] load(String s) throws Exception {
		InputStream inputStream;
		ZipFile zipFile = null;
		try {
			if (Emulator.midletJar == null) {
				final File fileFromClassPath;
				if ((fileFromClassPath = Emulator.getFileFromClassPath(s.replace('.', '/') + ".class")) == null || !fileFromClassPath.exists()) {
					throw new ClassNotFoundException();
				}
				inputStream = new FileInputStream(fileFromClassPath);
			} else {
				zipFile = new ZipFile(Emulator.midletJar);
				final ZipEntry entry = zipFile.getEntry(s.replace('.', '/') + ".class");
				if (entry == null) {
					throw new ClassNotFoundException();
				}
				inputStream = zipFile.getInputStream(entry);
			}

			final ClassReader classReader = new ClassReader(inputStream);
			final ClassWriter classWriter = new ClassWriter(0);
			try {
				classReader.accept(new CustomClassAdapter(classWriter, s), AppSettings.asmSkipDebug ? ClassReader.SKIP_DEBUG : 0);
			} finally {
				inputStream.close();
			}
			return classWriter.toByteArray();
		} finally {
			if (zipFile != null) zipFile.close();
		}
	}

	public static boolean isProtected(String s, boolean stack) {
		if (!Settings.hideEmulation && (Settings.protectedPackages == null || Settings.protectedPackages.isEmpty()))
			return false;

		if (s.startsWith("__")) return true;

		if (stack) {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			if ((st.length > 4 && !Emulator.jarClasses.contains(st[4].getClassName()))) {
				if (!st[4].getMethodName().equals("forName0") ||
						(st.length > 6 && !Emulator.jarClasses.contains(st[6].getClassName())))
					return false;
			}
		}

		if (Settings.protectedPackages.contains(s)
				|| (Settings.hideEmulation && !s.startsWith("emulator.custom.")
				&& (s.startsWith("kemnn.") || s.startsWith("emulator.")
				|| s.startsWith("club.") || s.startsWith("com.github.")
				|| s.startsWith("uk.co.caprica") || s.startsWith("ru.woesss.")
				|| s.startsWith("org.slf4j.") || s.startsWith("org.pigler.")
				|| s.startsWith("org.objectweb.") || s.startsWith("org.bridj")
				|| s.startsWith("com.sun.jna.") || s.startsWith("org.lwjgl.")
				|| s.startsWith("org.apache.tools.")|| s.startsWith("net.java.games.")
				|| s.startsWith("ru.nnproject")) && !Emulator.jarClasses.contains(s)))
			return true;

		if (Settings.hideEmulation
				&& (s.startsWith("java.applet") || s.startsWith("java.awt") || s.startsWith("java.swing")
				|| s.startsWith("org.eclipse.swt") || s.startsWith("java.lang.CharSequence")
				|| s.startsWith("java.util.Collection") || s.startsWith("java.util.ArrayList") || s.startsWith("java.lang.ClassLoader")
				|| s.startsWith("java.util.Map") || s.startsWith("java.util.Base64") || s.startsWith("java.util.Objects")
				|| s.startsWith("java.util.Deque") || s.startsWith("java.util.regex.") || s.startsWith("java.util.Terminator")
				|| s.startsWith("java.lang.Enum") || s.startsWith("java.lang.SecurityManager") || s.startsWith("java.lang.Terminator")
				|| s.startsWith("java.nio.DoubleBuffer") || s.startsWith("java.lang.Compiler") || s.startsWith("java.util.Observer")
				|| s.startsWith("sun.") || s.startsWith("jdk."))) {
			return true;
		}

		int idx = -1;
		while ((idx = s.indexOf('.', idx + 1)) != -1) {
			if (Settings.protectedPackages.contains(s.substring(0, idx))) return true;
		}
		return false;
	}

	public final InputStream getResourceAsStream(final String s) {
		return super.getResourceAsStream(s);
	}
}
