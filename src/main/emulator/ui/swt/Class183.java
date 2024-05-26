package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class183 extends KeyAdapter {
	private final Property aClass38_1454;

	Class183(final Property aClass38_1454) {
		super();
		this.aClass38_1454 = aClass38_1454;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1454).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method417(this.aClass38_1454).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[13] = Property.method417(this.aClass38_1454).getText().trim();
		}
	}
}
