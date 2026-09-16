package javax.bluetooth;

import emulator.bluetooth.BluetoothBackend;
import emulator.bluetooth.BluetoothBackendProvider;
import javax.microedition.io.Connection;

/**
 * Full implementation of LocalDevice through the selected Bluetooth backend.
 */
public class LocalDevice {

    private static LocalDevice instance;
    private final BluetoothBackend backend;
    private final DiscoveryAgent discoveryAgent;

    public LocalDevice() {
        try {
            this.backend = BluetoothBackendProvider.getInstance();
            this.discoveryAgent = new DiscoveryAgent(backend);
        } catch (BluetoothStateException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDevice(BluetoothBackend backend) {
        this.backend = backend;
        this.discoveryAgent = new DiscoveryAgent(backend);
    }

    public static LocalDevice getLocalDevice() throws BluetoothStateException {
        synchronized (LocalDevice.class) {
            BluetoothBackend backend = BluetoothBackendProvider.getInstance();
            // A backend lifecycle may have been released by CustomMethod.close
            // or a test harness; do not retain a stopped singleton facade.
            if (instance == null || instance.backend != backend) {
                instance = new LocalDevice(backend);
            }
            return instance;
        }
    }

    public DiscoveryAgent getDiscoveryAgent() {
        return discoveryAgent;
    }

    public String getFriendlyName() {
        return backend.getFriendlyName();
    }

    public DeviceClass getDeviceClass() {
        return new DeviceClass(backend.getDeviceClass());
    }

    public boolean setDiscoverable(final int mode) throws BluetoothStateException {
        return backend.setDiscoverable(mode);
    }

    public static String getProperty(final String property) {
        if (property == null) throw new NullPointerException();
        try {
            BluetoothBackend backend = BluetoothBackendProvider.getInstance();
            String value = backend.getProperty(property);
            if (value != null) return value;
        } catch (BluetoothStateException e) {
            // If a backend cannot initialize, retain the historic API-version
            // fallback below.
        }
        if ("bluetooth.api.version".equals(property)) return "1.1.1";
        if ("obex.api.version".equals(property)) return "1.1";
        return null;
    }

    public int getDiscoverable() {
        return backend.getDiscoverable();
    }

    public String getBluetoothAddress() {
        return backend.getLocalAddress();
    }

    public ServiceRecord getRecord(final Connection notifier) {
        if (notifier == null) throw new NullPointerException();
        return backend.getRecord(notifier);
    }

    public void updateRecord(final ServiceRecord srvRecord) throws ServiceRegistrationException {
        if (srvRecord == null) throw new NullPointerException();
        try {
            backend.updateRecord(srvRecord);
        } catch (ServiceRegistrationException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceRegistrationException(e.getMessage());
        }
    }

    /** JSR-82 1.1.1 method. */
    public static boolean isPowerOn() {
        try {
            BluetoothBackend backend = BluetoothBackendProvider.getInstanceIfExists();
            return backend == null || backend.isPowerOn();
        } catch (Exception e) {
            return false;
        }
    }
}
