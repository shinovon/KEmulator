package emulator.media.vlc;

import javax.microedition.media.control.MetaDataControl;

public class MetaDataControlImpl implements MetaDataControl {

	private final VLCPlayerImpl player;

	MetaDataControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	public String[] getKeys() {
		return new String[] { AUTHOR_KEY, COPYRIGHT_KEY, DATE_KEY, TITLE_KEY };
	}

	public String getKeyValue(String s) {

		return null;
	}
}
