package emulator.graphics3D.lwjgl;

import java.nio.*;

import org.lwjgl.BufferUtils;

public final class LWJGLUtility {
    private static ByteBuffer byteBuffer1;
    private static ByteBuffer byteBuffer2;
    private static ShortBuffer shortBuffer1;
    private static ShortBuffer shortBuffer2;
    private static ByteBuffer byteBuffer3;
    private static ShortBuffer shortBuffer3;
    private static IntBuffer intBuffer;
    private static IntBuffer intBufferShort;
    private static IntBuffer intBufferByte;
    private static IntBuffer intBufferByte2;
    private static FloatBuffer floatBuffer;
    private static ByteBuffer byteBufferShort;

    public static ByteBuffer getByteBuffer1(byte[] var0) {
        if(byteBuffer1 == null || byteBuffer1.capacity() < var0.length) {
            byteBuffer1 = BufferUtils.createByteBuffer(var0.length);
        }

        byteBuffer1.position(byteBuffer1.capacity() - var0.length);
        byteBuffer1.put(var0);
        byteBuffer1.position(byteBuffer1.capacity() - var0.length);
        return byteBuffer1;
    }

    public static ByteBuffer getByteBuffer2(byte[] var0) {
        if(byteBuffer2 == null || byteBuffer2.capacity() < var0.length) {
            byteBuffer2 = BufferUtils.createByteBuffer(var0.length);
        }

        byteBuffer2.position(byteBuffer2.capacity() - var0.length);
        byteBuffer2.put(var0);
        byteBuffer2.position(byteBuffer2.capacity() - var0.length);
        return byteBuffer2;
    }

    public static ShortBuffer getShortBuffer1(short[] var0) {
        if(shortBuffer1 == null || shortBuffer1.capacity() < var0.length) {
            shortBuffer1 = BufferUtils.createShortBuffer(var0.length);
        }

        shortBuffer1.position(shortBuffer1.capacity() - var0.length);
        shortBuffer1.put(var0);
        shortBuffer1.position(shortBuffer1.capacity() - var0.length);
        return shortBuffer1;
    }

    public static ShortBuffer getShortBuffer2(short[] var0) {
        if(shortBuffer2 == null || shortBuffer2.capacity() < var0.length) {
            shortBuffer2 = BufferUtils.createShortBuffer(var0.length);
        }

        shortBuffer2.position(shortBuffer2.capacity() - var0.length);
        shortBuffer2.put(var0);
        shortBuffer2.position(shortBuffer2.capacity() - var0.length);
        return shortBuffer2;
    }

    public static ByteBuffer getByteBuffer(byte[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(byteBuffer3 == null || byteBuffer3.capacity() < var3) {
            byteBuffer3 = BufferUtils.createByteBuffer(var3);
        }

        byteBuffer3.position(byteBuffer3.capacity() - var3);
        if(var1 == 1.0F) {
            byteBuffer3.put(var0);
        } else {
            int var4;
            if(var0.length == var3) {
                var4 = 0;

                while(var4 < var3) {
                    byteBuffer3.put(var0[var4++]);
                    byteBuffer3.put(var0[var4++]);
                    byteBuffer3.put(var0[var4++]);
                    byteBuffer3.put((byte)((int)((float)(var0[var4++] & 255) * var1 + 0.5F)));
                }
            } else {
                var4 = 0;

                while(var4 < var0.length) {
                    byteBuffer3.put(var0[var4++]);
                    byteBuffer3.put(var0[var4++]);
                    byteBuffer3.put(var0[var4++]);
                    byteBuffer3.put((byte)((int)(255.0F * var1 + 0.5F)));
                }
            }
        }

        byteBuffer3.position(byteBuffer3.capacity() - var3);
        return byteBuffer3;
    }

    public static ShortBuffer getShortBuffer(short[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(shortBuffer3 == null || shortBuffer3.capacity() < var3) {
            shortBuffer3 = BufferUtils.createShortBuffer(var3);
        }

        shortBuffer3.position(shortBuffer3.capacity() - var3);
        int var4;
        if(var1 == 1.0F) {
            for(var4 = 0; var4 < var0.length; ++var4) {
                shortBuffer3.put(var0);
            }
        } else if(var0.length == var3) {
            var4 = 0;

            while(var4 < var3) {
                shortBuffer3.put(var0[var4++]);
                shortBuffer3.put(var0[var4++]);
                shortBuffer3.put(var0[var4++]);
                shortBuffer3.put((short)((int)((float)(var0[var4++] & '\uffff') * var1 + 0.5F)));
            }
        } else {
            var4 = 0;

            while(var4 < var0.length) {
                shortBuffer3.put(var0[var4++]);
                shortBuffer3.put(var0[var4++]);
                shortBuffer3.put(var0[var4++]);
                shortBuffer3.put((short)((int)(65535.0F * var1 + 0.5F)));
            }
        }

        shortBuffer3.position(shortBuffer3.capacity() - var3);
        return shortBuffer3;
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
        if(intBufferShort == null || intBufferShort.capacity() < var0.length) {
            intBufferShort = BufferUtils.createIntBuffer(var0.length);
        }

        intBufferShort.position(intBufferShort.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            intBufferShort.put(var0[var1++]);
        }

        intBufferShort.position(intBufferShort.capacity() - var0.length);
        return intBufferShort;
    }

    public static IntBuffer getIntBuffer(byte[] var0) {
        if(intBufferByte == null || intBufferByte.capacity() < var0.length) {
            intBufferByte = BufferUtils.createIntBuffer(var0.length);
        }

        intBufferByte.position(intBufferByte.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            intBufferByte.put(var0[var1++]);
        }

        intBufferByte.position(intBufferByte.capacity() - var0.length);
        return intBufferByte;
    }

    public static IntBuffer getIntBuffer2(byte[] var0) {
        if(intBufferByte2 == null || intBufferByte2.capacity() < var0.length) {
            intBufferByte2 = BufferUtils.createIntBuffer(var0.length);
        }

        intBufferByte2.position(intBufferByte2.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            intBufferByte2.put(var0[var1++]);
        }

        intBufferByte2.position(intBufferByte2.capacity() - var0.length);
        return intBufferByte2;
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

    public static ByteBuffer getByteBuffer(short[] var0) {
        if (byteBufferShort == null || byteBufferShort.capacity() < var0.length) {
            byteBufferShort = BufferUtils.createByteBuffer(var0.length);
        }

        byteBufferShort.position(byteBufferShort.capacity() - var0.length);

        for(int var1 = 0; var1 < var0.length; ++var1) {
            byteBufferShort.put((byte)(var0[var1] / 257));
        }

        byteBufferShort.position(byteBufferShort.capacity() - var0.length);
        return byteBufferShort;
    }

    public static ByteBuffer getByteBuffer(short[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F ? var0.length : 4 * var2;
        if (byteBuffer3 == null || byteBuffer3.capacity() < var3) {
            byteBuffer3 = BufferUtils.createByteBuffer(var3);
        }

        byteBuffer3.position(byteBuffer3.capacity() - var0.length);
        int var4;
        if (var1 == 1.0F) {
            for(var4 = 0; var4 < var0.length; ++var4) {
                byteBuffer3.put((byte)((var0[var4] & '\uffff') / 257));
            }
        } else if (var0.length == var3) {
            var4 = 0;

            while(var4 < var3) {
                byteBuffer3.put((byte)((var0[var4++] & '\uffff') / 257));
                byteBuffer3.put((byte)((var0[var4++] & '\uffff') / 257));
                byteBuffer3.put((byte)((var0[var4++] & '\uffff') / 257));
                byteBuffer3.put((byte)((int)((float)((byte)((var0[var4++] & '\uffff') / 257) & 255) * var1 + 0.5F)));
            }
        } else {
            byte var5 = 0;

            while(var5 < var0.length) {
                byteBuffer3.put((byte)((var0[var5] & '\uffff') / 257));
                byteBuffer3.put((byte)((var0[var5] & '\uffff') / 257));
                byteBuffer3.put((byte)((var0[var5] & '\uffff') / 257));
                byteBuffer3.put((byte)((int)(255.0F * var1 + 0.5F)));
            }
        }

        byteBuffer3.position(byteBuffer3.capacity() - var0.length);
        return byteBuffer3;
    }
}