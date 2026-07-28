package com.sprintpcs.media;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.ToneControl;

public class Player {
	static int clipState = 0;
	static int currentPriority = 0;
	static Object current = null;
	static int foregroundClipLoopCount = -2;
	static int toneLoopCount = -2;
	static int backgroundClipLoopCount = -2;
	static javax.microedition.media.Player foregroundClipPlayer = null;
	static javax.microedition.media.Player backgroundClipPlayer = null;
	static javax.microedition.media.Player tonePlayer = null;
	private static ToneControl toneControl = null;
	static Clip foregroundClip = null;
	static DualTone tone = null;
	static Clip backgroundClip = null;
	static PlayerListener playerListener = null;

	public Player() {
	}

	public static void addPlayerListener(PlayerListener playerListener) {
		new PlayerListenerImpl(playerListener);
	}

	public static synchronized void play(Clip clip, int n) throws IllegalArgumentException {
		if (n < -1) {
			throw new IllegalArgumentException("Repeat must be -1 or greater");
		}
		if (clip == null || clip.priority < currentPriority) {
			return;
		}
		try {
			if (foregroundClipPlayer != null) {
				foregroundClipPlayer.close();
				foregroundClipPlayer = null;
			}
			switch (clipState) {
				case 0: {
					break;
				}
				case 1: {
					if (foregroundClipPlayer != null) {
						foregroundClipPlayer.close();
						foregroundClipPlayer = null;
					}
					if (playerListener == null) break;
					playerListener.playerUpdate(7, current);
					break;
				}
				case 6: {
					if (tonePlayer != null) {
						tonePlayer.close();
						tonePlayer = null;
					}
					if (playerListener == null) break;
					playerListener.playerUpdate(7, current);
					break;
				}
				case 4: {
					if (backgroundClipPlayer != null) {
						backgroundClipPlayer.stop();
					}
					if (playerListener == null) break;
					playerListener.playerUpdate(7, current);
					break;
				}
				default: {
					return;
				}
			}
			javax.microedition.media.Player foregroundClipPlayer = clip.createPlayer();
			foregroundClipLoopCount = n != -1 ? n + 1 : -1;
			foregroundClipPlayer.setLoopCount(foregroundClipLoopCount);
			PlayerListenerImpl class55 = new PlayerListenerImpl(playerListener);
			class55.addPlayerListener(foregroundClipPlayer);
			currentPriority = clip.priority;
			foregroundClip = clip;
			clipState = 1;
			current = clip;
			Vibrator.vibrate((int) clip.vibration);
			Player.foregroundClipPlayer = foregroundClipPlayer;
			foregroundClipPlayer.start();
			return;
		} catch (MediaException mediaException) {
			if (playerListener != null) {
				playerListener.playerUpdate(2, current);
			}
			System.out.println("Player.play() clip encountered a MediaException: " + mediaException.getMessage());
			mediaException.printStackTrace();
			clipState = 0;
			current = null;
			foregroundClip = null;
			currentPriority = 0;
			return;
		} catch (Exception exception) {
			clipState = 0;
			current = null;
			foregroundClip = null;
			currentPriority = 0;
			exception.printStackTrace();
			return;
		}
	}

	public static synchronized void play(DualTone dualTone, int n) throws IllegalArgumentException {
		if (n < -1) {
			throw new IllegalArgumentException("Repeat must be -1 or greater");
		}
		if (dualTone.anInt514 < currentPriority) {
			return;
		}
		try {
			switch (clipState) {
				case 1: {
					if (foregroundClipPlayer != null) {
						foregroundClipPlayer.close();
						foregroundClipPlayer = null;
					}
					if (playerListener == null) break;
					playerListener.playerUpdate(7, current);
					break;
				}
				case 4: {
					if (backgroundClipPlayer != null) {
						backgroundClipPlayer.stop();
					}
					if (playerListener == null) break;
					playerListener.playerUpdate(7, current);
					break;
				}
				case 6: {
					if (tonePlayer != null) {
						tonePlayer.close();
						tonePlayer = null;
					}
					if (playerListener == null) break;
					playerListener.playerUpdate(7, current);
					break;
				}
				default: {
					return;
				}
				case 0:
			}
			if (tonePlayer != null) {
				tonePlayer.close();
				tonePlayer = null;
			}
			tonePlayer = Manager.createPlayer("device://tone");
			tonePlayer.realize();
			toneControl = (ToneControl) tonePlayer.getControl("ToneControl");
			toneLoopCount = n != -1 ? n + 1 : -1;
			tonePlayer.setLoopCount(toneLoopCount);
			toneControl.setSequence(dualTone.aByteArray513);
			PlayerListenerImpl class55 = new PlayerListenerImpl(playerListener);
			class55.addPlayerListener(tonePlayer);
			currentPriority = dualTone.anInt514;
			current = dualTone;
			tone = dualTone;
			clipState = 6;
			tonePlayer.start();
			return;
		} catch (MediaException mediaException) {
			if (playerListener != null) {
				playerListener.playerUpdate(2, current);
			}
			mediaException.printStackTrace();
			System.out.println("Player.play(DualTone) encountered an exception: " + mediaException.getMessage());
			return;
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
	}

	public static synchronized void playBackground(Clip clip, int n) throws IllegalArgumentException {
		if (n < -1) {
			throw new IllegalArgumentException("Repeat must be -1 or greater");
		}
		if (clip == null) {
			return;
		}
		switch (clipState) {
			case 0: {
				clipState = 4;
				break;
			}
			default: {
				return;
			}
			case 1:
			case 4:
			case 6:
		}
		try {
			if (backgroundClipPlayer != null) {
				backgroundClipPlayer.close();
			}
			backgroundClipPlayer = clip.createPlayer();
			backgroundClipLoopCount = n != -1 ? n + 1 : -1;
			backgroundClipPlayer.setLoopCount(backgroundClipLoopCount);
			PlayerListenerImpl class55 = new PlayerListenerImpl(playerListener);
			class55.addPlayerListener(backgroundClipPlayer);
			backgroundClip = clip;
			if (clipState != 1 && clipState != 6) {
				current = backgroundClip;
				backgroundClipPlayer.start();
			}
			return;
		} catch (MediaException mediaException) {
			if (playerListener != null) {
				playerListener.playerUpdate(2, current);
			}
			mediaException.printStackTrace();
			System.out.println("playBackground() encountered a MediaException: " + mediaException.getMessage());
			clipState = 0;
			current = null;
			backgroundClip = null;
			return;
		} catch (Exception exception) {
			if (playerListener != null) {
				playerListener.playerUpdate(2, current);
			}
			System.out.println("playBackground() encountered an IOException: " + exception.getMessage());
			clipState = 0;
			current = null;
			backgroundClip = null;
			exception.printStackTrace();
			return;
		}
	}

	public static synchronized void pause() {
		block8:
		{
			try {
				switch (clipState) {
					case 1: {
						if (foregroundClipPlayer.getState() != 400) break;
						clipState = 2;
						foregroundClipPlayer.stop();
						break block8;
					}
					case 4: {
						if (backgroundClipPlayer.getState() != 400) break;
						clipState = 5;
						backgroundClipPlayer.stop();
					}
				}
				return;
			} catch (MediaException mediaException) {
				System.out.println("Player.pause encountered an exception: " + mediaException.getMessage());
				mediaException.printStackTrace();
				clipState = 0;
				current = null;
				backgroundClip = null;
				if (playerListener != null) {
					playerListener.playerUpdate(2, current);
				}
				return;
			} catch (Exception exception) {
				clipState = 0;
				current = null;
				backgroundClip = null;
				exception.printStackTrace();
			}
		}
	}

	public static synchronized void resume() {
		try {
			switch (clipState) {
				case 2: {
					clipState = 1;
					foregroundClipPlayer.start();
					break;
				}
				case 5: {
					clipState = 4;
					backgroundClipPlayer.start();
					break;
				}
				default: {
					return;
				}
			}
		} catch (Exception exception) {
			if (playerListener != null) {
				playerListener.playerUpdate(2, current);
			}
			clipState = 0;
			current = null;
			backgroundClip = null;
			exception.printStackTrace();
		}
	}

	public static synchronized void stop() {
		switch (clipState) {
			case 1: {
				if (foregroundClipPlayer == null) break;
				foregroundClipPlayer.close();
				foregroundClipPlayer = null;
				break;
			}
			case 2: {
				if (foregroundClipPlayer == null) break;
				foregroundClipPlayer.close();
				foregroundClipPlayer = null;
				break;
			}
			case 4: {
				if (backgroundClipPlayer == null) break;
				backgroundClipPlayer.close();
				backgroundClipPlayer = null;
				break;
			}
			case 5: {
				if (backgroundClipPlayer == null) break;
				backgroundClipPlayer.close();
				backgroundClipPlayer = null;
				break;
			}
			case 6: {
				if (tonePlayer == null) break;
				tonePlayer.close();
				System.out.println("entering here");
				tonePlayer = null;
				break;
			}
			default: {
				return;
			}
		}
		try {
			clipState = 0;
			current = null;
			currentPriority = 0;
			return;
		} catch (Exception exception) {
			if (playerListener != null) {
				playerListener.playerUpdate(2, current);
			}
			clipState = 0;
			current = null;
			currentPriority = 0;
			exception.printStackTrace();
			return;
		}
	}

	static {
	}
}
