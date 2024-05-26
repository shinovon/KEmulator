package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class68 extends SelectionAdapter {
	private final MemoryView aClass110_851;

	Class68(final MemoryView aClass110_851) {
		super();
		this.aClass110_851 = aClass110_851;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method679(this.aClass110_851, MemoryView.method682(this.aClass110_851).getSelection());
		MemoryView.method647(this.aClass110_851);
	}
}
