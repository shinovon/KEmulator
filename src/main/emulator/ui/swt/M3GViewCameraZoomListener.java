package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class M3GViewCameraZoomListener extends SelectionAdapter {
	private final M3GViewUI aClass90_1164;

	M3GViewCameraZoomListener(final M3GViewUI aClass90_1164) {
		super();
		this.aClass90_1164 = aClass90_1164;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		M3GViewUI.setCameraMode(this.aClass90_1164, 3);
	}
}
