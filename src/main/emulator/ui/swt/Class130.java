package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class130 extends MouseAdapter {
	private final KeyPad aClass161_1213;

	Class130(final KeyPad aClass161_1213) {
		super();
		this.aClass161_1213 = aClass161_1213;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1213, 0);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1213, 0);
	}
}
