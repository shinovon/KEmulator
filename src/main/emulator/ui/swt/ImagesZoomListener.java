package emulator.ui.swt;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

final class ImagesZoomListener implements ModifyListener {
	private final MemoryView mv;

	ImagesZoomListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void modifyText(final ModifyEvent modifyEvent) {
		mv.setImagesScaling();
	}
}
