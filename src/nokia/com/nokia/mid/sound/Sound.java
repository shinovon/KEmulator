package com.nokia.mid.sound;

import emulator.Settings;
import emulator.media.RingtoneParser;
import emulator.media.tone.MIDITonePlayer;
import emulator.media.tone.MidiToneConstants;
import emulator.media.tone.ToneManager;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.MIDIControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.control.VolumeControlImpl;
import java.io.ByteArrayInputStream;

public class Sound {
	public static final int SOUND_PLAYING = 0;
	public static final int SOUND_STOPPED = 1;
	public static final int SOUND_UNINITIALIZED = 3;
	public static final int FORMAT_TONE = 1;
	public static final int FORMAT_WAV = 5;
	public Player m_player;
	private Sound inst;
	private SoundListener soundListener;
	private int state;
	private int type;
	public int dataLen;

	private static final short[] FREQ_TABLE = {
			0, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 22, 23, 24, 26, 27, 29,
			30, 32, 34, 36, 38, 41, 43, 45, 48, 51, 54, 57, 60, 64, 68, 72, 76, 81, 85, 90, 96,
			101, 107, 114, 120, 128, 135, 143, 152, 161, 170, 180, 191, 202, 214, 227, 240, 255,
			270, 286, 303, 321, 340, 360, 381, 404, 428, 453, 480, 509, 539, 571, 605, 641, 679,
			719, 762, 807, 855, 906, 960, 1017, 1078, 1142, 1210, 1282, 1358, 1438, 1524, 1614,
			1710, 1812, 1920, 2034, 2155, 2283, 2419, 2563, 2715, 2876, 3047, 3228, 3420, 3624,
			3839, 4067, 4309, 4565, 4837, 5125, 5429, 5752, 6094, 6456, 6840, 7247, 7678, 8134,
			8618, 9130, 9673, 10249, 10858, 11504, 12188, 12912
	};

	private final PlayerListener playerListener = new PlayerListener() {
		public void playerUpdate(Player p0, String p1, Object p2) {
			if (Sound.this.soundListener == null) return;
			if ("endOfMedia".equals(p1)) {
				Sound.this.soundListener.soundStateChanged(Sound.this, 1);
			}
		}
	};
	private byte[] data;
	private int gain = 255;

	public Sound(byte[] paramArrayOfByte, int paramInt) {
		type = paramInt;
		this.dataLen = paramArrayOfByte.length;
		this.inst = this;
		init(paramArrayOfByte, paramInt);
	}

	public String getType() {
		if (type == 1) {
			return "FORMAT_TONE";
		}
		if (type == 5) {
			return "FORMAT_WAV";
		}
		return null;
	}

	public Sound(int paramInt, long paramLong) {
		init(paramInt, paramLong);
	}

	public static int getConcurrentSoundCount(int paramInt) {
		return 1;
	}

	public int getGain() {
		return ((VolumeControlImpl) this.m_player.getControl("VolumeControl")).getLevel();
	}

	public int getState() {
		return state;
	}

	public static int[] getSupportedFormats() {
		return new int[]{1, 5};
	}

	public void init(byte[] paramArrayOfByte, int paramInt) {
		if (paramArrayOfByte == null) {
			throw new NullPointerException();
		}
		if (paramInt == 1) {
			// TODO fix memory leaks
			if (Settings.enableOTT) {
				paramArrayOfByte = new RingtoneParser(paramArrayOfByte).getMIDIData();
			} else {
				m_player = new MIDITonePlayer();
				state = 3;
				return;
			}
		}
		if (Settings.enableMediaDump) data = paramArrayOfByte;
		try {
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
			m_player = new PlayerImpl(localByteArrayInputStream, type == FORMAT_WAV ? "audio/wav" : null);
			m_player.addPlayerListener(playerListener);
			localByteArrayInputStream.close();
		} catch (Exception ignored) {}
		state = 3;
	}

	public void init(int freq, long duration) {
		System.out.println("init " + freq + " " + duration );
		if (duration <= 0) {
			throw new IllegalArgumentException("duration = " + duration);
		}
		if (freq < 0 || freq > FREQ_TABLE[FREQ_TABLE.length - 1]) {
			throw new IllegalArgumentException("freq = " + freq);
		}

		try {
			if (m_player != null) {
				m_player.close();
			}
			int note = convertFreqToNote(freq);
			m_player = ToneManager.createPlayer(note, (int) duration, MidiToneConstants.TONE_MAX_VOLUME);
			if (m_player instanceof VolumeControl) {
				((VolumeControl)m_player).setLevel(gain * 100 / 255);
			}
			state = SOUND_STOPPED;
		} catch (MediaException e) {
			e.printStackTrace();
			state = SOUND_UNINITIALIZED;
		}
	}

	public void play(int paramInt) {
		this.m_player.setLoopCount(paramInt == 0 ? -1 : paramInt);
		resume();
	}

	public void release() {
		data = null;
		if (state == 0) {
			stop();
		}
		if (state != 3) {
			if (m_player != null) this.m_player.deallocate();
			state = 3;
			if (this.soundListener != null) {
				this.soundListener.soundStateChanged(this, 3);
			}
		}
	}

	public void resume() {
		try {
			this.m_player.start();
			if (type == FORMAT_TONE) {
				((MIDIControl) m_player.getControl("MIDIControl")).setChannelVolume(0, 64);
			}
		} catch (Exception ignored) {}
		state = 0;
		if (this.soundListener != null) {
			this.soundListener.soundStateChanged(this, 0);
		}
	}

	public void setGain(int paramInt) {
		gain = paramInt;
		((VolumeControl) m_player.getControl("VolumeControl")).setLevel(gain * 100 / 255);
	}

	public void setSoundListener(SoundListener paramSoundListener) {
		this.soundListener = paramSoundListener;
	}

	public void stop() {
		try {
			this.m_player.stop();
		} catch (Exception ignored) {}
		if (state != 1) {
			state = 1;
			if (this.soundListener != null) {
				this.soundListener.soundStateChanged(this, 1);
			}
		}
	}

	public byte[] getData() {
		if (m_player == null || !(m_player instanceof PlayerImpl)) return null;
		if (data != null) return data.clone();
		return ((PlayerImpl) m_player).getData();
	}

	public String getExportName() {
		switch (type) {
			case FORMAT_TONE:
				return "nokiatoneconverted" + hashCode() + ".mid";
			case FORMAT_WAV:
				return "nokiaaudio" + hashCode() + ".wav";
		}
		if(!(m_player instanceof PlayerImpl)) return "unknown";
		return "nokiaaudio" + ((PlayerImpl) m_player).getExportName();
	}

	protected void dispose() {
		try {
			if (m_player != null) {
				m_player.deallocate();
				m_player.close();
			}
		} catch (Exception ignored) {}
	}

	public static int convertFreqToNote(int freq) {
		int low = 0;
		int high = FREQ_TABLE.length - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = FREQ_TABLE[mid];

			if (midVal < freq) {
				low = mid + 1;
			} else if (midVal > freq) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		if ((freq - FREQ_TABLE[low - 1]) < (FREQ_TABLE[low] - freq)) {
			return low - 1;
		} else {
			return low;
		}
	}
}
