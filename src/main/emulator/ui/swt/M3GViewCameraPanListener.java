package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class M3GViewCameraPanListener extends SelectionAdapter {
	private final M3GViewUI aClass90_1056;

	M3GViewCameraPanListener(final M3GViewUI aClass90_1056) {
		super();
		this.aClass90_1056 = aClass90_1056;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		M3GViewUI.setCameraMode(this.aClass90_1056, 1);
	}
}
