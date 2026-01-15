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

abstract class PhraseTrackBase {
	public static final int NO_DATA = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	public static final int PAUSED = 5;
	public static final int DEFAULT_VOLUME = 100;
	public static final int DEFAULT_PANPOT = 64;
	private int state;
	int volume = DEFAULT_VOLUME;
	int panpot = DEFAULT_PANPOT;
	boolean mute;
	int id;

	PhraseTrackBase(int id) {
		this.id = id;
	}

	public void removePhrase() {
	}

	public void play() {
		state = PLAYING;
	}

	public void play(int loop) {
		state = PLAYING;
	}

	public void stop() {
		state = PAUSED;
	}

	public void pause() {
		state = PAUSED;
	}

	public void resume() {
		state = PLAYING;
	}

	public int getState() {
		return state;
	}

	public void setVolume(int value) {
		this.volume = value;
	}

	public int getVolume() {
		return volume;
	}

	public void setPanpot(int value) {
		this.panpot = value;
	}

	public int getPanpot() {
		return panpot;
	}

	public void mute(boolean mute) {
		this.mute = mute;
	}

	public boolean isMute() {
		return mute;
	}

	public int getID() {
		return id;
	}

	public void setEventListener(PhraseTrackListener l) {
	}
}