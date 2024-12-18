package emulator.ui.swt;

import emulator.Emulator;
import emulator.KeyMapping;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

final class Class135 extends KeyAdapter {
	private Text text;
	private int i;

	Class135(Text text, int i) {
		super();
		this.text = text;
		this.i = i;
	}

	public final void keyPressed(final KeyEvent keyEvent) {
		keyEvent.keyCode &= 0xFEFFFFFF;
		if (((Property) Emulator.getEmulator().getProperty()).controllerCombo.getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
			text.setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
			Property.method365()[i] = text.getText().trim();
		}
		keyEvent.doit = false;
	}
}
