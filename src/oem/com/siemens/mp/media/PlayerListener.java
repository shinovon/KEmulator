package com.siemens.mp.media;

public abstract interface PlayerListener {
	public static final String STARTED = "started";
	public static final String STOPPED = "stopped";
	public static final String STOPPED_AT_TIME = "stoppedAtTime";
	public static final String END_OF_MEDIA = "endOfMedia";
	public static final String DURATION_UPDATED = "durationUpdated";
	public static final String DEVICE_UNAVAILABLE = "deviceUnavailable";
	public static final String DEVICE_AVAILABLE = "deviceAvailable";
	public static final String VOLUME_CHANGED = "volumeChanged";
	public static final String SIZE_CHANGED = "sizeChanged";
	public static final String ERROR = "error";
	public static final String CLOSED = "closed";
	public static final String RECORD_STARTED = "recordStarted";
	public static final String RECORD_STOPPED = "recordStopped";
	public static final String RECORD_ERROR = "recordError";
	public static final String BUFFERING_STARTED = "bufferingStarted";
	public static final String BUFFERING_STOPPED = "bufferingStopped";

	public abstract void playerUpdate(Player paramPlayer, String paramString, Object paramObject);
}
