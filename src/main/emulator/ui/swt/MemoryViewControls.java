package emulator.ui.swt;

import emulator.debug.Memory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MemoryViewControls extends Composite implements SelectionListener, ModifyListener, Runnable, FocusListener {

	private final MemoryView mv;
	private final Button autoUpdate;
	private final Text autoUpdateInterval;
	private final Button updateNow;
	private final CLabel objectsSize;
	private final CLabel totalSize;
	private final CLabel jvmSize;
	private final Button gc;

	private int maxObjectsSize = 0;
	private int objSize;

	private int interval = 500;

	public MemoryViewControls(Composite parent, MemoryView mv) {
		super(parent, 0);
		this.mv = mv;
		RowLayout rl = new RowLayout();
		rl.wrap = true;
		rl.center = true;
		rl.type = SWT.HORIZONTAL;
		rl.spacing = 2;
		setLayout(rl);

		autoUpdate = new Button(this, 32);
		autoUpdate.setText("Autoupdate: each ");
		autoUpdate.addSelectionListener(this);

		autoUpdateInterval = new Text(this, 2048);
		autoUpdateInterval.setText("500");
		autoUpdateInterval.addModifyListener(this);
		autoUpdateInterval.addFocusListener(this);

		new CLabel(this, 0).setText("ms ");

		updateNow = new Button(this, SWT.PUSH);
		updateNow.setText("Update now");
		updateNow.addSelectionListener(this);

		CLabel bytecodeSize = new CLabel(this, 0);
		bytecodeSize.setText("Bytecode size: " + Memory.getBytecodeSize() + "B  ");
		bytecodeSize.setToolTipText("Total size of all loaded classes in bytes");

		objectsSize = new CLabel(this, 0);
		objectsSize.setText("Objects (now/max): ????????B/????????B");
		objectsSize.setToolTipText("Total size of objects heap");

		totalSize = new CLabel(this, 0);
		totalSize.setText("Total memory used: ?????KiB");

		jvmSize = new CLabel(this, 0);
		jvmSize.setText("Real usage: ????/????MiB ");
		jvmSize.setToolTipText("Total memory, taken by KEmulator");

		gc = new Button(this, SWT.PUSH);
		gc.setText("GC");
		gc.setToolTipText("Perform garbage collection");
		gc.addSelectionListener(this);
	}

	public void refreshStats() {
		objSize = mv.memoryMgr.objectsSize();
		maxObjectsSize = Math.max(maxObjectsSize, objSize);
		getDisplay().asyncExec(this);
	}

	@Override
	public void run() {
		long t = Runtime.getRuntime().totalMemory();
		long f = Runtime.getRuntime().freeMemory();
		objectsSize.setText("Objects (now/max): " + objSize + "B/" + maxObjectsSize + "B  ");
		totalSize.setText("Total memory used: " + ((objSize + Memory.getBytecodeSize()) / 1024) + "KiB  ");
		jvmSize.setText("Real usage: " + ((t - f) / 1048576) + "/" + (t / 1048576) + "MiB  ");
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == autoUpdate) {
			mv.setAutoUpdate(autoUpdate.getSelection(), interval);
		} else if (e.widget == updateNow) {
			mv.updateEverything();
		} else if (e.widget == gc) {
			mv.updateEverything();
			System.gc();
			refreshStats(); // one more call after gc
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	@Override
	public void modifyText(ModifyEvent e) {
		try {
			interval = Integer.parseInt(autoUpdateInterval.getText());
			if (interval >= 10)
				mv.setAutoUpdate(autoUpdate.getSelection(), interval);
		} catch (NumberFormatException ignored) {
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.widget == autoUpdateInterval)
			autoUpdateInterval.setSelection(0, autoUpdateInterval.getText().length());
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.widget == autoUpdateInterval) {
			try {
				interval = Integer.parseInt(autoUpdateInterval.getText());
				if (interval >= 10) {
					mv.setAutoUpdate(autoUpdate.getSelection(), interval);
					return;
				}
			} catch (NumberFormatException ignored) {
			}
			// too small or not a number
			autoUpdateInterval.setText("500");
			interval = 500;
			mv.setAutoUpdate(autoUpdate.getSelection(), interval);
		}
	}
}
