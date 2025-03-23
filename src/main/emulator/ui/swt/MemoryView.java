package emulator.ui.swt;

import emulator.Settings;
import emulator.UILocale;
import emulator.debug.Memory;
import emulator.debug.MemoryViewImage;
import emulator.graphics2D.awt.ImageAWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.lcdui.Image;
import java.util.*;

public final class MemoryView implements DisposeListener {
	private Shell shell;
	private Button aButton1081;
	private Button aButton1104;
	private CLabel aCLabel1082;
	private Text aText1087;
	private CLabel aCLabel1105;
	private CLabel aCLabel1114;
	private CLabel bytecodeSizeLbl;
	private CLabel objectsSizeLbl;
	private CLabel aCLabel1128;
	private CLabel totalmemLbl;
	private CLabel aCLabel1136;
	private CLabel aCLabel1140;
	private Display display;
	private Memory memoryMgr;
	private boolean visible;
	private TabFolder bottomTabs;
	private Composite aComposite1098;
	private Composite audioControlComp;
	private CLabel aCLabel1143;
	private Combo imageScaleCombo;
	private CLabel aCLabel1146;
	private Combo aCombo1107;
	private Button aButton1115;
	private Button aButton1121;
	private Button aButton1125;
	private Button aButton1129;
	private Button aButton1133;
	private Button aButton1137;
	private Button aButton1141;
	private Canvas aCanvas1095;
	private SashForm horizontalSeparator;
	private Composite aComposite1116;
	private SashForm aSashForm1103;
	private Table table;
	private Table classTable;
	private CLabel aCLabel1149;
	private CLabel aCLabel1152;
	private double imageScaling;
	private int anInt1109;
	private boolean aBoolean1111;
	private Menu aMenu1084;
	private Menu aMenu1113;
	private int anInt1117;
	private int bytecodeSize;
	private int objectsSize;
	private int anInt1130;
	private static Object anObject1086;
	private static Vector aVector1099;
	private static ArrayList anArrayList1090;
	private int anInt1134;
	private boolean aBoolean1118;
	private int anInt1138;
	private int anInt1142;
	private int anInt1144;
	private int anInt1147;
	private boolean aBoolean1123;
	private boolean aBoolean1127;
	private boolean aBoolean1131;
	private boolean aBoolean1135;
	private boolean imgSelected;
	private int anInt1150;
	private Hashtable aHashtable1091;
	private Image anImage1088;
	private ArrayList anArrayList1110;
	private Vector classRefsVector;
	private AutoUpdate aClass88_1100;
	private Thread autoUpdateThread;
	private Button startAudioBtn;
	private Button stopAudioBtn;
	private CLabel aCLabel1153;
	private Scale aScale1089;
	private CLabel aCLabel1154;
	private Table soundsTable;
	private CLabel aCLabel1155;
	private ProgressBar aProgressBar1092;
	private Button pauseAudioBtn;
	private CLabel jvmmem1Label;
	private CLabel jvmmemLabel;
	private int sortColumn = -1;
	private Button exportAudioBtn;
	private int SOUNDS_TAB_ID;
	private int IMAGES_TAB_ID;
	private int OBJECTS_TAB_ID;

	public MemoryView() {
		super();
		this.shell = null;
		this.aButton1081 = null;
		this.aButton1104 = null;
		this.aCLabel1082 = null;
		this.aText1087 = null;
		this.aCLabel1105 = null;
		this.aCLabel1114 = null;
		this.bytecodeSizeLbl = null;
		this.objectsSizeLbl = null;
		this.aCLabel1128 = null;
		this.totalmemLbl = null;
		this.aCLabel1136 = null;
		this.aCLabel1140 = null;
		this.bottomTabs = null;
		this.aComposite1098 = null;
		this.audioControlComp = null;
		this.aCLabel1143 = null;
		this.imageScaleCombo = null;
		this.aCLabel1146 = null;
		this.aCombo1107 = null;
		this.aButton1115 = null;
		this.aButton1121 = null;
		this.aButton1125 = null;
		this.aButton1129 = null;
		this.aButton1133 = null;
		this.aButton1137 = null;
		this.aButton1141 = null;
		this.aCanvas1095 = null;
		this.horizontalSeparator = null;
		this.aComposite1116 = null;
		this.aSashForm1103 = null;
		this.table = null;
		this.classTable = null;
		this.aCLabel1149 = null;
		this.aCLabel1152 = null;
		this.aBoolean1123 = true;
		this.aBoolean1127 = true;
		this.aHashtable1091 = new Hashtable();
		this.startAudioBtn = null;
		this.stopAudioBtn = null;
		this.aCLabel1153 = null;
		this.aScale1089 = null;
		this.aCLabel1154 = null;
		this.soundsTable = null;
		this.aCLabel1155 = null;
		this.aProgressBar1092 = null;
		this.pauseAudioBtn = null;
		this.display = EmulatorImpl.getDisplay();
		this.memoryMgr = new Memory();
		this.classRefsVector = new Vector();
		this.anArrayList1110 = new ArrayList();
	}

	public final void open() {
		this.method665();
		this.shell.setLocation(
                (int) (this.display.getClientArea().height * 0.025), //- this.shell.getSize().x >> 3,
                (int) (this.display.getClientArea().height * 0.025)// - this.shell.getSize().y >> 2
		);
		this.shell.open();
		this.shell.addDisposeListener(this);
		this.method692();
		this.visible = true;
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		Settings.showMemViewFrame = false;
		if (this.autoUpdateThread != null && this.autoUpdateThread.isAlive()) {
			this.autoUpdateThread.interrupt();
		}
		this.memoryMgr.aVector1463.clear();
		this.visible = false;
	}

	public final void dispose() {
		if (this.shell != null && !this.shell.isDisposed()) {
			this.shell.dispose();
		}
		this.visible = false;
	}

	public final boolean isShown() {
		return this.visible;
	}

	private void method665() {
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 4;
		gridData.verticalAlignment = 2;
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 4;
		layoutData3.verticalAlignment = 2;
		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalSpan = 2;
		layoutData4.verticalAlignment = 2;
		layoutData4.grabExcessHorizontalSpace = false;
		layoutData4.horizontalAlignment = 4;
		final GridData gridData2;
		(gridData2 = new GridData()).horizontalAlignment = 4;
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.verticalAlignment = 2;
		final GridData gridData3;
		(gridData3 = new GridData()).horizontalAlignment = 4;
		gridData3.grabExcessHorizontalSpace = false;
		gridData3.verticalAlignment = 2;
		final GridData gridData4;
		(gridData4 = new GridData()).horizontalAlignment = 4;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.horizontalSpan = 3;
		gridData4.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 4;
		layoutData5.grabExcessHorizontalSpace = false;
		layoutData5.horizontalIndent = 0;
		layoutData5.verticalAlignment = 2;
		final GridData layoutData6;
		(layoutData6 = new GridData()).verticalAlignment = 2;
		layoutData6.verticalSpan = 1;
		layoutData6.grabExcessHorizontalSpace = false;
		layoutData6.horizontalAlignment = 4;
		final GridData gridData7;
		(gridData7 = new GridData()).horizontalAlignment = 4;
		gridData7.grabExcessHorizontalSpace = false;
		gridData7.verticalAlignment = 2;
		final GridData gridData8;
		(gridData8 = new GridData()).horizontalAlignment = 4;
		gridData8.grabExcessHorizontalSpace = false;
		gridData8.verticalAlignment = 2;
		final GridData gridData9;
		(gridData9 = new GridData()).verticalAlignment = 2;
		gridData9.grabExcessHorizontalSpace = false;
		gridData9.horizontalAlignment = 4;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 7;
		(this.shell = new Shell(1264)).setText(UILocale.get("MEMORY_VIEW_TITLE", "MemoryView"));
		this.shell.setImage(new org.eclipse.swt.graphics.Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		this.shell.setLayout(layout);

		//(int)(this.display.getClientArea().width * 0.33);
		//(int)(this.display.getClientArea().height * 0.8);
		this.shell.setSize(
				(int)(this.display.getClientArea().width * 0.4),
				(int)(this.display.getClientArea().height * 0.95)
		);

		(this.aButton1081 = new Button(this.shell, 8388608)).setText(" " + UILocale.get("MEMORY_VIEW_UPDATE", "Update") + " ");
		(this.aButton1081).setLayoutData(layoutData6);
		this.aButton1081.addSelectionListener(new Class129(this));
		(this.aButton1104 = new Button(this.shell, 32)).setText(UILocale.get("MEMORY_VIEW_AUTO_UPDATE", "AutoUpdate"));
		(this.aButton1104).setLayoutData(layoutData4);
		this.aButton1104.addSelectionListener(new Class70(this));
		(this.aCLabel1105 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_BYTECODE_SIZE", "ByteCode Size:"));
		(this.aCLabel1105).setLayoutData(layoutData);
		(this.bytecodeSizeLbl = new CLabel(this.shell, 0)).setText("0              bytes");
		(this.bytecodeSizeLbl).setLayoutData(gridData3);
		(this.aCLabel1128 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_TOTALMEM_SIZE", "Total Memory Used:"));
		(this.aCLabel1128).setLayoutData(gridData);
		(this.totalmemLbl = new CLabel(this.shell, 0)).setText("0");
		(this.totalmemLbl).setLayoutData(gridData2);


		Button gcButton = new Button(shell, SWT.PUSH);
		gcButton.setText("GC");
		gcButton.setLayoutData(gridData9);
		gcButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				System.gc();
			}
		});
		(this.aCLabel1082 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_INTERVAL", "Interval(milli sec):"));
		(this.aCLabel1082).setLayoutData(layoutData3);
		(this.aText1087 = new Text(this.shell, 2048)).setText("1000");
		(this.aText1087).setLayoutData(layoutData5);
		(this.aCLabel1114 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_OBJECT_SIZE", "Objects Size:"));
		this.aCLabel1114.setLayoutData(layoutData2);
		(this.objectsSizeLbl = new CLabel(this.shell, 0)).setText("0");
		this.objectsSizeLbl.setLayoutData(gridData7);
		(this.aCLabel1136 = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_MAX_OBJECT_SIZE", "Max Objects Size:"));
		this.aCLabel1136.setLayoutData(gridData);
		(this.aCLabel1140 = new CLabel(this.shell, 0)).setText("0");
		this.aCLabel1140.setLayoutData(gridData2);
		(this.jvmmem1Label = new CLabel(this.shell, 0)).setText(UILocale.get("MEMORY_VIEW_JVM_MEMORY", "Real usage:"));
		(this.jvmmemLabel = new CLabel(this.shell, 0)).setText("0");
		this.jvmmemLabel.setLayoutData(gridData8);
		this.method687();
	}

	private void initTabs() {
		this.bottomTabs = new TabFolder(this.horizontalSeparator, 0);

		this.method677();
		this.method681();

		int tabIndex = 0;

		final TabItem tabItem;
		(tabItem = new TabItem(this.bottomTabs, 0)).setText(UILocale.get("MEMORY_VIEW_IMAGES", "Images"));
		tabItem.setControl(this.aComposite1098);
		this.IMAGES_TAB_ID = tabIndex;
		tabIndex++;

		final TabItem tabItem2;
		(tabItem2 = new TabItem(this.bottomTabs, 0)).setText(UILocale.get("MEMORY_VIEW_SOUNDS", "Sounds"));
		tabItem2.setControl(this.audioControlComp);
		this.SOUNDS_TAB_ID = tabIndex;
		tabIndex++;

		final TabItem tabItemMemory;
		(tabItemMemory = new TabItem(this.bottomTabs, 0)).setText(UILocale.get("MEMORY_VIEW_MEMORY", "Objects"));
		this.OBJECTS_TAB_ID = tabIndex;
		tabIndex++;


		Composite memoryComp = new Composite(this.bottomTabs, 0);
		GridLayout memoryLayout = new GridLayout(1, false);
		memoryComp.setLayout(memoryLayout);

		SashForm tablesSash = new SashForm(memoryComp, SWT.HORIZONTAL);
		GridData sashLayoutData = new GridData();
		sashLayoutData.horizontalAlignment = GridData.FILL;
		sashLayoutData.grabExcessHorizontalSpace = true;
		sashLayoutData.grabExcessVerticalSpace = true;
		sashLayoutData.verticalAlignment = GridData.FILL;
		tablesSash.setLayoutData(sashLayoutData);

		this.table = new Table(tablesSash, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.table.addSelectionListener(new Class34(this));

		final TableColumn tableColumn;
		(tableColumn = new TableColumn(this.table, 0)).setWidth(170);
		tableColumn.setText(UILocale.get("MEMORY_VIEW_CLASS", "Class"));
		tableColumn.addSelectionListener(new Class31(this));

		final TableColumn tableColumn2;
		(tableColumn2 = new TableColumn(this.table, 0)).setWidth(70);
		tableColumn2.setText(UILocale.get("MEMORY_VIEW_INSTANCES", "Instances"));
		tableColumn2.addSelectionListener(new Class140(this));

		final TableColumn tableColumn3;
		(tableColumn3 = new TableColumn(this.table, 0)).setWidth(100);
		tableColumn3.setText(UILocale.get("MEMORY_VIEW_TOTAL_HEAP_SIZE", "Total Heap Size"));
		tableColumn3.addSelectionListener(new Class17(this));

		this.classTable = new Table(tablesSash, 67584);
		this.classTable.setHeaderVisible(true);
		this.classTable.setLinesVisible(true);
		this.classTable.addSelectionListener(new Class19(this));
		this.classTable.addMouseListener(new Class13(this));

		final TableColumn tableColumn4;
		(tableColumn4 = new TableColumn(this.classTable, 0)).setWidth(230);
		tableColumn4.setText(UILocale.get("MEMORY_VIEW_REFERENCE", "Reference"));

		final TableColumn tableColumn5;
		(tableColumn5 = new TableColumn(this.classTable, 0)).setWidth(70);
		tableColumn5.setText(UILocale.get("MEMORY_VIEW_INSTANCE", "Instance"));

		final TableColumn tableColumn6;
		(tableColumn6 = new TableColumn(this.classTable, 0)).setWidth(100);
		tableColumn6.setText(UILocale.get("MEMORY_VIEW_SIZE", "Size"));
		tablesSash.setWeights(new int[]{1, 1});

		tabItemMemory.setControl(memoryComp);
	}

	private void method677() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.horizontalAlignment = 4;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 7;
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		(this.aComposite1098 = new Composite(this.bottomTabs, 0)).setLayout(layout);
		(this.aCLabel1143 = new CLabel(this.aComposite1098, 0)).setText(UILocale.get("MEMORY_VIEW_ZOOM", "Zoom:"));
		this.method684();
		(this.aCLabel1146 = new CLabel(this.aComposite1098, 0)).setText(UILocale.get("MEMORY_VIEW_SORT", "Sort:"));
		this.method685();
		(this.aButton1121 = new Button(this.aComposite1098, 32)).setText(UILocale.get("MEMORY_VIEW_IMAGES_DRAWN", "Images Drawn"));
		this.aButton1121.setSelection(true);
		this.aButton1121.addSelectionListener(new Class144(this));
		(this.aButton1129 = new Button(this.aComposite1098, 32)).setText(UILocale.get("MEMORY_VIEW_UNUSED_REGION", "Darken Unused Regions"));
		this.aButton1129.addSelectionListener(new Class125(this));
		(this.aButton1137 = new Button(this.aComposite1098, 8388608)).setText(UILocale.get("MEMORY_VIEW_RESET_IMAGE", "Reset Image Usage"));
		this.aButton1137.addSelectionListener(new Class123(this));
		(this.aCLabel1149 = new CLabel(this.aComposite1098, 0)).setText(UILocale.get("MEMORY_VIEW_SIZE", "Size") + ":");
		(this.aCLabel1152 = new CLabel(this.aComposite1098, 0)).setText("");
		this.aCLabel1152.setLayoutData(layoutData);
		(this.aButton1115 = new Button(this.aComposite1098, 32)).setText(UILocale.get("MEMORY_VIEW_ASCEND", "Ascending"));
		this.aButton1115.setEnabled(true);
		this.aButton1115.setSelection(true);
		this.aBoolean1111 = true;
		this.aButton1115.addSelectionListener(new Class66(this));
		(this.aButton1125 = new Button(this.aComposite1098, 32)).setText(UILocale.get("MEMORY_VIEW_IMAGE_NEVER_DRAW", "Images Never Drawn"));
		this.aButton1125.setSelection(true);
		this.aButton1125.addSelectionListener(new Class69(this));
		(this.aButton1133 = new Button(this.aComposite1098, 32)).setText(UILocale.get("MEMORY_VIEW_RELEASED_IMAGES", "Released Images"));
		this.aButton1133.setEnabled(Settings.recordReleasedImg);
		this.aButton1133.addSelectionListener(new Class68(this));
		(this.aButton1141 = new Button(this.aComposite1098, 8388608)).setText(UILocale.get("MEMORY_VIEW_CLEAR_RELEASED_IMAGES", "Clear Released Images"));
		this.aButton1141.setEnabled(Settings.recordReleasedImg);
		this.aButton1141.addSelectionListener(new Class71(this));
		this.method686();
	}

	private void method681() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 8;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = 4;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 9;
		(this.audioControlComp = new Composite(this.bottomTabs, 0)).setLayout(layout);
		(this.startAudioBtn = new Button(this.audioControlComp, 8388608)).setText(UILocale.get("MEMORY_VIEW_SOUND_START", "Start"));
		(this.pauseAudioBtn = new Button(this.audioControlComp, 8388608)).setText(UILocale.get("MEMORY_VIEW_SOUND_PAUSE", "Pause"));
		this.pauseAudioBtn.addSelectionListener(new Class28(this));
		this.startAudioBtn.addSelectionListener(new Class7(this));
		(this.stopAudioBtn = new Button(this.audioControlComp, 8388608)).setText(UILocale.get("MEMORY_VIEW_SOUND_STOP", "Stop"));
		this.stopAudioBtn.addSelectionListener(new Class27(this));
		(this.aCLabel1153 = new CLabel(this.audioControlComp, 0)).setText(UILocale.get("MEMORY_VIEW_SOUND_VOLUME", "Volume:"));
		(this.aCLabel1154 = new CLabel(this.audioControlComp, 0)).setText("0   ");
		(this.aScale1089 = new Scale(this.audioControlComp, 0)).addSelectionListener(new Class21(this));
		(this.aCLabel1155 = new CLabel(this.audioControlComp, 0)).setText(UILocale.get("MEMORY_VIEW_SOUND_PROGRESS", "Progress:"));
		(this.aProgressBar1092 = new ProgressBar(this.audioControlComp, 65536)).setSelection(0);

		(this.exportAudioBtn = new Button(this.audioControlComp, 8388608)).setText(UILocale.get("MEMORY_VIEW_SOUND_EXPORT", "Export"));
		this.exportAudioBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent selectionEvent) {
				Object value;
				if (soundsTable.getSelectionIndex() != -1 &&
						soundsTable.getSelectionIndex() < memoryMgr.players.size() &&
						(value = memoryMgr.players.get(soundsTable.getSelectionIndex())) != null) {
					Memory.playerAct(value, 3);
				}
			}
		});
		final Color foreground = new Color(Display.getCurrent(), 217, 108, 0);
		this.aProgressBar1092.setForeground(foreground);
		foreground.dispose();
		(this.soundsTable = new Table(this.audioControlComp, 67584)).setHeaderVisible(true);
		this.soundsTable.setLayoutData(layoutData);
		this.soundsTable.setLinesVisible(true);
		this.soundsTable.addSelectionListener(new Class24(this));
		final TableColumn tableColumn;
		(tableColumn = new TableColumn(this.soundsTable, 0)).setWidth(100);
		tableColumn.setText(UILocale.get("MEMORY_VIEW_INSTANCE", "Instance"));
		final TableColumn tableColumn2;
		(tableColumn2 = new TableColumn(this.soundsTable, 0)).setWidth(100);
		tableColumn2.setText(UILocale.get("MEMORY_VIEW_CONTENT_TYPE", "ContentType"));
		final TableColumn tableColumn3;
		(tableColumn3 = new TableColumn(this.soundsTable, 0)).setWidth(100);
		tableColumn3.setText(UILocale.get("MEMORY_VIEW_STATE", "State"));
		final TableColumn tableColumn4;
		(tableColumn4 = new TableColumn(this.soundsTable, 0)).setWidth(100);
		tableColumn4.setText(UILocale.get("MEMORY_VIEW_LOOP_COUNT", "LoopCount"));
		final TableColumn tableColumn5;
		(tableColumn5 = new TableColumn(this.soundsTable, 0)).setWidth(100);
		tableColumn5.setText(UILocale.get("MEMORY_VIEW_DATA_SIZE", "DataSize"));
	}

	private void method684() {
		this.imageScaleCombo = new Combo(this.aComposite1098, 8);
		this.imageScaleCombo.add("50%");
		this.imageScaleCombo.add("100%");
		this.imageScaleCombo.add("200%");
		this.imageScaleCombo.add("300%");
		this.imageScaleCombo.add("400%");
		this.imageScaleCombo.setText("100%");
		this.imageScaling = 1;
		this.imageScaleCombo.addModifyListener(new Class35(this));
	}

	private void method685() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 1;
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.verticalAlignment = 2;
		(this.aCombo1107 = new Combo(this.aComposite1098, 8)).setEnabled(true);
		this.aCombo1107.setLayoutData(layoutData);
		this.aCombo1107.add(UILocale.get("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
		this.aCombo1107.add(UILocale.get("MEMORY_VIEW_SORT_SIZE", "Size"));
		this.aCombo1107.add(UILocale.get("MEMORY_VIEW_SORT_DRAW_COUNT", "Draw Count"));
		this.aCombo1107.setText(UILocale.get("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
		this.anInt1109 = 0;
		this.aCombo1107.addModifyListener(new Class22(this));
	}

	private void method686() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.horizontalSpan = 7;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		((this.aCanvas1095 = new Canvas(this.aComposite1098, 537135616))).setLayout(null);
		this.aCanvas1095.setLayoutData(layoutData);
		this.aCanvas1095.addPaintListener(new Class26(this));
		this.aCanvas1095.addMouseListener(new Class25(this));
		this.aCanvas1095.getVerticalBar().addSelectionListener(new Class23(this));
		this.aMenu1084 = new Menu(this.shell, 8);
		final MenuItem menuItem;
		(menuItem = new MenuItem(this.aMenu1084, 8)).setText(UILocale.get("MEMORY_VIEW_SAVE_AS", "Save As..."));
		menuItem.addSelectionListener(new Class36(this));
		this.aMenu1113 = new Menu(this.shell, 8);
		final MenuItem menuItem2;
		(menuItem2 = new MenuItem(this.aMenu1113, 8)).setText(UILocale.get("MEMORY_VIEW_SAVE_ALL", "Save All Images..."));
		menuItem2.addSelectionListener(new Class33(this));
	}

	private void method687() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 7;
		layoutData.horizontalAlignment = 4;
		layoutData.verticalAlignment = 4;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;

		(this.horizontalSeparator = new SashForm(this.shell, 0)).setOrientation(SWT.VERTICAL);
		this.horizontalSeparator.setLayoutData(layoutData);
		this.initTabs();

	}
/*
	private void method688() {
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 1;
		this.aComposite1116 = new Composite(this.horizontalSeparator, 0);
		this.method689();
		this.aComposite1116.setLayout(layout);
	}

	private void method689() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		(this.aSashForm1103 = new SashForm(this.aComposite1116, 0)).setLayoutData(layoutData);
		(this.table = new Table(this.aSashForm1103, 67584)).setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.table.addSelectionListener(new Class34(this));
		final TableColumn tableColumn;
		(tableColumn = new TableColumn(this.table, 0)).setWidth(100);
		tableColumn.setText(UILocale.get("MEMORY_VIEW_CLASS", "Class"));
		tableColumn.addSelectionListener(new Class31(this));
		final TableColumn tableColumn2;
		(tableColumn2 = new TableColumn(this.table, 0)).setWidth(100);
		tableColumn2.setText(UILocale.get("MEMORY_VIEW_INSTANCES", "Instances"));
		tableColumn2.addSelectionListener(new Class140(this));
		final TableColumn tableColumn3;
		(tableColumn3 = new TableColumn(this.table, 0)).setWidth(100);
		tableColumn3.setText(UILocale.get("MEMORY_VIEW_TOTAL_HEAP_SIZE", "Total Heap Size"));
		tableColumn3.addSelectionListener(new Class17(this));
		(this.classTable = new Table(this.aSashForm1103, 67584)).setHeaderVisible(true);
		this.classTable.setLinesVisible(true);
		this.classTable.addSelectionListener(new Class19(this));
		this.classTable.addMouseListener(new Class13(this));
		final TableColumn tableColumn4;
		(tableColumn4 = new TableColumn(this.classTable, 0)).setWidth(100);
		tableColumn4.setText(UILocale.get("MEMORY_VIEW_REFERENCE", "Reference"));
		final TableColumn tableColumn5;
		(tableColumn5 = new TableColumn(this.classTable, 0)).setWidth(100);
		tableColumn5.setText(UILocale.get("MEMORY_VIEW_INSTANCE", "Instance"));
		final TableColumn tableColumn6;
		(tableColumn6 = new TableColumn(this.classTable, 0)).setWidth(100);
		tableColumn6.setText(UILocale.get("MEMORY_VIEW_SIZE", "Size"));
	}
*/
	private void method690() {
		if (this.aBoolean1118) {
			return;
		}
		this.aBoolean1118 = true;
		try {
			this.memoryMgr.method846();
			this.method694();
			this.bytecodeSize = Memory.bytecodeSize();
			this.objectsSize = this.memoryMgr.objectsSize();
			this.anInt1130 = Math.max(this.anInt1130, this.objectsSize);
		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (Error ex) {
			ex.printStackTrace();
		}
		this.aBoolean1118 = false;
	}

	private void method691() {
		if (this.shell.isDisposed()) {
			return;
		}
		try {
			if (this.bottomTabs.getSelectionIndex() == this.IMAGES_TAB_ID) {
				this.aCanvas1095.redraw();
			} else {
				this.method695();
			}
			this.method696();
		} catch (Exception ignored) {
		}
		this.bytecodeSizeLbl.setText(this.bytecodeSize + " bytes");
		this.objectsSizeLbl.setText(this.objectsSize + " bytes");
		this.totalmemLbl.setText(this.bytecodeSize + this.objectsSize + " bytes");
		this.aCLabel1140.setText(this.anInt1130 + " bytes");
		long t = Runtime.getRuntime().totalMemory();
		long f = Runtime.getRuntime().freeMemory();
		this.jvmmemLabel.setText(((t - f) / 1024L) + "/" + (t / 1024L) + " kb");
		sort();
	}

	private void method692() {
		this.method690();
		this.method691();

	}

	private void method693() {
		synchronized (MemoryView.anObject1086) {
			Collections.sort((java.util.List<Object>) MemoryView.anArrayList1090, new Class15(this));
		}
	}

	private void method694() {
		synchronized (MemoryView.anObject1086) {
			final int size = this.memoryMgr.images.size();
			final int n = this.memoryMgr.images.size() + (this.aBoolean1131 ? this.memoryMgr.aVector1463.size() : 0);
			MemoryView.aVector1099.clear();
			MemoryView.anArrayList1090.clear();
			for (int i = 0; i < n; ++i) {
				Image image;
				try {
					if (i < size) {
						image = (Image) this.memoryMgr.images.get(i);
					} else {
						image = (Image) this.memoryMgr.aVector1463.get(i - size);
					}
				} catch (Exception ex) {
					break;
				}
				MemoryView.aVector1099.add(image);
				if (i < size && (image.getUsedCount() <= 0 || !this.aBoolean1123)) {
					if (image.getUsedCount() != 0) {
						continue;
					}
					if (!this.aBoolean1127) {
						continue;
					}
				}
				MemoryView.anArrayList1090.add(image);
			}
			this.anInt1134 = size;
		}
		this.method693();
	}

	private boolean method628(final int n, final int n2) {
		final Enumeration<Rectangle> keys = this.aHashtable1091.keys();
		while (keys.hasMoreElements()) {
			final Rectangle rectangle;
			if ((rectangle = keys.nextElement()).contains(n, n2)) {
				this.anImage1088 = (Image) this.aHashtable1091.get(rectangle);
				return true;
			}
		}
		return false;
	}

	private void method636(final GC gc) {
		int n = 12 - this.anInt1147;
		int n2 = 10;
		int max = 0;
		this.anInt1144 = 0;
		final Color background = new Color(null, 151, 150, 147);
		final Color color = new Color(null, 255, 0, 0);
		final Color color2 = new Color(null, 0, 0, 0);
		final Color color3 = new Color(null, 0, 255, 0);
		final Color foreground = new Color(null, 0, 0, 255);
		gc.setBackground(background);
		gc.fillRectangle(0, 0, this.anInt1138, this.anInt1142);
		this.anInt1117 = 0;
		this.aHashtable1091.clear();
		for (int size = MemoryView.anArrayList1090.size(), i = 0; i < size; ++i) {
			Image image;
			try {
				image = (Image) MemoryView.anArrayList1090.get(i);
			} catch (Exception ex) {
				break;
			}
			final int n3 = (int) (image.getWidth() * this.imageScaling);
			final int n4 = (int) (image.getHeight() * this.imageScaling);
			if (n2 + n3 + 30 > this.anInt1138) {
				n2 = 10;
				n += max + 12;
				max = 0;
			}
			Label_0547:
			{
				if (n + n4 <= 0) {
					if (n <= this.anInt1142) {
						break Label_0547;
					}
				}
				try {
					if (Settings.g2d == 1) {
						((ImageAWT) image.getImpl()).copyToScreen(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
						if (this.aBoolean1135) {
							((ImageAWT) image.getUsedRegion()).copyToScreen(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
						}
					} else if (Settings.g2d == 0) {
						((emulator.graphics2D.swt.ImageSWT) image.getImpl()).copyToScreen(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
						if (this.aBoolean1135) {
							((emulator.graphics2D.swt.ImageSWT) image.getUsedRegion()).copyToScreen(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
						}
					}
				} catch (Exception ignored) {
				}
				if (image instanceof MemoryViewImage) {
					gc.setForeground(foreground);
					gc.drawString("Tex", n2 - 1, n + 1 - gc.getFontMetrics().getHeight(), true);
				} else {
					GC gc2;
					Color foreground2;
					if (this.imgSelected && this.anInt1150 >= 0 && this.anInt1150 < MemoryView.aVector1099.size() && MemoryView.aVector1099.get(this.anInt1150) == image) {
						gc2 = gc;
						foreground2 = color3;
					} else {
						gc2 = gc;
						foreground2 = ((i < this.anInt1134) ? color2 : color);
					}
					gc2.setForeground(foreground2);
				}
				gc.drawRectangle(n2 - 1, n - 1, n3 + 1, n4 + 1);
				this.aHashtable1091.put(new Rectangle(n2 - 1, n - 1, n3 + 1, n4 + 1), image);
			}
			n2 += n3 + 10;
			max = Math.max(max, n4);
			this.anInt1117 += image.getWidth() * image.getHeight() * 2;
		}
		background.dispose();
		color.dispose();
		color2.dispose();
		color3.dispose();
		this.anInt1144 = n + max + this.anInt1147 + 10;
		this.aCanvas1095.getVerticalBar().setMaximum(this.anInt1144);
		this.aCanvas1095.getVerticalBar().setThumb(Math.min(this.anInt1144, this.aCanvas1095.getClientArea().height));
		this.aCanvas1095.getVerticalBar().setIncrement(10);
		this.aCLabel1152.setText(this.anInt1117 + " bytes");
	}

	private void method695() {
		if (this.memoryMgr.players.size() > this.soundsTable.getItemCount()) {
			for (int i = this.memoryMgr.players.size() - this.soundsTable.getItemCount(); i > 0; --i) {
				new TableItem(this.soundsTable, 0);
			}
		} else {
			for (int j = this.memoryMgr.players.size(); j < this.soundsTable.getItemCount(); ++j) {
				this.soundsTable.remove(j);
			}
		}
		for (int k = 0; k < this.memoryMgr.players.size(); ++k) {
			final Object value = this.memoryMgr.players.get(k);
			final TableItem item;
			(item = this.soundsTable.getItem(k)).setText(0, value.toString());
			item.setText(1, Memory.playerType(value));
			item.setText(2, Memory.playerStateStr(value));
			item.setText(3, String.valueOf(Memory.loopCount(value)));
			item.setText(4, String.valueOf(Memory.dataLen(value)));
		}
		final Object value2;
		if (this.soundsTable.getSelectionIndex() != -1 && this.soundsTable.getSelectionIndex() < this.memoryMgr.players.size() && (value2 = this.memoryMgr.players.get(this.soundsTable.getSelectionIndex())) != null) {
			this.aScale1089.setSelection(Memory.volume(value2));
			this.aCLabel1154.setText(String.valueOf(this.aScale1089.getSelection()));
			this.aProgressBar1092.setSelection(Memory.progress(value2));
		}
	}

	private void changeSort(final int n) {
		this.table.setSortColumn(this.table.getColumn(n));
		int x = (this.table.getSortDirection() == 128) ? 1024 : 128;
		this.table.setSortDirection(x);
		sortColumn = n;
		sort();
	}

	private void sort() {
		if (sortColumn == -1) return;
		Collections.sort(this.anArrayList1110, new Class9(this, sortColumn));
		for (int i = 0; i < this.anArrayList1110.size(); ++i) {
			final Object value = this.anArrayList1110.get(i);
			final TableItem item;
			(item = this.table.getItem(i)).setText(0, (String) value);
			item.setText(1, String.valueOf(this.memoryMgr.method866(value)));
			item.setText(2, String.valueOf(this.memoryMgr.method867(value)));
		}
	}

	private void method696() {
		this.anArrayList1110 = Collections.list(this.memoryMgr.table.keys());
		for (int i = 0; i < this.anArrayList1110.size(); ++i) {
			if (this.memoryMgr.method867(this.anArrayList1110.get(i)) == 0) {
				this.anArrayList1110.remove(i--);
			}
		}
		Collections.sort((java.util.List<Comparable>) this.anArrayList1110);
		if (this.anArrayList1110.size() > this.table.getItemCount()) {
			for (int j = this.anArrayList1110.size() - this.table.getItemCount(); j > 0; --j) {
				new TableItem(this.table, 0);
			}
		} else {
			while (this.table.getItemCount() > this.anArrayList1110.size()) {
				this.table.remove(this.anArrayList1110.size());
			}
		}
		for (int k = 0; k < this.anArrayList1110.size(); ++k) {
			final Object value = this.anArrayList1110.get(k);
			final TableItem item;
			(item = this.table.getItem(k)).setText(0, (String) value);
			item.setText(1, String.valueOf(this.memoryMgr.method866(value)));
			item.setText(2, String.valueOf(this.memoryMgr.method867(value)));
		}
		//this.aTable1096.setSortColumn(this.aTable1096.getColumn(0));
		//this.aTable1096.setSortDirection(128);
	}

	private void method644(TableItem[] array) {
		if (array == null || array.length < 1) {
			return;
		}
		classTable.removeAll();
		classRefsVector.removeAllElements();
		String text = array[0].getText(0);
		Vector objs = this.memoryMgr.objs(text);
		if (text.equalsIgnoreCase("javax.microedition.lcdui.Image")) {
			imgSelected = true;
			anInt1150 = -1;
		} else {
			imgSelected = false;
		}
		for (Object o : objs) {
			TableItem ti = new TableItem(classTable, 0);
			ti.setText(0, Memory.refs(o));
			String s = String.valueOf(Memory.val(o));
			//XXX
			if (s.length() > 128) {
				s = s.substring(0, 127) + "...";
			}
			ti.setText(1, s);
			ti.setText(2, String.valueOf(Memory.size(o)));
			classRefsVector.add(Memory.val(o));
		}
	}

	private void method655(final TableItem[] array) {
		if (array == null || array.length < 1) {
			return;
		}
		final Object value;
		if ((value = this.classRefsVector.get(this.classTable.getSelectionIndex())) != null && emulator.debug.ClassTypes.method871(value.getClass())) {
			new Watcher(value).method311(this.shell);
		}
	}

	public final void widgetDisposed(final DisposeEvent disposeEvent) {
		this.dispose();
	}

	static void method647(final MemoryView class110) {
		class110.method692();
	}

	static Button method648(final MemoryView class110) {
		return class110.aButton1104;
	}

	static Text method652(final MemoryView class110) {
		return class110.aText1087;
	}

	static AutoUpdate method653(final MemoryView class110) {
		return class110.aClass88_1100;
	}

	static AutoUpdate method633(final MemoryView class110, final AutoUpdate aClass88_1100) {
		return class110.aClass88_1100 = aClass88_1100;
	}

	static Thread method635(final MemoryView class110, final Thread aThread1085) {
		return class110.autoUpdateThread = aThread1085;
	}

	static Thread method626(final MemoryView class110) {
		return class110.autoUpdateThread;
	}

	static boolean method627(final MemoryView class110, final boolean aBoolean1123) {
		return class110.aBoolean1123 = aBoolean1123;
	}

	static Button method657(final MemoryView class110) {
		return class110.aButton1121;
	}

	static boolean method659(final MemoryView class110, final boolean aBoolean1135) {
		return class110.aBoolean1135 = aBoolean1135;
	}

	static Button method666(final MemoryView class110) {
		return class110.aButton1129;
	}

	static Memory method629(final MemoryView class110) {
		return class110.memoryMgr;
	}

	static boolean method667(final MemoryView class110, final boolean aBoolean1111) {
		return class110.aBoolean1111 = aBoolean1111;
	}

	static Button method673(final MemoryView class110) {
		return class110.aButton1115;
	}

	static void method661(final MemoryView class110) {
		class110.method693();
	}

	static void method668(final MemoryView class110) {
		class110.method691();
	}

	static boolean method674(final MemoryView class110, final boolean aBoolean1127) {
		return class110.aBoolean1127 = aBoolean1127;
	}

	static Button method678(final MemoryView class110) {
		return class110.aButton1125;
	}

	static boolean method679(final MemoryView class110, final boolean aBoolean1131) {
		return class110.aBoolean1131 = aBoolean1131;
	}

	static Button method682(final MemoryView class110) {
		return class110.aButton1133;
	}

	static Table method649(final MemoryView class110) {
		return class110.soundsTable;
	}

	static Scale method630(final MemoryView class110) {
		return class110.aScale1089;
	}

	static CLabel method624(final MemoryView class110) {
		return class110.aCLabel1154;
	}

	static ProgressBar method646(final MemoryView class110) {
		return class110.aProgressBar1092;
	}

	static double method651(final MemoryView class110, final double anInt1102) {
		return class110.imageScaling = anInt1102;
	}

	static Combo method631(final MemoryView class110) {
		return class110.imageScaleCombo;
	}

	static int method662(final MemoryView class110, final int anInt1109) {
		return class110.anInt1109 = anInt1109;
	}

	static Combo method654(final MemoryView class110) {
		return class110.aCombo1107;
	}

	static int method669(final MemoryView class110, final int anInt1138) {
		return class110.anInt1138 = anInt1138;
	}

	static Canvas method642(final MemoryView class110) {
		return class110.aCanvas1095;
	}

	static int method675(final MemoryView class110, final int anInt1142) {
		return class110.anInt1142 = anInt1142;
	}

	static int method680(final MemoryView class110, final int anInt1147) {
		return class110.anInt1147 = anInt1147;
	}

	static void method650(final MemoryView class110, final GC gc) {
		class110.method636(gc);
	}

	static boolean method625(final MemoryView class110, final int n, final int n2) {
		return class110.method628(n, n2);
	}

	static Menu method643(final MemoryView class110) {
		return class110.aMenu1084;
	}

	static Menu method663(final MemoryView class110) {
		return class110.aMenu1113;
	}

	static Shell method632(final MemoryView class110) {
		return class110.shell;
	}

	static Image method640(final MemoryView class110) {
		return class110.anImage1088;
	}

	static ArrayList method638() {
		return MemoryView.anArrayList1090;
	}

	static Table method664(final MemoryView class110) {
		return class110.table;
	}

	static void method641(final MemoryView class110, final TableItem[] array) {
		class110.method644(array);
	}

	static void method623(final MemoryView class110, final int n) {
		class110.changeSort(n);
	}

	static boolean method639(final MemoryView class110) {
		return class110.imgSelected;
	}

	static int method683(final MemoryView class110, final int anInt1150) {
		return class110.anInt1150 = anInt1150;
	}

	static Table method670(final MemoryView class110) {
		return class110.classTable;
	}

	static void method658(final MemoryView class110, final TableItem[] array) {
		class110.method655(array);
	}

	static int method645(final MemoryView class110) {
		return class110.anInt1109;
	}

	static Vector method634() {
		return MemoryView.aVector1099;
	}

	static boolean method660(final MemoryView class110) {
		return class110.aBoolean1111;
	}

	static boolean method671(final MemoryView class110) {
		return class110.aBoolean1118;
	}

	static void method676(final MemoryView class110) {
		class110.method690();
	}

	static {
		MemoryView.anObject1086 = new Object();
		MemoryView.aVector1099 = new Vector();
		MemoryView.anArrayList1090 = new ArrayList();
	}

	final class DoUpdate implements Runnable {
		private final MemoryView aClass110_1210;

		DoUpdate(final MemoryView aClass110_1210) {
			super();
			this.aClass110_1210 = aClass110_1210;
		}

		public final void run() {
			this.aClass110_1210.method691();
		}

		DoUpdate(final MemoryView class110, final Class129 class111) {
			this(class110);
		}
	}

	final class AutoUpdate implements Runnable {
		boolean aBoolean885;
		long aLong886;
		long aLong888;
		private final MemoryView aClass110_887;

		AutoUpdate(final MemoryView aClass110_887, final long aLong888) {
			super();
			this.aClass110_887 = aClass110_887;
			this.aLong888 = aLong888;
			this.aBoolean885 = true;
			MemoryView.method633(aClass110_887, this);
		}

		public final void run() {
			this.aLong886 = System.currentTimeMillis();
			while (this.aBoolean885 && !MemoryView.method632(this.aClass110_887).isDisposed()) {
				try {
					if (System.currentTimeMillis() - this.aLong886 > this.aLong888 && !MemoryView.method671(this.aClass110_887)) {
						this.aClass110_887.method690();
						EmulatorImpl.syncExec(this.aClass110_887.new DoUpdate(aClass110_887));

						this.aLong886 = System.currentTimeMillis();
					}
					Thread.sleep(1L);
				} catch (InterruptedException ignored) {}
			}
			MemoryView.method633(this.aClass110_887, this);
		}
	}
}
