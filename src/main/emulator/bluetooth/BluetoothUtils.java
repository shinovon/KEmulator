package emulator.bluetooth;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Utility methods for Bluetooth emulation.
 */
public final class BluetoothUtils {

    private static final Random RANDOM = new Random();

    private BluetoothUtils() {}

    /**
     * Generate a random Bluetooth address (12 hex digits, uppercase).
     * The address is formatted as 12 hex chars without colons, as per JSR-82.
     */
    public static String generateRandomAddress() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 6; i++) {
            int b = RANDOM.nextInt(256);
            sb.append(String.format("%02X", b));
        }
        // Ensure locally administered and unicast
        // Not strictly required for emulation, but nice to have
        return sb.toString();
    }

    /**
     * Validate BT address format: 12 hex digits.
     */
    public static boolean isValidBtAddress(String addr) {
        if (addr == null) return false;
        if (addr.length() != 12) return false;
        for (int i = 0; i < 12; i++) {
            char c = addr.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Normalize address to uppercase.
     */
    public static String normalizeAddress(String addr) {
        if (addr == null) return null;
        return addr.toUpperCase();
    }

    /**
     * Returns a stable lookup key for a Bluetooth service identifier.
     *
     * JSR-82 applications commonly use an undashed 32-hex-digit UUID in a
     * connection URL, while this emulator's UUID object exposes the same UUID
     * with dashes.  SDP matching must regard those representations as equal.
     * Short UUIDs are expanded to their Bluetooth-base 128-bit form as well.
     * Non-UUID identifiers (for example an implementation-specific PSM) stay
     * case-insensitive strings.
     */
    public static String normalizeServiceIdentifier(String identifier) {
        if (identifier == null) return null;

        String normalized = identifier.trim().toUpperCase();
        String hex = normalized.replace("-", "");
        if (!isHexadecimal(hex)) return normalized;

        try {
            if (hex.length() <= 8) {
                return new javax.bluetooth.UUID(hex, true).toString128();
            }
            if (hex.length() == 32) {
                return new javax.bluetooth.UUID(hex, false).toString128();
            }
        } catch (IllegalArgumentException ignored) {
            // Keep an unrecognised identifier as a case-insensitive string.
        }
        return normalized;
    }

    private static boolean isHexadecimal(String value) {
        if (value == null || value.isEmpty()) return false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get local IP address that is reachable on LAN.
     * Prefers non-loopback IPv4.
     */
    public static InetAddress getLocalInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr;
                    }
                }
            }
            // Fallback to localhost
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            try {
                return InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException ex) {
                return null;
            }
        }
    }

    /**
     * True for the loopback addresses emulator instances can be reached at on
     * the same machine, and for a missing address. A loopback (or unknown)
     * address must never replace an address that is known to be routable.
     */
    public static boolean isLoopbackAddress(String ip) {
        if (ip == null || ip.isEmpty()) return true;
        return ip.startsWith("127.") || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

    /**
     * Get local IP as string.
     */
    public static String getLocalIpString() {
        InetAddress addr = getLocalInetAddress();
        return addr != null ? addr.getHostAddress() : "127.0.0.1";
    }

    /**
     * Returns every IPv4 directed-broadcast address for active LAN interfaces.
     * Sending to these addresses reaches a local subnet even on networks that
     * discard the limited broadcast address (255.255.255.255).
     */
    public static List<InetAddress> getBroadcastAddresses() {
        Set<InetAddress> addresses = new LinkedHashSet<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) return new ArrayList<>(addresses);

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast instanceof Inet4Address) {
                        addresses.add(broadcast);
                    }
                }
            }
        } catch (SocketException ignored) {
            // The limited broadcast and multicast fallbacks are still used.
        }
        return new ArrayList<>(addresses);
    }

    /**
     * Parse BT URL of form btspp://hostname:uuid;params or btl2cap://hostname:psm;params
     * Returns array: [protocol, hostname, channel/uuid, paramsString]
     */
    public static ParsedUrl parseBtUrl(String url) throws IOException {
        if (url == null) throw new IOException("Null URL");
        int protoEnd = url.indexOf("://");
        if (protoEnd < 0) throw new IOException("Invalid URL, no protocol: " + url);
        String protocol = url.substring(0, protoEnd).toLowerCase();
        if (!protocol.equals("btspp") && !protocol.equals("btl2cap") && !protocol.equals("btgoep")) {
            throw new IOException("Unsupported BT protocol: " + protocol);
        }
        String rest = url.substring(protoEnd + 3);
        // Split params
        String paramsStr = "";
        int semicolon = rest.indexOf(';');
        String hostPart;
        if (semicolon >= 0) {
            hostPart = rest.substring(0, semicolon);
            paramsStr = rest.substring(semicolon + 1);
        } else {
            hostPart = rest;
        }
        // hostPart is hostname:channel
        int colon = hostPart.lastIndexOf(':');
        if (colon < 0) throw new IOException("Invalid BT URL, no colon: " + url);
        String hostname = hostPart.substring(0, colon);
        String channel = hostPart.substring(colon + 1);
        if (hostname.isEmpty() || channel.isEmpty()) {
            throw new IOException("Invalid BT URL, empty host or channel: " + url);
        }
        return new ParsedUrl(protocol, hostname, channel, paramsStr, url);
    }

    public static class ParsedUrl {
        public final String protocol;
        public final String hostname;
        public final String channel; // uuid or psm or channel
        public final String params;
        public final String fullUrl;

        ParsedUrl(String protocol, String hostname, String channel, String params, String fullUrl) {
            this.protocol = protocol;
            this.hostname = hostname;
            this.channel = channel;
            this.params = params;
            this.fullUrl = fullUrl;
        }

        public String getParam(String key) {
            if (params == null || params.isEmpty()) return null;
            String[] parts = params.split(";");
            for (String p : parts) {
                int eq = p.indexOf('=');
                if (eq > 0) {
                    String k = p.substring(0, eq).trim();
                    String v = p.substring(eq + 1).trim();
                    if (k.equalsIgnoreCase(key)) return v;
                } else {
                    if (p.trim().equalsIgnoreCase(key)) return "true";
                }
            }
            return null;
        }

        public boolean isServer() {
            return hostname.equalsIgnoreCase("localhost");
        }
    }

}
