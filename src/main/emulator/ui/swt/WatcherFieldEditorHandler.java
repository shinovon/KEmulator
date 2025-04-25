package emulator.ui.swt;

import emulator.debug.ClassTypes;
import emulator.debug.Instance;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import java.lang.reflect.Field;

final class WatcherFieldEditorHandler extends KeyAdapter implements FocusListener {
	private final TreeItem item;
	private final Text text;
	private final Watcher w;

	private Object targetObject;
	private Field targetField;
	private int targetIndex;

	WatcherFieldEditorHandler(final Watcher w, final TreeItem item, final Text text, Object targetObject, Field targetField, int targetIndex) {
		super();
		this.w = w;
		this.item = item;
		this.text = text;
		this.targetObject = targetObject;
		this.targetField = targetField;
		this.targetIndex = targetIndex;
	}

	public final void focusLost(final FocusEvent focusEvent) {
		item.setText(1, this.text.getText());
		apply();
		text.dispose();
	}

	public void focusGained(FocusEvent var1) {
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		switch (keyEvent.keyCode) {
			case 13: {
				item.setText(1, this.text.getText());
				apply();
				text.dispose();
				break;
			}
			case 27: {
				text.dispose();
				break;
			}
		}
	}

	private void apply() {
		synchronized (w) {
			if (targetField == null) {
				ClassTypes.setArrayValue(targetObject, targetIndex, text.getText());
			} else {
				ClassTypes.setFieldValue(targetObject, targetField, text.getText());
			}
		}
	}
}
