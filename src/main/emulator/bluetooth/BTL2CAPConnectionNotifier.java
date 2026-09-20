package emulator.bluetooth;

import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server notifier for L2CAP emulated over TCP.
 *
 * <p>The MTUs requested in the server URL are retained for the accepted
 * connection rather than silently falling back to the default values.</p>
 */
public class BTL2CAPConnectionNotifier implements L2CAPConnectionNotifier {

    private final ServerSocket serverSocket;
    private final String url;
    private final String psm;
    private final String serviceName;
    private final int receiveMTU;
    private final int transmitMTU;
    private boolean closed = false;

    public BTL2CAPConnectionNotifier(ServerSocket serverSocket, String url, String psm, String serviceName) {
        this(serverSocket, url, psm, serviceName,
                BluetoothConstants.DEFAULT_MTU, BluetoothConstants.DEFAULT_MTU);
    }

    public BTL2CAPConnectionNotifier(ServerSocket serverSocket, String url, String psm, String serviceName,
                                     int receiveMTU, int transmitMTU) {
        this.serverSocket = serverSocket;
        this.url = url;
        this.psm = psm;
        this.serviceName = serviceName;
        this.receiveMTU = receiveMTU;
        this.transmitMTU = transmitMTU;
    }

    @Override
    public L2CAPConnection acceptAndOpen() throws IOException {
        if (closed) throw new IOException("Notifier closed");
        System.out.println("[BT] L2CAP notifier waiting: " + url + " port " + serverSocket.getLocalPort());
        Socket client = serverSocket.accept();
        System.out.println("[BT] L2CAP client connected: " + client.getInetAddress());
        return new BTL2CAPConnection(client, receiveMTU, transmitMTU, url);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        BluetoothBackend backend = BluetoothBackendProvider.getInstanceIfExists();
        if (backend != null) {
            backend.unregisterService(this);
        }
        serverSocket.close();
        System.out.println("[BT] L2CAP notifier closed: " + url);
    }
}
