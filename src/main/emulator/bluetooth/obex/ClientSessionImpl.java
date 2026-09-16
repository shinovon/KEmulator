package emulator.bluetooth.obex;

import javax.obex.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Emulated OBEX ClientSession over TCP (which itself emulates BT RFCOMM).
 * 
 * Simple protocol:
 * - All messages are: int length + byte type + headers + optional data
 * Types: 0=CONNECT, 1=DISCONNECT, 2=PUT, 3=GET, 4=SETPATH, 5=DELETE, 6=RESPONSE
 * 
 * For simplicity, we implement OBEX as direct method calls over socket using Java serialization
 * of headers and data. This is sufficient for emulation between KEmulator instances.
 */
public class ClientSessionImpl implements ClientSession {

    private final Socket socket;
    private final String url;
    private final DataInputStream in;
    private final DataOutputStream out;
    private Authenticator authenticator;
    private long connectionId = -1;
    private boolean connected = false;
    private boolean closed = false;

    public ClientSessionImpl(Socket socket, String url) throws IOException {
        this.socket = socket;
        this.url = url;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        socket.setTcpNoDelay(true);
    }

    @Override
    public void setAuthenticator(Authenticator auth) {
        this.authenticator = auth;
    }

    @Override
    public HeaderSet createHeaderSet() {
        return new HeaderSetImpl();
    }

    @Override
    public void setConnectionID(long id) {
        this.connectionId = id;
    }

    @Override
    public long getConnectionID() {
        return connectionId;
    }

    @Override
    public HeaderSet connect(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        if (connected) throw new IOException("Already connected");

        HeaderSetImpl req = headers instanceof HeaderSetImpl ? (HeaderSetImpl) headers : new HeaderSetImpl();
        if (headers != null) {
            try {
                for (int id : headers.getHeaderList()) {
                    req.setHeader(id, headers.getHeader(id));
                }
            } catch (IOException ignored) {}
        }
        if (connectionId != -1) {
            req.setHeader(HeaderSet.TARGET, String.valueOf(connectionId));
        }

        sendRequest(0, req, null);

        HeaderSetImpl resp = readResponse();
        connected = true;
        if (resp.getHeader(HeaderSet.WHO) != null) {
            try {
                connectionId = Long.parseLong(resp.getHeader(HeaderSet.WHO).toString());
            } catch (Exception ignored) {}
        }
        return resp;
    }

    @Override
    public HeaderSet disconnect(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        HeaderSetImpl req = toImpl(headers);
        sendRequest(1, req, null);
        HeaderSetImpl resp = readResponse();
        connected = false;
        return resp;
    }

    @Override
    public HeaderSet setPath(HeaderSet headers, boolean backup, boolean create) throws IOException {
        if (closed) throw new IOException("Closed");
        HeaderSetImpl req = toImpl(headers);
        req.setHeader(0x100, backup ? Boolean.TRUE : Boolean.FALSE);
        req.setHeader(0x101, create ? Boolean.TRUE : Boolean.FALSE);
        sendRequest(4, req, null);
        return readResponse();
    }

    @Override
    public HeaderSet delete(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        HeaderSetImpl req = toImpl(headers);
        sendRequest(5, req, null);
        return readResponse();
    }

    @Override
    public Operation get(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        HeaderSetImpl req = toImpl(headers);
        sendRequest(3, req, null);
        // Read response: headers + data
        HeaderSetImpl respHeaders = readResponse();
        int dataLen = in.readInt();
        byte[] data = new byte[dataLen];
        if (dataLen > 0) in.readFully(data);
        OperationImpl op = new OperationImpl(respHeaders, data, false);
        op.setResponseCode(respHeaders.getResponseCode());
        return op;
    }

    @Override
    public Operation put(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        HeaderSetImpl req = toImpl(headers);
        // For PUT, we need to return an Operation where client can write data
        // We'll implement as: create OperationImpl that buffers output, and on close sends data
        return new PutOperation(req);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        try {
            if (connected) {
                try {
                    disconnect(null);
                } catch (IOException ignored) {}
            }
            in.close();
            out.close();
            socket.close();
        } finally {
            System.out.println("[BT] OBEX ClientSession closed: " + url);
        }
    }

    // --- Internal helpers ---

    private HeaderSetImpl toImpl(HeaderSet hs) {
        if (hs == null) return new HeaderSetImpl();
        if (hs instanceof HeaderSetImpl) return (HeaderSetImpl) hs;
        HeaderSetImpl impl = new HeaderSetImpl();
        try {
            for (int id : hs.getHeaderList()) {
                impl.setHeader(id, hs.getHeader(id));
            }
        } catch (IOException ignored) {}
        return impl;
    }

    private void sendRequest(int type, HeaderSetImpl headers, byte[] data) throws IOException {
        synchronized (out) {
            out.writeInt(0); // placeholder for length
            out.writeByte(type);
            // Write headers count
            Map<Integer, Object> map = headers.getHeaders();
            out.writeInt(map.size());
            for (Map.Entry<Integer, Object> e : map.entrySet()) {
                out.writeInt(e.getKey());
                // Write value as UTF string for simplicity
                String valStr = e.getValue() != null ? e.getValue().toString() : "";
                out.writeUTF(valStr);
                // Also write class name for type hint
                out.writeUTF(e.getValue() != null ? e.getValue().getClass().getName() : "null");
            }
            if (data != null) {
                out.writeInt(data.length);
                out.write(data);
            } else {
                out.writeInt(0);
            }
            out.flush();
        }
    }

    private HeaderSetImpl readResponse() throws IOException {
        synchronized (in) {
            // Our simple protocol doesn't use length prefix for response yet, just read
            int headerCount = in.readInt();
            HeaderSetImpl hs = new HeaderSetImpl();
            for (int i = 0; i < headerCount; i++) {
                int id = in.readInt();
                String valStr = in.readUTF();
                String className = in.readUTF();
                Object val = valStr;
                // Try to reconstruct original type for common types
                if (className.equals("java.lang.Long") || id == HeaderSet.LENGTH || id == HeaderSet.TIME_4_BYTE) {
                    try { val = Long.parseLong(valStr); } catch (NumberFormatException ignored) {}
                } else if (className.equals("java.lang.Integer")) {
                    try { val = Integer.parseInt(valStr); } catch (NumberFormatException ignored) {}
                }
                hs.setHeader(id, val);
            }
            int respCode = in.readInt();
            hs.setResponseCode(respCode);
            return hs;
        }
    }

    /**
     * PUT operation that buffers data and sends on close.
     */
    private class PutOperation extends OperationImpl {
        private final HeaderSetImpl requestHeaders;
        private boolean finished = false;

        PutOperation(HeaderSetImpl requestHeaders) {
            super(requestHeaders, new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream(), true);
            this.requestHeaders = requestHeaders;
        }

        @Override
        public void close() throws IOException {
            if (finished) return;
            finished = true;
            byte[] data = getOutputData();
            // Send PUT request with data
            sendRequest(2, requestHeaders, data);
            HeaderSetImpl resp = readResponse();
            setResponseCode(resp.getResponseCode());
            super.close();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return super.openOutputStream();
        }
    }
}
