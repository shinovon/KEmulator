package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class87 extends SelectionAdapter {
	private final MessageConsole aClass83_884;

	Class87(final MessageConsole aClass83_884) {
		super();
		this.aClass83_884 = aClass83_884;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final String text;
		if ((text = MessageConsole.method489(this.aClass83_884).getText()) != null && text.length() > 0) {
			MessageConsole.method484(this.aClass83_884).addElement(text);
		}
	}
}
