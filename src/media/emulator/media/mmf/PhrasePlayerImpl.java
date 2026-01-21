/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package emulator.media.mmf;

public class PhrasePlayerImpl {

	PhraseTrackImpl[] tracks;
	AudioPhraseTrackImpl[] audioTracks;
	private boolean stub;
	private boolean supportsAudioTracks;

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
			if (audioTracks && MMFPlayer.getMaDll().supportsAudioTracks()) {
				inst.audioTracks = new AudioPhraseTrackImpl[MMFPlayer.getMaDll().getMaxAudioTracks()];
				inst.supportsAudioTracks = true;
			} else {
				inst.audioTracks = new AudioPhraseTrackImpl[4];
			}
		} catch (Exception e) {
			e.printStackTrace();

			inst.tracks = new PhraseTrackImpl[4];
			inst.audioTracks = new AudioPhraseTrackImpl[4];
			inst.stub = true;
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
		PhraseTrackImpl t = new PhraseTrackImpl(id, this);
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
		if (audioTracks != null) {
			for (int i = 0; i < audioTracks.length; ++i) {
				if (audioTracks[i] == null) continue;
				audioTracks[i].pause();
			}
		}
	}

	public void resume() {
		for (int i = 0; i < tracks.length; ++i) {
			if (tracks[i] == null) continue;
			tracks[i].resume();
		}
		if (audioTracks != null) {
			for (int i = 0; i < audioTracks.length; ++i) {
				if (audioTracks[i] == null) continue;
				audioTracks[i].resume();
			}
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
		return supportsAudioTracks;
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

	void audioEventCallback(int id, int mode) {
		// TODO
		if (mode != 2) return;
		int type = -1;
		try {
			for (AudioPhraseTrackImpl track : audioTracks) {
				if (track.sound != id) continue;

				track.redirectEvent(type);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void setLinks() {
		for (PhraseTrackImpl track : tracks) {
			if (track == null || track.phrase == null || track.stub) {
				continue;
			}
			int slave = 0;
			for (int j = 0; j < tracks.length; j++) {
				if (tracks[j] == null) continue;
				if (tracks[j].getSyncMaster() == track) {
					slave |= 1 << j;
				}
			}
			try {
				MMFPlayer.getMaDll().phraseSetLink(track.getID(), slave);
			} catch (Exception ignored) {}
		}
	}
}
