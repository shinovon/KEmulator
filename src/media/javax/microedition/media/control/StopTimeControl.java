package javax.microedition.media.control;

import javax.microedition.media.*;

public interface StopTimeControl extends Control {
    public static final long RESET = Long.MAX_VALUE;

    long getStopTime();

    void setStopTime(final long p0);
}
