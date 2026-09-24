package javax.bluetooth;

import emulator.bluetooth.BluetoothBackend;
import emulator.bluetooth.BluetoothBackendProvider;
import emulator.bluetooth.BluetoothUtils;
import emulator.bluetooth.BTL2CAPConnection;
import javax.microedition.io.Connection;
import java.io.IOException;

/**
 * L2CAP client connection implementation.
 * Delegates to emulator.bluetooth.BTL2CAPConnection (TCP emulated).
 */
public class L2CAPConnectionImpl implements L2CAPConnection {

    private final BTL2CAPConnection impl;

    protected L2CAPConnectionImpl(final java.net.Socket socket, int receiveMTU, int transmitMTU, String url) throws IOException {
        this.impl = new BTL2CAPConnection(socket, receiveMTU, transmitMTU, url);
    }

    private L2CAPConnectionImpl(BTL2CAPConnection impl) {
        this.impl = impl;
    }

    @Override
    public int getTransmitMTU() throws IOException {
        return impl.getTransmitMTU();
    }

    @Override
    public int getReceiveMTU() throws IOException {
        return impl.getReceiveMTU();
    }

    @Override
    public void send(final byte[] data) throws IOException {
        impl.send(data);
    }

    @Override
    public int receive(final byte[] inBuf) throws IOException {
        return impl.receive(inBuf);
    }

    @Override
    public boolean ready() throws IOException {
        return impl.ready();
    }

    @Override
    public void close() throws IOException {
        impl.close();
    }

    public static Connection open(final String url) throws IOException {
        // Delegate to the selected Bluetooth backend.
        try {
            BluetoothBackend backend = BluetoothBackendProvider.getInstance();
            Connection conn = backend.openClientConnection(url);
            if (conn instanceof L2CAPConnection) {
                return conn;
            }
            if (conn instanceof BTL2CAPConnection) {
                return new L2CAPConnectionImpl((BTL2CAPConnection) conn);
            }
            // If a backend returns a generic connection, retain direct-TCP
            // parsing as a compatibility fallback.
            BluetoothUtils.ParsedUrl parsed = BluetoothUtils.parseBtUrl(url);
            String host = parsed.hostname;
            int port;
            try {
                port = Integer.parseInt(parsed.channel);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid L2CAP URL, channel must be PSM or port: " + url);
            }
            java.net.Socket socket = new java.net.Socket(host, port);
            int recvMTU = 672;
            int transMTU = 672;
            String r = parsed.getParam("ReceiveMTU");
            String t = parsed.getParam("TransmitMTU");
            if (r != null) try { recvMTU = Integer.parseInt(r); } catch (NumberFormatException ignored) {}
            if (t != null) try { transMTU = Integer.parseInt(t); } catch (NumberFormatException ignored) {}
            return new L2CAPConnectionImpl(socket, recvMTU, transMTU, url);
        } catch (BluetoothStateException e) {
            throw new IOException("Bluetooth backend not available: " + e.getMessage());
        }
    }
}
