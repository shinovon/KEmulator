package emulator.media.tone;

import javax.microedition.media.control.ToneControl;

public class ToneControlImpl implements ToneControl {
	private MIDITonePlayer p;

	public ToneControlImpl() {
	}

	public ToneControlImpl(MIDITonePlayer p) {
		this.p = p;
	}

	public void setSequence(final byte[] array) {
		if(p == null) return;
		p.setSequence(array);
	}
}
