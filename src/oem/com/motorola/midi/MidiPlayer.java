package com.motorola.midi;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;

public class MidiPlayer {

	private static Player impl;

	public static void play(String s, int n) {
		stop();
		try {
			impl = Manager.createPlayer(s);
			impl.start();
		} catch (Exception ignored) {}
	}

	public static void stop() {
		if (impl == null) return;
		try {
			impl.close();
		} catch (Exception ignored) {}
		impl = null;
	}

	public static void playEffect(int n1, int n2, int n3, int n4, int n5) {}
}
