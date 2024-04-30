package com.nokia.mid.ui;

import emulator.Emulator;
import emulator.debug.Profiler;
import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.ITransform;
import emulator.graphics2D.b;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

final class a
        implements DirectGraphics {
    Graphics jdField_a_of_type_JavaxMicroeditionLcduiGraphics;
    IGraphics2D jdField_a_of_type_EmulatorGraphics2DIGraphics2D;
    static final int[][] jdField_a_of_type_Array2dOfInt = {{0, 24756}, {16384, 8372}, {8192, 16564}, {180, 24576}, {16474, 8462}, {270, 24666}, {90, 24846}, {8282, 16654}};

    public a(Graphics paramGraphics) {
        this.jdField_a_of_type_JavaxMicroeditionLcduiGraphics = paramGraphics;
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D = paramGraphics.getImpl();
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
        this.jdField_a_of_type_JavaxMicroeditionLcduiGraphics.drawRegion(paramImage, 0, 0, i, j, k, paramInt1, paramInt2, paramInt3);
        Profiler.nokiaDrawImageCallCount += 1;
        Profiler.nokiaDrawImagePixelCount += i * j;
        Profiler.drawRegionCallCount -= 1;
        Profiler.drawRegionPixelCount -= i * j;
    }

    public final void drawPixels(short[] paramArrayOfShort, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        IImage localIImage = b.method164(paramArrayOfShort, paramBoolean, paramInt1, paramInt2, paramInt5, paramInt6);
        ITransform localITransform = this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.getTransform().newTransform(paramInt5, paramInt6, a(paramInt7), paramInt3, paramInt4, 0);
        this.jdField_a_of_type_JavaxMicroeditionLcduiGraphics.drawRegion(localIImage, 0, 0, paramInt5, paramInt6, localITransform, 65280);
        Profiler.nokiaDrawPixelCallCount += 1;
        Profiler.nokiaDrawPixelPixelCount += paramInt5 * paramInt6;
    }

    public final void drawPixels(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        Emulator.getEmulator().getLogStream().println("***com.nokia.mid.ui.drawPixels(byte[]) not implemented yet");
    }

    public final void drawPixels(int[] paramArrayOfInt, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        IImage localIImage = b.method163(paramArrayOfInt, paramBoolean, paramInt1, paramInt2, paramInt5, paramInt6);
        ITransform localITransform = this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.getTransform().newTransform(paramInt5, paramInt6, a(paramInt7), paramInt3, paramInt4, 0);
        this.jdField_a_of_type_JavaxMicroeditionLcduiGraphics.drawRegion(localIImage, 0, 0, paramInt5, paramInt6, localITransform, 65280);
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
        int i = this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.getColor();
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.setColor(paramInt4, true);
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.drawPolygon(arrayOfInt3);
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.setColor(i, true);
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
        int i = this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.getColor();
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.setColor(paramInt4, true);
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.fillPolygon(arrayOfInt3);
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.setColor(i, true);
    }

    public final void fillTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        fillPolygon(new int[]{paramInt1, paramInt3, paramInt5}, 0, new int[]{paramInt2, paramInt4, paramInt6}, 0, 3, paramInt7);
    }

    public final int getAlphaComponent() {
        return this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.getColor() >> 24 & 0xFF;
    }

    public final int getNativePixelFormat() {
        return 4444;
    }

    public final void getPixels(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        b.method165(this.jdField_a_of_type_JavaxMicroeditionLcduiGraphics.getImage(), paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public final void getPixels(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        b.method166(this.jdField_a_of_type_JavaxMicroeditionLcduiGraphics.getImage(), paramArrayOfShort, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public final void getPixels(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        Emulator.getEmulator().getLogStream().println("***com.nokia.mid.ui.getPixels(byte[]) not implemented yet");
    }

    public final void setARGBColor(int paramInt) {
        this.jdField_a_of_type_EmulatorGraphics2DIGraphics2D.setColor(paramInt, true);
    }
}
