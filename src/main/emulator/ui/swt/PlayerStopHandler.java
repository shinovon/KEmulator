package emulator.ui.swt;

import emulator.debug.Memory;
import emulator.debug.PlayerActionType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class PlayerStopHandler extends SelectionAdapter {
	private final MemoryView aClass110_596;

	PlayerStopHandler(final MemoryView aClass110_596) {
		super();
		this.aClass110_596 = aClass110_596;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_596).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_596).getSelectionIndex() < MemoryView.method629(this.aClass110_596).players.size() && (value = MemoryView.method629(this.aClass110_596).players.get(MemoryView.method649(this.aClass110_596).getSelectionIndex())) != null) {
			Memory.playerAct(value, PlayerActionType.stop);
		}
	}
}
