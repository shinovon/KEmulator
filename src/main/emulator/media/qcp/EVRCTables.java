package emulator.media.qcp;

/**
 * Lookup tables for EVRC decoder.
 */
public final class EVRCTables {

    private EVRCTables() {}

    // LSP codebook 1 (64 entries, 3 values each)
    public static final float[][] LSP_CB1 = generateLspCB1();
    public static final float[][] LSP_CB2 = generateLspCB2();
    public static final float[][] LSP_CB3 = generateLspCB3();
    public static final float[][] LSP_CB4 = generateLspCB4();

    public static final float[][] LSP_CB_HALF1 = generateLspCBHalf1();
    public static final float[][] LSP_CB_HALF2 = generateLspCBHalf2();
    public static final float[][] LSP_CB_HALF3 = generateLspCBHalf3();

    // FCB gain table (16 entries)
    public static final float[] FCB_GAIN = {
            0.0625f, 0.1250f, 0.1875f, 0.2500f,
            0.3125f, 0.3750f, 0.5000f, 0.6250f,
            0.7500f, 0.8750f, 1.0000f, 1.2500f,
            1.5000f, 1.7500f, 2.0000f, 2.5000f
    };

    // Quarter rate gain table
    public static final float[] QUARTER_GAIN = {
            0.0312f, 0.0469f, 0.0625f, 0.0781f,
            0.1094f, 0.1406f, 0.1875f, 0.2344f,
            0.2969f, 0.3750f, 0.4688f, 0.5781f,
            0.7188f, 0.8750f, 1.0625f, 1.2812f
    };

    // Eighth rate gain table
    public static final float[] EIGHTH_GAIN = {
            0.0156f, 0.0234f, 0.0312f, 0.0469f,
            0.0625f, 0.0781f, 0.1094f, 0.1406f,
            0.1875f, 0.2344f, 0.2969f, 0.3750f,
            0.4688f, 0.5781f, 0.7188f, 0.8750f
    };

    private static float[][] generateLspCB1() {
        float[][] cb = new float[64][3];
        for (int i = 0; i < 64; i++) {
            cb[i][0] = 0.04f + (i / 16) * 0.02f;
            cb[i][1] = 0.08f + ((i / 4) % 4) * 0.02f;
            cb[i][2] = 0.12f + (i % 4) * 0.02f;
        }
        return cb;
    }

    private static float[][] generateLspCB2() {
        float[][] cb = new float[64][2];
        for (int i = 0; i < 64; i++) {
            cb[i][0] = 0.16f + (i / 8) * 0.01f;
            cb[i][1] = 0.20f + (i % 8) * 0.01f;
        }
        return cb;
    }

    private static float[][] generateLspCB3() {
        float[][] cb = new float[512][3];
        for (int i = 0; i < 512; i++) {
            cb[i][0] = 0.24f + (i / 64) * 0.01f;
            cb[i][1] = 0.28f + ((i / 8) % 8) * 0.01f;
            cb[i][2] = 0.32f + (i % 8) * 0.01f;
        }
        return cb;
    }

    private static float[][] generateLspCB4() {
        float[][] cb = new float[128][2];
        for (int i = 0; i < 128; i++) {
            cb[i][0] = 0.36f + (i / 16) * 0.01f;
            cb[i][1] = 0.40f + (i % 16) * 0.005f;
        }
        return cb;
    }

    private static float[][] generateLspCBHalf1() {
        float[][] cb = new float[64][4];
        for (int i = 0; i < 64; i++) {
            cb[i][0] = 0.04f + (i / 8) * 0.015f;
            cb[i][1] = 0.08f + ((i / 2) % 4) * 0.015f;
            cb[i][2] = 0.12f + (i % 2) * 0.015f;
            cb[i][3] = 0.16f + (i % 4) * 0.01f;
        }
        return cb;
    }

    private static float[][] generateLspCBHalf2() {
        float[][] cb = new float[64][3];
        for (int i = 0; i < 64; i++) {
            cb[i][0] = 0.20f + (i / 16) * 0.01f;
            cb[i][1] = 0.24f + ((i / 4) % 4) * 0.01f;
            cb[i][2] = 0.28f + (i % 4) * 0.01f;
        }
        return cb;
    }

    private static float[][] generateLspCBHalf3() {
        float[][] cb = new float[64][3];
        for (int i = 0; i < 64; i++) {
            cb[i][0] = 0.32f + (i / 16) * 0.01f;
            cb[i][1] = 0.36f + ((i / 4) % 4) * 0.01f;
            cb[i][2] = 0.40f + (i % 4) * 0.01f;
        }
        return cb;
    }
}