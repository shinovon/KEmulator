package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class145 extends MouseAdapter {
	private final KeyPad aClass161_1376;

	Class145(final KeyPad aClass161_1376) {
		super();
		this.aClass161_1376 = aClass161_1376;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1376, 2);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1376, 2);
	}
}
