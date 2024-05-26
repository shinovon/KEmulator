package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class17 extends SelectionAdapter {
	private final MemoryView aClass110_582;

	Class17(final MemoryView aClass110_582) {
		super();
		this.aClass110_582 = aClass110_582;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method623(this.aClass110_582, 2);
	}
}
