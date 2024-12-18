package emulator.ui.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

final class Class79 extends SelectionAdapter {
	private final Methods aClass46_863;

	Class79(final Methods aClass46_863) {
		super();
		this.aClass46_863 = aClass46_863;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final String text;
		if ((text = Methods.method434(this.aClass46_863).getText()).trim().length() > 0) {
			Methods.method445(this.aClass46_863, Methods.method440(this.aClass46_863).getText().indexOf(text, Methods.method437(this.aClass46_863)));
			if (Methods.method437(this.aClass46_863) != -1) {
				Methods.method440(this.aClass46_863).setSelection(Methods.method437(this.aClass46_863), Methods.method437(this.aClass46_863) + text.length());
			}
			Methods.method447(this.aClass46_863);
		}
	}
}
