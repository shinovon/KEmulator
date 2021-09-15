package com.samsung.util;

import com.samsung.util.AudioClip;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

final class Class47
implements PlayerListener {
    private final AudioClip anAudioClip734;

    Class47(AudioClip audioClip) {
        this.anAudioClip734 = audioClip;
    }

    public final void playerUpdate(Player player, String string, Object object) {
        if (string.equals("stopped")) {
            AudioClip.method358((AudioClip)this.anAudioClip734, (int)0);
        }
    }
}
