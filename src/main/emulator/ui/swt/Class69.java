package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class69 extends SelectionAdapter {
	private final MemoryView aClass110_852;

	Class69(final MemoryView aClass110_852) {
		super();
		this.aClass110_852 = aClass110_852;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method674(this.aClass110_852, MemoryView.method678(this.aClass110_852).getSelection());
		MemoryView.method647(this.aClass110_852);
	}
}
