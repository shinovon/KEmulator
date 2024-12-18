package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class92 extends MouseAdapter {
	private final KeyPad aClass161_940;

	Class92(final KeyPad aClass161_940) {
		super();
		this.aClass161_940 = aClass161_940;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_940, 5);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_940, 5);
	}
}
