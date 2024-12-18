package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface RateControl extends Control {
	int getMaxRate();

	int getMinRate();

	int getRate();

	int setRate(final int p0);
}
