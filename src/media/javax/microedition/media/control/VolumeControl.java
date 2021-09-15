package javax.microedition.media.control;

import javax.microedition.media.*;

public interface VolumeControl extends Control
{
    int getLevel();
    
    boolean isMuted();
    
    int setLevel(final int p0);
    
    void setMute(final boolean p0);
}
