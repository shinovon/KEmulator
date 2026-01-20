package com.samsung.util;

import emulator.custom.ResourceManager;
import emulator.media.mmf.MMFPlayer;
import emulator.media.mmf.MaDll;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.PlayerListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioClip {
	public static final int TYPE_MMF = 1;
	public static final int TYPE_MP3 = 2;
	public static final int TYPE_MIDI = 3;
	public PlayerImpl m_player;
	public int dataLen;
	public int loopCount;
	public int volume;
	public int type;
	private byte[] data;
	private boolean mmfInit;
	public static final int STATUS_STOP = 0;
	public static final int STATUS_PLAY = 1;
	public static final int STATUS_PAUSE = 2;
	private int status;
	private int mmfSound = -1;

	public AudioClip(int n, byte[] b, int n2, int n3) {
		byte[] t = new byte[n3];
		java.lang.System.arraycopy(b, n2, t, 0, n3);
		this.init(t, n);
	}

	public AudioClip(int type, String s) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (InputStream is = ResourceManager.getResourceAsStream(s)) {
			byte[] b = new byte[512];
			while (is.available() > 0) {
				int n2 = is.read(b);
				baos.write(b, 0, n2);
			}
		}
		byte[] t = new byte[baos.size()];
		java.lang.System.arraycopy(baos.toByteArray(), 0, t, 0, t.length);
		this.init(t, type);
	}

	private void init(byte[] b, int type) {
		if (b == null) {
			throw new NullPointerException();
		}
		if (b.length > 4 && b[0] == 77 && b[1] == 77 && b[2] == 77 && b[3] == 68) { // mmf header check
			type = TYPE_MMF;
		}
		this.type = type;
		if (type == TYPE_MMF) {
			this.mmfInit = MMFPlayer.initialize();
			this.data = b;
			this.dataLen = b.length;
			this.status = STATUS_STOP;
			return;
		}
		try {
			String ct = "";
			if (type == TYPE_MIDI) {
				ct = "audio/midi";
			} else if (type == TYPE_MP3) {
				ct = "audio/mpeg";
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			this.m_player = new PlayerImpl(bais, ct);
			this.m_player.addPlayerListener(new MMAPIListener());
			bais.close();
		} catch (Exception ignored) {
		}
		this.status = STATUS_STOP;
	}

	public int getStatus() {
		if (mmfSound != -1) {
			int s = MMFPlayer.getMaDll().getStatus(mmfSound);
			if (s == MaDll.STATE_PAUSED) {
				return STATUS_PAUSE;
			}
			if (s == MaDll.STATE_PLAYING) {
				return STATUS_PLAY;
			}
			return STATUS_STOP;
		}
		return this.status;
	}

	public static boolean isSupported() {
		return true;
	}

	public void pause() {
		if (this.type == TYPE_MMF) {
			if (this.mmfInit) {
				if (mmfSound != -1) {
					if (MMFPlayer.getMaDll().getStatus(mmfSound) == MaDll.STATE_PLAYING) {
						MMFPlayer.getMaDll().pause(mmfSound);
					}
					return;
				}
				this.status = STATUS_PAUSE;
			}
		} else if (this.m_player != null) {
			try {
				long l = this.m_player.getMediaTime();
				this.m_player.stop();
				this.m_player.setMediaTime(l);
			} catch (Exception ignored) {
			}
			this.status = STATUS_PAUSE;
		}
	}

	public void play(int l, int v) {
		if (l == -1) {
			l = 255;
		}
		if (l < 0 || l > 255) {
			throw new IllegalArgumentException("loop must be in range 0 to 255: " + l);
		}
		if (v < 0 || v > 5) {
			throw new IllegalArgumentException("volume must be in range from 0 to 5: " + v);
		}
		if (this.type == 1) {
			this.loopCount = l;
			this.volume = v;
			if (this.mmfInit) {
				try {
					if (mmfSound == -1) {
						mmfSound = MMFPlayer.getMaDll().load(data);
					}
					if (mmfSound != -1 && MMFPlayer.getMaDll().getStatus(mmfSound) == MaDll.STATE_READY) {
						MMFPlayer.getMaDll().setVolume(mmfSound, (v * 100) / 5);
						MMFPlayer.getMaDll().start(mmfSound, l);
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
				status = STATUS_PLAY;
				return;
			}
			this.status = STATUS_PLAY;
		} else if (this.m_player != null) {
			this.m_player.setLoopCount(l);
			this.m_player.setLevel(v * 20);
			try {
				this.m_player.start();
			} catch (Exception ignored) {
			}
			this.status = STATUS_PLAY;
		}
	}

	public void resume() {
		if (this.type == TYPE_MMF) {
			if (this.mmfInit) {
				MMFPlayer.getMaDll().resume(mmfSound);
				return;
			}
			this.status = STATUS_PLAY;
		} else if (this.m_player != null) {
			try {
				this.m_player.start();
			} catch (Exception ignored) {
			}
			this.status = STATUS_PLAY;
		}
	}

	public void stop() {
		if (this.type == TYPE_MMF) {
			if (this.mmfInit) {
				if (MMFPlayer.getMaDll().getStatus(mmfSound) < MaDll.STATE_PLAYING) {
					MMFPlayer.getMaDll().stop(mmfSound);
				}
				this.status = STATUS_STOP;
				return;
			}
		} else if (this.m_player != null) {
			try {
				this.m_player.stop();
			} catch (Exception ignored) {
			}
			this.status = STATUS_STOP;
		}
	}

	class MMAPIListener implements PlayerListener {
		public void playerUpdate(Player p, String s, Object o) {
			if (STOPPED.equals(s) || END_OF_MEDIA.equals(s)) {
				AudioClip.this.status = STATUS_STOP;
			}
		}
	}

	protected void finalize() {
		if (mmfSound == -1) return;
		try {
			MMFPlayer.getMaDll().close(mmfSound);
		} catch (Throwable ignored) {}
	}

	public byte[] getData() {
		if (data == null) return null;
		return data.clone();
	}

	public String getExportName() {
		String ext = "";
		switch (type) {
			case TYPE_MMF:
				ext = "mmf";
				break;
			case TYPE_MP3:
				ext = "mp3";
				break;
			case TYPE_MIDI:
				ext = "mid";
				break;
		}
		return "audioclip" + hashCode() + "." + ext;
	}
}
