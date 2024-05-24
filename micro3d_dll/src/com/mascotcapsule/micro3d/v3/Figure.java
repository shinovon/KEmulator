/*
 * Copyright (c) 2020. Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.mascotcapsule.micro3d.v3;

import java.io.IOException;

import ru.woesss.micro3d.PlatformHelper;

public class Figure {
    private int nPointer;
    private Texture mTexture;
    private Texture[] mTexArray;

    public Figure(byte[] b) {
        if (b == null) {
            throw new NullPointerException("Argument is null");
        }
        this.nPointer = nInit(b);
        if (this.nPointer == 0) {
            throw new RuntimeException();
        }
    }

    public Figure(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("Argument is null");
        }
        byte[] b = PlatformHelper.getResourceBytes(name);
        if (b == null) {
            throw new IOException("Resource \"" + name + "\" not found");
        }
        this.nPointer = nInit(b);
        if (this.nPointer == 0) {
            throw new RuntimeException();
        }
    }

    public final int getNumPattern() {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        return nGetNumPatterns();
    }

    public final int getNumTextures() {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        return this.mTexArray == null ? 0 : this.mTexArray.length;
    }

    public final Texture getTexture() {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        return this.mTexture;
    }

    public final void selectTexture(int idx) {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        if ((this.mTexArray == null) || (idx < 0) || (idx >= this.mTexArray.length)) {
            throw new IllegalArgumentException();
        }
        Texture tex = this.mTexArray[idx];
        if (tex.nPointer == 0) {
            throw new RuntimeException("Texture is disposed");
        }
        this.mTexture = tex;
    }

    public final void setPattern(int idx) {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        nSetPattern(idx);
    }

    public final void setPosture(ActionTable act, int action, int frame) {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        if (act == null) {
            throw new NullPointerException("Argument ActionTable is null");
        }
        if ((action < 0) || (action >= act.getNumActions())) {
            throw new IllegalArgumentException("Illegal action index: " + action);
        }
        nSetPosture(act, action, frame);
    }

    public final void setTexture(Texture t) {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        if (t == null) {
            throw new NullPointerException("Argument is null");
        }
        if (t.nPointer == 0) {
            throw new RuntimeException("Texture is disposed");
        }
        if (!t.isForModel) {
            throw new IllegalArgumentException("Texture not for model");
        }
        this.mTexArray = new Texture[1];
        this.mTexArray[0] = t;
        this.mTexture = t;
    }

    public final void setTexture(Texture[] t) {
        if (this.nPointer == 0) {
            throw new RuntimeException("Figure is disposed");
        }
        if (t == null) {
            throw new NullPointerException("Argument is null");
        }
        if (t.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        for (int i = 0; i < t.length; i++) {
            if (t[i] == null) {
                throw new NullPointerException("Null element at index=" + i);
            }
            if (t[i].nPointer == 0) {
                throw new RuntimeException("Disposed texture at index=" + i);
            }
            if (!t[i].isForModel) {
                throw new IllegalArgumentException("Texture not for model at index=" + i);
            }
        }
        this.mTexArray = t;
        this.mTexture = null;
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

    private native int nInit(byte[] paramArrayOfByte);

    private native void nSetPosture(ActionTable paramActionTable, int paramInt1, int paramInt2);

    private native void nSetPattern(int paramInt);

    private native int nGetNumPatterns();

    private native void nFinalize();

    private static native void nInitClass();
}
