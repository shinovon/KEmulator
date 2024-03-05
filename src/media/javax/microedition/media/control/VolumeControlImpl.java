package javax.microedition.media.control;

import javax.microedition.media.*;

public class VolumeControlImpl implements VolumeControl {
    Player player;
    int volume;
    boolean mute;

    public VolumeControlImpl(final Object o) {
        super();
        this.player = (Player) o;
        this.volume = 100;
    }

    public int getLevel() {
        return this.volume;
    }

    public boolean isMuted() {
        return this.mute;
    }

    public int setLevel(final int anInt365) {
        if (anInt365 >= 0 && anInt365 <= 100) {
            this.volume = anInt365;
            if (this.player instanceof PlayerImpl) {
                ((PlayerImpl) this.player).setLevel(this.volume);
            }
        }
        return this.volume;
    }

    public void setMute(final boolean aBoolean366) {
        this.mute = aBoolean366;
        if (!(this.player instanceof PlayerImpl))
            return;
        ((PlayerImpl) player).setLevel(this.mute ? 0 : this.volume);
    }
}
