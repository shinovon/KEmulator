package com.jblend.media.smaf.phrase;

import emulator.media.mmf.MMFPlayer;

public class PhraseTrack extends PhraseTrackBase {
	private Phrase phrase;
	private PhraseTrack master;
    boolean stub;

	PhraseTrack(int id) {
		super(id);
	}

	public Phrase getPhrase() {
		return phrase;
	}

	public void setPhrase(Phrase phrase) {
		if (this.phrase != null) {
			this.phrase.references--;
		}
		if (phrase != null) {
			try {
				MMFPlayer.getMaDll().phraseSetData(id, phrase.data);
			} catch (Exception ignored) {
				stub = true;
			}
			phrase.references++;
		}
		this.phrase = phrase;
	}

	public void removePhrase() {
		if (stub) return;
		if (phrase != null) {
			MMFPlayer.getMaDll().phraseRemoveData(id);
			phrase.references--;
			phrase = null;
		}
	}

	public void play() {
        if (phrase == null) {
            return;
        }
        if (stub) {
            return;
        }
		MMFPlayer.getMaDll().phrasePlay(id, 0);
	}

	public void play(int loops) {
		if (phrase == null) {
			return;
		}
        if (stub) {
            return;
        }
		MMFPlayer.getMaDll().phrasePlay(id, loops);
	}

	public void stop() {
        if (phrase == null) {
            return;
        }
        if (stub) {
            return;
        }
		MMFPlayer.getMaDll().phraseStop(id);
	}

	public void pause() {
        if (phrase == null) {
            return;
        }
        if (stub) {
            return;
        }
		MMFPlayer.getMaDll().phrasePause(id);
	}

	public void resume() {
        if (phrase == null) {
            return;
        }
        if (stub) {
            return;
        }
		MMFPlayer.getMaDll().phraseRestart(id);
	}

	public boolean isPlaying() {
		return getState() == PLAYING;
	}

    public int getState() {
        // TODO
        if (phrase == null) {
            return NO_DATA;
        }
        return READY;
    }

	public void setVolume(int volume) {
		if (phrase == null || stub) {
			return;
		}
		this.volume = volume;
		if (!mute) {
			MMFPlayer.getMaDll().phraseSetVolume(id, volume);
		}
	}

	public void setPanpot(int panpot) {
		if (phrase == null || stub) {
			return;
		}
		this.panpot = panpot;
		MMFPlayer.getMaDll().phraseSetPanpot(id, panpot);
	}

	public void mute(boolean mute) {
		if (phrase == null || stub) {
			return;
		}
		if (!mute) {
			MMFPlayer.getMaDll().phraseSetVolume(id, volume);
		} else {
			MMFPlayer.getMaDll().phraseSetVolume(id, 0);
		}
		this.mute = mute;
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