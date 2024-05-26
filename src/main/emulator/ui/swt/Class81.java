package emulator.ui.swt;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

final class Class81 extends SelectionAdapter {
	private final Shell aShell866;

	Class81(final Class30 class30, final Shell aShell866) {
		super();
		this.aShell866 = aShell866;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		this.aShell866.close();
	}
}
