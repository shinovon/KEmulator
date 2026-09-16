package javax.bluetooth;

import javax.microedition.io.Connection;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Represents a remote Bluetooth device.
 * Extended with LAN emulation fields (IP, SDP port).
 */
public class RemoteDevice {

    // Cache of known devices: btAddress -> RemoteDevice
    private static final Hashtable<String, RemoteDevice> deviceCache = new Hashtable<>();

    protected final String btAddress; // 12 hex chars uppercase
    protected String friendlyName;
    protected boolean trusted = false;
    protected boolean authenticated = false;
    protected boolean encrypted = false;

    // Emulation extension - not part of spec but useful
    protected String ipAddress;
    protected int sdpPort;
    protected int deviceClassInt;

    protected RemoteDevice(final String address) {
        if (address == null) throw new NullPointerException();
        String norm = address.toUpperCase();
        // Validate 12 hex
        if (norm.length() != 12) {
            throw new IllegalArgumentException("Invalid BT address: " + address);
        }
        this.btAddress = norm;
        this.friendlyName = norm; // default
        synchronized (deviceCache) {
            deviceCache.put(norm, this);
        }
    }

    // Internal constructor with extra info
    protected RemoteDevice(String address, String friendlyName, String ipAddress, int sdpPort, int deviceClass) {
        this(address);
        if (friendlyName != null) this.friendlyName = friendlyName;
        this.ipAddress = ipAddress;
        this.sdpPort = sdpPort;
        this.deviceClassInt = deviceClass;
    }

    public boolean isTrustedDevice() {
        return trusted;
    }

    public String getFriendlyName(final boolean alwaysAsk) throws IOException {
        if (friendlyName != null && !alwaysAsk) {
            return friendlyName;
        }
        // In emulation, we already have friendly name from discovery
        if (friendlyName != null) return friendlyName;
        return btAddress;
    }

    public final String getBluetoothAddress() {
        return btAddress;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof RemoteDevice) {
            return btAddress.equals(((RemoteDevice) obj).btAddress);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return btAddress.hashCode();
    }

    public static RemoteDevice getRemoteDevice(final Connection conn) throws IOException {
        if (conn == null) throw new NullPointerException();
        // Try to extract BT address from connection
        // For our emulated connections, we store URL
        if (conn instanceof emulator.bluetooth.BTSPPConnection) {
            String url = ((emulator.bluetooth.BTSPPConnection) conn).getUrl();
            try {
                emulator.bluetooth.BluetoothUtils.ParsedUrl parsed = emulator.bluetooth.BluetoothUtils.parseBtUrl(url);
                String addr = parsed.hostname;
                if (emulator.bluetooth.BluetoothUtils.isValidBtAddress(addr)) {
                    return getOrCreate(addr);
                }
            } catch (Exception ignored) {}
        }
        if (conn instanceof emulator.bluetooth.BTL2CAPConnection) {
            // Similar
            return null;
        }
        // For other connections, try to lookup cache
        return null;
    }

    public boolean authenticate() throws IOException {
        // In emulation, always succeed
        authenticated = true;
        return true;
    }

    public boolean authorize(final Connection conn) throws IOException {
        if (conn == null) throw new NullPointerException();
        return true;
    }

    public boolean encrypt(final Connection conn, final boolean on) throws IOException {
        if (conn == null) throw new NullPointerException();
        encrypted = on;
        return true;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isAuthorized(final Connection conn) throws IOException {
        if (conn == null) throw new NullPointerException();
        return true;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    // --- Emulation helpers ---

    public static RemoteDevice getOrCreate(String address) {
        synchronized (deviceCache) {
            RemoteDevice dev = deviceCache.get(address.toUpperCase());
            if (dev != null) return dev;
            return new RemoteDevice(address);
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getSdpPort() {
        return sdpPort;
    }

    @Override
    public String toString() {
        return "RemoteDevice[" + btAddress + " (" + friendlyName + ")]";
    }
}
