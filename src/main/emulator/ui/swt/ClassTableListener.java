package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class ClassTableListener extends MouseAdapter {
	private final MemoryView mv;

	ClassTableListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		if (mouseEvent.button == 3) {
			try {
				mv.openWatcherForSelected();
			} catch (Exception ignored) {
			}
		}
	}
}
