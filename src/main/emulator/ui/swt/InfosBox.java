package emulator.ui.swt;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class InfosBox {
	private Shell shell;
	private CLabel label;
	private boolean visible;

	public void open(final Shell parent) {
		Display display = Display.getCurrent();
		createShell(parent);
		shell.setLocation(parent.getLocation().x + parent.getSize().x, parent.getLocation().y);
		shell.open();

		visible = true;
		parent.forceActive();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		visible = false;
	}

	public void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
	}

	public void setText(final String text) {
		label.setText(text);
	}

	public boolean isShown() {
		return this.visible;
	}

	public Shell getShell() {
		return this.shell;
	}

	private void createShell(final Shell parent) {
		Display display = Display.getCurrent();

		final GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = 4;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.horizontalSpacing = 0;

		shell = new Shell(parent, 8);
		shell.setLayout(layout);
		shell.setSize(new Point(130, 50));
		shell.setBackground(display.getSystemColor(2));

		label = new CLabel(this.shell, 0);
		label.setText("Pos(0,0)\nColr(0)\nRect(0,0,0,0)");
		label.setLayoutData(layoutData);
		label.setBackground(display.getSystemColor(13));
	}
}
