package com.vodafone.v10.sound;

import emulator.media.mmf.MMFPlayer;

public class SoundTrack {
	public static final int NO_DATA = 0;
	public static final int PAUSED = 3;
	public static final int PLAYING = 2;
	public static final int READY = 1;
	private int id;
	private Sound phrase;
	private SoundTrackListener listener;
	private SoundTrack master;
	boolean stub;
	boolean playing;
	int volume;
	int panpot;
	boolean mute;

	public SoundTrack() {
	}

	SoundTrack(int id) {
		this.id = id;
	}

	public Sound getSound() {
		return phrase;
	}

	public void setSound(Sound phrase) {
		if (phrase != null) {
			try {
				MMFPlayer.getMaDll().phraseSetData(id, phrase.data);
			} catch (Exception e) {
				e.printStackTrace();
				stub = true;
			}
		}
		this.phrase = phrase;
	}

	public void play() {
		play(1);
	}

	public void play(int loops) {
		if (phrase == null) {
			return;
		}
		if (stub) {
			playing = true;
			return;
		}
		MMFPlayer.getMaDll().phrasePlay(id, loops);
	}

	public void stop() {
		if (stub) {
			playing = false;
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseStop(id);
	}

	public void pause() {
		if (stub) {
			playing = false;
			return;
		}
		if (phrase == null || getState() != PLAYING) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phrasePause(id);
	}

	public void resume() {
		if (stub) {
			playing = true;
			return;
		}
		if (phrase == null || getState() != PAUSED) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseRestart(id);
	}

	public void setVolume(int volume) {
		if (volume < 0 || volume > 127) {
			throw new IllegalArgumentException();
		}
		this.volume = volume;
		if (stub) {
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		if (!mute) {
			MMFPlayer.getMaDll().phraseSetVolume(id, volume);
		}
	}

	public void removeSound() {
		if (stub) return;
		if (phrase != null) {
			MMFPlayer.getMaDll().phraseRemoveData(id);
			phrase = null;
		}
	}

	public void setEventListener(SoundTrackListener l) {
		this.listener = l;
	}

	private void postEvent(int event) {
		if (listener != null) {
			listener.eventOccurred(event);
		}
	}

	public SoundTrack getSyncMaster() {
		return master;
	}

	public void setSubjectTo(SoundTrack master) {
		this.master = master;
		if (phrase == null || stub) {
			return;
		}
		MMFPlayer.getMaDll().phraseSetLink(id, 0);
		if (master != null) {
			MMFPlayer.getMaDll().phraseSetLink(id, 1L << master.id);
		}
	}

	public int getState() {
		if (phrase == null) {
			return NO_DATA;
		}
		if (stub && playing) {
			return PLAYING;
		}
		return MMFPlayer.getMaDll().phraseGetStatus(id);
	}

	public void setPanpot(int panpot) {
		if (panpot < 0 || panpot > 127) {
			throw new IllegalArgumentException();
		}
		this.panpot = panpot;
		if (stub) {
			return;
		}
		if (phrase == null) {
			throw new IllegalStateException();
		}
		MMFPlayer.getMaDll().phraseSetPanpot(id, panpot);
	}

	public int getVolume() {
		return volume;
	}

	public int getPanpot() {
		return panpot;
	}

	public int getID() {
		return id;
	}
}