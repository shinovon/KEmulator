/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import javax.microedition.lcdui.Image;

public class Texture {
	//8-bit indexed color texture data
	byte[] bitmapData;
	//32-bit color bitmap for environment mapping
	int[] envmapData;
	//Image debugImage;
	int width, height;
	int widthBit, heightBit;
	int paddedWidth, paddedHeight;
	
	//ARGB palette (256 colors or 256*32 when shade lookup table is used)
	int[] origPalette, palette;
	boolean firstColorIsBlack;
	
	//True if this is a model texture, false if for environment mapping
	boolean isForModel;
	
	//Used in Graphics3D polygons list
	int g3dBindIdx = -1;

	public Texture(byte[] b, boolean isForModel) {
		if (b == null) throw new NullPointerException();
		this.isForModel = isForModel;
		loadBMP(b);
	}

	public Texture(String name, boolean isForModel) throws IOException {
		if (name == null) throw new NullPointerException();
		this.isForModel = isForModel;

		InputStream is = getClass().getResourceAsStream(name);
		if (is == null) throw new IOException("Resource not found: " + name);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[Math.max(1024, is.available())];
		
		int len;
		while ((len = is.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		
		is.close();

		loadBMP(baos.toByteArray());
	}
	
	private static final int BMP_FILE_HEADER_SIZE = 14;
	private static final int BMP_VERSION_3 = 40;
	private static final int BMP_VERSION_CORE = 12;

	private final void loadBMP(byte[] data) {
		Loader loader = new Loader(data);
		if (loader.readUByte() != 'B' || loader.readUByte() != 'M') {
			throw new RuntimeException("Not a BMP!");
		}
		
		loader.skip(BMP_FILE_HEADER_SIZE - 6);

		int rasterOffset = loader.readInt();
		int dibHeaderSize = loader.readInt();

		int numColors;
		boolean reversed;
		
		if (dibHeaderSize == BMP_VERSION_CORE) {
			width = loader.readUShort();
			height = loader.readUShort();
			loader.skip(2);
			
			int bpp = loader.readUShort();
			if (bpp != 8) {
				throw new RuntimeException("Unsupported BMP format: bpp = " + bpp);
			}
			
			numColors = 256;
			reversed = true;
		} else if (dibHeaderSize == BMP_VERSION_3) {
			width = loader.readInt();
			int h = loader.readInt();
			height = Math.abs(h);
			reversed = h >= 0;
			
			loader.skip(2);
			
			int bpp = loader.readUShort();
			if (bpp != 8) {
				throw new RuntimeException("Unsupported BMP format: bpp = " + bpp);
			}
			
			int compression = loader.readInt();
			if (compression != 0) {
				throw new RuntimeException("Unsupported BMP format: compression = " + compression);
			}
			
			loader.skip(12);
			numColors = loader.readInt();
			if (numColors == 0) numColors = 256;
			loader.skip(4);
		} else {
			throw new RuntimeException("Unsupported BMP version = " + dibHeaderSize);
		}

		int paletteOffset = BMP_FILE_HEADER_SIZE + dibHeaderSize;
		//Fix for broken bmp files
		if (rasterOffset < paletteOffset + numColors * 4) rasterOffset = paletteOffset + numColors * 4;
		
		palette = new int[256];
		for (int i = 0; i < numColors; i++) {
			int idx = i * 4 + paletteOffset;
			
			palette[i] = 
					0xff000000 | 
					((data[idx + 2] & 0xff) << 16) | 
					((data[idx + 1] & 0xff) << 8) | 
					(data[idx] & 0xff);
		}

		bitmapData = new byte[width * height];
		
		int remainder = width & 3;
		int stride = remainder == 0 ? width : width + 4 - remainder;
		int bitmapIdx = 0;
		
		if (reversed) {
			for (int i = height - 1; i >= 0; i--) {
				for (int j = rasterOffset + i * stride, s = j + width; j < s; j++, bitmapIdx++) {
					bitmapData[bitmapIdx] = data[j];
				}
			}
		} else {
			for (int i = 0; i < height; i++) {
				for (int j = rasterOffset + i * stride, s = j + width; j < s; j++, bitmapIdx++) {
					bitmapData[bitmapIdx] = data[j];
				}
			}
		}
		
		//Implementation specific details
		origPalette = palette;
		firstColorIsBlack = palette[0] == 0xff000000;
		
		widthBit = countBits(width);
		heightBit = countBits(height);
		//Only 64x64 sphere textures are supported in MascotCapsule v3
		if (!isForModel) widthBit = heightBit = 6;
		
		//Padding for NPOT textures
		paddedWidth = (1 << widthBit);
		paddedHeight = (1 << heightBit);
		
		if (paddedWidth != width || paddedHeight != height) {
			byte[] paddedBitmap = new byte[paddedWidth * paddedHeight];
			
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					paddedBitmap[y * paddedWidth + x] = bitmapData[y * width + x];
				}
			}
			
			bitmapData = paddedBitmap;
		}
		
		/*int[] rgbData = new int[bitmapData.length];
		for (int i = 0; i < rgbData.length; i++) {
			rgbData[i] = palette[bitmapData[i] & 0xff];
		}
		
		debugImage = Image.createRGBImage(rgbData, paddedWidth, paddedHeight, false);*/
		
		if (!isForModel) {
			envmapData = new int[bitmapData.length];
			
			for (int i = 0; i < envmapData.length; i++) {
				int palId = bitmapData[i] & 0xff;
				//First envmap color is ignored in mcv3
				int color = palId > 0 ? color = palette[palId] : 0;
				
				envmapData[i] = color & 0xffffff;
			}
			
			bitmapData = null;
			origPalette = null;
			palette = null;
		}
	}
	
	private final int countBits(int x) {
		for (int i = 0; i < 32; i++) {
			if(1 << i >= x) return i;
		}
		
		return 0;
	}

	final void generateShadedPalette() {
		int[] newPalette = new int[256 * 32];
		
		for (int shade = 0; shade < 32; shade++) {
			for (int i = 0; i < 256; i++) {
				int color = palette[i];

				int r = (color >> 16) & 0xFF;
				int g = (color >> 8) & 0xFF;
				int b = color & 0xFF;

				r = (r * shade) / 31;
				g = (g * shade) / 31;
				b = (b * shade) / 31;

				newPalette[shade * 256 + i] = 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}
		
		palette = newPalette;
	}

	public final void dispose() {
		bitmapData = null;
		envmapData = null;
		palette = null;
	}
}