package javax.microedition.lcdui;

import emulator.AppSettings;
import emulator.Emulator;
import emulator.custom.ResourceManager;
import emulator.debug.Profiler;
import emulator.graphics2D.GraphicsUtils;
import emulator.graphics2D.IImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Image {
	private boolean mutable;
	private IImage imageImpl;
	private IImage xrayBuffer;
	private IImage usedRegion;
	int usedCount;
	private boolean disposed;

	public Image(final IImage img) {
		super();
		this.imageImpl = img;
		this.usedRegion = Emulator.getEmulator().newImage(this.imageImpl.getWidth(), this.imageImpl.getHeight(), true);
		this._resetUsedRegion();
		Profiler.totalImagePixelCount += this.getWidth() * this.getHeight();
		++Profiler.totalImageInstances;
	}

	protected void finalize() {
		Profiler.totalImagePixelCount -= this.getWidth() * this.getHeight();
		--Profiler.totalImageInstances;
	}

	public IImage _getImpl() {
		return this.imageImpl;
	}

	IImage _getXRayBuffer() {
		if (this.xrayBuffer == null && (AppSettings.xrayView || AppSettings.xrayBuffer)) {
			this.xrayBuffer = Emulator.getEmulator().newImage(this.getWidth(), this.getHeight(), true);
		}
		return this.xrayBuffer;
	}

	public IImage _getUsedRegion() {
		return this.usedRegion;
	}

	public void _resetUsedRegion() {
		this.usedRegion.setAlpha(0, 0, this.getWidth(), this.getHeight(), 128);
		this.usedCount = 0;
	}

	public int _getUsedCount() {
		return this.usedCount;
	}

	public Graphics getGraphics() {
//		if (!this.mutable) {
//			throw new IllegalStateException("the image is immutable.");
//		}
		if (this.xrayBuffer == null && (AppSettings.xrayView || AppSettings.xrayBuffer)) {
			this.xrayBuffer = Emulator.getEmulator().newImage(this.getWidth(), this.getHeight(), true);
		}

		return new Graphics(this.imageImpl, xrayBuffer);
	}

	public int getWidth() {
		return this.imageImpl.getWidth();
	}

	public int getHeight() {
		return this.imageImpl.getHeight();
	}

	public boolean isMutable() {
		return this.mutable;
	}

	private static Image decode(final byte[] array) throws IllegalArgumentException {
		try {
			return new Image(Emulator.getEmulator().newImage(array));
		} catch (Exception ex) {
			ex.printStackTrace();
			Emulator.getEmulator().getLogStream().println("*** createImage error!! check it ***");
			throw new IllegalArgumentException(ex.toString());
		}
	}

	public static Image createImage(final byte[] array, final int n, final int n2) {
		final byte[] array2 = new byte[n2];
		System.arraycopy(array, n, array2, 0, n2);
		return decode(array2);
	}

	public static Image createImage(final InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new NullPointerException();
		}
		try {
			return decode(ResourceManager.getBytes(inputStream));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IOException();
		}
	}

	public static Image createImage(final int n, final int n2) {
		if (n <= 0 || n2 <= 0) throw new IllegalArgumentException();
		final Image image;
		(image = new Image(Emulator.getEmulator().newImage(n, n2, false))).mutable = true;
		if (AppSettings.xrayView || AppSettings.xrayBuffer)
			image.xrayBuffer = Emulator.getEmulator().newImage(n, n2, true);
		return image;
	}

	public static Image createImage(final int n, final int n2, int color) {
		if (n <= 0 || n2 <= 0) throw new IllegalArgumentException();
		final Image image;
		(image = new Image(Emulator.getEmulator().newImage(n, n2, true, color))).mutable = true;
		if (AppSettings.xrayView || AppSettings.xrayBuffer)
			image.xrayBuffer = Emulator.getEmulator().newImage(n, n2, true, color);
		return image;
	}

	public static Image createImage(final Image image) {
		return createImage(image, 0, 0, image.getWidth(), image.getHeight(), 0);
	}

	public static Image createImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
		final boolean b;
		final int n6 = (b = (((n5 != 4 && n5 != 5 && n5 != 6 && n5 != 7) ? 1 : 0) != 0)) ? n3 : n4;
		final int n7 = b ? n4 : n3;
		final Image image2;
		(image2 = new Image(Emulator.getEmulator().newImage(n6, n7, true, 0))).mutable = true;
		if (AppSettings.xrayView || AppSettings.xrayBuffer)
			image2.xrayBuffer = Emulator.getEmulator().newImage(n6, n7, true, 0);
		image2.getGraphics().drawRegion(image, n, n2, n3, n4, n5, 0, 0, 20);
		image2.mutable = false;
		return image2;
	}

	public static Image createImage(String string) throws IOException {
		if (string == null) {
			throw new NullPointerException();
		}
		try {
			if (!string.startsWith("/")) {
				string = "/" + string;
			}
			return createImage(ResourceManager.getResourceAsStream(string));
		} catch (Exception ex) {
			//ex.printStackTrace();
			throw new IOException(string, ex);
		}
	}

	public static Image createRGBImage(final int[] array, final int n, final int n2, final boolean b) {
		final Image image;
		GraphicsUtils.setImageData((image = new Image(Emulator.getEmulator().newImage(n, n2, b))).imageImpl, array, b, 0, n, n, n2);
		return image;
	}

	public void getRGB(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		GraphicsUtils.getImageData(this.imageImpl, array, n, n2, n3, n4, n5, n6);
	}

	public static Image createImage(InputStream var0, int var1) throws IOException {
		ByteArrayOutputStream var2 = new ByteArrayOutputStream();
		byte[] var3 = new byte[512];
		if (var1 == 8) {
			var2.write(137);
			var2.write(80);
			var2.write(78);
			var2.write(71);
			var2.write(13);
			var2.write(10);
			var2.write(26);
			var2.write(10);
		}

		while (var0.available() > 0) {
			int var4 = var0.read(var3);
			var2.write(var3, 0, var4);
		}

		byte[] b = var2.toByteArray();
		return createImage(b, 0, b.length);
	}

	void dispose() {
		imageImpl = null;
		usedRegion = null;
		disposed = true;
	}

	public int _size() {
		if (disposed) return 5;
		int i = 5 + imageImpl.size();
		if (xrayBuffer != null) i += xrayBuffer.size();
		if (usedRegion != null) i += usedRegion.size();
		return i;
	}
}
