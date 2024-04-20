package emulator.graphics3D.lwjgl;

import java.nio.*;

import org.lwjgl.BufferUtils;

public final class LWJGLUtility {
    private static ByteBuffer normalByteBuffer, normalShortBuffer;
    private static ByteBuffer colorBuffer;
    private static ByteBuffer imageBuffer;
    private static IntBuffer vertexByteBuffer;
    private static ShortBuffer vertexShortBuffer;
    private static IntBuffer texCoordsByteBuffer, texCoordsShortBuffer;
    private static IntBuffer elementsBuffer;
    private static FloatBuffer floatBuffer;

    public static ByteBuffer getNormalBuffer(byte[] var0) {
        if(normalByteBuffer == null || normalByteBuffer.capacity() < var0.length) {
            normalByteBuffer = BufferUtils.createByteBuffer(var0.length);
        }

        normalByteBuffer.position(normalByteBuffer.capacity() - var0.length);
        normalByteBuffer.put(var0);
        normalByteBuffer.position(normalByteBuffer.capacity() - var0.length);
        return normalByteBuffer;
    }

    public static ByteBuffer getNormalBuffer(short[] var0) {
        if (normalShortBuffer == null || normalShortBuffer.capacity() < var0.length) {
            normalShortBuffer = BufferUtils.createByteBuffer(var0.length);
        }

        normalShortBuffer.position(normalShortBuffer.capacity() - var0.length);

        for(int var1 = 0; var1 < var0.length; ++var1) {
            normalShortBuffer.put((byte)(var0[var1] / 257));
        }

        normalShortBuffer.position(normalShortBuffer.capacity() - var0.length);
        return normalShortBuffer;
    }

    public static ByteBuffer getImageBuffer(byte[] var0) {
        if(imageBuffer == null || imageBuffer.capacity() < var0.length) {
            imageBuffer = BufferUtils.createByteBuffer(var0.length);
        }

        imageBuffer.position(imageBuffer.capacity() - var0.length);
        imageBuffer.put(var0);
        imageBuffer.position(imageBuffer.capacity() - var0.length);
        return imageBuffer;
    }

    public static IntBuffer getVertexBuffer(byte[] var0) {
        if(vertexByteBuffer == null || vertexByteBuffer.capacity() < var0.length) {
            vertexByteBuffer = BufferUtils.createIntBuffer(var0.length);
        }

        vertexByteBuffer.position(vertexByteBuffer.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            vertexByteBuffer.put(var0[var1++]);
        }

        vertexByteBuffer.position(vertexByteBuffer.capacity() - var0.length);
        return vertexByteBuffer;
    }

    public static ShortBuffer getVertexBuffer(short[] var0) {
        if(vertexShortBuffer == null || vertexShortBuffer.capacity() < var0.length) {
            vertexShortBuffer = BufferUtils.createShortBuffer(var0.length);
        }

        vertexShortBuffer.position(vertexShortBuffer.capacity() - var0.length);
        vertexShortBuffer.put(var0);
        vertexShortBuffer.position(vertexShortBuffer.capacity() - var0.length);
        return vertexShortBuffer;
    }

    public static ByteBuffer getColorBuffer(byte[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F?var0.length:4 * var2;
        if(colorBuffer == null || colorBuffer.capacity() < var3) {
            colorBuffer = BufferUtils.createByteBuffer(var3);
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

    public static ByteBuffer getColorBuffer(short[] var0, float var1, int var2) {
        int var3 = var1 == 1.0F ? var0.length : 4 * var2;
        if (colorBuffer == null || colorBuffer.capacity() < var3) {
            colorBuffer = BufferUtils.createByteBuffer(var3);
        }

        colorBuffer.position(colorBuffer.capacity() - var0.length);
        int var4;
        if (var1 == 1.0F) {
            for(var4 = 0; var4 < var0.length; ++var4) {
                colorBuffer.put((byte)((var0[var4] & '\uffff') / 257));
            }
        } else if (var0.length == var3) {
            var4 = 0;

            while(var4 < var3) {
                colorBuffer.put((byte)((var0[var4++] & '\uffff') / 257));
                colorBuffer.put((byte)((var0[var4++] & '\uffff') / 257));
                colorBuffer.put((byte)((var0[var4++] & '\uffff') / 257));
                colorBuffer.put((byte)((int)((float)((byte)((var0[var4++] & '\uffff') / 257) & 255) * var1 + 0.5F)));
            }
        } else {
            byte var5 = 0;

            while(var5 < var0.length) {
                colorBuffer.put((byte)((var0[var5] & '\uffff') / 257));
                colorBuffer.put((byte)((var0[var5] & '\uffff') / 257));
                colorBuffer.put((byte)((var0[var5] & '\uffff') / 257));
                colorBuffer.put((byte)((int)(255.0F * var1 + 0.5F)));
            }
        }

        colorBuffer.position(colorBuffer.capacity() - var0.length);
        return colorBuffer;
    }

    public static IntBuffer getElementsBuffer(int[] var0) {
        if(elementsBuffer == null || elementsBuffer.capacity() < var0.length) {
            elementsBuffer = BufferUtils.createIntBuffer(var0.length);
        }

        elementsBuffer.position(elementsBuffer.capacity() - var0.length);
        elementsBuffer.put(var0);
        elementsBuffer.position(elementsBuffer.capacity() - var0.length);
        return elementsBuffer;
    }

    public static IntBuffer getTexCoordsBuffer(short[] var0) {
        if(texCoordsShortBuffer == null || texCoordsShortBuffer.capacity() < var0.length) {
            texCoordsShortBuffer = BufferUtils.createIntBuffer(var0.length);
        }

        texCoordsShortBuffer.position(texCoordsShortBuffer.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            texCoordsShortBuffer.put(var0[var1++]);
        }

        texCoordsShortBuffer.position(texCoordsShortBuffer.capacity() - var0.length);
        return texCoordsShortBuffer;
    }

    public static IntBuffer getTexCoordsBuffer(byte[] var0) {
        if(texCoordsByteBuffer == null || texCoordsByteBuffer.capacity() < var0.length) {
            texCoordsByteBuffer = BufferUtils.createIntBuffer(var0.length);
        }

        texCoordsByteBuffer.position(texCoordsByteBuffer.capacity() - var0.length);
        int var1 = 0;
        int var2 = var0.length;

        while(var1 < var2) {
            texCoordsByteBuffer.put(var0[var1++]);
        }

        texCoordsByteBuffer.position(texCoordsByteBuffer.capacity() - var0.length);
        return texCoordsByteBuffer;
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
}