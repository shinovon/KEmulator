package emulator.bluetooth;

import java.net.InetAddress;

/**
 * Constants for Bluetooth LAN emulation.
 * All communication is done via UDP discovery and TCP service channels.
 */
public final class BluetoothConstants {

    private BluetoothConstants() {}

    /** UDP port for device discovery (broadcast + multicast) */
    public static final int DISCOVERY_PORT = 63520;

    /** Multicast group for discovery (local network) */
    public static final String DISCOVERY_MULTICAST_GROUP = "239.255.10.10";

    /** TCP port range for SDP server - 0 means random free port */
    public static final int SDP_SERVER_PORT = 0;

    /** Discovery packet magic header */
    public static final String MAGIC = "KEM_BT";

    /** Discovery message types */
    public static final String TYPE_DISCOVER_REQ = "DISCOVER_REQ";
    public static final String TYPE_DISCOVER_RESP = "DISCOVER_RESP";
    public static final String TYPE_BYE = "BYE";
    public static final String TYPE_SERVICE_SEARCH = "SERVICE_SEARCH";

    /** Default inquiry duration in ms */
    public static final int DEFAULT_INQUIRY_DURATION_MS = 8000;

    /** Service search timeout */
    public static final int SERVICE_SEARCH_TIMEOUT_MS = 5000;

    /** Default MTU for L2CAP */
    public static final int DEFAULT_MTU = 672;

    /** Minimum MTU */
    public static final int MINIMUM_MTU = 48;

    /** Default device class (Computer) */
    public static final int DEFAULT_DEVICE_CLASS = 0x100;

    /** Default local BT address if not set */
    public static final String DEFAULT_LOCAL_ADDRESS = "001122AABBCC";

    /** System properties */
    public static final String PROP_BT_ADDRESS = "bluetooth.address";
    public static final String PROP_BT_NAME = "bluetooth.friendly.name";
    public static final String PROP_BT_DISCOVERABLE = "bluetooth.discoverable";
}
