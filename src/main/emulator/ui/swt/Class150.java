package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class150 extends MouseAdapter {
	private final KeyPad aClass161_1395;

	Class150(final KeyPad aClass161_1395) {
		super();
		this.aClass161_1395 = aClass161_1395;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1395, 15);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1395, 15);
	}
}
