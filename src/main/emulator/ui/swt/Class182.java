package emulator.ui.swt;

import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

final class Class182 extends KeyAdapter {
	private final Property aClass38_1453;

	Class182(final Property aClass38_1453) {
		super();
		this.aClass38_1453 = aClass38_1453;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1453).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method419(this.aClass38_1453).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[16] = Property.method419(this.aClass38_1453).getText().trim();
		}
	}
}
