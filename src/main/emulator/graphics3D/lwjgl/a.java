package emulator.graphics3D.lwjgl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;

public final class a {
    private static ByteBuffer aByteBuffer770;
    private static ByteBuffer aByteBuffer774;
    private static ShortBuffer aShortBuffer771;
    private static ShortBuffer aShortBuffer775;
    private static ByteBuffer aByteBuffer777;
    private static ShortBuffer aShortBuffer778;
    private static IntBuffer anIntBuffer772;
    private static IntBuffer anIntBuffer776;
    private static IntBuffer anIntBuffer779;
    private static IntBuffer anIntBuffer780;
    private static FloatBuffer aFloatBuffer773;

    public static ByteBuffer method520(byte[] var0) {
        if(aByteBuffer770 == null || aByteBuffer770.capacity() < var0.length) {
            aByteBuffer770 = BufferUtils.createByteBuffer(var0.length);
        }

        aByteBuffer770.position(aByteBuffer770.capacity() - var0.length);
        aByteBuffer770.put(var0);
        aByteBuffer770.position(aByteBuffer770.capacity() - var0.length);
        return aByteBuffer770;
    }

    public static ByteBuffer method528(byte[] var0) {
        if(aByteBuffer774 == null || aByteBuffer774.capacity() < var0.length) {
            aByteBuffer774 = BufferUtils.createByteBuffer(var0.length);
        }

        aByteBuffer774.position(aByteBuffer774.capacity() - var0.length);
        aByteBuffer774.put(var0);
        aByteBuffer774.position(aByteBuffer774.capacity() - var0.length);
        return aByteBuffer774;
    }

    public static ShortBuffer method521(short[] var0) {
        if(aShortBuffer771 == null || aShortBuffer771.capacity() < var0.length) {
            aShortBuffer771 = BufferUtils.createShortBuffer(var0.length);
        }

        aShortBuffer771.position(aShortBuffer771.capacity() - var0.length);
        aShortBuffer771.put(var0);
        aShortBuffer771.position(aShortBuffer771.capacity() - var0.length);
        return aShortBuffer771;
    }

    public static ShortBuffer method529(short[] var0) {
        if(aShortBuffer775 == null || aShortBuffer775.capacity() < var0.length) {
            aShortBuffer775 = BufferUtils.createShortBuffer(var0.length);
        }

        aShortBuffer775.position(aShortBuffer775.capacity() - var0.length);
        aShortBuffer775.put(var0);
        aShortBuffer775.position(aShortBuffer775.capacity() - var0.length);
        return aShortBuffer775;
    }

    public static ByteBuffer method522(byte[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(aByteBuffer777 == null || aByteBuffer777.capacity() < var3) {
            aByteBuffer777 = BufferUtils.createByteBuffer(var3);
        }

        aByteBuffer777.position(aByteBuffer777.capacity() - var3);
        if(var1 == 1.0F) {
            aByteBuffer777.put(var0);
        } else {
            int var4;
            if(var0.length == var3) {
                var4 = 0;

                while(var4 < var3) {
                    aByteBuffer777.put(var0[var4++]);
                    aByteBuffer777.put(var0[var4++]);
                    aByteBuffer777.put(var0[var4++]);
                    aByteBuffer777.put((byte)((int)((float)(var0[var4++] & 255) * var1 + 0.5F)));
                }
            } else {
                var4 = 0;

                while(var4 < var0.length) {
                    aByteBuffer777.put(var0[var4++]);
                    aByteBuffer777.put(var0[var4++]);
                    aByteBuffer777.put(var0[var4++]);
                    aByteBuffer777.put((byte)((int)(255.0F * var1 + 0.5F)));
                }
            }
        }

        aByteBuffer777.position(aByteBuffer777.capacity() - var3);
        return aByteBuffer777;
    }

    public static ShortBuffer method523(short[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(aShortBuffer778 == null || aShortBuffer778.capacity() < var3) {
            aShortBuffer778 = BufferUtils.createShortBuffer(var3);
        }

        aShortBuffer778.position(aShortBuffer778.capacity() - var3);
        int var4;
        if(var1 == 1.0F) {
            for(var4 = 0; var4 < var0.length; ++var4) {
                aShortBuffer778.put(var0);
            }
        } else if(var0.length == var3) {
            var4 = 0;

            while(var4 < var3) {
                aShortBuffer778.put(var0[var4++]);
                aShortBuffer778.put(var0[var4++]);
                aShortBuffer778.put(var0[var4++]);
                aShortBuffer778.put((short)((int)((float)(var0[var4++] & '\uffff') * var1 + 0.5F)));
            }
        } else {
            var4 = 0;

            while(var4 < var0.length) {
                aShortBuffer778.put(var0[var4++]);
                aShortBuffer778.put(var0[var4++]);
                aShortBuffer778.put(var0[var4++]);
                aShortBuffer778.put((short)((int)(65535.0F * var1 + 0.5F)));
            }
        }

        aShortBuffer778.position(aShortBuffer778.capacity() - var3);
        return aShortBuffer778;
    }

    public static IntBuffer method524(int[] var0) {
        if(anIntBuffer772 == null || anIntBuffer772.capacity() < var0.length) {
            anIntBuffer772 = BufferUtils.createIntBuffer(var0.length);
        }

        anIntBuffer772.position(anIntBuffer772.capacity() - var0.length);
        anIntBuffer772.put(var0);
        anIntBuffer772.position(anIntBuffer772.capacity() - var0.length);
        return anIntBuffer772;
    }

    public static IntBuffer method525(short[] var0) {
        if(anIntBuffer776 == null || anIntBuffer776.capacity() < var0.length) {
            anIntBuffer776 = BufferUtils.createIntBuffer(var0.length);
        }

        anIntBuffer776.position(anIntBuffer776.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            anIntBuffer776.put(var0[var1++]);
        }

        anIntBuffer776.position(anIntBuffer776.capacity() - var0.length);
        return anIntBuffer776;
    }

    public static IntBuffer method526(byte[] var0) {
        if(anIntBuffer779 == null || anIntBuffer779.capacity() < var0.length) {
            anIntBuffer779 = BufferUtils.createIntBuffer(var0.length);
        }

        anIntBuffer779.position(anIntBuffer779.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            anIntBuffer779.put(var0[var1++]);
        }

        anIntBuffer779.position(anIntBuffer779.capacity() - var0.length);
        return anIntBuffer779;
    }

    public static IntBuffer method530(byte[] var0) {
        if(anIntBuffer780 == null || anIntBuffer780.capacity() < var0.length) {
            anIntBuffer780 = BufferUtils.createIntBuffer(var0.length);
        }

        anIntBuffer780.position(anIntBuffer780.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            anIntBuffer780.put(var0[var1++]);
        }

        anIntBuffer780.position(anIntBuffer780.capacity() - var0.length);
        return anIntBuffer780;
    }

    public static FloatBuffer method527(float[] var0) {
        if(aFloatBuffer773 == null || aFloatBuffer773.capacity() < var0.length) {
            aFloatBuffer773 = BufferUtils.createFloatBuffer(var0.length);
        }

        aFloatBuffer773.position(aFloatBuffer773.capacity() - var0.length);
        aFloatBuffer773.put(var0);
        aFloatBuffer773.position(aFloatBuffer773.capacity() - var0.length);
        return aFloatBuffer773;
    }
}
