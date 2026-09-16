package com.vodafone.bluetooth;

import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;

/**
 * Server-side session (waits for clients).
 */
public class SessionMember extends SessionBase {

    private StreamConnectionNotifier notifier;
    private Thread acceptThread;

    public SessionMember(SessionListener listener) throws NullPointerException {
        super(listener);
    }

    public final int open(LocalService service) throws SecurityException, NullPointerException, IllegalStateException, IOException {
        if (service == null) throw new NullPointerException();
        String uuid = service.getServiceID();
        if (uuid == null || uuid.isEmpty()) uuid = "1101";
        String name = service.getServiceName();
        if (name == null) name = "VodafoneService";
        String url = "btspp://localhost:" + uuid + ";name=" + name;
        return openByUrl(url);
    }

    public final int openSecured(LocalService service, boolean auth, boolean encrypt) throws SecurityException, NullPointerException, IllegalStateException, IOException {
        if (service == null) throw new NullPointerException();
        String uuid = service.getServiceID();
        if (uuid == null || uuid.isEmpty()) uuid = "1101";
        String name = service.getServiceName();
        if (name == null) name = "VodafoneService";
        String url = "btspp://localhost:" + uuid + ";name=" + name;
        if (auth) url += ";authenticate=true";
        if (encrypt) url += ";encrypt=true";
        return openByUrl(url);
    }

    private int openByUrl(String url) throws IOException {
        notifier = (StreamConnectionNotifier) Connector.open(url);
        // Accept in background
        acceptThread = new Thread(() -> {
            try {
                StreamConnection conn = notifier.acceptAndOpen();
                setupConnection(conn);
            } catch (IOException e) {
                if (connected) {
                    try {
                        listener.gotConnectionStatus(0, SessionListener.CONN_FAILED);
                    } catch (Exception ignored) {}
                }
            }
        }, "Vodafone-BT-Accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
        return 0;
    }

    @Override
    public boolean close(int reason) {
        if (notifier != null) {
            try {
                notifier.close();
            } catch (IOException ignored) {}
            notifier = null;
        }
        if (acceptThread != null) {
            acceptThread.interrupt();
            acceptThread = null;
        }
        return super.close(reason);
    }

    public String getBluetoothAddress(int memberId) {
        // In emulation, return local address or connected peer?
        try {
            return javax.bluetooth.LocalDevice.getLocalDevice().getBluetoothAddress();
        } catch (Exception e) {
            return "000000000000";
        }
    }
}
