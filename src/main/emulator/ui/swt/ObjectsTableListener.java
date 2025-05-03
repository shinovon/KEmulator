package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

final class ObjectsTableListener extends MouseAdapter implements SelectionListener {
	private final MemoryView mv;

	ObjectsTableListener(final MemoryView mv) {
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

	@Override
	public void widgetSelected(SelectionEvent e) {
		mv.onObjectTableItemSelection();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
