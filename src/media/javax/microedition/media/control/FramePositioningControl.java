package javax.microedition.media.control;

import javax.microedition.media.*;

public interface FramePositioningControl extends Control {
    long mapFrameToTime(final int frameBumber);

    int mapTimeToFrame(final long mediaTime);

    int seek(final int frameNumber);

    int skip(final int framesToSkip);
}
