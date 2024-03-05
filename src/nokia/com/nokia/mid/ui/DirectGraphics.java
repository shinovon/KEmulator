package com.nokia.mid.ui;

import javax.microedition.lcdui.Image;

public abstract interface DirectGraphics {
    public static final int FLIP_HORIZONTAL = 8192;
    public static final int FLIP_VERTICAL = 16384;
    public static final int ROTATE_90 = 90;
    public static final int ROTATE_180 = 180;
    public static final int ROTATE_270 = 270;
    public static final int TYPE_BYTE_1_GRAY = 1;
    public static final int TYPE_BYTE_1_GRAY_VERTICAL = -1;
    public static final int TYPE_BYTE_2_GRAY = 2;
    public static final int TYPE_BYTE_4_GRAY = 4;
    public static final int TYPE_BYTE_8_GRAY = 8;
    public static final int TYPE_BYTE_332_RGB = 332;
    public static final int TYPE_USHORT_4444_ARGB = 4444;
    public static final int TYPE_USHORT_444_RGB = 444;
    public static final int TYPE_USHORT_555_RGB = 555;
    public static final int TYPE_USHORT_1555_ARGB = 1555;
    public static final int TYPE_USHORT_565_RGB = 565;
    public static final int TYPE_INT_888_RGB = 888;
    public static final int TYPE_INT_8888_ARGB = 8888;

    public abstract void drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    public abstract void drawPixels(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);

    public abstract void drawPixels(int[] paramArrayOfInt, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);

    public abstract void drawPixels(short[] paramArrayOfShort, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);

    public abstract void drawPolygon(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4);

    public abstract void drawTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

    public abstract void fillPolygon(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4);

    public abstract void fillTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

    public abstract int getAlphaComponent();

    public abstract int getNativePixelFormat();

    public abstract void getPixels(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

    public abstract void getPixels(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

    public abstract void getPixels(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

    public abstract void setARGBColor(int paramInt);
}
