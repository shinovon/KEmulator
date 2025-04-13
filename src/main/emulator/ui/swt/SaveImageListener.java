package emulator.ui.swt;

import emulator.UILocale;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;

final class SaveImageListener extends SelectionAdapter {
	private final MemoryView mv;

	SaveImageListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final FileDialog fileDialog;
		(fileDialog = new FileDialog(mv.getShell(), 8192)).setText(UILocale.get("MEMORY_VIEW_SAVE_TO_FILE", "Save to file"));
		fileDialog.setFilterExtensions(new String[]{"*.png"});
		final String open;
		if ((open = fileDialog.open()) != null && mv.getSelectedImage() != null) {
			mv.getSelectedImage().getImpl().saveToFile(open);
		}
	}
}
