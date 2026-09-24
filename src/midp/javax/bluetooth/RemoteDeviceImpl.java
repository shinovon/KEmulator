package javax.bluetooth;

/**
 * Extended RemoteDevice with LAN endpoint information.
 * Used internally by the built-in LAN backend.
 */
public class RemoteDeviceImpl extends RemoteDevice {

    public RemoteDeviceImpl(String address, String friendlyName, String ipAddress, int sdpPort, int deviceClass) {
        super(address, friendlyName, ipAddress, sdpPort, deviceClass);
    }

    public void update(String friendlyName, String ipAddress, int sdpPort, int deviceClass) {
        if (friendlyName != null) this.friendlyName = friendlyName;
        if (ipAddress != null) this.ipAddress = ipAddress;
        if (sdpPort != 0) this.sdpPort = sdpPort;
        this.deviceClassInt = deviceClass;
    }

    public String getFriendlyNameInternal() {
        return friendlyName;
    }

    public int getDeviceClassInt() {
        return deviceClassInt;
    }
}
