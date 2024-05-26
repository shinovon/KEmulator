package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class42 extends SelectionAdapter {
	private final EmulatorScreen aClass93_778;

	Class42(final EmulatorScreen aClass93_778) {
		super();
		this.aClass93_778 = aClass93_778;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		EmulatorScreen.method562(this.aClass93_778, 2);
		this.aClass93_778.interposeNearestMenuItem.setSelection(false);
		this.aClass93_778.interposeLowMenuItem.setSelection(false);
		this.aClass93_778.interposeHightMenuItem.setSelection(true);
	}
}
