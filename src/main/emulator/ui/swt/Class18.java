package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class18 extends FocusAdapter {
	private final TreeItem aTreeItem583;
	private final Text aText584;
	private final Class5 aClass5_585;

	Class18(final Class5 aClass5_585, final TreeItem aTreeItem583, final Text aText584) {
		super();
		this.aClass5_585 = aClass5_585;
		this.aTreeItem583 = aTreeItem583;
		this.aText584 = aText584;
	}

	public final void focusLost(final FocusEvent focusEvent) {
		this.aTreeItem583.setText(1, this.aText584.getText());
		Class5.method315(this.aClass5_585, this.aTreeItem583, this.aText584.getText());
		this.aText584.dispose();
	}
}
