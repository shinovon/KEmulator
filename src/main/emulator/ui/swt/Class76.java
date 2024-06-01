package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class76 extends SelectionAdapter {
	private final Methods aClass46_860;

	Class76(final Methods aClass46_860) {
		super();
		this.aClass46_860 = aClass46_860;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Methods.method443(this.aClass46_860, Methods.method441(this.aClass46_860).getSelection());
	}
}
