package emulator.ui.swt;

import emulator.UILocale;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;

final class SaveAllImagesListener extends SelectionAdapter {
	private final MemoryView mv;

	SaveAllImagesListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void widgetSelected(final SelectionEvent selectionEvent) {
		final DirectoryDialog directoryDialog;
		(directoryDialog = new DirectoryDialog(mv.getShell())).setText(UILocale.get("MEMORY_VIEW_SAVE_ALL_IMAGES", "Save all images"));
		directoryDialog.setMessage(UILocale.get("MEMORY_VIEW_CHOOSE_DIRECTORY", "Choose a directory"));
		directoryDialog.setFilterPath(System.getProperty("user.dir"));
		final String open;
		if ((open = directoryDialog.open()) != null) {
			for (int i = 0; i < MemoryView.imagesToShow.size(); ++i) {
				try {
					MemoryView.imagesToShow.get(i).drawable.getImpl().saveToFile(open + "/" + i + ".png");
				} catch (Exception ignored) {
				}
			}
		}
	}
}
