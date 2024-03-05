package javax.obex;

import javax.microedition.io.*;
import java.io.*;

public interface Operation extends ContentConnection {
    void abort() throws IOException;

    HeaderSet getReceivedHeaders() throws IOException;

    void sendHeaders(final HeaderSet p0) throws IOException;

    int getResponseCode() throws IOException;
}
