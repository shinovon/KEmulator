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
