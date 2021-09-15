package emulator.ui.swt;

import org.eclipse.swt.custom.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Image;

import org.eclipse.swt.layout.*;
import emulator.*;
import emulator.UILocale;

import java.util.*;
import org.eclipse.swt.graphics.*;
import emulator.debug.*;
import emulator.debug.f;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public final class Class110 implements DisposeListener
{
    private Shell aShell1080;
    private Button aButton1081;
    private Button aButton1104;
    private CLabel aCLabel1082;
    private Text aText1087;
    private CLabel aCLabel1105;
    private CLabel aCLabel1114;
    private CLabel aCLabel1120;
    private CLabel aCLabel1124;
    private CLabel aCLabel1128;
    private CLabel aCLabel1132;
    private CLabel aCLabel1136;
    private CLabel aCLabel1140;
    private Display aDisplay1093;
    private a ana1079;
    private boolean aBoolean1094;
    private TabFolder aTabFolder1097;
    private Composite aComposite1098;
    private Composite aComposite1106;
    private CLabel aCLabel1143;
    private Combo aCombo1101;
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
    private SashForm aSashForm1083;
    private Composite aComposite1116;
    private SashForm aSashForm1103;
    private Table aTable1096;
    private Table aTable1108;
    private CLabel aCLabel1149;
    private CLabel aCLabel1152;
    private int anInt1102;
    private int anInt1109;
    private boolean aBoolean1111;
    private Menu aMenu1084;
    private Menu aMenu1113;
    private int anInt1117;
    private int anInt1122;
    private int anInt1126;
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
    private boolean aBoolean1139;
    private int anInt1150;
    private Hashtable aHashtable1091;
    private Image anImage1088;
    private ArrayList anArrayList1110;
    private Vector aVector1112;
    private AutoUpdate aClass88_1100;
    private Thread aThread1085;
    private Button aButton1145;
    private Button aButton1148;
    private CLabel aCLabel1153;
    private Scale aScale1089;
    private CLabel aCLabel1154;
    private Table aTable1119;
    private CLabel aCLabel1155;
    private ProgressBar aProgressBar1092;
    private Button aButton1151;
	private int sadasd;
	private boolean wyw;
	private int lastn;
	private boolean lastn2;
    
    public Class110() {
        super();
        this.aShell1080 = null;
        this.aButton1081 = null;
        this.aButton1104 = null;
        this.aCLabel1082 = null;
        this.aText1087 = null;
        this.aCLabel1105 = null;
        this.aCLabel1114 = null;
        this.aCLabel1120 = null;
        this.aCLabel1124 = null;
        this.aCLabel1128 = null;
        this.aCLabel1132 = null;
        this.aCLabel1136 = null;
        this.aCLabel1140 = null;
        this.aTabFolder1097 = null;
        this.aComposite1098 = null;
        this.aComposite1106 = null;
        this.aCLabel1143 = null;
        this.aCombo1101 = null;
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
        this.aSashForm1083 = null;
        this.aComposite1116 = null;
        this.aSashForm1103 = null;
        this.aTable1096 = null;
        this.aTable1108 = null;
        this.aCLabel1149 = null;
        this.aCLabel1152 = null;
        this.aBoolean1123 = true;
        this.aBoolean1127 = true;
        this.aHashtable1091 = new Hashtable();
        this.aButton1145 = null;
        this.aButton1148 = null;
        this.aCLabel1153 = null;
        this.aScale1089 = null;
        this.aCLabel1154 = null;
        this.aTable1119 = null;
        this.aCLabel1155 = null;
        this.aProgressBar1092 = null;
        this.aButton1151 = null;
        this.aDisplay1093 = Class146.getDisplay();
        this.ana1079 = new a();
        this.aVector1112 = new Vector();
        this.anArrayList1110 = new ArrayList();
    }
    
    public final void method621() {
        this.method665();
        ((Control)this.aShell1080).setLocation(this.aDisplay1093.getClientArea().width - this.aShell1080.getSize().x >> 1, this.aDisplay1093.getClientArea().height - this.aShell1080.getSize().y >> 1);
        this.aShell1080.open();
        ((Widget)this.aShell1080).addDisposeListener((DisposeListener)this);
        this.method692();
        this.aBoolean1094 = true;
        while (!((Widget)this.aShell1080).isDisposed()) {
            if (!this.aDisplay1093.readAndDispatch()) {
                this.aDisplay1093.sleep();
            }
        }
        if (this.aThread1085 != null && this.aThread1085.isAlive()) {
            this.aThread1085.interrupt();
        }
        this.ana1079.aVector1463.clear();
        this.aBoolean1094 = false;
    }
    
    public final void method656() {
        if (this.aShell1080 != null && !((Widget)this.aShell1080).isDisposed()) {
            this.aShell1080.dispose();
        }
        this.aBoolean1094 = false;
    }
    
    public final boolean method622() {
        return this.aBoolean1094;
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
        layoutData6.verticalSpan = 2;
        layoutData6.grabExcessHorizontalSpace = false;
        layoutData6.horizontalAlignment = 4;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 7;
        ((Decorations)(this.aShell1080 = new Shell(1264))).setText(UILocale.uiText("MEMORY_VIEW_TITLE", "MemoryView"));
        ((Decorations)this.aShell1080).setImage(new org.eclipse.swt.graphics.Image((Device)Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        ((Composite)this.aShell1080).setLayout((Layout)layout);
        ((Control)this.aShell1080).setSize(new Point(740, 466));
        (this.aButton1081 = new Button((Composite)this.aShell1080, 8388608)).setText(" " + UILocale.uiText("MEMORY_VIEW_UPDATE", "Update") + " ");
        ((Control)this.aButton1081).setLayoutData((Object)layoutData6);
        this.aButton1081.addSelectionListener((SelectionListener)new Class129(this));
        (this.aButton1104 = new Button((Composite)this.aShell1080, 32)).setText(UILocale.uiText("MEMORY_VIEW_AUTO_UPDATE", "AutoUpdate"));
        ((Control)this.aButton1104).setLayoutData((Object)layoutData4);
        this.aButton1104.addSelectionListener((SelectionListener)new Class70(this));
        (this.aCLabel1105 = new CLabel((Composite)this.aShell1080, 0)).setText(UILocale.uiText("MEMORY_VIEW_BYTECODE_SIZE", "ByteCode Size:"));
        ((Control)this.aCLabel1105).setLayoutData((Object)layoutData);
        (this.aCLabel1120 = new CLabel((Composite)this.aShell1080, 0)).setText("0              bytes");
        ((Control)this.aCLabel1120).setLayoutData((Object)gridData3);
        (this.aCLabel1128 = new CLabel((Composite)this.aShell1080, 0)).setText(UILocale.uiText("MEMORY_VIEW_TOTALMEM_SIZE", "Total Memory Used:"));
        ((Control)this.aCLabel1128).setLayoutData((Object)gridData);
        (this.aCLabel1132 = new CLabel((Composite)this.aShell1080, 0)).setText("0");
        ((Control)this.aCLabel1132).setLayoutData((Object)gridData2);
        (this.aCLabel1082 = new CLabel((Composite)this.aShell1080, 0)).setText(UILocale.uiText("MEMORY_VIEW_INTERVAL", "Interval(milli sec):"));
        ((Control)this.aCLabel1082).setLayoutData((Object)layoutData3);
        (this.aText1087 = new Text((Composite)this.aShell1080, 2048)).setText("1000    ");
        ((Control)this.aText1087).setLayoutData((Object)layoutData5);
        (this.aCLabel1114 = new CLabel((Composite)this.aShell1080, 0)).setText(UILocale.uiText("MEMORY_VIEW_OBJECT_SIZE", "Objects Size:"));
        ((Control)this.aCLabel1114).setLayoutData((Object)layoutData2);
        (this.aCLabel1124 = new CLabel((Composite)this.aShell1080, 0)).setText("0");
        ((Control)this.aCLabel1124).setLayoutData((Object)gridData3);
        (this.aCLabel1136 = new CLabel((Composite)this.aShell1080, 0)).setText(UILocale.uiText("MEMORY_VIEW_MAX_OBJECT_SIZE", "Max Objects Size:"));
        ((Control)this.aCLabel1136).setLayoutData((Object)gridData);
        (this.aCLabel1140 = new CLabel((Composite)this.aShell1080, 0)).setText("0");
        ((Control)this.aCLabel1140).setLayoutData((Object)gridData2);
        this.method687();
    }
    
    private void method672() {
        this.aTabFolder1097 = new TabFolder((Composite)this.aSashForm1083, 0);
        this.method677();
        this.method681();
        final TabItem tabItem;
        (tabItem = new TabItem(this.aTabFolder1097, 0)).setText(UILocale.uiText("MEMORY_VIEW_IMAGES", "Images"));
        tabItem.setControl((Control)this.aComposite1098);
        final TabItem tabItem2;
        (tabItem2 = new TabItem(this.aTabFolder1097, 0)).setText(UILocale.uiText("MEMORY_VIEW_SOUNDS", "Sounds"));
        tabItem2.setControl((Control)this.aComposite1106);
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
        (this.aComposite1098 = new Composite((Composite)this.aTabFolder1097, 0)).setLayout((Layout)layout);
        (this.aCLabel1143 = new CLabel(this.aComposite1098, 0)).setText(UILocale.uiText("MEMORY_VIEW_ZOOM", "Zoom:"));
        this.method684();
        (this.aCLabel1146 = new CLabel(this.aComposite1098, 0)).setText(UILocale.uiText("MEMORY_VIEW_SORT", "Sort:"));
        this.method685();
        (this.aButton1121 = new Button(this.aComposite1098, 32)).setText(UILocale.uiText("MEMORY_VIEW_IMAGES_DRAWN", "Images Drawn"));
        this.aButton1121.setSelection(true);
        this.aButton1121.addSelectionListener((SelectionListener)new Class144(this));
        (this.aButton1129 = new Button(this.aComposite1098, 32)).setText(UILocale.uiText("MEMORY_VIEW_UNUSED_REGION", "Darken Unused Regions"));
        this.aButton1129.addSelectionListener((SelectionListener)new Class125(this));
        (this.aButton1137 = new Button(this.aComposite1098, 8388608)).setText(UILocale.uiText("MEMORY_VIEW_RESET_IMAGE", "Reset Image Usage"));
        this.aButton1137.addSelectionListener((SelectionListener)new Class123(this));
        (this.aCLabel1149 = new CLabel(this.aComposite1098, 0)).setText(UILocale.uiText("MEMORY_VIEW_SIZE", "Size:"));
        (this.aCLabel1152 = new CLabel(this.aComposite1098, 0)).setText("");
        ((Control)this.aCLabel1152).setLayoutData((Object)layoutData);
        (this.aButton1115 = new Button(this.aComposite1098, 32)).setText(UILocale.uiText("MEMORY_VIEW_ASCEND", "Ascending"));
        ((Control)this.aButton1115).setEnabled(true);
        this.aButton1115.setSelection(true);
        this.aBoolean1111 = true;
        this.aButton1115.addSelectionListener((SelectionListener)new Class66(this));
        (this.aButton1125 = new Button(this.aComposite1098, 32)).setText(UILocale.uiText("MEMORY_VIEW_IMAGE_NEVER_DRAW", "Images Never Drawn"));
        this.aButton1125.setSelection(true);
        this.aButton1125.addSelectionListener((SelectionListener)new Class69(this));
        (this.aButton1133 = new Button(this.aComposite1098, 32)).setText(UILocale.uiText("MEMORY_VIEW_RELEASED_IMAGES", "Released Images"));
        ((Control)this.aButton1133).setEnabled(Settings.recordReleasedImg);
        this.aButton1133.addSelectionListener((SelectionListener)new Class68(this));
        (this.aButton1141 = new Button(this.aComposite1098, 8388608)).setText(UILocale.uiText("MEMORY_VIEW_CLEAR_RELEASED_IMAGES", "Clear Released Images"));
        ((Control)this.aButton1141).setEnabled(Settings.recordReleasedImg);
        this.aButton1141.addSelectionListener((SelectionListener)new Class71(this));
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
        (layout = new GridLayout()).numColumns = 8;
        (this.aComposite1106 = new Composite((Composite)this.aTabFolder1097, 0)).setLayout((Layout)layout);
        (this.aButton1145 = new Button(this.aComposite1106, 8388608)).setText(UILocale.uiText("MEMORY_VIEW_SOUND_START", "Start"));
        (this.aButton1151 = new Button(this.aComposite1106, 8388608)).setText(UILocale.uiText("MEMORY_VIEW_SOUND_PAUSE", "Pause"));
        this.aButton1151.addSelectionListener((SelectionListener)new Class28(this));
        this.aButton1145.addSelectionListener((SelectionListener)new Class7(this));
        (this.aButton1148 = new Button(this.aComposite1106, 8388608)).setText(UILocale.uiText("MEMORY_VIEW_SOUND_STOP", "Stop"));
        this.aButton1148.addSelectionListener((SelectionListener)new Class27(this));
        (this.aCLabel1153 = new CLabel(this.aComposite1106, 0)).setText(UILocale.uiText("MEMORY_VIEW_SOUND_VOLUME", "Volume:"));
        (this.aCLabel1154 = new CLabel(this.aComposite1106, 0)).setText("0   ");
        (this.aScale1089 = new Scale(this.aComposite1106, 0)).addSelectionListener((SelectionListener)new Class21(this));
        (this.aCLabel1155 = new CLabel(this.aComposite1106, 0)).setText(UILocale.uiText("MEMORY_VIEW_SOUND_PROGRESS", "Progress:"));
        (this.aProgressBar1092 = new ProgressBar(this.aComposite1106, 65536)).setSelection(0);
        final Color foreground = new Color((Device)Display.getCurrent(), 217, 108, 0);
        ((Control)this.aProgressBar1092).setForeground(foreground);
        foreground.dispose();
        (this.aTable1119 = new Table(this.aComposite1106, 67584)).setHeaderVisible(true);
        ((Control)this.aTable1119).setLayoutData((Object)layoutData);
        this.aTable1119.setLinesVisible(true);
        this.aTable1119.addSelectionListener((SelectionListener)new Class24(this));
        final TableColumn tableColumn;
        (tableColumn = new TableColumn(this.aTable1119, 0)).setWidth(100);
        tableColumn.setText(UILocale.uiText("MEMORY_VIEW_INSTANCE", "Instance"));
        final TableColumn tableColumn2;
        (tableColumn2 = new TableColumn(this.aTable1119, 0)).setWidth(100);
        tableColumn2.setText(UILocale.uiText("MEMORY_VIEW_CONTENT_TYPE", "ContentType"));
        final TableColumn tableColumn3;
        (tableColumn3 = new TableColumn(this.aTable1119, 0)).setWidth(100);
        tableColumn3.setText(UILocale.uiText("MEMORY_VIEW_STATE", "State"));
        final TableColumn tableColumn4;
        (tableColumn4 = new TableColumn(this.aTable1119, 0)).setWidth(100);
        tableColumn4.setText(UILocale.uiText("MEMORY_VIEW_LOOP_COUNT", "LoopCount"));
        final TableColumn tableColumn5;
        (tableColumn5 = new TableColumn(this.aTable1119, 0)).setWidth(100);
        tableColumn5.setText(UILocale.uiText("MEMORY_VIEW_DATA_SIZE", "DataSize"));
    }
    
    private void method684() {
        (this.aCombo1101 = new Combo(this.aComposite1098, 8)).add("100%");
        this.aCombo1101.add("200%");
        this.aCombo1101.add("300%");
        this.aCombo1101.add("400%");
        this.aCombo1101.setText("100%");
        this.anInt1102 = 1;
        this.aCombo1101.addModifyListener((ModifyListener)new Class35(this));
    }
    
    private void method685() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 1;
        layoutData.grabExcessHorizontalSpace = false;
        layoutData.verticalAlignment = 2;
        ((Control)(this.aCombo1107 = new Combo(this.aComposite1098, 8))).setEnabled(true);
        ((Control)this.aCombo1107).setLayoutData((Object)layoutData);
        this.aCombo1107.add(UILocale.uiText("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
        this.aCombo1107.add(UILocale.uiText("MEMORY_VIEW_SORT_SIZE", "Size"));
        this.aCombo1107.add(UILocale.uiText("MEMORY_VIEW_SORT_DRAW_COUNT", "Draw Count"));
        this.aCombo1107.setText(UILocale.uiText("MEMORY_VIEW_SORT_REFERENCE", "Reference"));
        this.anInt1109 = 0;
        this.aCombo1107.addModifyListener((ModifyListener)new Class22(this));
    }
    
    private void method686() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.horizontalSpan = 7;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        ((Composite)(this.aCanvas1095 = new Canvas(this.aComposite1098, 537135616))).setLayout((Layout)null);
        ((Control)this.aCanvas1095).setLayoutData((Object)layoutData);
        ((Control)this.aCanvas1095).addPaintListener((PaintListener)new Class26(this));
        ((Control)this.aCanvas1095).addMouseListener((MouseListener)new Class25(this));
        ((Scrollable)this.aCanvas1095).getVerticalBar().addSelectionListener((SelectionListener)new Class23(this));
        this.aMenu1084 = new Menu((Decorations)this.aShell1080, 8);
        final MenuItem menuItem;
        (menuItem = new MenuItem(this.aMenu1084, 8)).setText(UILocale.uiText("MEMORY_VIEW_SAVE_AS", "Save As ..."));
        menuItem.addSelectionListener((SelectionListener)new Class36(this));
        this.aMenu1113 = new Menu((Decorations)this.aShell1080, 8);
        final MenuItem menuItem2;
        (menuItem2 = new MenuItem(this.aMenu1113, 8)).setText(UILocale.uiText("MEMORY_VIEW_SAVE_ALL", "Save All Images ..."));
        menuItem2.addSelectionListener((SelectionListener)new Class33(this));
    }
    
    private void method687() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalSpan = 7;
        layoutData.horizontalAlignment = 4;
        layoutData.verticalAlignment = 4;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;
        (this.aSashForm1083 = new SashForm((Composite)this.aShell1080, 0)).setOrientation(512);
        ((Control)this.aSashForm1083).setLayoutData((Object)layoutData);
        this.method688();
        this.method672();
        this.aSashForm1083.setWeights(new int[] { 4, 6 });
    }
    
    private void method688() {
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 1;
        this.aComposite1116 = new Composite((Composite)this.aSashForm1083, 0);
        this.method689();
        this.aComposite1116.setLayout((Layout)layout);
    }
    
    private void method689() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        ((Control)(this.aSashForm1103 = new SashForm(this.aComposite1116, 0))).setLayoutData((Object)layoutData);
        (this.aTable1096 = new Table((Composite)this.aSashForm1103, 67584)).setHeaderVisible(true);
        this.aTable1096.setLinesVisible(true);
        this.aTable1096.addSelectionListener((SelectionListener)new Class34(this));
        final TableColumn tableColumn;
        (tableColumn = new TableColumn(this.aTable1096, 0)).setWidth(100);
        tableColumn.setText(UILocale.uiText("MEMORY_VIEW_CLASS", "Class"));
        tableColumn.addSelectionListener((SelectionListener)new Class31(this));
        final TableColumn tableColumn2;
        (tableColumn2 = new TableColumn(this.aTable1096, 0)).setWidth(100);
        tableColumn2.setText(UILocale.uiText("MEMORY_VIEW_INSTANCES", "Instances"));
        tableColumn2.addSelectionListener((SelectionListener)new Class140(this));
        final TableColumn tableColumn3;
        (tableColumn3 = new TableColumn(this.aTable1096, 0)).setWidth(100);
        tableColumn3.setText(UILocale.uiText("MEMORY_VIEW_TOTAL_HEAP_SIZE", "Total Heap Size"));
        tableColumn3.addSelectionListener((SelectionListener)new Class17(this));
        (this.aTable1108 = new Table((Composite)this.aSashForm1103, 67584)).setHeaderVisible(true);
        this.aTable1108.setLinesVisible(true);
        this.aTable1108.addSelectionListener((SelectionListener)new Class19(this));
        ((Control)this.aTable1108).addMouseListener((MouseListener)new Class13(this));
        final TableColumn tableColumn4;
        (tableColumn4 = new TableColumn(this.aTable1108, 0)).setWidth(100);
        tableColumn4.setText(UILocale.uiText("MEMORY_VIEW_REFERENCE", "Reference"));
        final TableColumn tableColumn5;
        (tableColumn5 = new TableColumn(this.aTable1108, 0)).setWidth(100);
        tableColumn5.setText(UILocale.uiText("MEMORY_VIEW_INSTANCE", "Instance"));
        final TableColumn tableColumn6;
        (tableColumn6 = new TableColumn(this.aTable1108, 0)).setWidth(100);
        tableColumn6.setText(UILocale.uiText("MEMORY_VIEW_SIZE", "Size"));
    }
    
    private void method690() {
        if (this.aBoolean1118) {
            return;
        }
        this.aBoolean1118 = true;
        try {
            this.ana1079.method846();
            this.method694();
            this.anInt1122 = a.method850();
            this.anInt1126 = this.ana1079.method857();
            this.anInt1130 = Math.max(this.anInt1130, this.anInt1126);
        }
        catch (Exception ex) {}
        catch (Error ex) {}
        this.aBoolean1118 = false;
    }
    
    private void method691() {
        if (((Widget)this.aShell1080).isDisposed()) {
            return;
        }
        try {
            if (this.aTabFolder1097.getSelectionIndex() == 0) {
                ((Control)this.aCanvas1095).redraw();
            }
            else {
                this.method695();
            }
            this.method696();
        }
        catch (Exception ex) {}
        this.aCLabel1120.setText(this.anInt1122 + " bytes");
        this.aCLabel1124.setText(this.anInt1126 + " bytes");
        this.aCLabel1132.setText(this.anInt1122 + this.anInt1126 + " bytes");
        this.aCLabel1140.setText(this.anInt1130 + " bytes");
    }
    
    private void method692() {
        this.method690();
        this.method691();
    }
    
    private void method693() {
        synchronized (Class110.anObject1086) {
            Collections.sort((java.util.List<Object>)Class110.anArrayList1090, new Class15(this));
        }
    }
    
    private void method694() {
        synchronized (Class110.anObject1086) {
            final int size = this.ana1079.aVector1461.size();
            final int n = this.ana1079.aVector1461.size() + (this.aBoolean1131 ? this.ana1079.aVector1463.size() : 0);
            Class110.aVector1099.clear();
            Class110.anArrayList1090.clear();
            for (int i = 0; i < n; ++i) {
                Image image;
                try {
                    if (i < size) {
                        image = (Image) this.ana1079.aVector1461.get(i);
                    }
                    else {
                        image = (Image) this.ana1079.aVector1463.get(i - size);
                    }
                }
                catch (Exception ex) {
                    break;
                }
                Class110.aVector1099.add(image);
                if (i < size && (image.getUsedCount() <= 0 || !this.aBoolean1123)) {
                    if (image.getUsedCount() != 0) {
                        continue;
                    }
                    if (!this.aBoolean1127) {
                        continue;
                    }
                }
                Class110.anArrayList1090.add(image);
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
                this.anImage1088 = (Image)this.aHashtable1091.get(rectangle);
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
        final Color background = new Color((Device)null, 151, 150, 147);
        final Color color = new Color((Device)null, 255, 0, 0);
        final Color color2 = new Color((Device)null, 0, 0, 0);
        final Color color3 = new Color((Device)null, 0, 255, 0);
        final Color foreground = new Color((Device)null, 0, 0, 255);
        gc.setBackground(background);
        gc.fillRectangle(0, 0, this.anInt1138, this.anInt1142);
        this.anInt1117 = 0;
        this.aHashtable1091.clear();
        for (int size = Class110.anArrayList1090.size(), i = 0; i < size; ++i) {
            Image image;
            try {
                image = (Image) Class110.anArrayList1090.get(i);
            }
            catch (Exception ex) {
                break;
            }
            final int n3 = image.getWidth() * this.anInt1102;
            final int n4 = image.getHeight() * this.anInt1102;
            if (n2 + n3 + 30 > this.anInt1138) {
                n2 = 10;
                n += max + 12;
                max = 0;
            }
            Label_0547: {
                if (n + n4 <= 0) {
                    if (n <= this.anInt1142) {
                        break Label_0547;
                    }
                }
                try {
                    if (Settings.g2d == 1) {
                        ((emulator.graphics2D.awt.d)image.getImpl()).method13(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
                        if (this.aBoolean1135) {
                            ((emulator.graphics2D.awt.d)image.getUsedRegion()).method13(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
                        }
                    }
                    else if (Settings.g2d == 0) {
                        ((emulator.graphics2D.swt.ImageSWT)image.getImpl()).method13(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
                        if (this.aBoolean1135) {
                            ((emulator.graphics2D.swt.ImageSWT)image.getUsedRegion()).method13(gc, 0, 0, image.getWidth(), image.getHeight(), n2, n, n3, n4);
                        }
                    }
                }
                catch (Exception ex2) {}
                if (image instanceof f) {
                    gc.setForeground(foreground);
                    gc.drawString("Tex", n2 - 1, n + 1 - gc.getFontMetrics().getHeight(), true);
                }
                else {
                    GC gc2;
                    Color foreground2;
                    if (this.aBoolean1139 && this.anInt1150 >= 0 && this.anInt1150 < Class110.aVector1099.size() && Class110.aVector1099.get(this.anInt1150) == image) {
                        gc2 = gc;
                        foreground2 = color3;
                    }
                    else {
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
        ((Scrollable)this.aCanvas1095).getVerticalBar().setMaximum(this.anInt1144);
        ((Scrollable)this.aCanvas1095).getVerticalBar().setThumb(Math.min(this.anInt1144, ((Scrollable)this.aCanvas1095).getClientArea().height));
        ((Scrollable)this.aCanvas1095).getVerticalBar().setIncrement(10);
        this.aCLabel1152.setText(this.anInt1117 + " bytes");
    }
    
    private void method695() {
        if (this.ana1079.aVector1465.size() > this.aTable1119.getItemCount()) {
            for (int i = this.ana1079.aVector1465.size() - this.aTable1119.getItemCount(); i > 0; --i) {
                new TableItem(this.aTable1119, 0);
            }
        }
        else {
            for (int j = this.ana1079.aVector1465.size(); j < this.aTable1119.getItemCount(); ++j) {
                this.aTable1119.remove(j);
            }
        }
        for (int k = 0; k < this.ana1079.aVector1465.size(); ++k) {
            final Object value = this.ana1079.aVector1465.get(k);
            final TableItem item;
            (item = this.aTable1119.getItem(k)).setText(0, value.toString());
            item.setText(1, a.method851(value));
            item.setText(2, a.method858(value));
            item.setText(3, String.valueOf(a.method852(value)));
            item.setText(4, String.valueOf(a.method859(value)));
        }
        final Object value2;
        if (this.aTable1119.getSelectionIndex() != -1 && this.aTable1119.getSelectionIndex() < this.ana1079.aVector1465.size() && (value2 = this.ana1079.aVector1465.get(this.aTable1119.getSelectionIndex())) != null) {
            this.aScale1089.setSelection(a.method865(value2));
            this.aCLabel1154.setText(String.valueOf(this.aScale1089.getSelection()));
            this.aProgressBar1092.setSelection(a.method863(value2));
        }
    }
    
	private void method637(final int n) {
    	lastn = n;
    	wyw = true;
        this.aTable1096.setSortColumn(this.aTable1096.getColumn(n));
        int x = (this.aTable1096.getSortDirection() == 128) ? 1024 : 128;
        this.aTable1096.setSortDirection(x);

    	//	lastn2 = x == 1024;
        lastn2 = true;
        Collections.sort(this.anArrayList1110, new Class9(this, n));
        for (int i = 0; i < this.anArrayList1110.size(); ++i) {
            final Object value = this.anArrayList1110.get(i);
            final TableItem item;
            (item = this.aTable1096.getItem(i)).setText(0, (String)value);
            item.setText(1, String.valueOf(this.ana1079.method866(value)));
            item.setText(2, String.valueOf(this.ana1079.method867(value)));
        }
    }
    
    private void method696() {
        this.anArrayList1110 = Collections.list(this.ana1079.aHashtable1458.keys());
        for (int i = 0; i < this.anArrayList1110.size(); ++i) {
            if (this.ana1079.method867(this.anArrayList1110.get(i)) == 0) {
                this.anArrayList1110.remove(i--);
            }
        }
        Collections.sort((java.util.List<Comparable>)this.anArrayList1110);
        if (this.anArrayList1110.size() > this.aTable1096.getItemCount()) {
            for (int j = this.anArrayList1110.size() - this.aTable1096.getItemCount(); j > 0; --j) {
                new TableItem(this.aTable1096, 0);
            }
        }
        else {
            while (this.aTable1096.getItemCount() > this.anArrayList1110.size()) {
                this.aTable1096.remove(this.anArrayList1110.size());
            }
        }
        for (int k = 0; k < this.anArrayList1110.size(); ++k) {
            final Object value = this.anArrayList1110.get(k);
            final TableItem item;
            (item = this.aTable1096.getItem(k)).setText(0, (String)value);
            item.setText(1, String.valueOf(this.ana1079.method866(value)));
            item.setText(2, String.valueOf(this.ana1079.method867(value)));
        }
        this.aTable1096.setSortColumn(this.aTable1096.getColumn(0));
        this.aTable1096.setSortDirection(128);
    }
    
    private void method644(final TableItem[] array) {
        if (array == null || array.length < 1) {
            return;
        }
        this.aTable1108.removeAll();
        this.aVector1112.removeAllElements();
        final String text = array[0].getText(0);
        final Vector method854 = this.ana1079.method854(text);
        if (text.equalsIgnoreCase("javax.microedition.lcdui.Image")) {
            this.aBoolean1139 = true;
            this.anInt1150 = -1;
        }
        else {
            this.aBoolean1139 = false;
        }
        for (int i = 0; i < method854.size(); ++i) {
            final Object value = method854.get(i);
            final TableItem tableItem;
            (tableItem = new TableItem(this.aTable1108, 0)).setText(0, a.method864(value));
            tableItem.setText(1, String.valueOf(a.method855(value)));
            tableItem.setText(2, String.valueOf(a.method868(value)));
            this.aVector1112.add(a.method855(value));
        }
    }
    
    private void method655(final TableItem[] array) {
        if (array == null || array.length < 1) {
            return;
        }
        final Object value;
        if ((value = this.aVector1112.get(this.aTable1108.getSelectionIndex())) != null && emulator.debug.ClassTypes.method871(value.getClass())) {
            new Class5(value).method311(this.aShell1080);
        }
    }
    
    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        this.method656();
    }
    
    static void method647(final Class110 class110) {
        class110.method692();
    }
    
    static Button method648(final Class110 class110) {
        return class110.aButton1104;
    }
    
    static Text method652(final Class110 class110) {
        return class110.aText1087;
    }
    
    static AutoUpdate method653(final Class110 class110) {
        return class110.aClass88_1100;
    }
    
    static AutoUpdate method633(final Class110 class110, final AutoUpdate aClass88_1100) {
        return class110.aClass88_1100 = aClass88_1100;
    }
    
    static Thread method635(final Class110 class110, final Thread aThread1085) {
        return class110.aThread1085 = aThread1085;
    }
    
    static Thread method626(final Class110 class110) {
        return class110.aThread1085;
    }
    
    static boolean method627(final Class110 class110, final boolean aBoolean1123) {
        return class110.aBoolean1123 = aBoolean1123;
    }
    
    static Button method657(final Class110 class110) {
        return class110.aButton1121;
    }
    
    static boolean method659(final Class110 class110, final boolean aBoolean1135) {
        return class110.aBoolean1135 = aBoolean1135;
    }
    
    static Button method666(final Class110 class110) {
        return class110.aButton1129;
    }
    
    static a method629(final Class110 class110) {
        return class110.ana1079;
    }
    
    static boolean method667(final Class110 class110, final boolean aBoolean1111) {
        return class110.aBoolean1111 = aBoolean1111;
    }
    
    static Button method673(final Class110 class110) {
        return class110.aButton1115;
    }
    
    static void method661(final Class110 class110) {
        class110.method693();
    }
    
    static void method668(final Class110 class110) {
        class110.method691();
    }
    
    static boolean method674(final Class110 class110, final boolean aBoolean1127) {
        return class110.aBoolean1127 = aBoolean1127;
    }
    
    static Button method678(final Class110 class110) {
        return class110.aButton1125;
    }
    
    static boolean method679(final Class110 class110, final boolean aBoolean1131) {
        return class110.aBoolean1131 = aBoolean1131;
    }
    
    static Button method682(final Class110 class110) {
        return class110.aButton1133;
    }
    
    static Table method649(final Class110 class110) {
        return class110.aTable1119;
    }
    
    static Scale method630(final Class110 class110) {
        return class110.aScale1089;
    }
    
    static CLabel method624(final Class110 class110) {
        return class110.aCLabel1154;
    }
    
    static ProgressBar method646(final Class110 class110) {
        return class110.aProgressBar1092;
    }
    
    static int method651(final Class110 class110, final int anInt1102) {
        return class110.anInt1102 = anInt1102;
    }
    
    static Combo method631(final Class110 class110) {
        return class110.aCombo1101;
    }
    
    static int method662(final Class110 class110, final int anInt1109) {
        return class110.anInt1109 = anInt1109;
    }
    
    static Combo method654(final Class110 class110) {
        return class110.aCombo1107;
    }
    
    static int method669(final Class110 class110, final int anInt1138) {
        return class110.anInt1138 = anInt1138;
    }
    
    static Canvas method642(final Class110 class110) {
        return class110.aCanvas1095;
    }
    
    static int method675(final Class110 class110, final int anInt1142) {
        return class110.anInt1142 = anInt1142;
    }
    
    static int method680(final Class110 class110, final int anInt1147) {
        return class110.anInt1147 = anInt1147;
    }
    
    static void method650(final Class110 class110, final GC gc) {
        class110.method636(gc);
    }
    
    static boolean method625(final Class110 class110, final int n, final int n2) {
        return class110.method628(n, n2);
    }
    
    static Menu method643(final Class110 class110) {
        return class110.aMenu1084;
    }
    
    static Menu method663(final Class110 class110) {
        return class110.aMenu1113;
    }
    
    static Shell method632(final Class110 class110) {
        return class110.aShell1080;
    }
    
    static Image method640(final Class110 class110) {
        return class110.anImage1088;
    }
    
    static ArrayList method638() {
        return Class110.anArrayList1090;
    }
    
    static Table method664(final Class110 class110) {
        return class110.aTable1096;
    }
    
    static void method641(final Class110 class110, final TableItem[] array) {
        class110.method644(array);
    }
    
    static void method623(final Class110 class110, final int n) {
        class110.method637(n);
    }
    
    static boolean method639(final Class110 class110) {
        return class110.aBoolean1139;
    }
    
    static int method683(final Class110 class110, final int anInt1150) {
        return class110.anInt1150 = anInt1150;
    }
    
    static Table method670(final Class110 class110) {
        return class110.aTable1108;
    }
    
    static void method658(final Class110 class110, final TableItem[] array) {
        class110.method655(array);
    }
    
    static int method645(final Class110 class110) {
        return class110.anInt1109;
    }
    
    static Vector method634() {
        return Class110.aVector1099;
    }
    
    static boolean method660(final Class110 class110) {
        return class110.aBoolean1111;
    }
    
    static boolean method671(final Class110 class110) {
        return class110.aBoolean1118;
    }
    
    static void method676(final Class110 class110) {
        class110.method690();
    }
    
    static {
        Class110.anObject1086 = new Object();
        Class110.aVector1099 = new Vector();
        Class110.anArrayList1090 = new ArrayList();
    }
    
    final class DoUpdate implements Runnable
    {
        private final Class110 aClass110_1210;
        
        DoUpdate(final Class110 aClass110_1210) {
            super();
            this.aClass110_1210 = aClass110_1210;
        }
        
        public final void run() {
            Class110.method668(this.aClass110_1210);
            if(wyw)
            {
            	aClass110_1210.method637(lastn);
            	if(lastn2)
            	aClass110_1210.method637(lastn);
            }
        }
        
        DoUpdate(final Class110 class110, final Class129 class111) {
            this(class110);
        }
    }
    
    final class AutoUpdate implements Runnable
    {
        boolean aBoolean885;
        long aLong886;
        long aLong888;
        private final Class110 aClass110_887;
        
        AutoUpdate(final Class110 aClass110_887, final long aLong888) {
            super();
            this.aClass110_887 = aClass110_887;
            this.aLong888 = aLong888;
            this.aBoolean885 = true;
            Class110.method633(aClass110_887, this);
        }
        
        public final void run() {
            this.aLong886 = System.currentTimeMillis();
            while (this.aBoolean885 && !((Widget)Class110.method632(this.aClass110_887)).isDisposed()) {
                try {
                    if (System.currentTimeMillis() - this.aLong886 > this.aLong888 && !Class110.method671(this.aClass110_887)) {
                        Class110.method676(this.aClass110_887);
                        Class146.syncExec(this.aClass110_887.new DoUpdate(aClass110_887));
                       
                        this.aLong886 = System.currentTimeMillis();
                    }
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {}
            }
            Class110.method633(this.aClass110_887, this);
        }
    }
}
