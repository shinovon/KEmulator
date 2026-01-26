package emulator.media.qcp;

/**
 * Interface for audio frame decoders.
 */
public interface FrameDecoder {

    /**
     * Decode a single frame of audio data.
     * @param frameData Encoded frame data (including rate/mode byte)
     * @param output Output buffer for decoded samples (160 samples)
     */
    void decodeFrame(byte[] frameData, short[] output);

    /**
     * Reset decoder state.
     */
    void reset();
}