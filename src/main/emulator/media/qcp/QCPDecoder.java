package emulator.media.qcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * QCP format decoder supporting QCELP-13K, EVRC, SMV codecs.
 * Based on RFC 3625: "The QCP File Format and Media Types for Speech Data"
 */
public class QCPDecoder {

    public static final int CODEC_UNKNOWN = 0;
    public static final int CODEC_QCELP = 1;
    public static final int CODEC_EVRC = 2;
    public static final int CODEC_SMV = 3;
    public static final int CODEC_4GV = 4;

    private static final int SAMPLE_RATE = 8000;
    private static final int SAMPLES_PER_FRAME = 160;

    // QCELP-13K GUID: {5E7F6D41-B115-11D0-BA91-00805FB4B97E}
    // As stored in file (little-endian for first 3 parts):
    // 41 6D 7F 5E  15 B1  D0 11  BA 91 00 80 5F B4 B9 7E
    private static final byte[] GUID_QCELP_13K_1 = {
            0x41, 0x6d, 0x7f, 0x5e, 0x15, (byte) 0xb1, (byte) 0xd0, 0x11,
            (byte) 0xba, (byte) 0x91, 0x00, (byte) 0x80, 0x5f, (byte) 0xb4, (byte) 0xb9, 0x7e
    };

    // QCELP-13K variant: {5E7F6D42-B115-11D0-BA91-00805FB4B97E}
    private static final byte[] GUID_QCELP_13K_2 = {
            0x42, 0x6d, 0x7f, 0x5e, 0x15, (byte) 0xb1, (byte) 0xd0, 0x11,
            (byte) 0xba, (byte) 0x91, 0x00, (byte) 0x80, 0x5f, (byte) 0xb4, (byte) 0xb9, 0x7e
    };

    // EVRC GUID: {E689D48D-9076-46B5-91EF-736A5100CEB4}
    private static final byte[] GUID_EVRC = {
            (byte) 0x8d, (byte) 0xd4, (byte) 0x89, (byte) 0xe6, 0x76, (byte) 0x90, (byte) 0xb5, 0x46,
            (byte) 0x91, (byte) 0xef, 0x73, 0x6a, 0x51, 0x00, (byte) 0xce, (byte) 0xb4
    };

    // EVRC-B GUID
    private static final byte[] GUID_EVRC_B = {
            (byte) 0x8d, (byte) 0xd4, (byte) 0x89, (byte) 0xe6, 0x76, (byte) 0x90, (byte) 0xb5, 0x46,
            (byte) 0x91, (byte) 0xef, 0x73, 0x6a, 0x51, 0x00, (byte) 0xce, (byte) 0xb4
    };

    // SMV GUID: {8D7C2B75-A797-ED49-985E-D53C8CC75F84}
    private static final byte[] GUID_SMV = {
            0x75, 0x2b, 0x7c, (byte) 0x8d, (byte) 0x97, (byte) 0xa7, 0x49, (byte) 0xed,
            (byte) 0x98, 0x5e, (byte) 0xd5, 0x3c, (byte) 0x8c, (byte) 0xc7, 0x5f, (byte) 0x84
    };

    // 4GV GUID
    private static final byte[] GUID_4GV = {
            (byte) 0xca, 0x29, (byte) 0xfd, 0x3c, 0x53, (byte) 0xf6, (byte) 0xf5, 0x4e,
            (byte) 0x90, (byte) 0xe9, (byte) 0xf4, 0x23, 0x6d, 0x59, (byte) 0x9b, 0x61
    };

    // PureVoice GUID (another QCELP variant)
    private static final byte[] GUID_PUREVOICE = {
            0x5e, 0x7f, 0x6d, 0x41, 0x15, (byte) 0xb1, (byte) 0xd0, 0x11,
            (byte) 0xba, (byte) 0x91, 0x00, (byte) 0x80, 0x5f, (byte) 0xb4, (byte) 0xb9, 0x7e
    };

    private int codecType = CODEC_UNKNOWN;
    private int sampleRate = SAMPLE_RATE;
    private int packetSize = 0;
    private boolean variableRate = false;
    private int[] ratesPerMode = new int[5];

    /**
     * Decode QCP data to PCM samples.
     * @param data Raw QCP file data
     * @return PCM audio data (16-bit signed, mono, 8000Hz) or null on error
     */
    public static byte[] decode(byte[] data) {
        if (data == null || data.length < 50) {
            return null;
        }

        try {
            QCPDecoder decoder = new QCPDecoder();
            return decoder.decodeInternal(data);
        } catch (Exception e) {
            System.err.println(">>QCP decode error: " + e.getMessage());
            return null;
        }
    }

    private byte[] decodeInternal(byte[] data) throws IOException {
        int pos = 0;

        // Check RIFF header
        if (!checkTag(data, pos, "RIFF")) {
            throw new IOException("Not a RIFF file");
        }
        pos += 4;

        int fileSize = readInt32LE(data, pos);
        pos += 4;

        // Check QLCM format
        if (!checkTag(data, pos, "QLCM")) {
            throw new IOException("Not a QCP file (expected QLCM, got: " +
                    new String(data, pos, 4) + ")");
        }
        pos += 4;

        // Parse chunks
        int dataOffset = -1;
        int dataSize = 0;

        while (pos < data.length - 8) {
            String tag = new String(data, pos, 4);
            pos += 4;
            int chunkSize = readInt32LE(data, pos);
            pos += 4;

            if ("fmt ".equals(tag)) {
                parseFmtChunk(data, pos, chunkSize);
            } else if ("vrat".equals(tag)) {
                parseVratChunk(data, pos, chunkSize);
            } else if ("data".equals(tag)) {
                dataOffset = pos;
                dataSize = chunkSize;
                break;
            }

            pos += chunkSize;
            // Align to word boundary
            if ((pos & 1) != 0) {
                pos++;
            }
        }

        if (dataOffset < 0 || dataSize <= 0) {
            throw new IOException("No data chunk found");
        }

        return decodeAudioData(data, dataOffset, dataSize);
    }

    private void parseFmtChunk(byte[] data, int offset, int size) throws IOException {
        if (size < 150) {
            throw new IOException("fmt chunk too small: " + size);
        }

        // Skip major version (1 byte) and minor version (1 byte)
        int pos = offset + 2;

        // Read codec GUID (16 bytes)
        byte[] guid = new byte[16];
        System.arraycopy(data, pos, guid, 0, 16);
        codecType = identifyCodec(guid);
        pos += 16;

        if (codecType == CODEC_UNKNOWN) {
            // Print GUID for debugging
            StringBuilder sb = new StringBuilder("Unknown codec GUID: ");
            for (int i = 0; i < 16; i++) {
                sb.append(String.format("%02X ", guid[i] & 0xFF));
            }
            System.err.println(sb.toString());
            throw new IOException("Unknown codec GUID");
        }

        // Skip codec version (2 bytes) and codec name (80 bytes)
        pos += 2 + 80;

        // Average bits per second (2 bytes)
        int bitRate = readInt16LE(data, pos);
        pos += 2;

        // Packet size (2 bytes)
        packetSize = readInt16LE(data, pos);
        pos += 2;

        // Block size (2 bytes)
        pos += 2;

        // Sample rate (2 bytes)
        sampleRate = readInt16LE(data, pos);
        if (sampleRate == 0) {
            sampleRate = SAMPLE_RATE;
        }
        pos += 2;

        // Sample size (2 bytes)
        pos += 2;

        // Rate map entries
        Arrays.fill(ratesPerMode, -1);
        int numRates = Math.min(readInt32LE(data, pos), 8);
        pos += 4;

        for (int i = 0; i < numRates; i++) {
            int pktSize = data[pos++] & 0xFF;
            int mode = data[pos++] & 0xFF;
            if (mode <= 4) {
                ratesPerMode[mode] = pktSize;
            }
        }
    }

    private void parseVratChunk(byte[] data, int offset, int size) {
        if (size >= 8) {
            int varRateFlag = readInt32LE(data, offset);
            variableRate = (varRateFlag != 0);
            if (variableRate) {
                packetSize = 0;
            }
        }
    }

    private int identifyCodec(byte[] guid) {
        // Check all known QCELP GUIDs
        if (Arrays.equals(guid, GUID_QCELP_13K_1) || Arrays.equals(guid, GUID_QCELP_13K_2)) {
            return CODEC_QCELP;
        }

        // Check QCELP with first byte variation (0x41 or 0x42)
        if ((guid[0] == 0x41 || guid[0] == 0x42) && guid[1] == 0x6d && guid[2] == 0x7f && guid[3] == 0x5e) {
            return CODEC_QCELP;
        }

        // Check PureVoice format (different byte order)
        if (guid[0] == 0x5e && guid[1] == 0x7f && guid[2] == 0x6d && (guid[3] == 0x41 || guid[3] == 0x42)) {
            return CODEC_QCELP;
        }

        // Check by first 4 bytes pattern for QCELP
        int first4 = readInt32LE(guid, 0);
        if (first4 == 0x5E7F6D41 || first4 == 0x5E7F6D42) {
            return CODEC_QCELP;
        }

        if (Arrays.equals(guid, GUID_EVRC) || Arrays.equals(guid, GUID_EVRC_B)) {
            return CODEC_EVRC;
        }

        // Check EVRC by pattern
        if (guid[0] == (byte) 0x8d && guid[1] == (byte) 0xd4 && guid[2] == (byte) 0x89 && guid[3] == (byte) 0xe6) {
            return CODEC_EVRC;
        }

        if (Arrays.equals(guid, GUID_SMV)) {
            return CODEC_SMV;
        }

        // Check SMV by pattern
        if (guid[0] == 0x75 && guid[1] == 0x2b && guid[2] == 0x7c && guid[3] == (byte) 0x8d) {
            return CODEC_SMV;
        }

        if (Arrays.equals(guid, GUID_4GV)) {
            return CODEC_4GV;
        }

        return CODEC_UNKNOWN;
    }

    private byte[] decodeAudioData(byte[] data, int offset, int size) throws IOException {
        ByteArrayOutputStream pcmOut = new ByteArrayOutputStream();
        int pos = offset;
        int endPos = offset + size;

        // Create appropriate decoder
        FrameDecoder frameDecoder;
        switch (codecType) {
            case CODEC_QCELP:
                frameDecoder = new QCELPDecoder();
                break;
            case CODEC_EVRC:
                frameDecoder = new EVRCDecoder();
                break;
            case CODEC_SMV:
                frameDecoder = new SMVDecoder();
                break;
            default:
                throw new IOException("Unsupported codec type: " + codecType);
        }

        short[] frameSamples = new short[SAMPLES_PER_FRAME];

        while (pos < endPos) {
            int mode = data[pos++] & 0xFF;
            int pktSize;

            if (packetSize > 0) {
                pktSize = packetSize - 1;
            } else if (mode <= 4 && ratesPerMode[mode] >= 0) {
                pktSize = ratesPerMode[mode];
            } else {
                // Unknown mode, try to continue
                continue;
            }

            if (pos + pktSize > endPos) {
                break;
            }

            byte[] frameData = new byte[pktSize + 1];
            frameData[0] = (byte) mode;
            System.arraycopy(data, pos, frameData, 1, pktSize);
            pos += pktSize;

            // Decode frame
            frameDecoder.decodeFrame(frameData, frameSamples);

            // Convert to bytes (little-endian 16-bit)
            for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
                pcmOut.write(frameSamples[i] & 0xFF);
                pcmOut.write((frameSamples[i] >> 8) & 0xFF);
            }
        }

        return pcmOut.toByteArray();
    }

    private boolean checkTag(byte[] data, int offset, String tag) {
        if (offset + 4 > data.length) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (data[offset + i] != tag.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static int readInt16LE(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readInt32LE(byte[] data, int offset) {
        return (data[offset] & 0xFF) |
                ((data[offset + 1] & 0xFF) << 8) |
                ((data[offset + 2] & 0xFF) << 16) |
                ((data[offset + 3] & 0xFF) << 24);
    }

    /**
     * Check if the data appears to be a QCP file.
     */
    public static boolean isQCPFile(byte[] data) {
        if (data == null || data.length < 12) {
            return false;
        }
        return data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
                data[8] == 'Q' && data[9] == 'L' && data[10] == 'C' && data[11] == 'M';
    }

    public int getCodecType() {
        return codecType;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public String getCodecName() {
        switch (codecType) {
            case CODEC_QCELP:
                return "QCELP-13K";
            case CODEC_EVRC:
                return "EVRC";
            case CODEC_SMV:
                return "SMV";
            case CODEC_4GV:
                return "4GV";
            default:
                return "Unknown";
        }
    }
}