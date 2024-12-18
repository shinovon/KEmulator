package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class165 extends SelectionAdapter {
	private final Methods aClass46_1433;

	Class165(final Methods aClass46_1433) {
		super();
		this.aClass46_1433 = aClass46_1433;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		try {
			Methods.method443(this.aClass46_1433, Methods.method441(this.aClass46_1433).getSelection());
		} catch (Exception ignored) {
		}
	}
}
