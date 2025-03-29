package com.nec.media;

public final class Media {
	public static final AudioClip getAudioClip(String location)
			throws IllegalArgumentException {
		return new AudioClip() {
			@Override
			public void addAudioListener(AudioListener paramAudioListener) {

			}

			@Override
			public void play() throws IllegalStateException {

			}

			@Override
			public void stop() throws IllegalStateException {

			}

			@Override
			public int getLapsedTime() throws IllegalStateException {
				return 0;
			}

			@Override
			public int getTime() throws IllegalStateException {
				return 0;
			}

			@Override
			public int getChannel() throws IllegalStateException {
				return 0;
			}

			@Override
			public int getTempo() throws IllegalStateException {
				return 0;
			}

			@Override
			public void setLoopCount(int paramInt) throws IllegalArgumentException, IllegalStateException {

			}

			@Override
			public AudioListener getAudioListener() {
				return null;
			}
		};
	}
}
