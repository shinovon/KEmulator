package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class181 extends KeyAdapter {
	private final Property aClass38_1451;

	Class181(final Property aClass38_1451) {
		super();
		this.aClass38_1451 = aClass38_1451;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1451).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method409(this.aClass38_1451).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[11] = Property.method409(this.aClass38_1451).getText().trim();
		}
	}
}
