package emulator.bluetooth.obex;

import javax.microedition.io.Connection;
import javax.obex.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server notifier for OBEX (btgoep) emulated over TCP.
 * Accepts connections and delegates to {@link ServerRequestHandler}.
 */
public class SessionNotifierImpl implements SessionNotifier {

    private final ServerSocket serverSocket;
    private final String url;
    private final String uuid;
    private final String serviceName;
    private boolean closed = false;

    public SessionNotifierImpl(ServerSocket serverSocket, String url, String uuid, String serviceName) {
        this.serverSocket = serverSocket;
        this.url = url;
        this.uuid = uuid;
        this.serviceName = serviceName;
    }

    @Override
    public Connection acceptAndOpen(ServerRequestHandler handler) throws IOException {
        return acceptAndOpen(handler, null);
    }

    @Override
    public Connection acceptAndOpen(ServerRequestHandler handler, Authenticator auth) throws IOException {
        if (closed) throw new IOException("Notifier closed");
        if (handler == null) throw new NullPointerException("handler");
        System.out.println("[BT] OBEX notifier waiting: " + url + " port " + serverSocket.getLocalPort());
        Socket client = serverSocket.accept();
        System.out.println("[BT] OBEX client connected: " + client.getInetAddress());

        Thread thread = new Thread(() -> handleObexSession(client, handler, auth),
                "KEm-BT-OBEX-" + client.getPort());
        thread.setDaemon(true);
        thread.start();

        // JSR-82 exposes the accepted transport as a Connection while the
        // handler thread owns the framed OBEX request/response protocol.
        return new ObexTransportConnection(client, url);
    }

    private void handleObexSession(Socket socket, ServerRequestHandler handler, Authenticator auth) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            socket.setTcpNoDelay(true);
            long connectionId = System.currentTimeMillis();

            while (!socket.isClosed()) {
                try {
                    ObexWireCodec.Request request = ObexWireCodec.readRequest(in);
                    HeaderSetImpl requestHeaders = request.headers;
                    HeaderSetImpl responseHeaders = new HeaderSetImpl();
                    byte[] responseBody = null;
                    int responseCode = ResponseCodes.OBEX_HTTP_OK;

                    switch (request.operation) {
                        case ObexWireCodec.CONNECT:
                            responseCode = handler.onConnect(requestHeaders, responseHeaders);
                            responseHeaders.setHeader(HeaderSet.WHO, Long.valueOf(connectionId));
                            break;
                        case ObexWireCodec.DISCONNECT:
                            handler.onDisconnect(requestHeaders, responseHeaders);
                            break;
                        case ObexWireCodec.PUT:
                            OperationImpl putOperation = new OperationImpl(requestHeaders, request.body, true);
                            responseCode = handler.onPut(putOperation);
                            responseHeaders = putOperation.getSentHeaders();
                            break;
                        case ObexWireCodec.GET:
                            OperationImpl getOperation = new OperationImpl(requestHeaders,
                                    new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream(), false);
                            responseCode = handler.onGet(getOperation);
                            responseHeaders = getOperation.getSentHeaders();
                            responseBody = getOperation.getOutputData();
                            break;
                        case ObexWireCodec.SET_PATH:
                            responseCode = handler.onSetPath(requestHeaders, responseHeaders,
                                    readBooleanHeader(requestHeaders, 0x100),
                                    readBooleanHeader(requestHeaders, 0x101));
                            break;
                        case ObexWireCodec.DELETE:
                            responseCode = handler.onDelete(requestHeaders, responseHeaders);
                            break;
                        default:
                            responseCode = ResponseCodes.OBEX_HTTP_BAD_REQUEST;
                            break;
                    }

                    synchronized (out) {
                        ObexWireCodec.writeResponse(out, responseHeaders, responseCode, responseBody);
                    }

                    if (request.operation == ObexWireCodec.DISCONNECT) break;
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException e) {
            // A malformed or disconnected remote peer ends this session only.
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private static boolean readBooleanHeader(HeaderSetImpl headers, int id) {
        try {
            Object value = headers.getHeader(id);
            return value instanceof Boolean && ((Boolean) value).booleanValue();
        } catch (IOException ignored) {
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        emulator.bluetooth.BluetoothBackend backend =
                emulator.bluetooth.BluetoothBackendProvider.getInstanceIfExists();
        if (backend != null) {
            backend.unregisterService(this);
        }
        serverSocket.close();
        System.out.println("[BT] OBEX notifier closed: " + url);
    }

    /**
     * Transport returned from {@link #acceptAndOpen(ServerRequestHandler)}.
     * The server handler owns the OBEX framing; callers can still close the
     * underlying connection through the standard JSR-82 return value.
     */
    private static class ObexTransportConnection implements javax.microedition.io.StreamConnection {
        private final Socket socket;
        private final String url;

        ObexTransportConnection(Socket socket, String url) {
            this.socket = socket;
            this.url = url;
        }

        @Override public InputStream openInputStream() throws IOException { return socket.getInputStream(); }
        @Override public DataInputStream openDataInputStream() throws IOException { return new DataInputStream(openInputStream()); }
        @Override public OutputStream openOutputStream() throws IOException { return socket.getOutputStream(); }
        @Override public DataOutputStream openDataOutputStream() throws IOException { return new DataOutputStream(openOutputStream()); }
        @Override public void close() throws IOException { socket.close(); }
    }
}
