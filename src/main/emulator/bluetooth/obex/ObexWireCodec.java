package emulator.bluetooth.obex;

import emulator.bluetooth.BluetoothWireCodec;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * Explicit framed wire format used by the LAN OBEX adapter.
 *
 * <p>Each request and response is a {@link BluetoothWireCodec} frame. A
 * request frame contains {@code byte operation}, a typed header map and a
 * nullable binary body. A response frame contains a typed header map,
 * {@code int responseCode}, and a nullable binary body. Header entries are
 * {@code int id + byte valueType + value}; strings and byte arrays are
 * length-prefixed by {@link BluetoothWireCodec}. No Java object serialization,
 * class names, or JDK modified-UTF encoding is used.</p>
 */
final class ObexWireCodec {

    static final int CONNECT = 0;
    static final int DISCONNECT = 1;
    static final int PUT = 2;
    static final int GET = 3;
    static final int SET_PATH = 4;
    static final int DELETE = 5;

    private static final int MAX_HEADERS = 256;

    private static final int VALUE_NULL = 0;
    private static final int VALUE_STRING = 1;
    private static final int VALUE_LONG = 2;
    private static final int VALUE_INTEGER = 3;
    private static final int VALUE_BYTES = 4;
    private static final int VALUE_BOOLEAN = 5;
    private static final int VALUE_CALENDAR = 6;

    private ObexWireCodec() {}

    static void writeRequest(DataOutputStream output, final int operation,
                             final HeaderSetImpl headers, final byte[] body) throws IOException {
        byte[] payload = BluetoothWireCodec.buildFrame(new BluetoothWireCodec.FrameWriter() {
            @Override
            public void write(DataOutputStream frame) throws IOException {
                frame.writeByte(operation);
                writeHeaders(frame, headers);
                BluetoothWireCodec.writeBlob(frame, body);
            }
        });
        BluetoothWireCodec.writeFrame(output, payload);
        output.flush();
    }

    static Request readRequest(DataInputStream input) throws IOException {
        DataInputStream frame = new DataInputStream(new ByteArrayInputStream(BluetoothWireCodec.readFrame(input)));
        int operation = frame.readUnsignedByte();
        HeaderSetImpl headers = readHeaders(frame);
        byte[] body = BluetoothWireCodec.readBlob(frame);
        ensureFullyRead(frame, "request");
        return new Request(operation, headers, body);
    }

    static void writeResponse(DataOutputStream output, final HeaderSetImpl headers,
                              final int responseCode, final byte[] body) throws IOException {
        byte[] payload = BluetoothWireCodec.buildFrame(new BluetoothWireCodec.FrameWriter() {
            @Override
            public void write(DataOutputStream frame) throws IOException {
                writeHeaders(frame, headers);
                frame.writeInt(responseCode);
                BluetoothWireCodec.writeBlob(frame, body);
            }
        });
        BluetoothWireCodec.writeFrame(output, payload);
        output.flush();
    }

    static Response readResponse(DataInputStream input) throws IOException {
        DataInputStream frame = new DataInputStream(new ByteArrayInputStream(BluetoothWireCodec.readFrame(input)));
        HeaderSetImpl headers = readHeaders(frame);
        int responseCode = frame.readInt();
        byte[] body = BluetoothWireCodec.readBlob(frame);
        ensureFullyRead(frame, "response");
        headers.setResponseCode(responseCode);
        return new Response(headers, body);
    }

    private static void writeHeaders(DataOutputStream output, HeaderSetImpl headerSet) throws IOException {
        Map<Integer, Object> headers = headerSet != null ? headerSet.getHeaders() : null;
        int count = headers != null ? headers.size() : 0;
        if (count > MAX_HEADERS) {
            throw new IOException("Too many OBEX headers: " + count);
        }
        output.writeInt(count);
        if (headers == null) return;

        for (Map.Entry<Integer, Object> entry : headers.entrySet()) {
            Integer id = entry.getKey();
            if (id == null) {
                throw new IOException("OBEX header ID cannot be null");
            }
            output.writeInt(id.intValue());
            writeHeaderValue(output, entry.getValue());
        }
    }

    private static HeaderSetImpl readHeaders(DataInputStream input) throws IOException {
        int count = BluetoothWireCodec.readCount(input, MAX_HEADERS, "OBEX header");
        HeaderSetImpl result = new HeaderSetImpl();
        for (int i = 0; i < count; i++) {
            int id = input.readInt();
            Object value = readHeaderValue(input);
            if (value != null) {
                result.setHeader(id, value);
            }
        }
        return result;
    }

    private static void writeHeaderValue(DataOutputStream output, Object value) throws IOException {
        if (value == null) {
            output.writeByte(VALUE_NULL);
        } else if (value instanceof String) {
            output.writeByte(VALUE_STRING);
            BluetoothWireCodec.writeString(output, (String) value);
        } else if (value instanceof Long) {
            output.writeByte(VALUE_LONG);
            output.writeLong(((Long) value).longValue());
        } else if (value instanceof Integer) {
            output.writeByte(VALUE_INTEGER);
            output.writeInt(((Integer) value).intValue());
        } else if (value instanceof byte[]) {
            output.writeByte(VALUE_BYTES);
            BluetoothWireCodec.writeBlob(output, (byte[]) value);
        } else if (value instanceof Boolean) {
            output.writeByte(VALUE_BOOLEAN);
            output.writeBoolean(((Boolean) value).booleanValue());
        } else if (value instanceof Calendar) {
            output.writeByte(VALUE_CALENDAR);
            output.writeLong(((Calendar) value).getTimeInMillis());
        } else {
            throw new IOException("Unsupported OBEX header value type");
        }
    }

    private static Object readHeaderValue(DataInputStream input) throws IOException {
        switch (input.readUnsignedByte()) {
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return BluetoothWireCodec.readString(input);
            case VALUE_LONG:
                return Long.valueOf(input.readLong());
            case VALUE_INTEGER:
                return Integer.valueOf(input.readInt());
            case VALUE_BYTES:
                return BluetoothWireCodec.readBlob(input);
            case VALUE_BOOLEAN:
                return Boolean.valueOf(input.readBoolean());
            case VALUE_CALENDAR:
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(input.readLong());
                return calendar;
            default:
                throw new IOException("Unknown OBEX header value type");
        }
    }

    private static void ensureFullyRead(DataInputStream frame, String kind) throws IOException {
        if (frame.available() != 0) {
            throw new IOException("Trailing bytes in OBEX " + kind + " frame");
        }
    }

    static final class Request {
        final int operation;
        final HeaderSetImpl headers;
        final byte[] body;

        Request(int operation, HeaderSetImpl headers, byte[] body) {
            this.operation = operation;
            this.headers = headers;
            this.body = body;
        }
    }

    static final class Response {
        final HeaderSetImpl headers;
        final byte[] body;

        Response(HeaderSetImpl headers, byte[] body) {
            this.headers = headers;
            this.body = body;
        }
    }
}
