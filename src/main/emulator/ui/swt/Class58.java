package emulator.ui.swt;

import emulator.graphics3D.view.M3GView3D;
import org.eclipse.swt.widgets.*;

final class Class58 implements Listener {
	private final M3GViewUI aClass90_824;

	Class58(final M3GViewUI aClass90_824) {
		super();
		this.aClass90_824 = aClass90_824;
	}

	public final void handleEvent(final Event event) {
		switch (event.type) {
			case 12: {
				M3GViewUI.method243(this.aClass90_824, false);
				M3GView3D.releaseContext();
				break;
			}
		}
	}
}
