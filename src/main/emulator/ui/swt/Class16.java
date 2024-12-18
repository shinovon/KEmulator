package emulator.ui.swt;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

final class Class16 implements ModifyListener {
	private final Watcher aClass5_581;

	Class16(final Watcher aClass5_581) {
		super();
		this.aClass5_581 = aClass5_581;
	}

	public final void modifyText(final ModifyEvent modifyEvent) {
		this.aClass5_581.aBoolean545 = true;
		Watcher.method317(this.aClass5_581);
		this.aClass5_581.aBoolean545 = false;
		this.aClass5_581.run();
	}
}
