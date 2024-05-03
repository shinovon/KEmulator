package emulator.graphics3D.lwjgl;

import java.nio.*;

import org.lwjgl.BufferUtils;

public final class LWJGLUtility {
    private ByteBuffer normalByteBuffer;
    private ShortBuffer normalShortBuffer;
    private ByteBuffer colorBuffer;
    private ShortBuffer vertexShortBuffer;
    private ShortBuffer[] texCoordsBuffer = new ShortBuffer[Emulator3D.NumTextureUnits];
    private IntBuffer elementsBuffer;

    private ByteBuffer imageBuffer;
    private FloatBuffer floatBuffer;

    public LWJGLUtility() {
        final int initVerticesCount = 1024 * 4;

        normalByteBuffer = BufferUtils.createByteBuffer(initVerticesCount * 3);
        normalShortBuffer = BufferUtils.createShortBuffer(initVerticesCount * 3);

        colorBuffer = BufferUtils.createByteBuffer(initVerticesCount * 4);

        vertexShortBuffer = BufferUtils.createShortBuffer(initVerticesCount * 3);

        for (int slot = 0; slot < Emulator3D.NumTextureUnits; slot++) {
            texCoordsBuffer[slot] = BufferUtils.createShortBuffer(initVerticesCount * 2);
        }

        final int initPolyCount = 1024;

        elementsBuffer = BufferUtils.createIntBuffer(initPolyCount * 3);

        final int initTextureSize = 512;

        imageBuffer = BufferUtils.createByteBuffer(initTextureSize * initTextureSize * 4);

        floatBuffer = BufferUtils.createFloatBuffer(16); //should be enough for matrices and stuff
    }

    public ByteBuffer getNormalBuffer(byte[] var0) {
        if(normalByteBuffer == null || normalByteBuffer.capacity() < var0.length) {
            normalByteBuffer = BufferUtils.createByteBuffer(var0.length * 4 / 3);
        }

        normalByteBuffer.position(normalByteBuffer.capacity() - var0.length);
        normalByteBuffer.put(var0);
        normalByteBuffer.position(normalByteBuffer.capacity() - var0.length);
        return normalByteBuffer;
    }

    public ShortBuffer getNormalBuffer(short[] var0) {
        if (normalShortBuffer == null || normalShortBuffer.capacity() < var0.length) {
            normalShortBuffer = BufferUtils.createShortBuffer(var0.length * 4 / 3);
        }

        normalShortBuffer.position(normalShortBuffer.capacity() - var0.length);

        for (short i : var0) {
            normalShortBuffer.put(i);
        }

        normalShortBuffer.position(normalShortBuffer.capacity() - var0.length);
        return normalShortBuffer;
    }

    public ByteBuffer getImageBuffer(byte[] var0) {
        if(imageBuffer == null || imageBuffer.capacity() < var0.length) {
            imageBuffer = BufferUtils.createByteBuffer(var0.length * 4 / 3);
        }

        imageBuffer.position(imageBuffer.capacity() - var0.length);
        imageBuffer.put(var0);
        imageBuffer.position(imageBuffer.capacity() - var0.length);
        return imageBuffer;
    }

    public ShortBuffer getVertexBuffer(byte[] var0) {
        if(vertexShortBuffer == null || vertexShortBuffer.capacity() < var0.length) {
            vertexShortBuffer = BufferUtils.createShortBuffer(var0.length * 4 / 3);
        }

        vertexShortBuffer.position(vertexShortBuffer.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            vertexShortBuffer.put(var0[var1++]);
        }

        vertexShortBuffer.position(vertexShortBuffer.capacity() - var0.length);
        return vertexShortBuffer;
    }

    public ShortBuffer getVertexBuffer(short[] var0) {
        if(vertexShortBuffer == null || vertexShortBuffer.capacity() < var0.length) {
            vertexShortBuffer = BufferUtils.createShortBuffer(var0.length * 4 / 3);
        }

        vertexShortBuffer.position(vertexShortBuffer.capacity() - var0.length);
        vertexShortBuffer.put(var0);
        vertexShortBuffer.position(vertexShortBuffer.capacity() - var0.length);
        return vertexShortBuffer;
    }

    public ByteBuffer getColorBuffer(byte[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(colorBuffer == null || colorBuffer.capacity() < var3) {
            colorBuffer = BufferUtils.createByteBuffer(var3 * 4 / 3);
        }

        colorBuffer.position(colorBuffer.capacity() - var3);
        if(var1 == 1.0F) {
            colorBuffer.put(var0);
        } else {
            int var4;
            if(var0.length == var3) {
                var4 = 0;

                while(var4 < var3) {
                    colorBuffer.put(var0[var4++]);
                    colorBuffer.put(var0[var4++]);
                    colorBuffer.put(var0[var4++]);
                    colorBuffer.put((byte)((int)((float)(var0[var4++] & 255) * var1 + 0.5F)));
                }
            } else {
                var4 = 0;

                while(var4 < var0.length) {
                    colorBuffer.put(var0[var4++]);
                    colorBuffer.put(var0[var4++]);
                    colorBuffer.put(var0[var4++]);
                    colorBuffer.put((byte)((int)(255.0F * var1 + 0.5F)));
                }
            }
        }

        colorBuffer.position(colorBuffer.capacity() - var3);
        return colorBuffer;
    }

    public IntBuffer getElementsBuffer(int[] var0) {
        if(elementsBuffer == null || elementsBuffer.capacity() < var0.length) {
            elementsBuffer = BufferUtils.createIntBuffer(var0.length * 4 / 3);
        }

        elementsBuffer.position(elementsBuffer.capacity() - var0.length);
        elementsBuffer.put(var0);
        elementsBuffer.position(elementsBuffer.capacity() - var0.length);
        return elementsBuffer;
    }

    public ShortBuffer getTexCoordBuffer(short[] var0, int idx) {
        if(texCoordsBuffer[idx] == null || texCoordsBuffer[idx].capacity() < var0.length) {
            texCoordsBuffer[idx] = BufferUtils.createShortBuffer(var0.length * 4 / 3);
        }
        ShortBuffer buf = texCoordsBuffer[idx];

        buf.position(buf.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            buf.put(var0[var1++]);
        }

        buf.position(buf.capacity() - var0.length);
        return buf;
    }

    public ShortBuffer getTexCoordBuffer(byte[] var0, int idx) {
        if(texCoordsBuffer[idx] == null || texCoordsBuffer[idx].capacity() < var0.length) {
            texCoordsBuffer[idx] = BufferUtils.createShortBuffer(var0.length * 4 / 3);
        }
        ShortBuffer buf = texCoordsBuffer[idx];

        buf.position(buf.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            buf.put(var0[var1++]);
        }

        buf.position(buf.capacity() - var0.length);
        return buf;
    }

    public FloatBuffer getFloatBuffer(float[] var0) {
        if(floatBuffer == null || floatBuffer.capacity() < var0.length) {
            floatBuffer = BufferUtils.createFloatBuffer(var0.length * 4 / 3);
        }

        floatBuffer.position(floatBuffer.capacity() - var0.length);
        floatBuffer.put(var0);
        floatBuffer.position(floatBuffer.capacity() - var0.length);
        return floatBuffer;
    }
}