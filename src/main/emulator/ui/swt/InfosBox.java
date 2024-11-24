package emulator.ui.swt;

import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public final class InfosBox {
	private Shell shell;
	private Display aDisplay1070;
	private CLabel aCLabel1071;
	private boolean visible;

	public InfosBox() {
		super();
		this.shell = null;
		this.aCLabel1071 = null;
	}

	public final void method607(final Shell shell) {
		this.aDisplay1070 = Display.getCurrent();
		this.method612(shell);
		this.shell.setLocation(shell.getLocation().x + shell.getSize().x, shell.getLocation().y);
		this.shell.open();
		this.visible = true;
		shell.forceActive();
		while (!this.shell.isDisposed()) {
			if (!this.aDisplay1070.readAndDispatch()) {
				this.aDisplay1070.sleep();
			}
		}
		this.visible = false;
	}

	public final void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
	}

	public final void method609(final String text) {
		this.aCLabel1071.setText(text);
	}

	public final boolean isShown() {
		return this.visible;
	}

	public final Shell method611() {
		return this.shell;
	}

	private void method612(final Shell shell) {
		final GridData layoutData;
		(layoutData = new GridData()).grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = 4;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 1;
		layout.verticalSpacing = 0;
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.horizontalSpacing = 0;
		(this.shell = new Shell(shell, 8)).setLayout(layout);
		this.shell.setSize(new Point(130, 50));
		this.shell.setBackground(this.aDisplay1070.getSystemColor(2));
		(this.aCLabel1071 = new CLabel(this.shell, 0)).setText("Pos(0,0)\nColr(0)\nRect(0,0,0,0)");
		this.aCLabel1071.setLayoutData(layoutData);
		this.aCLabel1071.setBackground(this.aDisplay1070.getSystemColor(13));
	}
}
