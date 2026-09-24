package emulator.bluetooth;

import javax.bluetooth.L2CAPConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Emulated L2CAP connection over TCP with packet framing.
 * <p>L2CAP is packet-oriented, whereas TCP is a byte stream. Each packet is
 * therefore encoded as a two-byte unsigned, big-endian length followed by its
 * payload.</p>
 *
 * <p>A reader thread turns the TCP stream back into complete packets. This is
 * important for {@link #ready()}: {@code InputStream.available()} is only a
 * snapshot of a stream and cannot reliably implement the JSR-82 promise that
 * a successful {@code ready()} call makes the following {@code receive()}
 * non-blocking. In particular, games such as Art Of War poll {@code ready()}
 * before every packet and otherwise never issue a blocking read.</p>
 */
public class BTL2CAPConnection implements L2CAPConnection {

    /**
     * A normal game exchange is much smaller than this (Art Of War uses up to
     * 77 packets for a 22 KiB snapshot). The limit bounds memory use while
     * still allowing TCP backpressure if an application stops receiving.
     */
    private static final int MAX_QUEUED_PACKETS = 256;

    private static final AtomicInteger NEXT_CONNECTION_ID = new AtomicInteger();

    private final Socket socket;
    private final String url;
    private final int receiveMTU;
    private final int transmitMTU;
    private final DataInputStream dataIn;
    private final DataOutputStream dataOut;
    private final Object receiveLock = new Object();
    private final ArrayDeque<byte[]> receivedPackets = new ArrayDeque<byte[]>();
    private final Thread readerThread;
    private final int connectionId;

    private volatile boolean closed;
    private IOException inputFailure;
    private long sentPackets;
    private long sentBytes;
    private long receivedPacketCount;
    private long receivedBytes;


    public BTL2CAPConnection(Socket socket, int receiveMTU, int transmitMTU, String url) throws IOException {
        this.socket = socket;
        this.url = url;
        this.receiveMTU = Math.max(BluetoothConstants.MINIMUM_MTU,
                Math.min(receiveMTU, BluetoothConstants.DEFAULT_MTU));
        this.transmitMTU = Math.max(BluetoothConstants.MINIMUM_MTU,
                Math.min(transmitMTU, BluetoothConstants.DEFAULT_MTU));
        this.dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.connectionId = NEXT_CONNECTION_ID.incrementAndGet();
        try {
            socket.setTcpNoDelay(true);
        } catch (Exception ignored) {
            // TCP_NODELAY is a latency improvement, not a connection requirement.
        }

        readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                readPackets();
            }
        }, "KEm-BT-L2CAP-Reader-" + connectionId);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    @Override
    public int getTransmitMTU() throws IOException {
        ensureOpen();
        return transmitMTU;
    }

    @Override
    public int getReceiveMTU() throws IOException {
        ensureOpen();
        return receiveMTU;
    }

    @Override
    public void send(byte[] data) throws IOException {
        ensureOpen();
        if (data == null) {
            throw new NullPointerException();
        }

        int len = Math.min(data.length, transmitMTU);
        synchronized (dataOut) {
            ensureOpen();
            dataOut.writeShort(len);
            dataOut.write(data, 0, len);
            dataOut.flush();
            sentPackets++;
            sentBytes += len;
        }
    }

    @Override
    public int receive(byte[] inBuf) throws IOException {
        if (inBuf == null) {
            throw new NullPointerException();
        }

        byte[] packet;
        synchronized (receiveLock) {
            ensureOpen();
            while (receivedPackets.isEmpty()) {
                throwIfInputUnavailable();
                try {
                    receiveLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    InterruptedIOException interrupted = new InterruptedIOException("Interrupted while receiving L2CAP packet");
                    interrupted.initCause(e);
                    throw interrupted;
                }
            }
            packet = receivedPackets.removeFirst();
            receiveLock.notifyAll();
        }

        if (inBuf.length == 0) {
            return 0;
        }

        int copyLen = Math.min(packet.length, inBuf.length);
        System.arraycopy(packet, 0, inBuf, 0, copyLen);
        // If packet is larger than the caller's buffer, the remainder is
        // discarded as required by the L2CAPConnection contract.
        return copyLen;
    }

    @Override
    public boolean ready() throws IOException {
        synchronized (receiveLock) {
            ensureOpen();
            if (!receivedPackets.isEmpty()) {
                return true;
            }
            throwIfInputUnavailable();
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (receiveLock) {
            if (closed) {
                return;
            }
            closed = true;
            receiveLock.notifyAll();
        }

        // Preserve the historical close behavior: a peer may already have
        // disappeared, but closing our buffered wrappers should not prevent
        // the socket itself from being released.
        try {
            dataIn.close();
        } catch (IOException ignored) {
        }
        try {
            dataOut.close();
        } catch (IOException ignored) {
        }
        try {
            socket.close();
        } finally {
            readerThread.interrupt();
        }

        System.out.println("[BT] L2CAP connection closed: " + url
                + " (sent " + sentPackets + " packet(s)/" + sentBytes + " byte(s), received "
                + receivedPacketCount + " packet(s)/" + receivedBytes + " byte(s))");
    }

    private void readPackets() {
        try {
            while (!closed) {
                int length = dataIn.readUnsignedShort();
                byte[] packet = new byte[length];
                dataIn.readFully(packet);

                synchronized (receiveLock) {
                    while (!closed && receivedPackets.size() >= MAX_QUEUED_PACKETS) {
                        try {
                            receiveLock.wait();
                        } catch (InterruptedException e) {
                            if (closed) {
                                return;
                            }
                            Thread.currentThread().interrupt();
                            recordInputFailure(new IOException("L2CAP reader interrupted"));
                            return;
                        }
                    }
                    if (closed) {
                        return;
                    }
                    receivedPackets.addLast(packet);
                    receivedPacketCount++;
                    receivedBytes += length;
                    receiveLock.notifyAll();
                }
            }
        } catch (EOFException e) {
            if (!closed) {
                recordInputFailure(connectionClosed(e));
            }
        } catch (IOException e) {
            if (!closed) {
                recordInputFailure(e);
            }
        }
    }

    private void recordInputFailure(IOException failure) {
        synchronized (receiveLock) {
            if (inputFailure == null) {
                inputFailure = failure;
                System.out.println("[BT] L2CAP input failed for " + url + ": " + failure.getMessage());
            }
            receiveLock.notifyAll();
        }
    }

    private IOException connectionClosed(EOFException cause) {
        IOException failure = new IOException("Connection closed");
        failure.initCause(cause);
        return failure;
    }

    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Closed");
        }
    }

    private void throwIfInputUnavailable() throws IOException {
        if (closed) {
            throw new IOException("Closed");
        }
        if (inputFailure != null) {
            throw inputFailure;
        }
    }
}