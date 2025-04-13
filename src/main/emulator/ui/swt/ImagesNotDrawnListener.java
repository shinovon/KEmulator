package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class ImagesNotDrawnListener extends SelectionAdapter {
	private final MemoryView mv;

	ImagesNotDrawnListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.setImagesNotDrawn();
    }
}
