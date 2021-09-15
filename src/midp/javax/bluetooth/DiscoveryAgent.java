package javax.bluetooth;

public class DiscoveryAgent
{
    public static final int NOT_DISCOVERABLE = 0;
    public static final int GIAC = 10390323;
    public static final int LIAC = 10390272;
    public static final int CACHED = 0;
    public static final int PREKNOWN = 1;
    
    public DiscoveryAgent() {
        super();
    }
    
    public RemoteDevice[] retrieveDevices(final int n) {
        return null;
    }
    
    public boolean startInquiry(final int n, final DiscoveryListener discoveryListener) throws BluetoothStateException {
        return false;
    }
    
    public boolean cancelInquiry(final DiscoveryListener discoveryListener) {
        return false;
    }
    
    public int searchServices(final int[] array, final UUID[] array2, final RemoteDevice remoteDevice, final DiscoveryListener discoveryListener) throws BluetoothStateException {
        return 0;
    }
    
    public boolean cancelServiceSearch(final int n) {
        return false;
    }
    
    public String selectService(final UUID uuid, final int n, final boolean b) throws BluetoothStateException {
        return null;
    }
}
