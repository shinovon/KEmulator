package com.mascotcapsule.micro3d.v3;

import java.io.IOException;

import ru.woesss.micro3d.PlatformHelper;

public class Texture {
    int nPointer;
    boolean isForModel;

    public Texture(byte[] b, boolean isForModel) {
        if (b == null) {
            throw new NullPointerException();
        }
        this.nPointer = nInit(b, isForModel);
        if (this.nPointer == 0) {
            throw new RuntimeException();
        }
        this.isForModel = isForModel;
    }

    public Texture(String name, boolean isForModel) throws IOException {
        if (name == null) {
            throw new NullPointerException();
        }
        byte[] b = PlatformHelper.getResourceBytes(name);
        if (b == null) {
            throw new IOException("Resource \"" + name + "\" not found");
        }
        this.nPointer = nInit(b, isForModel);
        if (this.nPointer == 0) {
            throw new RuntimeException();
        }
        this.isForModel = isForModel;
    }

    public final void dispose() {
        if (this.nPointer != 0) {
            nFinalize();
            this.nPointer = 0;
        }
    }

    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    static {
        System.loadLibrary("java_micro3d_v3_32");
        nInitClass();
    }

    private native int nInit(byte[] paramArrayOfByte, boolean paramBoolean);

    private native void nFinalize();

    private static native void nInitClass();
}
