package com.vodafone.bluetooth;

import java.io.IOException;
import javax.bluetooth.RemoteDevice;

public class Device {
	public Device(String paramString)
			throws NullPointerException, IllegalArgumentException {
	}

	Device(RemoteDevice paramRemoteDevice) {
	}

	public final String getFriendlyName() {
		return null;
	}

	public final synchronized void startServiceSeek(BaseService[] paramArrayOfBaseService, SeekListener paramSeekListener)
			throws NullPointerException, IllegalStateException, IOException {
	}

	public final synchronized boolean stopServiceSeek() {
		return false;
	}

	public final String getBluetoothAddress() {
		return null;
	}
}
