package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class173 extends KeyAdapter {
	private final Property aClass38_1442;

	Class173(final Property aClass38_1442) {
		super();
		this.aClass38_1442 = aClass38_1442;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1442).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method397(this.aClass38_1442).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[6] = Property.method397(this.aClass38_1442).getText().trim();
		}
	}
}
