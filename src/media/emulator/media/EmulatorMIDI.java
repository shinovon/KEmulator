package emulator.media;

import emulator.Settings;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.sound.midi.*;

public class EmulatorMIDI {
	public static Player currentPlayer;
	private static MidiDevice.Info[] deviceInfo;
	private static Sequencer sequencer;
	private static Synthesizer synthesizer;
	private static MidiDevice device;
	private static Receiver receiver;

	public static boolean useExternalReceiver() {
		return Settings.searchVms && receiver != null && synthesizer == null;
	}

	public static void initDevices() {
		if (deviceInfo != null)
			return;
		deviceInfo = MidiSystem.getMidiDeviceInfo();
	}

	public static void initDevice() throws MidiUnavailableException {
		initDevice(false);
	}

	public static void initDevice(boolean noVms) throws MidiUnavailableException {
		if (receiver == null && Settings.searchVms && !noVms) {
			for (MidiDevice.Info info : deviceInfo) {
				if (info.getName().toLowerCase().contains("virtualmidisynth")) {
					device = MidiSystem.getMidiDevice(info);
					device.open();
					receiver = device.getReceiver();
					break;
				}
			}
		}
		if (receiver == null) {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			receiver = synthesizer.getReceiver();
		}
		if (sequencer == null) {
			sequencer = MidiSystem.getSequencer(false);
			setReceiver(sequencer, receiver);
			sequencer.open();
			sequencer.addMetaEventListener(new MetaEventListener() {
				public void meta(MetaMessage meta) {
					if (meta.getType() == 0x2F && currentPlayer instanceof PlayerImpl) {
						((PlayerImpl) currentPlayer).notifyCompleted();
					}
				}
			});
		}
	}

	public static void setSequence(Sequence sequence) throws InvalidMidiDataException, MidiUnavailableException {
		initDevice();
		sequencer.setSequence(sequence);
	}

	public static void start(PlayerImpl player, Sequence sequence, long position) throws InvalidMidiDataException, MidiUnavailableException {
		initDevice();
		sequencer.setSequence(sequence);
		sequencer.setMicrosecondPosition(position);
		sequencer.start();
	}

	public static void startTone(Sequence sequence, long position) throws InvalidMidiDataException, MidiUnavailableException {
		initDevice(true);
		sequencer.setSequence(sequence);
		sequencer.setMicrosecondPosition(position);
		sequencer.start();
	}

	public static void stop() {
		sequencer.stop();
	}

	public static void close(boolean force) {
		if (Settings.reopenMidiDevice || force) {
			if (sequencer != null) {
				sequencer.close();
				sequencer = null;
			}
			if (receiver != null) {
				receiver.close();
				receiver = null;
			}
			if (device != null) {
				device.close();
				device = null;
			}
			if (synthesizer != null) {
				synthesizer.close();
				synthesizer = null;
			}
		}
	}

	public static void setMicrosecondPosition(long ms) throws MidiUnavailableException {
		initDevice();
		sequencer.setMicrosecondPosition(ms);
	}

	public static long getMicrosecondPosition() throws MidiUnavailableException {
		initDevice();
		return sequencer.getMicrosecondPosition();
	}

	public static void setMIDIChannelVolume(final int n, final int n2) {
		try {
			Receiver r = getMidiReceiver();
			ShortMessage shortMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, n, 7, n2);
			r.send(shortMessage, -1L);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void shortMidiEvent(int type, int data1, int data2) {
		try {
			Receiver r = getMidiReceiver();
			ShortMessage shortMessage = new ShortMessage(type, data1, data2);
			r.send(shortMessage, -1L);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
//		return sequencer.getTransmitters().iterator().next().getReceiver();
		return receiver;
	}

	public static void setupSequencer(Sequencer sequencer) throws MidiUnavailableException {
		if (!Settings.searchVms) return;
		EmulatorMIDI.initDevice();
		setReceiver(sequencer, receiver);
	}

	private static void setReceiver(Sequencer sequencer, Receiver receiver) throws MidiUnavailableException {
		if (sequencer.isOpen()) {
			for (Transmitter t : sequencer.getTransmitters()) {
				t.setReceiver(receiver);
			}
			return;
		}
		sequencer.getTransmitter().setReceiver(receiver);
	}
}
