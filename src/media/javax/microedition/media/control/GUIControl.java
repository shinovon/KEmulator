package javax.microedition.media.control;

import javax.microedition.media.*;

public interface GUIControl extends Control
{
    public static final int USE_GUI_PRIMITIVE = 0;
    
    Object initDisplayMode(final int p0, final Object p1);
}
