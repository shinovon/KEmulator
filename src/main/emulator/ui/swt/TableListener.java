package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class TableListener extends MouseAdapter implements SelectionListener {
	private final MemoryView mv;

	TableListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public void mouseDown(final MouseEvent mouseEvent) {
		if (mouseEvent.button == 3) {
			mv.openWatcherForSelectedClass();
		}
	}

	public void widgetSelected(final SelectionEvent selectionEvent) {
		try {
			mv.onClassTableItemSelection();
		} catch (Exception ignored) {
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent selectionEvent) {

	}
}
