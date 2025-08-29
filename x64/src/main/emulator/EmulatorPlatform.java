package emulator;

import emulator.graphics3D.IGraphics3D;

import java.io.File;

public class EmulatorPlatform implements IEmulatorPlatform {

	public boolean isX64() {
		return true;
	}

	public String getName() {
		return "KEmulator nnx64";
	}

	public String getTitleName() {
		return "KEmnnx64";
	}

	public String getInfoString(String version) {
		return "KEmulator nnmod " + version + "\n\n"
				+ "\tMulti-Platform\n"
				+ "\t" + UILocale.get("ABOUT_INFO_EMULATOR", "Mobile Game Emulator") + "\n\n"
				+ UILocale.get("ABOUT_INFO_APIS", "Support APIs") + ":\n\n"
				+ "\tMIDP 2.0 (JSR118)\n"
				+ "\tNokiaUI 1.4\n"
				+ "\tSprint 1.0\n"
				+ "\tSiemens API\n"
				+ "\tWMA 1.0 (JSR120)\n"
				+ "\tM3G 1.1 (JSR184)\n"
				+ "\tOpenGL ES (JSR239)\n"
				+ "\tMascotCapsule v3\n"
				+ "\tSoftBank MEXA"
				;
	}

	public void loadLibraries() {
		System.setProperty("jna.nosys", "true");
		if (Emulator.termux) {
			System.setProperty("org.lwjgl.librarypath", "/data/data/com.termux/files/usr/lib:" + Emulator.getAbsolutePath());
		}
		loadSWTLibrary();
		loadLWJGLNatives();
		loadAMR();
	}

	public boolean supportsMascotCapsule() {
		return true;
	}

	public boolean supportsM3G() {
		return true;
	}

	public IGraphics3D getGraphics3D() {
		return emulator.graphics3D.lwjgl.Emulator3D.getInstance();
	}

	public void load3D() {
		if (supportsM3G()) {
			// load m3g
			boolean m3gLoaded = false;
			try {
				Class cls = Class.forName("javax.microedition.m3g.Graphics3D");
				try {
					m3gLoaded = !cls.getField("_STUB").getBoolean(null);
				} catch (Throwable e) {
					m3gLoaded = true;
				}
			} catch (Throwable ignored) {
			}
			if (!m3gLoaded) {
				// TODO
				addToClassPath("m3g_lwjgl.jar");
			}
		}

		// load mascot
		if (supportsMascotCapsule()) {
			boolean mascotLoaded = false;
			try {
				Class cls = Class.forName("com.mascotcapsule.micro3d.v3.Graphics3D");
				try {
					mascotLoaded = !cls.getField("_STUB").getBoolean(null);
				} catch (Throwable e) {
					mascotLoaded = true;
				}
			} catch (Throwable ignored) {
			}
			if (!mascotLoaded) {
				addToClassPath("micro3d_gl.jar");
			}
		}
	}

	private void loadSWTLibrary() {
		if (new File(Emulator.getAbsolutePath() + File.separatorChar + "swt-custom.jar").exists()) {
			addToClassPath("swt-custom.jar");
			return;
		}

		String swtLibName = getSwtLibraryName();

		try {
			addToClassPath(swtLibName);
		} catch (RuntimeException e) {
			// Check if SWT is already loaded
			try {
				Class.forName("org.eclipse.swt.SWT");
			} catch (ClassNotFoundException e2) {
				throw e;
			}
		}
	}

	private void loadLWJGLNatives() {
		try {
			for (String lib : getLwjglLibraryNames()) {
				addToClassPath(lib);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public String[] getLwjglLibraryNames() {
		String osn = System.getProperty("os.name").toLowerCase();
		String osa = System.getProperty("os.arch").toLowerCase();
		String os =
				osn.contains("win") ? "windows" :
				osn.contains("mac") ? "macos" :
				Emulator.termux ? "android" :
				osn.contains("linux") || osn.contains("nix") ? "linux" :
				null;
		if (os == null) {
			return new String[0];
		}
		if (!osa.contains("amd64") && !osa.contains("86") && !osa.contains("aarch64") && !osa.contains("arm")) {
			return new String[0];
		}
		String arch = os + ((osa.contains("amd64") || osa.contains("x84_64")) ? "" : osa.contains("86") ? "-x86" : osa.contains("aarch64") ? "-arm64" : osa.contains("arm") ? "-arm32" : "");

		String[] l = new String[4];
		l[0] = "lwjgl-natives-" + arch + ".jar";
		l[1] = "lwjgl-glfw-natives-" + arch + ".jar";
		l[2] = "lwjgl-opengl-natives-" + arch + ".jar";
		l[3] = "lwjgl3-swt-" + arch + ".jar";
		return l;
	}

	public String getSwtLibraryName() {
		String osn = System.getProperty("os.name").toLowerCase();
		String osa = System.getProperty("os.arch").toLowerCase();

		String os;
		if (osn.contains("win")) {
			os = "win32";
		} else if (osn.contains("mac")) {
			os = "macosx";
		} else if (Emulator.termux) {
			os = "gtk-android";
		} else if (osn.contains("linux") || osn.contains("nix")) {
			os = "gtk-linux";
		} else {
			throw new RuntimeException("Unsupported os: " + osn);
		}

		String arch;
		if (osa.contains("amd64") || osa.contains("x86_64")) {
			arch = "x86_64";
		} else if (osa.contains("86")) {
			arch = "x86";
		} else if (osa.contains("aarch64") || osa.contains("armv8")) {
			arch = "aarch64";
		} else if (osa.contains("arm")) {
			arch = "armhf";
		} else if (osa.contains("ppc") || osa.contains("riscv")) {
			arch = osa;
		} else {
			throw new RuntimeException("Unsupported arch: " + osa);
		}
		return "swt-" + os + "-" + arch + ".jar";
	}

	private static void loadAMR() {
		String osn = System.getProperty("os.name").toLowerCase();
		String osa = System.getProperty("os.arch").toLowerCase();
		String path = Emulator.getAbsolutePath() + File.separatorChar;
		try {
			if (osn.contains("win")) {
				if (osa.contains("amd64") || osa.contains("x86_64")) {
					path += "amrdecoder_64.dll";
				} else if (osa.contains("aarch64") || osa.contains("arm")) { // I don't know which one it returns exactly
					path += "amrdecoder_arm64.dll";
				} else {
					path += "amrdecoder.dll";
				}
				System.load(path);
			} else {
				if (osn.contains("linux")) {
					path += "libamrdecoder";
					if (osa.contains("amd64") || osa.contains("x86_64")) {
						path += "_64";
					} else if (osa.contains("86")) {
					} else if (osa.contains("aarch64") || osa.contains("armv8")) {
						path += "_arm64";
					} else if (osa.contains("arm")) {
						path += "_arm32";
					} else {
						path += '_' + osa;
					}
					System.load(path + ".so");
				} else if (osn.contains("mac")) {
					path += "libamrdecoder";
					if (osa.contains("aarch64")) {
						path += "_arm64";
					}
					System.load(path + ".dylib");
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void addToClassPath(String s) {
		try {
			Agent.addClassPath(new File(Emulator.getAbsolutePath() + File.separatorChar + s));
		} catch (Exception e) {
			throw new RuntimeException(s, e);
		}
	}
}