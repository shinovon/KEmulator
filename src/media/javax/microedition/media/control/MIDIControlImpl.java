package javax.microedition.media.control;

import emulator.media.EmulatorMIDI;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class MIDIControlImpl implements MIDIControl {
	static final int PROGRAM_CHANGE = 0xC0;
	static final int CONTROL_BANK_CHANGE_MSB = 0x00;
	static final int CONTROL_BANK_CHANGE_LSB = 0x20;

	Player player;

	public MIDIControlImpl(Player o) {
		super();
		this.player = o;
	}

	public boolean isBankQuerySupported() {
		return false;
	}

	public int[] getProgram(final int n) throws MediaException {
		return null;
	}

	public int getChannelVolume(final int n) {
		return -1;
	}

	public void setProgram(final int channel, final int bank, final int program) {
		shortMidiEvent(CONTROL_CHANGE | channel, CONTROL_BANK_CHANGE_MSB, bank >> 7);
		shortMidiEvent(CONTROL_CHANGE | channel, CONTROL_BANK_CHANGE_LSB, bank & 0x7F);
		shortMidiEvent(PROGRAM_CHANGE | channel, program, 0);
	}

	public void setChannelVolume(final int n, final int n2) {
		EmulatorMIDI.setMIDIChannelVolume(n, n2);
	}

	public int[] getBankList(final boolean b) throws MediaException {
		return null;
	}

	public int[] getProgramList(final int n) throws MediaException {
		return null;
	}

	public String getProgramName(final int n, final int n2) throws MediaException {
		return null;
	}

	public String getKeyName(final int n, final int n2, final int n3) throws MediaException {
		return null;
	}

	public void shortMidiEvent(final int n, final int n2, final int n3) {
		EmulatorMIDI.shortMidiEvent(n, n2, n3);
	}

	public int longMidiEvent(final byte[] array, final int n, final int n2) {
		return EmulatorMIDI.longMidiEvent(array, n, n2);
	}
}
