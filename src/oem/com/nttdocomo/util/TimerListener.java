package com.nttdocomo.util;

public abstract interface TimerListener extends EventListener {
	public abstract void timerExpired(Timer paramTimer);
}
