package com.jblend.media;

import com.jblend.media.smaf.SmafPlayer;

import java.io.IOException;

public class MediaFactory {

	public static final int MEDIA_TYPE_SMAF = 1;
	public static final int MEDIA_TYPE_KARAOKE = 11;

	public static MediaPlayer getMediaPlayer(String name) throws IOException {
		return new SmafPlayer((byte[]) null);
	}

	public static MediaPlayer getMediaPlayer(byte[] data) {
		return new SmafPlayer(data);
	}

	public static MediaPlayer getMediaPlayer(String paramString, int paramInt) throws IOException {
		return null;
	}

	public static MediaPlayer getMediaPlayer(byte[] paramArrayOfByte, int paramInt) {
		return null;
	}
}