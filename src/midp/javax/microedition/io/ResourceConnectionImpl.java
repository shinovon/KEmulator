package javax.microedition.io;

import java.io.*;

final class ResourceConnectionImpl implements InputConnection
{
    String url;
    
    ResourceConnectionImpl(final String s) {
        super();
        if(s.startsWith("resource://")) {
            this.url = s.substring("resource://".length());
        }  else {
            this.url = s.substring("resource:".length());
        }
    }
    
    public final DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.openInputStream());
    }
    
    public final InputStream openInputStream() throws IOException {
        return emulator.custom.CustomJarResources.getResourceAsStream(this.url);
    }
    
    public final void close() {
    }
}
