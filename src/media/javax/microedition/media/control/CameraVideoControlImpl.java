package javax.microedition.media.control;

import javax.microedition.media.MediaException;
import javax.microedition.media.CapturePlayerImpl;
import javax.microedition.media.Player;

public class CameraVideoControlImpl implements VideoControl {

    CapturePlayerImpl p;
    int dx;
    int dy;
    int dw;
    int dh;
    int sw;
    int sh;
    boolean visible;
    boolean dfull;
    private Object dcanv;
    private int dmode;

    public CameraVideoControlImpl(final Object o) {
        super();
        this.p = (CapturePlayerImpl) o;
    }

    @Override
    public int getDisplayHeight() {
        return dh;
    }

    @Override
    public int getDisplayWidth() {
        return dw;
    }

    @Override
    public int getDisplayX() {
        return dx;
    }

    @Override
    public int getDisplayY() {
        return dy;
    }

    @Override
    public byte[] getSnapshot(String p0) throws MediaException {
        return p.getSnapshot(p0);
    }

    @Override
    public int getSourceHeight() {
        sh = p.getSourceHeight();
        return sh;
    }

    @Override
    public int getSourceWidth() {
        sw = p.getSourceWidth();
        return sw;
    }

    @Override
    public Object initDisplayMode(int p0, Object p1) {
        dcanv = p1;
        dmode = p0;
        return p.initDisplayMode(p0, p1);
    }

    @Override
    public void setDisplayFullScreen(boolean p0) {
        dfull = p0;
        p.setDisplayFullScreen(p0);
    }

    @Override
    public void setDisplayLocation(int p0, int p1) {
        dx = p0;
        dy = p0;
        p.setDisplayLocation(p0, p1);
    }

    @Override
    public void setDisplaySize(int p0, int p1) {
        dw = p0;
        dh = p1;
        p.setDisplaySize(p0, p1);
    }

    @Override
    public void setVisible(boolean p0) {
        visible = p0;
        p.setVisible(p0);
    }

}
