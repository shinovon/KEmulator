package emulator.ui.swt;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

final class WatcherFieldEditorHandler extends KeyAdapter implements FocusListener {
	private final TreeItem item;
	private final Text text;
	private final Watcher w;

	WatcherFieldEditorHandler(final Watcher w, final TreeItem item, final Text text) {
		super();
		this.w = w;
		this.item = item;
		this.text = text;
	}

	public final void focusLost(final FocusEvent focusEvent) {
		item.setText(1, this.text.getText());
		w.finishFieldEditing(this.item, this.text.getText());
		text.dispose();
	}

	public void focusGained(FocusEvent var1) {
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		switch (keyEvent.keyCode) {
			case 13: {
				item.setText(1, this.text.getText());
				w.finishFieldEditing(this.item, this.text.getText());
				text.dispose();
				break;
			}
			case 27: {
				text.dispose();
				break;
			}
		}
	}
}
