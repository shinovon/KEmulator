package com.j_phone.amuse;

import emulator.media.mmf.MMFPlayer;

public class PhrasePlayer {
	protected int trackCount;
	protected PhraseTrack[] tracks;
	protected boolean[] useFlag;
	protected static PhrasePlayer phrasePlayer;

	PhrasePlayer() {
		MMFPlayer.initialize();
		MMFPlayer.getMaDll().phraseInitialize();
		tracks = new PhraseTrack[MMFPlayer.getMaDll().getMaxTracks()];
	}

	public static PhrasePlayer getPlayer() {
		if (phrasePlayer == null) phrasePlayer = new PhrasePlayer();
		return phrasePlayer;
	}

	public PhraseTrack getTrack() {
		int id = -1;
		for (int i = 0; i < tracks.length; ++i) {
			if (tracks[i] == null) {
				id = i;
				break;
			}
		}
		if (id == -1) {
			throw new IllegalStateException();
		}
		PhraseTrack t = new PhraseTrack(id);
		tracks[id] = t;
		trackCount++;
		return t;
	}

	public int getTrackCount() {
		return tracks.length;
	}

	public PhraseTrack getTrack(int id) {
		return tracks[id];
	}

	public PhraseTrack getTrackPair() {
		// TODO
		return getTrack();
	}

	public PhraseTrack getTrackPair(int id) {
		// TODO
		return getTrack(id);
	}

	public void disposeTrack(PhraseTrack t) {
		int id = t.getID();
		if (tracks[id] == t) {
			t.removePhrase();
			tracks[id] = null;
		}
	}

	public void kill() {
//		for (int i = 0; i < tracks.length; ++i) {
//			if (tracks[i] == null) continue;
//			tracks[i].removePhrase();
//		}
		MMFPlayer.getMaDll().phraseKill();
	}

	public void pause() {
		for (int i = 0; i < tracks.length; ++i) {
			if (tracks[i] == null) continue;
			tracks[i].pause();
		}
	}

	public void resume() {
		for (int i = 0; i < tracks.length; ++i) {
			if (tracks[i] == null) continue;
			tracks[i].resume();
		}
	}
}
