package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class ShowReleasedImagesListener extends SelectionAdapter {
	private final MemoryView mv;

	ShowReleasedImagesListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.setShowReleasedImages();
    }
}
