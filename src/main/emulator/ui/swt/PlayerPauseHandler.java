package emulator.ui.swt;

import emulator.debug.Memory;
import emulator.debug.PlayerActionType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class PlayerPauseHandler extends SelectionAdapter {
	private final MemoryView aClass110_597;

	PlayerPauseHandler(final MemoryView aClass110_597) {
		super();
		this.aClass110_597 = aClass110_597;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_597).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_597).getSelectionIndex() < MemoryView.method629(this.aClass110_597).players.size() && (value = MemoryView.method629(this.aClass110_597).players.get(MemoryView.method649(this.aClass110_597).getSelectionIndex())) != null) {
			Memory.playerAct(value, PlayerActionType.pause);
		}
	}
}
