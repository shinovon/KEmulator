package emulator.ui.swt;

import emulator.Emulator;
import emulator.debug.ClassTypes;
import emulator.debug.Instance;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.*;

public final class Watcher extends SelectionAdapter implements Runnable, DisposeListener, TreeListener {
	private Shell shell;
	private Combo classCombo;
	private Text filterInput;
	private Button hexDecSwitch;
	private Button exportBtn;
	private boolean visible;
	public final Map<String, Instance> selectableClasses = new HashMap<>();
	public final WatcherType type;
	private Tree tree;
	private TreeEditor treeEditor;
	public static final Vector<Watcher> activeWatchers = new Vector();
	private boolean disposed;
	private boolean updateInProgress;
	private float propCol1;
	private float propCol2;
	private float propCol3;
	private TreeColumn column1;
	private TreeColumn column2;
	private TreeColumn column3;
	private long lastShellResizeEvent;

	public static final String SHELL_TYPE = "WATCHER";

	private Watcher(final WatcherType type) {
		super();
		this.type = type;
	}

	public static Watcher createForStatics() {
		return new Watcher(WatcherType.Static);
	}

	public static Watcher createForProfiler() {
		return new Watcher(WatcherType.Profiler);
	}

	public Watcher(final Object o) {
		this(WatcherType.Instance);
		final Instance c = new Instance(o.getClass().getName(), o);
		c.updateFields(null);
		this.selectableClasses.put(o.toString(), c);
	}

	public void fillClassList() {
		new Thread(new ClassListFiller(this)).start();
	}

	public Instance getWatched() {
		return this.selectableClasses.get(classCombo.getText());
	}

	/**
	 * Call this to update window title and tree layout.
	 */
	private void updateContent() {
		final Instance c = getWatched();
		if (c == null) {
			tree.removeAll();
			return;
		}
		String filterText = filterInput == null ? "" : filterInput.getText();
		c.updateFields(filterText.isEmpty() ? null : filterText);

		switch (type) {
			case Static: {
				String s = c.getCls().getName();
				this.shell.setText(s + " (static) - " + emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher"));
				break;
			}
			case Profiler:
				// it's already localized
				shell.setText(classCombo.getText());
				break;
			case Instance: {
				Object o = c.getInstance();
				String s = o.getClass().getName();
				String hash = Integer.toHexString(o.hashCode());
				this.shell.setText(s + " (" + hash + ") - " + emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher"));
				break;
			}
		}

		tree.removeAll();
		for (int i = 0; i < c.getFields().size(); ++i) {
			final Object value = c.getFields().get(i);
			if (value instanceof Field) {
				final Field field = (Field) c.getFields().get(i);
				final TreeItem treeItem = new TreeItem(this.tree, 0);
				treeItem.setText(0, field.getName());
				if (this.type != WatcherType.Profiler) {
					this.tree.getItem(i).setText(2, ClassTypes.getReadableClassName(field.getType()));
					if (field.getType().isArray()) {
						new TreeItem(treeItem, 0).setText(0, "");
					}
				}
			} else {
				new TreeItem(this.tree, 0).setText(0, value.getClass().getName());
				if (this.type == WatcherType.Instance) {
					this.tree.getItem(i).setText(2, value.getClass().getName());
				}
			}
		}
	}

	private void method301(final Instance c, final Field field, final TreeItem treeItem) {
		final String name = ClassTypes.getReadableClassName(field.getType());
		if (field.getType().isArray()) {
			final Object method870 = ClassTypes.getFieldValue(c.getInstance(), field);
			method305(method870, method303(method870, name.substring(0, name.length() - 2)), treeItem);
		}
	}

	private static String method303(final Object o, String method870) {
		if (method870.equalsIgnoreCase("java.lang.Object")) {
			try {
				final String string;
				final int index;
				if (Array.getLength(o) > 0 && (index = (string = Array.get(o, 0).toString()).indexOf(64)) != -1) {
					method870 = ClassTypes.method870(string.substring(0, index));
				}
			} catch (Exception ignored) {
			}
		}
		return method870;
	}

	private static void method305(final Object o, final String s, final TreeItem treeItem) {
		treeItem.removeAll();
		if (o != null) {
			for (int length = Array.getLength(o), i = 0; i < length; ++i) {
				final TreeItem treeItem2;
				(treeItem2 = new TreeItem(treeItem, 0)).setText(0, Integer.toString(i));
				treeItem2.setText(2, s);
				if (s.charAt(s.length() - 1) == ']') {
					new TreeItem(treeItem2, 0).setText(0, "");
				}
			}
			return;
		}
		new TreeItem(treeItem, 0).setText(0, "");
	}

	public void treeExpanded(final TreeEvent treeEvent) {
		TreeItem item = (TreeItem) treeEvent.item;
		if (item.getExpanded()) {
			return;
		}
		final Instance c = getWatched();
		if (item.getParentItem() == null) {
			this.method301(c, (Field) c.getFields().get(item.getParent().indexOf(item)), item);
			SWTFrontend.asyncExec(this);
			return;
		}
		TreeItem parentItem = item;
		final Stack stack = new Stack<TreeItem>();
		while (parentItem.getParentItem() != null) {
			stack.push(parentItem);
			parentItem = parentItem.getParentItem();
		}
		final Field field = (Field) c.getFields().get(parentItem.getParent().indexOf(parentItem));
		Object o = ClassTypes.getFieldValue(c.getInstance(), field);
		final String method869 = ClassTypes.getReadableClassName(field.getType());
		String s = method303(o, method869.substring(0, method869.length() - 2));
		String s2;
		while (true) {
			s2 = s;
			if (stack.isEmpty()) {
				break;
			}
			final TreeItem treeItem2 = (TreeItem) stack.pop();
			o = Array.get(o, treeItem2.getParentItem().indexOf(treeItem2));
			s = s2.substring(0, s2.length() - 2);
		}
		method305(o, s2, item);
		SWTFrontend.asyncExec(this);
	}

	public void treeCollapsed(TreeEvent var1) {
	}

	public final void openWatcherForSelected() {
		TreeItem[] array = tree.getSelection();
		if (array == null || array.length == 0) {
			return;
		}
		final Instance c = getWatched();
		Object o;
		Class<?> clazz;
		if (array[0].getParentItem() == null) {
			final Field field = (Field) c.getFields().get(array[0].getParent().indexOf(array[0]));
			o = ClassTypes.getFieldValue(c.getInstance(), field);
			clazz = field.getType();
		} else {
			TreeItem parentItem = array[0];
			final Stack stack = new Stack<TreeItem>();
			while (parentItem.getParentItem() != null) {
				stack.push(parentItem);
				parentItem = parentItem.getParentItem();
			}
			clazz = (o = ClassTypes.getFieldValue(c.getInstance(), (Field) c.getFields().get(parentItem.getParent().indexOf(parentItem)))).getClass().getComponentType();
			while (!stack.isEmpty()) {
				final TreeItem treeItem = (TreeItem) stack.pop();
				final int index = treeItem.getParentItem().indexOf(treeItem);
				clazz = o.getClass().getComponentType();
				o = Array.get(o, index);
			}
		}
		if (o != null && ClassTypes.method871(clazz)) {
			new Watcher(o).open(shell);
		}
	}

	public void startFieldEditingForSelected() {
		TreeItem[] array = tree.getSelection();
		if (array == null || array.length == 0) {
			return;
		}
		final TreeItem treeItem = array[0];
		Instance c = getWatched();

		// target will be null for static fields.
		Object target;
		// field will be null if we will work with an array.
		Field targetField;
		// index will contain sane value if target is array.
		int targetIndex;

		if (treeItem.getParentItem() == null) {
			target = c.getInstance();
			targetField = (Field) c.getFields().get(treeItem.getParent().indexOf(treeItem));
			targetIndex = -1;
		} else {
			TreeItem parentItem = treeItem;
			final Stack stack = new Stack<TreeItem>();
			while (parentItem.getParentItem() != null) {
				stack.push(parentItem);
				parentItem = parentItem.getParentItem();
			}
			int n = parentItem.getParent().indexOf(parentItem);
			Object o = ClassTypes.getFieldValue(c.getInstance(), (Field) c.getFields().get(n));
			Object o2;
			Label_0140:
			while (true) {
				o2 = o;
				while (!stack.isEmpty()) {
					final TreeItem treeItem2 = (TreeItem) stack.pop();
					n = treeItem2.getParentItem().indexOf(treeItem2);
					if (!stack.isEmpty()) {
						o = Array.get(o2, n);
						continue Label_0140;
					}
				}
				break;
			}
			target = o2;
			targetIndex = n;
			targetField = null;
		}

		// somehow nothing to edit was found
		if (target == null && targetField == null)
			return;

		if (targetField != null) {
			// not an array. Checking target type.
			if (!ClassTypes.canSetFieldValue(targetField))
				return;
			// attempt to edit "instance" field over null instance
			if (target == null && !Modifier.isStatic(targetField.getModifiers()))
				return;
		}

		final Text control = new Text(this.tree, 0);
		control.setText(treeItem.getText(1));
		control.selectAll();
		control.setFocus();
		WatcherFieldEditorHandler handler = new WatcherFieldEditorHandler(this, treeItem, control, target, targetField, targetIndex);
		control.addFocusListener(handler);
		control.addKeyListener(handler);
		this.treeEditor.setEditor(control, treeItem, 1);
	}

	public final void open(final Shell parent) {
		createWidgets();
		updateContent();
		setInitialRect(parent);

		shell.open();
		shell.addDisposeListener(this);
		shell.setData("TYPE", SHELL_TYPE);
		updateColumnSizes();
		disposed = false;
		visible = true;
		SWTFrontend.asyncExec(this);
		Watcher.activeWatchers.addElement(this);
		Display display = SWTFrontend.getDisplay();
		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		visible = false;
	}

	private void setInitialRect(Shell parent) {
		String type = String.valueOf(parent.getData("TYPE"));
		final int WIDTH = 400;
		final int HEIGHT = 500;
		final int OFFSET = 40;
		Rectangle dsp = shell.getMonitor().getClientArea();

		if (parent.getMaximized()) {
			// let WM choose position
			shell.setSize(WIDTH, HEIGHT);
			return;
		}

		switch (type) {
			case Watcher.SHELL_TYPE: {
				// parent is watcher
				Point ps = parent.getSize();
				Point plc = parent.getLocation();

				shell.setSize(ps);

				if (plc.y + OFFSET + ps.y > dsp.height) {
					// y overflow
					shell.setLocation(plc.x - OFFSET, OFFSET);
				} else if (plc.x + OFFSET + ps.x > dsp.width) {
					// x overflow
					shell.setLocation(plc.x - OFFSET, plc.y + OFFSET);
				} else {
					shell.setLocation(plc.x + OFFSET, plc.y + OFFSET);
				}
				break;
			}
			case MemoryView.SHELL_TYPE: {
				// parent is memview
				shell.setSize(WIDTH, HEIGHT);
				shell.setLocation(parent.getLocation().x + parent.getSize().x + 10, parent.getLocation().y);
				break;
			}
			default: {
				// parent is main window
				shell.setSize(WIDTH, HEIGHT);
				if (parent.getSize().x < WIDTH + 100 || parent.getSize().y < HEIGHT + 100) {
					shell.setLocation(parent.getLocation().x + parent.getSize().x + 10, parent.getLocation().y);
					break;
				}
				int x = parent.getLocation().x + parent.getSize().x - WIDTH;
				int y = parent.getLocation().y + parent.getSize().y - HEIGHT;
				shell.setLocation(x, y);
				break;
			}
		}
	}

	public final void dispose() {
		this.disposed = true;
		Watcher.activeWatchers.removeElement(this);
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	public final boolean isVisible() {
		return this.visible;
	}

	private void fillClassCombo() {
		if (!this.selectableClasses.isEmpty()) {
			Set<String> classes = selectableClasses.keySet();
			final List list = Arrays.asList(classes.toArray());
			Collections.sort((List<Comparable>) list);
			final Enumeration enumeration = Collections.enumeration(list);
			while (enumeration.hasMoreElements()) {
				this.classCombo.add(enumeration.nextElement().toString());
			}
			this.classCombo.select(0);
		}
	}

	// see WatcherFieldEditorHandler - it locks "this" while writes the new value.
	public synchronized final void run() {
		if (this.selectableClasses.isEmpty() || !this.visible || this.updateInProgress || this.disposed) {
			return;
		}
		this.updateInProgress = true;
		final Instance c = getWatched();
		int n = 0;
		try {
			for (int i = 0; i < c.getFields().size(); ++i) {
				final Field field = (Field) c.getFields().get(i);
				if (this.disposed) {
					this.updateInProgress = false;
					return;
				}
				final String s = (!Modifier.isStatic(field.getModifiers()) && c.getInstance() == null) ? "" : ClassTypes.method874(c.getInstance(), field, hexDecSwitch != null && hexDecSwitch.getSelection());
				final TreeItem item;
				(item = this.tree.getItem(n++)).setText(1, s);
				this.fillArraySubtree(ClassTypes.getFieldValue(c.getInstance(), field), item);
			}
		} catch (Exception ignored) {
		}
		this.updateInProgress = false;
	}

	private void fillArraySubtree(final Object o, final TreeItem treeItem) {
		if (o == null) {
			return;
		}
		if (treeItem.getExpanded()) {
			for (int i = treeItem.getItemCount() - 1; i >= 0; --i) {
				final TreeItem item = treeItem.getItem(i);
				// readable value
				item.setText(1, ClassTypes.getArrayValue(o, i, hexDecSwitch != null && hexDecSwitch.getSelection()));
				// recursion for int[][][]... case
				fillArraySubtree(Array.get(o, i), item);
			}
		}
	}

	private void updateProportionsFromColumnWidths() {
		if (System.currentTimeMillis() - lastShellResizeEvent < 100)
			return;
		float sum = tree.getClientArea().width;

		propCol1 = column1.getWidth() / sum;

		if (column3 == null) {
			propCol2 = 1f - propCol1;
		} else {
			propCol2 = column2.getWidth() / sum;
			propCol3 = 1f - (propCol1 + propCol2);
		}
	}

	private void updateColumnSizes() {
		lastShellResizeEvent = System.currentTimeMillis(); // https://stackoverflow.com/questions/2074966/detecting-when-a-user-is-finished-resizing-swt-shell
		int width = tree.getClientArea().width;
		tree.setRedraw(false);

		column1.setWidth((int) (width * propCol1) - 1);
		column2.setWidth((int) (width * propCol2) - 1);
		if (column3 != null)
			column3.setWidth((int) (width * propCol3) - 1);

		tree.setRedraw(true);
	}

	private void createWidgets() {
		shell = new Shell();
		shell.setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Watches"));
		shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));

		shell.setMinimumSize(160, 140);

		boolean isClassComboUseful = createClassCombo();

		final GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = isClassComboUseful ? 2 : 3;
		shell.setLayout(shellLayout);

		if (isClassComboUseful)
			createExportBtn();

		if (type != WatcherType.Profiler) {
			createFilter();
			createHexSwitch();
		}

		if (!isClassComboUseful)
			createExportBtn();

		final GridData treeLayout = new GridData();
		treeLayout.horizontalAlignment = 4;
		treeLayout.horizontalSpan = isClassComboUseful ? 2 : 3;
		treeLayout.grabExcessHorizontalSpace = true;
		treeLayout.grabExcessVerticalSpace = true;
		treeLayout.verticalAlignment = 4;

		tree = new Tree(this.shell, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(treeLayout);
		if (this.type != WatcherType.Profiler) {
			tree.setToolTipText("Right click: open watcher\nDouble click: edit value");
			tree.addMouseListener(new WatcherTreeMouseHandler(this));
		}
		tree.addTreeListener(this);

		this.column1 = new TreeColumn(this.tree, SWT.LEFT);
		column1.setText("Name");
		column1.setMoveable(false);

		this.column2 = new TreeColumn(this.tree, SWT.LEFT);
		column2.setText("Value");
		column2.setMoveable(false);


		ControlAdapter onColumnResized = new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				updateProportionsFromColumnWidths();
			}
		};
		ControlAdapter onShellResized = new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				updateColumnSizes();
			}
		};

		column1.addControlListener(onColumnResized);
		column2.addControlListener(onColumnResized);

		if (this.type != WatcherType.Profiler) {
			this.column3 = new TreeColumn(this.tree, SWT.LEFT);
			this.column3.setText("Type");
			this.column3.setMoveable(false);
			column3.addControlListener(onColumnResized);
			propCol1 = propCol2 = propCol3 = 1f / 3f;
		} else {
			column3 = null;
			propCol1 = propCol2 = 0.5f;
		}

		this.shell.addControlListener(onShellResized);

		this.treeEditor = new TreeEditor(this.tree);
		this.treeEditor.horizontalAlignment = SWT.LEFT;
		this.treeEditor.grabHorizontal = true;
	}

	private void createFilter() {
		final GridData filterLayout = new GridData();
		filterLayout.horizontalAlignment = 4;
		filterLayout.grabExcessHorizontalSpace = true;
		filterLayout.verticalAlignment = 2;
		filterInput = new Text(this.shell, 2048);
		filterInput.setLayoutData(filterLayout);
		filterInput.setMessage("Filter fields");
		filterInput.setToolTipText("Filtering will be performed only by fields' names.");
		filterInput.addModifyListener(this::onFilterTextModify);
	}

	private void createHexSwitch() {
		hexDecSwitch = new Button(this.shell, 32);
		hexDecSwitch.setText("HEX");
		hexDecSwitch.setToolTipText("If checked, numbers will be shown in hexadecimal form.");
		hexDecSwitch.addSelectionListener(this);
	}

	private void createExportBtn() {
		exportBtn = new Button(this.shell, SWT.PUSH);
		exportBtn.setText("Export");
		exportBtn.setToolTipText("Watcher content will be saved to data folder");
		exportBtn.addSelectionListener(this);
	}

	public void widgetSelected(final SelectionEvent se) {
		if (se.widget == hexDecSwitch) {
			SWTFrontend.asyncExec(this);
		} else if (se.widget == exportBtn) {
			new Thread(this::exportValues).start();
		}
	}

	private void onFilterTextModify(ModifyEvent modifyEvent) {
		updateContent();
		SWTFrontend.asyncExec(this);
	}

	private boolean createClassCombo() {
		classCombo = new Combo(this.shell, SWT.READ_ONLY);
		classCombo.setVisibleItemCount(24);
		fillClassCombo();
		classCombo.addModifyListener(this::onClassSelect);
		boolean isUseful = classCombo.getItemCount() > 1;

		if (isUseful) {
			final GridData layoutData = new GridData();
			(layoutData).horizontalAlignment = 4;
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.verticalAlignment = 2;
			classCombo.setLayoutData(layoutData);
		} else {
			final GridData layoutData = new GridData();
			layoutData.exclude = true;
			classCombo.setLayoutData(layoutData);
			classCombo.setSize(0, 0);
			classCombo.setVisible(false);
		}

		return isUseful;
	}

	private void onClassSelect(ModifyEvent me) {
		synchronized (this) {
			updateContent();
		}
		run();
	}

	private void exportValues() {
		try {
			File file = new File(Emulator.getUserPath() + "/classwatcher.txt");
			if (!file.exists()) file.createNewFile();
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			try {
				final List list = Arrays.asList(selectableClasses.keySet().toArray());
				Collections.sort((List<Comparable>) list);
				for (Object o : list) {
					ps.println(o);
					final Instance c = (Instance) selectableClasses.get(o);
					c.updateFields(null);
					Vector fields = c.getFields();
					for (int i = 0; i < fields.size(); ++i) {
						final Object f = fields.get(i);
						if (f instanceof Field) {
							final Field field = (Field) c.getFields().get(i);
							Class type = field.getType();
							ps.print(" " + field.getDeclaringClass().getName() + "> " + ClassTypes.getReadableClassName(type) + " " + field.getName());
							try {
								Object v = field.get(c.getInstance());
								if (type.isArray()) {
									int l = Array.getLength(v);
									ps.println(" = " + ClassTypes.getReadableClassName(v.getClass()).replaceFirst("\\[\\]", "[" + l + "]"));
									ps.print(" [");
									for (int n = 0; n < l; n++) {
										ps.print(ClassTypes.asd(v, n, false));
										if (n != l - 1) ps.print(", ");
									}
									ps.println("]");
								} else {
									String s = v.toString();
									if (v instanceof String) {
										StringBuilder sb = new StringBuilder();
										sb.append('"');
										for (char ch : s.toCharArray()) {
											if (ch == '\r') {
												sb.append("\\r");
												continue;
											}
											if (ch == '\n') {
												sb.append("\\n");
												continue;
											}
											sb.append(ch);
										}
										sb.append('"');
										s = sb.toString();
									}
									ps.println(" = " + s);
								}
							} catch (NullPointerException e) {
								ps.println(" = null");
							} catch (Exception e) {
								ps.println();
								ps.println(" " + e);
							}
						}
					}
					ps.println();
				}
			} finally {
				ps.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}
}
