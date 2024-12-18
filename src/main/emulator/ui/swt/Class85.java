package emulator.ui.swt;

import emulator.Emulator;
import emulator.KeyMapping;

final class Class85 implements Runnable {
	Class85(final Class50 class50) {
		super();
	}

	public final void run() {
		Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(KeyMapping.soft2(), true);
	}
}
