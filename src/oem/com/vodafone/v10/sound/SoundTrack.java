/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package com.vodafone.v10.sound;

import emulator.media.mmf.IPhraseEventRedirect;
import emulator.media.mmf.PhraseTrackImpl;

public class SoundTrack implements IPhraseEventRedirect {
	public static final int NO_DATA = 0;
	public static final int PAUSED = 3;
	public static final int PLAYING = 2;
	public static final int READY = 1;

	private final PhraseTrackImpl impl;
	private Sound phrase;
	private SoundTrack master;
	private SoundTrackListener listener;

	SoundTrack(PhraseTrackImpl impl) {
		this.impl = impl;
	}

	public Sound getSound() {
		return phrase;
	}

	public void setSound(Sound phrase) {
		impl.setPhrase(phrase != null ? phrase.data : null);
		this.phrase = phrase;
	}

	public void play() {
		impl.play(1);
	}

	public void play(int loops) {
		impl.play(loops);
	}

	public void stop() {
		impl.stop();
	}

	public void pause() {
		impl.pause();
	}

	public void resume() {
		impl.resume();
	}

	public void setVolume(int volume) {
		impl.setVolume(volume);
	}

	public void removeSound() {
		impl.removePhrase();
		phrase = null;
	}

	public void setEventListener(SoundTrackListener l) {
		this.listener = l;
	}

	public void _redirectEvent(int event) {
		if (listener != null) {
			listener.eventOccurred(event);
		}
	}

	public SoundTrack getSyncMaster() {
		return master;
	}

	public void setSubjectTo(SoundTrack master) {
		this.master = master;
		impl.setSubjectTo(master.impl);
	}

	public int getState() {
		return impl.getState();
	}

	public void setPanpot(int panpot) {
		impl.setPanpot(panpot);
	}

	public int getVolume() {
		return impl.getVolume();
	}

	public int getPanpot() {
		return impl.getPanpot();
	}

	public int getID() {
		return impl.getID();
	}
}