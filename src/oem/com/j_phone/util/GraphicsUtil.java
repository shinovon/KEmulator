package com.j_phone.util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class GraphicsUtil {
	public static final int TRANS_NONE = 0;
	public static final int TRANS_ROT90 = 5;
	public static final int TRANS_ROT180 = 3;
	public static final int TRANS_ROT270 = 6;
	public static final int TRANS_MIRROR = 2;
	public static final int TRANS_MIRROR_ROT90 = 7;
	public static final int TRANS_MIRROR_ROT180 = 1;
	public static final int TRANS_MIRROR_ROT270 = 4;
	public static final int STRETCH_QUALITY_NORMAL = 0;
	public static final int STRETCH_QUALITY_LOW = 1;
	public static final int STRETCH_QUALITY_HIGH = 2;

	public static int getPixel(Graphics g, int x, int y) {
		return g.getImage().getRGB(x, y);
	}

	public static void setPixel(Graphics g, int x, int y) {
		g.getImage().setRGB(x, y, 0);
	}

	public static void setPixel(Graphics g, int x, int y, int color) {
		g.getImage().setRGB(x, y, color);
	}

	public static void drawRegion(Graphics g, Image src, int x_src, int y_src, int width, int height, int transform,
								  int x_dest, int y_dest, int anchor) {
		g.drawRegion(src, x_src, y_src, width, height, transform, x_dest, y_dest, anchor);
	}

	public static void drawRegion(Graphics g, Image src, int x_src, int y_src, int width, int height, int transform,
								  int x_dest, int y_dest, int width_dest, int height_dest, int anchor,
								  int stretch_quality) {
		g.drawRegion(src, x_src, y_src, width, height, transform, x_dest, y_dest, anchor);
	}

	public static void drawPseudoTransparentImage(Graphics g, Image src, int x_dest, int y_dest, int anchor, short mask_pattern, int element_size) {
		g.drawImage(src, x_dest, y_dest, anchor);
	}
}
