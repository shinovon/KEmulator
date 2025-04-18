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

public final class Watcher implements Runnable, DisposeListener {
	private Shell parentShell;
	private Shell shell;
	private Combo aCombo546;
	private Text filterInput;
	private CLabel aCLabel547;
	private Button filterSwitch;
	private Button hexDecSwitch;
	private Display aDisplay550;
	private boolean visible;
	private Map table;
	private int type;
	private Tree aTree554;
	private TreeEditor aTreeEditor555;
	private Watcher aClass5_556;
	private String aString551;
	public static Vector aVector548;
	public static Watcher profiler;
	private boolean aBoolean561;
	boolean aBoolean545;
	boolean aBoolean559;
	private float propCol1;
	private float propCol2;
	private float propCol3;
	private TreeColumn treeColumn;
	private TreeColumn treeColumn2;
	private TreeColumn treeColumn3;
	public boolean isBeingResized;
	public boolean collumnIsDragged;
	private int defWindowWidth;
	private int defWindowHeight;
	private int minWindowWidth;
	private int minWindowHeight;

	public Watcher(final int anInt553) {
		super();
		this.shell = null;
		this.aCombo546 = null;
		this.filterInput = null;
		this.aCLabel547 = null;
		this.filterSwitch = null;
		this.hexDecSwitch = null;
		this.aTree554 = null;
		this.aTreeEditor555 = null;
		this.aDisplay550 = EmulatorImpl.getDisplay();
		this.type = anInt553;
		this.table = new LinkedHashMap();
		this.aClass5_556 = this;
		this.commonInit();
	}

	public Watcher(final Object o) {
		super();
		this.shell = null;
		this.aCombo546 = null;
		this.filterInput = null;
		this.aCLabel547 = null;
		this.filterSwitch = null;
		this.hexDecSwitch = null;
		this.aTree554 = null;
		this.aTreeEditor555 = null;
		if (o == null) {
			return;
		}
		this.aDisplay550 = EmulatorImpl.getDisplay();
		this.type = 0;
		this.table = new Hashtable();
		final Instance c;
		(c = new Instance(o.getClass().getName(), o)).method879(null);
		this.table.put(o.toString(), c);
		this.aClass5_556 = this;
		this.aClass5_556 = this;
		this.commonInit();

	}

	private final void commonInit(){
		this.isBeingResized = false;
		this.collumnIsDragged = false;
		this.defWindowWidth = 640;
		this.defWindowHeight = 480;
		this.minWindowWidth = 200;
		this.minWindowHeight = 210;
	}

	public final void fill() {
		new Thread(new Class14(this)).start();
	}

	private void method322() {
		this.aString551 = this.aCombo546.getText();
		final Instance c = (Instance) this.table.get(this.aString551);
		if (c == null) {
			this.aTree554.removeAll();
			return;
		}
		c.method879(this.filterSwitch.getSelection() ? this.filterInput.getText() : null);
		updateTitle(c);
		this.aTree554.removeAll();
		for (int i = 0; i < c.getFields().size(); ++i) {
			final Object value;
			if ((value = c.getFields().get(i)) instanceof Field) {
				final Field field = (Field) c.getFields().get(i);
				final TreeItem treeItem;
				(treeItem = new TreeItem(this.aTree554, 0)).setText(0, field.getName());
				if (this.type == 0) {
					this.aTree554.getItem(i).setText(2, ClassTypes.method869(field.getType()));
					if (field.getType().isArray()) {
						new TreeItem(treeItem, 0).setText(0, "");
					}
				}
			} else {
				new TreeItem(this.aTree554, 0).setText(0, value.getClass().getName());
				if (this.type == 0) {
					this.aTree554.getItem(i).setText(2, value.getClass().getName());
				}
			}
		}
	}

	private void updateTitle(Instance c) {
		if (type == 0 && c != null) {
			Object o = c.getInstance();
			if (o != null) {
				String s = o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher") + " (" + s + ')');
			} else {
				String s = c.getCls().getName();
				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher") + " (static " + s + ')');
			}
		}
	}

	private void method301(final Instance c, final Field field, final TreeItem treeItem) {
		final String method869 = ClassTypes.method869(field.getType());
		if (field.getType().isArray()) {
			final Object method870 = ClassTypes.getFieldValue(c.getInstance(), field);
			method305(method870, method303(method870, method869.substring(0, method869.length() - 2)), treeItem);
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

	private void method306(final TreeItem treeItem) {
		final Instance c = (Instance) this.table.get(this.aString551);
		if (treeItem.getParentItem() == null) {
			this.method301(c, (Field) c.getFields().get(treeItem.getParent().indexOf(treeItem)), treeItem);
			return;
		}
		TreeItem parentItem = treeItem;
		final Stack stack = new Stack<TreeItem>();
		while (parentItem.getParentItem() != null) {
			stack.push(parentItem);
			parentItem = parentItem.getParentItem();
		}
		final Field field = (Field) c.getFields().get(parentItem.getParent().indexOf(parentItem));
		Object o = ClassTypes.getFieldValue(c.getInstance(), field);
		final String method869 = ClassTypes.method869(field.getType());
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
		method305(o, s2, treeItem);
	}

	public final void method307(final TreeItem[] array) {
		if (array == null || array.length == 0) {
			return;
		}
		final Instance c = (Instance) this.table.get(this.aString551);
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
		((Text) (control = new Text(this.aTree554, 0))).setText(treeItem.getText(1));
		((Text) control).selectAll();
		control.setFocus();
		control.addFocusListener(new Class18(this, treeItem, (Text) control));
		control.addKeyListener(new Class20(this, treeItem, (Text) control));
		this.aTreeEditor555.setEditor(control, treeItem, 1);
	}

	private void method310(final TreeItem treeItem, final String s) {
		final Instance c = (Instance) this.table.get(this.aString551);
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
		this.method324();
		this.method323();
		int x, y;
		switch (this.type) {
			case 0: {
//				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_TITLE", "Class Watcher"));
				if (parent != null && !parent.isDisposed()) {
					x = parent.getLocation().x + (parent.getSize().x - this.shell.getSize().x) / 2;
					y = parent.getLocation().y + (parent.getSize().y - this.shell.getSize().y) / 2;
					shell.setLocation(x, y);
					break;
				}
				x = this.aDisplay550.getClientArea().width - this.shell.getSize().x >> 1;
				y = this.aDisplay550.getClientArea().height - this.shell.getSize().y >> 1;
				shell.setLocation(x, y);
				break;
			}
			case 1: {
				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_PROFILER", "Profiler Monitor"));
				if(!parent.getMaximized()) {
					this.shell.setSize(parent.getSize());
					x = parent.getLocation().x - this.shell.getSize().x;
					y = parent.getLocation().y;
					shell.setLocation(x, y);
				}
				break;
			}
			case 2: {
				this.shell.setText(emulator.UILocale.get("WATCHES_FRAME_PROFILER_3D", "3D Profiler Monitor"));

				if(!parent.getMaximized()) {
					this.shell.setSize(parent.getSize());
					x = parent.getLocation().x - this.shell.getSize().x;
					y = parent.getLocation().y;
					shell.setLocation(x, y);
				}
				break;
			}
		}
		if (type == 1) {
			profiler = this;
		}
		this.parentShell = parent;
		this.shell.open();
		this.shell.addDisposeListener(this);
		this.aBoolean561 = false;
		this.visible = true;
		EmulatorImpl.asyncExec(this.aClass5_556);
		if (type == 0) Watcher.aVector548.addElement(this);
		while (!this.shell.isDisposed()) {
			if (!this.aDisplay550.readAndDispatch()) {
				this.aDisplay550.sleep();
			}
		}
		this.visible = false;
		return;

	}

	public final void dispose() {
		this.aBoolean561 = true;
		Watcher.aVector548.removeElement(this);
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	public final boolean method313() {
		return this.visible;
	}

	private void method323() {
		final List list = Arrays.asList(table.keySet().toArray());
		Collections.sort((List<Comparable>) list);
		final Enumeration enumeration = Collections.enumeration(list);
		while (enumeration.hasMoreElements()) {
			this.aCombo546.add(enumeration.nextElement().toString());
		}
		if (this.table.size() > 0) {
			aString551 = type == 1 ? "SystemProfiler" : aCombo546.getItem(0);
		} else {
			aString551 = "";
		}
		this.aCombo546.setText(this.aString551);
		this.method322();
	}

	public final void run() {
		if (this.table.size() == 0 || !this.visible || this.aBoolean559 || this.aBoolean561) {
			return;
		}
		this.aBoolean559 = true;
		final Instance c = (Instance) this.table.get(this.aString551);
		int n = 0;
		try {
			for (int i = 0; i < c.getFields().size(); ++i) {
				final Field field = (Field) c.getFields().get(i);
				if (this.aBoolean561) {
					this.aBoolean559 = false;
					return;
				}
				final String s = (!Modifier.isStatic(field.getModifiers()) && c.getInstance() == null) ? "" : ClassTypes.method874(c.getInstance(), field, this.hexDecSwitch.getSelection());
				if (this.aBoolean545) {
					this.aBoolean559 = false;
					return;
				}
				final TreeItem item;
				(item = this.aTree554.getItem(n++)).setText(1, s);
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
		int minColWidth = (this.minWindowWidth - 10)/3;
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


	private void method324() {
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
		(this.aCLabel547 = new CLabel(this.shell, 0)).setText("Classes:");
		this.method325();
		this.shell.setSize(this.defWindowWidth, this.defWindowHeight);
		this.shell.setMinimumSize(this.minWindowWidth, this.minWindowHeight);

		if (this.type == 0) {
			Button exportBtn = new Button(this.shell, SWT.PUSH);
			exportBtn.setText("Export");
			exportBtn.setToolTipText("Watcher content will be saved to data folder");
			exportBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					new Thread(() -> {
						try {
							File file = new File(Emulator.getUserPath() + "/classwatcher.txt");
							if (!file.exists()) file.createNewFile();
							PrintStream ps = new PrintStream(new FileOutputStream(file));
							try {
								final List list = Arrays.asList(table.keySet().toArray());
								Collections.sort((List<Comparable>) list);
								for (Object o : list) {
									ps.println(o);
									final Instance c = (Instance) table.get(o);
									c.method879(null);
									Vector fields = c.getFields();
									for (int i = 0; i < fields.size(); ++i) {
										final Object f = fields.get(i);
										if (f instanceof Field) {
											final Field field = (Field) c.getFields().get(i);
											Class type = field.getType();
											ps.print(" " + field.getDeclaringClass().getName() + "> " + ClassTypes.method869(type) + " " + field.getName());
											try {
												Object v = field.get(c.getInstance());
												if (type.isArray()) {
													int l = Array.getLength(v);
													ps.println(" = " + ClassTypes.method869(v.getClass()).replaceFirst("\\[\\]", "[" + l + "]"));
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
					}).start();
				}
			});
		}

		(this.aTree554 = new Tree(this.shell, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL)).setHeaderVisible(true);
		this.aTree554.setLinesVisible(true);
		this.aTree554.setLayoutData(layoutData);
		this.aTree554.setToolTipText("Right click to open a Object Watcher");
		this.aTree554.addTreeListener(new Class6(this));
		this.aTree554.addMouseListener(new Class12(this));

		int colWidth = (int)Math.round((this.shell.getSize().x -10)/3);
		this.treeColumn = new TreeColumn(this.aTree554, SWT.LEFT);
		treeColumn.setText("Variable");
		treeColumn.setMoveable(false);
		treeColumn.setWidth(colWidth);

		this.treeColumn2 = new TreeColumn(this.aTree554, SWT.LEFT);
		treeColumn2.setText("Value");
		treeColumn2.setMoveable(false);
		treeColumn2.setWidth(colWidth);


		if (this.type == 0) {
			this.treeColumn3 = new TreeColumn(this.aTree554, SWT.LEFT);
			this.treeColumn3.setText("Type");
			this.treeColumn3.setMoveable(false);
			this.treeColumn3.setWidth(colWidth);
			this.propCol1 = 0.33f;
			this.propCol2 = 0.33f;
			this.propCol3 = 0.34f;
		} else {
			this.treeColumn3 = null;
			this.propCol1 = 0.5f;
			this.propCol2 = 0.5f;
		}
		treeColumn.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!isBeingResized) {
					updateProportionsFromColumnWidths();
				}
			}
		});

		treeColumn2.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!isBeingResized) {
					updateProportionsFromColumnWidths();
                }
			}
		});

		if (treeColumn3 != null) {
			treeColumn3.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					if (!isBeingResized) {
						updateProportionsFromColumnWidths();
					}
				}
			});
		}
		this.aTree554.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!collumnIsDragged) {
					isBeingResized = true;
					Rectangle area = aTree554.getClientArea();
					int totalWidth = shell.getSize().x - 10;
					if (aTree554.getVerticalBar() != null && aTree554.getVerticalBar().isVisible()) {
						totalWidth -= aTree554.getVerticalBar().getSize().x;
					}
					aTree554.setRedraw(false);
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
					aTree554.setRedraw(true);
					Display.getDefault().timerExec(50, new Runnable() {
						@Override
						public void run() {
							isBeingResized = false;
						}
					});

				}
			}
		});

		this.aTreeEditor555 = new TreeEditor(this.aTree554);
		this.aTreeEditor555.horizontalAlignment = SWT.LEFT;
		this.aTreeEditor555.grabHorizontal = true;

		(this.filterSwitch = new Button(this.shell, 32)).setText("Filter:");
		this.filterSwitch.addSelectionListener(new Class139(this));
		(this.filterInput = new Text(this.shell, 2048)).setLayoutData(layoutData2);
		this.filterInput.addModifyListener(new Class141(this));
		(this.hexDecSwitch = new Button(this.shell, 32)).setText("HEX");
		this.hexDecSwitch.addSelectionListener(new Class8(this));
	}

	private void method325() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.aCombo546 = new Combo(this.shell, SWT.READ_ONLY)).setLayoutData(layoutData);
		aCombo546.setVisibleItemCount(24);
		this.aCombo546.addModifyListener(new Class16(this));
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}

	static int staticGetType(final Watcher class5) {
		return class5.type;
	}

	static Map staticGetTable(final Watcher class5) {
		return class5.table;
	}

	static void method315(final Watcher class5, final TreeItem treeItem, final String s) {
		class5.method310(treeItem, s);
	}

	static void method317(final Watcher class5) {
		class5.method322();
	}

	static Watcher method308(final Watcher class5) {
		return class5.aClass5_556;
	}

	static Button method312(final Watcher class5) {
		return class5.filterSwitch;
	}

	static void method316(final Watcher class5, final TreeItem treeItem) {
		class5.method306(treeItem);
	}

	static Tree method309(final Watcher class5) {
		return class5.aTree554;
	}

	static void method319(final Watcher class5, final TreeItem[] array) {
		class5.method320(array);
	}

	static {
		Watcher.aVector548 = new Vector();
	}
}
