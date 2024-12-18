package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class132 extends MouseAdapter {
	private final KeyPad aClass161_1232;

	Class132(final KeyPad aClass161_1232) {
		super();
		this.aClass161_1232 = aClass161_1232;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1232, 8);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1232, 8);
	}
}
