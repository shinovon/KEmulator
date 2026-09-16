package javax.bluetooth;

import emulator.bluetooth.BluetoothBackend;
import emulator.bluetooth.BluetoothBackendProvider;

/**
 * JSR-82 discovery facade delegated to the selected Bluetooth backend.
 */
public class DiscoveryAgent {

    public static final int NOT_DISCOVERABLE = 0;
    public static final int GIAC = 10390323; // 0x9E8B33
    public static final int LIAC = 10390272; // 0x9E8B00
    public static final int CACHED = 0;
    public static final int PREKNOWN = 1;

    private final BluetoothBackend backend;

    public DiscoveryAgent() {
        try {
            this.backend = BluetoothBackendProvider.getInstance();
        } catch (BluetoothStateException e) {
            throw new RuntimeException(e);
        }
    }

    DiscoveryAgent(BluetoothBackend backend) {
        this.backend = backend;
    }

    public RemoteDevice[] retrieveDevices(final int option) {
        if (option != CACHED && option != PREKNOWN) {
            throw new IllegalArgumentException("Invalid option: " + option);
        }
        return backend.retrieveDevices(option);
    }

    public boolean startInquiry(final int accessCode, final DiscoveryListener listener) throws BluetoothStateException {
        if (listener == null) throw new NullPointerException("listener is null");
        if (accessCode != GIAC && accessCode != LIAC && (accessCode < 0x9E8B00 || accessCode > 0x9E8B3F) && accessCode != NOT_DISCOVERABLE) {
            throw new IllegalArgumentException("Invalid access code: " + accessCode);
        }
        return backend.startInquiry(accessCode, listener);
    }

    public boolean cancelInquiry(final DiscoveryListener listener) {
        if (listener == null) throw new NullPointerException();
        return backend.cancelInquiry(listener);
    }

    public int searchServices(final int[] attrSet, final UUID[] uuidSet, final RemoteDevice btDev,
                              final DiscoveryListener discListener) throws BluetoothStateException {
        if (btDev == null || discListener == null) throw new NullPointerException();
        if (uuidSet == null) throw new NullPointerException("uuidSet is null");
        return backend.searchServices(attrSet, uuidSet, btDev, discListener);
    }

    public boolean cancelServiceSearch(final int transID) {
        return backend.cancelServiceSearch(transID);
    }

    public String selectService(final UUID uuid, final int security, final boolean master) throws BluetoothStateException {
        if (uuid == null) throw new NullPointerException();
        if (security != ServiceRecord.NOAUTHENTICATE_NOENCRYPT &&
                security != ServiceRecord.AUTHENTICATE_NOENCRYPT &&
                security != ServiceRecord.AUTHENTICATE_ENCRYPT) {
            throw new IllegalArgumentException("Invalid security: " + security);
        }
        return backend.selectService(uuid, security, master);
    }
}
