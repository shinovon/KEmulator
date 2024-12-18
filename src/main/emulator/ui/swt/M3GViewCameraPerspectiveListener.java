package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class M3GViewCameraPerspectiveListener extends SelectionAdapter {
	private final M3GViewUI aClass90_1162;

	M3GViewCameraPerspectiveListener(final M3GViewUI aClass90_1162) {
		super();
		this.aClass90_1162 = aClass90_1162;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		M3GViewUI.method510(this.aClass90_1162, 0);
		M3GViewUI.method505(this.aClass90_1162).setEnabled(true);
		M3GViewUI.method252(this.aClass90_1162);
	}
}
