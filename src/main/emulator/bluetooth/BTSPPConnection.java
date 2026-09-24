package emulator.bluetooth;

import javax.microedition.io.StreamConnection;
import java.io.*;

/**
 * Emulated BTSPP (RFCOMM) connection over TCP.
 * Implements StreamConnection for J2ME compatibility.
 */
public class BTSPPConnection implements StreamConnection {

    private final java.net.Socket socket;
    private final String url;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean closed = false;

    public BTSPPConnection(java.net.Socket socket, String url) throws IOException {
        this.socket = socket;
        this.url = url;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        // Disable Nagle for low latency (important for games)
        try {
            socket.setTcpNoDelay(true);
        } catch (Exception ignored) {}
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (closed) throw new IOException("Connection closed");
        return inputStream;
    }

    @Override
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        if (closed) throw new IOException("Connection closed");
        return outputStream;
    }

    @Override
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        try {
            inputStream.close();
        } catch (IOException ignored) {}
        try {
            outputStream.close();
        } catch (IOException ignored) {}
        socket.close();
        System.out.println("[BT] BTSPP connection closed: " + url);
    }

    /**
     * For internal use - get raw socket.
     */
    public java.net.Socket getSocket() {
        return socket;
    }

    public String getUrl() {
        return url;
    }
}
