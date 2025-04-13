package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class19 extends SelectionAdapter {
	private final MemoryView mv;

	Class19(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.trySetImageSelectedIndexForClasses();
	}
}
