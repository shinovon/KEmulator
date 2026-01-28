package emulator;

import emulator.graphics3D.IGraphics3D;

import java.io.File;

public class EmulatorPlatform implements IEmulatorPlatform {

	public boolean isX64() {
		return false;
	}

	public String getName() {
		return "KEmulator nnmod";
	}

	public String getTitleName() {
		return "KEmnn";
	}

	public String getInfoString(String version) {
		return "KEmulator nnmod " + version;
	}

	public void loadLibraries() {
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
//                if (!m3gLoaded) {
//                    System.out.println("m3g stub!!");
//                }
				} catch (Throwable e) {
					m3gLoaded = true;
				}
			} catch (Throwable ignored) {
			}
			if (!m3gLoaded) {
				addToClassPath(Settings.g3d == 0 ? "m3g_swerve.jar" : "m3g_lwjgl.jar");
			}
		}

		if (supportsMascotCapsule()) {
			// load mascot
			boolean mascotLoaded = false;
			try {
				Class cls = Class.forName("com.mascotcapsule.micro3d.v3.Graphics3D");
				try {
					mascotLoaded = !cls.getField("_STUB").getBoolean(null);
//                if (!m3gLoaded) {
//                    System.out.println("m3g stub!!");
//                }
				} catch (Throwable e) {
					mascotLoaded = true;
				}
			} catch (Throwable ignored) {
			}
			if (!mascotLoaded) {
				addToClassPath(Settings.micro3d == 0 ? "micro3d_dll.jar" : "micro3d_gl.jar");
			}
		}
	}

	public String getSwtLibraryName() {
		return "swt-win-x86.jar";
	}

	public String[] getLwjglLibraryNames(){
		return new String[0];
	}

	private static void loadAMR() {
		try {
			System.load(Emulator.getAbsolutePath() + File.separatorChar + "amrdecoder.dll");
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
