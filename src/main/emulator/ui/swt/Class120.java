package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class120 extends MouseAdapter {
	private final KeyPad aClass161_1199;

	Class120(final KeyPad aClass161_1199) {
		super();
		this.aClass161_1199 = aClass161_1199;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1199, 6);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1199, 6);
	}
}
