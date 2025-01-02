package com.j_phone.amuse;

public class PhraseTrack {
	public static final int DEFAULT_VOLUME = 100;
	private Phrase phrase;

	PhraseTrack() {
	}

	public void setPhrase(Phrase paramPhrase) {
		phrase = paramPhrase;
	}

	public void removePhrase() {
	}

	public void play() {
	}

	public void play(int paramInt) {
	}

	public void stop() {
	}

	public void pause() {
	}

	public void resume() {
	}

	public boolean isPlaying() {
		return false;
	}

	public Phrase getPhrase() {
		return phrase;
	}

	public void setVolume(int paramInt) {
	}

	public void mute(boolean paramBoolean) {
	}

	public boolean isMute() {
		return false;
	}

	public int getID() {
		return 0;
	}

	public void setSubjectTo(PhraseTrack paramPhraseTrack) {
	}

	public PhraseTrack getSyncMaster() {
		return null;
	}

	public void setEventListener(PhraseTrackListener paramPhraseTrackListener) {
	}
}
