package emulator.bluetooth;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;

/**
 * Represents a remote Bluetooth peer discovered via LAN.
 * Holds network info (IP, SDP port) plus BT identity.
 */
public class BluetoothPeer {

    private final String btAddress; // 12 hex chars uppercase
    private String friendlyName;
    private String ipAddress;
    private int sdpPort;
    private int deviceClass;
    private int discoverableMode;
    private long lastSeen;
    private RemoteDevice remoteDevice; // cached JSR-82 object

    public BluetoothPeer(String btAddress, String friendlyName, String ipAddress, int sdpPort, int deviceClass) {
        this.btAddress = BluetoothUtils.normalizeAddress(btAddress);
        this.friendlyName = friendlyName != null ? friendlyName : btAddress;
        this.ipAddress = ipAddress;
        this.sdpPort = sdpPort;
        this.deviceClass = deviceClass;
        this.discoverableMode = 0x9E8B33; // GIAC
        this.lastSeen = System.currentTimeMillis();
    }

    public String getBtAddress() {
        return btAddress;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getSdpPort() {
        return sdpPort;
    }

    public void setSdpPort(int sdpPort) {
        this.sdpPort = sdpPort;
    }

    public int getDeviceClass() {
        return deviceClass;
    }

    public void setDeviceClass(int deviceClass) {
        this.deviceClass = deviceClass;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void touch() {
        this.lastSeen = System.currentTimeMillis();
    }

    public synchronized RemoteDevice getRemoteDevice() {
        if (remoteDevice == null) {
            // Create RemoteDevice wrapper with extra info
            remoteDevice = new javax.bluetooth.RemoteDeviceImpl(btAddress, friendlyName, ipAddress, sdpPort, deviceClass);
        } else {
            // Update its fields
            if (remoteDevice instanceof javax.bluetooth.RemoteDeviceImpl) {
                ((javax.bluetooth.RemoteDeviceImpl) remoteDevice).update(friendlyName, ipAddress, sdpPort, deviceClass);
            }
        }
        return remoteDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BluetoothPeer)) return false;
        BluetoothPeer that = (BluetoothPeer) o;
        return btAddress.equals(that.btAddress);
    }

    @Override
    public int hashCode() {
        return btAddress.hashCode();
    }

    @Override
    public String toString() {
        return "BluetoothPeer{" +
                "btAddress='" + btAddress + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", ip='" + ipAddress + '\'' +
                ", sdpPort=" + sdpPort +
                '}';
    }
}
