package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class113 implements SelectionListener {
	private final Property aClass38_1163;

	Class113(final Property aClass38_1163) {
		super();
		this.aClass38_1163 = aClass38_1163;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		Property.method375(this.aClass38_1163);
		this.aClass38_1163.saveProperties();
		Property.method364(this.aClass38_1163).dispose();
	}

	public final void widgetDefaultSelected(final SelectionEvent selectionEvent) {
	}
}
