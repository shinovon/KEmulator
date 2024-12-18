package emulator.ui.swt;

import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.widgets.TreeItem;

final class Class6 extends TreeAdapter {
	private final Watcher aClass5_562;

	Class6(final Watcher aClass5_562) {
		super();
		this.aClass5_562 = aClass5_562;
	}

	public final void treeExpanded(final TreeEvent treeEvent) {
		if (!((TreeItem) treeEvent.item).getExpanded()) {
			Watcher.method316(this.aClass5_562, (TreeItem) treeEvent.item);
			EmulatorImpl.asyncExec(Watcher.method308(this.aClass5_562));
		}
	}
}
