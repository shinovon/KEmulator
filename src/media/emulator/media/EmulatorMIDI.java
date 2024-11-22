package emulator.media;

import emulator.Settings;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.sound.midi.*;

public class EmulatorMIDI {
	public static Player currentPlayer;
	private static MidiDevice.Info[] midiDeviceInfo;
	private static MidiDevice midiDevice;
	private static Sequencer midiSequencer;

	public static void initDevices() {
		if (midiDeviceInfo != null)
			return;
		midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
	}

	static MidiDevice.Info getMidiDeviceInfo() throws MidiUnavailableException {
		if (Settings.searchVms) {
			for (MidiDevice.Info info : midiDeviceInfo) {
				if (info.getName().toLowerCase().contains("virtualmidisynth")) {
					return info;
				}
			}
		}
		return MidiSystem.getSynthesizer().getDeviceInfo();
	}

	public static void initDevice() throws MidiUnavailableException {
		initDevice(false);
	}

	public static void initDevice(boolean noVms) throws MidiUnavailableException {
		if (midiDevice == null) {
			midiDevice = MidiSystem.getMidiDevice(getMidiDeviceInfo());
			midiDevice.open();
		}
		if (midiSequencer == null) {
			midiSequencer = MidiSystem.getSequencer();
			midiSequencer.addMetaEventListener(new MetaEventListener() {
				public void meta(MetaMessage meta) {
					if (meta.getType() == 0x2F && currentPlayer instanceof PlayerImpl) {
						((PlayerImpl) currentPlayer).notifyCompleted();
					}
				}
			});
			if (Settings.searchVms && !noVms) {
				for (Transmitter t : midiSequencer.getTransmitters()) {
					t.setReceiver(midiDevice.getReceiver());
				}
			}
			midiSequencer.open();
		}
	}

	public static void setSequence(Sequence sequence) throws InvalidMidiDataException, MidiUnavailableException {
		initDevice();
		midiSequencer.setSequence(sequence);
	}

	public static void start(PlayerImpl player, Sequence sequence, long position) throws InvalidMidiDataException, MidiUnavailableException {
		initDevice();
		midiSequencer.setSequence(sequence);
		midiSequencer.setMicrosecondPosition(position);
		midiSequencer.start();
	}

	public static void startTone(Sequence sequence, long position) throws InvalidMidiDataException, MidiUnavailableException {
		initDevice(true);
		midiSequencer.setSequence(sequence);
		midiSequencer.setMicrosecondPosition(position);
		midiSequencer.start();
	}

	public static void stop() {
		midiSequencer.stop();
	}

	public static void close() {
		if (Settings.reopenMidiDevice && midiSequencer != null) {
			midiSequencer.close();
			midiDevice.close();
			midiSequencer = null;
			midiDevice = null;
		}
	}

	public static void setMicrosecondPosition(long ms) throws MidiUnavailableException {
		initDevice();
		midiSequencer.setMicrosecondPosition(ms);
	}

	public static long getMicrosecondPosition() throws MidiUnavailableException {
		initDevice();
		return midiSequencer.getMicrosecondPosition();
	}

	public static void setMIDIChannelVolume(final int n, final int n2) {
	}

	public static void shortMidiEvent(int type, int data1, int data2) {
	}

	public static int longMidiEvent(byte[] data, int offset, int length) {
		byte[] b = new byte[length];
		System.arraycopy(data, offset, b, 0, length);
		try {
			Receiver r = getMidiReceiver();
			MidiMessage msg = new MidiMessage(b) {
				public Object clone() {
					return null;
				}
			};
			r.send(msg, -1L);
			return length;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	private static Receiver getMidiReceiver() throws MidiUnavailableException {
		EmulatorMIDI.initDevice();
		return midiSequencer.getTransmitters().iterator().next().getReceiver();
	}

	public static void setupSequencer(Sequencer sequencer) throws MidiUnavailableException {
		if (!Settings.searchVms) return;
		EmulatorMIDI.initDevice();
		for (Transmitter t : sequencer.getTransmitters()) {
			t.setReceiver(midiDevice.getReceiver());
		}
	}
}
