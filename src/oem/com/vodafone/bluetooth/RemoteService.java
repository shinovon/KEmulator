package com.vodafone.bluetooth;

import javax.bluetooth.ServiceRecord;

public class RemoteService
		extends BaseService {
	public final Device getDevice() {
		return null;
	}

	private RemoteService(Device paramDevice, ServiceRecord paramServiceRecord)
			throws IllegalArgumentException {
	}
}
