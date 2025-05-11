package com.nttdocomo.ui;

import com.nttdocomo.util.*;

public interface ComponentListener extends EventListener {
	public static final int BUTTON_PRESSED = 1;
	public static final int SELECTION_CHANGED = 2;
	public static final int TEXT_CHANGED = 3;

	void componentAction(final Component p0, final int p1, final int p2);
}
