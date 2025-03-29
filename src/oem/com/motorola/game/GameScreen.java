package com.motorola.game;

import com.siemens.mp.color_game.GameCanvas;

public abstract class GameScreen extends GameCanvas {

	protected GameScreen() {
		super(false);
	}

	public void playBackgroundMusic(BackgroundMusic bm, boolean b) {}

	public void playSoundEffect(SoundEffect s, int n1, int n2) {}

	public void stopAllSoundEffects() {}
}
