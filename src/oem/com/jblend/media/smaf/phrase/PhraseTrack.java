package com.jblend.media.smaf.phrase;

import emulator.media.mmf.MMFPlayer;

public class PhraseTrack extends PhraseTrackBase {
	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;

	private Phrase phrase;
	private PhraseTrack master;
    boolean stub;
	boolean playing;

	PhraseTrack(int id) {
		super(id);
	}

	public Phrase getPhrase() {
		return phrase;
	}

	public void setPhrase(Phrase phrase) {
		if (phrase != null) {
			try {
				MMFPlayer.getMaDll().phraseSetData(id, phrase.data);
			} catch (Exception ignored) {
				stub = true;
			}
		}
		this.phrase = phrase;
	}

	public void removePhrase() {
		if (!stub && phrase != null) {
			MMFPlayer.getMaDll().phraseRemoveData(id);
		}
		phrase = null;
	}

	public void play() {
        play(1);
	}

	public void play(int loops) {
        if (stub) {
			playing = true;
            return;
        }
		if (phrase == null || getState() != READY) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phrasePlay(id, loops);
	}

	public void stop() {
        if (stub) {
			playing = false;
            return;
        }
		if (phrase == null || getState() != PLAYING) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseStop(id);
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

	public boolean isMute() {
		return mute;
	}

	public int getID() {
		return id;
	}

	public void setSubjectTo(PhraseTrack master) {
		this.master = master;
		if (phrase == null || stub) {
			return;
		}
		MMFPlayer.getMaDll().phraseSetLink(id, 0);
		if (master != null) {
			MMFPlayer.getMaDll().phraseSetLink(id, 1L << master.id);
		}
	}

	public PhraseTrack getSyncMaster() {
		return master;
	}
}