package javax.microedition.io;

import java.io.*;
import emulator.custom.*;

final class ResourceConnectionImpl implements InputConnection
{
    String aString314;
    
    ResourceConnectionImpl(final String s) {
        super();
        this.aString314 = s.substring("resource://".length());
    }
    
    public final DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.openInputStream());
    }
    
    public final InputStream openInputStream() throws IOException {
        return emulator.custom.CustomJarResources.getResourceStream(this.aString314);
    }
    
    public final void close() {
    }
}
