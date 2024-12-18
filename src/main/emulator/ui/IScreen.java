package emulator.ui;

import emulator.graphics2D.IImage;

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
}
