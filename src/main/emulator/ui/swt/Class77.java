package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class77 extends SelectionAdapter {
	private final Methods aClass46_861;

	Class77(final Methods aClass46_861) {
		super();
		this.aClass46_861 = aClass46_861;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Methods.method443(this.aClass46_861, Methods.method441(this.aClass46_861).getSelection());
	}
}
