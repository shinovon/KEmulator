package javax.microedition.media.control;

import javax.microedition.media.*;

public class MIDIControlImpl implements MIDIControl {
    PlayerImpl aPlayerImpl539;

    public MIDIControlImpl(final Object o) {
        super();
        this.aPlayerImpl539 = (PlayerImpl) o;
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

    public void setProgram(final int n, final int n2, final int n3) {
    }

    public void setChannelVolume(final int n, final int n2) {
        this.aPlayerImpl539.setMIDIChannelVolume(n, n2);
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
    }

    public int longMidiEvent(final byte[] array, final int n, final int n2) {
        return -1;
    }
}
