package emulator.ui.swt;

import emulator.debug.Memory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class MemoryViewControls extends Composite implements SelectionListener, ModifyListener, Runnable, FocusListener {

	private final MemoryView mv;
	private final Button autoUpdate;
	private final Text autoUpdateInterval;
	private final Button updateNow;
	private final Label objectsSize;
	private final Label totalSize;
	private final Label jvmSize;
	private final Button gc;
	private final Button packages;

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
		rl.spacing = 8;
		setLayout(rl);

		autoUpdate = new Button(this, 32);
		autoUpdate.setText("Autoupdate: each");
		autoUpdate.addSelectionListener(this);

		autoUpdateInterval = new Text(this, 2048);
		autoUpdateInterval.setText("500");
		autoUpdateInterval.addModifyListener(this);
		autoUpdateInterval.addFocusListener(this);

		new Label(this, 0).setText("ms");

		updateNow = new Button(this, SWT.PUSH);
		updateNow.setText("Update now");
		updateNow.addSelectionListener(this);

		Group bytecodeSizeGroup = new Group(this, SWT.NONE);
		bytecodeSizeGroup.setText("Bytecode size");
		bytecodeSizeGroup.setToolTipText("Total size of all loaded classes in bytes");
		bytecodeSizeGroup.setLayout(new RowLayout());
		Label bytecodeSize = new Label(bytecodeSizeGroup, 0);
		bytecodeSize.setText(Memory.getBytecodeSize() + " bytes");

		Group objectsSizeGroup = new Group(this, SWT.NONE);
		objectsSizeGroup.setText("Objects size");
		objectsSizeGroup.setToolTipText("Total size of objects heap");
		objectsSizeGroup.setLayout(new RowLayout());
		new Label(objectsSizeGroup, 0).setText("Now/max: ");
		objectsSize = new Label(objectsSizeGroup, 0);
		objectsSize.setText("????????B/????????B");

		Group totalSizeGroup = new Group(this, SWT.NONE);
		totalSizeGroup.setText("Total memory used");
		totalSizeGroup.setLayout(new RowLayout());
		totalSize = new Label(totalSizeGroup, 0);
		totalSize.setText("?????KiB");

		Group jvmSizeGroup = new Group(this, SWT.NONE);
		jvmSizeGroup.setText("Real usage");
		jvmSizeGroup.setToolTipText("Total memory, taken by KEmulator");
		jvmSizeGroup.setLayout(new RowLayout());
		jvmSize = new Label(jvmSizeGroup, 0);
		jvmSize.setText("????/????MiB");

		gc = new Button(this, SWT.PUSH);
		gc.setText("GC");
		gc.setToolTipText("Perform garbage collection");
		gc.addSelectionListener(this);

		packages = new Button(this, 32);
		packages.setText("Pkg names");
		packages.setToolTipText("If disabled, package names in class names will be hidden. Disable if you want to see \"Display\" instead of \"javax.microedition.lcdui.Display\".");
		packages.setSelection(true);
		packages.addSelectionListener(this);
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
		objectsSize.setText(objSize + "B / " + maxObjectsSize + "B");
		totalSize.setText(((objSize + Memory.getBytecodeSize()) / 1024) + "KiB");
		jvmSize.setText(((t - f) / 1048576) + "/" + (t / 1048576) + "MiB");
		this.layout(true, true);
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
		} else if (e.widget == packages) {
			mv.setPkgNamesDisplay(packages.getSelection());
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
