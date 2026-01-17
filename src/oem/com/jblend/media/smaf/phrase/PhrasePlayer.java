/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package com.jblend.media.smaf.phrase;

import emulator.media.mmf.AudioPhraseTrackImpl;
import emulator.media.mmf.PhrasePlayerImpl;
import emulator.media.mmf.PhraseTrackImpl;

public class PhrasePlayer {
	private static PhrasePlayer phrasePlayer;

	private PhrasePlayerImpl impl;

	protected int trackCount;
	protected int audioTrackCount;

	private PhraseTrack[] tracks;
	private AudioPhraseTrack[] audioTracks;

	PhrasePlayer() {
		impl = PhrasePlayerImpl.open(true);
		tracks = new PhraseTrack[trackCount = impl.getTracksCount()];
		audioTracks = new AudioPhraseTrack[audioTrackCount = impl.getAudioTracksCount()];
	}

	public static PhrasePlayer getPlayer() {
		if (phrasePlayer == null) phrasePlayer = new PhrasePlayer();
		return phrasePlayer;
	}

	public void disposePlayer() {
		impl.close();
		phrasePlayer = null;
	}

	public PhraseTrack getTrack() {
		PhraseTrackImpl t = impl.createTrack();
		return tracks[t.getID()] = new PhraseTrack(t);
	}

	public AudioPhraseTrack getAudioTrack() {
		AudioPhraseTrackImpl t = impl.createAudioTrack();
		return audioTracks[t.getID()] = new AudioPhraseTrack(t);
	}

	public int getTrackCount() {
		return trackCount;
	}

	public int getAudioTrackCount() {
		return audioTrackCount;
	}

	public PhraseTrack getTrack(int track) {
		return tracks[track];
	}

	public AudioPhraseTrack getAudioTrack(int track) {
		return audioTracks[track];
	}

	public void disposeTrack(PhraseTrack t) {
		int id = t.getID();
		if (tracks[id] == t) {
			impl.disposeTrack(id);
			tracks[id] = null;
		}
	}

	public void disposeAudioTrack(AudioPhraseTrack t) {
		int id = t.getID();
		if (audioTracks[id] == t) {
			impl.disposeAudioTrack(id);
			audioTracks[id] = null;
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