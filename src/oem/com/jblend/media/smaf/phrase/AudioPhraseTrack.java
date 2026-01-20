/*
 * Copyright 2023 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jblend.media.smaf.phrase;

import emulator.media.mmf.AudioPhraseTrackImpl;

public class AudioPhraseTrack extends PhraseTrackBase {
	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;

	private final AudioPhraseTrackImpl impl;
	private PhraseTrackListener listener;
	private AudioPhrase phrase;

	AudioPhraseTrack(AudioPhraseTrackImpl impl) {
		super(impl.getID());
		this.impl = impl;
	}

	public AudioPhrase getPhrase() {
		return phrase;
	}

	public void setPhrase(AudioPhrase phrase) {
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