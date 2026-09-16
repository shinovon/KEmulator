package emulator.bluetooth;

/**
 * Constants for Bluetooth LAN emulation.
 * All communication is done via UDP discovery and TCP service channels.
 */
public final class BluetoothConstants {

    private BluetoothConstants() {}

    /** Default UDP port for discovery broadcast and multicast. */
    public static final int DEFAULT_DISCOVERY_PORT = 63520;

    /** Default TCP SDP port; zero asks the operating system for a free port. */
    public static final int DEFAULT_SDP_SERVER_PORT = 0;

    /**
     * @deprecated The LAN backend reads {@link #PROP_DISCOVERY_PORT}; this is
     * only the source-compatible default value.
     */
    @Deprecated
    public static final int DISCOVERY_PORT = DEFAULT_DISCOVERY_PORT;

    /**
     * @deprecated The LAN backend reads {@link #PROP_SDP_PORT}; this is only
     * the source-compatible default value.
     */
    @Deprecated
    public static final int SDP_SERVER_PORT = DEFAULT_SDP_SERVER_PORT;

    /** Multicast group for discovery (local network). */
    public static final String DISCOVERY_MULTICAST_GROUP = "239.255.10.10";

    /** Discovery packet magic header. */
    public static final String MAGIC = "KEM_BT";

    /** Discovery message types. */
    public static final String TYPE_DISCOVER_REQ = "DISCOVER_REQ";
    public static final String TYPE_DISCOVER_RESP = "DISCOVER_RESP";
    public static final String TYPE_BYE = "BYE";
    public static final String TYPE_SERVICE_SEARCH = "SERVICE_SEARCH";

    /** Default inquiry duration in ms. */
    public static final int DEFAULT_INQUIRY_DURATION_MS = 8000;

    /** Service search timeout. */
    public static final int SERVICE_SEARCH_TIMEOUT_MS = 5000;

    /** Default MTU for L2CAP. */
    public static final int DEFAULT_MTU = 672;

    /** Minimum MTU. */
    public static final int MINIMUM_MTU = 48;

    /** Default device class (Computer). */
    public static final int DEFAULT_DEVICE_CLASS = 0x100;

    /** Default local BT address if not set. */
    public static final String DEFAULT_LOCAL_ADDRESS = "001122AABBCC";

    /** Standard Bluetooth-facing system properties. */
    public static final String PROP_BT_ADDRESS = "bluetooth.address";
    public static final String PROP_BT_NAME = "bluetooth.friendly.name";
    public static final String PROP_BT_DISCOVERABLE = "bluetooth.discoverable";

    /**
     * Optional host configuration: UDP port used by LAN discovery.
     * Every peer that relies on automatic discovery must use the same value.
     */
    public static final String PROP_DISCOVERY_PORT = "kemulator.bluetooth.discovery.port";

    /** Optional host configuration: fixed TCP port for the local SDP server. */
    public static final String PROP_SDP_PORT = "kemulator.bluetooth.sdp.port";

    /**
     * Optional host configuration for preknown peers. Entries use
     * {@code BT_ADDRESS@host:sdpPort}, separated by commas or semicolons.
     */
    public static final String PROP_MANUAL_PEERS = "kemulator.bluetooth.peers";

    /** Optional host configuration for selecting a {@link BluetoothBackend}. */
    public static final String PROP_BACKEND = "kemulator.bluetooth.backend";
}
