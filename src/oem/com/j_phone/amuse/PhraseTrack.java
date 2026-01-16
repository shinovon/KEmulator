package com.j_phone.amuse;

import emulator.media.mmf.MMFPlayer;

public class PhraseTrack {
	public static final int DEFAULT_VOLUME = 100;
	private Phrase phrase;
	private final int id;
	private PhraseTrack master;
	private int volume = DEFAULT_VOLUME;
	private boolean mute;
	private boolean stub;
	private boolean playing;

	PhraseTrack(int id) {
		this.id = id;
	}

	public void setPhrase(Phrase phrase) {
		if (phrase != null) {
			try {
				MMFPlayer.getMaDll().phraseSetData(id, phrase.data);
			} catch (Exception e) {
				e.printStackTrace();
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
		if (phrase == null) {
			return;
		}
		if (stub) {
			playing = true;
			return;
		}
		MMFPlayer.getMaDll().phrasePlay(id, loops);
	}

	public void stop() {
		if (phrase == null) {
			return;
		}
		if (stub) {
			playing = false;
			return;
		}
		MMFPlayer.getMaDll().phraseStop(id);
	}

	public void pause() {
		if (phrase == null) {
			return;
		}
		if (stub) {
			playing = false;
			return;
		}
		MMFPlayer.getMaDll().phrasePause(id);
	}

	public void resume() {
		if (phrase == null) {
			return;
		}
		if (stub) {
			playing = true;
			return;
		}
		MMFPlayer.getMaDll().phraseRestart(id);
	}

	public boolean isPlaying() {
		if (phrase == null) {
			return false;
		}
		if (stub) {
			return playing;
		}
		return MMFPlayer.getMaDll().phraseGetStatus(id) == 3;
	}

	public Phrase getPhrase() {
		return phrase;
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

	public void setEventListener(PhraseTrackListener l) {
		// TODO
	}
}
