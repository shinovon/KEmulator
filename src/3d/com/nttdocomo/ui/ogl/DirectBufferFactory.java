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
        return com.mexa.opgl.ByteBuffer.allocateDirect(paramInt);
    }

    public ByteBuffer allocateByteBuffer(byte[] paramArrayOfByte) {
        return null;
    }

    public ByteBuffer allocateByteBuffer(ByteBuffer paramByteBuffer) {
        return com.mexa.opgl.ByteBuffer.allocateDirect((com.mexa.opgl.ByteBuffer) paramByteBuffer);
    }

    public ShortBuffer allocateShortBuffer(int paramInt) {
        return com.mexa.opgl.ShortBuffer.allocateDirect(paramInt);
    }

    public ShortBuffer allocateShortBuffer(byte[] paramArrayOfByte) {
        com.mexa.opgl.ByteBuffer b = com.mexa.opgl.ByteBuffer.allocateDirect(paramArrayOfByte.length);
        b.put(0, paramArrayOfByte);
        return b.asShortBuffer();
    }

    public ShortBuffer allocateShortBuffer(ShortBuffer paramShortBuffer) {
        return com.mexa.opgl.ShortBuffer.allocateDirect((com.mexa.opgl.ShortBuffer) paramShortBuffer);
    }

    public IntBuffer allocateIntBuffer(int paramInt) {
        return com.mexa.opgl.IntBuffer.allocateDirect(paramInt);
    }

    public IntBuffer allocateIntBuffer(byte[] paramArrayOfByte) {
        com.mexa.opgl.ByteBuffer b = com.mexa.opgl.ByteBuffer.allocateDirect(paramArrayOfByte.length);
        b.put(0, paramArrayOfByte);
        return b.asIntBuffer();
    }

    public IntBuffer allocateIntBuffer(IntBuffer paramIntBuffer) {
        return com.mexa.opgl.IntBuffer.allocateDirect((com.mexa.opgl.IntBuffer) paramIntBuffer);
    }

    public FloatBuffer allocateFloatBuffer(int paramInt) {
        return com.mexa.opgl.FloatBuffer.allocateDirect(paramInt);
    }

    public FloatBuffer allocateFloatBuffer(byte[] paramArrayOfByte) {
        com.mexa.opgl.ByteBuffer b = com.mexa.opgl.ByteBuffer.allocateDirect(paramArrayOfByte.length);
        b.put(0, paramArrayOfByte);
        return b.asFloatBuffer();
    }

    public FloatBuffer allocateFloatBuffer(FloatBuffer paramFloatBuffer) {
        return com.mexa.opgl.FloatBuffer.allocateDirect((com.mexa.opgl.FloatBuffer) paramFloatBuffer);
    }
}
