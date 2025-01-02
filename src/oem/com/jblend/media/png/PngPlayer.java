package com.jblend.media.png;

import com.jblend.media.MediaData;
import com.jblend.media.MediaImageOperator;
import com.jblend.media.MediaPlayer;
import com.jblend.media.MediaPlayerListener;

public class PngPlayer
		extends MediaPlayer
		implements MediaImageOperator {
	public PngPlayer() {
	}

	public PngPlayer(PngData paramPngData) {
		this();
	}

	public PngPlayer(byte[] paramArrayOfByte) {
		this();
	}

	public void setData(PngData paramPngData) {
	}

	public void setData(MediaData paramMediaData) {
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

	public synchronized void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
	}

	public int getOriginX() {
		return 0;
	}

	public int getOriginY() {
		return 0;
	}

	public void setOrigin(int paramInt1, int paramInt2) {
	}

	public int getMediaWidth() {
		return 0;
	}

	public int getMediaHeight() {
		return 0;
	}

	public void play() {
	}

	public void play(boolean paramBoolean) {
	}

	public void play(int paramInt) {
	}

	public void stop() {
	}

	public void pause() {
	}

	public void resume() {
	}

	public int getState() {
		return 0;
	}

	public void addMediaPlayerListener(MediaPlayerListener paramMediaPlayerListener) {
	}

	public void removeMediaPlayerListener(MediaPlayerListener paramMediaPlayerListener) {
	}
}
