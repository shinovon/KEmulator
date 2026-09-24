package emulator.bluetooth;

import emulator.AppSettings;
import emulator.Settings;

/**
 * Host-side configuration shared by Bluetooth backends.
 *
 * <p>Values can be supplied as JVM {@code -D} properties, per-application or
 * persisted global system-property maps, or matching environment variables.
 * JVM properties win so a launch command can override saved configuration.
 * These are intentionally host settings rather than JSR-82 properties exposed
 * to a MIDlet.</p>
 */
final class BluetoothConfiguration {

    private static final String ENV_DISCOVERY_PORT = "KEM_BT_DISCOVERY_PORT";
    private static final String ENV_SDP_PORT = "KEM_BT_SDP_PORT";
    private static final String ENV_MANUAL_PEERS = "KEM_BT_PEERS";
    private static final String ENV_BACKEND = "KEM_BT_BACKEND";

    private BluetoothConfiguration() {}

    static int getDiscoveryPort() {
        int port = getPort(BluetoothConstants.PROP_DISCOVERY_PORT, ENV_DISCOVERY_PORT,
                BluetoothConstants.DEFAULT_DISCOVERY_PORT, false);
        if (port != BluetoothConstants.DEFAULT_DISCOVERY_PORT) {
            System.out.println("[BT] WARNING: custom discovery port " + port +
                    " is active. Automatic discovery will not work with peers using a " +
                    "different port; every participating emulator must use " +
                    BluetoothConstants.PROP_DISCOVERY_PORT + "=" + port + ".");
        }
        return port;
    }

    static int getSdpPort() {
        return getPort(BluetoothConstants.PROP_SDP_PORT, ENV_SDP_PORT,
                BluetoothConstants.DEFAULT_SDP_SERVER_PORT, true);
    }

    static String getManualPeers() {
        return getValue(BluetoothConstants.PROP_MANUAL_PEERS, ENV_MANUAL_PEERS);
    }

    static String getBackendClassName() {
        return getValue(BluetoothConstants.PROP_BACKEND, ENV_BACKEND);
    }

    static String getValue(String property, String environment) {
        return get(property, environment);
    }

    private static int getPort(String property, String environment, int defaultValue, boolean allowZero) {
        String value = get(property, environment);
        if (value == null) return defaultValue;
        try {
            int port = Integer.parseInt(value);
            if ((allowZero && port == 0) || (port >= 1 && port <= 65535)) {
                return port;
            }
        } catch (NumberFormatException ignored) {}

        System.out.println("[BT] Invalid " + property + " value '" + value +
                "'; using " + defaultValue + ".");
        return defaultValue;
    }

    private static String get(String property, String environment) {
        String value = systemProperty(property);
        if (!isBlank(value)) return normalize(value);

        // An explicit map entry wins even when its ':' indirection resolves
        // to null, matching the existing CustomMethod property semantics.
        if (AppSettings.systemProperties.containsKey(property)) {
            return normalize(configuredProperty(AppSettings.systemProperties.get(property)));
        }
        if (Settings.systemProperties.containsKey(property)) {
            return normalize(configuredProperty(Settings.systemProperties.get(property)));
        }

        try {
            return normalize(System.getenv(environment));
        } catch (SecurityException ignored) {
            return null;
        }
    }

    private static String normalize(String value) {
        if (value == null) return null;
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private static String systemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException ignored) {
            return null;
        }
    }

    // Settings maps use the same ':' indirection convention as CustomMethod.
    private static String configuredProperty(String value) {
        if (value == null || !value.startsWith(":")) return value;
        String target = value.substring(1);
        return "null".equals(target) ? null : systemProperty(target);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
