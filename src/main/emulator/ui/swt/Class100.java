package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class100 extends MouseAdapter {
	private final KeyPad aClass161_1054;

	Class100(final KeyPad aClass161_1054) {
		super();
		this.aClass161_1054 = aClass161_1054;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1054, 3);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1054, 3);
	}
}
