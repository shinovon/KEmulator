package emulator.media;

import emulator.Emulator;

public final class RingtoneParser {

    private MidiBuilder midiBuilder;
    private int dataLength;
    private byte[] dataBuffer;

    private static int bitIndex;
    private static int bitOffset;

    private static final int[] noteDurations = {
            25, 28, 31, 35, 40, 45, 50, 56, 63, 70, 80, 90, 100, 112, 125, 140,
            160, 180, 200, 225, 250, 285, 320, 355, 400, 450, 500, 565, 635, 715,
            800, 900
    };

    public RingtoneParser(final byte[] inputData) {
        this.midiBuilder = new MidiBuilder();
        this.dataLength = inputData.length;
        this.dataBuffer = new byte[dataLength + 10];
        System.arraycopy(inputData, 0, dataBuffer, 0, dataLength);
        parse(dataBuffer);
    }

    public byte[] getMIDIData() {
        return midiBuilder.getMIDIData();
    }

    // ================== Parsing helpers ==================

    private static int readBits(final byte[] array, final int n) {
        int value = (((array[bitIndex] & 0xFF) << 8) | (array[bitIndex + 1] & 0xFF)) >> (16 - (n + bitOffset)) & ((1 << n) - 1);
        bitOffset += n;
        if (bitOffset > 7) {
            bitOffset -= 8;
            bitIndex++;
        }
        return value;
    }

    private static int getTempoValue(final int index) {
        return noteDurations[index];
    }

    // ================== Core parsing ==================

    private void parse(final byte[] array) {
        bitIndex = 0;
        bitOffset = 0;

        int trackInitialized = 0;

        // Skip header bytes
        readBits(array, 8);
        readBits(array, 8);
        readBits(array, 7);

        int ringtoneType = readBits(array, 3);
        if (ringtoneType != 1 && ringtoneType != 2) {
            Emulator.getEmulator().getLogStream().println("Unsupported ringtone type");
            return;
        }

        // Skip initial bytes
        int initialCount = readBits(array, 4);
        for (int i = 0; i < initialCount; i++) {
            readBits(array, 8);
        }

        int loopCounter = readBits(array, 8);

        while (bitIndex < dataLength && loopCounter > 0) {

            readBits(array, 3); // unused
            readBits(array, 2); // unused
            readBits(array, 4); // unused

            int notesCount = readBits(array, 8);
            for (int i = 0; i < notesCount && bitIndex < dataLength; i++) {
                int command = readBits(array, 3);
                switch (command) {
                    case 0:
                        readBits(array, 2); // unused
                        break;
                    case 1:
                        if (trackInitialized == 0) {
                            midiBuilder.initTrack();
                            trackInitialized = 1;
                        }
                        midiBuilder.note = readBits(array, 4);
                        midiBuilder.durationType = readBits(array, 3);
                        midiBuilder.noteType = readBits(array, 2);
                        midiBuilder.addNote();
                        break;
                    case 2:
                        midiBuilder.octave = readBits(array, 2);
                        if (midiBuilder.octave > 0) {
                            midiBuilder.octave--;
                        }
                        break;
                    case 3:
                        readBits(array, 2); // unused
                        break;
                    case 4:
                        midiBuilder.tempo = getTempoValue(readBits(array, 5));
                        if (trackInitialized == 1) {
                            midiBuilder.setTempo();
                        }
                        break;
                    case 5:
                        midiBuilder.volume = readBits(array, 4);
                        break;
                }
            }

            loopCounter--;
        }

        midiBuilder.finishTrack();
    }
}
