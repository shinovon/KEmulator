package emulator.ui.swt;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

final class Class78 extends FocusAdapter {
	private final Methods aClass46_862;

	Class78(final Methods aClass46_862) {
		super();
		this.aClass46_862 = aClass46_862;
	}

	public final void focusLost(final FocusEvent focusEvent) {
		Methods.method445(this.aClass46_862, 0);
	}
}
