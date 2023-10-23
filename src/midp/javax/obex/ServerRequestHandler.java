package javax.obex;

public class ServerRequestHandler
{
    private long connectionID;
    
    protected ServerRequestHandler() {
        super();
    }
    
    public final HeaderSet createHeaderSet() {
        return null;
    }
    
    public void setConnectionID(final long aLong1168) {
        this.connectionID = aLong1168;
    }
    
    public long getConnectionID() {
        return this.connectionID;
    }
    
    public int onConnect(final HeaderSet set, final HeaderSet set2) {
        return 0;
    }
    
    public void onDisconnect(final HeaderSet set, final HeaderSet set2) {
    }
    
    public int onSetPath(final HeaderSet set, final HeaderSet set2, final boolean b, final boolean b2) {
        return 0;
    }
    
    public int onDelete(final HeaderSet set, final HeaderSet set2) {
        return 0;
    }
    
    public int onPut(final Operation operation) {
        return 0;
    }
    
    public int onGet(final Operation operation) {
        return 0;
    }
    
    public void onAuthenticationFailure(final byte[] array) {
    }
}
