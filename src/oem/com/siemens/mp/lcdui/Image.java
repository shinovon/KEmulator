/*
 * Copyright 2018 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.siemens.mp.lcdui;

import emulator.Emulator;
import emulator.graphics2D.awt.AWTImageUtils;
import emulator.graphics2D.awt.ImageAWT;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class Image extends com.siemens.mp.ui.Image {

	public static javax.microedition.lcdui.Image createImageFromFile(
			String resname, boolean scaleToFullScreen) throws IOException {
		if (scaleToFullScreen) {
			return createImageFromFile(resname, Emulator.getEmulator().getScreen().getWidth(),
					Emulator.getEmulator().getScreen().getHeight());
		}
		return javax.microedition.lcdui.Image.createImage(resname);
	}

	public static javax.microedition.lcdui.Image createImageFromFile(
			String resname, int scaleToWidth, int scaleToHeight) throws IOException {
		javax.microedition.lcdui.Image img = javax.microedition.lcdui.Image.createImage(resname);

		if (scaleToWidth != 0 || scaleToHeight != 0) {
			if (scaleToWidth == 0) {
				scaleToWidth = (int) (((double) img.getWidth() / img.getHeight()) * scaleToHeight);
			} else if (scaleToHeight == 0) {
				scaleToHeight = (int) (((double) img.getHeight() / img.getWidth()) * scaleToWidth);
			}

			return new javax.microedition.lcdui.Image(
					new ImageAWT(AWTImageUtils.resize((BufferedImage) img._getImpl().getNative(),
							scaleToWidth, scaleToHeight)));
		}

		return img;
	}

	public static int getPixelColor(javax.microedition.lcdui.Image image, int x, int y) {
		return image._getImpl().getRGB(x, y);
	}

	public static void setPixelColor(
			javax.microedition.lcdui.Image image, int x, int y, int color) throws IllegalArgumentException {
		image._getImpl().setRGB(x, y, color);
	}

	public static void writeImageToFile(javax.microedition.lcdui.Image img, String file)
			throws IOException {
		FileConnection f = (FileConnection) Connector.open("file://" + file);
		try (OutputStream out = f.openOutputStream()) {
			img._getImpl().write(out, "png");
		} finally {
			f.close();
		}
	}

	public static void writeBmpToFile(javax.microedition.lcdui.Image image, String filename) throws IOException {
		FileConnection f = (FileConnection) Connector.open("file://" + filename);
		try (OutputStream out = f.openOutputStream()) {
			image._getImpl().write(out, "bmp");
		} finally {
			f.close();
		}
	}

	public static javax.microedition.lcdui.Image clipAndScaleImage(
			javax.microedition.lcdui.Image img, int n1, int n2, int n3, int n4, int n5, int n6) {
		// TODO
		return img;
	}

	public static javax.microedition.lcdui.Image createTransparentImageFromMask(javax.microedition.lcdui.Image image, javax.microedition.lcdui.Image mask) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imagePixels = new int[width * height];
		int[] maskPixels = new int[width * height];

		image.getRGB(imagePixels, 0, width, 0, 0, width, height);
		mask.getRGB(maskPixels, 0, width, 0, 0, width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (maskPixels[y * width + x] == 0xFFFFFFFF) {
					imagePixels[y * width + x] = 0;
				}
			}
		}
		return javax.microedition.lcdui.Image.createRGBImage(imagePixels, width, height, true);
	}
}