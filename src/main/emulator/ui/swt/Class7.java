package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class7 extends SelectionAdapter {
	private final MemoryView aClass110_563;

	Class7(final MemoryView aClass110_563) {
		super();
		this.aClass110_563 = aClass110_563;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_563).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_563).getSelectionIndex() < MemoryView.method629(this.aClass110_563).players.size() && (value = MemoryView.method629(this.aClass110_563).players.get(MemoryView.method649(this.aClass110_563).getSelectionIndex())) != null) {
			Memory.playerAct(value, 0);
		}
	}
}
