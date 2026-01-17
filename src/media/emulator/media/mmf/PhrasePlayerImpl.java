/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package emulator.media.mmf;

public class PhrasePlayerImpl {

	PhraseTrackImpl[] tracks;
	AudioPhraseTrackImpl[] audioTracks;
	boolean stub;

	private PhrasePlayerImpl() {
	}

	public static PhrasePlayerImpl open(boolean audioTracks) {
		PhrasePlayerImpl inst = new PhrasePlayerImpl();

		try {
			if (!MMFPlayer.initialize()) {
				throw new Exception();
			}
			MMFPlayer.getMaDll().phraseInitialize();
			inst.tracks = new PhraseTrackImpl[MMFPlayer.getMaDll().getMaxTracks()];
		} catch (Exception e) {
			e.printStackTrace();

			inst.tracks = new PhraseTrackImpl[16];
			inst.stub = true;
		}

		if (audioTracks) {
			inst.audioTracks = new AudioPhraseTrackImpl[16];
		}

		return inst;
	}

	public PhraseTrackImpl createTrack() {
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
		PhraseTrackImpl t = new PhraseTrackImpl(id);
		tracks[id] = t;
		return t;
	}

	public AudioPhraseTrackImpl createAudioTrack() {
		if (audioTracks == null) {
			throw new IllegalStateException();
		}
		int id = -1;
		for (int i = 0; i < audioTracks.length; ++i) {
			if (audioTracks[i] == null) {
				id = i;
				break;
			}
		}
		if (id == -1) {
			throw new IllegalStateException();
		}
		AudioPhraseTrackImpl t = new AudioPhraseTrackImpl(id);
		audioTracks[id] = t;
		return t;
	}

	public void disposeTrack(int id) {
		PhraseTrackImpl t = tracks[id];
		t.removePhrase();
		tracks[id] = null;
	}

	public void disposeAudioTrack(int id) {
		if (audioTracks == null) {
			throw new IllegalStateException();
		}
		AudioPhraseTrackImpl t = audioTracks[id];
		t.removePhrase();
		audioTracks[id] = null;
	}

	public void kill() {
		for (int i = 0; i < tracks.length; ++i) {
			if (tracks[i] == null) continue;
			tracks[i].removePhrase();
			tracks[i] = null;
		}
		if (audioTracks != null) {
			for (int i = 0; i < audioTracks.length; ++i) {
				if (audioTracks[i] == null) continue;
				audioTracks[i].removePhrase();
				audioTracks[i] = null;
			}
		}
		if (stub) return;
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

	public void close() {
		kill();
	}

	public int getTracksCount() {
		return tracks.length;
	}

	public int getAudioTracksCount() {
		return audioTracks.length;
	}

	public boolean isStub() {
		return stub;
	}

	public boolean audioTracksSupported() {
		return false;
	}

	void eventCallback(int ch, int mode) {
		try {
			PhraseTrackImpl track = tracks[ch];
			if (track == null) return;

			track.redirectEvent(mode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
