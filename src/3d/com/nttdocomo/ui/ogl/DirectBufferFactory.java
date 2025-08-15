package com.nttdocomo.ui.ogl;

public final class DirectBufferFactory {
    private static DirectBufferFactory instance;

    public static DirectBufferFactory getFactory() {
        if (instance != null) {
            return instance;
        }
        return instance = new DirectBufferFactory();
    }

    public ByteBuffer allocateByteBuffer(int paramInt) {
        return null;
    }

    public ByteBuffer allocateByteBuffer(byte[] paramArrayOfByte) {
        return null;
    }

    public ByteBuffer allocateByteBuffer(ByteBuffer paramByteBuffer) {
        return null;
    }

    public ShortBuffer allocateShortBuffer(int paramInt) {
        return null;
    }

    public ShortBuffer allocateShortBuffer(byte[] paramArrayOfByte) {
        return null;
    }

    public ShortBuffer allocateShortBuffer(ShortBuffer paramShortBuffer) {
        return null;
    }

    public IntBuffer allocateIntBuffer(int paramInt) {
        return null;
    }

    public IntBuffer allocateIntBuffer(byte[] paramArrayOfByte) {
        return null;
    }

    public IntBuffer allocateIntBuffer(IntBuffer paramIntBuffer) {
        return null;
    }

    public FloatBuffer allocateFloatBuffer(int paramInt) {
        return null;
    }

    public FloatBuffer allocateFloatBuffer(byte[] paramArrayOfByte) {
        return null;
    }

    public FloatBuffer allocateFloatBuffer(FloatBuffer paramFloatBuffer) {
        return null;
    }
}
