package javax.microedition.rms;

import java.io.*;

final class RMSListFileNameFilter implements FilenameFilter
{
    RMSListFileNameFilter() {
        super();
    }
    
    public final boolean accept(final File file, final String s) {
        return s.startsWith(".") && new File(RecordStore.rmsDir + "/" + s + "/" + s.substring(1) + ".idx").exists();
    }
}
