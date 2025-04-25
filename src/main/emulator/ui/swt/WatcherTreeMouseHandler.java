package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class WatcherTreeMouseHandler extends MouseAdapter {
	private final Watcher w;

	WatcherTreeMouseHandler(final Watcher w) {
		super();
		this.w = w;
	}

	public void mouseDown(final MouseEvent mouseEvent) {
		if (mouseEvent.button == 3) {
			w.openWatcherForSelected();
		}
	}

	public void mouseDoubleClick(final MouseEvent mouseEvent) {
		w.startFieldEditingForSelected();
	}
}
