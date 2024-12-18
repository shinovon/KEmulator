package emulator.graphics3D.egl;

import javax.microedition.khronos.egl.EGLConfig;
import java.util.Hashtable;

/**
 * EGLConfigImpl
 */
public final class EGLConfigImpl extends EGLConfig {
	private static Hashtable aHashtable1333;
	int anInt1334;

	private EGLConfigImpl(final int anInt1334) {
		super();
		synchronized (EGLConfigImpl.aHashtable1333) {
			this.anInt1334 = anInt1334;
			EGLConfigImpl.aHashtable1333.put(new Integer(anInt1334), this);
		}
	}

	public static EGLConfigImpl method779(final int n) {
		synchronized (EGLConfigImpl.aHashtable1333) {
			final EGLConfigImpl value;
			if ((value = (EGLConfigImpl) EGLConfigImpl.aHashtable1333.get(new Integer(n))) == null) {
				return new EGLConfigImpl(n);
			}
			return value;
		}
	}

	public static int method780(final EGLConfig[] array) {
		if (array != null) {
			EGLConfigImpl.aHashtable1333.values().toArray(array);
		}
		return EGLConfigImpl.aHashtable1333.values().size();
	}

	public final String toString() {
		return "EGLConfigImpl[" + this.anInt1334 + "]";
	}

	static {
		EGLConfigImpl.aHashtable1333 = new Hashtable();
	}
}
