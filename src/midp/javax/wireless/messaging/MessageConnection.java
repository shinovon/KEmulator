package javax.wireless.messaging;

import javax.microedition.io.*;
import java.io.*;

public interface MessageConnection extends Connection {
    public static final String TEXT_MESSAGE = "text";
    public static final String BINARY_MESSAGE = "binary";
    public static final String MULTIPART_MESSAGE = "multipart";

    Message newMessage(final String p0);

    Message newMessage(final String p0, final String p1);

    void send(final Message p0) throws IOException, InterruptedIOException;

    Message receive() throws IOException, InterruptedIOException;

    void setMessageListener(final MessageListener p0) throws IOException;

    int numberOfSegments(final Message p0);
}
