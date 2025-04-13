package emulator.ui.swt;

import emulator.debug.Memory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class AudioTableListener extends SelectionAdapter {
	private final MemoryView mv;

	AudioTableListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final Object value;
		if (mv.getSelectedAudioIndex() != -1 && mv.getSelectedAudioIndex() < this.mv.memoryMgr.players.size()) {
			if ((value = this.mv.memoryMgr.players.get(mv.getSelectedAudioIndex())) != null) {
				mv.volumeScale.setSelection(Memory.volume(value));
				this.mv.audioVolumeLable.setText(String.valueOf(mv.volumeScale.getSelection()));
				this.mv.audioProgressBar.setSelection(Memory.progress(value));
			}
		}
	}
}
