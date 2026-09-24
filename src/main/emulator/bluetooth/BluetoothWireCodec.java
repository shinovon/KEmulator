package emulator.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Shared binary primitives for the KEmulator Bluetooth LAN protocol.
 *
 * <p>The protocol deliberately does not use Java object serialization or
 * the JDK modified-UTF helper. Integers are signed, big-endian
 * 32-bit values. A string is encoded as its UTF-8 byte length followed by its
 * bytes; {@code -1} represents {@code null}. A binary blob uses the same
 * length-prefix convention. A frame is an {@code int} byte length followed by
 * exactly that many bytes.</p>
 *
 * <p>All remote-controlled lengths are bounded before allocation. Keeping
 * these primitives in one place makes the LAN transport inspectable and lets
 * another Bluetooth backend reuse the same on-wire format.</p>
 */
public final class BluetoothWireCodec {

    /** Largest UTF-8 field accepted from a remote peer. */
    public static final int MAX_STRING_BYTES = 64 * 1024;

    /** Largest binary payload or complete frame accepted from a remote peer. */
    public static final int MAX_BLOB_BYTES = 16 * 1024 * 1024;

    private BluetoothWireCodec() {}

    /**
     * Writes a nullable string as {@code int UTF-8-byte-length + UTF-8 bytes}.
     */
    public static void writeString(DataOutputStream output, String value) throws IOException {
        if (value == null) {
            output.writeInt(-1);
            return;
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeLength(output, bytes.length, MAX_STRING_BYTES, "string");
        output.write(bytes);
    }

    /**
     * Reads a nullable string written by {@link #writeString(DataOutputStream, String)}.
     */
    public static String readString(DataInputStream input) throws IOException {
        int length = readLength(input, MAX_STRING_BYTES, "string");
        if (length == -1) return null;
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes a nullable binary blob as {@code int length + bytes}.
     */
    public static void writeBlob(DataOutputStream output, byte[] value) throws IOException {
        if (value == null) {
            output.writeInt(-1);
            return;
        }
        writeLength(output, value.length, MAX_BLOB_BYTES, "blob");
        output.write(value);
    }

    /**
     * Reads a nullable binary blob written by {@link #writeBlob(DataOutputStream, byte[])}.
     */
    public static byte[] readBlob(DataInputStream input) throws IOException {
        int length = readLength(input, MAX_BLOB_BYTES, "blob");
        if (length == -1) return null;
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return bytes;
    }

    /**
     * Writes a complete payload as {@code int byte-length + bytes}.
     */
    public static void writeFrame(DataOutputStream output, byte[] payload) throws IOException {
        if (payload == null) {
            throw new NullPointerException("payload");
        }
        writeLength(output, payload.length, MAX_BLOB_BYTES, "frame");
        output.write(payload);
    }

    /**
     * Reads one complete length-prefixed payload.
     */
    public static byte[] readFrame(DataInputStream input) throws IOException {
        int length = readLength(input, MAX_BLOB_BYTES, "frame");
        if (length < 0) {
            throw new IOException("Negative frame length");
        }
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return bytes;
    }

    /**
     * Builds a byte array with a caller-provided encoder. Used by framed
     * protocols so the length is known before anything reaches the socket.
     */
    public static byte[] buildFrame(FrameWriter writer) throws IOException {
        if (writer == null) throw new NullPointerException("writer");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream frame = new DataOutputStream(bytes);
        writer.write(frame);
        frame.flush();
        byte[] result = bytes.toByteArray();
        if (result.length > MAX_BLOB_BYTES) {
            throw new IOException("Frame exceeds " + MAX_BLOB_BYTES + " bytes");
        }
        return result;
    }

    /** Writes a self-contained frame body. */
    public interface FrameWriter {
        void write(DataOutputStream output) throws IOException;
    }

    /**
     * Reads a count supplied by a remote peer and rejects unreasonable values
     * before a collection is allocated or populated.
     */
    public static int readCount(DataInputStream input, int maximum, String label) throws IOException {
        int count = input.readInt();
        if (count < 0 || count > maximum) {
            throw new IOException("Invalid " + label + " count: " + count);
        }
        return count;
    }

    private static void writeLength(DataOutputStream output, int length, int maximum, String label) throws IOException {
        if (length < 0 || length > maximum) {
            throw new IOException(label + " exceeds " + maximum + " bytes");
        }
        output.writeInt(length);
    }

    private static int readLength(DataInputStream input, int maximum, String label) throws IOException {
        int length = input.readInt();
        if (length < -1 || length > maximum) {
            throw new IOException("Invalid " + label + " length: " + length);
        }
        return length;
    }
}
