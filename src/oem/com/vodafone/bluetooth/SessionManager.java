package com.vodafone.bluetooth;

import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;

/**
 * Manages client-side Bluetooth session (connects to RemoteService).
 */
public class SessionManager extends SessionBase {

    public SessionManager(SessionListener listener) throws NullPointerException {
        super(listener);
    }

    public final int open(RemoteService service) throws SecurityException, NullPointerException, IOException {
        if (service == null) throw new NullPointerException();
        ServiceRecord rec = service.getServiceRecord();
        String url = rec != null ? rec.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false) : service.getServiceID();
        return openByUrl(url);
    }

    public final int openSecured(RemoteService service, boolean auth) throws SecurityException, NullPointerException, IOException {
        if (service == null) throw new NullPointerException();
        ServiceRecord rec = service.getServiceRecord();
        String url = rec != null ? rec.getConnectionURL(
                auth ? ServiceRecord.AUTHENTICATE_NOENCRYPT : ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false) : service.getServiceID();
        return openByUrl(url);
    }

    private int openByUrl(String url) throws IOException {
        if (url == null) throw new IOException("Invalid service URL");
        // url might be btspp://... - use Connector
        try {
            StreamConnection conn = (StreamConnection) Connector.open(url);
            setupConnection(conn);
            return 0;
        } catch (IOException e) {
            try {
                listener.gotConnectionStatus(0, SessionListener.CONN_FAILED);
            } catch (Exception ignored) {}
            throw e;
        }
    }
}
