package com.nokia.mid.ui;

import emulator.Emulator;
import emulator.Settings;
import emulator.debug.Profiler;
import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.ITransform;
import emulator.graphics2D.b;
import emulator.graphics2D.swt.TransformSWT;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import java.awt.geom.AffineTransform;

final class a
        implements DirectGraphics {
    Graphics gc;
    IGraphics2D impl;
    static final int[][] jdField_a_of_type_Array2dOfInt = {{0, 24756}, {16384, 8372}, {8192, 16564}, {180, 24576}, {16474, 8462}, {270, 24666}, {90, 24846}, {8282, 16654}};

    public a(Graphics paramGraphics) {
        this.gc = paramGraphics;
        this.impl = paramGraphics.getImpl();
    }

    private static int a(int paramInt) {
        for (int i = 0; i < jdField_a_of_type_Array2dOfInt.length; i++) {
            for (int j = 0; j < jdField_a_of_type_Array2dOfInt[i].length; j++) {
                if (jdField_a_of_type_Array2dOfInt[i][j] == paramInt) {
                    return i;
                }
            }
        }
        Emulator.getEmulator().getLogStream().println("*** nokiaManip2MIDP2Manip: Invalid Nokia Manipulation: " + paramInt);
        return 0;
    }

    public final void drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        IImage localIImage;
        int i = (localIImage = paramImage.getImpl()).getWidth();
        int j = localIImage.getHeight();
        int k = a(paramInt4);
        this.gc.drawRegion(paramImage, 0, 0, i, j, k, paramInt1, paramInt2, paramInt3);
        Profiler.nokiaDrawImageCallCount += 1;
        Profiler.nokiaDrawImagePixelCount += i * j;
        Profiler.drawRegionCallCount -= 1;
        Profiler.drawRegionPixelCount -= i * j;
    }

    public final void drawPixels(short[] paramArrayOfShort, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        IImage localIImage = b.method164(paramArrayOfShort, paramBoolean, paramInt1, paramInt2, paramInt5, paramInt6);
        ITransform localITransform = this.impl.getTransform().newTransform(paramInt5, paramInt6, a(paramInt7), paramInt3, paramInt4, 0);
        this.gc.drawRegion(localIImage, 0, 0, paramInt5, paramInt6, localITransform, 65280);
        Profiler.nokiaDrawPixelCallCount += 1;
        Profiler.nokiaDrawPixelPixelCount += paramInt5 * paramInt6;
    }

    public void drawPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
    {
        // From FreeJ2ME
        int[] Type1 = {0xFFFFFFFF, 0xFF000000, 0x00FFFFFF, 0x00000000};
        int c = 0;
        int[] data;
        Image temp;
        switch(format)
        {
            case -1: // TYPE_BYTE_1_GRAY_VERTICAL // used by Monkiki's Castles
                data = new int[width*height];
                int ods = offset / scanlength;
                int oms = offset % scanlength;
                int b = ods % 8; //Bit offset in a byte
                for (int yj = 0; yj < height; yj++)
                {
                    int ypos = yj * width;
                    int tmp = (ods + yj) / 8 * scanlength+oms;
                    for (int xj = 0; xj < width; xj++)
                    {
                        c = ((pixels[tmp + xj]>>b)&1);
                        if(transparencyMask!=null) { c |= (((transparencyMask[tmp + xj]>>b)&1)^1)<<1; }
                        data[(yj*width)+xj] = Type1[c];
                    }
                    b++;
                    if(b>7) b=0;
                }

                temp = Image.createImage(width, height, 0);
                temp.getImpl().setRGB(0, 0, width, height, data, 0, width);
                gc.drawImage(manipulateImage(temp, manipulation), x, y, 0);
                break;

            case 1: // TYPE_BYTE_1_GRAY // used by Monkiki's Castles
                data = new int[pixels.length*8];

                for(int i=(offset/8); i<pixels.length; i++)
                {
                    for(int j=7; j>=0; j--)
                    {
                        c = ((pixels[i]>>j)&1);
                        if(transparencyMask!=null) { c |= (((transparencyMask[i]>>j)&1)^1)<<1; }
                        data[(i*8)+(7-j)] = Type1[c];
                    }
                }
                temp = Image.createImage(width, height, 0);
                temp.getImpl().setRGB(0, 0, width, height, data, 0, scanlength);
                gc.drawImage(manipulateImage(temp, manipulation), x, y, 0);
                break;

            default: System.out.println("drawPixels A : Format " + format + " Not Implemented");
        }
    }

    public final void drawPixels(int[] paramArrayOfInt, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        IImage localIImage = b.method163(paramArrayOfInt, paramBoolean, paramInt1, paramInt2, paramInt5, paramInt6);
        ITransform localITransform = this.impl.getTransform().newTransform(paramInt5, paramInt6, a(paramInt7), paramInt3, paramInt4, 0);
        this.gc.drawRegion(localIImage, 0, 0, paramInt5, paramInt6, localITransform, 65280);
        Profiler.nokiaDrawPixelCallCount += 1;
        Profiler.nokiaDrawPixelPixelCount += paramInt5 * paramInt6;
    }

    public final void drawPolygon(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4) {
        int[] arrayOfInt1 = new int[paramInt3];
        int[] arrayOfInt2 = new int[paramInt3];
        System.arraycopy(paramArrayOfInt1, paramInt1, arrayOfInt1, 0, paramInt3);
        System.arraycopy(paramArrayOfInt2, paramInt2, arrayOfInt2, 0, paramInt3);
        int[] arrayOfInt3 = new int[paramInt3 << 1];
        for (int i = 0; i < paramInt3; i++) {
            arrayOfInt3[(i << 1)] = arrayOfInt1[i];
            arrayOfInt3[((i << 1) + 1)] = arrayOfInt2[i];
        }
        int i = this.impl.getColor();
        this.impl.setColor(paramInt4, true);
        this.impl.drawPolygon(arrayOfInt3);
        this.impl.setColor(i, true);
    }

    public final void drawTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        drawPolygon(new int[]{paramInt1, paramInt3, paramInt5}, 0, new int[]{paramInt2, paramInt4, paramInt6}, 0, 3, paramInt7);
    }

    public final void fillPolygon(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4) {
        int[] arrayOfInt1 = new int[paramInt3];
        int[] arrayOfInt2 = new int[paramInt3];
        System.arraycopy(paramArrayOfInt1, paramInt1, arrayOfInt1, 0, paramInt3);
        System.arraycopy(paramArrayOfInt2, paramInt2, arrayOfInt2, 0, paramInt3);
        int[] arrayOfInt3 = new int[paramInt3 << 1];
        for (int i = 0; i < paramInt3; i++) {
            arrayOfInt3[(i << 1)] = arrayOfInt1[i];
            arrayOfInt3[((i << 1) + 1)] = arrayOfInt2[i];
        }
        int i = this.impl.getColor();
        this.impl.setColor(paramInt4, true);
        this.impl.fillPolygon(arrayOfInt3);
        this.impl.setColor(i, true);
    }

    public final void fillTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        fillPolygon(new int[]{paramInt1, paramInt3, paramInt5}, 0, new int[]{paramInt2, paramInt4, paramInt6}, 0, 3, paramInt7);
    }

    public final int getAlphaComponent() {
        return this.impl.getColor() >> 24 & 0xFF;
    }

    public final int getNativePixelFormat() {
        return 4444;
    }

    public final void getPixels(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        b.method165(this.gc.getImage(), paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public final void getPixels(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        b.method166(this.gc.getImage(), paramArrayOfShort, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public final void getPixels(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        Emulator.getEmulator().getLogStream().println("***com.nokia.mid.ui.getPixels(byte[]) not implemented yet");
    }

    public final void setARGBColor(int paramInt) {
        this.impl.setColor(paramInt, true);
    }

    private static Image manipulateImage(Image image, int manipulation)
    {
        final int HV = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL;
        final int H90 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_90;
        switch(manipulation)
        {
            case DirectGraphics.FLIP_HORIZONTAL:
                return transformImage(image, Sprite.TRANS_MIRROR);
            case DirectGraphics.FLIP_VERTICAL:
                return transformImage(image, Sprite.TRANS_MIRROR_ROT180);
            case DirectGraphics.ROTATE_90:
                return transformImage(image, Sprite.TRANS_ROT270);
            case DirectGraphics.ROTATE_180:
                return transformImage(image, Sprite.TRANS_ROT180);
            case DirectGraphics.ROTATE_270:
                return transformImage(image, Sprite.TRANS_ROT90);
            case HV:
                return transformImage(image, Sprite.TRANS_ROT180);
            case H90:
                return transformImage(transformImage(image, Sprite.TRANS_MIRROR), Sprite.TRANS_ROT270);
            case 0: /* No Manipulation */ break;
            default:
                System.out.println("manipulateImage "+manipulation+" not defined");
        }
        return image;
    }
    public static Image transformImage(Image image, int transform)
    {
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        int out_width = width;
        int out_height = height;

        AffineTransform af = new AffineTransform();

        switch (transform) {
            case Sprite.TRANS_NONE:
                break;

            case Sprite.TRANS_ROT90:
                af.translate(height, 0);
                af.rotate(Math.PI / 2);
                out_width = height;
                out_height = width;
                break;

            case Sprite.TRANS_ROT180:
                af.translate(width, height);
                af.rotate(Math.PI);
                break;

            case Sprite.TRANS_ROT270:
                af.translate(0, width);
                af.rotate(Math.PI * 3 / 2);
                out_width = height;
                out_height = width;
                break;

            case Sprite.TRANS_MIRROR:
                af.translate(width, 0);
                af.scale(-1, 1);
                break;

            case Sprite.TRANS_MIRROR_ROT90:
                af.translate(height, 0);
                af.rotate(Math.PI / 2);
                af.translate(width, 0);
                af.scale(-1, 1);
                out_width = height;
                out_height = width;
                break;

            case Sprite.TRANS_MIRROR_ROT180:
                af.translate(width, 0);
                af.scale(-1, 1);
                af.translate(width, height);
                af.rotate(Math.PI);
                break;

            case Sprite.TRANS_MIRROR_ROT270:
                af.translate(0, width);
                af.rotate(Math.PI * 3 / 2);
                af.translate(width, 0);
                af.scale(-1, 1);
                out_width = height;
                out_height = width;
                break;
        }

        Image transimage = Image.createImage(out_width, out_height, 0);
        Graphics gc = transimage.getGraphics();
        if (Settings.g2d == 0) {
            gc.getImpl().setTransform(new TransformSWT(af));
        } else {
            gc.getImpl().setTransform(new emulator.graphics2D.awt.c(af));
        }
        gc.drawImage(image, 0, 0, 0);
        return transimage;
    }
}
