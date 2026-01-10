package emulator.media.qcp;

/**
 * SMV (Selectable Mode Vocoder) decoder implementation.
 * SMV is similar to EVRC with additional modes.
 */
public class SMVDecoder implements FrameDecoder {

    private static final int SAMPLES_PER_FRAME = 160;

    // Use EVRC decoder as base since SMV is an extension
    private final EVRCDecoder evrcDecoder = new EVRCDecoder();

    public SMVDecoder() {
        reset();
    }

    @Override
    public void reset() {
        evrcDecoder.reset();
    }

    @Override
    public void decodeFrame(byte[] frameData, short[] output) {
        // SMV frame structure is similar to EVRC
        // For basic support, delegate to EVRC decoder
        evrcDecoder.decodeFrame(frameData, output);
    }
}