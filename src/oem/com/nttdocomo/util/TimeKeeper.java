package com.nttdocomo.util;

public abstract interface TimeKeeper {
	public abstract int getResolution();

	public abstract void start();

	public abstract void stop();

	public abstract void dispose();
}
