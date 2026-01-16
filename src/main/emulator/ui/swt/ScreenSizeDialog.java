/*
Copyright (c) 2024 Arman Jussupgaliyev
*/
package emulator.ui.swt;

import emulator.UILocale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ScreenSizeDialog extends Dialog {

	protected int[] result;

	public ScreenSizeDialog(Shell parent, int width, int height) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		result = new int[]{width, height};
	}

	public int[] open() {
		Shell parentShell = getParent();
		Shell shell = new Shell(parentShell, getStyle());
		shell.setText(emulator.UILocale.get("SCREEN_SIZE_DIALOG_TITLE", "Set screen size"));
		createContents(shell);
		shell.pack();
		shell.setLocation(parentShell.getLocation().x + (parentShell.getSize().x - shell.getSize().x >> 1), parentShell.getLocation().y + (parentShell.getSize().y - shell.getSize().y >> 1));
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, true));

		GridData data = new GridData();
		data.horizontalSpan = 2;

		final Text width = new Text(shell, SWT.BORDER);
		width.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				event.doit = event.keyCode == SWT.BS || event.keyCode == SWT.DEL ||
						event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT ||
						Character.isDigit(event.character);
			}
		});
		width.setText(String.valueOf(result[0]));
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		width.setLayoutData(data);

		final Text height = new Text(shell, SWT.BORDER);
		height.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				event.doit = event.keyCode == SWT.BS || event.keyCode == SWT.DEL ||
						event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT ||
						Character.isDigit(event.character);
			}
		});
		height.setText(String.valueOf(result[1]));
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		height.setLayoutData(data);

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText(UILocale.get("DIALOG_SET", "Set"));
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				result = new int[]{Integer.parseInt(width.getText()), Integer.parseInt(height.getText())};
				shell.close();
			}
		});

		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText(UILocale.get("DIALOG_CANCEL", "Cancel"));
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				result = null;
				shell.close();
			}
		});

		shell.setDefaultButton(ok);

		width.setFocus();
	}
}