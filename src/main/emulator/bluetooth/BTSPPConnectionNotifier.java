package emulator.bluetooth;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server notifier for BTSPP (RFCOMM) emulated over TCP.
 */
public class BTSPPConnectionNotifier implements StreamConnectionNotifier {

    private final ServerSocket serverSocket;
    private final String url;
    private final String uuid;
    private final String serviceName;
    private boolean closed = false;

    public BTSPPConnectionNotifier(ServerSocket serverSocket, String url, String uuid, String serviceName) {
        this.serverSocket = serverSocket;
        this.url = url;
        this.uuid = uuid;
        this.serviceName = serviceName;
    }

    @Override
    public StreamConnection acceptAndOpen() throws IOException {
        if (closed) throw new IOException("Notifier closed");
        System.out.println("[BT] BTSPP notifier waiting for connection: " + url + " on port " + serverSocket.getLocalPort());
        Socket client = serverSocket.accept();
        System.out.println("[BT] BTSPP client connected from " + client.getInetAddress() + ":" + client.getPort());
        return new BTSPPConnection(client, url);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        // Unregister through the selected backend rather than assuming LAN.
        BluetoothBackend backend = BluetoothBackendProvider.getInstanceIfExists();
        if (backend != null) {
            backend.unregisterService(this);
        }
        serverSocket.close();
        System.out.println("[BT] BTSPP notifier closed: " + url);
    }

    public String getUrl() {
        return url;
    }
}
