package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class ImagesAscendListener extends SelectionAdapter {
	private final MemoryView mv;

	ImagesAscendListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.setImagesAscend();
	}
}
