package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics3D.IGraphics3D;

public interface IEmulatorPlatform {
	boolean isX64();

	String getName();

	String getTitleName();

	String getInfoString(String version);

	void loadLibraries();

	void load3D();

	boolean supportsM3G();

	boolean supportsMascotCapsule();

	MemoryViewImage convertMicro3DTexture(Object o);

	IGraphics3D getGraphics3D();

	long createGLContext(long gcHandle, boolean b) throws Exception;

	boolean isGLContextCurrent(long imgHandle) throws Exception;

	void setGLContextCurrent(long gcHandle, long contextHandle) throws Exception;

	void releaseGLContext(long gcHandle) throws Exception;

	void deleteGLContext(long contextHandle) throws Exception;

	long getDC(long handle) throws Exception;

	void releaseDC(long handle, long dc) throws Exception;

	void swapBuffers(long dc) throws Exception;
}
