package emulator.ui.swt;

import org.eclipse.swt.layout.*;
import emulator.*;
import org.eclipse.swt.widgets.*;

public final class Class30 extends Dialog {
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
        (shell = new Shell(this.getParent(), this.getStyle())).setText(this.getText());
        this.method341(shell);
        shell.setLocation(this.getParent().getBounds().x + (this.getParent().getBounds().width - shell.getSize().x) / 2, this.getParent().getBounds().y + (this.getParent().getBounds().height - shell.getSize().y) / 2);
        shell.open();
        final Display display = this.getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void method341(final Shell shell) {
        shell.setLayout(new GridLayout(2, true));
        final GridData layoutData = new GridData(768);
        Text text;
        float n;
        if (this.anInt604 == 0) {
            final Group group;
            (group = new Group(shell, 0)).setText(UILocale.get("M3G_VIEW_DIALOG_FOVY", "Fovy"));
            layoutData.horizontalSpan = 2;
            group.setLayoutData(layoutData);
            group.setLayout(new GridLayout(1, true));
            (this.aText605 = new Text(group, 133120)).setLayoutData(new GridData(768));
            text = this.aText605;
            n = this.aFloat603;
        } else if (this.anInt604 == 1) {
            final Group group2;
            (group2 = new Group(shell, 0)).setText(UILocale.get("M3G_VIEW_DIALOG_NEAR", "Near"));
            group2.setLayoutData(layoutData);
            group2.setLayout(new GridLayout(1, true));
            (this.aText607 = new Text(group2, 133120)).setLayoutData(layoutData);
            this.aText607.setText(String.valueOf(this.aFloat606));
            final Group group3;
            (group3 = new Group(shell, 0)).setText(UILocale.get("M3G_VIEW_DIALOG_FAR", "Far"));
            group3.setLayoutData(layoutData);
            group3.setLayout(new GridLayout(1, true));
            (this.aText609 = new Text(group3, 133120)).setLayoutData(layoutData);
            text = this.aText609;
            n = this.aFloat608;
        } else {
            final Group group4;
            (group4 = new Group(shell, 0)).setText("X");
            group4.setLayoutData(layoutData);
            group4.setLayout(new GridLayout(1, true));
            (this.aText611 = new Text(group4, 133120)).setLayoutData(layoutData);
            this.aText611.setText(String.valueOf(this.aFloat610));
            final Group group5;
            (group5 = new Group(shell, 0)).setText("Y");
            group5.setLayoutData(layoutData);
            group5.setLayout(new GridLayout(1, true));
            (this.aText613 = new Text(group5, 133120)).setLayoutData(layoutData);
            this.aText613.setText(String.valueOf(this.aFloat612));
            final Group group6;
            (group6 = new Group(shell, 0)).setText("Z");
            final GridData layoutData2;
            (layoutData2 = new GridData(768)).horizontalSpan = 2;
            group6.setLayoutData(layoutData2);
            group6.setLayout(new GridLayout(1, true));
            (this.aText615 = new Text(group6, 133120)).setLayoutData(new GridData(768));
            text = this.aText615;
            n = this.aFloat614;
        }
        text.setText(String.valueOf(n));
        final Button button;
        (button = new Button(shell, 8388616)).setText(UILocale.get("DIALOG_OK", "OK"));
        final GridData layoutData3;
        (layoutData3 = new GridData(768)).widthHint = 80;
        button.setLayoutData(layoutData3);
        button.addSelectionListener(new Class80(this, shell));
        final Button button2;
        (button2 = new Button(shell, 8388616)).setText(UILocale.get("DIALOG_CANCEL", "Cancel"));
        final GridData layoutData4;
        (layoutData4 = new GridData(768)).widthHint = 80;
        button2.setLayoutData(layoutData4);
        button2.addSelectionListener(new Class81(this, shell));
        shell.pack();
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
