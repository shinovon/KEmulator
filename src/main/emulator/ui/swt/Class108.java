package emulator.ui.swt;

import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public final class Class108
{
    private Shell aShell1069;
    private Display aDisplay1070;
    private CLabel aCLabel1071;
    private boolean aBoolean1072;
    
    public Class108() {
        super();
        this.aShell1069 = null;
        this.aCLabel1071 = null;
    }
    
    public final void method607(final Shell shell) {
        this.aDisplay1070 = Display.getCurrent();
        this.method612(shell);
        ((Control)this.aShell1069).setLocation(shell.getLocation().x + shell.getSize().x, shell.getLocation().y);
        this.aShell1069.open();
        this.aBoolean1072 = true;
        shell.forceActive();
        while (!((Widget)this.aShell1069).isDisposed()) {
            if (!this.aDisplay1070.readAndDispatch()) {
                this.aDisplay1070.sleep();
            }
        }
        this.aBoolean1072 = false;
    }
    
    public final void method608() {
        if (this.aShell1069 != null && !((Widget)this.aShell1069).isDisposed()) {
            this.aShell1069.dispose();
        }
    }
    
    public final void method609(final String text) {
        this.aCLabel1071.setText(text);
    }
    
    public final boolean method610() {
        return this.aBoolean1072;
    }
    
    public final Shell method611() {
        return this.aShell1069;
    }
    
    private void method612(final Shell shell) {
        final GridData layoutData;
        (layoutData = new GridData()).grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = 4;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = 4;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 1;
        layout.verticalSpacing = 0;
        layout.marginWidth = 1;
        layout.marginHeight = 1;
        layout.horizontalSpacing = 0;
        ((Composite)(this.aShell1069 = new Shell(shell, 8))).setLayout((Layout)layout);
        ((Control)this.aShell1069).setSize(new Point(130, 50));
        ((Control)this.aShell1069).setBackground(this.aDisplay1070.getSystemColor(2));
        (this.aCLabel1071 = new CLabel((Composite)this.aShell1069, 0)).setText("Pos(0,0)\nColr(0)\nRect(0,0,0,0)");
        ((Control)this.aCLabel1071).setLayoutData((Object)layoutData);
        this.aCLabel1071.setBackground(this.aDisplay1070.getSystemColor(13));
    }
}
