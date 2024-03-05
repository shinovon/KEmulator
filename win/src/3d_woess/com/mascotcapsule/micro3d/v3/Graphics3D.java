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

import javax.microedition.lcdui.Graphics;

import ru.woesss.micro3d.PlatformHelper;

public class Graphics3D {
    public static final int COMMAND_LIST_VERSION_1_0 = -33554431;
    public static final int COMMAND_END = Integer.MIN_VALUE;
    public static final int COMMAND_NOP = -2130706432;
    public static final int COMMAND_FLUSH = -2113929216;
    public static final int COMMAND_ATTRIBUTE = -2097152000;
    public static final int COMMAND_CLIP = -2080374784;
    public static final int COMMAND_CENTER = -2063597568;
    public static final int COMMAND_TEXTURE_INDEX = -2046820352;
    public static final int COMMAND_AFFINE_INDEX = -2030043136;
    public static final int COMMAND_PARALLEL_SCALE = -1879048192;
    public static final int COMMAND_PARALLEL_SIZE = -1862270976;
    public static final int COMMAND_PERSPECTIVE_FOV = -1845493760;
    public static final int COMMAND_PERSPECTIVE_WH = -1828716544;
    public static final int COMMAND_AMBIENT_LIGHT = -1610612736;
    public static final int COMMAND_DIRECTION_LIGHT = -1593835520;
    public static final int COMMAND_THRESHOLD = -1358954496;
    public static final int PRIMITVE_POINTS = 16777216;
    public static final int PRIMITVE_LINES = 33554432;
    public static final int PRIMITVE_TRIANGLES = 50331648;
    public static final int PRIMITVE_QUADS = 67108864;
    public static final int PRIMITVE_POINT_SPRITES = 83886080;
    public static final int POINT_SPRITE_LOCAL_SIZE = 0;
    public static final int POINT_SPRITE_PIXEL_SIZE = 1;
    public static final int POINT_SPRITE_PERSPECTIVE = 0;
    public static final int POINT_SPRITE_NO_PERS = 2;
    public static final int ENV_ATTR_LIGHTING = 1;
    public static final int ENV_ATTR_SPHERE_MAP = 2;
    public static final int ENV_ATTR_TOON_SHADING = 4;
    public static final int ENV_ATTR_SEMI_TRANSPARENT = 8;
    public static final int PATTR_LIGHTING = 1;
    public static final int PATTR_SPHERE_MAP = 2;
    public static final int PATTR_COLORKEY = 16;
    public static final int PATTR_BLEND_NORMAL = 0;
    public static final int PATTR_BLEND_HALF = 32;
    public static final int PATTR_BLEND_ADD = 64;
    public static final int PATTR_BLEND_SUB = 96;
    public static final int PDATA_NORMAL_NONE = 0;
    public static final int PDATA_NORMAL_PER_FACE = 512;
    public static final int PDATA_NORMAL_PER_VERTEX = 768;
    public static final int PDATA_COLOR_NONE = 0;
    public static final int PDATA_COLOR_PER_COMMAND = 1024;
    public static final int PDATA_COLOR_PER_FACE = 2048;
    public static final int PDATA_TEXURE_COORD_NONE = 0;
    public static final int PDATA_POINT_SPRITE_PARAMS_PER_CMD = 4096;
    public static final int PDATA_POINT_SPRITE_PARAMS_PER_FACE = 8192;
    public static final int PDATA_POINT_SPRITE_PARAMS_PER_VERTEX = 12288;
    public static final int PDATA_TEXURE_COORD = 12288;
    private int nPointer;
    private int nBufPointer;
    private boolean isBound = false;
    private Graphics mGraphics;
    private int[] mBuffer;
    private boolean captured;

    public Graphics3D() {
        this.nPointer = nInit();
        if (this.nPointer == 0) {
            throw new RuntimeException();
        }
    }

    public final synchronized void bind(Graphics graphics) {
        if (this.isBound) {
            throw new IllegalStateException("Target already bound");
        }
        this.mGraphics = graphics;
        int width = PlatformHelper.getWidth(graphics);
        int height = PlatformHelper.getHeight(graphics);
        int cx = graphics.getClipX() + graphics.getTranslateX();
        int cy = graphics.getClipY() + graphics.getTranslateY();
        int cw = graphics.getClipWidth();
        int ch = graphics.getClipHeight();
        if ((this.mBuffer == null) || (this.mBuffer.length != width * height)) {
            this.mBuffer = new int[width * height];
        }
        nBind(this.mBuffer, width, height, cx, cy, cw, ch);
        this.isBound = true;
        this.captured = false;
    }

    public final void dispose() {
        if (this.nPointer != 0) {
            nFinalize();
            this.nPointer = 0;
        }
    }

    public final synchronized void drawCommandList(Texture[] textures, int x, int y, FigureLayout layout,
                                                   Effect3D effect, int[] commandList) {
        if ((layout == null) || (effect == null)) {
            throw new NullPointerException();
        }
        if (textures != null) {
            for (int i = 0; i < textures.length; i++) {
                Texture tex = textures[i];
                if (tex == null) {
                    throw new NullPointerException();
                }
                if (tex.nPointer == 0) {
                    throw new RuntimeException("Already disposed");
                }
            }
        }
        if (commandList == null) {
            throw new NullPointerException();
        }
        int[] buffer = PlatformHelper.getBuffer(this.mGraphics);
        nCapture(buffer);
        int ret = nDrawCmd(textures, x, y, layout, effect, commandList);
        if (ret == -1) {
            throw new RuntimeException();
        }
        if (ret != 0) {
            throw new IllegalArgumentException();
        }
        nRender(buffer);
    }

    public final void drawCommandList(Texture texture, int x, int y, FigureLayout layout, Effect3D effect,
                                      int[] commandList) {
        Texture[] ta = null;
        if (texture != null) {
            ta = new Texture[]{texture};
        }
        drawCommandList(ta, x, y, layout, effect, commandList);
    }

    public final synchronized void drawFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect) {
        checkTargetIsValid();
        if ((figure == null) || (layout == null) || (effect == null)) {
            throw new NullPointerException();
        }
        if (this.nPointer == 0) {
            throw new RuntimeException("Already disposed");
        }
        int[] buffer = PlatformHelper.getBuffer(this.mGraphics);
        nCapture(buffer);
        int ret = nDrawFigure(figure, x, y, layout, effect);
        if (ret == -1) {
            throw new RuntimeException();
        }
        if (ret != 0) {
            throw new IllegalArgumentException();
        }
        nRender(buffer);
    }

    public final synchronized void flush() {
        checkTargetIsValid();

        int[] buffer = PlatformHelper.getBuffer(this.mGraphics);
        nCapture(buffer);
        int ret = nFlush();
        if (ret != 0) {
            throw new RuntimeException();
        }
        nRender(buffer);
    }

    public final synchronized void release(Graphics graphics) {
        if (!this.isBound) {
            return;
        }
        if (graphics != this.mGraphics) {
            throw new IllegalArgumentException("Unknown target");
        }
        nRelease(this.mBuffer);
        PlatformHelper.onReleased(graphics);
        this.mGraphics = null;
        this.isBound = false;
    }

    public final synchronized void renderFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect) {
        checkTargetIsValid();
        if ((figure == null) || (layout == null) || (effect == null)) {
            throw new NullPointerException();
        }
        if (this.nPointer == 0) {
            throw new RuntimeException("Already disposed");
        }
        int ret = nRenderFigure(figure, x, y, layout, effect);
        if (ret == -1) {
            throw new RuntimeException();
        }
        if (ret != 0) {
            throw new IllegalArgumentException();
        }
    }

    public final synchronized void renderPrimitives(Texture texture, int x, int y, FigureLayout layout, Effect3D effect,
                                                    int command, int numPrimitives, int[] vertexCoords, int[] normals, int[] textureCoords, int[] colors) {
        if ((layout == null) || (effect == null)) {
            throw new NullPointerException();
        }
        if ((vertexCoords == null) || (normals == null) || (textureCoords == null) || (colors == null)) {
            throw new NullPointerException();
        }
        if (command < 0) {
            throw new IllegalArgumentException();
        }
        if ((numPrimitives <= 0) || (numPrimitives > 255)) {
            throw new IllegalArgumentException();
        }
        if ((texture != null) && (texture.nPointer == 0)) {
            throw new RuntimeException("Already disposed");
        }
        int ret = nRenderPrimitive(texture, x, y, layout, effect, command
                | numPrimitives << 16, vertexCoords, normals, textureCoords, colors);
        if (ret == -1) {
            throw new RuntimeException();
        }
        if (ret != 0) {
            throw new IllegalArgumentException();
        }
    }

    private void checkTargetIsValid() {
        if (this.mGraphics == null) {
            throw new IllegalStateException("No target is bound");
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

    private native int nInit();

    private native void nBind(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4,
                              int paramInt5, int paramInt6);

    private native void nCapture(int[] paramArrayOfInt);

    private native void nRender(int[] paramArrayOfInt);

    private native int nDrawCmd(Texture[] paramArrayOfTexture, int paramInt1, int paramInt2,
                                FigureLayout paramFigureLayout, Effect3D paramEffect3D, int[] paramArrayOfInt);

    private native int nDrawFigure(Figure paramFigure, int paramInt1, int paramInt2, FigureLayout paramFigureLayout,
                                   Effect3D paramEffect3D);

    private native int nRenderPrimitive(Texture paramTexture, int paramInt1, int paramInt2,
                                        FigureLayout paramFigureLayout, Effect3D paramEffect3D, int paramInt3, int[] paramArrayOfInt1,
                                        int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4);

    private native int nRenderFigure(Figure paramFigure, int paramInt1, int paramInt2, FigureLayout paramFigureLayout,
                                     Effect3D paramEffect3D);

    private native int nFlush();

    private native void nRelease(int[] paramArrayOfInt);

    private native void nFinalize();

    private static native void nInitClass();
}
