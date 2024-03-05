package javax.bluetooth;

import javax.microedition.io.*;

public class LocalDevice {
    public LocalDevice() {
        super();
    }

    public static LocalDevice getLocalDevice() throws BluetoothStateException {
        return null;
    }

    public DiscoveryAgent getDiscoveryAgent() {
        return null;
    }

    public String getFriendlyName() {
        return "";
    }

    public DeviceClass getDeviceClass() {
        return null;
    }

    public boolean setDiscoverable(final int n) throws BluetoothStateException {
        return false;
    }

    public static String getProperty(final String s) {
        return null;
    }

    public int getDiscoverable() {
        return 0;
    }

    public String getBluetoothAddress() {
        return "";
    }

    public ServiceRecord getRecord(final Connection connection) {
        return null;
    }

    public void updateRecord(final ServiceRecord serviceRecord) throws ServiceRegistrationException {
    }
}
