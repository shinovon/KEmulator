/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package emulator.media.mmf;

public class PhraseTrackImpl {

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
	private PhraseTrackImpl master;
	private boolean playing;
	private int volume = DEFAULT_VOLUME;
	private int panpot = DEFAULT_PANPOT;
	private boolean mute;
	private IPhraseEventRedirect listener;

	PhraseTrackImpl(int id) {
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
		if (data != null) {
			try {
				MMFPlayer.getMaDll().phraseSetData(id, data);
			} catch (Exception ignored) {
				stub = true;
			}
		}
		this.phrase = data;
	}

	public void removePhrase() {
		if (!stub && phrase != null) {
			if (getState() != READY) stop();
			MMFPlayer.getMaDll().phraseRemoveData(id);
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
		if (loops > 255) {
			loops = 255;
		} else if (loops < 0) {
			loops = 0;
		}
		MMFPlayer.getMaDll().phrasePlay(id, loops);
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
		MMFPlayer.getMaDll().phraseStop(id);
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
		MMFPlayer.getMaDll().phrasePause(id);
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
		MMFPlayer.getMaDll().phraseRestart(id);
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
		return MMFPlayer.getMaDll().phraseGetStatus(id);
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

	public void mute(boolean mute) {
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

	public int getPosition() {
		if (stub) {
			return 0;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		return (int) MMFPlayer.getMaDll().phraseGetPosition(id);
	}

	public int getLength() {
		if (stub) {
			return 0;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		return (int) MMFPlayer.getMaDll().phraseGetLength(id);
	}

	public void seek(int pos) {
		if (stub) {
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (pos < 0) {
			throw new IllegalArgumentException();
		}
		MMFPlayer.getMaDll().phraseSeek(id, pos);
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

	public void setSubjectTo(PhraseTrackImpl master) {
		this.master = master;
		if (phrase == null || stub) {
			return;
		}
		MMFPlayer.getMaDll().phraseSetLink(id, 0);
		if (master != null) {
			MMFPlayer.getMaDll().phraseSetLink(id, 1L << master.id);
		}
	}

	public PhraseTrackImpl getSyncMaster() {
		return master;
	}

}
