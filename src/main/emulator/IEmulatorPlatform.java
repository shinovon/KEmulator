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

	IGraphics3D getGraphics3D();

	String getSwtLibraryName();

	String[] getLwjglLibraryNames();
}
