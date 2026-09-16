package emulator.bluetooth;

import javax.bluetooth.L2CAPConnection;
import java.io.*;

/**
 * Emulated L2CAP connection over TCP with packet framing.
 * 
 * L2CAP is packet-oriented, not stream-oriented.
 * We emulate it by prefixing each packet with 2-byte length (big-endian).
 */
public class BTL2CAPConnection implements L2CAPConnection {

    private final java.net.Socket socket;
    private final String url;
    private final int receiveMTU;
    private final int transmitMTU;
    private final DataInputStream dataIn;
    private final DataOutputStream dataOut;
    private boolean closed = false;

    // Buffer for ready() check - we need to peek if data available
    private byte[] bufferedPacket = null;

    public BTL2CAPConnection(java.net.Socket socket, int receiveMTU, int transmitMTU, String url) throws IOException {
        this.socket = socket;
        this.url = url;
        this.receiveMTU = Math.max(BluetoothConstants.MINIMUM_MTU, Math.min(receiveMTU, BluetoothConstants.DEFAULT_MTU));
        this.transmitMTU = Math.max(BluetoothConstants.MINIMUM_MTU, Math.min(transmitMTU, BluetoothConstants.DEFAULT_MTU));
        this.dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        try {
            socket.setTcpNoDelay(true);
        } catch (Exception ignored) {}
    }

    @Override
    public int getTransmitMTU() throws IOException {
        if (closed) throw new IOException("Closed");
        return transmitMTU;
    }

    @Override
    public int getReceiveMTU() throws IOException {
        if (closed) throw new IOException("Closed");
        return receiveMTU;
    }

    @Override
    public void send(byte[] data) throws IOException {
        if (closed) throw new IOException("Closed");
        if (data == null) throw new NullPointerException();
        int len = Math.min(data.length, transmitMTU);
        synchronized (dataOut) {
            dataOut.writeShort(len);
            dataOut.write(data, 0, len);
            dataOut.flush();
        }
    }

    @Override
    public int receive(byte[] inBuf) throws IOException {
        if (closed) throw new IOException("Closed");
        if (inBuf == null) throw new NullPointerException();

        byte[] packet;
        synchronized (this) {
            if (bufferedPacket != null) {
                packet = bufferedPacket;
                bufferedPacket = null;
            } else {
                // Read packet
                try {
                    int len = dataIn.readUnsignedShort();
                    packet = new byte[len];
                    dataIn.readFully(packet);
                } catch (EOFException e) {
                    throw new IOException("Connection closed");
                }
            }
        }

        if (inBuf.length == 0) {
            return 0;
        }

        int copyLen = Math.min(packet.length, inBuf.length);
        System.arraycopy(packet, 0, inBuf, 0, copyLen);
        // If packet larger than buffer, rest is discarded per spec
        return copyLen;
    }

    @Override
    public boolean ready() throws IOException {
        if (closed) throw new IOException("Closed");
        synchronized (this) {
            if (bufferedPacket != null) return true;
            if (dataIn.available() >= 2) {
                // Peek packet without consuming fully? For simplicity, read it
                dataIn.mark(65536);
                try {
                    int len = dataIn.readUnsignedShort();
                    if (dataIn.available() >= len) {
                        byte[] pkt = new byte[len];
                        dataIn.readFully(pkt);
                        bufferedPacket = pkt;
                        return true;
                    } else {
                        dataIn.reset();
                        return false;
                    }
                } catch (IOException e) {
                    try { dataIn.reset(); } catch (IOException ignored) {}
                    return false;
                }
            }
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        try { dataIn.close(); } catch (IOException ignored) {}
        try { dataOut.close(); } catch (IOException ignored) {}
        socket.close();
        System.out.println("[BT] L2CAP connection closed: " + url);
    }
}
