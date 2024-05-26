package com.pantech.titan;

import com.samsung.util.AudioClip;

public class PantechAudio {

	private AudioClip samsungImpl;

	public PantechAudio(int n, String s) {
		try {
			samsungImpl = new AudioClip(AudioClip.TYPE_MMF, s);
		} catch (Exception ignored) {}
	}

	public void start(int n1, int n2) {
		try {
			if (samsungImpl != null) samsungImpl.play(n1, 5);
		} catch (Exception ignored) {}
	}

	public void stop() {
		try {
			if (samsungImpl != null) samsungImpl.stop();
		} catch (Exception ignored) {}
	}
}
