package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class155 extends MouseAdapter {
	private final KeyPad aClass161_1400;

	Class155(final KeyPad aClass161_1400) {
		super();
		this.aClass161_1400 = aClass161_1400;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1400, 17);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1400, 17);
	}
}
