package emulator.ui.swt;

import emulator.Controllers;
import emulator.Emulator;
import emulator.KeyMapping;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

final class KeyBindListener extends KeyAdapter {
	private final Text text;
	private final int i;

	KeyBindListener(Text text, int i) {
		super();
		this.text = text;
		this.i = i;
	}

	public final void keyPressed(final KeyEvent e) {
		e.doit = false;
		if (e.keyCode == SWT.BS) {
			int controller = ((Property) Emulator.getEmulator().getProperty()).controllerCombo.getSelectionIndex();
			if (controller == 0) {
				Property.getKeyboardBindings()[i] = "";
			} else {
				Controllers.bind(controller - 1, i, "");
			}
			text.setText("");
			return;
		}
		e.keyCode &= 0xFEFFFFFF;
		if (((Property) Emulator.getEmulator().getProperty()).controllerCombo.getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(e.keyCode)) != null) {
			text.setText(KeyMapping.keyToString(String.valueOf(e.keyCode)));
			Property.getKeyboardBindings()[i] = text.getText().trim();
		}
	}
}
