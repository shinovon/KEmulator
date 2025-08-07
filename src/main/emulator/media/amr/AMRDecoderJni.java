package emulator.media.amr;

public class AMRDecoderJni {

    public static native long initDecoder();
    public static native void decodeFrame(long state, byte[] amrFrame, short[] pcmOut);
    public static native void closeDecoder(long state);
}