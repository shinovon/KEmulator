package com.nttdocomo.ui;

public class AudioPresenter implements MediaPresenter {
	public static final int PRIORITY = 1;
	public static final int MIN_PRIORITY = 1;
	public static final int NORM_PRIORITY = 5;
	public static final int MAX_PRIORITY = 10;
	public static final int SYNC_MODE = 2;
	public static final int ATTR_SYNC_OFF = 0;
	public static final int ATTR_SYNC_ON = 1;
	public static final int TRANSPOSE_KEY = 3;
	public static final int SET_VOLUME = 4;
	public static final int CHANGE_TEMPO = 5;
	public static final int AUDIO_PLAYING = 1;
	public static final int AUDIO_STOPPED = 2;
	public static final int AUDIO_COMPLETE = 3;
	public static final int AUDIO_SYNC = 4;
	public static final int AUDIO_PAUSED = 5;
	public static final int AUDIO_RESTARTED = 6;
	static final AudioPresenter a;

	protected AudioPresenter() {
	}

	public static AudioPresenter getAudioPresenter() {
		return AudioPresenter.a;
	}

	public static AudioPresenter getAudioPresenter(final int n) {
		return AudioPresenter.a;
	}

	public void setSound(final MediaSound mediaSound) {
	}

	public void setData(final MediaData mediaData) {
	}

	public MediaResource getMediaResource() {
		return null;
	}

	public void play() {
	}

	public void play(final int n) {
	}

	public void stop() {
	}

	public void pause() {
	}

	public void restart() {
	}

	public int getCurrentTime() {
		return 0;
	}

	public void setAttribute(final int n, final int n2) {
	}

	public void setSyncEvent(final int n, final int n2) {
	}

	public void setMediaListener(final MediaListener mediaListener) {
	}

	static {
		a = new AudioPresenter();
	}
}
