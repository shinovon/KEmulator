package emulator.media.vlc;

import uk.co.caprica.vlcj.media.Meta;

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
		try {
			if (TITLE_KEY.equals(s)) {
				return player.mediaPlayer.media().meta().get(Meta.TITLE);
			} else if (AUTHOR_KEY.equals(s)) {
				return player.mediaPlayer.media().meta().get(Meta.ARTIST);
			} else if (DATE_KEY.equals(s)) {
				return player.mediaPlayer.media().meta().get(Meta.DATE);
			} else if (COPYRIGHT_KEY.equals(s)) {
				return player.mediaPlayer.media().meta().get(Meta.COPYRIGHT);
			}
		} catch (Exception ignored) {}

		return null;
	}
}
