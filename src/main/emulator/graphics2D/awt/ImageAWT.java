package emulator.graphics2D.awt;

import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public final class ImageAWT implements IImage {
	private BufferedImage img;
	private Graphics2DAWT graphics;
	private Graphics2D g2d;
	private int[] data;
	private boolean directAccess;

	public ImageAWT(final byte[] array) throws IOException {
		super();
		try {
			img = emulator.graphics2D.c.toAwt(new ImageData(new ByteArrayInputStream(array)));
		} catch (SWTException e) {
			if (!("Invalid image".equals(e.getMessage())
					|| "Unsupported or unrecognized format".equals(e.getMessage())))
				throw e;
			img = ImageIO.read(new ByteArrayInputStream(array));
			if (img == null) throw new IOException();
		}
	}

	public ImageAWT(final BufferedImage bi) {
		super();
		img = bi;
	}

	public ImageAWT(final int n, final int n2, final boolean b, final int n3) {
		super();
		img = new BufferedImage(n, n2, b ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = g2d = img.createGraphics();
		g.setColor(new Color(n3, b));
		g.fillRect(0, 0, n, n2);
	}

	public final BufferedImage getBufferedImage() {
		return img;
	}

	public final void copyToScreen(Object g, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
		GC gc = (GC) g;
		final Image image = new Image(null, emulator.graphics2D.c.toSwt(img));
		gc.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8);
		image.dispose();
	}

	public boolean isTransparent() {
		return img.getType() == BufferedImage.TYPE_INT_ARGB;
	}

	public void fill(int color) {
		Arrays.fill(getInternalData(), color);
	}

	public final void copyToScreen(Object g) {
		GC gc = (GC) g;
		final Image image = new Image(null, emulator.graphics2D.c.toSwt(img));
		gc.drawImage(image, 0, 0);
		image.dispose();
	}

	public final IGraphics2D createGraphics() {
		return this.graphics = new Graphics2DAWT(img);
	}

	public final IGraphics2D getGraphics() {
		return this.graphics;
	}

	public final int getWidth() {
		return img.getWidth();
	}

	public final int getHeight() {
		return img.getHeight();
	}

	public final int[] getData() {
		try {
			final int[] data = getInternalData();
			directAccess = true;
			if (!img.getColorModel().hasAlpha()) {
				for (int i = data.length - 1; i >= 0; --i) {
					data[i] |= 0xFF000000;
				}
				directAccess = false;
			}
			return data;
		} catch (ClassCastException e) {
			e.printStackTrace();
			final byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			final int[] intdata = new int[data.length];
			for (int i = 0; i < data.length; i++) {
				intdata[i] = ((data[i++] & 0xFF) << 24) +
						((data[i++] & 0xFF) << 16) +
						((data[i++] & 0xFF) << 8) +
						(data[i++] & 0xFF);
//            	intdata[i] = data[i];
			}
			directAccess = false;
			return intdata;
		}
	}

	public final boolean directAccess() {
		return this.directAccess;
	}

	public final void setData(final int[] array) {
		final int[] data = getInternalData();
		if (array == data)
			return;
		if (array.length != data.length)
			return; // TODO throw exception?
		System.arraycopy(array, 0, data, 0, array.length);
	}

	public final void setAlpha(final int n, final int n2, final int n3, final int n4, final int n5) {
		if (n5 == 0) {
			Graphics2D g = g2d;
			if (g == null) g = img.createGraphics();
			g.setComposite(AlphaComposite.getInstance(1, 0.0f));
			g.fillRect(n, n2, n3, n4);
			return;
		}
		final int[] data = this.data != null ? this.data : ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		final int[] array = new int[n3];
		for (int i = 0; i < n3; ++i) {
			array[i] = n5 << 24;
		}
		for (int j = n2; j < n2 + n4; ++j) {
			System.arraycopy(array, 0, data, n + j * this.getWidth(), array.length);
		}
	}

	public final int getRGB(int x, int y) {
		return img.getRGB(x, y);
	}

	public void setRGB(int x, int y, int color) {
		img.setRGB(x, y, color | 0xFF000000);
	}

	public void setRGB(int x, int y, int w, int h, int[] data, int off, int scan) {
		img.setRGB(x, y, w, h, data, off, scan);
	}

	public final void getRGB(int[] data, int off, int scan, int x, int y, int w, int h) {
		img.getRGB(x, y, w, h, data, off, scan);
	}

	public final void saveToFile(final String s) {
		try {
			ImageIO.write(img, "png", new File(s));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void write(OutputStream out, String format) throws IOException {
		ImageIO.write(img, format, out);
	}

	public final void copyToClipBoard() {
		emulator.graphics2D.c.setClipboard(img);
	}

	public final void cloneImage(final IImage sourceImg) {
		System.arraycopy(getInternalData(), 0, ((ImageAWT) sourceImg).getInternalData(), 0, data.length);
	}

	public void cloneImage(IImage sourceImg, int x, int y, int w, int h) {
		Graphics2D g = ((ImageAWT) sourceImg).g2d;
		if (g == null) {
			if (((ImageAWT) sourceImg).graphics != null) {
				g = ((ImageAWT) sourceImg).graphics.g;
			} else {
				g = ((ImageAWT) sourceImg).g2d = img.createGraphics();
			}
		}
		g.drawImage(img, x, y, x + w, y + h, x, y, x + w, y + h, null);
	}

	public void copyImage(IGraphics2D destGraphics, int sx, int sy, int w, int h, int tx, int ty) {
		((Graphics2DAWT) destGraphics).g().drawImage(img, tx, ty, tx + w, ty + h, sx, sy, sx + w, sy + h, null);
	}

	private int[] getInternalData() {
		if (data == null) {
			// TODO: catch ClassCastException
			data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		}
		return data;
	}

	public int size() {
		DataBuffer d = img.getRaster().getDataBuffer();
		int size = d.getSize();
		if (d instanceof DataBufferInt) {
			size *= 4;
		}
		return size;
	}

	public Object getNative() {
		return getBufferedImage();
	}
}
