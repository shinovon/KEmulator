package emulator.ui.swt;

import emulator.debug.Memory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class24 extends SelectionAdapter {
	private final MemoryView aClass110_593;

	Class24(final MemoryView aClass110_593) {
		super();
		this.aClass110_593 = aClass110_593;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_593).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_593).getSelectionIndex() < MemoryView.method629(this.aClass110_593).players.size() && (value = MemoryView.method629(this.aClass110_593).players.get(MemoryView.method649(this.aClass110_593).getSelectionIndex())) != null) {
			MemoryView.method630(this.aClass110_593).setSelection(Memory.volume(value));
			MemoryView.method624(this.aClass110_593).setText(String.valueOf(MemoryView.method630(this.aClass110_593).getSelection()));
			MemoryView.method646(this.aClass110_593).setSelection(Memory.progress(value));
		}
	}
}
