package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;
import javax.microedition.media.MediaException;

public abstract interface ReverbControl
  extends EffectControl
{
  public abstract int setReverbLevel(int paramInt)
    throws IllegalArgumentException;
  
  public abstract int getReverbLevel();
  
  public abstract void setReverbTime(int paramInt)
    throws IllegalArgumentException, MediaException;
  
  public abstract int getReverbTime()
    throws MediaException;
}
