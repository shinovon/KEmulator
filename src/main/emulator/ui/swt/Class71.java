package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class71 extends SelectionAdapter {
	private final MemoryView aClass110_854;

	Class71(final MemoryView aClass110_854) {
		super();
		this.aClass110_854 = aClass110_854;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method629(this.aClass110_854).releasedImages.clear();
		this.aClass110_854.updateEverything();
	}
}
