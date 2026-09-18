package com.vodafone.bluetooth;

import javax.bluetooth.*;
import java.io.IOException;

/**
 * Vodafone Device wrapper around JSR-82 RemoteDevice.
 */
public class Device {

    private final RemoteDevice remoteDevice;
    private SeekListener serviceSeekListener;
    private boolean serviceSeeking = false;
    private int currentTransId = -1;

    public Device(String btAddress) throws NullPointerException, IllegalArgumentException {
        if (btAddress == null) throw new NullPointerException();
        this.remoteDevice = RemoteDevice.getOrCreate(btAddress);
    }

    Device(RemoteDevice remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public final String getFriendlyName() {
        try {
            return remoteDevice.getFriendlyName(false);
        } catch (IOException e) {
            return remoteDevice.getBluetoothAddress();
        }
    }

    public final synchronized void startServiceSeek(BaseService[] services, SeekListener listener)
            throws NullPointerException, IllegalStateException, IOException {

        if (services == null || listener == null) throw new NullPointerException();
        if (serviceSeeking) throw new IllegalStateException("Already seeking services");
        if (services.length == 0) throw new IllegalArgumentException("Empty service array");

        serviceSeeking = true;
        serviceSeekListener = listener;

        // Convert BaseService to UUIDs
        UUID[] uuids = new UUID[services.length];
        for (int i = 0; i < services.length; i++) {
            try {
                // ServiceID is expected to be UUID string
                uuids[i] = new UUID(services[i].getServiceID(), false);
            } catch (Exception e) {
                // Try short UUID
                try {
                    uuids[i] = new UUID(services[i].getServiceID(), true);
                } catch (Exception ex) {
                    uuids[i] = new UUID(0x1101); // Serial Port as fallback
                }
            }
        }

        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = local.getDiscoveryAgent();

            DiscoveryListener discListener = new DiscoveryListener() {
                @Override
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}

                @Override
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                    try {
                        RemoteService[] remoteServices = new RemoteService[servRecord.length];
                        for (int i = 0; i < servRecord.length; i++) {
                            remoteServices[i] = new RemoteService(Device.this, servRecord[i]);
                        }
                        listener.foundService(remoteServices);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void serviceSearchCompleted(int transID, int respCode) {
                    serviceSeeking = false;
                    int result = SeekListener.COMPLETED;
                    if (respCode == DiscoveryListener.SERVICE_SEARCH_TERMINATED) result = SeekListener.CANCELLED;
                    else if (respCode == DiscoveryListener.SERVICE_SEARCH_ERROR) result = SeekListener.ERROR;
                    else if (respCode == DiscoveryListener.SERVICE_SEARCH_NO_RECORDS) result = SeekListener.SERVICE_NOT_FOUND;
                    else if (respCode == DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE) result = SeekListener.ERROR;
                    listener.terminatedServiceSeek(Device.this, result);
                }

                @Override
                public void inquiryCompleted(int discType) {}
            };

            currentTransId = agent.searchServices(null, uuids, remoteDevice, discListener);

        } catch (BluetoothStateException e) {
            serviceSeeking = false;
            throw new IOException(e.getMessage());
        }
    }

    public final synchronized boolean stopServiceSeek() {
        if (!serviceSeeking) return false;
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            if (currentTransId != -1) {
                local.getDiscoveryAgent().cancelServiceSearch(currentTransId);
            }
        } catch (Exception ignored) {}
        serviceSeeking = false;
        return true;
    }

    public final String getBluetoothAddress() {
        return remoteDevice.getBluetoothAddress();
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
