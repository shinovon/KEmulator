package emulator.media;

public final class MidiBuilder {

    public int note;
    public int durationType;
    public int noteType;
    public int octave;
    public int tempo;
    public int volume;
    public int trackLengthOffset;
    public int trackCountOffset;

    private static int currentOffset;
    private byte[] midiData;

    public MidiBuilder() {
        this.midiData = new byte[100];
        currentOffset = 0;
    }

    public byte[] getMIDIData() {
        return midiData;
    }

    // ================== Public API ==================

    public void initTrack() {
        writeInt(0x4D546864, 4); // "MThd"
        writeInt(6, 4);           // Header length
        writeInt(1, 2);           // Format type 1
        trackCountOffset = currentOffset;
        writeInt(1, 2);           // Placeholder for track count
        writeInt(0, 2);           // Time division placeholder

        writeInt(0x4D54726B, 4);  // "MTrk"
        trackLengthOffset = currentOffset;
        writeInt(0, 4);           // Placeholder for track length

        writeVariableLengthQuantity(0);
        writeInt(0xC0, 1);        // Program change
        writeInt(80, 1);          // Instrument
        setTempo();
    }

    public void addNote() {
        int baseDuration = (int) (240.0f * (4.0f / (1 << durationType)));
        int totalDuration = calculateNoteDuration(baseDuration);

        int midiNote = note == 0 ? 0 : 60 + (note - 1) + octave * 12;
        int midiVolume = note == 0 ? 0 : Math.min(255, (int) (255.0f * (volume / 14.0f)));

        writeVariableLengthQuantity(0);
        writeInt(0x90, 1); // Note on
        writeInt(midiNote, 1);
        writeInt(255, 1);

        writeVariableLengthQuantity(totalDuration);
        writeInt(0x80, 1); // Note off
        writeInt(midiNote, 1);
        writeInt(midiVolume, 1);
    }

    public void setTempo() {
        writeVariableLengthQuantity(0);
        writeInt(0xFF, 1); // Meta event
        writeInt(0x51, 1); // Set tempo
        writeInt(3, 1);

        int microsecondsPerQuarter = tempo == 0 ? 60000000 / 250 : 60000000 / tempo;
        writeInt((microsecondsPerQuarter >> 16) & 0xFF, 1);
        writeInt((microsecondsPerQuarter >> 8) & 0xFF, 1);
        writeInt(microsecondsPerQuarter & 0xFF, 1);
    }

    public void finishTrack() {
        writeVariableLengthQuantity(0);
        writeInt(0xFF, 1);
        writeInt(0x2F, 1); // End of track
        writeInt(0, 1);

        int finalOffset = currentOffset;
        currentOffset = trackLengthOffset;
        writeInt(finalOffset - trackLengthOffset - 4, 4);
        resizeBuffer(finalOffset);
        copyTrackData(trackLengthOffset - 4, finalOffset, 10);
    }

    // ================== Private Helpers ==================

    private int calculateNoteDuration(int baseDuration) {
        if (noteType == 1) {          // Normal
            return baseDuration + baseDuration / 2;
        } else if (noteType == 2) {   // Dotted
            return baseDuration + baseDuration / 2;
        }
        return baseDuration;
    }

    private void writeInt(int value, int bytes) {
        ensureCapacity(bytes);
        for (int i = bytes - 1; i >= 0; i--) {
            midiData[currentOffset++] = (byte) ((value >> (i * 8)) & 0xFF);
        }
    }

    private void writeVariableLengthQuantity(int value) {
        int buffer = value & 0x7F;
        while ((value >>= 7) > 0) {
            buffer <<= 8;
            buffer |= ((value & 0x7F) | 0x80);
        }
        while (true) {
            writeInt(buffer & 0xFF, 1);
            if ((buffer & 0x80) != 0) {
                buffer >>= 8;
            } else {
                break;
            }
        }
    }

    private void ensureCapacity(int additionalBytes) {
        if (currentOffset + additionalBytes >= midiData.length) {
            int newSize = midiData.length + Math.max(additionalBytes, 100);
            byte[] newBuffer = new byte[newSize];
            System.arraycopy(midiData, 0, newBuffer, 0, midiData.length);
            midiData = newBuffer;
        }
    }

    private void resizeBuffer(int newSize) {
        if (newSize != midiData.length) {
            byte[] newBuffer = new byte[newSize];
            System.arraycopy(midiData, 0, newBuffer, 0, Math.min(newSize, midiData.length));
            midiData = newBuffer;
        }
        currentOffset = newSize;
    }

    private void copyTrackData(int start, int end, int repetitions) {
        int length = end - start;
        int bufferEnd = midiData.length;
        for (int i = 0; i < repetitions; i++) {
            resizeBuffer(bufferEnd + length);
            System.arraycopy(midiData, start, midiData, bufferEnd, length);
            bufferEnd = midiData.length;
        }
        int savedOffset = currentOffset;
        currentOffset = trackCountOffset;
        writeInt(repetitions + 1, 2);
        currentOffset = savedOffset;
    }
}
