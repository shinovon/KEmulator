package javax.microedition.media.control;

import javax.microedition.media.*;

public interface MIDIControl extends Control {
    public static final int CONTROL_CHANGE = 176;
    public static final int NOTE_ON = 144;

    int[] getBankList(final boolean p0) throws MediaException;

    int getChannelVolume(final int p0);

    String getKeyName(final int p0, final int p1, final int p2) throws MediaException;

    int[] getProgram(final int p0) throws MediaException;

    int[] getProgramList(final int p0) throws MediaException;

    String getProgramName(final int p0, final int p1) throws MediaException;

    boolean isBankQuerySupported();

    int longMidiEvent(final byte[] p0, final int p1, final int p2);

    void setChannelVolume(final int p0, final int p1);

    void setProgram(final int p0, final int p1, final int p2);

    void shortMidiEvent(final int p0, final int p1, final int p2);
}
