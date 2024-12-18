package emulator.ui;

import emulator.graphics2D.IFont;
import emulator.graphics2D.IImage;
import emulator.graphics3D.IGraphics3D;

import java.io.IOException;

public interface IEmulator {
	IMessage getMessage();

	ILogStream getLogStream();

	IProperty getProperty();

	IScreen getScreen();

	int getScreenDepth();

	void pushPlugin(final IPlugin p0);

	void disposeSubWindows();

	IFont newFont(final int size, final int style);

	IFont newCustomFont(final int height, final int style);

	IImage newImage(final int p0, final int p1, final boolean transparent);

	IImage newImage(final int p0, final int p1, final boolean p2, final int p3);

	IImage newImage(final byte[] p0) throws IOException;

	IGraphics3D getGraphics3D();

	void syncValues();

	String getAppProperty(final String p0);

    void updateLanguage();

}
