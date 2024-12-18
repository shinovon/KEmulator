package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class128 extends MouseAdapter {
	private final KeyPad aClass161_1211;

	Class128(final KeyPad aClass161_1211) {
		super();
		this.aClass161_1211 = aClass161_1211;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1211, 11);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1211, 11);
	}
}
