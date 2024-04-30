package javax.microedition.io;

import java.io.*;

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
        return emulator.custom.CustomJarResources.getResourceAsStream(this.aString314);
    }
    
    public final void close() {
    }
}
