package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class M3GViewXrayListener extends SelectionAdapter {
	private final M3GViewUI aClass90_829;

	M3GViewXrayListener(final M3GViewUI aClass90_829) {
		super();
		this.aClass90_829 = aClass90_829;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		aClass90_829.setXray(M3GViewUI.getXrayItem(this.aClass90_829).getSelection());
	}
}
