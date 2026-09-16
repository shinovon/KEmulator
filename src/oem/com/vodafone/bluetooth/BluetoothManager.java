package com.vodafone.bluetooth;

import emulator.bluetooth.BluetoothStack;
import javax.bluetooth.*;
import java.io.IOException;

/**
 * Full implementation of Vodafone BluetoothManager using LAN emulation.
 */
public class BluetoothManager {

    private static BluetoothManager instance;
    private final BluetoothStack stack;
    private SeekListener currentSeekListener;
    private boolean seeking = false;

    private BluetoothManager() throws IllegalStateException, IOException {
        try {
            this.stack = BluetoothStack.getInstance();
        } catch (BluetoothStateException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static final synchronized BluetoothManager getInstance() throws IllegalStateException {
        if (instance == null) {
            try {
                instance = new BluetoothManager();
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return instance;
    }

    public final synchronized String getFriendlyName() {
        return stack.getFriendlyName();
    }

    public final int getMaxDevices() {
        return 7;
    }

    public final void startDeviceSeek(SeekListener listener) throws NullPointerException, IllegalStateException, IOException {
        if (listener == null) throw new NullPointerException();
        if (seeking) throw new IllegalStateException("Already seeking");

        seeking = true;
        currentSeekListener = listener;

        // Use JSR-82 discovery under the hood
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = local.getDiscoveryAgent();

            DiscoveryListener discListener = new DiscoveryListener() {
                @Override
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    try {
                        Device dev = new Device(btDevice);
                        int devClass = cod != null ? cod.getRecord() : 0;
                        listener.foundDevice(dev, devClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {}

                @Override
                public void serviceSearchCompleted(int transID, int respCode) {}

                @Override
                public void inquiryCompleted(int discType) {
                    seeking = false;
                    int result = SeekListener.COMPLETED;
                    if (discType == DiscoveryListener.INQUIRY_TERMINATED) result = SeekListener.CANCELLED;
                    else if (discType == DiscoveryListener.INQUIRY_ERROR) result = SeekListener.ERROR;
                    listener.terminatedDeviceSeek(result);
                }
            };

            boolean started = agent.startInquiry(DiscoveryAgent.GIAC, discListener);
            if (!started) {
                seeking = false;
                throw new IOException("Failed to start inquiry");
            }

        } catch (BluetoothStateException e) {
            seeking = false;
            throw new IOException(e.getMessage());
        }
    }

    public final synchronized boolean stopDeviceSeek() {
        if (!seeking) return false;
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            local.getDiscoveryAgent().cancelInquiry(new DiscoveryListener() {
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {}
                public void serviceSearchCompleted(int transID, int respCode) {}
                public void inquiryCompleted(int discType) {}
            });
        } catch (Exception ignored) {}
        seeking = false;
        return true;
    }

    public final void registerPushRequest(String s1, String s2, String s3) throws NullPointerException, IllegalArgumentException, IOException {
        if (s1 == null || s2 == null || s3 == null) throw new NullPointerException();
        // No-op in emulation
    }

    public synchronized String[] getPushRequest() {
        return null;
    }
}
