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

import emulator.Emulator;
import emulator.graphics2D.IImage;
import ru.woesss.micro3d.PlatformHelper;

public class Texture {
    int nPointer;
    boolean isForModel;

    public IImage debugImage;

    public Texture(byte[] b, boolean isForModel) {
        if (b == null) {
            throw new NullPointerException();
        }
        this.nPointer = nInit(b, isForModel);
        if (this.nPointer == 0) {
            throw new RuntimeException();
        }
        loadDebugBitmap(b);
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
        loadDebugBitmap(b);
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

    private void loadDebugBitmap(byte[] data) {
        try {
            int pos = 0;
            if ((data[pos++] & 0xff) != 'B' || (data[pos++] & 0xff) != 'M') {
                throw new RuntimeException("Not a BMP!");
            }
            pos += 10;

            int rasterOffset = data[pos++] & 0xff | (data[pos++] & 0xff) << 8
                    | (data[pos++] & 0xff) << 16 | data[pos++] << 24;
            int dibHeaderSize = data[pos++] & 0xff | (data[pos++] & 0xff) << 8
                    | (data[pos++] & 0xff) << 16 | data[pos++] << 24;

            int width;
            int height;
            boolean reversed;
            if (dibHeaderSize == 12) {
                width = data[pos++] & 0xff | (data[pos++] & 0xff) << 8;
                height = data[pos++] & 0xff | (data[pos++] & 0xff) << 8;
                pos += 2;
                int bpp = data[pos++] & 0xff | (data[pos++] & 0xff) << 8;
                if (bpp != 8) {
                    throw new RuntimeException("Unsupported BMP format: bpp = " + bpp);
                }
                reversed = true;
            } else if (dibHeaderSize == 40) {
                width = data[pos++] & 0xff | (data[pos++] & 0xff) << 8
                        | (data[pos++] & 0xff) << 16 | data[pos++] << 24;
                int h = data[pos++] & 0xff | (data[pos++] & 0xff) << 8
                        | (data[pos++] & 0xff) << 16 | data[pos++] << 24;
                if (h < 0) {
                    height = -h;
                    reversed = false;
                } else {
                    height = h;
                    reversed = true;
                }
                pos += 2;
                int bpp = data[pos++] & 0xff | (data[pos++] & 0xff) << 8;
                if (bpp != 8) {
                    throw new RuntimeException("Unsupported BMP format: bpp = " + bpp);
                }
                int compression = data[pos++] & 0xff | (data[pos++] & 0xff) << 8
                        | (data[pos++] & 0xff) << 16 | data[pos++] << 24;
                if (compression != 0) {
                    throw new RuntimeException("Unsupported BMP format: compression = " + compression);
                }
                pos += 20;
            } else {
                throw new RuntimeException("Unsupported BMP version = " + dibHeaderSize);
            }

            int paletteOffset = 14 + dibHeaderSize;

            IImage image = Emulator.getEmulator().newImage(width, height, true);
            int[] rgb = image.getData();
            int remainder = width % 4;
            int stride = remainder == 0 ? width : width + 4 - remainder;
            int n = 0;
            if (reversed) {
                for (int i = height - 1; i >= 0; i--) {
                    for (int j = rasterOffset + i * stride, s = j + width; j < s; j++) {
                        byte idx = data[j];
                        int p = (idx & 0xff) * 4 + paletteOffset;
                        byte b = data[p++];
                        byte g = data[p++];
                        byte r = data[p];
                        rgb[n++] = ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + (b & 0xFF) + ((idx == 0 ? 0 : 0xff) << 24);
                    }
                }
            } else {
                for (int i = 0; i < height; i++) {
                    for (int j = rasterOffset + i * stride, s = j + width; j < s; j++) {
                        byte idx = data[j];
                        int p = (idx & 0xff) * 4 + paletteOffset;
                        byte b = data[p++];
                        byte g = data[p++];
                        byte r = data[p];
                        rgb[n++] = ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + (b & 0xFF) + ((idx == 0 ? 0 : 0xff) << 24);
                    }
                }
            }
            debugImage = image;
        } catch (Exception ignored) {}
    }

    static {
        System.loadLibrary("java_micro3d_v3_32");
        nInitClass();
    }

    private native int nInit(byte[] paramArrayOfByte, boolean paramBoolean);

    private native void nFinalize();

    private static native void nInitClass();
}
