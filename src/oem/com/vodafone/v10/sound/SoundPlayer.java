/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package com.vodafone.v10.sound;

import emulator.media.mmf.PhrasePlayerImpl;
import emulator.media.mmf.PhraseTrackImpl;

public class SoundPlayer {

	private static SoundPlayer soundPlayer;

	private PhrasePlayerImpl impl;

	private SoundTrack[] tracks;

	SoundPlayer() {
		impl = PhrasePlayerImpl.open(true);
		tracks = new SoundTrack[impl.getTracksCount()];
	}

	public static SoundPlayer getPlayer() {
		if (soundPlayer == null) soundPlayer = new SoundPlayer();
		return soundPlayer;
	}

	public SoundTrack getTrack() {
		PhraseTrackImpl impl = this.impl.createTrack();
		SoundTrack t = tracks[impl.getID()] = new SoundTrack(impl);
		impl.setEventListener(t);
		return t;
	}

	public SoundTrack getTrack(int i) {
		return tracks[i];
	}

	public int getTrackCount() {
		return tracks.length;
	}

	public void kill() {
		impl.kill();
	}

	public void pause() {
		impl.pause();
	}

	public void resume() {
		impl.resume();
	}

	public void disposeTrack(SoundTrack t) {
		int id = t.getID();
		if (tracks[id] == t) {
			impl.disposeTrack(id);
			tracks[id] = null;
		}
	}

	public void disposePlayer() {
		impl.close();
		soundPlayer = null;
	}
}