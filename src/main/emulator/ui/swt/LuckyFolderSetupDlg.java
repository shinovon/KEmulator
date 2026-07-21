package emulator.ui.swt;

import emulator.Emulator;
import emulator.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public final class LuckyFolderSetupDlg {

	private Shell shell;
	private Table table;
	private java.util.List<FolderEntry> entries = new java.util.ArrayList<FolderEntry>();

	private static class FolderEntry {
		String path;
		boolean favBrowserMode;
		FolderEntry(String p, boolean m) { path = p; favBrowserMode = m; }
	}

	public void open(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
		shell.setText("Lucky Folder Setup");
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 6;
		gl.marginHeight = 6;
		gl.verticalSpacing = 6;
		shell.setLayout(gl);
		EmulatorScreen.markThemeable(shell);

		Label info = new Label(shell, SWT.WRAP);
		GridData infoData = new GridData(SWT.FILL, SWT.TOP, true, false);
		info.setLayoutData(infoData);
		info.setText("Configure lucky folders below.\n" +
			"  ✓ = show jars in Favorites Browser\n" +
			"  ✗ = pick jars randomly via Lucky Jar button");

		table = new Table(shell, SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableData.widthHint = 600;
		tableData.heightHint = 300;
		table.setLayoutData(tableData);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		TableColumn colPath = new TableColumn(table, SWT.LEFT);
		colPath.setText("Folder Path");
		colPath.setWidth(580);

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK) {
					int idx = table.indexOf((TableItem) e.item);
					if (idx >= 0 && idx < entries.size()) {
						entries.get(idx).favBrowserMode = ((TableItem) e.item).getChecked();
						autoSave();
					}
				}
			}
		});

		Composite btnBar = new Composite(shell, SWT.NONE);
		GridData btnBarData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		btnBar.setLayoutData(btnBarData);
		GridLayout btnLayout = new GridLayout(2, true);
		btnLayout.marginWidth = 5;
		btnLayout.marginHeight = 5;
		btnBar.setLayout(btnLayout);

		Button addBtn = new Button(btnBar, SWT.PUSH);
		addBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addBtn.setText("Add Folder");
		addBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setText("Select Lucky Folder");
				String path = dd.open();
				if (path != null) {
					addEntry(path, true);
					autoSave();
				}
			}
		});

		Button removeBtn = new Button(btnBar, SWT.PUSH);
		removeBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		removeBtn.setText("Remove Selected");
		removeBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int idx = table.getSelectionIndex();
				if (idx >= 0 && idx < entries.size()) {
					table.remove(idx);
					entries.remove(idx);
					autoSave();
				}
			}
		});

		EmulatorScreen.applyThemeToShell(shell, Settings.favoritesDarkMode);

		loadFromSettings();
		shell.pack();
		shell.setMinimumSize(600, 350);
		shell.setLocation(1175, shell.getLocation().y);
		shell.open();
	}

	private void addEntry(String path, boolean mode) {
		entries.add(new FolderEntry(path, mode));
		TableItem item = new TableItem(table, SWT.NONE);
		item.setChecked(mode);
		item.setText(0, path);
	}

	private void loadFromSettings() {
		table.removeAll();
		entries.clear();
		String[] paths = Settings.luckyFolderPaths;
		boolean[] modes = Settings.luckyFolderFavBrowserMode;
		for (int i = 0; i < paths.length; i++) {
			boolean m = i < modes.length && modes[i];
			addEntry(paths[i], m);
		}
	}

	private void autoSave() {
		int n = entries.size();
		String[] paths = new String[n];
		boolean[] modes = new boolean[n];
		for (int i = 0; i < n; i++) {
			FolderEntry fe = entries.get(i);
			paths[i] = fe.path;
			modes[i] = fe.favBrowserMode;
		}
		LuckyFolderManager.setFolders(paths, modes);
		((Property) Emulator.getEmulator().getProperty()).saveProperties();
	}
}
