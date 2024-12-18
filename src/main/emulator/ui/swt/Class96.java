package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class96 extends MouseAdapter {
	private final KeyPad aClass161_1047;

	Class96(final KeyPad aClass161_1047) {
		super();
		this.aClass161_1047 = aClass161_1047;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1047, 4);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1047, 4);
	}
}
