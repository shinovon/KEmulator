package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class133 extends KeyAdapter {
	private final Property aClass38_1285;

	Class133(final Property aClass38_1285) {
		super();
		this.aClass38_1285 = aClass38_1285;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1285).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method385(this.aClass38_1285).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[2] = Property.method385(this.aClass38_1285).getText().trim();
		}
	}
}
