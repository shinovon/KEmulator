package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class12 extends MouseAdapter {
	private final Class5 aClass5_577;

	Class12(final Class5 aClass5_577) {
		super();
		this.aClass5_577 = aClass5_577;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		if (mouseEvent.button == 3) {
			this.aClass5_577.method307(Class5.method309(this.aClass5_577).getSelection());
		}
	}

	public final void mouseDoubleClick(final MouseEvent mouseEvent) {
		Class5.method319(this.aClass5_577, Class5.method309(this.aClass5_577).getSelection());
	}
}
