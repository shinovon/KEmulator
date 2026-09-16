package emulator.bluetooth;

import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRecordImpl;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Represents a local service advertised via SDP.
 * Holds TCP server socket and service record.
 */
public class BluetoothService {

    private final String protocol; // btspp, btl2cap, btgoep
    private final String uuidOrPsm; // UUID string or PSM decimal
    private final String serviceName;
    private final ServerSocket serverSocket;
    private final int tcpPort;
    private final ServiceRecordImpl serviceRecord;
    private final String notifierUrl; // original URL used to create notifier
    private final Object notifier; // the notifier object (for getRecord)
    private volatile boolean closed = false;

    public BluetoothService(String protocol, String uuidOrPsm, String serviceName,
                            ServerSocket serverSocket, String notifierUrl,
                            Object notifier, ServiceRecordImpl record) {
        this.protocol = protocol;
        this.uuidOrPsm = uuidOrPsm;
        this.serviceName = serviceName;
        this.serverSocket = serverSocket;
        this.tcpPort = serverSocket.getLocalPort();
        this.notifierUrl = notifierUrl;
        this.notifier = notifier;
        this.serviceRecord = record;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUuidOrPsm() {
        return uuidOrPsm;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public ServiceRecordImpl getServiceRecord() {
        return serviceRecord;
    }

    public String getNotifierUrl() {
        return notifierUrl;
    }

    public Object getNotifier() {
        return notifier;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        closed = true;
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
    }

    /**
     * Generate connection URL that remote clients can use.
     * For btspp: btspp://<localBtAddr>:<uuid>;name=<serviceName>
     */
    public String generateConnectionUrl(String localBtAddress) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(localBtAddress).append(":").append(uuidOrPsm);
        if (serviceName != null && !serviceName.isEmpty()) {
            sb.append(";name=").append(serviceName);
        }
        // Add internal hint for emulator to map to TCP port (not part of spec, but useful)
        // We store mapping separately, so URL stays spec-compliant.
        return sb.toString();
    }
}
