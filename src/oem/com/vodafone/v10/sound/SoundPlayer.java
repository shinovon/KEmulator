package com.vodafone.v10.sound;

import emulator.media.mmf.MMFPlayer;

public class SoundPlayer {

	private static SoundPlayer soundPlayer;

	SoundTrack[] tracks;

	boolean stub;

	SoundPlayer() {
		try {
			MMFPlayer.initialize();
			MMFPlayer.getMaDll().phraseInitialize();
			tracks = new SoundTrack[MMFPlayer.getMaDll().getMaxTracks()];
		} catch (Exception e) {
			e.printStackTrace();

			tracks = new SoundTrack[4];
			stub = true;
		}
	}

	public static SoundPlayer getPlayer() {
		if (soundPlayer == null) soundPlayer = new SoundPlayer();
		return soundPlayer;
	}

	public SoundTrack getTrack() {
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
		SoundTrack t = new SoundTrack(id);
		tracks[id] = t;
		return t;
	}

	public SoundTrack getTrack(int i) {
		return tracks[i];
	}

	public int getTrackCount() {
		return tracks.length;
	}

	public void kill() {
		for (int i = 0; i < tracks.length; ++i) {
			if (tracks[i] == null) continue;
			tracks[i].removeSound();
		}
		if (stub) return;
		MMFPlayer.getMaDll().phraseKill();
	}

	public void disposePlayer() {
		kill();
		soundPlayer = null;
	}
}