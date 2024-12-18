package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class84 extends SelectionAdapter {
	private final MessageConsole aClass83_882;

	Class84(final MessageConsole aClass83_882) {
		super();
		this.aClass83_882 = aClass83_882;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MessageConsole.method489(this.aClass83_882).setText("");
	}
}
