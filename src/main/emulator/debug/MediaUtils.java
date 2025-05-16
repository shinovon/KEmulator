package emulator.debug;

import com.nokia.mid.sound.Sound;
import com.samsung.util.AudioClip;
import emulator.Emulator;
import emulator.media.vlc.VLCPlayerImpl;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.control.VolumeControlImpl;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class MediaUtils {

	public static String getPlayerType(final Object o) {
		if (o instanceof Sound) {
			return ((Sound) o).getType();
		}
		if (o instanceof AudioClip) {
			return "MMF";
		}
		return ((Player) o).getContentType();
	}

	public static String playerStateStr(final Object o) {
		if (o instanceof Sound) {
			switch (((Sound) o).getState()) {
				case 0: {
					return "SOUND_PLAYING";
				}
				case 1: {
					return "SOUND_STOPPED";
				}
				case 3: {
					return "SOUND_UNINITIALIZED";
				}
				default: {
					return "INVALID STATE";
				}
			}
		} else if (o instanceof AudioClip) {
			switch (((AudioClip) o).getStatus()) {
				case 1: {
					return "SOUND_PLAY";
				}
				case 2: {
					return "SOUND_PAUSE";
				}
				case 0: {
					return "SOUND_STOP";
				}
				default: {
					return "INVALID STATE";
				}
			}
		} else {
			switch (((Player) o).getState()) {
				case 0: {
					return "CLOSED";
				}
				case 300: {
					return "PREFETCHED";
				}
				case 200: {
					return "REALIZED";
				}
				case 400: {
					return "STARTED";
				}
				case 100: {
					return "UNREALIZED";
				}
				default: {
					return "INVALID STATE";
				}
			}
		}
	}

	public static int getPlayerLoopCount(final Object o) {
		if (o instanceof Sound && ((Sound) o).m_player instanceof PlayerImpl) {
			return ((PlayerImpl) ((Sound) o).m_player).loopCount;
		}
		if (o instanceof AudioClip) {
			return ((AudioClip) o).loopCount;
		}
		if (o instanceof PlayerImpl) {
			return ((PlayerImpl) o).loopCount;
		}
		return 0;
	}

	public static int getPlayerDurationMs(final Object o) {
		if (o == null)
			return -1;
		if (o instanceof Sound) {
			return getPlayerDurationMs(((Sound) o).m_player);
		}
		if (o instanceof AudioClip) {
			return getPlayerDurationMs(((AudioClip) o).m_player);
		}
		if (o instanceof Player) {
			long dur = ((Player) o).getDuration();
			if (dur < 0) return -1;
			return (int) (dur / 1000L);
		}
		return -1;
	}

	public static int getPlayerCurrentMs(final Object o) {
		if (o == null)
			return -1;
		if (o instanceof Sound)
			return getPlayerCurrentMs(((Sound) o).m_player);
		if (o instanceof AudioClip)
			return getPlayerCurrentMs(((AudioClip) o).m_player);
		if (o instanceof Player) {
			long l = ((Player) o).getMediaTime();
			if (l < 0)
				return -1;
			return (int) (l / 1000L);
		}
		return -1;
	}

	public static int getPlayerDataLength(final Object o) {
		if (o instanceof Sound) {
			return ((Sound) o).dataLen;
		}
		if (o instanceof AudioClip) {
			return ((AudioClip) o).dataLen;
		}
		if (o instanceof VLCPlayerImpl) {
			return ((VLCPlayerImpl) o).dataLen;
		}
		if (!(o instanceof PlayerImpl)) {
			return 0;
		}
		return ((PlayerImpl) o).dataLen;
	}

	public static int getPlayerVolume(final Object o) {
		try {
			if (o instanceof Sound) {
				return ((Sound) o).getGain();
			}
			if (o instanceof AudioClip) {
				return ((AudioClip) o).volume * 20;
			}

			if (o instanceof VLCPlayerImpl) {
				return ((VolumeControlImpl) ((VLCPlayerImpl) o).getControl("VolumeControl")).getLevel();
			}
			if (o instanceof PlayerImpl) {
				return ((VolumeControlImpl) ((PlayerImpl) o).getControl("VolumeControl")).getLevel();
			}
			return 0;
		} catch (Exception ex) {
			return 0;
		}
	}

	public static void setPlayerVolume(final Object o, final int n) {
		try {
			if (o instanceof Sound) {
				((Sound) o).setGain(n);
			} else if (!(o instanceof AudioClip)) {

				if (o instanceof VLCPlayerImpl) {
					((VolumeControlImpl) ((VLCPlayerImpl) o).getControl("VolumeControl")).setLevel(n);
					return;
				}
				if (!(o instanceof PlayerImpl)) {
					return;
				}
				((VolumeControlImpl) ((PlayerImpl) o).getControl("VolumeControl")).setLevel(n);
			}
		} catch (Exception ignored) {
		}
	}

	public static void modifyPlayer(final Object o, final PlayerActionType n) {
		if (o instanceof Sound) {
			final Sound sound = (Sound) o;
			try {
				switch (n) {
					case resume: {
						sound.resume();
						break;
					}
					case pause: {
						final long mediaTime = sound.m_player.getMediaTime();
						sound.stop();
						sound.m_player.setMediaTime(mediaTime);
						break;
					}
					case stop: {
						sound.stop();
						break;
					}
					case export: {
						try {
							byte[] b = sound.getData();
							String s = sound.getExportName();
							if (b != null) {
								exportAudio(b, sound.getExportName());
								Emulator.getEmulator().getScreen().showMessage("Saved: " + s);
							} else {
								Emulator.getEmulator().getScreen().showMessage("Export failed: unsupported stream type");
							}
						} catch (Exception e) {
							Emulator.getEmulator().getScreen().showMessage("Export failed: " + e);
						}
						break;
					}
				}
			} catch (Exception ignored) {
			}
			return;
		}
		if (o instanceof AudioClip) {
			final AudioClip audioClip = (AudioClip) o;
			switch (n) {
				case resume: {
					audioClip.play(audioClip.loopCount, audioClip.volume);
					break;
				}
				case pause: {
					audioClip.pause();
					break;
				}
				case stop: {
					audioClip.stop();
					break;
				}
				case export: {
					try {
						byte[] b = audioClip.getData();
						String s = audioClip.getExportName();
						if (b != null) {
							exportAudio(b, s);
							Emulator.getEmulator().getScreen().showMessage("Saved: " + s);
						} else {
							Emulator.getEmulator().getScreen().showMessage("Export failed: unsupported stream type");
						}
					} catch (Exception e) {
						Emulator.getEmulator().getScreen().showMessage("Export failed: " + e);
					}
					break;
				}
			}
			return;
		}
		if (o instanceof VLCPlayerImpl) {
			final VLCPlayerImpl v = (VLCPlayerImpl) o;
			try {
				switch (n) {
					case resume: {
						v.start();
						break;
					}
					case pause: {
						final long mediaTime2 = v.getMediaTime();
						v.stop();
						v.setMediaTime(mediaTime2);
					}
					case stop: {
						v.stop();
						break;
					}
					case export: {
						Emulator.getEmulator().getScreen().showMessage("Export not supported!");
						break;
					}
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			return;
		}
		if (!(o instanceof PlayerImpl)) {
			return;
		}
		final PlayerImpl playerImpl = (PlayerImpl) o;
		try {
			switch (n) {
				case resume: {
					playerImpl.start();
					break;
				}
				case pause: {
					final long mediaTime2 = playerImpl.getMediaTime();
					playerImpl.stop();
					playerImpl.setMediaTime(mediaTime2);
					break;
				}
				case stop: {
					playerImpl.stop();
					break;
				}
				case export: {
					try {
						byte[] b = playerImpl.getData();
						String s = "audio" + playerImpl.getExportName();
						if (b != null) {
							exportAudio(b, s);
							Emulator.getEmulator().getScreen().showMessage("Saved: " + s);
						} else {
							Emulator.getEmulator().getScreen().showMessage("Export failed: unsupported stream type");
						}
					} catch (Exception e) {
						Emulator.getEmulator().getScreen().showMessage("Export failed: " + e);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	public static void exportAudio(byte[] b, String name) throws IOException {
		File f = new File(Emulator.getUserPath() + "/" + name);
		if (f.exists()) return;
		f.createNewFile();
		DataOutputStream o = new DataOutputStream(new FileOutputStream(f));
		o.write(b);
		o.close();
	}
}
