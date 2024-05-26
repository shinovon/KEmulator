package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class139 extends SelectionAdapter {
	private final Class5 aClass5_1298;

	Class139(final Class5 aClass5_1298) {
		super();
		this.aClass5_1298 = aClass5_1298;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Class5.method317(this.aClass5_1298);
		EmulatorImpl.asyncExec(Class5.method308(this.aClass5_1298));
	}
}
