package emulator.ui.swt;

import emulator.debug.Memory;
import emulator.debug.PlayerActionType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


final class PlayerResumeListener extends SelectionAdapter {
	private final MemoryView aClass110_563;

	PlayerResumeListener(final MemoryView aClass110_563) {
		super();
		this.aClass110_563 = aClass110_563;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (MemoryView.method649(this.aClass110_563).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_563).getSelectionIndex() < this.aClass110_563.memoryMgr.players.size()) {
			if ((value = this.aClass110_563.memoryMgr.players.get(MemoryView.method649(this.aClass110_563).getSelectionIndex())) != null) {
				Memory.modifyPlayer(value, PlayerActionType.resume);
			}
		}
	}
}
