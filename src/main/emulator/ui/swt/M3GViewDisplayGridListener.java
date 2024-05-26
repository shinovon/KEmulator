package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class M3GViewDisplayGridListener extends SelectionAdapter {
	private final M3GViewUI aClass90_826;

	M3GViewDisplayGridListener(final M3GViewUI aClass90_826) {
		super();
		this.aClass90_826 = aClass90_826;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		M3GViewUI.setGridVisible(this.aClass90_826, M3GViewUI.getGridItem(this.aClass90_826).getSelection());
	}
}
