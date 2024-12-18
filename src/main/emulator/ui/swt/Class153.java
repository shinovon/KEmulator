package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class153 extends MouseAdapter {
	private final KeyPad aClass161_1398;

	Class153(final KeyPad aClass161_1398) {
		super();
		this.aClass161_1398 = aClass161_1398;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1398, 12);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1398, 12);
	}
}
