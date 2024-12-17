package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics2D.IImage;
import emulator.graphics3D.IGraphics3D;
import ru.woesss.j2me.micro3d.TextureImpl;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.IntBuffer;

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
		return "KEmulator nnmod " + version + "\n\n\t" +
				UILocale.get("ABOUT_INFO_EMULATOR", "Mobile Game Emulator") + "\n\n" +
				UILocale.get("ABOUT_INFO_APIS", "Support APIs") + ":\n\n"
				+ "\tMIDP 2.0 (JSR118)\n"
				+ "\tNokiaUI 1.4\n"
				+ "\tSamsung 1.0\n"
				+ "\tSprint 1.0\n"
				+ "\tSiemens API\n"
				+ "\tWMA 1.0 (JSR120)\n"
				+ "\tSensor (JSR256)\n"
				+ "\tM3G 1.1 (JSR184)\n"
				+ "\tOpenGL ES (JSR239)\n"
				+ "\tMascotCapsule v3\n"
				+ "\tSoftBank MEXA"
				;
	}

	public void loadLibraries() {
	}

	public boolean supportsMascotCapsule() {
		return true;
	}

	public boolean supportsM3G() {
		return true;
	}

	public MemoryViewImage convertMicro3DTexture(Object o) {
		Class cls = o.getClass();
		try {
			if (Settings.micro3d == 1) {
				TextureImpl impl = (TextureImpl) cls.getField("impl").get(o);
				if (impl == null)
					return null;
				IntBuffer ib = impl.image.getRaster().asIntBuffer();
				int w = impl.getWidth(), h = impl.getHeight();
				IImage img = Emulator.getEmulator().newImage(w, h, true);
				int[] data = img.getData();
				int i = data.length - w;

				for (int j = h; j > 0; --j) {
					ib.get(data, i, w);
					i -= w;
				}
				return new MemoryViewImage(img);
			} else {
				IImage img = (IImage) cls.getField("debugImage").get(o);
				return new MemoryViewImage(img);
			}
		} catch (Exception ignored) {}
		return null;
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
			} catch (Throwable ignored) {}
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
			} catch (Throwable ignored) {}
			if (!mascotLoaded) {
				addToClassPath(Settings.micro3d == 0 ? "micro3d_dll.jar" : "micro3d_gl.jar");
			}
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
