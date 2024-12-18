package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class134 extends MouseAdapter {
	private final KeyPad aClass161_1286;

	Class134(final KeyPad aClass161_1286) {
		super();
		this.aClass161_1286 = aClass161_1286;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1286, 7);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1286, 7);
	}
}
