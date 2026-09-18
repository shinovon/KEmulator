package emulator.bluetooth;

import javax.bluetooth.ServiceRecordImpl;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Simple SDP (Service Discovery Protocol) server emulated over TCP.
 * 
 * Protocol (very simple, text based):
 * Client -> Server: "SEARCH <uuid1,uuid2,...>" or "SEARCH ALL" or "LIST"
 * Server -> Client:
 *   int count (DataOutputStream.writeInt)
 *   For each service:
 *     UTF: uuidOrPsm
 *     UTF: serviceName
 *     UTF: protocol (btspp/btl2cap/btgoep)
 *     Int: tcpPort
 *     Int: handle
 *     UTF: connectionUrl (btspp://...)
 *     Int: serviceClass
 * 
 * This is enough for our LAN emulation.
 */
public class SDPServer implements Runnable {

    private final ServiceRegistry registry;
    private final BluetoothStack stack;
    private ServerSocket serverSocket;
    private Thread thread;
    private volatile boolean running = false;

    public SDPServer(ServiceRegistry registry, BluetoothStack stack) {
        this.registry = registry;
        this.stack = stack;
    }

    public synchronized void start() throws IOException {
        if (running) return;
        serverSocket = new ServerSocket(BluetoothConstants.SDP_SERVER_PORT);
        // If port 0, system assigns free port
        running = true;
        thread = new Thread(this, "KEm-BT-SDP-Server");
        thread.setDaemon(true);
        thread.start();
        System.out.println("[BT] SDP Server started on port " + serverSocket.getLocalPort() + " IP " + BluetoothUtils.getLocalIpString());
    }

    public synchronized void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {}
            serverSocket = null;
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public int getPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : -1;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket client = serverSocket.accept();
                // Handle in new thread to not block
                new Thread(() -> handleClient(client), "KEm-BT-SDP-Handler").start();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void handleClient(Socket socket) {
        try (Socket s = socket;
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {

            s.setSoTimeout(5000);
            String request;
            try {
                request = in.readUTF();
            } catch (EOFException e) {
                return;
            }

            List<BluetoothService> services;

            if (request == null) {
                services = new java.util.ArrayList<>();
            } else if (request.startsWith("SEARCH")) {
                String arg = request.substring(6).trim();
                if (arg.equalsIgnoreCase("ALL") || arg.equalsIgnoreCase("LIST") || arg.isEmpty()) {
                    services = new java.util.ArrayList<>(registry.getAllServices());
                } else {
                    // Comma-separated UUIDs. A JSR-82 UUID can be expressed
                    // with or without dashes, so compare canonical values.
                    String[] uuids = arg.split(",");
                    // For simplicity, a service matches if its identifier is
                    // present in the requested UUID set.
                    java.util.Set<String> wanted = new java.util.HashSet<>();
                    for (String u : uuids) {
                        wanted.add(BluetoothUtils.normalizeServiceIdentifier(u));
                    }
                    java.util.List<BluetoothService> matched = new java.util.ArrayList<>();
                    for (BluetoothService svc : registry.getAllServices()) {
                        String serviceId = BluetoothUtils.normalizeServiceIdentifier(svc.getUuidOrPsm());
                        if (wanted.contains(serviceId)) {
                            matched.add(svc);
                        }
                    }
                    services = matched;
                }
            } else if (request.equalsIgnoreCase("LIST")) {
                services = new java.util.ArrayList<>(registry.getAllServices());
            } else {
                services = new java.util.ArrayList<>(registry.getAllServices());
            }

            System.out.println("[BT] SDP request from " + s.getInetAddress().getHostAddress() +
                    ": " + request + " -> " + services.size() + " service(s)");
            out.writeInt(services.size());
            for (BluetoothService svc : services) {
                out.writeUTF(svc.getUuidOrPsm());
                out.writeUTF(svc.getServiceName() != null ? svc.getServiceName() : "");
                out.writeUTF(svc.getProtocol());
                out.writeInt(svc.getTcpPort());
                out.writeInt(svc.getServiceRecord().getHandle());
                out.writeUTF(svc.generateConnectionUrl(stack.getLocalAddress()));
                out.writeInt(svc.getServiceRecord().getDeviceServiceClasses());
                // Attributes count
                ServiceRecordImpl rec = svc.getServiceRecord();
                out.writeInt(rec.getAttributeIDs().length);
                for (int attrId : rec.getAttributeIDs()) {
                    out.writeInt(attrId);
                    // For simplicity, we don't serialize DataElement fully here
                    // Client will reconstruct basic attributes
                }
            }
            out.flush();

        } catch (IOException e) {
            // Client disconnected or timeout, ignore
        }
    }

    /**
     * Query remote SDP server for services.
     * Returns list of ServiceRecordImpl (with host device set).
     */
    public static List<ServiceRecordImpl> queryRemote(String ip, int port, String[] uuidFilter, javax.bluetooth.RemoteDevice hostDevice) throws IOException {
        try (Socket socket = new Socket()) {
            // A peer that disappeared from the LAN must not leave a JSR-82
            // service search blocked behind the operating system's long TCP
            // connect timeout.
            socket.connect(new InetSocketAddress(ip, port), BluetoothConstants.SERVICE_SEARCH_TIMEOUT_MS);
            socket.setSoTimeout(BluetoothConstants.SERVICE_SEARCH_TIMEOUT_MS);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            StringBuilder req = new StringBuilder("SEARCH ");
            if (uuidFilter == null || uuidFilter.length == 0) {
                req.append("ALL");
            } else {
                for (int i = 0; i < uuidFilter.length; i++) {
                    if (i > 0) req.append(",");
                    req.append(uuidFilter[i]);
                }
            }
            out.writeUTF(req.toString());
            out.flush();

            int count = in.readInt();
            List<ServiceRecordImpl> result = new java.util.ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                String uuidOrPsm = in.readUTF();
                String serviceName = in.readUTF();
                String protocol = in.readUTF();
                int tcpPort = in.readInt();
                int handle = in.readInt();
                String connUrl = in.readUTF();
                int deviceClass = in.readInt();
                int attrCount = in.readInt();
                // Skip attributes
                for (int j = 0; j < attrCount; j++) {
                    in.readInt();
                }

                ServiceRecordImpl rec = new ServiceRecordImpl(hostDevice);
                rec.setHandle(handle);
                rec.setTcpPort(tcpPort);
                rec.setConnectionUrl(connUrl);
                rec.setProtocol(protocol);
                rec.setServiceName(serviceName);
                rec.setUuid(uuidOrPsm);
                rec.setDeviceServiceClasses(deviceClass);
                // setHandle() restores the immutable ServiceRecordHandle
                // attribute (0x0000); populate the remaining defaults here.
                rec.setAttributeValue(0x0001, createUuidSequence(uuidOrPsm));
                if (serviceName != null && !serviceName.isEmpty()) {
                    // ServiceName is attribute 0x0100 + base
                    rec.setAttributeValue(0x0100, new javax.bluetooth.DataElement(javax.bluetooth.DataElement.STRING, serviceName));
                }

                result.add(rec);
            }
            return result;
        }
    }

    private static javax.bluetooth.DataElement createUuidSequence(String uuid) {
        javax.bluetooth.DataElement seq = new javax.bluetooth.DataElement(javax.bluetooth.DataElement.DATSEQ);
        try {
            // Try to parse as UUID
            javax.bluetooth.UUID u = new javax.bluetooth.UUID(uuid, false);
            seq.addElement(new javax.bluetooth.DataElement(javax.bluetooth.DataElement.UUID, u));
        } catch (Exception e) {
            // If not valid UUID, store as string
            seq.addElement(new javax.bluetooth.DataElement(javax.bluetooth.DataElement.STRING, uuid));
        }
        return seq;
    }
}
