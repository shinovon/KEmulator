package com.j_phone.media;

import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Ticker;

public class MediaPlayer
		extends Canvas {
	public MediaPlayer(byte[] paramArrayOfByte) {
	}

	public MediaPlayer(String paramString)
			throws IOException {
	}

	public void setMediaData(byte[] paramArrayOfByte) {
	}

	public void setMediaData(String paramString)
			throws IOException {
	}

	public int getMediaWidth() {
		return 0;
	}

	public int getMediaHeight() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public void setContentPos(int paramInt1, int paramInt2) {
	}

	public void play() {
	}

	public void play(boolean paramBoolean) {
	}

	public void stop() {
	}

	public void pause() {
	}

	public void resume() {
	}

	public void setMediaPlayerListener(MediaPlayerListener paramMediaPlayerListener) {
	}

	protected void paint(Graphics g) {
	}

	protected void showNotify() {
		super.showNotify();
	}

	protected void hideNotify() {
		super.hideNotify();
	}

	public final void setFullScreenMode(boolean b) {
		super.setFullScreenMode(b);
	}

	public final Ticker getTicker() {
		return super.getTicker();
	}

	public final String getTitle() {
		return super.getTitle();
	}

	public final void setTicker(Ticker ticker) {
		super.setTicker(ticker);
	}

	public final void setTitle(String title) {
		super.setTitle(title);
	}
}
