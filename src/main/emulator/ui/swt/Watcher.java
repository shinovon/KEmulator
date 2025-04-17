package emulator.ui.swt;

import emulator.Emulator;
import emulator.debug.ClassTypes;
import emulator.debug.Instance;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
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
	private Shell parentShell;
	private Shell shell;
	private Combo classCombo;
	private Text filterInput;
	private Button filterSwitch;
	private Button hexDecSwitch;
	private Display display;
	private boolean visible;
	public final Map<String, Instance> selectableClasses = new HashMap<>();
	public final WatcherType type;
	private Tree tree;
	private TreeEditor aTreeEditor555;
	public static Vector<Watcher> activeWatchers = new Vector();
	private boolean disposed;
	boolean aBoolean545;
	boolean aBoolean559;
	private float propCol1;
	private float propCol2;
	private float propCol3;
	private TreeColumn treeColumn;
	private TreeColumn treeColumn2;
	private TreeColumn treeColumn3;
	public boolean isBeingResized = false;
	public boolean collumnIsDragged = false;
	private final int defWindowWidth = 640;
	private final int defWindowHeight = 480;
	private final int minWindowWidth = 200;
	private final int minWindowHeight = 210;

	private Watcher(final WatcherType type) {
		super();
		this.display = EmulatorImpl.getDisplay();
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

	public final void fillClassList() {
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
		c.updateFields(this.filterSwitch.getSelection() ? this.filterInput.getText() : null);

		switch (type) {
			case Static: {
				String s = c.getCls().getName();
				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher") + " (static " + s + ')');
				break;
			}
			case Profiler:
				// it's already localized
				shell.setText(classCombo.getText());
				break;
			case Instance: {
				Object o = c.getInstance();
				String s = o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher") + " (" + s + ')');
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

	public final void treeExpanded(final TreeEvent treeEvent) {
		TreeItem item = (TreeItem) treeEvent.item;
		if (item.getExpanded()) {
			return;
		}
		final Instance c = getWatched();
		if (item.getParentItem() == null) {
			this.method301(c, (Field) c.getFields().get(item.getParent().indexOf(item)), item);
			EmulatorImpl.asyncExec(this);
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
		EmulatorImpl.asyncExec(this);
	}

	public void treeCollapsed(TreeEvent var1) {
	}

	public final void method307(final TreeItem[] array) {
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
			new Watcher(o).open(this.parentShell);
		}
	}

	private void method320(final TreeItem[] array) {
		if (array == null || array.length == 0) {
			return;
		}
		final TreeItem treeItem = array[0];
		final Control control;
		((Text) (control = new Text(this.tree, 0))).setText(treeItem.getText(1));
		((Text) control).selectAll();
		control.setFocus();
		control.addFocusListener(new Class18(this, treeItem, (Text) control));
		control.addKeyListener(new Class20(this, treeItem, (Text) control));
		this.aTreeEditor555.setEditor(control, treeItem, 1);
	}

	private void method310(final TreeItem treeItem, final String s) {
		final Instance c = getWatched();
		this.aBoolean545 = true;
		if (treeItem.getParentItem() == null) {
			ClassTypes.setFieldValue(c.getInstance(), (Field) c.getFields().get(treeItem.getParent().indexOf(treeItem)), s);
		} else {
			TreeItem parentItem = treeItem;
			final Stack stack = new Stack<TreeItem>();
			while (parentItem.getParentItem() != null) {
				stack.push(parentItem);
				parentItem = parentItem.getParentItem();
			}
			int n = parentItem.getParent().indexOf(parentItem);
			Object o = ClassTypes.getFieldValue(c.getInstance(), (Field) c.getFields().get(n));
			Object o2 = null;
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
			if (o2 != null) {
				ClassTypes.method873(o2, n, s);
			}
		}
		this.aBoolean545 = false;
	}

	public final void open(final Shell parent) {
		this.createWidgets();
		this.fillClassCombo();
		this.updateContent();
		if (!parent.getMaximized()) {
			this.shell.setSize(parent.getSize());
		}
		int x = parent.getLocation().x + (parent.getSize().x - this.shell.getSize().x) / 2;
		int y = parent.getLocation().y + (parent.getSize().y - this.shell.getSize().y) / 2;
		shell.setLocation(x, y);
		this.parentShell = parent;
		this.shell.open();
		this.shell.addDisposeListener(this);
		this.disposed = false;
		this.visible = true;
		EmulatorImpl.asyncExec(this);
		Watcher.activeWatchers.addElement(this);
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		this.visible = false;
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

	public final void run() {
		if (this.selectableClasses.size() == 0 || !this.visible || this.aBoolean559 || this.disposed) {
			return;
		}
		this.aBoolean559 = true;
		final Instance c = getWatched();
		int n = 0;
		try {
			for (int i = 0; i < c.getFields().size(); ++i) {
				final Field field = (Field) c.getFields().get(i);
				if (this.disposed) {
					this.aBoolean559 = false;
					return;
				}
				final String s = (!Modifier.isStatic(field.getModifiers()) && c.getInstance() == null) ? "" : ClassTypes.method874(c.getInstance(), field, this.hexDecSwitch.getSelection());
				if (this.aBoolean545) {
					this.aBoolean559 = false;
					return;
				}
				final TreeItem item;
				(item = this.tree.getItem(n++)).setText(1, s);
				if (this.aBoolean545) {
					this.aBoolean559 = false;
					return;
				}
				this.method318(ClassTypes.getFieldValue(c.getInstance(), field), item);
			}
		} catch (Exception ignored) {
		}
		this.aBoolean559 = false;
	}

	private void method318(final Object o, final TreeItem treeItem) {
		if (o == null) {
			return;
		}
		if (treeItem.getExpanded()) {
			for (int i = treeItem.getItemCount() - 1; i >= 0; --i) {
				final String method872 = ClassTypes.method872(o, i, this.hexDecSwitch.getSelection());
				final Object value = Array.get(o, i);
				final TreeItem item;
				(item = treeItem.getItem(i)).setText(1, method872);
				this.method318(value, item);
			}
		}
	}

	private void updateProportionsFromColumnWidths() {
		collumnIsDragged = true;
		int minColWidth = (this.minWindowWidth - 10) / 3;
		int colW = Math.max(minColWidth, this.treeColumn.getWidth());
		int colW2 = Math.max(minColWidth, this.treeColumn2.getWidth());

		if (this.treeColumn3 != null) {
			int colW3 = Math.max(minColWidth, this.treeColumn3.getWidth());
			float totalColW = colW + colW2 + colW3;

			this.propCol1 = colW / totalColW;
			this.propCol2 = colW2 / totalColW;
			this.propCol3 = colW3 / totalColW;
		} else {
			float totalColW = colW + colW2;

			this.propCol1 = colW / totalColW;
			this.propCol2 = colW2 / totalColW;
		}

		Display.getDefault().timerExec(50, new Runnable() {
			@Override
			public void run() {
				collumnIsDragged = false;
			}
		});
	}


	private void createWidgets() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.horizontalSpan = 6;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 6;
		(this.shell = new Shell()).setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Watches"));
		this.shell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setLayout(layout);
		new CLabel(this.shell, 0).setText("Classes:");
		this.method325();
		this.shell.setSize(this.defWindowWidth, this.defWindowHeight);
		this.shell.setMinimumSize(this.minWindowWidth, this.minWindowHeight);

		if (this.type == WatcherType.Instance) {
			Button exportBtn = new Button(this.shell, SWT.PUSH);
			exportBtn.setText("Export");
			exportBtn.setToolTipText("Watcher content will be saved to data folder");
			exportBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					new Thread(this::exportValues).start();
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
			});
		}

		(this.tree = new Tree(this.shell, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL)).setHeaderVisible(true);
		this.tree.setLinesVisible(true);
		this.tree.setLayoutData(layoutData);
		this.tree.setToolTipText("Right click to open a Object Watcher");
		this.tree.addTreeListener(this);
		this.tree.addMouseListener(new Class12(this));

		int colWidth = (int) Math.round((this.shell.getSize().x - 10) / 3);
		this.treeColumn = new TreeColumn(this.tree, SWT.LEFT);
		treeColumn.setText("Variable");
		treeColumn.setMoveable(false);
		treeColumn.setWidth(colWidth);

		this.treeColumn2 = new TreeColumn(this.tree, SWT.LEFT);
		treeColumn2.setText("Value");
		treeColumn2.setMoveable(false);
		treeColumn2.setWidth(colWidth);

		ControlAdapter onTreeResized = new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!isBeingResized) {
					updateProportionsFromColumnWidths();
				}
			}
		};

		treeColumn.addControlListener(onTreeResized);
		treeColumn2.addControlListener(onTreeResized);

		if (this.type != WatcherType.Profiler) {
			this.treeColumn3 = new TreeColumn(this.tree, SWT.LEFT);
			this.treeColumn3.setText("Type");
			this.treeColumn3.setMoveable(false);
			this.treeColumn3.setWidth(colWidth);
			treeColumn3.addControlListener(onTreeResized);
			this.propCol1 = 0.33f;
			this.propCol2 = 0.33f;
			this.propCol3 = 0.34f;
		} else {
			this.treeColumn3 = null;
			this.propCol1 = 0.5f;
			this.propCol2 = 0.5f;
		}
		this.tree.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!collumnIsDragged) {
					isBeingResized = true;
					Rectangle area = tree.getClientArea();
					int totalWidth = shell.getSize().x - 10;
					if (tree.getVerticalBar() != null && tree.getVerticalBar().isVisible()) {
						totalWidth -= tree.getVerticalBar().getSize().x;
					}
					tree.setRedraw(false);
					if (treeColumn3 != null) {
						int minWidth = 60;
						int width1 = Math.max(minWidth, Math.round(propCol1 * totalWidth));
						int width2 = Math.max(minWidth, Math.round(propCol2 * totalWidth));
						int width3 = Math.max(minWidth, totalWidth - width1 - width2);

						treeColumn.setWidth(width1);
						treeColumn2.setWidth(width2);
						treeColumn3.setWidth(width3);
					} else {
						int minWidth = 60;
						int width1 = Math.max(minWidth, Math.round(propCol1 * totalWidth));
						int width2 = Math.max(minWidth, totalWidth - width1);
						treeColumn.setWidth(width1);
						treeColumn2.setWidth(width2);
					}
					tree.setRedraw(true);
					Display.getDefault().timerExec(50, new Runnable() {
						@Override
						public void run() {
							isBeingResized = false;
						}
					});

				}
			}
		});

		this.aTreeEditor555 = new TreeEditor(this.tree);
		this.aTreeEditor555.horizontalAlignment = SWT.LEFT;
		this.aTreeEditor555.grabHorizontal = true;

		filterSwitch = new Button(this.shell, 32);
		filterSwitch.setText("Filter:");
		filterSwitch.addSelectionListener(this);
		(this.filterInput = new Text(this.shell, 2048)).setLayoutData(layoutData2);
		this.filterInput.addModifyListener(this::onFilterTextModify);
		hexDecSwitch = new Button(this.shell, 32);
		hexDecSwitch.setText("HEX");
		hexDecSwitch.addSelectionListener(this);
	}

	public final void widgetSelected(final SelectionEvent se) {
		if (se.widget == hexDecSwitch) {
			EmulatorImpl.asyncExec(this);
		} else if (se.widget == filterSwitch) {
			updateContent();
			EmulatorImpl.asyncExec(this);
		}
	}

	private void onFilterTextModify(ModifyEvent modifyEvent) {
		if (filterSwitch.getSelection()) {
			updateContent();
			EmulatorImpl.asyncExec(this);
		}
	}

	private void method325() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.classCombo = new Combo(this.shell, SWT.READ_ONLY)).setLayoutData(layoutData);
		classCombo.setVisibleItemCount(24);
		this.classCombo.addModifyListener((ModifyEvent me) -> {
			aBoolean545 = true;
			updateContent();
			aBoolean545 = false;
			run();
		});
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}

	static void method315(final Watcher class5, final TreeItem treeItem, final String s) {
		class5.method310(treeItem, s);
	}

	static Tree method309(final Watcher class5) {
		return class5.tree;
	}

	static void method319(final Watcher class5, final TreeItem[] array) {
		class5.method320(array);
	}
}
