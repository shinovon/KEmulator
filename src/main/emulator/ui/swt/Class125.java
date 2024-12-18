package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class125 extends SelectionAdapter {
	private final MemoryView aClass110_1208;

	Class125(final MemoryView aClass110_1208) {
		super();
		this.aClass110_1208 = aClass110_1208;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method659(this.aClass110_1208, MemoryView.method666(this.aClass110_1208).getSelection());
		MemoryView.method647(this.aClass110_1208);
	}
}
