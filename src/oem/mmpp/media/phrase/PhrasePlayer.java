/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package mmpp.media.phrase;

import emulator.media.mmf.PhrasePlayerImpl;
import emulator.media.mmf.PhraseTrackImpl;

public class PhrasePlayer {
	private static PhrasePlayer phrasePlayer;

	private PhrasePlayerImpl impl;

	PhraseTrack[] tracks;

	PhrasePlayer() {
		impl = PhrasePlayerImpl.open(false);
		tracks = new PhraseTrack[impl.getTracksCount()];
	}

	public void closePlayer() {
		impl.close();
		phrasePlayer = null;
	}

	public void closeTrack(PhraseTrack track) {
		int id = track.getNumber();
		if (tracks[id] == track) {
			impl.disposeTrack(id);
			tracks[id] = null;
		}
	}

	public static PhrasePlayer getPlayer() {
		if (phrasePlayer == null) phrasePlayer = new PhrasePlayer();
		return phrasePlayer;
	}

	public PhraseTrack getTrack() {
		PhraseTrackImpl t = impl.createTrack();
		return tracks[t.getID()] = new PhraseTrack(t);
	}

	public int getTrackCount() {
		return tracks.length;
	}

	public static void handleEvent(int ch, int mode) {
		// TODO
	}

	public void kill() {
		impl.kill();
		for (int i = 0; i < tracks.length; ++i) {
			tracks[i] = null;
		}
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
