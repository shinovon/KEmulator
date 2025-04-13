package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class68 extends SelectionAdapter {
	private final MemoryView aClass110_851;

	Class68(final MemoryView aClass110_851) {
		super();
		this.aClass110_851 = aClass110_851;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final boolean aBoolean1131 = MemoryView.method682(this.aClass110_851).getSelection();
		this.aClass110_851.showReleasedImages = aBoolean1131;
		this.aClass110_851.updateEverything();
    }
}
