package emulator.media.qcp;

/**
 * EVRC (Enhanced Variable Rate Codec) decoder implementation.
 * Based on 3GPP2 C.S0014-C
 */
public class EVRCDecoder implements FrameDecoder {

    // EVRC frame rates
    public static final int RATE_BLANK = 0;
    public static final int RATE_EIGHTH = 1;
    public static final int RATE_QUARTER = 2;
    public static final int RATE_HALF = 3;
    public static final int RATE_FULL = 4;

    // Frame sizes in bytes (excluding rate byte)
    private static final int[] FRAME_SIZES = {0, 2, 5, 10, 22};

    private static final int SAMPLES_PER_FRAME = 160;
    private static final int LPC_ORDER = 10;
    private static final int SUBFRAMES = 3;
    private static final int SUBFRAME_SIZES[] = {54, 54, 52};

    // Decoder state
    private float[] lspPrev = new float[LPC_ORDER];
    private float[] lpcPrev = new float[LPC_ORDER + 1];
    private float[] excitation = new float[SAMPLES_PER_FRAME + 200];
    private float[] synthBuffer = new float[LPC_ORDER];
    private int rndSeed = 21845;

    // Bit reader
    private byte[] bits;
    private int bitPos;

    public EVRCDecoder() {
        reset();
    }

    @Override
    public void reset() {
        for (int i = 0; i < LPC_ORDER; i++) {
            lspPrev[i] = (i + 1.0f) / (LPC_ORDER + 1) * 0.5f;
        }
        lspToLpc(lspPrev, lpcPrev);
        java.util.Arrays.fill(excitation, 0);
        java.util.Arrays.fill(synthBuffer, 0);
        rndSeed = 21845;
    }

    @Override
    public void decodeFrame(byte[] frameData, short[] output) {
        if (frameData == null || frameData.length < 1) {
            generateErasure(output);
            return;
        }

        int rate = frameData[0] & 0x0F;
        if (rate > RATE_FULL || rate == 0) {
            generateErasure(output);
            return;
        }

        if (frameData.length < FRAME_SIZES[rate] + 1) {
            generateErasure(output);
            return;
        }

        bits = frameData;
        bitPos = 8;

        switch (rate) {
            case RATE_FULL:
                decodeFullRate(output);
                break;
            case RATE_HALF:
                decodeHalfRate(output);
                break;
            case RATE_QUARTER:
                decodeQuarterRate(output);
                break;
            case RATE_EIGHTH:
                decodeEighthRate(output);
                break;
            default:
                generateErasure(output);
                break;
        }
    }

    private void decodeFullRate(short[] output) {
        // Full rate: 171 bits (22 bytes - some padding)
        // LSP: 28 bits total
        int lspIdx1 = readBits(6);
        int lspIdx2 = readBits(6);
        int lspIdx3 = readBits(9);
        int lspIdx4 = readBits(7);

        float[] lsp = decodeLsp(new int[]{lspIdx1, lspIdx2, lspIdx3, lspIdx4});

        // Pitch delay and gains for 3 subframes
        int[] delay = new int[SUBFRAMES];
        float[] pitchGain = new float[SUBFRAMES];
        float[] fcbGain = new float[SUBFRAMES];

        delay[0] = readBits(7) + 17;
        for (int i = 1; i < SUBFRAMES; i++) {
            int delta = readBits(3) - 4;
            delay[i] = delay[i - 1] + delta;
            if (delay[i] < 17) delay[i] = 17;
            if (delay[i] > 120) delay[i] = 120;
        }

        for (int i = 0; i < SUBFRAMES; i++) {
            pitchGain[i] = readBits(3) * 0.125f;
            int fcbGainIdx = readBits(4);
            fcbGain[i] = EVRCTables.FCB_GAIN[fcbGainIdx];
        }

        // Fixed codebook indices
        int[] fcbIdx = new int[SUBFRAMES];
        for (int i = 0; i < SUBFRAMES; i++) {
            fcbIdx[i] = readBits(10);
        }

        synthesize(lsp, delay, pitchGain, fcbIdx, fcbGain, output);
    }

    private void decodeHalfRate(short[] output) {
        // Half rate: 80 bits (10 bytes)
        int lspIdx1 = readBits(6);
        int lspIdx2 = readBits(6);
        int lspIdx3 = readBits(6);

        float[] lsp = decodeLspHalf(new int[]{lspIdx1, lspIdx2, lspIdx3});

        int delay = readBits(7) + 17;
        float pitchGain = readBits(3) * 0.125f;
        int fcbGainIdx = readBits(4);
        float fcbGain = EVRCTables.FCB_GAIN[fcbGainIdx];

        int fcbIdx = readBits(10);

        synthesizeHalf(lsp, delay, pitchGain, fcbIdx, fcbGain, output);
    }

    private void decodeQuarterRate(short[] output) {
        // Quarter rate: 40 bits (5 bytes)
        int lspIdx1 = readBits(4);
        int lspIdx2 = readBits(4);

        float[] lsp = decodeLspQuarter(new int[]{lspIdx1, lspIdx2});

        int gainIdx = readBits(4);
        float gain = EVRCTables.QUARTER_GAIN[gainIdx];

        synthesizeQuarter(lsp, gain, output);
    }

    private void decodeEighthRate(short[] output) {
        // Eighth rate: 16 bits (2 bytes)
        int gainIdx = readBits(4);
        float gain = EVRCTables.EIGHTH_GAIN[gainIdx];

        generateComfortNoise(gain, output);
    }

    private float[] decodeLsp(int[] indices) {
        float[] lsp = new float[LPC_ORDER];

        // Decode from EVRC codebooks
        for (int i = 0; i < 3; i++) {
            lsp[i] = EVRCTables.LSP_CB1[indices[0]][i];
        }
        for (int i = 3; i < 5; i++) {
            lsp[i] = EVRCTables.LSP_CB2[indices[1]][i - 3];
        }
        for (int i = 5; i < 8; i++) {
            lsp[i] = EVRCTables.LSP_CB3[indices[2]][i - 5];
        }
        for (int i = 8; i < 10; i++) {
            lsp[i] = EVRCTables.LSP_CB4[indices[3]][i - 8];
        }

        stabilizeLsp(lsp);
        System.arraycopy(lsp, 0, lspPrev, 0, LPC_ORDER);
        return lsp;
    }

    private float[] decodeLspHalf(int[] indices) {
        float[] lsp = new float[LPC_ORDER];

        // Interpolate with previous
        for (int i = 0; i < LPC_ORDER; i++) {
            lsp[i] = 0.5f * lspPrev[i];
        }

        // Add codebook contribution
        for (int i = 0; i < 4; i++) {
            lsp[i] += 0.5f * EVRCTables.LSP_CB_HALF1[indices[0]][i];
        }
        for (int i = 4; i < 7; i++) {
            lsp[i] += 0.5f * EVRCTables.LSP_CB_HALF2[indices[1]][i - 4];
        }
        for (int i = 7; i < 10; i++) {
            lsp[i] += 0.5f * EVRCTables.LSP_CB_HALF3[indices[2]][i - 7];
        }

        stabilizeLsp(lsp);
        System.arraycopy(lsp, 0, lspPrev, 0, LPC_ORDER);
        return lsp;
    }

    private float[] decodeLspQuarter(int[] indices) {
        float[] lsp = new float[LPC_ORDER];

        // Strong interpolation with previous
        for (int i = 0; i < LPC_ORDER; i++) {
            lsp[i] = 0.8f * lspPrev[i] + 0.2f * ((i + 1.0f) / (LPC_ORDER + 1) * 0.5f);
        }

        stabilizeLsp(lsp);
        System.arraycopy(lsp, 0, lspPrev, 0, LPC_ORDER);
        return lsp;
    }

    private void stabilizeLsp(float[] lsp) {
        float minGap = 0.01f;
        for (int i = 1; i < LPC_ORDER; i++) {
            if (lsp[i] < lsp[i - 1] + minGap) {
                lsp[i] = lsp[i - 1] + minGap;
            }
        }
        if (lsp[0] < 0.005f) lsp[0] = 0.005f;
        if (lsp[LPC_ORDER - 1] > 0.495f) lsp[LPC_ORDER - 1] = 0.495f;
    }

    private void lspToLpc(float[] lsp, float[] lpc) {
        float[] p = new float[LPC_ORDER / 2 + 1];
        float[] q = new float[LPC_ORDER / 2 + 1];
        p[0] = 1.0f;
        q[0] = 1.0f;

        for (int i = 0; i < LPC_ORDER / 2; i++) {
            float w1 = (float) Math.cos(2 * Math.PI * lsp[2 * i]);
            float w2 = (float) Math.cos(2 * Math.PI * lsp[2 * i + 1]);

            for (int j = i + 1; j >= 1; j--) {
                p[j] = p[j] - 2 * w1 * p[j - 1] + (j >= 2 ? p[j - 2] : 0);
                q[j] = q[j] - 2 * w2 * q[j - 1] + (j >= 2 ? q[j - 2] : 0);
            }
        }

        lpc[0] = 1.0f;
        for (int i = 1; i <= LPC_ORDER; i++) {
            float pVal = (i <= LPC_ORDER / 2) ? p[i] : p[LPC_ORDER - i];
            float qVal = (i <= LPC_ORDER / 2) ? q[i] : q[LPC_ORDER - i];
            lpc[i] = 0.5f * (pVal + qVal);
        }
    }

    private void synthesize(float[] lsp, int[] delay, float[] pitchGain,
                            int[] fcbIdx, float[] fcbGain, short[] output) {
        float[] lpc = new float[LPC_ORDER + 1];
        lspToLpc(lsp, lpc);

        float[] excBuf = new float[SAMPLES_PER_FRAME];
        int pos = 0;

        for (int sf = 0; sf < SUBFRAMES; sf++) {
            int subframeSize = SUBFRAME_SIZES[sf];

            for (int i = 0; i < subframeSize; i++) {
                // Adaptive codebook (pitch)
                int lagIdx = 200 + pos + i - delay[sf];
                float excSample = 0;
                if (lagIdx >= 0 && lagIdx < excitation.length) {
                    excSample = pitchGain[sf] * excitation[lagIdx];
                }

                // Fixed codebook
                float fcb = generateFCBSample(fcbIdx[sf] + i);
                excSample += fcbGain[sf] * fcb;

                excBuf[pos + i] = excSample;
            }
            pos += subframeSize;
        }

        // Update excitation history
        System.arraycopy(excitation, SAMPLES_PER_FRAME, excitation, 0, 200);
        System.arraycopy(excBuf, 0, excitation, 200, SAMPLES_PER_FRAME);

        // LPC synthesis
        float[] synthOut = synthesisFilter(excBuf, lpc);
        System.arraycopy(synthOut, SAMPLES_PER_FRAME - LPC_ORDER, synthBuffer, 0, LPC_ORDER);

        // Output
        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            int sample = Math.round(synthOut[i] * 32767);
            sample = Math.max(-32768, Math.min(32767, sample));
            output[i] = (short) sample;
        }

        System.arraycopy(lpc, 0, lpcPrev, 0, LPC_ORDER + 1);
    }

    private void synthesizeHalf(float[] lsp, int delay, float pitchGain,
                                int fcbIdx, float fcbGain, short[] output) {
        float[] lpc = new float[LPC_ORDER + 1];
        lspToLpc(lsp, lpc);

        float[] excBuf = new float[SAMPLES_PER_FRAME];

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            int lagIdx = 200 + i - delay;
            float excSample = 0;
            if (lagIdx >= 0 && lagIdx < excitation.length) {
                excSample = pitchGain * excitation[lagIdx];
            }
            float fcb = generateFCBSample(fcbIdx + i);
            excSample += fcbGain * fcb;
            excBuf[i] = excSample;
        }

        System.arraycopy(excitation, SAMPLES_PER_FRAME, excitation, 0, 200);
        System.arraycopy(excBuf, 0, excitation, 200, SAMPLES_PER_FRAME);

        float[] synthOut = synthesisFilter(excBuf, lpc);
        System.arraycopy(synthOut, SAMPLES_PER_FRAME - LPC_ORDER, synthBuffer, 0, LPC_ORDER);

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            int sample = Math.round(synthOut[i] * 32767);
            sample = Math.max(-32768, Math.min(32767, sample));
            output[i] = (short) sample;
        }

        System.arraycopy(lpc, 0, lpcPrev, 0, LPC_ORDER + 1);
    }

    private void synthesizeQuarter(float[] lsp, float gain, short[] output) {
        float[] lpc = new float[LPC_ORDER + 1];
        lspToLpc(lsp, lpc);

        float[] excBuf = new float[SAMPLES_PER_FRAME];
        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            excBuf[i] = gain * generateRandom();
        }

        System.arraycopy(excitation, SAMPLES_PER_FRAME, excitation, 0, 200);
        System.arraycopy(excBuf, 0, excitation, 200, SAMPLES_PER_FRAME);

        float[] synthOut = synthesisFilter(excBuf, lpc);
        System.arraycopy(synthOut, SAMPLES_PER_FRAME - LPC_ORDER, synthBuffer, 0, LPC_ORDER);

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            int sample = Math.round(synthOut[i] * 32767);
            sample = Math.max(-32768, Math.min(32767, sample));
            output[i] = (short) sample;
        }

        System.arraycopy(lpc, 0, lpcPrev, 0, LPC_ORDER + 1);
    }

    private void generateComfortNoise(float gain, short[] output) {
        float[] excBuf = new float[SAMPLES_PER_FRAME];
        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            excBuf[i] = gain * generateRandom();
        }

        System.arraycopy(excitation, SAMPLES_PER_FRAME, excitation, 0, 200);
        System.arraycopy(excBuf, 0, excitation, 200, SAMPLES_PER_FRAME);

        float[] synthOut = synthesisFilter(excBuf, lpcPrev);
        System.arraycopy(synthOut, SAMPLES_PER_FRAME - LPC_ORDER, synthBuffer, 0, LPC_ORDER);

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            int sample = Math.round(synthOut[i] * 32767);
            sample = Math.max(-32768, Math.min(32767, sample));
            output[i] = (short) sample;
        }
    }

    private void generateErasure(short[] output) {
        float gain = 0.1f;
        float[] excBuf = new float[SAMPLES_PER_FRAME];

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            excBuf[i] = gain * generateRandom();
        }

        System.arraycopy(excitation, SAMPLES_PER_FRAME, excitation, 0, 200);
        System.arraycopy(excBuf, 0, excitation, 200, SAMPLES_PER_FRAME);

        float[] synthOut = synthesisFilter(excBuf, lpcPrev);
        System.arraycopy(synthOut, SAMPLES_PER_FRAME - LPC_ORDER, synthBuffer, 0, LPC_ORDER);

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            int sample = Math.round(synthOut[i] * 32767 * 0.5f);
            sample = Math.max(-32768, Math.min(32767, sample));
            output[i] = (short) sample;
        }
    }

    private float[] synthesisFilter(float[] excBuf, float[] lpc) {
        float[] synthOut = new float[SAMPLES_PER_FRAME];

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            synthOut[i] = excBuf[i];
            for (int j = 1; j <= LPC_ORDER; j++) {
                if (i - j >= 0) {
                    synthOut[i] -= lpc[j] * synthOut[i - j];
                } else {
                    synthOut[i] -= lpc[j] * synthBuffer[LPC_ORDER + i - j];
                }
            }
        }

        return synthOut;
    }

    private float generateFCBSample(int seed) {
        int x = ((seed * 1103515245 + 12345) >> 16) & 0x7FFF;
        return (x - 16384) / 16384.0f;
    }

    private float generateRandom() {
        rndSeed = (rndSeed * 31821 + 13849) & 0xFFFF;
        return (rndSeed - 32768) / 32768.0f;
    }

    private int readBits(int numBits) {
        int value = 0;
        for (int i = 0; i < numBits; i++) {
            int byteIdx = bitPos / 8;
            int bitIdx = 7 - (bitPos % 8);
            if (byteIdx < bits.length) {
                value = (value << 1) | ((bits[byteIdx] >> bitIdx) & 1);
            }
            bitPos++;
        }
        return value;
    }
}