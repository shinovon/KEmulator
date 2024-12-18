package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class179 extends KeyAdapter {
	private final Property aClass38_1449;

	Class179(final Property aClass38_1449) {
		super();
		this.aClass38_1449 = aClass38_1449;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1449).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method413(this.aClass38_1449).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[15] = Property.method413(this.aClass38_1449).getText().trim();
		}
	}
}
