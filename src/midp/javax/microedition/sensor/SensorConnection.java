package javax.microedition.sensor;

import javax.microedition.io.*;
import java.io.*;

public interface SensorConnection extends Connection {
    public static final int STATE_CLOSED = 4;
    public static final int STATE_LISTENING = 2;
    public static final int STATE_OPENED = 1;

    Channel getChannel(final ChannelInfo p0);

    Data[] getData(final int p0) throws IOException;

    Data[] getData(final int p0, final long p1, final boolean p2, final boolean p3, final boolean p4) throws IOException;

    SensorInfo getSensorInfo();

    int getState();

    void removeDataListener();

    void setDataListener(final DataListener p0, final int p1);

    void setDataListener(final DataListener p0, final int p1, final long p2, final boolean p3, final boolean p4, final boolean p5);
}
