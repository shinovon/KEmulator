package emulator.ui.swt;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

final class Class57 extends ControlAdapter {
	private final M3GViewUI aClass90_823;

	Class57(final M3GViewUI aClass90_823) {
		super();
		this.aClass90_823 = aClass90_823;
	}

	public final void controlResized(final ControlEvent controlEvent) {
		if (M3GViewUI.method499(this.aClass90_823).getVisible()) {
			M3GViewUI.method252(this.aClass90_823);
		}
	}
}
