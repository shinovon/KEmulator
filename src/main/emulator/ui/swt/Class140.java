package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class140 extends SelectionAdapter {
	private final MemoryView aClass110_1299;

	Class140(final MemoryView aClass110_1299) {
		super();
		this.aClass110_1299 = aClass110_1299;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method623(this.aClass110_1299, 1);
	}
}
