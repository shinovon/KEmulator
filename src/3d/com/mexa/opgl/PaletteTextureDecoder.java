package com.mexa.opgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class PaletteTextureDecoder {

    public static ByteBuffer decode(int format, ByteBuffer data, int width, int height) {
        int paletteSize;
        int bytesPerColor;

        switch (format) {
            case OpglGraphics.GL_PALETTE4_RGB8_OES:
                paletteSize = 16;
                bytesPerColor = 3;
                break;
            case OpglGraphics.GL_PALETTE4_RGBA8_OES:
                paletteSize = 16;
                bytesPerColor = 4;
                break;
            case OpglGraphics.GL_PALETTE4_R5_G6_B5_OES:
            case OpglGraphics.GL_PALETTE4_RGBA4_OES:
            case OpglGraphics.GL_PALETTE4_RGB5_A1_OES:
                paletteSize = 16;
                bytesPerColor = 2;
                break;
            case OpglGraphics.GL_PALETTE8_RGB8_OES:
                paletteSize = 256;
                bytesPerColor = 3;
                break;
            case OpglGraphics.GL_PALETTE8_RGBA8_OES:
                paletteSize = 256;
                bytesPerColor = 4;
                break;
            case OpglGraphics.GL_PALETTE8_R5_G6_B5_OES:
            case OpglGraphics.GL_PALETTE8_RGBA4_OES:
            case OpglGraphics.GL_PALETTE8_RGB5_A1_OES:
                paletteSize = 256;
                bytesPerColor = 2;
                break;
            default:
                throw new IllegalArgumentException("Unsupported palette format: 0x" + Integer.toHexString(format));
        }

        int paletteBytes = paletteSize * bytesPerColor;
        int[] palette = new int[paletteSize];
        ByteBuffer paletteBuffer = data.duplicate().order(data.order());
        paletteBuffer.limit(paletteBuffer.position() + paletteBytes);

        for (int i = 0; i < paletteSize; i++) {
            int color = 0;
            if (bytesPerColor == 2) {
                short colorShort = paletteBuffer.getShort();
                if (format == OpglGraphics.GL_PALETTE4_R5_G6_B5_OES || format == OpglGraphics.GL_PALETTE8_R5_G6_B5_OES) {
                    int r = (colorShort >> 11) & 0x1F;
                    int g = (colorShort >> 5) & 0x3F;
                    int b = colorShort & 0x1F;
                    r = (r << 3) | (r >> 2);
                    g = (g << 2) | (g >> 4);
                    b = (b << 3) | (b >> 2);
                    color = 0xFF000000 | (r << 16) | (g << 8) | b;
                } else if (format == OpglGraphics.GL_PALETTE4_RGBA4_OES || format == OpglGraphics.GL_PALETTE8_RGBA4_OES) {
                    int r = (colorShort >> 12) & 0xF;
                    int g = (colorShort >> 8) & 0xF;
                    int b = (colorShort >> 4) & 0xF;
                    int a = colorShort & 0xF;
                    r = (r << 4) | r;
                    g = (g << 4) | g;
                    b = (b << 4) | b;
                    a = (a << 4) | a;
                    color = (a << 24) | (r << 16) | (g << 8) | b;
                } else if (format == OpglGraphics.GL_PALETTE4_RGB5_A1_OES || format == OpglGraphics.GL_PALETTE8_RGB5_A1_OES) {
                    int r = (colorShort >> 11) & 0x1F;
                    int g = (colorShort >> 6) & 0x1F;
                    int b = (colorShort >> 1) & 0x1F;
                    int a = (colorShort & 1) * 0xFF;
                    r = (r << 3) | (r >> 2);
                    g = (g << 3) | (g >> 2);
                    b = (b << 3) | (b >> 2);
                    color = (a << 24) | (r << 16) | (g << 8) | b;
                }
            } else if (bytesPerColor == 3) {
                int r = paletteBuffer.get() & 0xFF;
                int g = paletteBuffer.get() & 0xFF;
                int b = paletteBuffer.get() & 0xFF;
                color = 0xFF000000 | (r << 16) | (g << 8) | b;
            } else if (bytesPerColor == 4) {
                int r = paletteBuffer.get() & 0xFF;
                int g = paletteBuffer.get() & 0xFF;
                int b = paletteBuffer.get() & 0xFF;
                int a = paletteBuffer.get() & 0xFF;
                color = (a << 24) | (r << 16) | (g << 8) | b;
            }
            palette[i] = color;
        }

        data.position(data.position() + paletteBytes);
        int indexCount = width * height;
        int bitsPerIndex = (paletteSize == 16) ? 4 : 8;
        int indexBytes = (indexCount * bitsPerIndex + 7) / 8;

        if (data.remaining() < indexBytes) {
            throw new IllegalArgumentException("Data buffer too small");
        }

        ByteBuffer rgbaBuffer = ByteBuffer.allocateDirect(indexCount * 4).order(ByteOrder.nativeOrder());
        ByteBuffer indexBuffer = data.slice().order(data.order());
        indexBuffer.limit(indexBytes);

        if (bitsPerIndex == 4) {
            for (int i = 0; i < indexCount; i++) {
                int index;
                if (i % 2 == 0) {
                    byte b = indexBuffer.get();
                    index = (b >> 4) & 0x0F;
                } else {
                    byte b = indexBuffer.get(indexBuffer.position() - 1);
                    index = b & 0x0F;
                }
                int color = palette[index];
                rgbaBuffer.put((byte) ((color >> 16) & 0xFF));
                rgbaBuffer.put((byte) ((color >> 8) & 0xFF));
                rgbaBuffer.put((byte) (color & 0xFF));
                rgbaBuffer.put((byte) ((color >> 24) & 0xFF));
            }
        } else {
            for (int i = 0; i < indexCount; i++) {
                int index = indexBuffer.get() & 0xFF;
                int color = palette[index];
                rgbaBuffer.put((byte) ((color >> 16) & 0xFF));
                rgbaBuffer.put((byte) ((color >> 8) & 0xFF));
                rgbaBuffer.put((byte) (color & 0xFF));
                rgbaBuffer.put((byte) ((color >> 24) & 0xFF));
            }
        }

        data.position(data.position() + indexBytes);
        rgbaBuffer.flip();
        return rgbaBuffer;
    }
}