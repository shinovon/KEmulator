package com.nttdocomo.device.location;

import com.nttdocomo.util.EventListener;

public abstract interface AccelerationEventListener
		extends EventListener {
	public abstract void actionPerformed(AccelerationSensor paramAccelerationSensor, int paramInt1, int paramInt2);
}
