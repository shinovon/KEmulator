/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package emulator.media.mmf;

public class AudioPhraseTrackImpl {

	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int ENDING = 4;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;

	private final int id;
	private boolean stub;
	private byte[] phrase;
	private boolean playing;
	private int volume = DEFAULT_VOLUME;
	private int panpot = DEFAULT_PANPOT;
	private boolean mute;
	private IPhraseEventRedirect listener;
	int sound = -1;

	AudioPhraseTrackImpl(int id) {
		this.id = id;
	}

	public void setEventListener(IPhraseEventRedirect listener) {
		this.listener = listener;
	}

	void redirectEvent(int event) {
		if (listener != null) {
			listener._redirectEvent(event);
		}
	}

	public void setPhrase(byte[] data) {
		if (sound != -1) {
			MMFPlayer.getMaDll().audioPhraseRemoveData(sound);
			sound = -1;
		}
		if (data != null) {
			try {
				sound = MMFPlayer.getMaDll().audioPhraseSetData(data, mute ? 0 : volume, panpot);
				stub = false;
			} catch (Exception ignored) {
				stub = true;
			}
		}
		this.phrase = data;
	}

	public void removePhrase() {
		if (sound != -1) {
			if (getState() != READY) stop();
			MMFPlayer.getMaDll().audioPhraseRemoveData(sound);
			sound = -1;
		}
		phrase = null;
	}

	public void play(int loops) {
		if (stub) {
			playing = true;
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (getState() != READY) {
			return;
		}
		if (mute) {
			MMFPlayer.getMaDll().audioPhraseSetVolume(sound, 0);
		} else if (volume != DEFAULT_VOLUME) {
			MMFPlayer.getMaDll().audioPhraseSetVolume(sound, volume);
		}
		if (panpot != DEFAULT_PANPOT) {
			MMFPlayer.getMaDll().audioPhraseSetPanpot(sound, panpot);
		}
		if (loops > 255) {
			loops = 255;
		} else if (loops < 0) {
			loops = 0;
		}
		MMFPlayer.getMaDll().audioPhrasePlay(sound, loops);
	}

	public void stop() {
		if (stub) {
			playing = false;
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		int state = getState();
		if (state != PLAYING && state != PAUSED) {
			return;
		}
		MMFPlayer.getMaDll().audioPhraseStop(sound);
	}

	public void pause() {
		if (stub) {
			playing = false;
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (getState() != PLAYING) {
			return;
		}
		MMFPlayer.getMaDll().audioPhrasePause(sound);
	}

	public void resume() {
		if (stub) {
			playing = true;
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (getState() != PAUSED) {
			return;
		}
		MMFPlayer.getMaDll().audioPhraseRestart(sound);
	}

	public boolean isPlaying() {
		return getState() == PLAYING;
	}

	public int getState() {
		if (phrase == null) {
			return NO_DATA;
		}
		if (stub && playing) {
			return PLAYING;
		}
		int s = MMFPlayer.getMaDll().audioPhraseGetStatus(sound);
		if (s == 3) {
			return PLAYING;
		}
		if (s == 4) {
			return PAUSED;
		}
		return READY;
	}

	public void setVolume(int volume) {
		if (volume < 0 || volume > 127) {
			throw new IllegalArgumentException();
		}
		this.volume = volume;
		if (stub || phrase == null) {
			return;
		}
		if (!mute) {
			MMFPlayer.getMaDll().audioPhraseSetVolume(sound, volume);
		}
	}

	public void setPanpot(int panpot) {
		if (panpot < 0 || panpot > 127) {
			throw new IllegalArgumentException();
		}
		this.panpot = panpot;
		if (stub || phrase == null) {
			return;
		}
		MMFPlayer.getMaDll().audioPhraseSetPanpot(sound, panpot);
	}

	public void mute(boolean mute) {
		this.mute = mute;
		if (stub || phrase == null) {
			return;
		}
		if (!mute) {
			MMFPlayer.getMaDll().audioPhraseSetVolume(sound, volume);
		} else {
			MMFPlayer.getMaDll().audioPhraseSetVolume(sound, 0);
		}
	}

	public boolean isMute() {
		return mute;
	}

	public int getVolume() {
		return volume;
	}

	public int getPanpot() {
		return panpot;
	}

	public int getID() {
		return id;
	}

}
