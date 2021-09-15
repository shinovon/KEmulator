package com.nokia.mid.sound;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

final class a
  implements PlayerListener
{
  private final Sound a;
  
  a(Sound paramSound)
  {
    this.a = paramSound;
  }
  
  public final void playerUpdate(Player paramPlayer, String paramString, Object paramObject)
  {
    if (paramString.equals("stopped"))
    {
      Sound.aI(this.a, 1);
      if (Sound.aL(this.a) != null) {
        Sound.aL(this.a).soundStateChanged(Sound.aS(this.a), 1);
      }
    }
  }
}
