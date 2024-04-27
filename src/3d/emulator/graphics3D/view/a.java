package emulator.graphics3D.view;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public final class a {
    private static ByteBuffer aByteBuffer596;
    private static ByteBuffer aByteBuffer599;
    private static IntBuffer anIntBuffer597;
    private static IntBuffer anIntBuffer600;
    private static FloatBuffer aFloatBuffer598;
    private static FloatBuffer aFloatBuffer601;

    public static ByteBuffer method394(byte[] paramArrayOfByte) {
        if ((aByteBuffer596 == null) || (aByteBuffer596.capacity() < paramArrayOfByte.length)) {
            aByteBuffer596 = BufferUtils.createByteBuffer(paramArrayOfByte.length);
        }
        aByteBuffer596.position(aByteBuffer596.capacity() - paramArrayOfByte.length);
        aByteBuffer596.put(paramArrayOfByte);
        aByteBuffer596.position(aByteBuffer596.capacity() - paramArrayOfByte.length);
        return aByteBuffer596;
    }

    public static ByteBuffer method395(short[] paramArrayOfShort) {
        if ((aByteBuffer596 == null) || (aByteBuffer596.capacity() < paramArrayOfShort.length)) {
            aByteBuffer596 = BufferUtils.createByteBuffer(paramArrayOfShort.length);
        }
        aByteBuffer596.position(aByteBuffer596.capacity() - paramArrayOfShort.length);
        for (int i = 0; i < paramArrayOfShort.length; i++) {
            aByteBuffer596.put((byte) (paramArrayOfShort[i] / 257));
        }
        aByteBuffer596.position(aByteBuffer596.capacity() - paramArrayOfShort.length);
        return aByteBuffer596;
    }

    public static ByteBuffer method396(byte[] paramArrayOfByte, float paramFloat, int paramInt) {
        int i = paramFloat == 1.0F ? paramArrayOfByte.length : 4 * paramInt;
        if ((aByteBuffer599 == null) || (aByteBuffer599.capacity() < i)) {
            aByteBuffer599 = BufferUtils.createByteBuffer(i);
        }
        aByteBuffer599.position(aByteBuffer599.capacity() - i);
        if (paramFloat == 1.0F) {
            aByteBuffer599.put(paramArrayOfByte);
        } else {
            if (paramArrayOfByte.length == i) {
                int j = 0;
                while (j < i) {
                    aByteBuffer599.put(paramArrayOfByte[(j++)]);
                    aByteBuffer599.put(paramArrayOfByte[(j++)]);
                    aByteBuffer599.put(paramArrayOfByte[(j++)]);
                    aByteBuffer599.put((byte) (int) ((paramArrayOfByte[(j++)] & 0xFF) * paramFloat + 0.5F));
                }
            }
            int j = 0;
            while (j < paramArrayOfByte.length) {
                aByteBuffer599.put(paramArrayOfByte[(j++)]);
                aByteBuffer599.put(paramArrayOfByte[(j++)]);
                aByteBuffer599.put(paramArrayOfByte[(j++)]);
                aByteBuffer599.put((byte) (int) (255.0F * paramFloat + 0.5F));
            }
        }
        aByteBuffer599.position(aByteBuffer599.capacity() - i);
        return aByteBuffer599;
    }

    public static ByteBuffer method397(short[] paramArrayOfShort, float paramFloat, int paramInt) {
        int i = paramFloat == 1.0F ? paramArrayOfShort.length : 4 * paramInt;
        if ((aByteBuffer599 == null) || (aByteBuffer599.capacity() < i)) {
            aByteBuffer599 = BufferUtils.createByteBuffer(i);
        }
        aByteBuffer599.position(aByteBuffer599.capacity() - i);
        int j;
        if (paramFloat == 1.0F) {
            for (j = 0; j < paramArrayOfShort.length; j++) {
                aByteBuffer596.put((byte) ((paramArrayOfShort[j] & 0xFFFF) / 257));
            }
        }
        if (paramArrayOfShort.length == i) {
            j = 0;
            while (j < i) {
                aByteBuffer599.put((byte) ((paramArrayOfShort[(j++)] & 0xFFFF) / 257));
                aByteBuffer599.put((byte) ((paramArrayOfShort[(j++)] & 0xFFFF) / 257));
                aByteBuffer599.put((byte) ((paramArrayOfShort[(j++)] & 0xFFFF) / 257));
                aByteBuffer599.put((byte) (int) (((byte) ((paramArrayOfShort[(j++)] & 0xFFFF) / 257) & 0xFF) * paramFloat + 0.5F));
            }
        }
        j = 0;
        while (0 < paramArrayOfShort.length) {
            aByteBuffer599.put((byte) ((paramArrayOfShort[0] & 0xFFFF) / 257));
            aByteBuffer599.put((byte) ((paramArrayOfShort[0] & 0xFFFF) / 257));
            aByteBuffer599.put((byte) ((paramArrayOfShort[0] & 0xFFFF) / 257));
            aByteBuffer599.put((byte) (int) (255.0F * paramFloat + 0.5F));
        }
        aByteBuffer599.position(aByteBuffer599.capacity() - i);
        return aByteBuffer599;
    }

    public static IntBuffer method398(int[] paramArrayOfInt) {
        if ((anIntBuffer597 == null) || (anIntBuffer597.capacity() < paramArrayOfInt.length)) {
            anIntBuffer597 = BufferUtils.createIntBuffer(paramArrayOfInt.length);
        }
        anIntBuffer597.position(anIntBuffer597.capacity() - paramArrayOfInt.length);
        anIntBuffer597.put(paramArrayOfInt);
        anIntBuffer597.position(anIntBuffer597.capacity() - paramArrayOfInt.length);
        return anIntBuffer597;
    }

    public static IntBuffer method399(short[] paramArrayOfShort) {
        if ((anIntBuffer600 == null) || (anIntBuffer600.capacity() < paramArrayOfShort.length)) {
            anIntBuffer600 = BufferUtils.createIntBuffer(paramArrayOfShort.length);
        }
        anIntBuffer600.position(anIntBuffer600.capacity() - paramArrayOfShort.length);
        for (int i = 0; i < paramArrayOfShort.length; i++) {
            anIntBuffer600.put(paramArrayOfShort[i]);
        }
        anIntBuffer600.position(anIntBuffer600.capacity() - paramArrayOfShort.length);
        return anIntBuffer600;
    }

    public static IntBuffer method400(byte[] paramArrayOfByte) {
        if ((anIntBuffer600 == null) || (anIntBuffer600.capacity() < paramArrayOfByte.length)) {
            anIntBuffer600 = BufferUtils.createIntBuffer(paramArrayOfByte.length);
        }
        anIntBuffer600.position(anIntBuffer600.capacity() - paramArrayOfByte.length);
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            anIntBuffer600.put(paramArrayOfByte[i]);
        }
        anIntBuffer600.position(anIntBuffer600.capacity() - paramArrayOfByte.length);
        return anIntBuffer600;
    }

    public static FloatBuffer method401(float[] paramArrayOfFloat) {
        if ((aFloatBuffer598 == null) || (aFloatBuffer598.capacity() < paramArrayOfFloat.length)) {
            aFloatBuffer598 = BufferUtils.createFloatBuffer(paramArrayOfFloat.length);
        }
        aFloatBuffer598.position(aFloatBuffer598.capacity() - paramArrayOfFloat.length);
        aFloatBuffer598.put(paramArrayOfFloat);
        aFloatBuffer598.position(aFloatBuffer598.capacity() - paramArrayOfFloat.length);
        return aFloatBuffer598;
    }

    public static FloatBuffer method402(short[] paramArrayOfShort) {
        if ((aFloatBuffer601 == null) || (aFloatBuffer601.capacity() < paramArrayOfShort.length)) {
            aFloatBuffer601 = BufferUtils.createFloatBuffer(paramArrayOfShort.length);
        }
        aFloatBuffer601.position(aFloatBuffer601.capacity() - paramArrayOfShort.length);
        for (int i = 0; i < paramArrayOfShort.length; i++) {
            aFloatBuffer601.put(paramArrayOfShort[i]);
        }
        aFloatBuffer601.position(aFloatBuffer601.capacity() - paramArrayOfShort.length);
        return aFloatBuffer601;
    }

    public static FloatBuffer method403(byte[] paramArrayOfByte) {
        if ((aFloatBuffer601 == null) || (aFloatBuffer601.capacity() < paramArrayOfByte.length)) {
            aFloatBuffer601 = BufferUtils.createFloatBuffer(paramArrayOfByte.length);
        }
        aFloatBuffer601.position(aFloatBuffer601.capacity() - paramArrayOfByte.length);
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            aFloatBuffer601.put(paramArrayOfByte[i]);
        }
        aFloatBuffer601.position(aFloatBuffer601.capacity() - paramArrayOfByte.length);
        return aFloatBuffer601;
    }
}
