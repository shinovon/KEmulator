package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class168 extends KeyAdapter {
	private final Property aClass38_1437;

	Class168(final Property aClass38_1437) {
		super();
		this.aClass38_1437 = aClass38_1437;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1437).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method394(this.aClass38_1437).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[5] = Property.method394(this.aClass38_1437).getText().trim();
		}
	}
}
