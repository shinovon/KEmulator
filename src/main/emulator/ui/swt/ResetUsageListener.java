package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class ResetUsageListener extends SelectionAdapter {
	private final MemoryView mv;

	ResetUsageListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		for (int i = 0; i < this.mv.memoryMgr.images.size(); ++i) {
			this.mv.memoryMgr.images.get(i)._resetUsedRegion();
		}
		this.mv.updateEverything();
	}
}
