package com.mascotcapsule.micro3d.v3;

import javax.microedition.lcdui.Graphics;

public class Graphics3D {
    public static boolean _STUB = true;

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
    public static final int PATTR_BLEND_NORMAL = 0;
    public static final int PATTR_LIGHTING = 1;
    public static final int PATTR_SPHERE_MAP = 2;
    public static final int PATTR_COLORKEY = 16;
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

    public final synchronized void bind(Graphics paramGraphics)
            throws IllegalStateException, NullPointerException {
    }

    public final synchronized void release(Graphics paramGraphics)
            throws IllegalArgumentException, NullPointerException {
    }

    public final void renderPrimitives(Texture paramTexture, int paramInt1, int paramInt2, FigureLayout paramFigureLayout, Effect3D paramEffect3D, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4) {
    }

    public final void drawCommandList(Texture[] paramArrayOfTexture, int paramInt1, int paramInt2, FigureLayout paramFigureLayout, Effect3D paramEffect3D, int[] paramArrayOfInt) {
    }

    public final void drawCommandList(Texture paramTexture, int paramInt1, int paramInt2, FigureLayout paramFigureLayout, Effect3D paramEffect3D, int[] paramArrayOfInt) {
        Texture[] arrayOfTexture = null;
        if (paramTexture != null) {
            (arrayOfTexture = new Texture[1])[0] = paramTexture;
        }
        drawCommandList(arrayOfTexture, paramInt1, paramInt2, paramFigureLayout, paramEffect3D, paramArrayOfInt);
    }

    public final void renderFigure(Figure paramFigure, int paramInt1, int paramInt2, FigureLayout paramFigureLayout, Effect3D paramEffect3D)
            throws IllegalStateException {
    }

    public final void drawFigure(Figure paramFigure, int paramInt1, int paramInt2, FigureLayout paramFigureLayout, Effect3D paramEffect3D)
            throws IllegalStateException {
    }

    public final void flush()
            throws IllegalStateException {
    }

    public final void dispose() {
    }
}
