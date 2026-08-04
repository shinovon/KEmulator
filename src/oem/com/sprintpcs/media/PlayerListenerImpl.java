package com.sprintpcs.media;

import javax.microedition.media.MediaException;

final class PlayerListenerImpl
		implements javax.microedition.media.PlayerListener {
	void addPlayerListener(javax.microedition.media.Player player) {
		try {
			player.addPlayerListener(this);
			return;
		} catch (Exception exception) {
			System.out.println("Exception in addPlayerListener");
			return;
		}
	}

	public final void playerUpdate(javax.microedition.media.Player player, String event, Object object) {
		block33:
		{
			int n;
			PlayerListener playerListener;
			block35:
			{
				block34:
				{
					block32:
					{
						if (Player.current == null && !"closed".equals(event)) {
							System.out.println("ERROR: currentPlayed is null!");
						}
						if (!event.equals("started")) break block32;
						long l = Long.parseLong(object.toString());
						if (l > 0L) {
							if (Player.playerListener != null) {
								Player.playerListener.playerUpdate(6, Player.current);
							}
						} else if (l == 0L) {
							if (Player.playerListener != null) {
								Player.playerListener.playerUpdate(3, Player.current);
							}
						} else {
							System.out.println("com.sprintpcs.media.Player: media time is not supported");
						}
						break block33;
					}
					if (!"stopped".equals(event)) break block33;
					if (Player.clipState != 2 && Player.clipState != 5) break block34;
					if (Player.playerListener == null) break block33;
					playerListener = Player.playerListener;
					n = 5;
					break block35;
				}
				if (Player.playerListener == null) break block33;
				playerListener = Player.playerListener;
				n = 4;
			}
			playerListener.playerUpdate(n, Player.current);
		}
		if (!"endOfMedia".equals(event)) {
			if ("deviceUnavailable".equals(event)) {
				if (Player.playerListener != null) {
					Player.playerListener.playerUpdate(0, Player.current);
					return;
				}
			} else if ("error".equals(event) && Player.playerListener != null) {
				Player.playerListener.playerUpdate(2, Player.current);
				return;
			}
		} else {
			if (Player.playerListener != null) {
				Player.playerListener.playerUpdate(1, Player.current);
			}
			if (player == Player.foregroundClipPlayer ? Player.toneLoopCount > 0 || Player.toneLoopCount == -1 : (player == Player.tonePlayer ? Player.toneLoopCount > 0 || Player.toneLoopCount == -1 : player == Player.backgroundClipPlayer)) {
				return;
			}
			player.close();
			switch (Player.clipState) {
			case 1: {
//				Player.foregroundClipPlayer.close();
//				Player.foregroundClipPlayer = null;
				Player.foregroundClip = null;
				Player.current = null;
				Player.clipState = 0;
				Player.currentPriority = 0;
				if (Player.backgroundClipPlayer == null) break;
				Player.currentPriority = 0;
				Player.clipState = 4;
				Player.current = Player.backgroundClip;
				try {
					Player.backgroundClipPlayer.start();
					return;
				} catch (Exception mediaException) {
					mediaException.printStackTrace();
					return;
				}
			}
			case 4: {
				return;
			}
			case 6: {
				Exception exception;
				try {
//					Player.tonePlayer.close();
//					Player.tonePlayer = null;
					Player.tone = null;
					Player.current = null;
					Player.clipState = 0;
					Player.currentPriority = 0;
					if (Player.backgroundClipPlayer != null) {
						Player.currentPriority = 0;
						Player.clipState = 4;
						Player.current = Player.backgroundClip;
						Player.backgroundClipPlayer.start();
						return;
					}
					return;
				} catch (MediaException mediaException) {
					if (Player.playerListener != null) {
						Player.playerListener.playerUpdate(2, Player.current);
						System.out.println("play background clip encountered a MediaException: " + mediaException.getMessage());
					}
					exception = mediaException;
				} catch (Exception exception2) {
					Exception exception3;
					exception = exception3 = exception2;
				}
				exception.printStackTrace();
			}
			}
		}
	}

	PlayerListenerImpl(PlayerListener playerListener) {
		Player.playerListener = playerListener;
	}
}
