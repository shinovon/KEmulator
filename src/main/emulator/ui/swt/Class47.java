package emulator.ui.swt;

import emulator.Emulator;
import emulator.KeyMapping;

final class Class47 implements Runnable {
	Class47(final Class43 class43) {
		super();
	}

	public final void run() {
		Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(KeyMapping.soft1(), true);
	}
}
