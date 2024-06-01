package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class163 extends SelectionAdapter {
	private final About aClass54_1431;

	Class163(final About aClass54_1431) {
		super();
		this.aClass54_1431 = aClass54_1431;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		About.method456(this.aClass54_1431).dispose();
	}
}
