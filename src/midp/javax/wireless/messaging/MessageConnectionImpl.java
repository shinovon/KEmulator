package javax.wireless.messaging;

import emulator.*;

import java.io.*;

public class MessageConnectionImpl implements MessageConnection {
    private final String aString314;
    private MessageListener aMessageListener1365;
    private boolean aBoolean313;

    public MessageConnectionImpl(final String aString314) {
        super();
        this.aString314 = aString314;
        this.aMessageListener1365 = null;
        this.aBoolean313 = false;
    }

    public Message newMessage(final String s) {
        return this.newMessage(s, this.aString314);
    }

    public Message newMessage(final String s, final String s2) {
        if (s.equals("text")) {
            return new TextMessageImpl(s2, null);
        }
        if (s.equals("binary")) {
            return new BinaryMessageImpl(s2, null);
        }
        return null;
    }

    public void send(final Message message) throws IOException, InterruptedIOException {
        Permission.checkPermission("messageconnection.send");
        Emulator.getEmulator().getMessage().send(message, this.aString314);
    }

    public Message receive() throws IOException, InterruptedIOException {
        Permission.checkPermission("messageconnection.receive");
        Message receive = null;
        while (receive == null && !this.aBoolean313) {
            try {
                receive = Emulator.getEmulator().getMessage().receive(this.aString314);
                Thread.sleep(1L);
            } catch (InterruptedException ignored) {}
        }
        if (receive != null) {
            this.aMessageListener1365.notifyIncomingMessage(this);
        }
        return receive;
    }

    public void setMessageListener(final MessageListener aMessageListener1365) throws IOException {
        this.aMessageListener1365 = aMessageListener1365;
    }

    public int numberOfSegments(final Message message) {
        if (message instanceof TextMessageImpl) {
            return ((TextMessageImpl) message).data.length() / 160 + 1;
        }
        return 1;
    }

    public void close() throws IOException {
        this.aBoolean313 = true;
    }
}
