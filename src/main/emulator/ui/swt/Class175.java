package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class175 extends KeyAdapter {
	private final Property aClass38_1444;

	Class175(final Property aClass38_1444) {
		super();
		this.aClass38_1444 = aClass38_1444;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1444).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method405(this.aClass38_1444).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[0] = Property.method405(this.aClass38_1444).getText().trim();
		}
	}
}
