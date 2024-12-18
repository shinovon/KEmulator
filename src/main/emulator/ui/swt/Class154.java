package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class154 extends MouseAdapter {
	private final KeyPad aClass161_1399;

	Class154(final KeyPad aClass161_1399) {
		super();
		this.aClass161_1399 = aClass161_1399;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1399, 18);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1399, 18);
	}
}
