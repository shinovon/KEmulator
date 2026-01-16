/*
Copyright (c) 2025 Fyodor Ryzhov
*/
package emulator.ui.swt;

import emulator.ui.TargetedCommand;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class CommandsMenuListener implements SelectionListener {
	@Override
	public void widgetSelected(SelectionEvent e) {
		Object d = e.widget.getData();
		if (d instanceof TargetedCommand) {
			((TargetedCommand) d).invoke();
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent selectionEvent) {
	}
}
