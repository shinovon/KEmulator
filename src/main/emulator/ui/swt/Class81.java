package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

final class Class81 extends SelectionAdapter {
	private final Shell aShell866;

	Class81(final M3GViewCameraSetDialog class30, final Shell aShell866) {
		super();
		this.aShell866 = aShell866;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		this.aShell866.close();
	}
}
