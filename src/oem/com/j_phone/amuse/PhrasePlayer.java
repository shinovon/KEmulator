package com.j_phone.amuse;

public class PhrasePlayer {
	protected int trackCount;
	protected PhraseTrack[] tracks;
	protected boolean[] useFlag;
	protected static PhrasePlayer phrasePlayer;

	public static PhrasePlayer getPlayer() {
		if (phrasePlayer == null) phrasePlayer = new PhrasePlayer();
		return phrasePlayer;
	}

	public PhraseTrack getTrack() {
		return new PhraseTrack();
	}

	public int getTrackCount() {
		return 16;
	}

	public PhraseTrack getTrack(int paramInt) {
		return new PhraseTrack();
	}

	public PhraseTrack getTrackPair() {
		return null;
	}

	public PhraseTrack getTrackPair(int paramInt) {
		return null;
	}

	public void disposeTrack(PhraseTrack paramPhraseTrack) {
	}

	public void kill() {
	}

	public void pause() {
	}

	public void resume() {
	}
}
