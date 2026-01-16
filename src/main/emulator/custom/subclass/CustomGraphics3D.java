/*
Copyright (c) 2024 Arman Jussupgaliyev
*/
package emulator.custom.subclass;

import emulator.debug.Memory;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Transform;

public class CustomGraphics3D {

	public static Camera getCamera(Graphics3D o, Transform var1) {
		synchronized (Memory.m3gLock) {
			return o.getCamera(var1);
		}
	}

	public static void setCamera(Graphics3D o, Camera var1, Transform var2) {
		synchronized (Memory.m3gLock) {
			o.setCamera(var1, var2);
		}
	}

	public static void setLight(Graphics3D o, int var1, Light var2, Transform var3) {
		synchronized (Memory.m3gLock) {
			o.setLight(var1, var2, var3);
		}
	}

	public static Light getLight(Graphics3D o, int var1, Transform var2) {
		synchronized (Memory.m3gLock) {
			return o.getLight(var1, var2);
		}
	}

	public static int addLight(Graphics3D o, Light var1, Transform var2) {
		synchronized (Memory.m3gLock) {
			return o.addLight(var1, var2);
		}
	}

}
