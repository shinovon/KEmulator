package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface ToneControl extends Control {
	public static final byte VERSION = -2;
	public static final byte TEMPO = -3;
	public static final byte RESOLUTION = -4;
	public static final byte BLOCK_START = -5;
	public static final byte BLOCK_END = -6;
	public static final byte PLAY_BLOCK = -7;
	public static final byte SET_VOLUME = -8;
	public static final byte REPEAT = -9;
	public static final byte C4 = 60;
	public static final byte SILENCE = -1;

	void setSequence(final byte[] p0);
}
