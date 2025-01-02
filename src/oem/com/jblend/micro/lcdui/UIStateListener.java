package com.jblend.micro.lcdui;

public abstract interface UIStateListener {
	public abstract void notifyStartDraw(Object paramObject);

	public abstract void notifyEndDraw(Object paramObject);

	public abstract void notifyShowScreen(Object paramObject);

	public abstract void notifyHideScreen(Object paramObject);

	public abstract void notifyDisplayUpdated(Object paramObject);

	public abstract void notifyOccupyScreen(Object paramObject);

	public abstract void notifyReleaseScreen(Object paramObject);
}
