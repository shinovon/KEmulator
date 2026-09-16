package emulator.bluetooth.obex;

import javax.obex.*;
import java.io.*;
import java.net.Socket;

/**
 * Emulated OBEX ClientSession over TCP (which itself emulates BT RFCOMM).
 *
 * <p>Requests and responses use {@link ObexWireCodec}: a manually encoded,
 * length-framed protocol with typed headers and UTF-8 fields. It intentionally
 * does not use Java serialization or JDK modified-UTF encoding.</p>
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

        HeaderSetImpl req = toImpl(headers);
        if (connectionId != -1) {
            req.setHeader(HeaderSet.TARGET, Long.valueOf(connectionId));
        }

        sendRequest(ObexWireCodec.CONNECT, req, null);
        HeaderSetImpl response = readResponse().headers;
        connected = true;
        Object who = response.getHeader(HeaderSet.WHO);
        if (who instanceof Long) {
            connectionId = ((Long) who).longValue();
        } else if (who instanceof Integer) {
            connectionId = ((Integer) who).longValue();
        }
        return response;
    }

    @Override
    public HeaderSet disconnect(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        sendRequest(ObexWireCodec.DISCONNECT, toImpl(headers), null);
        HeaderSetImpl response = readResponse().headers;
        connected = false;
        return response;
    }

    @Override
    public HeaderSet setPath(HeaderSet headers, boolean backup, boolean create) throws IOException {
        if (closed) throw new IOException("Closed");
        HeaderSetImpl req = toImpl(headers);
        req.setHeader(0x100, backup ? Boolean.TRUE : Boolean.FALSE);
        req.setHeader(0x101, create ? Boolean.TRUE : Boolean.FALSE);
        sendRequest(ObexWireCodec.SET_PATH, req, null);
        return readResponse().headers;
    }

    @Override
    public HeaderSet delete(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        sendRequest(ObexWireCodec.DELETE, toImpl(headers), null);
        return readResponse().headers;
    }

    @Override
    public Operation get(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        sendRequest(ObexWireCodec.GET, toImpl(headers), null);
        ObexWireCodec.Response response = readResponse();
        OperationImpl operation = new OperationImpl(response.headers, response.body, false);
        operation.setResponseCode(response.headers.getResponseCode());
        return operation;
    }

    @Override
    public Operation put(HeaderSet headers) throws IOException {
        if (closed) throw new IOException("Closed");
        // The operation buffers output locally and transmits one framed PUT on
        // close, matching the previous emulation behaviour without object
        // serialization.
        return new PutOperation(toImpl(headers));
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        try {
            if (connected) {
                try {
                    disconnect(null);
                } catch (IOException ignored) {}
            }
        } finally {
            closed = true;
            try { in.close(); } catch (IOException ignored) {}
            try { out.close(); } catch (IOException ignored) {}
            socket.close();
            System.out.println("[BT] OBEX ClientSession closed: " + url);
        }
    }

    private HeaderSetImpl toImpl(HeaderSet headerSet) {
        if (headerSet == null) return new HeaderSetImpl();
        if (headerSet instanceof HeaderSetImpl) return (HeaderSetImpl) headerSet;

        HeaderSetImpl result = new HeaderSetImpl();
        try {
            for (int id : headerSet.getHeaderList()) {
                result.setHeader(id, headerSet.getHeader(id));
            }
        } catch (IOException ignored) {}
        return result;
    }

    private void sendRequest(int operation, HeaderSetImpl headers, byte[] body) throws IOException {
        synchronized (out) {
            ObexWireCodec.writeRequest(out, operation, headers, body);
        }
    }

    private ObexWireCodec.Response readResponse() throws IOException {
        synchronized (in) {
            return ObexWireCodec.readResponse(in);
        }
    }

    /** PUT operation that buffers data and sends it as one framed request on close. */
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
            sendRequest(ObexWireCodec.PUT, requestHeaders, getOutputData());
            ObexWireCodec.Response response = readResponse();
            setResponseCode(response.headers.getResponseCode());
            super.close();
        }
    }
}
