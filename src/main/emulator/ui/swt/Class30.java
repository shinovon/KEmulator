package emulator.ui.swt;

import org.eclipse.swt.layout.*;
import emulator.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public final class Class30 extends Dialog
{
    float aFloat603;
    float aFloat606;
    float aFloat608;
    float aFloat610;
    float aFloat612;
    float aFloat614;
    private int anInt604;
    private Text aText605;
    private Text aText607;
    private Text aText609;
    private Text aText611;
    private Text aText613;
    private Text aText615;
    
    public Class30(final Shell shell, final int anInt604) {
        super(shell, 67680);
        this.anInt604 = anInt604;
        this.aFloat606 = 1.0f;
        this.aFloat608 = 10000.0f;
        this.aFloat603 = 50.0f;
    }
    
    public final void method337(final float aFloat603) {
        this.aFloat603 = aFloat603;
    }
    
    public final void method338(final float aFloat606, final float aFloat607) {
        this.aFloat606 = aFloat606;
        this.aFloat608 = aFloat607;
    }
    
    public final void method339(final float aFloat610, final float aFloat611, final float aFloat612) {
        this.aFloat610 = aFloat610;
        this.aFloat612 = aFloat611;
        this.aFloat614 = aFloat612;
    }
    
    public final void method340() {
        final Shell shell;
        ((Decorations)(shell = new Shell(this.getParent(), this.getStyle()))).setText(this.getText());
        this.method341(shell);
        ((Control)shell).setLocation(this.getParent().getBounds().x + (this.getParent().getBounds().width - shell.getSize().x) / 2, this.getParent().getBounds().y + (this.getParent().getBounds().height - shell.getSize().y) / 2);
        shell.open();
        final Display display = ((Widget)this.getParent()).getDisplay();
        while (!((Widget)shell).isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
    
    private void method341(final Shell shell) {
        ((Composite)shell).setLayout((Layout)new GridLayout(2, true));
        final GridData layoutData = new GridData(768);
        Text text;
        float n;
        if (this.anInt604 == 0) {
            final Group group;
            (group = new Group((Composite)shell, 0)).setText(UILocale.uiText("M3G_VIEW_DIALOG_FOVY", "Fovy"));
            layoutData.horizontalSpan = 2;
            ((Control)group).setLayoutData((Object)layoutData);
            ((Composite)group).setLayout((Layout)new GridLayout(1, true));
            ((Control)(this.aText605 = new Text((Composite)group, 133120))).setLayoutData((Object)new GridData(768));
            text = this.aText605;
            n = this.aFloat603;
        }
        else if (this.anInt604 == 1) {
            final Group group2;
            (group2 = new Group((Composite)shell, 0)).setText(UILocale.uiText("M3G_VIEW_DIALOG_NEAR", "Near"));
            ((Control)group2).setLayoutData((Object)layoutData);
            ((Composite)group2).setLayout((Layout)new GridLayout(1, true));
            ((Control)(this.aText607 = new Text((Composite)group2, 133120))).setLayoutData((Object)layoutData);
            this.aText607.setText(String.valueOf(this.aFloat606));
            final Group group3;
            (group3 = new Group((Composite)shell, 0)).setText(UILocale.uiText("M3G_VIEW_DIALOG_FAR", "Far"));
            ((Control)group3).setLayoutData((Object)layoutData);
            ((Composite)group3).setLayout((Layout)new GridLayout(1, true));
            ((Control)(this.aText609 = new Text((Composite)group3, 133120))).setLayoutData((Object)layoutData);
            text = this.aText609;
            n = this.aFloat608;
        }
        else {
            final Group group4;
            (group4 = new Group((Composite)shell, 0)).setText("X");
            ((Control)group4).setLayoutData((Object)layoutData);
            ((Composite)group4).setLayout((Layout)new GridLayout(1, true));
            ((Control)(this.aText611 = new Text((Composite)group4, 133120))).setLayoutData((Object)layoutData);
            this.aText611.setText(String.valueOf(this.aFloat610));
            final Group group5;
            (group5 = new Group((Composite)shell, 0)).setText("Y");
            ((Control)group5).setLayoutData((Object)layoutData);
            ((Composite)group5).setLayout((Layout)new GridLayout(1, true));
            ((Control)(this.aText613 = new Text((Composite)group5, 133120))).setLayoutData((Object)layoutData);
            this.aText613.setText(String.valueOf(this.aFloat612));
            final Group group6;
            (group6 = new Group((Composite)shell, 0)).setText("Z");
            final GridData layoutData2;
            (layoutData2 = new GridData(768)).horizontalSpan = 2;
            ((Control)group6).setLayoutData((Object)layoutData2);
            ((Composite)group6).setLayout((Layout)new GridLayout(1, true));
            ((Control)(this.aText615 = new Text((Composite)group6, 133120))).setLayoutData((Object)new GridData(768));
            text = this.aText615;
            n = this.aFloat614;
        }
        text.setText(String.valueOf(n));
        final Button button;
        (button = new Button((Composite)shell, 8388616)).setText(UILocale.uiText("DIALOG_OK", "OK"));
        final GridData layoutData3;
        (layoutData3 = new GridData(768)).widthHint = 80;
        ((Control)button).setLayoutData((Object)layoutData3);
        button.addSelectionListener((SelectionListener)new Class80(this, shell));
        final Button button2;
        (button2 = new Button((Composite)shell, 8388616)).setText(UILocale.uiText("DIALOG_CANCEL", "Cancel"));
        final GridData layoutData4;
        (layoutData4 = new GridData(768)).widthHint = 80;
        ((Control)button2).setLayoutData((Object)layoutData4);
        button2.addSelectionListener((SelectionListener)new Class81(this, shell));
        ((Control)shell).pack();
    }
    
    static int method342(final Class30 class30) {
        return class30.anInt604;
    }
    
    static Text method343(final Class30 class30) {
        return class30.aText605;
    }
    
    static Text method344(final Class30 class30) {
        return class30.aText607;
    }
    
    static Text method345(final Class30 class30) {
        return class30.aText609;
    }
    
    static Text method346(final Class30 class30) {
        return class30.aText611;
    }
    
    static Text method347(final Class30 class30) {
        return class30.aText613;
    }
    
    static Text method348(final Class30 class30) {
        return class30.aText615;
    }
}
