package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class M3GViewDisplayAxisListener extends SelectionAdapter {
	private final M3GViewUI aClass90_827;

	M3GViewDisplayAxisListener(final M3GViewUI aClass90_827) {
		super();
		this.aClass90_827 = aClass90_827;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		M3GViewUI.setAxisVisible(this.aClass90_827, M3GViewUI.getAxisItem(this.aClass90_827).getSelection());
	}
}
