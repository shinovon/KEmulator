package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class17 extends SelectionAdapter {
	private final MemoryView aClass110_582;

	Class17(final MemoryView aClass110_582) {
		super();
		this.aClass110_582 = aClass110_582;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		this.aClass110_582.changeClassesSort(2);
	}
}
