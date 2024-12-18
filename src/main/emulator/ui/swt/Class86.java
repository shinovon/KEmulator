package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class86 extends SelectionAdapter {
	private final MessageConsole aClass83_883;

	Class86(final MessageConsole aClass83_883) {
		super();
		this.aClass83_883 = aClass83_883;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		MessageConsole.method485(this.aClass83_883, MessageConsole.method486(this.aClass83_883).getSelection());
	}
}
