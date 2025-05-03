package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class TableListener extends SelectionAdapter {
	private final MemoryView mv;

	TableListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		try {
			mv.onClassTableItemSelection();
		} catch (Exception ignored) {
		}
	}
}
