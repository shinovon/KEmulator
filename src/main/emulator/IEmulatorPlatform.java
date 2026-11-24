/*
Copyright (c) 2024 Arman Jussupgaliyev
*/
package emulator;

import emulator.graphics3D.IGraphics3D;

public interface IEmulatorPlatform {
	boolean isMultiplatform();

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
