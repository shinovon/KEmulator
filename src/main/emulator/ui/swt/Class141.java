package emulator.ui.swt;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

final class Class141 implements ModifyListener {
	private final Watcher aClass5_1300;

	Class141(final Watcher aClass5_1300) {
		super();
		this.aClass5_1300 = aClass5_1300;
	}

	public final void modifyText(final ModifyEvent modifyEvent) {
		if (Watcher.method312(this.aClass5_1300).getSelection()) {
			Watcher.method317(this.aClass5_1300);
			EmulatorImpl.asyncExec(this.aClass5_1300);
		}
	}
}
