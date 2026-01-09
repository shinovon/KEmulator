package emulator.ui.swt;

import emulator.Settings;
import emulator.UILocale;
import emulator.custom.h;
import emulator.custom.h.MethodInfo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public final class Methods implements Runnable, DisposeListener {
	private Shell shell;
	private SashForm mainSashForm;
	private Composite tablePanel;
	private Button resetCallsBtn;
	private Button exportBytecodeBtn;
	private Table methodsTable;
	private Composite detailsPanel;
	private Button showLineNumbersBtn;
	private Button showFramesBtn;
	private Text searchText;
	private Button searchBtn;
	private StyledText codeViewer;
	private Display display;
	private boolean visible;
	private static NumberFormat numberFormat;
	private int sortColumnIndex;
	private ArrayList methodDataList;

	public Methods() {
		super();
		this.shell = null;
		this.mainSashForm = null;
		this.tablePanel = null;
		this.resetCallsBtn = null;
		this.exportBytecodeBtn = null;
		this.methodsTable = null;
		this.detailsPanel = null;
		this.showLineNumbersBtn = null;
		this.showFramesBtn = null;
		this.searchText = null;
		this.searchBtn = null;
		this.codeViewer = null;
		Methods.numberFormat = NumberFormat.getInstance();
		this.methodDataList = new ArrayList();
	}

	private void refreshMethodData() {
		this.methodDataList.clear();
		this.methodDataList.addAll(h.methodProfiles.values());
		final Enumeration<h.MethodInfo> elements = h.methodProfiles.elements();
		while (elements.hasMoreElements()) {
			final h.MethodInfo data = elements.nextElement();
			final TableItem tableItem;
			(tableItem = new TableItem(this.methodsTable, 0)).setData(data);
			tableItem.setText(0, data.className);
			tableItem.setText(1, data.methodName);
			tableItem.setText(2, data.methodSignature);
			tableItem.setText(3, String.valueOf(data.codeSize));
			tableItem.setText(4, String.valueOf(data.refCount));
			tableItem.setText(5, String.valueOf(data.callCount));
			tableItem.setText(6, Methods.numberFormat.format(data.totalExecutionTime));
			tableItem.setText(7, Methods.numberFormat.format(data.averageExecutionTime));
			tableItem.setText(8, Methods.numberFormat.format(data.timePercentage));
		}
		this.run();
		this.sortMethodTable(0);
	}

	private void sortMethodTable(final int n) {
		this.methodsTable.setSortColumn(this.methodsTable.getColumn(n));
		this.methodsTable.setSortDirection((this.methodsTable.getSortDirection() == 128) ? 1024 : 128);
		Collections.sort((List<Object>) this.methodDataList, new MethodTableComparator(this, n));
		for (int i = this.methodDataList.size() - 1; i >= 0; --i) {
			final h.MethodInfo data = (MethodInfo) this.methodDataList.get(i);
			final TableItem item;
			(item = this.methodsTable.getItem(i)).setData(data);
			item.setText(0, data.className);
			item.setText(1, data.methodName);
			item.setText(2, data.methodSignature);
			item.setText(3, String.valueOf(data.codeSize));
			item.setText(4, String.valueOf(data.refCount));
			item.setText(5, String.valueOf(data.callCount));
			item.setText(5, String.valueOf(data.callCount));
			item.setText(6, Methods.numberFormat.format(data.totalExecutionTime));
			item.setText(7, Methods.numberFormat.format(data.averageExecutionTime));
			item.setText(8, Methods.numberFormat.format(data.timePercentage));
		}
	}

	public final void run() {
		if (!this.visible) {
			return;
		}
		try {
			long max = 0L;
			final Enumeration<h.MethodInfo> elements = (Enumeration<h.MethodInfo>) h.methodProfiles.elements();
			while (elements.hasMoreElements()) {
				max = Math.max(max, elements.nextElement().totalExecutionTime);
			}
			if (max > 0L) {
				final Enumeration<h.MethodInfo> elements2 = (Enumeration<h.MethodInfo>) h.methodProfiles.elements();
				while (elements2.hasMoreElements()) {
					final h.MethodInfo methodInfo = elements2.nextElement();
					methodInfo.timePercentage = 100.0f * methodInfo.totalExecutionTime / max;
				}
			}
			for (int i = this.methodDataList.size() - 1; i >= 0; --i) {
				final h.MethodInfo methodInfo2 = (MethodInfo) this.methodDataList.get(i);
				final TableItem item;
				(item = this.methodsTable.getItem(i)).setText(5, String.valueOf(methodInfo2.callCount));
				item.setText(6, Methods.numberFormat.format(methodInfo2.totalExecutionTime));
				item.setText(7, Methods.numberFormat.format(methodInfo2.averageExecutionTime));
				item.setText(8, Methods.numberFormat.format(methodInfo2.timePercentage));
			}
		} catch (Exception ignored) {
		}
	}

	public final void showWindow() {
		if (h.methodProfiles == null) {
			h.methodProfiles = new Hashtable();
			h.method591();
		}
		this.method449();
		this.display = Display.getCurrent();
		Rectangle clientArea = shell.getMonitor().getClientArea();
		Point size = shell.getSize();
		shell.setLocation(clientArea.x + (clientArea.width - size.x) / 2, clientArea.y + (clientArea.height - size.y) / 2);
		this.shell.open();
		this.shell.addDisposeListener(this);
		this.visible = true;
		this.refreshMethodData();
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		this.visible = false;
	}

	public final void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	public final boolean method438() {
		return this.visible;
	}

	private void method449() {
		(this.shell = new Shell(1264)).setText(UILocale.get("METHOD_FRAME_TITLE", "Methods"));
		this.shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setSize(new Point(752, 483));
		this.shell.setLayout(new GridLayout());
		this.method450();
	}

	private void method450() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		(this.mainSashForm = new SashForm(this.shell, 0)).setOrientation(512);
		this.method451();
		this.mainSashForm.setLayoutData(layoutData);
		this.method452();
	}

	private void method451() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 2;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = 4;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 2;
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		(this.tablePanel = new Composite(this.mainSashForm, 0)).setLayout(layout);
		(this.resetCallsBtn = new Button(this.tablePanel, 8388608)).setText(UILocale.get("METHOD_FRAME_RESET_CALLS", "Reset Calls"));
		this.resetCallsBtn.addSelectionListener(new ResetCallsListener(this));
		(this.exportBytecodeBtn = new Button(this.tablePanel, 8388608)).setText(UILocale.get("METHOD_FRAME_EXPORT_BYTECODE", "Export ByteCode"));
		this.exportBytecodeBtn.addSelectionListener(new ExportBytecodeListener(this));
		if (!Settings.enableMethodTrack)
			new Label(this.tablePanel, 8388608).setText("To track calls, enable it in System settings");
		(this.methodsTable = new Table(this.tablePanel, 67584)).setHeaderVisible(true);
		this.methodsTable.setLayoutData(layoutData);
		this.methodsTable.setLinesVisible(true);
		this.methodsTable.addSelectionListener(new TableSelectionListener(this));
		final TableColumn tableColumn;
		(tableColumn = new TableColumn(this.methodsTable, 0)).setWidth(100);
		tableColumn.setText("Class");
		tableColumn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(0);
			}
		});
		final TableColumn tableColumn2;
		(tableColumn2 = new TableColumn(this.methodsTable, 0)).setWidth(100);
		tableColumn2.setText("Name");
		tableColumn2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(1);
			}
		});
		final TableColumn tableColumn3;
		(tableColumn3 = new TableColumn(this.methodsTable, 0)).setWidth(200);
		tableColumn3.setText("Description");
		tableColumn3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(2);
			}
		});
		final TableColumn tableColumn4;
		(tableColumn4 = new TableColumn(this.methodsTable, 0)).setWidth(60);
		tableColumn4.setText("Code Size");
		tableColumn4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(3);
			}
		});
		final TableColumn tableColumn5;
		(tableColumn5 = new TableColumn(this.methodsTable, 0)).setWidth(60);
		tableColumn5.setText("References");
		tableColumn5.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(4);
			}
		});
		final TableColumn tableColumn6;
		(tableColumn6 = new TableColumn(this.methodsTable, 0)).setWidth(60);
		tableColumn6.setText("Calls");
		tableColumn6.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(5);
			}
		});
		final TableColumn tableColumn7;
		(tableColumn7 = new TableColumn(this.methodsTable, 0)).setWidth(60);
		tableColumn7.setText("Total Time(ms)");
		tableColumn7.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortMethodTable(6);
			}
		});
		final TableColumn tableColumn8;
		(tableColumn8 = new TableColumn(this.methodsTable, 0)).setWidth(60);
		tableColumn8.setText("Average Time(ms)");
		tableColumn8.addSelectionListener(new Class75(this));
		final TableColumn tableColumn9;
		(tableColumn9 = new TableColumn(this.methodsTable, 0)).setWidth(60);
		tableColumn9.setText("% Time");
		tableColumn9.addSelectionListener(new Class74(this));
	}

	private void displayMethodDetails(final TableItem[] array) {
		if (array == null || array.length < 1) {
			return;
		}
		final h.MethodInfo methodInfo;
		if ((methodInfo = (h.MethodInfo) array[0].getData()) != null) {
			this.codeViewer.setText(methodInfo.method705(this.showLineNumbersBtn.getSelection(), this.showFramesBtn.getSelection()));
		}
	}

	private void method452() {
		final GridData layoutData;
		(layoutData = new GridData()).grabExcessHorizontalSpace = false;
		layoutData.verticalAlignment = 4;
		layoutData.horizontalAlignment = 1;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalSpan = 4;
		layoutData2.grabExcessVerticalSpace = true;
		layoutData2.horizontalAlignment = 4;
		layoutData2.verticalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 4;
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		(this.detailsPanel = new Composite(this.mainSashForm, 0)).setLayout(layout);
		(this.showLineNumbersBtn = new Button(this.detailsPanel, 32)).setText("Show Line Numbers");
		this.showLineNumbersBtn.addSelectionListener(new Class77(this));
		(this.showFramesBtn = new Button(this.detailsPanel, 32)).setText("Show Frames    ");
		this.showFramesBtn.addSelectionListener(new Class76(this));
		(this.searchText = new Text(this.detailsPanel, 2048)).setLayoutData(layoutData);
		(this.searchBtn = new Button(this.detailsPanel, 8388608)).setText(UILocale.get("METHOD_FRAME_SEARCH", "Search"));
		this.searchBtn.addSelectionListener(new SearchButtonListener(this));
		this.searchBtn.addFocusListener(new Class78(this));
		(this.codeViewer = new StyledText(this.detailsPanel, 2562)).setLayoutData(layoutData2);
		this.codeViewer.setEditable(false);
		this.codeViewer.setIndent(3);
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}

	static Table method441(final Methods class46) {
		return class46.methodsTable;
	}

	static Shell method442(final Methods class46) {
		return class46.shell;
	}

	static void method443(final Methods class46, final TableItem[] array) {
		class46.displayMethodDetails(array);
	}

	static void method444(final Methods class46, final int n) {
		class46.sortMethodTable(n);
	}

	static Text method434(final Methods class46) {
		return class46.searchText;
	}

	static int method445(final Methods class46, final int anInt786) {
		return class46.sortColumnIndex = anInt786;
	}

	static int method437(final Methods class46) {
		return class46.sortColumnIndex;
	}

	static StyledText method440(final Methods class46) {
		return class46.codeViewer;
	}

	static int method447(final Methods class46) {
		return class46.sortColumnIndex++;
	}
}
