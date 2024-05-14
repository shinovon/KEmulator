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

    public static int getPixel(Graphics paramGraphics, int paramInt1, int paramInt2) {
        return paramGraphics.getImage().getRGB(paramInt1, paramInt2);
    }

    public static void setPixel(Graphics paramGraphics, int paramInt1, int paramInt2) {
        paramGraphics.getImage().setRGB(paramInt1, paramInt2, 0);
    }

    public static void setPixel(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
        paramGraphics.getImage().setRGB(paramInt1, paramInt2, paramInt3);
    }

    public static void drawRegion(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    }

    public static void drawRegion(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11) {
    }

    public static void drawPseudoTransparentImage(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2, int paramInt3, short paramShort, int paramInt4) {
    }
}
