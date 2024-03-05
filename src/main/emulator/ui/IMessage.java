package emulator.ui;

import javax.wireless.messaging.*;
import java.io.*;

public interface IMessage {
    Message receive(final String p0) throws IOException, InterruptedIOException;

    void send(final Message p0, final String p1) throws IOException, InterruptedIOException;
}
