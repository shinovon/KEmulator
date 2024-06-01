package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.program.*;

final class Class156 extends SelectionAdapter {
	Class156(final About class54) {
		super();
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Program.launch("mailto:liang.wu@glu.com");
	}
}
