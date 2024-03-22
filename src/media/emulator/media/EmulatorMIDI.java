package emulator.media;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerImpl;
import javax.sound.midi.*;

public class EmulatorMIDI {
    public static Player currentPlayer;
    private static MidiDevice.Info[] midiDeviceInfo;
    private static MidiDevice midiDevice;
    private static Sequencer midiSequencer;

    public static void initDevices() {
        if(midiDeviceInfo != null)
            return;
        midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
    }

    static MidiDevice.Info getMidiDeviceInfo() throws MidiUnavailableException {
        for (int i = 0; i < midiDeviceInfo.length; ++i) {
            if (midiDeviceInfo[i].getName().toLowerCase().contains("virtualmidisynth")) {
                return midiDeviceInfo[i];
            }
        }
        return MidiSystem.getSynthesizer().getDeviceInfo();
    }

    public static void initDevice() throws MidiUnavailableException {
        if (midiDevice == null) {
            midiDevice = MidiSystem.getMidiDevice(getMidiDeviceInfo());
            midiDevice.open();
        }
        if (midiSequencer == null) {
            midiSequencer = MidiSystem.getSequencer();
            for (Transmitter t : midiSequencer.getTransmitters()) {
                t.setReceiver(midiDevice.getReceiver());
            }
            midiSequencer.addMetaEventListener(new MetaEventListener() {
                @Override
                public void meta(MetaMessage meta) {
                    if (meta.getType() == 0x2F && currentPlayer instanceof PlayerImpl) {
                        ((PlayerImpl) currentPlayer).notifyCompleted();
                    }
                }
            });
            midiSequencer.open();
        }
    }

    public static void setSequence(Sequence sequence) throws InvalidMidiDataException {
        midiSequencer.setSequence(sequence);
    }

    public static void start(PlayerImpl player, Sequence sequence, long position) throws InvalidMidiDataException {
        midiSequencer.setSequence(sequence);
        midiSequencer.setMicrosecondPosition(position);
        midiSequencer.start();
    }

    public static void stop() {
        midiSequencer.stop();
    }

    public static void setMicrosecondPosition(long ms) {
        midiSequencer.setMicrosecondPosition(ms);
    }

    public static long getMicrosecondPosition() {
        return midiSequencer.getMicrosecondPosition();
    }

    public static void setMIDIChannelVolume(final int n, final int n2) {
        try {
            Receiver r = getMidiReceiver();
            ShortMessage shortMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, n, 7, (int) (n2 * 127.0));
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

    private static Receiver getMidiReceiver() {
        return midiSequencer.getTransmitters().iterator().next().getReceiver();
    }
}
