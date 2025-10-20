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
	static int currentOffset;
	byte[] midiData;

	public MidiBuilder() {
		super();
		this.midiData = new byte[100];
		MidiBuilder.currentOffset = 0;
	}

	public final byte[] getMIDIData() {
		return this.midiData;
	}

	private void writeInt(final int n, final int n2) {
		if (MidiBuilder.currentOffset + n2 >= this.midiData.length) {
			final int n3 = (n2 > 100) ? n2 : 100;
			final byte[] array = new byte[this.midiData.length];
			System.arraycopy(this.midiData, 0, array, 0, this.midiData.length);
			System.arraycopy(array, 0, this.midiData = new byte[array.length + n3], 0, array.length);
		}
		for (int i = n2 - 1; i >= 0; --i) {
			this.midiData[MidiBuilder.currentOffset++] = (byte) (n >> (i << 3) & 0xFF);
		}
	}

	private void resizeBuffer(final int n) {
		if (n == this.midiData.length) {
			return;
		}
		byte[] array2;
		int n2;
		byte[] array3;
		int n3;
		int length;
		if (n < this.midiData.length) {
			final byte[] array = new byte[this.midiData.length];
			System.arraycopy(this.midiData, 0, array, 0, this.midiData.length);
			this.midiData = new byte[n];
			array2 = array;
			n2 = 0;
			array3 = this.midiData;
			n3 = 0;
			length = n;
		} else {
			final byte[] array4 = new byte[this.midiData.length];
			System.arraycopy(this.midiData, 0, array4, 0, this.midiData.length);
			this.midiData = new byte[n];
			array2 = array4;
			n2 = 0;
			array3 = this.midiData;
			n3 = 0;
			length = array4.length;
		}
		System.arraycopy(array2, n2, array3, n3, length);
	}

	private void writeVariableLengthQuantity(final int n) {
		int n2 = 7;
		int n3;
		while (true) {
			n3 = n2;
			if (n >> n3 == 0) {
				break;
			}
			n2 = n3 + 7;
		}
		int n5;
		int n4 = n5 = n3 - 7;
		while (true) {
			final int n6 = n5;
			if (n4 < 0) {
				break;
			}
			MidiBuilder a;
			int n7;
			if (n6 != 0) {
				a = this;
				n7 = (n >> n6 & 0x7F) + 128;
			} else {
				a = this;
				n7 = (n >> n6 & 0x7F);
			}
			a.writeInt(n7, 1);
			n4 = (n5 = n6 - 7);
		}
	}

	public final void initTrack() {
		this.writeInt(1297377380, 4);
		this.writeInt(6, 4);
		this.writeInt(1, 2);
		this.trackCountOffset = MidiBuilder.currentOffset;
		this.writeInt(1, 2);
		this.writeInt(240, 2);
		this.writeInt(1297379947, 4);
		this.trackLengthOffset = MidiBuilder.currentOffset;
		this.writeInt(0, 4);
		this.writeVariableLengthQuantity(0);
		this.writeInt(192, 1);
		this.writeInt(80, 1); // replaced from 64
		this.setTempo();
	}

	public final void addNote() {
		int n = (int) (240.0f * (4.0f / (1 << this.durationType)));
		Label_0047:
		{
			int n2;
			int n3;
			int n4;
			if (this.noteType == 1) {
				n2 = n;
				n3 = n;
				n4 = 2;
			} else {
				if (this.noteType != 2) {
					break Label_0047;
				}
				n2 = n + n / 2;
				n3 = n;
				n4 = 4;
			}
			n = n2 + n3 / n4;
		}
		this.writeVariableLengthQuantity(0);
		this.writeInt(144, 1);
		MidiBuilder a;
		int n5;
		if (this.note == 0) {
			a = this;
			n5 = 0;
		} else {
			a = this;
			n5 = 60 + (this.note - 1) + this.octave * 12;
		}
		a.writeInt(n5, 1);
		this.writeInt(255, 1);
		this.writeVariableLengthQuantity(n);
		this.writeInt(128, 1);
		MidiBuilder a2;
		int n6;
		if (this.note == 0) {
			this.writeInt(0, 1);
			a2 = this;
			n6 = 0;
		} else {
			this.writeInt(60 + (this.note - 1) + this.octave * 12, 1);
			int n7;
			if ((n7 = (int) (255.0f * (this.volume / 14.0f))) > 255) {
				n7 = 255;
			}
			a2 = this;
			n6 = n7;
		}
		a2.writeInt(n6, 1);
	}

	public final void setTempo() {
		this.writeVariableLengthQuantity(0);
		this.writeInt(255, 1);
		this.writeInt(81, 1);
		this.writeInt(3, 1);
		// tempo
		final int n = this.tempo == 0 ? 60000000 / 250 : 60000000 / this.tempo;
		this.writeInt(n >> 16, 1);
		this.writeInt(n >> 8 & 0xFF, 1);
		this.writeInt(n & 0xFF, 1);
	}

	public final void finishTrack() {
		this.writeVariableLengthQuantity(0);
		this.writeInt(255, 1);
		this.writeInt(47, 1);
		this.writeInt(0, 1);
		final int anInt1284 = MidiBuilder.currentOffset;
		MidiBuilder.currentOffset = this.trackLengthOffset;
		this.writeInt(anInt1284 - this.trackLengthOffset - 4, 4);
		this.resizeBuffer(MidiBuilder.currentOffset = anInt1284);
		this.copyTrackData(this.trackLengthOffset - 4, MidiBuilder.currentOffset, 10);
	}

	private void copyTrackData(final int n, final int n2, final int n3) {
		final int n4 = n2 - n;
		int n5 = this.midiData.length;
		for (int i = 0; i < n3; ++i) {
			this.resizeBuffer(n5 + n4);
			System.arraycopy(this.midiData, n, this.midiData, n5, n4);
			n5 = this.midiData.length;
		}
		final int anInt1284 = MidiBuilder.currentOffset;
		MidiBuilder.currentOffset = this.trackCountOffset;
		this.writeInt(n3 + 1, 2);
		MidiBuilder.currentOffset = anInt1284;
	}
}
