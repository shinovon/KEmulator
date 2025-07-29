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

	void setLeftSoftLabel(final String label);

	void setRightSoftLabel(final String label);

	void showCommandsList(Vector<TargetedCommand> cmds, CommandsMenuPosition target, int tx, int ty);

	void forceCloseCommandsList();

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

	/**
	 * Runs frontend without MIDlet. This must block until exit.
	 */
	void runEmpty();

	/**
	 * Runs frontend with MIDlet. This must block until exit.
	 */
	void runWithMidlet();

	/**
	 * Called once MIDlet successfully started.
	 *
	 * @param first Was this the first call to startApp() ?
	 */
	void appStarted(boolean first);
}
