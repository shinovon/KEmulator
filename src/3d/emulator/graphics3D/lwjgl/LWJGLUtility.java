package emulator.graphics3D.lwjgl;

import java.nio.*;

import org.lwjgl.BufferUtils;

public final class LWJGLUtility {
    private static ByteBuffer byteBuffer;
    private static ByteBuffer byteBuffer2;
    private static IntBuffer intBuffer;
    private static IntBuffer intBuffer2;
    private static FloatBuffer floatBuffer;
    private static FloatBuffer floatBuffer2;
    private static ShortBuffer shortBuffer;

    public static ByteBuffer getByteBuffer(byte[] var0) {
        if(byteBuffer == null || byteBuffer.capacity() < var0.length) {
            byteBuffer = BufferUtils.createByteBuffer(var0.length);
        }

        byteBuffer.position(byteBuffer.capacity() - var0.length);
        byteBuffer.put(var0);
        byteBuffer.position(byteBuffer.capacity() - var0.length);
        return byteBuffer;
    }

    public static ByteBuffer getByteBuffer(short[] var0) {
        if (byteBuffer == null || byteBuffer.capacity() < var0.length) {
            byteBuffer = BufferUtils.createByteBuffer(var0.length);
        }

        byteBuffer.position(byteBuffer.capacity() - var0.length);

        for(int var1 = 0; var1 < var0.length; ++var1) {
            byteBuffer.put((byte)(var0[var1] / 257));
        }

        byteBuffer.position(byteBuffer.capacity() - var0.length);
        return byteBuffer;
    }

    public static ByteBuffer getByteBuffer(byte[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(byteBuffer2 == null || byteBuffer2.capacity() < var3) {
            byteBuffer2 = BufferUtils.createByteBuffer(var3);
        }

        byteBuffer2.position(byteBuffer2.capacity() - var3);
        if(var1 == 1.0F) {
            byteBuffer2.put(var0);
        } else {
            int var4;
            if(var0.length == var3) {
                var4 = 0;

                while(var4 < var3) {
                    byteBuffer2.put(var0[var4++]);
                    byteBuffer2.put(var0[var4++]);
                    byteBuffer2.put(var0[var4++]);
                    byteBuffer2.put((byte)((int)((float)(var0[var4++] & 255) * var1 + 0.5F)));
                }
            } else {
                var4 = 0;

                while(var4 < var0.length) {
                    byteBuffer2.put(var0[var4++]);
                    byteBuffer2.put(var0[var4++]);
                    byteBuffer2.put(var0[var4++]);
                    byteBuffer2.put((byte)((int)(255.0F * var1 + 0.5F)));
                }
            }
        }

        byteBuffer2.position(byteBuffer2.capacity() - var3);
        return byteBuffer2;
    }

    public static ByteBuffer getByteBuffer(short[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F ? var0.length : 4 * var2;
        if (byteBuffer2 == null || byteBuffer2.capacity() < var3) {
            byteBuffer2 = BufferUtils.createByteBuffer(var3);
        }

        byteBuffer2.position(byteBuffer2.capacity() - var0.length);
        int var4;
        if (var1 == 1.0F) {
            for(var4 = 0; var4 < var0.length; ++var4) {
                byteBuffer2.put((byte)((var0[var4] & '\uffff') / 257));
            }
        } else if (var0.length == var3) {
            var4 = 0;

            while(var4 < var3) {
                byteBuffer2.put((byte)((var0[var4++] & '\uffff') / 257));
                byteBuffer2.put((byte)((var0[var4++] & '\uffff') / 257));
                byteBuffer2.put((byte)((var0[var4++] & '\uffff') / 257));
                byteBuffer2.put((byte)((int)((float)((byte)((var0[var4++] & '\uffff') / 257) & 255) * var1 + 0.5F)));
            }
        } else {
            byte var5 = 0;

            while(var5 < var0.length) {
                byteBuffer2.put((byte)((var0[var5] & '\uffff') / 257));
                byteBuffer2.put((byte)((var0[var5] & '\uffff') / 257));
                byteBuffer2.put((byte)((var0[var5] & '\uffff') / 257));
                byteBuffer2.put((byte)((int)(255.0F * var1 + 0.5F)));
            }
        }

        byteBuffer2.position(byteBuffer2.capacity() - var0.length);
        return byteBuffer2;
    }

    public static IntBuffer getIntBuffer(int[] var0) {
        if(intBuffer == null || intBuffer.capacity() < var0.length) {
            intBuffer = BufferUtils.createIntBuffer(var0.length);
        }

        intBuffer.position(intBuffer.capacity() - var0.length);
        intBuffer.put(var0);
        intBuffer.position(intBuffer.capacity() - var0.length);
        return intBuffer;
    }

    public static IntBuffer getIntBuffer(short[] var0) {
        if(intBuffer2 == null || intBuffer2.capacity() < var0.length) {
            intBuffer2 = BufferUtils.createIntBuffer(var0.length);
        }

        intBuffer2.position(intBuffer2.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            intBuffer2.put(var0[var1++]);
        }

        intBuffer2.position(intBuffer2.capacity() - var0.length);
        return intBuffer2;
    }

    public static IntBuffer getIntBuffer(byte[] var0) {
        if(intBuffer2 == null || intBuffer2.capacity() < var0.length) {
            intBuffer2 = BufferUtils.createIntBuffer(var0.length);
        }

        intBuffer2.position(intBuffer2.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            intBuffer2.put(var0[var1++]);
        }

        intBuffer2.position(intBuffer2.capacity() - var0.length);
        return intBuffer2;
    }

    public static FloatBuffer getFloatBuffer(float[] var0) {
        if(floatBuffer == null || floatBuffer.capacity() < var0.length) {
            floatBuffer = BufferUtils.createFloatBuffer(var0.length);
        }

        floatBuffer.position(floatBuffer.capacity() - var0.length);
        floatBuffer.put(var0);
        floatBuffer.position(floatBuffer.capacity() - var0.length);
        return floatBuffer;
    }

    public static FloatBuffer getFloatBuffer(short[] var0) {
        if (floatBuffer2 == null || floatBuffer2.capacity() < var0.length) {
            floatBuffer2 = BufferUtils.createFloatBuffer(var0.length);
        }

        floatBuffer2.position(floatBuffer2.capacity() - var0.length);

        for(int var1 = 0; var1 < var0.length; ++var1) {
            floatBuffer2.put((float)var0[var1]);
        }

        floatBuffer2.position(floatBuffer2.capacity() - var0.length);
        return floatBuffer2;
    }

    public static FloatBuffer getFloatBuffer(byte[] var0) {
        if (floatBuffer2 == null || floatBuffer2.capacity() < var0.length) {
            floatBuffer2 = BufferUtils.createFloatBuffer(var0.length);
        }

        floatBuffer2.position(floatBuffer2.capacity() - var0.length);

        for(int var1 = 0; var1 < var0.length; ++var1) {
            floatBuffer2.put((float)var0[var1]);
        }

        floatBuffer2.position(floatBuffer2.capacity() - var0.length);
        return floatBuffer2;
    }

    public static ShortBuffer getShortBuffer(short[] var0) {
        if(shortBuffer == null || shortBuffer.capacity() < var0.length) {
            shortBuffer = BufferUtils.createShortBuffer(var0.length);
        }

        shortBuffer.position(shortBuffer.capacity() - var0.length);
        shortBuffer.put(var0);
        shortBuffer.position(shortBuffer.capacity() - var0.length);
        return shortBuffer;
    }
}
