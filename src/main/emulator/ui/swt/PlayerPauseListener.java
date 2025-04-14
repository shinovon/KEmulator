package emulator.ui.swt;

import emulator.debug.Memory;
import emulator.debug.PlayerActionType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class PlayerPauseListener extends SelectionAdapter {
	private final MemoryView aClass110_597;

	PlayerPauseListener(final MemoryView aClass110_597) {
		super();
		this.aClass110_597 = aClass110_597;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_597).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_597).getSelectionIndex() < this.aClass110_597.memoryMgr.players.size()) {
			if ((value = this.aClass110_597.memoryMgr.players.get(MemoryView.method649(this.aClass110_597).getSelectionIndex())) != null) {
				Memory.modifyPlayer(value, PlayerActionType.pause);
			}
		}
	}
}
