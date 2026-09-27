package javax.microedition.m3g;

import emulator.graphics3D.m3g.M3GLoader;

import java.io.IOException;

public class Loader {
	public static Object3D[] load(String name) throws IOException {
		return M3GLoader.load(name);
	}

	public static Object3D[] load(byte[] data, int offset) throws IOException {
		return M3GLoader.load(data, offset);
	}
}
