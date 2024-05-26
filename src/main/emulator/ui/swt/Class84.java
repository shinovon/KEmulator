package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class84 extends SelectionAdapter {
	private final Class83 aClass83_882;

	Class84(final Class83 aClass83_882) {
		super();
		this.aClass83_882 = aClass83_882;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Class83.method489(this.aClass83_882).setText("");
	}
}
