package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class MemoryViewAutoUpdateListener extends SelectionAdapter {
	private final MemoryView mv;

	MemoryViewAutoUpdateListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.setAutoUpdate();
	}
}
