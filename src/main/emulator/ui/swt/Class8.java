package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class8 extends SelectionAdapter {
	private final Watcher aClass5_564;

	Class8(final Watcher aClass5_564) {
		super();
		this.aClass5_564 = aClass5_564;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		EmulatorImpl.asyncExec(Watcher.method308(this.aClass5_564));
	}
}
