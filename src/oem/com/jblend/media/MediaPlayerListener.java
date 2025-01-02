package com.jblend.media;

public interface MediaPlayerListener {
	public void playerStateChanged(MediaPlayer player);

	public void playerRepeated(MediaPlayer player);
}