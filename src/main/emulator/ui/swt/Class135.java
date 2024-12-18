package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class135 extends KeyAdapter {
	private final Property aClass38_1294;

	Class135(final Property aClass38_1294) {
		super();
		this.aClass38_1294 = aClass38_1294;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1294).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method380(this.aClass38_1294).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[1] = Property.method380(this.aClass38_1294).getText().trim();
		}
	}
}
