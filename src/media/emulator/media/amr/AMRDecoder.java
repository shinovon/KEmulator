package emulator.media.amr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AMRDecoder {
    private static final int[] FRAME_SIZES = {
            12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0
    };

    public static byte[] decode(byte[] amrData) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(amrData);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Skip AMR header if present
        byte[] header = new byte[6];
        if (in.read(header) == 6 &&
                header[0] == '#' && header[1] == '!' &&
                header[2] == 'A' && header[3] == 'M' &&
                header[4] == 'R' && header[5] == '\n') {
            // Valid header, proceed
        } else {
            // No header, reset stream
            in = new ByteArrayInputStream(amrData);
        }

        long decoderState = AMRDecoderJni.initDecoder();
        if (decoderState == 0) {
            throw new IOException("Failed to initialize AMR decoder");
        }

        try {
            byte[] frame = new byte[32]; // Max frame size
            short[] pcmFrame = new short[160];

            while (in.available() > 0) {
                int toc = in.read();
                if (toc == -1) break;

                int frameType = (toc >> 3) & 0x0F;
                int frameSize = FRAME_SIZES[frameType];

                if (frameSize <= 0) continue;

                int read = in.read(frame, 0, frameSize);
                if (read != frameSize) break;

                // Combine TOC and frame data
                byte[] fullFrame = new byte[frameSize + 1];
                fullFrame[0] = (byte) toc;
                System.arraycopy(frame, 0, fullFrame, 1, frameSize);

                AMRDecoderJni.decodeFrame(decoderState, fullFrame, pcmFrame);

                // Convert PCM to byte array (little-endian)
                for (short sample : pcmFrame) {
                    out.write(sample & 0xFF);
                    out.write((sample >> 8) & 0xFF);
                }
            }
        } finally {
            AMRDecoderJni.closeDecoder(decoderState);
        }

        return out.toByteArray();
    }
}