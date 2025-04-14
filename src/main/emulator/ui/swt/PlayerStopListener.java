package emulator.ui.swt;

import emulator.debug.Memory;
import emulator.debug.PlayerActionType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class PlayerStopListener extends SelectionAdapter {
	private final MemoryView aClass110_596;

	PlayerStopListener(final MemoryView aClass110_596) {
		super();
		this.aClass110_596 = aClass110_596;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_596).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_596).getSelectionIndex() < this.aClass110_596.memoryMgr.players.size()) {
			if ((value = this.aClass110_596.memoryMgr.players.get(MemoryView.method649(this.aClass110_596).getSelectionIndex())) != null) {
				Memory.modifyPlayer(value, PlayerActionType.stop);
			}
		}
	}
}
