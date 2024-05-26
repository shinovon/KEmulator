package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class176 extends KeyAdapter {
	private final Property aClass38_1445;

	Class176(final Property aClass38_1445) {
		super();
		this.aClass38_1445 = aClass38_1445;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (Property.method376(this.aClass38_1445).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			Property.method411(this.aClass38_1445).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[14] = Property.method411(this.aClass38_1445).getText().trim();
		}
	}
}
