package javax.microedition.m3g;

import emulator.graphics2D.IImage;
import emulator.graphics3D.lwjgl.Emulator3D;

import javax.microedition.lcdui.Image;

public class Image2D extends Object3D {
	public static final int ALPHA = 96;
	public static final int LUMINANCE = 97;
	public static final int LUMINANCE_ALPHA = 98;
	public static final int RGB = 99;
	public static final int RGBA = 100;
	private int type;
	private int width;
	private int height;
	private byte[] imageData;
	private boolean mutable;
	private static byte[] tmp;
	public boolean loaded;
	private int id;
//	private ByteBuffer buffer;
//	private int size;

	public Image2D(int format, Object image) {
		if (image == null) {
			throw new NullPointerException();
		} else if (!checkType(format)) {
			throw new IllegalArgumentException();
		} else if (image instanceof Image) {
			Image lcduiImage = (Image) image;
			this.width = lcduiImage.getWidth();
			this.height = lcduiImage.getHeight();
			this.mutable = false;
			this.type = format;
			this.imageData = convert(format, lcduiImage._getImpl().getData(), lcduiImage.isMutable());
//			allocateBuffer(imageData.length);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Image2D(int format, int width, int height, byte[] image) {
		if (image == null) {
			throw new NullPointerException();
		} else if (width > 0 && height > 0 && checkType(format)) {
			int size = width * height * bytesPerPixel(format);
			if (image.length < size) {
				throw new IllegalArgumentException();
			} else {
				this.width = width;
				this.height = height;
				this.mutable = false;
				this.type = format;
				this.imageData = new byte[size];
				System.arraycopy(image, 0, this.imageData, 0, size);
//				allocateBuffer(size);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Image2D(int format, int width, int height, byte[] pixels, byte[] palette) {
		if (pixels != null && palette != null) {
			int pixelCount = width * height;
			if (width > 0 && height > 0 && checkType(format) && pixels.length >= pixelCount) {
				int bytesPerPixel = bytesPerPixel(format);
				if (palette.length < 256 * bytesPerPixel && palette.length % bytesPerPixel != 0) {
					throw new IllegalArgumentException();
				} else {
					this.width = width;
					this.height = height;
					this.mutable = false;
					this.type = format;
					this.imageData = new byte[pixelCount * bytesPerPixel];

					for (int i = 0; i < pixelCount; ++i) {
						System.arraycopy(palette, (pixels[i] & 255) * bytesPerPixel, this.imageData, i * bytesPerPixel, bytesPerPixel);
					}
//					allocateBuffer(imageData.length);
				}
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new NullPointerException();
		}
	}

	public Image2D(int format, int width, int height) {
		if (width > 0 && height > 0 && checkType(format)) {
			this.width = width;
			this.height = height;
			this.mutable = true;
			this.type = format;
			int rowSize = width * bytesPerPixel(format);
			this.imageData = new byte[rowSize * height];
			int i;
			if (tmp == null || tmp.length < rowSize) {
				tmp = new byte[rowSize];

				for (i = rowSize - 1; i >= 0; --i) {
					tmp[i] = -1;
				}
			}

			for (i = 0; i < height; ++i) {
				System.arraycopy(tmp, 0, this.imageData, i * rowSize, rowSize);
			}
//			allocateBuffer(imageData.length);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void set(int x, int y, int width, int height, byte[] image) {
		if (image == null) {
			throw new NullPointerException();
		} else if (this.mutable && x >= 0 && y >= 0 && width > 0 && height > 0 && x + width <= this.width && y + height <= this.height) {
			int bytesPerPixel = this.getBitsPerColor();
			if (image.length < width * height * bytesPerPixel) {
				throw new IllegalArgumentException();
			} else {
				for (int row = 0; row < height; ++row) {
					System.arraycopy(image, row * width * bytesPerPixel, this.imageData, ((y + row) * this.width + x) * bytesPerPixel, width * bytesPerPixel);
				}
				((Emulator3D) Graphics3D.getImpl()).invalidateTexture(this);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean isMutable() {
		return this.mutable;
	}

	public int getFormat() {
		return this.type;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getBitsPerColor() {
		return bytesPerPixel(this.type);
	}

	private static int bytesPerPixel(int type) {
		switch (type) {
			case ALPHA:
			case LUMINANCE:
				return 1;
			case LUMINANCE_ALPHA:
				return 2;
			case RGB:
				return 3;
			case RGBA:
				return 4;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static boolean checkType(int format) {
		return format >= 96 && format <= 100;
	}

	public final byte[] getImageData() {
//		if (buffer != null) {
//			buffer.position(buffer.capacity() - size);
//			byte[] t = new byte[size];
//			buffer.get(t);
//		}
		return this.imageData;
	}

	private static byte[] convert(int type, int[] data, boolean mutable) {
		byte[] result = null;
		int pixelCount = data.length;
		int i;
		if (mutable) {
			switch (type) {
				case 96:
				case 97:
					result = new byte[pixelCount];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i] = (byte) (((data[i] >> 16 & 255) + (data[i] >> 8 & 255) + (data[i] & 255)) / 3 & 255);
					}

					return result;
				case 98:
					result = new byte[pixelCount * 2];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i * 2] = (byte) (((data[i] >> 16 & 255) + (data[i] >> 8 & 255) + (data[i] & 255)) / 3 & 255);
						result[i * 2 + 1] = -1;
					}

					return result;
				case 99:
					result = new byte[pixelCount * 3];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i * 3] = (byte) (data[i] >> 16 & 255);
						result[i * 3 + 1] = (byte) (data[i] >> 8 & 255);
						result[i * 3 + 2] = (byte) (data[i] & 255);
					}

					return result;
				case 100:
					result = new byte[pixelCount * 4];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i * 4] = (byte) (data[i] >> 16 & 255);
						result[i * 4 + 1] = (byte) (data[i] >> 8 & 255);
						result[i * 4 + 2] = (byte) (data[i] & 255);
						result[i * 4 + 3] = -1;
					}
			}
		} else {
			switch (type) {
				case 96:
					result = new byte[pixelCount];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i] = (byte) (data[i] >> 24 & 255);
					}

					return result;
				case 97:
					result = new byte[pixelCount];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i] = (byte) (((data[i] >> 16 & 255) + (data[i] >> 8 & 255) + (data[i] & 255)) / 3 & 255);
					}

					return result;
				case 98:
					result = new byte[pixelCount * 2];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i * 2] = (byte) (((data[i] >> 16 & 255) + (data[i] >> 8 & 255) + (data[i] & 255)) / 3 & 255);
						result[i * 2 + 1] = (byte) (data[i] >> 24 & 255);
					}

					return result;
				case 99:
					result = new byte[pixelCount * 3];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i * 3] = (byte) (data[i] >> 16 & 255);
						result[i * 3 + 1] = (byte) (data[i] >> 8 & 255);
						result[i * 3 + 2] = (byte) (data[i] & 255);
					}

					return result;
				case 100:
					result = new byte[pixelCount * 4];

					for (i = pixelCount - 1; i >= 0; --i) {
						result[i * 4] = (byte) (data[i] >> 16 & 255);
						result[i * 4 + 1] = (byte) (data[i] >> 8 & 255);
						result[i * 4 + 2] = (byte) (data[i] & 255);
						result[i * 4 + 3] = (byte) (data[i] >> 24 & 255);
					}
			}
		}

		return result;
	}

	protected Object3D duplicateObject() {
		Image2D copy = (Image2D) super.duplicateObject();
		copy.imageData = (byte[]) this.imageData.clone();
//		copy.imageData = getImageData();
//		copy.allocateBuffer(size);
		return copy;
	}

	protected void finalize() {
		// check if was binded
		if (id == 0) return;
		((Emulator3D) Graphics3D.getImpl()).finalizeTexture(this);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean b) {
		loaded = b;
	}

	public void setId(int id) {
		this.id = id;
		loaded = false;
	}

	public int getId() {
		return id;
	}

	public int size() {
		// TODO
//		if (buffer != null) return buffer.capacity();
		return imageData.length;
	}

	public void getPixels(byte[] dist) {
		byte[] b = getImageData();
		System.arraycopy(b, 0, dist, 0, b.length);
	}

	public boolean isPalettized() {
		return false;
	}

	public void getPalette(byte[] array) {
	}

	void setRGB(IImage image) {
		int[] data = image.getData();
		int l = data.length;
//		if (buffer != null) {
//			buffer.position(buffer.capacity() - (l * 3));
//			for (int i = l - 1; i >= 0; --i) {
//				buffer.put((byte) (data[i] >> 16 & 255));
//				buffer.put((byte) (data[i] >> 8 & 255));
//				buffer.put((byte) (data[i] & 255));
//			}
//			((Emulator3D) Graphics3D.getImpl()).invalidateTexture(this);
//			return;
//		}
		if (imageData == null || imageData.length != l * 3)
			imageData = new byte[l * 3];
		byte[] pixels = imageData;

		for (int i = l - 1; i >= 0; --i) {
			pixels[i * 3] = (byte) (data[i] >> 16 & 255);
			pixels[i * 3 + 1] = (byte) (data[i] >> 8 & 255);
			pixels[i * 3 + 2] = (byte) (data[i] & 255);
		}
//		allocateBuffer(imageData.length);
		((Emulator3D) Graphics3D.getImpl()).invalidateTexture(this);
	}

//	public ByteBuffer getBuffer() {
//		buffer.position(buffer.capacity() - size);
//		return buffer;
//	}
//
//	private void allocateBuffer(int size) {
//		if (buffer == null || buffer.capacity() < size) {
//			buffer = ByteBuffer.allocateDirect((size * 4 / 3) << 2)
//					.order(ByteOrder.nativeOrder());
//		}
//		buffer.position(buffer.capacity() - size);
//		this.size = size;
//		if (imageData != null) {
//			buffer.put(imageData);
//			imageData = null;
//		}
//	}
}
