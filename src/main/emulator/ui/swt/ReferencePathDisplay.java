package emulator.ui.swt;

import emulator.debug.ClassTypes;
import emulator.debug.Memory;
import emulator.debug.ReferencePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class ReferencePathDisplay extends Composite implements SelectionListener {

	private final String rootName;

	public ReferencePathDisplay(Composite parent, ReferencePath path, boolean fullNames) {
		super(parent, 0);
		final GridData gd = new GridData();
		gd.horizontalAlignment = 4;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = 4;
		setLayoutData(gd);
		RowLayout rl = new RowLayout();
		rl.wrap = true;
		rl.center = true;
		rl.type = SWT.HORIZONTAL;
		setLayout(rl);

		if (path.isRootStatic) {
			rootName = path.root;
			Link l = new Link(this, SWT.NONE);
			if (fullNames)
				l.setText("<a>" + path.root + "</a>");
			else {
				String[] split = path.root.split("\\.");
				l.setText("<a>" + split[split.length - 1] + "</a>");
			}
			l.setToolTipText("Static class, click to open watcher");
			l.setLinkForeground(null);
			l.addSelectionListener(this);
		} else {
			rootName = null;
			Label l = new Label(this, SWT.NONE);
			l.setText(path.root);
			l.setToolTipText("This object was found in internal memory. Watcher can't be opened here");
		}
		for (ReferencePath.ReferencePathEntry e : path.getPath()) {
			if (!e.isIndex) {
				new Label(this, SWT.NONE).setText(" -> ");
			}
			if (ClassTypes.isObject(e.object.getClass())) {
				Link l = new Link(this, SWT.NONE);
				if (e.isIndex)
					l.setText("[<a>" + e.fieldName + "</a>]");
				else
					l.setText("<a>" + e.fieldName + "</a>");
				l.setToolTipText("Object of type \"" + e.object.getClass().getName() + "\", click to open watcher");
				l.setLinkForeground(null);
				l.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent selectionEvent) {
						new Watcher(e.object).open(getShell());
					}
				});
			} else {
				Label l = new Label(this, SWT.NONE);
				l.setText(e.toString());
				l.setToolTipText("Object of type \"" + e.object.getClass().getName() + "\"");
			}
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		new Watcher(Memory.cls(rootName)).open(getShell());
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}
}
