package com.vodafone.bluetooth;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base for Vodafone Bluetooth sessions (message exchange).
 * Emulated over BTSPP (TCP).
 */
public abstract class SessionBase {

    public static final int REPORT_NONE = 0;
    public static final int REPORT_ERROR = 1;
    public static final int REPORT_RESULT = 2;

    protected final SessionListener listener;
    protected StreamConnection connection;
    protected DataInputStream dataIn;
    protected DataOutputStream dataOut;
    protected boolean connected = false;
    protected Thread receiverThread;

    // Message tracking
    protected final AtomicInteger messageIdCounter = new AtomicInteger(1);
    protected final ConcurrentHashMap<Integer, byte[]> pendingMessages = new ConcurrentHashMap<>();

    SessionBase(SessionListener listener) throws NullPointerException {
        if (listener == null) throw new NullPointerException();
        this.listener = listener;
    }

    public boolean close(int reason) {
        connected = false;
        if (receiverThread != null) {
            receiverThread.interrupt();
            receiverThread = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ignored) {}
            connection = null;
        }
        try {
            listener.gotConnectionStatus(0, SessionListener.CONN_CLOSED);
        } catch (Exception ignored) {}
        return true;
    }

    public int send(int[] recipients, String message, int report) throws NullPointerException, IllegalArgumentException {
        if (recipients == null || message == null) throw new NullPointerException();
        if (!connected) return SessionListener.ERROR_NO_CONNECTION;
        try {
            byte[] data = message.getBytes("UTF-8");
            return sendInternal(recipients, data, report, true);
        } catch (IOException e) {
            return SessionListener.ERROR_NO_CONNECTION;
        }
    }

    public int send(int[] recipients, byte[] data, int report) throws NullPointerException, IllegalArgumentException {
        if (recipients == null || data == null) throw new NullPointerException();
        if (!connected) return SessionListener.ERROR_NO_CONNECTION;
        return sendInternal(recipients, data, report, false);
    }

    private int sendInternal(int[] recipients, byte[] data, int report, boolean isString) {
        try {
            int msgId = messageIdCounter.getAndIncrement();
            synchronized (dataOut) {
                dataOut.writeInt(msgId);
                dataOut.writeInt(recipients.length);
                for (int r : recipients) dataOut.writeInt(r);
                dataOut.writeBoolean(isString);
                dataOut.writeInt(data.length);
                dataOut.write(data);
                dataOut.writeInt(report);
                dataOut.flush();
            }
            return SessionListener.SUCCESS;
        } catch (IOException e) {
            return SessionListener.ERROR_NO_CONNECTION;
        }
    }

    public int sendSignal(int[] recipients, int signal, int report) throws NullPointerException, IllegalArgumentException {
        if (recipients == null) throw new NullPointerException();
        if (!connected) return SessionListener.ERROR_NO_CONNECTION;
        try {
            synchronized (dataOut) {
                dataOut.writeInt(-signal); // negative for signal
                dataOut.writeInt(recipients.length);
                for (int r : recipients) dataOut.writeInt(r);
                dataOut.writeInt(report);
                dataOut.flush();
            }
            // Notify locally for loopback?
            return SessionListener.SUCCESS;
        } catch (IOException e) {
            return SessionListener.ERROR_NO_CONNECTION;
        }
    }

    public void cleanAllMessage() {
        pendingMessages.clear();
    }

    protected void startReceiver() {
        receiverThread = new Thread(() -> {
            try {
                while (connected) {
                    int msgId = dataIn.readInt();
                    if (msgId < 0) {
                        // Signal
                        int sig = -msgId;
                        int count = dataIn.readInt();
                        int[] recipients = new int[count];
                        for (int i = 0; i < count; i++) recipients[i] = dataIn.readInt();
                        int report = dataIn.readInt();
                        // For simplicity, we treat first recipient as sender?
                        int sender = recipients.length > 0 ? recipients[0] : 0;
                        listener.gotSignal(sender, sig);
                        if (report == REPORT_RESULT) {
                            listener.gotResult(msgId, recipients, new int[]{SessionListener.SUCCESS});
                        }
                    } else {
                        int count = dataIn.readInt();
                        int[] recipients = new int[count];
                        for (int i = 0; i < count; i++) recipients[i] = dataIn.readInt();
                        boolean isString = dataIn.readBoolean();
                        int len = dataIn.readInt();
                        byte[] data = new byte[len];
                        dataIn.readFully(data);
                        int report = dataIn.readInt();

                        int sender = 0; // In real impl, sender is device index
                        if (isString) {
                            listener.gotMessage(sender, new String(data, "UTF-8"));
                        } else {
                            listener.gotMessage(sender, data);
                        }
                        if (report == REPORT_RESULT) {
                            listener.gotResult(msgId, recipients, new int[]{SessionListener.SUCCESS});
                        }
                    }
                }
            } catch (IOException e) {
                if (connected) {
                    connected = false;
                    try {
                        listener.gotConnectionStatus(0, SessionListener.CONN_FAILED);
                    } catch (Exception ignored) {}
                }
            }
        }, "Vodafone-BT-Session-Receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    protected void setupConnection(StreamConnection conn) throws IOException {
        this.connection = conn;
        this.dataIn = new DataInputStream(conn.openInputStream());
        this.dataOut = new DataOutputStream(conn.openOutputStream());
        this.connected = true;
        startReceiver();
        listener.gotConnectionStatus(0, SessionListener.CONN_OPENED);
    }
}
