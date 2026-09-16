package javax.bluetooth;

import emulator.bluetooth.BluetoothStack;
import javax.microedition.io.Connection;

/**
 * Full implementation of LocalDevice using LAN emulation.
 */
public class LocalDevice {

    private static LocalDevice instance;
    private final BluetoothStack stack;
    private final DiscoveryAgent discoveryAgent;

    public LocalDevice() {
        try {
            this.stack = BluetoothStack.getInstance();
            this.discoveryAgent = new DiscoveryAgent(stack);
        } catch (BluetoothStateException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDevice(BluetoothStack stack) {
        this.stack = stack;
        this.discoveryAgent = new DiscoveryAgent(stack);
    }

    public static LocalDevice getLocalDevice() throws BluetoothStateException {
        synchronized (LocalDevice.class) {
            if (instance == null) {
                BluetoothStack stack = BluetoothStack.getInstance();
                instance = new LocalDevice(stack);
            }
            return instance;
        }
    }

    public DiscoveryAgent getDiscoveryAgent() {
        return discoveryAgent;
    }

    public String getFriendlyName() {
        return stack.getFriendlyName();
    }

    public DeviceClass getDeviceClass() {
        return new DeviceClass(stack.getDeviceClass());
    }

    public boolean setDiscoverable(final int mode) throws BluetoothStateException {
        return stack.setDiscoverable(mode);
    }

    public static String getProperty(final String property) {
        if (property == null) throw new NullPointerException();
        try {
            BluetoothStack stack = BluetoothStack.getInstance();
            String val = stack.getProperty(property);
            if (val != null) return val;
        } catch (BluetoothStateException e) {
            // If stack not initialized, return null for most, but api version should still work
        }
        // Fallback for some properties
        if ("bluetooth.api.version".equals(property)) return "1.1.1";
        if ("obex.api.version".equals(property)) return "1.1";
        return null;
    }

    public int getDiscoverable() {
        return stack.getDiscoverable();
    }

    public String getBluetoothAddress() {
        return stack.getLocalAddress();
    }

    public ServiceRecord getRecord(final Connection notifier) {
        if (notifier == null) throw new NullPointerException();
        return stack.getRecord(notifier);
    }

    public void updateRecord(final ServiceRecord srvRecord) throws ServiceRegistrationException {
        if (srvRecord == null) throw new NullPointerException();
        try {
            stack.updateRecord(srvRecord);
        } catch (ServiceRegistrationException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceRegistrationException(e.getMessage());
        }
    }

    /**
     * JSR-82 1.1.1 method - not in earlier stub but part of spec.
     */
    public static boolean isPowerOn() {
        try {
            BluetoothStack stack = BluetoothStack.getInstanceIfExists();
            if (stack == null) return true; // assume on if not initialized
            return stack.isPowerOn();
        } catch (Exception e) {
            return false;
        }
    }
}
