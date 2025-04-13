package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class31 extends SelectionAdapter {
	private final MemoryView aClass110_616;

	Class31(final MemoryView aClass110_616) {
		super();
		this.aClass110_616 = aClass110_616;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		this.aClass110_616.changeClassesSort(0);
	}
}
