package com.vodafone.bluetooth;

import java.io.IOException;

public class BluetoothManager {
	private BluetoothManager()
			throws IllegalStateException, IOException {
	}

	public static final synchronized BluetoothManager getInstance()
			throws IllegalStateException {
		return null;
	}

	public final synchronized String getFriendlyName() {
		return null;
	}

	public final int getMaxDevices() {
		return 0;
	}

	public final void startDeviceSeek(SeekListener paramSeekListener)
			throws NullPointerException, IllegalStateException, IOException {
	}

	public final synchronized boolean stopDeviceSeek() {
		return false;
	}

	public final void registerPushRequest(String paramString1, String paramString2, String paramString3)
			throws NullPointerException, IllegalArgumentException, IOException {
	}

	public synchronized String[] getPushRequest() {
		return null;
	}
}
