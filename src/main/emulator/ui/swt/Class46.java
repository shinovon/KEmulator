package emulator.ui.swt;

import org.eclipse.swt.custom.*;

import java.text.*;

import emulator.custom.h;
import emulator.custom.h.MethodInfo;

import java.util.*;
import java.util.List;

import emulator.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class46 implements Runnable, DisposeListener {
    private Shell aShell784;
    private SashForm aSashForm785;
    private Composite aComposite787;
    private Button aButton783;
    private Button aButton795;
    private Table aTable788;
    private Composite aComposite796;
    private Button aButton797;
    private Button aButton798;
    private Text aText789;
    private Button aButton799;
    private StyledText aStyledText790;
    private Display aDisplay791;
    private boolean aBoolean793;
    private static NumberFormat aNumberFormat794;
    private int anInt786;
    private ArrayList anArrayList792;

    public Class46() {
        super();
        this.aShell784 = null;
        this.aSashForm785 = null;
        this.aComposite787 = null;
        this.aButton783 = null;
        this.aButton795 = null;
        this.aTable788 = null;
        this.aComposite796 = null;
        this.aButton797 = null;
        this.aButton798 = null;
        this.aText789 = null;
        this.aButton799 = null;
        this.aStyledText790 = null;
        Class46.aNumberFormat794 = NumberFormat.getInstance();
        this.anArrayList792 = new ArrayList();
    }

    private void method448() {
        this.anArrayList792.clear();
        this.anArrayList792.addAll(h.aHashtable1061.values());
        final Enumeration<h.MethodInfo> elements = h.aHashtable1061.elements();
        while (elements.hasMoreElements()) {
            final h.MethodInfo data = elements.nextElement();
            final TableItem tableItem;
            (tableItem = new TableItem(this.aTable788, 0)).setData(data);
            tableItem.setText(0, data.aString1172);
            tableItem.setText(1, data.aString1177);
            tableItem.setText(2, data.aString1181);
            tableItem.setText(3, String.valueOf(data.anInt1173));
            tableItem.setText(4, String.valueOf(data.refCount));
            tableItem.setText(5, String.valueOf(data.anInt1182));
            tableItem.setText(6, Class46.aNumberFormat794.format(data.aLong1179));
            tableItem.setText(7, Class46.aNumberFormat794.format(data.aFloat1175));
            tableItem.setText(8, Class46.aNumberFormat794.format(data.aFloat1180));
        }
        this.run();
        this.method435(0);
    }

    private void method435(final int n) {
        this.aTable788.setSortColumn(this.aTable788.getColumn(n));
        this.aTable788.setSortDirection((this.aTable788.getSortDirection() == 128) ? 1024 : 128);
        Collections.sort((List<Object>) this.anArrayList792, new Class169(this, n));
        for (int i = this.anArrayList792.size() - 1; i >= 0; --i) {
            final h.MethodInfo data = (MethodInfo) this.anArrayList792.get(i);
            final TableItem item;
            (item = this.aTable788.getItem(i)).setData(data);
            item.setText(0, data.aString1172);
            item.setText(1, data.aString1177);
            item.setText(2, data.aString1181);
            item.setText(3, String.valueOf(data.anInt1173));
            item.setText(4, String.valueOf(data.refCount));
            item.setText(5, String.valueOf(data.anInt1182));
            item.setText(5, String.valueOf(data.anInt1182));
            item.setText(6, Class46.aNumberFormat794.format(data.aLong1179));
            item.setText(7, Class46.aNumberFormat794.format(data.aFloat1175));
            item.setText(8, Class46.aNumberFormat794.format(data.aFloat1180));
        }
    }

    public final void run() {
        if (!this.aBoolean793) {
            return;
        }
        try {
            long max = 0L;
            final Enumeration<h.MethodInfo> elements = (Enumeration<h.MethodInfo>) h.aHashtable1061.elements();
            while (elements.hasMoreElements()) {
                max = Math.max(max, elements.nextElement().aLong1179);
            }
            if (max > 0L) {
                final Enumeration<h.MethodInfo> elements2 = (Enumeration<h.MethodInfo>) h.aHashtable1061.elements();
                while (elements2.hasMoreElements()) {
                    final h.MethodInfo methodInfo = elements2.nextElement();
                    methodInfo.aFloat1180 = 100.0f * methodInfo.aLong1179 / max;
                }
            }
            for (int i = this.anArrayList792.size() - 1; i >= 0; --i) {
                final h.MethodInfo methodInfo2 = (MethodInfo) this.anArrayList792.get(i);
                final TableItem item;
                (item = this.aTable788.getItem(i)).setText(5, String.valueOf(methodInfo2.anInt1182));
                item.setText(6, Class46.aNumberFormat794.format(methodInfo2.aLong1179));
                item.setText(7, Class46.aNumberFormat794.format(methodInfo2.aFloat1175));
                item.setText(8, Class46.aNumberFormat794.format(methodInfo2.aFloat1180));
            }
        } catch (Exception ignored) {
        }
    }

    public final void method436() {
        if (h.aHashtable1061 == null) {
            h.aHashtable1061 = new Hashtable();
            h.method591();
        }
        this.method449();
        this.aDisplay791 = Display.getCurrent();
        this.aShell784.setLocation(this.aDisplay791.getClientArea().width - this.aShell784.getSize().x >> 1, this.aDisplay791.getClientArea().height - this.aShell784.getSize().y >> 1);
        this.aShell784.open();
        this.aShell784.addDisposeListener(this);
        this.aBoolean793 = true;
        this.method448();
        while (!this.aShell784.isDisposed()) {
            if (!this.aDisplay791.readAndDispatch()) {
                this.aDisplay791.sleep();
            }
        }
        this.aBoolean793 = false;
    }

    public final void method446() {
        if (this.aShell784 != null && !this.aShell784.isDisposed()) {
            this.aShell784.dispose();
        }
        this.aBoolean793 = false;
    }

    public final boolean method438() {
        return this.aBoolean793;
    }

    private void method449() {
        (this.aShell784 = new Shell(1264)).setText(UILocale.get("METHOD_FRAME_TITLE", "Methods"));
        this.aShell784.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        this.aShell784.setSize(new Point(752, 483));
        this.aShell784.setLayout(new GridLayout());
        this.method450();
    }

    private void method450() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = 4;
        (this.aSashForm785 = new SashForm(this.aShell784, 0)).setOrientation(512);
        this.method451();
        this.aSashForm785.setLayoutData(layoutData);
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
        (this.aComposite787 = new Composite(this.aSashForm785, 0)).setLayout(layout);
        (this.aButton783 = new Button(this.aComposite787, 8388608)).setText(UILocale.get("METHOD_FRAME_RESET_CALLS", "Reset Calls"));
        this.aButton783.addSelectionListener(new Class170(this));
        (this.aButton795 = new Button(this.aComposite787, 8388608)).setText(UILocale.get("METHOD_FRAME_EXPORT_BYTECODE", "Export ByteCode"));
        this.aButton795.addSelectionListener(new Class164(this));
        if (!Settings.enableMethodTrack)
            new Label(this.aComposite787, 8388608).setText("To track calls, enable it in System settings");
        (this.aTable788 = new Table(this.aComposite787, 67584)).setHeaderVisible(true);
        this.aTable788.setLayoutData(layoutData);
        this.aTable788.setLinesVisible(true);
        this.aTable788.addSelectionListener(new Class165(this));
        final TableColumn tableColumn;
        (tableColumn = new TableColumn(this.aTable788, 0)).setWidth(100);
        tableColumn.setText("Class");
        tableColumn.addSelectionListener(new Class166(this));
        final TableColumn tableColumn2;
        (tableColumn2 = new TableColumn(this.aTable788, 0)).setWidth(100);
        tableColumn2.setText("Name");
        tableColumn2.addSelectionListener(new Class167(this));
        final TableColumn tableColumn3;
        (tableColumn3 = new TableColumn(this.aTable788, 0)).setWidth(200);
        tableColumn3.setText("Description");
        tableColumn3.addSelectionListener(new Class160(this));
        final TableColumn tableColumn4;
        (tableColumn4 = new TableColumn(this.aTable788, 0)).setWidth(60);
        tableColumn4.setText("Code Size");
        tableColumn4.addSelectionListener(new Class99(this));
        final TableColumn tableColumn5;
        (tableColumn5 = new TableColumn(this.aTable788, 0)).setWidth(60);
        tableColumn5.setText("References");
        tableColumn5.addSelectionListener(new Class95(this));
        final TableColumn tableColumn6;
        (tableColumn6 = new TableColumn(this.aTable788, 0)).setWidth(60);
        tableColumn6.setText("Calls");
        tableColumn6.addSelectionListener(new Class73(this));
        final TableColumn tableColumn7;
        (tableColumn7 = new TableColumn(this.aTable788, 0)).setWidth(60);
        tableColumn7.setText("Total Time(ms)");
        tableColumn7.addSelectionListener(new Class72(this));
        final TableColumn tableColumn8;
        (tableColumn8 = new TableColumn(this.aTable788, 0)).setWidth(60);
        tableColumn8.setText("Average Time(ms)");
        tableColumn8.addSelectionListener(new Class75(this));
        final TableColumn tableColumn9;
        (tableColumn9 = new TableColumn(this.aTable788, 0)).setWidth(60);
        tableColumn9.setText("% Time");
        tableColumn9.addSelectionListener(new Class74(this));
    }

    private void method439(final TableItem[] array) {
        if (array == null || array.length < 1) {
            return;
        }
        final h.MethodInfo methodInfo;
        if ((methodInfo = (h.MethodInfo) array[0].getData()) != null) {
            this.aStyledText790.setText(methodInfo.method705(this.aButton797.getSelection(), this.aButton798.getSelection()));
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
        (this.aComposite796 = new Composite(this.aSashForm785, 0)).setLayout(layout);
        (this.aButton797 = new Button(this.aComposite796, 32)).setText("Show Line Numbers");
        this.aButton797.addSelectionListener(new Class77(this));
        (this.aButton798 = new Button(this.aComposite796, 32)).setText("Show Frames    ");
        this.aButton798.addSelectionListener(new Class76(this));
        (this.aText789 = new Text(this.aComposite796, 2048)).setLayoutData(layoutData);
        (this.aButton799 = new Button(this.aComposite796, 8388608)).setText(UILocale.get("METHOD_FRAME_SEARCH", "Search"));
        this.aButton799.addSelectionListener(new Class79(this));
        this.aButton799.addFocusListener(new Class78(this));
        (this.aStyledText790 = new StyledText(this.aComposite796, 2562)).setLayoutData(layoutData2);
        this.aStyledText790.setEditable(false);
        this.aStyledText790.setIndent(3);
    }

    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        this.method446();
    }

    static Table method441(final Class46 class46) {
        return class46.aTable788;
    }

    static Shell method442(final Class46 class46) {
        return class46.aShell784;
    }

    static void method443(final Class46 class46, final TableItem[] array) {
        class46.method439(array);
    }

    static void method444(final Class46 class46, final int n) {
        class46.method435(n);
    }

    static Text method434(final Class46 class46) {
        return class46.aText789;
    }

    static int method445(final Class46 class46, final int anInt786) {
        return class46.anInt786 = anInt786;
    }

    static int method437(final Class46 class46) {
        return class46.anInt786;
    }

    static StyledText method440(final Class46 class46) {
        return class46.aStyledText790;
    }

    static int method447(final Class46 class46) {
        return class46.anInt786++;
    }
}
