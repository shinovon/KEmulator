package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class107 extends KeyAdapter {
	private final Property aClass38_1068;

	Class107(final Property aClass38_1068) {
		super();
		this.aClass38_1068 = aClass38_1068;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1068).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method366(this.aClass38_1068).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[17] = Property.method366(this.aClass38_1068).getText().trim();
		}
	}
}
