package emulator;

import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class NetworkLog {

    public static void init() {

    }

    public static void openFailed(String url, String reason) {
        // TODO log fail
    }

    public static void checkOpen(String url, String perm) {
        if (!Permission.returnPermission(url)) {
            openFailed(url, "Security");
            throw new SecurityException(perm);
        }
    }

    public static Connection created(String url, Connection c) {
        if (c instanceof HttpConnection) {

        } else {
            // TODO log connection open
        }
        return c;
    }

    public static void httpConnected(HttpConnection c, HttpURLConnection client) {
        // TODO log headers and stuff
    }

    public static InputStream httpInput(HttpConnection c, InputStream stream) {
        // TODO log read data
        return stream;
    }
}
