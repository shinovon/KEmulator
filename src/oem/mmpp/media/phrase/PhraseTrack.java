/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package mmpp.media.phrase;

import emulator.media.mmf.PhraseTrackImpl;

public class PhraseTrack {
	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int ENDING = 4;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;

	private final PhraseTrackImpl impl;
	private PhraseData phrase;
	private PhraseTrack master;
	private PhraseTrackListener listener;

	PhraseTrack(PhraseTrackImpl impl) {
		this.impl = impl;
	}

	public void setPhraseData(PhraseData phrase) {
		impl.setPhrase(phrase != null ? phrase.data : null);
		this.phrase = phrase;
	}

	public PhraseData getPhraseData() {
		return phrase;
	}

	public void removePhraseData() {
		impl.removePhrase();
		phrase = null;
	}

	public void setMaster(PhraseTrack master) {
		this.master = master;
		impl.setSubjectTo(master.impl);
	}

	public PhraseTrack getMaster() {
		return master;
	}

	public int getNumber() {
		return impl.getID();
	}

	public int getPanpot() {
		return impl.getPanpot();
	}

	public void setPanpot(int panpot) {
		impl.setPanpot(panpot);
	}

	public int getVolume() {
		return impl.getVolume();
	}

	public void setVolume(int volume) {
		impl.setVolume(volume);
	}

	public boolean getMute() {
		return impl.isMute();
	}

	public void setMute(boolean mute) {
		impl.mute(mute);
	}

	public int getState() {
		return impl.getState();
	}

	public void play(int loop) {
		impl.play(loop);
	}

	public void play() {
		impl.play(1);
	}

	public void pause() {
		impl.pause();
	}

	public void resume() {
		impl.resume();
	}

	public void stop() {
		impl.stop();
	}

	public int getPlayingTime() {
		// TODO what does this mean?
		return getPosition();
	}

	public int getPosition() {
		return impl.getPosition();
	}

	public void seek(int pos) {
		impl.seek(pos);
	}

	public int getIsMaster() {
		// TODO
		return master != null ? 1 : 0;
	}

	public void setPhraseTrackListener(PhraseTrackListener listener) {
		this.listener = listener;
	}

	public void _redirectEvent(int event) {
		if (listener != null) {
			listener.eventNotify(getNumber(), event);
		}
	}
}
