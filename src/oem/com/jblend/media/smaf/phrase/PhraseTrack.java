package com.jblend.media.smaf.phrase;

import emulator.media.mmf.IPhraseEventRedirect;
import emulator.media.mmf.PhraseTrackImpl;

public class PhraseTrack extends PhraseTrackBase implements IPhraseEventRedirect {
	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;

	private final PhraseTrackImpl impl;
	private Phrase phrase;
	private PhraseTrack master;
	private PhraseTrackListener listener;

	PhraseTrack(PhraseTrackImpl impl) {
		super(impl.getID());
		this.impl = impl;
	}

	public Phrase getPhrase() {
		return phrase;
	}

	public void setPhrase(Phrase phrase) {
		impl.setPhrase(phrase != null ? phrase.data : null);
		this.phrase = phrase;
	}

	public void removePhrase() {
		impl.removePhrase();
		phrase = null;
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

	public boolean isPlaying() {
		return impl.isPlaying();
	}

	public int getState() {
		return impl.getState();
	}

	public void setVolume(int volume) {
		impl.setVolume(volume);
	}

	public void setPanpot(int panpot) {
		impl.setPanpot(panpot);
	}

	public void mute(boolean mute) {
		impl.mute(mute);
	}

	public boolean isMute() {
		return impl.isMute();
	}

	public int getID() {
		return impl.getID();
	}

	public void setSubjectTo(PhraseTrack master) {
		this.master = master;
		impl.setSubjectTo(master.impl);
	}

	public PhraseTrack getSyncMaster() {
		return master;
	}

	public void setEventListener(PhraseTrackListener l) {
		this.listener = l;
	}

	public void _redirectEvent(int event) {
		if (listener != null) {
			listener.eventOccurred(event);
		}
	}

}