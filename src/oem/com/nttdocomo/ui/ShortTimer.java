package com.nttdocomo.ui;

import com.nttdocomo.util.*;

public final class ShortTimer implements TimeKeeper {
	protected ShortTimer() {
	}

	public static ShortTimer getShortTimer(final Canvas canvas, final int n, final int n2, final boolean b) {
		return new ShortTimer();
	}

	public int getResolution() {
		return 1;
	}

	public void start() {
	}

	public void stop() {
	}

	public void dispose() {
	}
}
