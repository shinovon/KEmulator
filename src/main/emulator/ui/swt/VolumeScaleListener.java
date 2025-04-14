package emulator.ui.swt;

import emulator.debug.Memory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class VolumeScaleListener extends SelectionAdapter {
	private final MemoryView mv;

	VolumeScaleListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		this.mv.audioVolumeLabel.setText(String.valueOf(mv.volumeScale.getSelection()));
		final Object value;
		int selected = mv.getSelectedAudioIndex();
		if (selected != -1 && selected < this.mv.memoryMgr.players.size()) {
			if ((value = this.mv.memoryMgr.players.get(mv.getSelectedAudioIndex())) != null) {
				Memory.setVolume(value, mv.volumeScale.getSelection());
			}
		}
	}
}
