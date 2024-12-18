package emulator.ui.swt;

import emulator.Emulator;
import emulator.KeyMapping;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import javax.microedition.lcdui.Screen;

final class Class32 implements Listener {
	private final EmulatorScreen aClass93_617;

	Class32(final EmulatorScreen aClass93_617) {
		super();
		this.aClass93_617 = aClass93_617;
	}

	public final void handleEvent(final Event event) {
		if (EmulatorScreen.method566(this.aClass93_617) == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
			return;
		}
		if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			return;
		}
		switch (event.type) {
			case 37: {
				Screen screen;
				int n;
				if (event.count > 0) {
					screen = Emulator.getScreen();
					n = 1;
				} else {
					if (event.count >= 0) {
						break;
					}
					screen = Emulator.getScreen();
					n = 6;
				}
				screen._invokeKeyPressed(KeyMapping.getArrowKeyFromDevice(n));
				break;
			}
		}
	}
}
