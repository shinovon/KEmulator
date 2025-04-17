package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class139 extends SelectionAdapter {
	private final Watcher aClass5_1298;

	Class139(final Watcher aClass5_1298) {
		super();
		this.aClass5_1298 = aClass5_1298;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Watcher.method317(this.aClass5_1298);
		EmulatorImpl.asyncExec(this.aClass5_1298);
	}
}
