package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class144 extends SelectionAdapter {
	private final MemoryView aClass110_1303;

	Class144(final MemoryView aClass110_1303) {
		super();
		this.aClass110_1303 = aClass110_1303;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method627(this.aClass110_1303, MemoryView.method657(this.aClass110_1303).getSelection());
		MemoryView.method647(this.aClass110_1303);
	}
}
