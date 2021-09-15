package javax.microedition.media.control;

import javax.microedition.media.*;

public class VolumeControlImpl implements VolumeControl
{
    Player aPlayer364;
    int anInt365;
    boolean aBoolean366;
    
    public VolumeControlImpl(final Object o) {
        super();
        this.aPlayer364 = (Player)o;
        this.anInt365 = 100;
    }
    
    public int getLevel() {
        return this.anInt365;
    }
    
    public boolean isMuted() {
        return this.aBoolean366;
    }
    
    public int setLevel(final int anInt365) {
        if (anInt365 >= 0 && anInt365 <= 100) {
            this.anInt365 = anInt365;
            if (this.aPlayer364 instanceof PlayerImpl) {
                ((PlayerImpl)this.aPlayer364).setLevel(this.anInt365);
            }
        }
        return this.anInt365;
    }
    
    public void setMute(final boolean aBoolean366) {
        this.aBoolean366 = aBoolean366;
        PlayerImpl playerImpl;
        int anInt365;
        if (this.aBoolean366) {
            if (!(this.aPlayer364 instanceof PlayerImpl)) {
                return;
            }
            playerImpl = (PlayerImpl)this.aPlayer364;
            anInt365 = 0;
        }
        else {
            if (!(this.aPlayer364 instanceof PlayerImpl)) {
                return;
            }
            playerImpl = (PlayerImpl)this.aPlayer364;
            anInt365 = this.anInt365;
        }
        playerImpl.setLevel(anInt365);
    }
}
