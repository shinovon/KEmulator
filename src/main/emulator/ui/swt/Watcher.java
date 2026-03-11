package emulator.ui.swt;

import emulator.Emulator;
import emulator.debug.ClassTypes;
import emulator.debug.Instance;
import emulator.debug.Memory;
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

public final class Watcher extends SelectionAdapter implements Runnable, DisposeListener, TreeListener, ControlListener {
	private Shell shell;
	private Display display;
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
	private Shell parentShell;
	private boolean attachedToParent;

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

	public Watcher(final Class cls) {
		this(WatcherType.Static);
		final Instance c = new Instance(cls.getName(), null);
		c.updateFields(null);
		this.selectableClasses.put(cls.getName(), c);
	}

	public void fillClassList() {
		new Thread(new Runnable() {
			public void run() {
				try {
					switch (type) {
						case Static: {
							for (int i = 0; i < Emulator.jarClasses.size(); ++i) {
								final String s = Emulator.jarClasses.get(i);
								try {
									final Instance c;
									if ((c = new Instance(s, s.equals(Emulator.getMIDlet().getClass().getName()) ? Emulator.getMIDlet() : null)).updateFields(null)) {
										String s2 = c.toString();
										if (c.getCls().getSuperclass() != null) {
											s2 = s2 + "@" + c.getCls().getSuperclass().getName();
										}
										((Map) selectableClasses).put(s2, c);
									}
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
							break;
						}
						case Profiler: {
							final Instance c2 = new Instance("emulator.debug.Profiler", null);
							c2.updateFields(null);
							((Map) selectableClasses).put(emulator.UILocale.get("WATCHES_FRAME_PROFILER", "Profiler monitor"), c2);

							final Instance c3 = new Instance("emulator.debug.Profiler3D", null);
							((Map) selectableClasses).put(emulator.UILocale.get("WATCHES_FRAME_PROFILER_3D", "3D profiler monitor"), c3);
							break;
						}
					}
				} catch (Error ignored) {}
			}
		}).start();
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
				final TreeItem treeItem = new TreeItem(this.tree, 0);
				treeItem.setText(0, (String) value);
				if (this.type == WatcherType.Instance) {
					this.tree.getItem(i).setText(2, ClassTypes.getReadableClassName(c.getCls().getComponentType()));
					if (c.getCls().getComponentType().isArray()) {
						new TreeItem(treeItem, 0).setText(0, "");
					}
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
			Object field = c.getFields().get(item.getParent().indexOf(item));
			if (field instanceof Field) {
				this.method301(c, (Field) field, item);
				display.asyncExec(this);
			} else {
				final String name = ClassTypes.getReadableClassName(c.getCls().getComponentType());
				final Object method870 = Array.get(c.getInstance(), Integer.parseInt((String) field));
				method305(method870, method303(method870, name.substring(0, name.length() - 2)), item);
				display.asyncExec(this);
			}
			return;
		}
		TreeItem parentItem = item;
		final Stack stack = new Stack<TreeItem>();
		while (parentItem.getParentItem() != null) {
			stack.push(parentItem);
			parentItem = parentItem.getParentItem();
		}
		final Object field = c.getFields().get(parentItem.getParent().indexOf(parentItem));
		Object o;
		Class clazz;
		if (field instanceof Field) {
			o = ClassTypes.getFieldValue(c.getInstance(), (Field) field);
			clazz = ((Field) field).getType();
		} else {
			o = Array.get(c.getInstance(), Integer.parseInt((String) field));
			clazz = c.getCls().getComponentType();
		}
		final String method869 = ClassTypes.getReadableClassName(clazz);
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
		display.asyncExec(this);
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
			final Object field = c.getFields().get(array[0].getParent().indexOf(array[0]));
			if (field instanceof Field) {
				o = ClassTypes.getFieldValue(c.getInstance(), (Field) field);
				clazz = ((Field) field).getType();
			} else {
				o = Array.get(c.getInstance(), Integer.parseInt((String) field));
				clazz = c.getCls().getComponentType();
			}
		} else {
			TreeItem parentItem = array[0];
			final Stack stack = new Stack<TreeItem>();
			while (parentItem.getParentItem() != null) {
				stack.push(parentItem);
				parentItem = parentItem.getParentItem();
			}
			Object field = c.getFields().get(parentItem.getParent().indexOf(parentItem));
			if (field instanceof Field) {
				o = ClassTypes.getFieldValue(c.getInstance(), (Field) field);
				clazz = ((Field) field).getType().getComponentType();
			} else {
				o = Array.get(c.getInstance(), Integer.parseInt((String) field));
				clazz = c.getCls().getComponentType();
			}
			if (o == null) return;
			while (!stack.isEmpty()) {
				final TreeItem treeItem = (TreeItem) stack.pop();
				final int index = treeItem.getParentItem().indexOf(treeItem);
				clazz = o.getClass().getComponentType();
				o = Array.get(o, index);
			}
		}
		if (o != null && (ClassTypes.isObject(clazz) || clazz.isArray())) {
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
			Object field = c.getFields().get(treeItem.getParent().indexOf(treeItem));
			if (field instanceof Field) {
				targetField = (Field) field;
				targetIndex = -1;
			} else {
				targetField = null;
				targetIndex = Integer.parseInt((String) field);
			}
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
		display = parent.getDisplay();
		this.parentShell = parent;
		createWidgets();
		updateContent();
		setInitialRect(parent);

		shell.open();
		shell.addDisposeListener(this);
		shell.addControlListener(this);
		shell.setData("TYPE", SHELL_TYPE);
		updateColumnSizes();
		shell.layout();
		disposed = false;
		visible = true;
		display.asyncExec(this);
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
				if (parent.getSize().x < WIDTH || parent.getSize().y < HEIGHT) {
					shell.setSize(WIDTH, HEIGHT);
				} else {
					shell.setSize(parent.getSize());
				}
				shell.setLocation(parent.getLocation().x - parent.getSize().x, parent.getLocation().y);
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
		boolean hex = hexDecSwitch != null && hexDecSwitch.getSelection();
		try {
			if (Memory.getInstance().isNotInitialized(c.getCls())) {
				this.updateInProgress = false;
				return;
			}
			for (int i = 0; i < c.getFields().size(); ++i) {
				final Object field = c.getFields().get(i);
				if (this.disposed) {
					this.updateInProgress = false;
					return;
				}
				if (field instanceof Field) {
					final String s = (!Modifier.isStatic(((Field) field).getModifiers()) && c.getInstance() == null) ? "" : ClassTypes.getFieldValue(c.getInstance(), (Field) field, hex);
					final TreeItem item = this.tree.getItem(n++);
					item.setText(1, s);
					this.fillArraySubtree(ClassTypes.getFieldValue(c.getInstance(), (Field) field), item);
				} else {
					TreeItem item = this.tree.getItem(n++);
					Object value = ClassTypes.getArrayValue(c.getInstance(), Integer.parseInt((String) field), hex);
					item.setText(1, String.valueOf(value));
					this.fillArraySubtree(Array.get(c.getInstance(), Integer.parseInt((String) field)), item);
				}
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

		tree = new Tree(this.shell, SWT.FULL_SELECTION | SWT.BORDER);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(treeLayout);

		if (this.type != WatcherType.Profiler) {
			tree.setToolTipText("Right click: open watcher\nDouble click: edit value");
			tree.addMouseListener(new MouseAdapter() {
				public void mouseDown(final MouseEvent mouseEvent) {
					if (mouseEvent.button == 3) {
						openWatcherForSelected();
					}
				}

				public void mouseDoubleClick(final MouseEvent mouseEvent) {
					startFieldEditingForSelected();
				}
			});
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
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		hexDecSwitch.setLayoutData(gd);
	}

	private void createExportBtn() {
		exportBtn = new Button(this.shell, SWT.PUSH);
		exportBtn.setText("Export");
		exportBtn.setToolTipText("Watcher content will be saved to data folder");
		exportBtn.addSelectionListener(this);
	}

	public void widgetSelected(final SelectionEvent se) {
		if (se.widget == hexDecSwitch) {
			display.asyncExec(this);
		} else if (se.widget == exportBtn) {
			new Thread(this::exportValues).start();
		}
	}

	private void onFilterTextModify(ModifyEvent modifyEvent) {
		updateContent();
		display.asyncExec(this);
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

	public final void controlMoved(final ControlEvent controlEvent) {
		if (parentShell == null || parentShell.isDisposed()) return;
		if (Math.abs(this.parentShell.getLocation().x - shell.getSize().x - this.shell.getLocation().x) < 10 && Math.abs(this.parentShell.getLocation().y - this.shell.getLocation().y) < 20) {
			shell.setLocation(this.parentShell.getLocation().x - this.shell.getSize().x, this.parentShell.getLocation().y);
			attachedToParent = true;
		} else {
			attachedToParent = false;
		}
	}

	public final void controlResized(final ControlEvent controlEvent) {
	}

	public Shell getShell() {
		return shell;
	}

	public final boolean isAttachedToParent() {
		return this.attachedToParent;
	}
}
