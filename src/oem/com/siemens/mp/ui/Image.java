package com.siemens.mp.ui;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;

public class Image extends com.siemens.mp.misc.NativeMem {

    public Image() {
    }
    public Image(byte[] imageData) {
        System.out.println("public Image(byte[] imageData)");
    }

    public Image(byte[] bytes, int imageWidth, int imageHeight) {
        System.out.println("public Image(byte[] bytes, int imageWidth, int imageHeight)");
    }

    public Image(byte[] bytes, int imageWidth, int imageHeight, boolean transparent) {
        System.out.println("public Image(byte[] bytes, int imageWidth, int imageHeight, boolean transparent)");
    }

    public Image(Image image) {
        System.out.println("public Image(Image image)");
    }

    public Image(int imageWidth, int imageHeight) {
        System.out.println("public Image(int imageWidth, int imageHeight)");
    }

    public Image(String name, boolean doScale) {
        System.out.println("public Image(String name, boolean doScale)");
    }

    public static javax.microedition.lcdui.Image createImageFromBitmap(byte[] imageData, int imageWidth, int imageHeight) {
        return createImageFromBitmap(imageData, null, imageWidth, imageHeight);
    }

    public static javax.microedition.lcdui.Image createImageFromBitmap(byte[] imageData, byte[] alpha, int imageWidth, int imageHeight) {
        if (imageData == null) {
            return null;
        }
        javax.microedition.lcdui.Image ret = javax.microedition.lcdui.Image.createImage(imageWidth, imageHeight);
        if (imageWidth < 8) {
            imageWidth = 8;
        }
        Graphics g = ret.getGraphics();
        g.setColor(0);
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth / 8; x++) {
                for (int b = 7; b >= 0; b--) {
                    int c = doAlpha(imageData, alpha, y * imageWidth / 8 + x, b);
                    g.setColor(c);
                    g.drawLine(x * 8 + 7 - b, y, x * 8 + 7 - b, y);
                }
            }
        }
        return ret;
    }

    public static javax.microedition.lcdui.Image createImageWithoutScaling(String name)
            throws IOException {
        return javax.microedition.lcdui.Image.createImage(name);
    }

    public static javax.microedition.lcdui.Image createTransparentImageFromBitmap(byte[] imageData, int imageWidth, int imageHeight) {
        if (imageData == null) {
            return null;
        }
        javax.microedition.lcdui.Image ret = javax.microedition.lcdui.Image.createImage(imageWidth, imageHeight);
        if (imageWidth < 4) {
            imageWidth = 4;
        }
        Graphics g = ret.getGraphics();
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth / 4; x++) {
                for (int b = 7; b >= 0; b -= 2) {
                    if (isBitSet(imageData[(y * imageWidth / 4 + x)], b)) {
                        g.drawLine(x * 4 + 3 - b / 2, y, x * 4 + 3 - b / 2, y);
                    }
                }
            }
        }
        return ret;
    }

    public int getHeight() {
        System.out.println("public int getHeight()");
        return 0;
    }

    public static Image getNativeImage(javax.microedition.lcdui.Image img) {
        System.out.println("public static Image getNativeImage(javax.microedition.lcdui.Image img)");
        return null;
    }

    public int getWidth() {
        System.out.println("public int getWidth()");
        return 0;
    }

    private static boolean isBitSet(byte b, int pos) {
        return (b & (byte) (1 << pos)) != 0;
    }

    public static int doAlpha(byte[] pix, byte[] alpha, int pos, int shift) {
        int p;
        if (isBitSet(pix[pos], shift)) {
            p = 0;
        } else {
            p = 16777215;
        }
        int a;
        if ((alpha == null) || (isBitSet(alpha[pos], shift))) {
            a = -16777216;
        } else {
            a = 0;
        }
        return p | a;
    }

    public static void mirrorImageVertically(javax.microedition.lcdui.Image image) {}

    protected static void setNativeImage(javax.microedition.lcdui.Image img, Image simg) {}
}
