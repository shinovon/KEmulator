package emulator.ui.swt;

import emulator.Emulator;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class43 extends MouseAdapter {
	private final EmulatorScreen aClass93_779;

	Class43(final EmulatorScreen aClass93_779) {
		super();
		this.aClass93_779 = aClass93_779;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		if (EmulatorScreen.method566(this.aClass93_779) == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		new Thread(new Class47(this)).start();
	}
}
