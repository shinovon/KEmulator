package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class172 extends KeyAdapter {
	private final Property aClass38_1441;

	Class172(final Property aClass38_1441) {
		super();
		this.aClass38_1441 = aClass38_1441;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1441).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method399(this.aClass38_1441).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[7] = Property.method399(this.aClass38_1441).getText().trim();
		}
	}
}
