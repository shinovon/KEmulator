package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class16 implements ModifyListener {
	private final Class5 aClass5_581;

	Class16(final Class5 aClass5_581) {
		super();
		this.aClass5_581 = aClass5_581;
	}

	public final void modifyText(final ModifyEvent modifyEvent) {
		this.aClass5_581.aBoolean545 = true;
		Class5.method317(this.aClass5_581);
		this.aClass5_581.aBoolean545 = false;
		this.aClass5_581.run();
	}
}
