package javax.bluetooth;

import javax.microedition.io.Connection;

public class LocalDevice {
	private static LocalDevice localDevice;
	private DiscoveryAgent discoveryAgent;

	public LocalDevice() {
		super();
	}

	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		if (localDevice == null) {
			localDevice = new LocalDevice();
		}
		return localDevice;
	}

	public DiscoveryAgent getDiscoveryAgent() {
		if (discoveryAgent == null) {
			discoveryAgent = new DiscoveryAgent();
		}
		return discoveryAgent;
	}

	public String getFriendlyName() {
		return "KEmulator";
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
