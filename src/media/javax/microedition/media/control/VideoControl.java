package javax.microedition.media.control;

import javax.microedition.media.MediaException;

public interface VideoControl extends GUIControl {
    public static final int USE_DIRECT_VIDEO = 1;

    int getDisplayHeight();

    int getDisplayWidth();

    int getDisplayX();

    int getDisplayY();

    byte[] getSnapshot(final String p0) throws MediaException;

    int getSourceHeight();

    int getSourceWidth();

    Object initDisplayMode(final int p0, final Object p1);

    void setDisplayFullScreen(final boolean p0);

    void setDisplayLocation(final int p0, final int p1);

    void setDisplaySize(final int p0, final int p1);

    void setVisible(final boolean p0);
}
