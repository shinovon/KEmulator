package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class174 extends KeyAdapter {
	private final Property aClass38_1443;

	Class174(final Property aClass38_1443) {
		super();
		this.aClass38_1443 = aClass38_1443;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1443).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method403(this.aClass38_1443).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[9] = Property.method403(this.aClass38_1443).getText().trim();
		}
	}
}
