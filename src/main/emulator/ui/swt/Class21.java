package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class21 extends SelectionAdapter {
	private final MemoryView aClass110_590;

	Class21(final MemoryView aClass110_590) {
		super();
		this.aClass110_590 = aClass110_590;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MemoryView.method624(this.aClass110_590).setText(String.valueOf(MemoryView.method630(this.aClass110_590).getSelection()));
		final Object value;
		if (MemoryView.method649(this.aClass110_590).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_590).getSelectionIndex() < MemoryView.method629(this.aClass110_590).players.size() && (value = MemoryView.method629(this.aClass110_590).players.get(MemoryView.method649(this.aClass110_590).getSelectionIndex())) != null) {
			Memory.setVolume(value, MemoryView.method630(this.aClass110_590).getSelection());
		}
	}
}
