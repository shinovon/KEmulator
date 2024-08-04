package javax.microedition.m3g;

import java.nio.ByteBuffer;

public class Image2D
        extends Object3D {
    public static final int ALPHA = 96;
    public static final int LUMINANCE = 97;
    public static final int LUMINANCE_ALPHA = 98;
    public static final int RGB = 99;
    public static final int RGBA = 100;

    public Image2D(int paramInt, Object paramObject) {
        super(2);
    }

    public Image2D(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) {
        super(2);
    }

    public Image2D(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        super(2);
    }

    public Image2D(int paramInt1, int paramInt2, int paramInt3) {
        super(2);
    }

    public void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) {
    }

    public boolean isMutable() {
        return true;
    }

    public int getFormat() {
        return 1;
    }

    public int getWidth() {
        return 1;
    }

    public int getHeight() {
        return 1;
    }

    Image2D(int paramInt) {
        super(paramInt);
    }

    public boolean isPalettized() { return false; }

    public void getPixels(byte[] array) {
    }

    public void getPalette(byte[] array) {
    }

    public int getBitsPerColor() {
        return 0;
    }

    public int size() {
        return 0;
    }

    public byte[] getImageData() {
        return null;
    }

    public ByteBuffer getBuffer() {
        return null;
    }

    public boolean isLoaded() {
        return false;
    }

    public void setLoaded(boolean b) {
    }

    public void setId(int id) {
    }

    public int getId() {
        return 0;
    }
}
