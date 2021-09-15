package javax.microedition.io;

import java.io.*;

public class PushRegistry
{
    private PushRegistry() {
        super();
    }
    
    public static synchronized void registerConnection(final String s, final String s2, final String s3) throws ClassNotFoundException, IOException {
    }
    
    public static synchronized boolean unregisterConnection(final String s) {
        return false;
    }
    
    public static synchronized String[] listConnections(final boolean b) {
        return null;
    }
    
    public static synchronized String getMIDlet(final String s) {
        return null;
    }
    
    public static synchronized String getFilter(final String s) {
        return null;
    }
    
    public static synchronized long registerAlarm(final String s, final long n) throws ClassNotFoundException, ConnectionNotFoundException {
        return 0L;
    }
}
