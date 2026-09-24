package emulator.bluetooth;

import javax.bluetooth.*;
import javax.microedition.io.Connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Built-in LAN Bluetooth backend.
 * Singleton that manages local device state, discovery, services, and
 * connections using UDP for discovery and TCP for data.
 */
public class BluetoothStack implements BluetoothBackend {

    private static BluetoothStack instance;

    private final String localAddress;
    private String friendlyName;
    private int discoverableMode = DiscoveryAgent.GIAC;
    private int deviceClass = BluetoothConstants.DEFAULT_DEVICE_CLASS;
    private boolean powerOn = true;

    private final ServiceRegistry serviceRegistry;
    private final DiscoveryManager discoveryManager;
    private final SDPServer sdpServer;

    private final Map<Integer, ServiceSearchTransaction> serviceSearchTransactions = new ConcurrentHashMap<>();
    private final AtomicInteger transactionIdCounter = new AtomicInteger(1);

    // Map notifier -> service record (for LocalDevice.getRecord)
    private final Map<Connection, ServiceRecord> notifierRecordMap = new ConcurrentHashMap<>();

    private BluetoothStack() throws IOException {
        // Host -D settings and persisted system-property maps are resolved by
        // the common configuration helper before falling back to generated
        // identity values.
        String configuredAddress = BluetoothConfiguration.getValue(
                BluetoothConstants.PROP_BT_ADDRESS, "KEM_BT_ADDRESS");
        if (configuredAddress != null && BluetoothUtils.isValidBtAddress(configuredAddress)) {
            localAddress = BluetoothUtils.normalizeAddress(configuredAddress);
        } else {
            localAddress = BluetoothUtils.generateRandomAddress();
        }

        String configuredName = BluetoothConfiguration.getValue(
                BluetoothConstants.PROP_BT_NAME, "KEM_BT_NAME");
        if (configuredName != null) {
            friendlyName = configuredName;
        } else {
            friendlyName = "KEmulator-" + localAddress.substring(8);
        }

        serviceRegistry = new ServiceRegistry();
        discoveryManager = new DiscoveryManager(this);
        sdpServer = new SDPServer(serviceRegistry, this);

        // Start services. If discovery cannot bind (for example because a
        // configured port is in use), do not leave a partial SDP listener
        // running behind a failed backend initialization.
        sdpServer.start();
        try {
            discoveryManager.start();
        } catch (IOException e) {
            sdpServer.stop();
            throw e;
        }

        System.out.println("[BT] Stack initialized: addr=" + localAddress + " name=" + friendlyName +
                " ip=" + BluetoothUtils.getLocalIpString() + " sdpPort=" + sdpServer.getPort());

        // Keep direct BluetoothStack users safe too; public facades normally
        // release the backend through BluetoothBackendProvider.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (Exception ignored) {}
        }, "KEm-BT-Shutdown"));
    }

    public static synchronized BluetoothStack getInstance() throws BluetoothStateException {
        if (instance == null) {
            try {
                instance = new BluetoothStack();
            } catch (IOException e) {
                throw new BluetoothStateException("Failed to initialize BT stack: " + e.getMessage());
            }
        }
        return instance;
    }

    public static synchronized BluetoothStack getInstanceIfExists() {
        return instance;
    }

    /** Stops LAN listeners and drops this singleton so a later lifecycle can recreate it. */
    @Override
    public void shutdown() {
        powerOn = false;
        discoveryManager.stop();
        sdpServer.stop();
        serviceRegistry.clear();
        notifierRecordMap.clear();
        serviceSearchTransactions.clear();
        synchronized (BluetoothStack.class) {
            if (instance == this) {
                instance = null;
            }
        }
        BluetoothBackendProvider.release(this);
    }

    // --- Local device properties ---

    public String getLocalAddress() {
        return localAddress;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String name) {
        this.friendlyName = name;
    }

    public int getDiscoverable() {
        return discoverableMode;
    }

    public boolean setDiscoverable(int mode) throws BluetoothStateException {
        if (!powerOn) throw new BluetoothStateException("Bluetooth is off");
        if (mode != DiscoveryAgent.GIAC && mode != DiscoveryAgent.LIAC && mode != DiscoveryAgent.NOT_DISCOVERABLE) {
            // Allow range 0x9E8B00-0x9E8B3F per spec
            if (mode < 0x9E8B00 || mode > 0x9E8B3F) {
                throw new IllegalArgumentException("Invalid discoverable mode: " + mode);
            }
        }
        this.discoverableMode = mode;
        return true;
    }

    public int getDeviceClass() {
        return deviceClass;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public SDPServer getSdpServer() {
        return sdpServer;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public DiscoveryManager getDiscoveryManager() {
        return discoveryManager;
    }

    // --- Discovery ---

    public boolean startInquiry(int accessCode, DiscoveryListener listener) throws BluetoothStateException {
        if (!powerOn) throw new BluetoothStateException("Bluetooth off");
        return discoveryManager.startInquiry(accessCode, listener);
    }

    public boolean cancelInquiry(DiscoveryListener listener) {
        return discoveryManager.cancelInquiry(listener);
    }

    public RemoteDevice[] retrieveDevices(int option) {
        return discoveryManager.retrieveDevices(option);
    }

    // --- Service search ---

    public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice device, DiscoveryListener listener) throws BluetoothStateException {
        if (!powerOn) throw new BluetoothStateException("Bluetooth off");
        if (device == null || listener == null) throw new NullPointerException();
        if (uuidSet == null || uuidSet.length == 0) throw new IllegalArgumentException("UUID set empty");

        int transId = transactionIdCounter.getAndIncrement();
        ServiceSearchTransaction trans = new ServiceSearchTransaction(transId, attrSet, uuidSet, device, listener);
        serviceSearchTransactions.put(transId, trans);

        // Run search in background thread
        new Thread(() -> {
            try {
                BluetoothPeer peer = null;
                if (device instanceof RemoteDeviceImpl) {
                    RemoteDeviceImpl impl = (RemoteDeviceImpl) device;
                    peer = new BluetoothPeer(impl.getBluetoothAddress(), impl.getFriendlyNameInternal(), impl.getIpAddress(), impl.getSdpPort(), 0);
                } else {
                    // Try to find peer by address
                    peer = discoveryManager.getPeerByAddress(device.getBluetoothAddress());
                }

                if (peer == null) {
                    listener.serviceSearchCompleted(transId, DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE);
                    serviceSearchTransactions.remove(transId);
                    return;
                }

                String ip = peer.getIpAddress();
                int sdpPort = peer.getSdpPort();
                if (ip == null || sdpPort == 0) {
                    // Try to get from RemoteDeviceImpl
                    if (device instanceof RemoteDeviceImpl) {
                        RemoteDeviceImpl impl = (RemoteDeviceImpl) device;
                        ip = impl.getIpAddress();
                        sdpPort = impl.getSdpPort();
                    }
                }

                if (ip == null || sdpPort == 0) {
                    listener.serviceSearchCompleted(transId, DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE);
                    serviceSearchTransactions.remove(transId);
                    return;
                }

                // Convert UUIDs to strings for search
                String[] uuidStrs = new String[uuidSet.length];
                for (int i = 0; i < uuidSet.length; i++) {
                    uuidStrs[i] = uuidSet[i].toString();
                }

                List<ServiceRecordImpl> records = SDPServer.queryRemote(ip, sdpPort, uuidStrs, device);

                // Filter by UUIDs: service must contain all UUIDs? For simplicity, we already filtered by any.
                // Now apply attrSet filtering - we ignore for emulation, return all attributes.

                if (records.isEmpty()) {
                    listener.serviceSearchCompleted(transId, DiscoveryListener.SERVICE_SEARCH_NO_RECORDS);
                } else {
                    ServiceRecord[] arr = records.toArray(new ServiceRecord[0]);
                    listener.servicesDiscovered(transId, arr);
                    listener.serviceSearchCompleted(transId, DiscoveryListener.SERVICE_SEARCH_COMPLETED);
                }

            } catch (IOException e) {
                try {
                    listener.serviceSearchCompleted(transId, DiscoveryListener.SERVICE_SEARCH_ERROR);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } finally {
                serviceSearchTransactions.remove(transId);
            }
        }, "KEm-BT-ServiceSearch-" + transId).start();

        return transId;
    }

    public boolean cancelServiceSearch(int transId) {
        ServiceSearchTransaction trans = serviceSearchTransactions.remove(transId);
        if (trans != null) {
            trans.cancel();
            try {
                trans.listener.serviceSearchCompleted(transId, DiscoveryListener.SERVICE_SEARCH_TERMINATED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public String selectService(UUID uuid, int security, boolean master) throws BluetoothStateException {
        if (!powerOn) throw new BluetoothStateException("Bluetooth off");
        // For simplicity, search all cached devices for first matching service
        RemoteDevice[] devices = retrieveDevices(DiscoveryAgent.CACHED);
        if (devices == null) return null;

        for (RemoteDevice dev : devices) {
            try {
                BluetoothPeer peer = discoveryManager.getPeerByAddress(dev.getBluetoothAddress());
                if (peer == null) continue;
                List<ServiceRecordImpl> records = SDPServer.queryRemote(
                        peer.getIpAddress(), peer.getSdpPort(),
                        new String[]{uuid.toString()}, dev);
                if (!records.isEmpty()) {
                    return records.get(0).getConnectionURL(security, master);
                }
            } catch (IOException ignored) {}
        }
        return null;
    }

    // --- Service registration ---

    public ServiceRecordImpl createServiceRecord(Object notifier, String protocol, String uuidOrPsm,
                                                 String serviceName, ServerSocket serverSocket,
                                                 String notifierUrl) {
        ServiceRecordImpl record = new ServiceRecordImpl(null); // host is null for local
        record.setProtocol(protocol);
        record.setUuid(uuidOrPsm);
        record.setServiceName(serviceName);
        record.setTcpPort(serverSocket.getLocalPort());
        record.setConnectionUrl(protocol + "://localhost:" + uuidOrPsm + ";name=" + serviceName);

        // Set default attributes per spec. The registry assigns the
        // ServiceRecordHandle (0x0000) when it registers this service.
        DataElement uuidSeq = new DataElement(DataElement.DATSEQ);
        try {
            UUID u = new UUID(uuidOrPsm, false);
            uuidSeq.addElement(new DataElement(DataElement.UUID, u));
        } catch (Exception e) {
            uuidSeq.addElement(new DataElement(DataElement.STRING, uuidOrPsm));
        }
        record.setAttributeValue(0x0001, uuidSeq); // ServiceClassIDList
        if (serviceName != null) {
            record.setAttributeValue(0x0100, new DataElement(DataElement.STRING, serviceName));
        }

        BluetoothService service = new BluetoothService(protocol, uuidOrPsm, serviceName, serverSocket, notifierUrl, notifier, record);
        serviceRegistry.register(service);
        notifierRecordMap.put((Connection) notifier, record);

        System.out.println("[BT] Service registered: " + protocol + " uuid=" + uuidOrPsm + " name=" + serviceName + " port=" + serverSocket.getLocalPort());

        return record;
    }

    public ServiceRecord getRecord(Connection notifier) {
        return notifierRecordMap.get(notifier);
    }

    public void updateRecord(ServiceRecord record) throws ServiceRegistrationException {
        if (record == null) throw new NullPointerException();
        if (!(record instanceof ServiceRecordImpl)) throw new ServiceRegistrationException("Invalid record");
        // In emulation, update is just replacing attributes - already done via setAttributeValue
        // We could notify but no-op
    }

    public void unregisterService(Object notifier) {
        serviceRegistry.unregisterByNotifier(notifier);
        notifierRecordMap.remove(notifier);
    }

    // --- Connection handling ---

    public Connection openClientConnection(String url) throws IOException {
        BluetoothUtils.ParsedUrl parsed = BluetoothUtils.parseBtUrl(url);
        String hostname = parsed.hostname;
        String channel = parsed.channel;

        // Resolve peer
        BluetoothPeer peer;
        String ip;
        int serviceTcpPort = -1;

        if (hostname.equalsIgnoreCase("localhost") || hostname.equals("127.0.0.1")) {
            throw new IOException("Cannot open client connection to localhost");
        }

        // hostname is BT address
        peer = discoveryManager.getPeerByAddress(hostname);
        if (peer == null) {
            // Try to interpret hostname as IP for direct testing
            // If hostname looks like IP, use it directly
            if (hostname.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                ip = hostname;
                // Need to find service by querying SDP? For direct IP, we need port?
                // For simplicity, assume channel is TCP port if numeric
                try {
                    serviceTcpPort = Integer.parseInt(channel);
                    ip = hostname;
                } catch (NumberFormatException e) {
                    throw new IOException("Peer not found: " + hostname);
                }
            } else {
                throw new IOException("Peer not found: " + hostname);
            }
        } else {
            ip = peer.getIpAddress();
            // Query SDP to find service port for this UUID/PSM
            try {
                List<ServiceRecordImpl> records = SDPServer.queryRemote(ip, peer.getSdpPort(), new String[]{channel}, peer.getRemoteDevice());
                if (!records.isEmpty()) {
                    serviceTcpPort = records.get(0).getTcpPort();
                } else {
                    // Try to find any service with matching UUID
                    // If not found, try to use channel as port number (for testing)
                    try {
                        serviceTcpPort = Integer.parseInt(channel);
                    } catch (NumberFormatException ex) {
                        throw new IOException("Service not found for UUID: " + channel);
                    }
                }
            } catch (IOException e) {
                // Fallback: try channel as port
                try {
                    serviceTcpPort = Integer.parseInt(channel);
                } catch (NumberFormatException ex) {
                    throw e;
                }
            }
        }

        if (ip == null || serviceTcpPort <= 0) {
            throw new IOException("Cannot resolve service: " + url);
        }

        if (BluetoothUtils.isLoopbackAddress(ip)) {
            // Two instances on one PC legitimately connect over loopback. For a
            // peer that was discovered on the LAN this means the cached address
            // was replaced by a loopback announcement, and the connection would
            // go back to this same machine.
            System.out.println("[BT] Warning: peer " + hostname + " resolved to loopback address " + ip
                    + " (service port " + serviceTcpPort + ")");
        }

        System.out.println("[BT] Opening client connection to " + ip + ":" + serviceTcpPort + " for " + url);

        // Open TCP socket.  Use a bounded connect so an offline LAN peer does
        // not indefinitely block the MIDlet's connection attempt.
        java.net.Socket socket = new java.net.Socket();
        try {
            socket.connect(new java.net.InetSocketAddress(ip, serviceTcpPort),
                    BluetoothConstants.SERVICE_SEARCH_TIMEOUT_MS);
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ignored) {}
            throw e;
        }

        if (parsed.protocol.equals("btspp")) {
            return new BTSPPConnection(socket, url);
        } else if (parsed.protocol.equals("btl2cap")) {
            return new BTL2CAPConnection(socket,
                    parseL2capMtu(parsed, "ReceiveMTU"),
                    parseL2capMtu(parsed, "TransmitMTU"),
                    url);
        } else if (parsed.protocol.equals("btgoep")) {
            // For GOEP, return ClientSessionImpl that wraps socket
            return new emulator.bluetooth.obex.ClientSessionImpl(socket, url);
        }

        throw new IOException("Unsupported protocol: " + parsed.protocol);
    }

    /**
     * Parses an optional L2CAP MTU URL parameter. The connection constructor
     * performs the final JSR-82 range clamp; this method only preserves the
     * default when an application supplied a malformed value.
     */
    private static int parseL2capMtu(BluetoothUtils.ParsedUrl parsed, String parameterName) {
        String value = parsed.getParam(parameterName);
        if (value == null) {
            return BluetoothConstants.DEFAULT_MTU;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return BluetoothConstants.DEFAULT_MTU;
        }
    }

    public Connection openServerNotifier(String url) throws IOException {
        BluetoothUtils.ParsedUrl parsed = BluetoothUtils.parseBtUrl(url);
        if (!parsed.isServer()) {
            throw new IOException("Server URL must use localhost: " + url);
        }

        String protocol = parsed.protocol;
        String channel = parsed.channel;
        String serviceName = parsed.getParam("name");
        if (serviceName == null) serviceName = "BT Service";

        // Bind directly to port zero so the OS reserves the selected service
        // port atomically (rather than probing first and introducing TOCTOU).
        ServerSocket ss = new ServerSocket(0);
        System.out.println("[BT] Opening server notifier: " + url + " -> TCP port " + ss.getLocalPort());

        if (protocol.equals("btspp")) {
            BTSPPConnectionNotifier notifier = new BTSPPConnectionNotifier(ss, url, channel, serviceName);
            // Create service record
            createServiceRecord(notifier, protocol, channel, serviceName, ss, url);
            return notifier;
        } else if (protocol.equals("btl2cap")) {
            BTL2CAPConnectionNotifier notifier = new BTL2CAPConnectionNotifier(ss, url, channel, serviceName,
                    parseL2capMtu(parsed, "ReceiveMTU"),
                    parseL2capMtu(parsed, "TransmitMTU"));
            createServiceRecord(notifier, protocol, channel, serviceName, ss, url);
            return notifier;
        } else if (protocol.equals("btgoep")) {
            emulator.bluetooth.obex.SessionNotifierImpl notifier =
                    new emulator.bluetooth.obex.SessionNotifierImpl(ss, url, channel, serviceName);
            createServiceRecord(notifier, protocol, channel, serviceName, ss, url);
            return notifier;
        }

        ss.close();
        throw new IOException("Unsupported protocol: " + protocol);
    }

    // --- Properties ---

    public String getProperty(String prop) {
        if (prop == null) return null;
        switch (prop) {
            case "bluetooth.api.version":
                return "1.1.1";
            case "bluetooth.master.switch":
                return "false";
            case "bluetooth.connected.devices.max":
                return "7";
            case "bluetooth.connected.inquiry":
                return "true";
            case "bluetooth.connected.page":
                return "true";
            case "bluetooth.connected.inquiry.scan":
                return "true";
            case "bluetooth.connected.page.scan":
                return "true";
            case "bluetooth.sd.trans.max":
                return "7";
            case "bluetooth.sd.attr.retrievable.max":
                return "100";
            case "bluetooth.l2cap.receiveMTU.max":
                return "672";
            case "obex.api.version":
                return "1.1";
            default:
                return null;
        }
    }

    // --- Inner class for service search transaction ---

    private static class ServiceSearchTransaction {
        final int id;
        final int[] attrSet;
        final UUID[] uuidSet;
        final RemoteDevice device;
        final DiscoveryListener listener;
        volatile boolean cancelled = false;

        ServiceSearchTransaction(int id, int[] attrSet, UUID[] uuidSet, RemoteDevice device, DiscoveryListener listener) {
            this.id = id;
            this.attrSet = attrSet;
            this.uuidSet = uuidSet;
            this.device = device;
            this.listener = listener;
        }

        void cancel() {
            cancelled = true;
        }
    }
}
