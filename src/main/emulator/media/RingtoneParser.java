package emulator.media;

import emulator.Emulator;

public final class RingtoneParser {
	MidiBuilder midiBuilder;
	int dataLength;
	byte[] dataBuffer;
	static int bitIndex;
	static int bitOffset;
	static final int[] noteDurations;

	public RingtoneParser(final byte[] array) {
		super();
		this.midiBuilder = new MidiBuilder();
		this.dataLength = array.length;
		System.arraycopy(array, 0, this.dataBuffer = new byte[this.dataLength + 10], 0, this.dataLength);
		this.parse(this.dataBuffer);
	}

	public final byte[] getMIDIData() {
		return this.midiBuilder.getMIDIData();
	}

	private static int readbits(final byte[] array, final int n) {
		final int n2 = ((array[RingtoneParser.bitIndex] & 0xFF) << 8) + (array[RingtoneParser.bitIndex + 1] & 0xFF) >> 16 - (n + RingtoneParser.bitOffset) & (1 << n) - 1;
		RingtoneParser.bitOffset += n;
		if (RingtoneParser.bitOffset > 7) {
			RingtoneParser.bitOffset -= 8;
			++RingtoneParser.bitIndex;
		}
		return n2;
	}

	private static int getTempoValue(final int n) {
		return RingtoneParser.noteDurations[n];
	}

	private void parse(final byte[] array) {
		RingtoneParser.bitIndex = 0;
		RingtoneParser.bitOffset = 0;
		int n = 0;
		readbits(array, 8);
		readbits(array, 8);
		readbits(array, 7);
		final int method727;
		if ((method727 = readbits(array, 3)) != 1 && method727 != 2) {
			Emulator.getEmulator().getLogStream().println("Unsupported ringtone type");
			return;
		}
		for (int method728 = readbits(array, 4), i = 0; i < method728; ++i) {
			readbits(array, 8);
		}
		int method729 = readbits(array, 8);
		while (RingtoneParser.bitIndex < this.dataLength) {
			if (method729 == 0) {
				break;
			}
			readbits(array, 3);
			readbits(array, 2);
			readbits(array, 4);
			for (int method730 = readbits(array, 8), n2 = 0; n2 < method730 && RingtoneParser.bitIndex < this.dataLength; ++n2) {
				switch (readbits(array, 3)) {
					case 0: {
						readbits(array, 2);
						break;
					}
					case 1: {
						if (n == 0) {
							this.midiBuilder.initTrack();
							n = 1;
						}
						this.midiBuilder.note = readbits(array, 4);
						this.midiBuilder.durationType = readbits(array, 3);
						this.midiBuilder.noteType = readbits(array, 2);
						this.midiBuilder.addNote();
						break;
					}
					case 2: {
						this.midiBuilder.octave = readbits(array, 2);
						if (this.midiBuilder.octave > 0) {
							final MidiBuilder ana1225 = this.midiBuilder;
							--ana1225.octave;
							break;
						}
						break;
					}
					case 3: {
						readbits(array, 2);
						break;
					}
					case 4: {
						this.midiBuilder.tempo = getTempoValue(readbits(array, 5));
						if (n == 1) {
							this.midiBuilder.setTempo();
							break;
						}
						break;
					}
					case 5: {
						this.midiBuilder.volume = readbits(array, 4);
						break;
					}
				}
			}
			if (RingtoneParser.bitIndex >= this.dataLength) {
				break;
			}
			--method729;
		}
		this.midiBuilder.finishTrack();
	}

	static {
		noteDurations = new int[]{25, 28, 31, 35, 40, 45, 50, 56, 63, 70, 80, 90, 100, 112, 125, 140, 160, 180, 200, 225, 250, 285, 320, 355, 400, 450, 500, 565, 635, 715, 800, 900};
	}
}
