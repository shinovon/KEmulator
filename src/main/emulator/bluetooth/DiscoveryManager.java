package emulator.bluetooth;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles Bluetooth device discovery over LAN using UDP broadcast/multicast.
 * 
 * Each emulator instance:
 * - Listens on its configured UDP discovery port for requests and multicast traffic
 * - On startInquiry(), broadcasts DISCOVER_REQ from a unique reply port and
 *   collects the unicast DISCOVER_RESP packets sent to that port
 * - On receiving DISCOVER_REQ, responds with DISCOVER_RESP if discoverable
 */
public class DiscoveryManager implements Runnable {

    private static final int DISCOVERY_REQUEST_INTERVAL_MS = 2000;
    private static final int INQUIRY_RECEIVE_TIMEOUT_MS = 250;
    private static final int DISCOVERY_MULTICAST_TTL = 1;

    private final BluetoothStack stack;
    private final int discoveryPort;

    /*
     * A MulticastSocket is also a DatagramSocket, so one socket can receive
     * both UDP broadcasts and packets addressed to the discovery group.
     */
    private volatile MulticastSocket socket;
    private volatile boolean multicastJoined;
    private Thread thread;
    private volatile boolean running = false;

    // Cached peers: btAddress -> peer. Manually configured peers are kept in
    // the same routing table but separately tracked for PREKNOWN semantics.
    private final Map<String, BluetoothPeer> cachedPeers = new ConcurrentHashMap<>();
    private final Set<String> preknownPeerAddresses =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    // Inquiry state
    private volatile boolean inquiryRunning = false;
    private volatile DiscoveryListener inquiryListener;
    private final Set<String> inquiryNotifiedPeers =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Thread inquiryThread;

    public DiscoveryManager(BluetoothStack stack) {
        this.stack = stack;
        this.discoveryPort = BluetoothConfiguration.getDiscoveryPort();
    }

    public synchronized void start() throws IOException {
        if (running) return;

        // Set SO_REUSEADDR before binding so separate KEmulator processes can
        // listen on the same discovery port. MulticastSocket also receives
        // broadcast datagrams, so a second DatagramSocket is not needed.
        socket = new MulticastSocket(null);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        try {
            // Discovery is intentionally link-local.  Keep multicast packets
            // on the LAN and allow local emulator processes to receive them.
            socket.setTimeToLive(DISCOVERY_MULTICAST_TTL);
            socket.setLoopbackMode(false);
        } catch (IOException e) {
            // Directed and limited broadcasts remain available as a fallback.
            System.out.println("[BT] Could not configure multicast options: " + e.getMessage());
        }
        socket.bind(new InetSocketAddress(discoveryPort));
        loadManualPeers();

        try {
            socket.joinGroup(InetAddress.getByName(BluetoothConstants.DISCOVERY_MULTICAST_GROUP));
            multicastJoined = true;
        } catch (IOException e) {
            multicastJoined = false;
            System.out.println("[BT] Multicast not available, using broadcast only: " + e.getMessage());
        }

        running = true;
        thread = new Thread(this, "KEm-BT-Discovery");
        thread.setDaemon(true);
        thread.start();
        System.out.println("[BT] Discovery started on port " + discoveryPort);
    }

    public synchronized void stop() {
        running = false;
        MulticastSocket discoverySocket = socket;
        socket = null;
        if (discoverySocket != null) {
            if (multicastJoined) {
                try {
                    discoverySocket.leaveGroup(InetAddress.getByName(BluetoothConstants.DISCOVERY_MULTICAST_GROUP));
                } catch (IOException ignored) {}
            }
            discoverySocket.close();
        }
        multicastJoined = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        cancelInquiry();
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        while (running) {
            try {
                MulticastSocket discoverySocket = socket;
                if (discoverySocket == null) return;
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                discoverySocket.receive(packet);
                handlePacket(packet);
            } catch (IOException e) {
                if (running) {
                    // e.printStackTrace();
                }
                // Small delay to avoid busy loop on error
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
    }

    private void handlePacket(DatagramPacket packet) {
        String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
        // Expected format: KEM_BT|TYPE|...
        if (!msg.startsWith(BluetoothConstants.MAGIC)) return;
        String[] parts = msg.split("\\|");
        if (parts.length < 2) return;
        String type = parts[1];

        String senderIp = packet.getAddress().getHostAddress();

        if (type.equals(BluetoothConstants.TYPE_DISCOVER_REQ)) {
            // Format: KEM_BT|DISCOVER_REQ|btAddr|friendlyName|ip|sdpPort
            if (parts.length < 6) return;
            String remoteBtAddr = parts[2];
            // Don't respond to ourselves
            if (remoteBtAddr.equalsIgnoreCase(stack.getLocalAddress())) return;
            if (stack.getDiscoverable() == DiscoveryAgent.NOT_DISCOVERABLE) return;

            // Respond with our info
            String resp = String.join("|",
                    BluetoothConstants.MAGIC,
                    BluetoothConstants.TYPE_DISCOVER_RESP,
                    stack.getLocalAddress(),
                    stack.getFriendlyName(),
                    BluetoothUtils.getLocalIpString(),
                    String.valueOf(stack.getSdpServer().getPort()),
                    String.valueOf(stack.getDeviceClass()),
                    String.valueOf(stack.getDiscoverable())
            );
            sendResponse(resp, packet.getAddress(), packet.getPort());
            // Cache the packet source, not the self-reported address.  The
            // source is the endpoint that is demonstrably reachable from this
            // host, which matters on PCs with more than one network adapter.
            try {
                String reqFriendly = parts[3];
                int reqSdp = Integer.parseInt(parts[5]);
                addOrUpdatePeer(remoteBtAddr, reqFriendly, senderIp, reqSdp, 0);
            } catch (Exception ignored) {}

        } else if (type.equals(BluetoothConstants.TYPE_DISCOVER_RESP)) {
            // Format: KEM_BT|DISCOVER_RESP|btAddr|friendlyName|ip|sdpPort|deviceClass|discoverable
            if (parts.length < 6) return;
            String btAddr = parts[2];
            if (btAddr.equalsIgnoreCase(stack.getLocalAddress())) return; // ignore self
            String friendlyName = parts.length > 3 ? parts[3] : btAddr;
            // The UDP source is the route the response actually used.  Do not
            // trust an advertised interface selected by a multi-homed peer.
            String ip = senderIp;
            int sdpPort = 0;
            int devClass = 0;
            try {
                if (parts.length > 5) sdpPort = Integer.parseInt(parts[5]);
                if (parts.length > 6) devClass = Integer.parseInt(parts[6]);
            } catch (NumberFormatException ignored) {}

            BluetoothPeer peer = addOrUpdatePeer(btAddr, friendlyName, ip, sdpPort, devClass);

            // A peer can answer via broadcast, multicast, and loopback. JSR-82
            // reports a discovered device once per inquiry, so suppress repeats.
            if (inquiryRunning && inquiryListener != null &&
                    inquiryNotifiedPeers.add(btAddr.toUpperCase())) {
                try {
                    javax.bluetooth.DeviceClass dc = new javax.bluetooth.DeviceClass(devClass);
                    inquiryListener.deviceDiscovered(peer.getRemoteDevice(), dc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (type.equals(BluetoothConstants.TYPE_BYE)) {
            if (parts.length < 3) return;
            String btAddr = parts[2].toUpperCase();
            // A manually configured endpoint stays addressable even if it
            // broadcasts BYE or is currently offline.
            if (!preknownPeerAddresses.contains(btAddr)) {
                cachedPeers.remove(btAddr);
            }
        }
    }

    /**
     * Loads manually addressable LAN peers. This is useful on networks that
     * block broadcast/multicast or when a remote emulator uses another
     * discovery port. The syntax is a comma- or semicolon-separated list of
     * {@code BT_ADDRESS@host:sdpPort}, for example
     * {@code 001122AABBCC@192.168.1.42:63521}.
     */
    private void loadManualPeers() {
        String configured = BluetoothConfiguration.getManualPeers();
        if (configured == null) return;

        String[] entries = configured.split("[,;]");
        for (String rawEntry : entries) {
            String entry = rawEntry.trim();
            if (entry.isEmpty()) continue;

            int at = entry.indexOf('@');
            int colon = entry.lastIndexOf(':');
            if (at <= 0 || colon <= at + 1 || colon == entry.length() - 1) {
                logInvalidManualPeer(entry);
                continue;
            }

            String address = BluetoothUtils.normalizeAddress(entry.substring(0, at).trim());
            String host = entry.substring(at + 1, colon).trim();
            String portText = entry.substring(colon + 1).trim();
            if (!BluetoothUtils.isValidBtAddress(address) || host.isEmpty()) {
                logInvalidManualPeer(entry);
                continue;
            }
            if (address.equalsIgnoreCase(stack.getLocalAddress())) {
                System.out.println("[BT] Ignoring local address in " + BluetoothConstants.PROP_MANUAL_PEERS);
                continue;
            }

            int sdpPort;
            try {
                sdpPort = Integer.parseInt(portText);
            } catch (NumberFormatException e) {
                logInvalidManualPeer(entry);
                continue;
            }
            if (sdpPort < 1 || sdpPort > 65535) {
                logInvalidManualPeer(entry);
                continue;
            }

            addOrUpdatePeer(address, address, host, sdpPort, 0);
            preknownPeerAddresses.add(address);
            System.out.println("[BT] Configured preknown peer " + address + " at " + host + ":" + sdpPort);
        }
    }

    private void logInvalidManualPeer(String entry) {
        System.out.println("[BT] Ignoring invalid " + BluetoothConstants.PROP_MANUAL_PEERS +
                " entry '" + entry + "' (expected BT_ADDRESS@host:sdpPort)");
    }

    private BluetoothPeer addOrUpdatePeer(String btAddr, String friendlyName, String ip, int sdpPort, int devClass) {
        String key = btAddr.toUpperCase();
        BluetoothPeer existing = cachedPeers.get(key);
        if (existing != null) {
            existing.setFriendlyName(friendlyName);
            existing.setIpAddress(ip);
            if (sdpPort != 0) existing.setSdpPort(sdpPort);
            existing.setDeviceClass(devClass);
            existing.touch();
            return existing;
        } else {
            BluetoothPeer peer = new BluetoothPeer(btAddr, friendlyName, ip, sdpPort, devClass);
            cachedPeers.put(key, peer);
            System.out.println("[BT] Discovered peer: " + peer);
            return peer;
        }
    }

    private void sendResponse(String msg, InetAddress address, int port) {
        DatagramSocket responseSocket = socket;
        if (responseSocket == null || responseSocket.isClosed()) return;
        try {
            byte[] data = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            responseSocket.send(packet);
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Sends a discovery request from the permanent listener socket.
     * Kept public for callers that want to announce themselves outside an
     * active inquiry; normal inquiries use a private, ephemeral reply port.
     */
    public void broadcastDiscoveryRequest() {
        DatagramSocket discoverySocket = socket;
        if (discoverySocket != null && !discoverySocket.isClosed()) {
            sendDiscoveryRequest(discoverySocket);
        }
    }

    /**
     * Sends discovery traffic from {@code sender}. An inquiry supplies an
     * ephemeral sender socket, which makes each unicast DISCOVER_RESP return
     * to the correct emulator even when several instances share port 63520.
     */
    private void sendDiscoveryRequest(DatagramSocket sender) {
        String msg = String.join("|",
                BluetoothConstants.MAGIC,
                BluetoothConstants.TYPE_DISCOVER_REQ,
                stack.getLocalAddress(),
                stack.getFriendlyName(),
                BluetoothUtils.getLocalIpString(),
                String.valueOf(stack.getSdpServer().getPort())
        );
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);

        // Send a directed broadcast on every active IPv4 LAN.  Some routers
        // and Wi-Fi drivers discard 255.255.255.255 while accepting the
        // subnet-specific broadcast address.
        Set<InetAddress> broadcasts = new LinkedHashSet<>(BluetoothUtils.getBroadcastAddresses());
        try {
            broadcasts.add(InetAddress.getByName("255.255.255.255"));
        } catch (UnknownHostException ignored) {}
        for (InetAddress broadcast : broadcasts) {
            try {
                DatagramPacket broadcastPacket = new DatagramPacket(data, data.length,
                        broadcast, discoveryPort);
                sender.send(broadcastPacket);
            } catch (IOException ignored) {}
        }

        // Send multicast even when this instance could not subscribe to the
        // group: it can still receive unicast replies, and LAN peers may have
        // multicast enabled.
        try {
            DatagramPacket multicastPacket = new DatagramPacket(data, data.length,
                    InetAddress.getByName(BluetoothConstants.DISCOVERY_MULTICAST_GROUP),
                    discoveryPort);
            sender.send(multicastPacket);
        } catch (IOException ignored) {}

        // Explicit loopback supports multiple emulator processes on one PC.
        try {
            DatagramPacket localhostPacket = new DatagramPacket(data, data.length,
                    InetAddress.getByName("127.0.0.1"), discoveryPort);
            sender.send(localhostPacket);
        } catch (IOException ignored) {}
    }

    public synchronized boolean startInquiry(int accessCode, DiscoveryListener listener) {
        if (inquiryRunning) return false;
        if (listener == null) return false;

        inquiryNotifiedPeers.clear();
        inquiryRunning = true;
        inquiryListener = listener;

        inquiryThread = new Thread(() -> runInquiry(listener), "KEm-BT-Inquiry");
        inquiryThread.setDaemon(true);
        inquiryThread.start();
        return true;
    }

    /**
     * Runs an inquiry with a unique UDP source port for responses. A response
     * sent to the shared discovery port can be delivered to a different local
     * KEmulator process when SO_REUSEADDR is in use.
     */
    private void runInquiry(DiscoveryListener listener) {
        // Use MulticastSocket for the ephemeral response endpoint too.  Its
        // enabled multicast loopback lets all local KEmulator processes see a
        // request while the unique source port keeps their replies separate.
        try (MulticastSocket replySocket = new MulticastSocket(null)) {
            replySocket.setReuseAddress(true);
            replySocket.setBroadcast(true);
            try {
                replySocket.setTimeToLive(DISCOVERY_MULTICAST_TTL);
                replySocket.setLoopbackMode(false);
            } catch (IOException ignored) {}
            replySocket.bind(new InetSocketAddress(0));
            replySocket.setSoTimeout(INQUIRY_RECEIVE_TIMEOUT_MS);

            System.out.println("[BT] Starting inquiry...");
            long start = System.currentTimeMillis();
            long nextRequestAt = start;
            while (inquiryRunning &&
                    (System.currentTimeMillis() - start) < BluetoothConstants.DEFAULT_INQUIRY_DURATION_MS) {
                long now = System.currentTimeMillis();
                if (now >= nextRequestAt) {
                    sendDiscoveryRequest(replySocket);
                    nextRequestAt = now + DISCOVERY_REQUEST_INTERVAL_MS;
                }

                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    replySocket.receive(packet);
                    handlePacket(packet);
                } catch (SocketTimeoutException ignored) {
                    // Check the inquiry deadline and cancellation state again.
                }
            }

            if (inquiryRunning) {
                inquiryRunning = false;
                try {
                    listener.inquiryCompleted(DiscoveryListener.INQUIRY_COMPLETED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("[BT] Inquiry completed, found " + cachedPeers.size() + " peers");
            }
        } catch (IOException e) {
            if (inquiryRunning) {
                inquiryRunning = false;
                try {
                    listener.inquiryCompleted(DiscoveryListener.INQUIRY_ERROR);
                } catch (Exception callbackError) {
                    callbackError.printStackTrace();
                }
                System.out.println("[BT] Inquiry failed: " + e.getMessage());
            }
        } finally {
            inquiryRunning = false;
        }
    }

    public synchronized boolean cancelInquiry(DiscoveryListener listener) {
        if (!inquiryRunning) return false;
        // Spec says listener param must be the same that started inquiry, but we ignore check for simplicity
        inquiryRunning = false;
        if (inquiryThread != null) {
            inquiryThread.interrupt();
            inquiryThread = null;
        }
        if (inquiryListener != null) {
            try {
                inquiryListener.inquiryCompleted(DiscoveryListener.INQUIRY_TERMINATED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        inquiryListener = null;
        System.out.println("[BT] Inquiry cancelled");
        return true;
    }

    public synchronized void cancelInquiry() {
        inquiryRunning = false;
        if (inquiryThread != null) {
            inquiryThread.interrupt();
            inquiryThread = null;
        }
        inquiryListener = null;
    }

    public javax.bluetooth.RemoteDevice[] retrieveDevices(int option) {
        if (option == DiscoveryAgent.CACHED) {
            Collection<BluetoothPeer> peers = cachedPeers.values();
            javax.bluetooth.RemoteDevice[] result = new javax.bluetooth.RemoteDevice[peers.size()];
            int i = 0;
            for (BluetoothPeer p : peers) {
                result[i++] = p.getRemoteDevice();
            }
            return result.length > 0 ? result : null;
        } else if (option == DiscoveryAgent.PREKNOWN) {
            List<javax.bluetooth.RemoteDevice> result = new ArrayList<>();
            for (String address : preknownPeerAddresses) {
                BluetoothPeer peer = cachedPeers.get(address);
                if (peer != null) {
                    result.add(peer.getRemoteDevice());
                }
            }
            return result.isEmpty() ? null : result.toArray(new javax.bluetooth.RemoteDevice[result.size()]);
        }
        return null;
    }

    public BluetoothPeer getPeerByAddress(String btAddress) {
        if (btAddress == null) return null;
        return cachedPeers.get(btAddress.toUpperCase());
    }

    public Collection<BluetoothPeer> getCachedPeers() {
        return new ArrayList<>(cachedPeers.values());
    }
}
