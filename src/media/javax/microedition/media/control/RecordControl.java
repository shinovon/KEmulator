package javax.microedition.media.control;

import javax.microedition.media.*;
import java.io.*;

public interface RecordControl extends Control {
    void commit();

    String getContentType();

    void reset();

    void setRecordLocation(final String p0);

    int setRecordSizeLimit(final int p0);

    void setRecordStream(final OutputStream p0);

    void startRecord();

    void stopRecord();
}
