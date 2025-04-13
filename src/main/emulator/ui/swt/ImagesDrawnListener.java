package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class ImagesDrawnListener extends SelectionAdapter {
	private final MemoryView mv;

	ImagesDrawnListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.setImagesDrawn();
	}
}
