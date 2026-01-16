/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package mmpp.media.phrase;

import emulator.media.mmf.MMFPlayer;

public class PhraseTrack {
	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int ENGING = 4;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;

	private PhraseData phrase;
	private PhraseTrack master;
	private final int id;
	boolean stub;
	boolean playing;
	boolean mute;
	int volume = DEFAULT_VOLUME;
	int panpot = DEFAULT_PANPOT;

	PhraseTrack(int id) {
		this.id = id;
	}

	public void setPhraseData(PhraseData phrase) {
		if (phrase != null) {
			try {
				MMFPlayer.getMaDll().phraseSetData(id, phrase.data);
			} catch (Exception ignored) {
				stub = true;
			}
		}
		this.phrase = phrase;
	}

	public PhraseData getPhraseData() {
		return phrase;
	}

	public void removePhraseData() {
		if (!stub && phrase != null) {
			MMFPlayer.getMaDll().phraseRemoveData(id);
		}
		phrase = null;
	}

	public void setMaster(PhraseTrack master) {
		this.master = master;
	}

	public PhraseTrack getMaster() {
		return master;
	}

	public void setPhraseTrackListener(PhraseTrackListener listener) {
		// TODO
	}

	public int getNumber() {
		return id;
	}

	public int getPanpot() {
		return panpot;
	}

	public void setPanpot(int panpot) {
		if (panpot < 0 || panpot > 127) {
			throw new IllegalArgumentException();
		}
		this.panpot = panpot;
		if (stub) {
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseSetPanpot(id, panpot);
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		if (volume < 0 || volume > 127) {
			throw new IllegalArgumentException();
		}
		this.volume = volume;
		if (stub) {
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (!mute) {
			MMFPlayer.getMaDll().phraseSetVolume(id, volume);
		}
	}

	public boolean getMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
		if (stub) {
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (!mute) {
			MMFPlayer.getMaDll().phraseSetVolume(id, volume);
		} else {
			MMFPlayer.getMaDll().phraseSetVolume(id, 0);
		}
	}

	public int getState() {
		if (phrase == null) {
			return NO_DATA;
		}
		if (stub && playing) {
			return PLAYING;
		}
		return MMFPlayer.getMaDll().phraseGetStatus(id);
	}

	public void play(int loop) {
		if (stub) {
			playing = true;
			return;
		}
		if (phrase == null || getState() != READY) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phrasePlay(id, loop);
	}

	public void play() {
		play(1);
	}

	public void pause() {
		if (stub) {
			playing = false;
			return;
		}
		if (phrase == null || getState() != PLAYING) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phrasePause(id);
	}

	public void resume() {
		if (stub) {
			playing = true;
			return;
		}
		if (phrase == null || getState() != PAUSED) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseRestart(id);
	}

	public void stop() {
		if (stub) {
			playing = false;
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseStop(id);
	}

	public int getPlayingTime() {
		// TODO
		return 0;
	}

	public int getPosition() {
		// TODO
		return 0;
	}

	public void seek(int pos) {
		// TODO
	}

	public int getIsMaster() {
		// TODO
		return master != null ? 1 : 0;
	}
}
