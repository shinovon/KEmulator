package emulator.ui;

import emulator.graphics2D.IImage;

import java.io.InputStream;
import java.util.Vector;

public interface IScreen {
	IImage getScreenImg();

	IImage getBackBufferImage();

	IImage getXRayScreenImage();

	void repaint();

	int getWidth();

	int getHeight();

	void setCommandLeft(final String p0);

	void setCommandRight(final String p0);

	void startVibra(final long p0);

	void stopVibra();

	ICaret getCaret();

	void setSize(int w, int h);

	void setWindowIcon(final InputStream inputStream);

	void showMessage(final String message);

	void showMessage(final String title, final String detail);

	void showMessageThreadSafe(final String title, final String detail);

	int showMidletChoice(Vector<String> midletKeys);

	int showUpdateDialog(int type);

	boolean showSecurityDialog(final String message);

	String showIMEIDialog();

	void setCurrent(final javax.microedition.lcdui.Displayable d);

	void updateTitle();

	void startWithMidlet();

	void startEmpty();
}
