/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package com.jblend.media.smaf;

import com.jblend.media.MediaData;
import com.jblend.media.MediaImageOperator;
import com.jblend.media.MediaPlayer;
import com.jblend.media.MediaPlayerListener;
import emulator.media.mmf.MMFPlayer;
import emulator.media.mmf.MaDll;

import java.util.ArrayList;
import java.util.List;

public class SmafPlayer extends MediaPlayer implements MediaImageOperator {

	private int sound;
	private boolean error;
	private SmafData data;
	private final List<MediaPlayerListener> mediaListeners = new ArrayList<>();
	private final List<SmafPlayerListener> smafListeners = new ArrayList<>();

	private int volume = 127;
	private int transpose = 0;
	private int speed = 100;

	public SmafPlayer() {
	}

	public SmafPlayer(byte[] data) {
		this(new SmafData(data));
	}

	public SmafPlayer(SmafData data) {
		setData(data);
	}

	public int getX() {
		return 0;
	}

	public int getY() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public void setBounds(int x, int y, int width, int height) {

	}

	public int getOriginX() {
		return 0;
	}

	public int getOriginY() {
		return 0;
	}

	public void setOrigin(int offset_x, int offset_y) {

	}

	public int getMediaWidth() {
		return 0;
	}

	public int getMediaHeight() {
		return 0;
	}

	public void setData(MediaData data) {
		if (!(data instanceof SmafData)) {
			throw new IllegalArgumentException();
		}
		setData((SmafData) data);
	}

	public void setData(SmafData data) {
		int state = getState();
		if (state == PLAYING || state == PAUSED) {
			throw new IllegalStateException();
		}
		if (!MMFPlayer.initialize()) {
			return;
		}
		error = false;
		if (sound != 0) {
			MMFPlayer.getMaDll().close(sound);
			sound = 0;
		}
		this.data = data;
		if (data == null) {
			return;
		}
		try {
			sound = MMFPlayer.getMaDll().load(data.data);
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
		}
	}

	public void play() {
		play(1);
	}

	public void play(boolean isRepeat) {
		play(isRepeat ? 0 : 1);
	}

	public void play(int count) {
		if (getState() != READY) {
//			throw new IllegalStateException();
			return;
		}
		MMFPlayer.getMaDll().start(sound, count);
	}

	public void stop() {
		int state = getState();
		if (state != READY && state != PLAYING) {
//			throw new IllegalStateException();
			return;
		}
		MMFPlayer.getMaDll().stop(sound);
	}

	public void pause() {
		int state = getState();
		if (state != READY && state != PLAYING) {
//			throw new IllegalStateException();
			return;
		}
		MMFPlayer.getMaDll().pause(sound);
	}

	public void resume() {
		int state = getState();
		if (state != PAUSED) {
//			throw new IllegalStateException();
			return;
		}
		MMFPlayer.getMaDll().resume(sound);
	}

	public int getState() {
		if (error) {
			return ERROR;
		}
		if (data == null || sound == 0) {
			return NO_DATA;
		}
		int status = MMFPlayer.getMaDll().getStatus(sound);
		if (status == MaDll.STATE_PAUSED) {
			return PAUSED;
		} else if (status == MaDll.STATE_PLAYING) {
			return PLAYING;
		} else {
			return READY;
		}
	}

	public int getSpeed() {
		// TODO
		return speed;
	}

	public int getTranspose() {
		// TODO
		return transpose;
	}

	public int getVolume() {
		// TODO
		return volume;
	}

	public void seek(int time) {
		// TODO
		if (sound == 0) {
			throw new IllegalStateException();
		}
		if (time < 0) {
			throw new IllegalArgumentException();
		}
		MMFPlayer.getMaDll().seek(sound, time);
	}

	public void setPlayEnd(int pos) {
		// TODO
	}

	public void setSpeed(int speed) {
		if (sound == 0) {
			throw new IllegalStateException();
		}
		if (speed < 70 || speed > 130) {
			throw new IllegalArgumentException();
		}
		MMFPlayer.getMaDll().setTempo(sound, this.speed = speed);
	}

	public void setTranspose(int shift) {
		if (sound == 0) {
			throw new IllegalStateException();
		}
		if (shift < -12 || shift > 12) {
			throw new IllegalArgumentException();
		}
		MMFPlayer.getMaDll().setPitch(sound, this.transpose = shift);
	}

	public void setVolume(int volume) {
		if (sound == 0) {
			throw new IllegalStateException();
		}
		if (volume < 0 || volume > 127) {
			throw new IllegalArgumentException();
		}
		MMFPlayer.getMaDll().setVolume(sound, this.volume = volume);
	}

	public int getCurrent() {
		if (sound == 0) {
			throw new IllegalStateException();
		}
		return MMFPlayer.getMaDll().getPosition(sound);
	}

	// TODO events

	public void addMediaPlayerListener(MediaPlayerListener l) {
		mediaListeners.add(l);
	}

	public void addSmafPlayerListener(SmafPlayerListener l) {
		smafListeners.add(l);
	}

	public void removeMediaPlayerListener(MediaPlayerListener l) {
		mediaListeners.remove(l);
	}

	public void removeSmafPlayerListener(SmafPlayerListener l) {
		smafListeners.remove(l);
	}

	protected void finalize() {
		try {
			if (sound != 0) {
				MMFPlayer.getMaDll().close(sound);
				sound = 0;
			}
		} catch (Exception ignored) {}
	}
}
