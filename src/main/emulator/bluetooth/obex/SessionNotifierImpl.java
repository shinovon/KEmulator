package emulator.bluetooth.obex;

import javax.microedition.io.Connection;
import javax.obex.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Server notifier for OBEX (btgoep) emulated over TCP.
 * Accepts connections and delegates to ServerRequestHandler.
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
        System.out.println("[BT] OBEX notifier waiting: " + url + " port " + serverSocket.getLocalPort());
        Socket client = serverSocket.accept();
        System.out.println("[BT] OBEX client connected: " + client.getInetAddress());

        // Handle OBEX session in background thread that uses handler
        // For JSR-82, acceptAndOpen should return a Connection that represents the transport?
        // Actually spec says it returns Connection (the underlying RFCOMM connection) and handler will be called for OBEX requests.
        // For simplicity, we start a thread that handles OBEX protocol and calls handler, and return a dummy connection that wraps socket.

        // Start OBEX handler thread
        Thread t = new Thread(() -> handleObexSession(client, handler, auth), "KEm-BT-OBEX-" + client.getPort());
        t.setDaemon(true);
        t.start();

        // Return a connection that represents this client session (for API compatibility)
        // We'll return a ClientSessionImpl-like object but for server side we return the socket connection
        return new ObexTransportConnection(client, url);
    }

    private void handleObexSession(Socket socket, ServerRequestHandler handler, Authenticator auth) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            socket.setTcpNoDelay(true);
            long connectionId = System.currentTimeMillis();

            while (!socket.isClosed()) {
                int headerCount;
                try {
                    // Try to read next request - we need to handle our simple protocol
                    // First byte is type, but we wrote length placeholder earlier as 0 - we need to adjust
                    // For simplicity, read as: we expect client to send header count directly (since we simplified)
                    // Actually ClientSessionImpl sends: int placeholder, byte type, int headerCount...
                    // Let's read placeholder int (ignore), then byte type
                    int placeholder = in.readInt(); // ignore
                    byte type = in.readByte();
                    headerCount = in.readInt();

                    Map<Integer, Object> headersMap = new HashMap<>();
                    for (int i = 0; i < headerCount; i++) {
                        int id = in.readInt();
                        String valStr = in.readUTF();
                        String className = in.readUTF();
                        headersMap.put(id, valStr);
                    }
                    int dataLen = in.readInt();
                    byte[] data = new byte[dataLen];
                    if (dataLen > 0) in.readFully(data);

                    HeaderSetImpl reqHeaders = new HeaderSetImpl();
                    reqHeaders.setHeaders(headersMap);
                    HeaderSetImpl respHeaders = new HeaderSetImpl();

                    int responseCode = ResponseCodes.OBEX_HTTP_OK;

                    switch (type) {
                        case 0: // CONNECT
                            responseCode = handler.onConnect(reqHeaders, respHeaders);
                            respHeaders.setHeader(HeaderSet.WHO, String.valueOf(connectionId));
                            break;
                        case 1: // DISCONNECT
                            handler.onDisconnect(reqHeaders, respHeaders);
                            responseCode = ResponseCodes.OBEX_HTTP_OK;
                            break;
                        case 2: // PUT
                            OperationImpl putOp = new OperationImpl(reqHeaders, data, true);
                            responseCode = handler.onPut(putOp);
                            // Send response headers
                            break;
                        case 3: // GET
                            // For GET, handler.onGet will be called with operation that can write data
                            OperationImpl getOp = new OperationImpl(reqHeaders, new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream(), false);
                            responseCode = handler.onGet(getOp);
                            byte[] outData = getOp.getOutputData();
                            // We'll send headers + data in response
                            // For GET, we need to send data back
                            // Our protocol: after headers, send response code, then data
                            // We'll handle below
                            respHeaders = getOp.getSentHeaders() != null ? getOp.getSentHeaders() : respHeaders;
                            // Store outData to send
                            data = outData;
                            break;
                        case 4: // SETPATH
                            boolean backup = false, create = false;
                            Object b = headersMap.get(0x100);
                            Object c = headersMap.get(0x101);
                            if (b instanceof String) backup = Boolean.parseBoolean((String) b);
                            if (c instanceof String) create = Boolean.parseBoolean((String) c);
                            responseCode = handler.onSetPath(reqHeaders, respHeaders, backup, create);
                            break;
                        case 5: // DELETE
                            responseCode = handler.onDelete(reqHeaders, respHeaders);
                            break;
                        default:
                            responseCode = ResponseCodes.OBEX_HTTP_BAD_REQUEST;
                    }

                    // Send response: header count, headers, response code
                    synchronized (out) {
                        Map<Integer, Object> respMap = respHeaders.getHeaders();
                        out.writeInt(respMap.size());
                        for (Map.Entry<Integer, Object> e : respMap.entrySet()) {
                            out.writeInt(e.getKey());
                            out.writeUTF(e.getValue() != null ? e.getValue().toString() : "");
                            out.writeUTF(e.getValue() != null ? e.getValue().getClass().getName() : "null");
                        }
                        out.writeInt(responseCode);
                        if (type == 3) { // GET returns data
                            out.writeInt(data != null ? data.length : 0);
                            if (data != null && data.length > 0) out.write(data);
                        }
                        out.flush();
                    }

                    if (type == 1) break; // disconnect

                } catch (EOFException e) {
                    break;
                }
            }

        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        emulator.bluetooth.BluetoothStack stack = emulator.bluetooth.BluetoothStack.getInstanceIfExists();
        if (stack != null) {
            stack.unregisterService(this);
        }
        serverSocket.close();
        System.out.println("[BT] OBEX notifier closed: " + url);
    }

    /**
     * Dummy transport connection returned by acceptAndOpen.
     * In real JSR-82, it returns a StreamConnection, but spec says Connection.
     * We'll return a simple StreamConnection that wraps the socket, so MIDlet can close it.
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
