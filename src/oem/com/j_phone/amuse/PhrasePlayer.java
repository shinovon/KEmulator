/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package com.j_phone.amuse;

import emulator.media.mmf.PhrasePlayerImpl;
import emulator.media.mmf.PhraseTrackImpl;

public class PhrasePlayer {
	private static PhrasePlayer phrasePlayer;
	private final PhrasePlayerImpl impl;

	protected int trackCount;
	protected PhraseTrack[] tracks;
	protected boolean[] useFlag;

	PhrasePlayer() {
		impl = PhrasePlayerImpl.open(true);
		tracks = new PhraseTrack[trackCount = impl.getTracksCount()];
		useFlag = new boolean[trackCount];
	}

	public static PhrasePlayer getPlayer() {
		if (phrasePlayer == null) phrasePlayer = new PhrasePlayer();
		return phrasePlayer;
	}

	public PhraseTrack getTrack() {
		PhraseTrackImpl impl = this.impl.createTrack();
		useFlag[impl.getID()] = true;
		PhraseTrack t = tracks[impl.getID()] = new PhraseTrack(impl);
		impl.setEventListener(t);
		return t;
	}

	public int getTrackCount() {
		return trackCount;
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
			useFlag[id] = false;
			impl.disposeTrack(id);
			tracks[id] = null;
		}
	}

	public void kill() {
		impl.kill();
		for (int i = 0; i < tracks.length; ++i) {
			tracks[i] = null;
		}
	}

	public void pause() {
		impl.pause();
	}

	public void resume() {
		impl.resume();
	}
}
