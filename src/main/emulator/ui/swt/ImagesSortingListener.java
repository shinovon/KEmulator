package emulator.ui.swt;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

final class ImagesSortingListener implements ModifyListener {
	private final MemoryView mv;

	ImagesSortingListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void modifyText(final ModifyEvent modifyEvent) {
		mv.setImagesSorting();
	}
}
