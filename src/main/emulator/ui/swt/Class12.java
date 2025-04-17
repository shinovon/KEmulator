package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class12 extends MouseAdapter {
	private final Watcher aClass5_577;

	Class12(final Watcher aClass5_577) {
		super();
		this.aClass5_577 = aClass5_577;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		if (mouseEvent.button == 3) {
			this.aClass5_577.method307(Watcher.method309(this.aClass5_577).getSelection());
		}
	}

	public final void mouseDoubleClick(final MouseEvent mouseEvent) {
		this.aClass5_577.startFieldEditing(Watcher.method309(this.aClass5_577).getSelection());
	}
}
