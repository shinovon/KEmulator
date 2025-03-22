package emulator.graphics2D;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;

public interface IImage {
	IGraphics2D createGraphics();

	IGraphics2D getGraphics();

	int getWidth();

	int getHeight();

	int[] getData();

	void setData(final int[] p0);

	int getRGB(final int x, final int y);

	void setRGB(int x, int y, int color);

	void setRGB(int x, int y, int w, int h, int[] data, int off, int scan);

	void setAlpha(final int p0, final int p1, final int p2, final int p3, final int p4);

	void saveToFile(final String p0);

	void write(OutputStream out, String format) throws IOException;

	void copyToClipBoard();

	void cloneImage(final IImage img);

	void cloneImage(final IImage img, int x, int y, int w, int h);

	void copyImage(final IGraphics2D g, int sx, int sy, int w, int h, int tx, int ty);

	int size();

	Object getNative();

	void copyToScreen(Object gc);

	void copyToScreen(Object gc, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight);

	boolean isTransparent();

	void fill(int color);

	void getRGB(int[] data, int off, int scan, int x, int y, int w, int h);
}
