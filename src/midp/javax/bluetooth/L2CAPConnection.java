package javax.bluetooth;

import javax.microedition.io.*;
import java.io.*;

public interface L2CAPConnection extends Connection {
    public static final int DEFAULT_MTU = 672;
    public static final int MINIMUM_MTU = 48;

    int getTransmitMTU() throws IOException;

    int getReceiveMTU() throws IOException;

    void send(final byte[] p0) throws IOException;

    int receive(final byte[] p0) throws IOException;

    boolean ready() throws IOException;
}
