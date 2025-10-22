package emulator.ui.effect;

import java.util.Random;

/**
 * Implements a water ripple effect.
 * Manages two heightmap buffers for simulation and rendering.
 */
public final class WaterEffect {

    private int width; // anInt316
    private int height; // anInt320
    private final boolean useShading; // aBoolean317

    /** 0 = buffer1 is active, 1 = buffer2 is active. Used for swapping. */
    public int currentBufferIndex; // Recommended refactor name: currentBufferIndex

    /** Damping factor for the wave. */
    public int damping;

    private int[] buffer1; // anIntArray318
    private int[] buffer2; // anIntArray321

    private final Random random; // aRandom319

    public WaterEffect() {
        super();
        this.buffer1 = null;
        this.buffer2 = null;
        this.width = 0;
        this.height = 0;
        this.useShading = true;
        this.currentBufferIndex = 0;
        this.damping = 5;
        this.random = new Random();
    }

    /**
     * Initializes or resizes the simulation buffers.
     */
    public void initialize(final int width, final int height) {
        if (this.buffer1 != null) {
            this.buffer1 = null;
        }
        if (this.buffer2 != null) {
            this.buffer2 = null;
        }

        this.buffer1 = new int[width * height];
        this.buffer2 = new int[width * height];
        this.width = width;
        this.height = height;
        this.currentBufferIndex = 0;
    }

    /**
     * Processes one simulation step and renders the result.
     */
    public void processFrame(final int[] sourceImage, final int[] destImage) {
        if (!this.useShading) {
            this.renderRefraction(sourceImage, destImage);
        } else {
            this.renderRefractionWithShading(sourceImage, destImage);
        }
        // Update the water physics simulation
        this.updateSimulation(this.currentBufferIndex, this.damping);
        // Swap the active buffer
        this.currentBufferIndex ^= 1;
    }

    /**
     * Updates the water physics simulation using a simple wave equation approximation.
     * @param bufferIndex The source buffer index (0 or 1).
     * @param dampingFactor The factor to dampen the waves.
     */
    // This was method139
    private void updateSimulation(int bufferIndex, int dampingFactor) {
        int i = this.width + 1;
        int[] srcBuffer;
        int[] dstBuffer;

        if (bufferIndex == 0) {
            dstBuffer = this.buffer1;
            srcBuffer = this.buffer2;
        } else {
            dstBuffer = this.buffer2;
            srcBuffer = this.buffer1;
        }

        int endOffset = (this.height - 1) * this.width;
        while (i < endOffset) {
            int rowEndOffset = i + this.width - 2;
            while (i < rowEndOffset) {
                // Average the height of 8 neighbors
                int newHeight = (
                        srcBuffer[i + this.width] +
                                srcBuffer[i - this.width] +
                                srcBuffer[i + 1] +
                                srcBuffer[i - 1] +
                                srcBuffer[i - this.width - 1] +
                                srcBuffer[i - this.width + 1] +
                                srcBuffer[i + this.width - 1] +
                                srcBuffer[i + this.width + 1]
                                >> 2) - dstBuffer[i];

                // Apply damping and store new height
                dstBuffer[i] = newHeight - (newHeight >> dampingFactor);
                ++i;
            }
            i += 2;
        }
    }


    /**
     * Adds a "drop" (disturbance) to the simulation buffer.
     * @param x X coordinate
     * @param y Y coordinate
     * @param radius Radius of the drop
     * @param strength "Height" or strength of the drop
     * @param bufferIndex Which buffer to add to (0 or 1)
     */
    public void addDrop(int x, int y, final int radius, final int strength, final int bufferIndex) {
        final int[] buffer = (bufferIndex == 0) ? this.buffer1 : this.buffer2;
        final int radiusSq = radius * radius;

        // If coordinates are negative, pick a random spot
        if (x < 0) {
            x = 1 + radius + this.random.nextInt() % (this.width - 2 * radius - 1);
        }
        if (y < 0) {
            y = 1 + radius + this.random.nextInt() % (this.height - 2 * radius - 1);
        }

        // Clamp rendering area to buffer bounds
        int yStart = -radius;
        int yEnd = radius;
        int xStart = -radius;
        int xEnd = radius;

        if (x - radius < 1) {
            xStart -= x - radius - 1;
        }
        if (y - radius < 1) {
            yStart -= y - radius - 1;
        }
        if (x + radius > this.width - 1) {
            xEnd -= x + radius - this.width + 1;
        }
        if (y + radius > this.height - 1) {
            yEnd -= y + radius - this.height + 1;
        }

        // Draw the circular drop
        for (int iy = yStart; iy < yEnd; ++iy) {
            final int iySq = iy * iy;
            for (int ix = xStart; ix < xEnd; ++ix) {
                if (ix * ix + iySq < radiusSq) {
                    final int offset = this.width * (iy + y) + (ix + x);
                    buffer[offset] += strength;
                }
            }
        }
    }

    /**
     * Renders the refraction effect (no shading).
     */
    // This was method140
    private void renderRefraction(final int[] srcImage, final int[] dstImage) {
        int i = this.width + 1;
        final int[] heightMap = this.buffer1;
        int endOffset = (this.height - 1) * this.width;

        while (i < endOffset) {
            int rowEndOffset = i + this.width - 2;
            while (i < rowEndOffset) {
                int diffY = heightMap[i] - heightMap[i + this.width];
                int diffX = heightMap[i] - heightMap[i + 1];

                // Calculate pixel offset
                int offset = i + this.width * (diffY >> 3) + (diffX >> 3);
                dstImage[i] = srcImage[offset];
                ++i;

                // Manual loop unroll
                diffY = heightMap[i] - heightMap[i + this.width];
                diffX = heightMap[i] - heightMap[i + 1];
                offset = i + this.width * (diffY >> 3) + (diffX >> 3);
                dstImage[i] = srcImage[offset];
                ++i;
            }
            i += 2;
        }
    }

    /**
     * Renders the refraction effect with dynamic shading.
     */
    // This was method141
    private void renderRefractionWithShading(int[] srcImage, int[] dstImage) {
        int i = this.width + 1;
        int maxOffset = this.width * this.height;
        int[] heightMap = this.buffer1; // Use buffer1 for rendering
        int endOffset = (this.height - 1) * this.width;

        for (; i < endOffset; i += 2) {
            int rowEndOffset = i + this.width - 2;
            for (; i < rowEndOffset; ++i) {
                int diffX = heightMap[i] - heightMap[i + 1];
                int diffY = heightMap[i] - heightMap[i + this.width];

                int offset = i + this.width * (diffY >> 3) + (diffX >> 3);

                if (offset < maxOffset && offset > 0) {
                    // Apply shading to the pixel
                    int pixelColor = applyShading(srcImage[offset], diffX);
                    dstImage[i] = pixelColor;
                }

                // Manual loop unroll
                ++i;
                diffX = heightMap[i] - heightMap[i + 1];
                diffY = heightMap[i] - heightMap[i + this.width];
                offset = i + this.width * (diffY >> 3) + (diffX >> 3);

                if (offset < maxOffset && offset > 0) {
                    int pixelColor = applyShading(srcImage[offset], diffX);
                    dstImage[i] = pixelColor;
                }
            }
        }
    }

    /**
     * Applies a shading effect to a pixel color.
     * @param color The source ARGB color.
     * @param shade The shade value (height difference), subtracted from R, G, B.
     * @return The modified ARGB color.
     */
    // This was method138
    private static int applyShading(final int color, final int shade) {
        int r = (color >> 16 & 0xFF) - shade;
        int g = (color >> 8 & 0xFF) - shade;
        int b = (color & 0xFF) - shade;

        // Clamp values to 0-255, using Math.min as suggested
        r = (r < 0) ? 0 : Math.min(r, 255);
        g = (g < 0) ? 0 : Math.min(g, 255);
        b = (b < 0) ? 0 : Math.min(b, 255);

        // Re-assemble the color (Alpha is 255 / 0xFF)
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}