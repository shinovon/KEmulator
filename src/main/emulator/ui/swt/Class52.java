package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class52 extends SelectionAdapter {
	private final EmulatorScreen aClass93_803;

	Class52(final EmulatorScreen aClass93_803) {
		super();
		this.aClass93_803 = aClass93_803;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		EmulatorScreen.setInterpolation(this.aClass93_803, 0);
		this.aClass93_803.interposeNearestMenuItem.setSelection(true);
		this.aClass93_803.interposeLowMenuItem.setSelection(false);
		this.aClass93_803.interposeHightMenuItem.setSelection(false);
	}
}
