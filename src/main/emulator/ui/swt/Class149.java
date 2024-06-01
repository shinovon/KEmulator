package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class149 extends MouseAdapter {
	private final KeyPad aClass161_1394;

	Class149(final KeyPad aClass161_1394) {
		super();
		this.aClass161_1394 = aClass161_1394;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1394, 13);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1394, 13);
	}
}
