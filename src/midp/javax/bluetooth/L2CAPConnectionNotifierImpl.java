package javax.bluetooth;

import emulator.bluetooth.BluetoothStack;
import javax.microedition.io.Connection;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Implementation of L2CAPConnectionNotifier.
 * Used for server side btl2cap:// URLs.
 */
public class L2CAPConnectionNotifierImpl implements L2CAPConnectionNotifier {

    private final emulator.bluetooth.BTL2CAPConnectionNotifier impl;
    private final String url;

    public L2CAPConnectionNotifierImpl(ServerSocket ss, String url, String psm, String name) {
        this.impl = new emulator.bluetooth.BTL2CAPConnectionNotifier(ss, url, psm, name);
        this.url = url;
    }

    @Override
    public L2CAPConnection acceptAndOpen() throws IOException {
        return impl.acceptAndOpen();
    }

    @Override
    public void close() throws IOException {
        impl.close();
    }

    public static Connection open(String url) throws IOException {
        try {
            BluetoothStack stack = BluetoothStack.getInstance();
            return stack.openServerNotifier(url);
        } catch (BluetoothStateException e) {
            throw new IOException(e.getMessage());
        }
    }
}
