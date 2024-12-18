package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface PitchControl extends Control {
	int getMaxPitch();

	int getMinPitch();

	int getPitch();

	int setPitch(final int p0);
}
