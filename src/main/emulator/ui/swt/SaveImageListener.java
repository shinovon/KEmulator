package emulator.ui.swt;

import emulator.UILocale;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;

import javax.microedition.lcdui.Image;

final class SaveImageListener extends SelectionAdapter {
	private final MemoryView mv;

	SaveImageListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		mv.exportSelectedImage();
	}
}
