package emulator.ui.swt;

import emulator.*;
import org.eclipse.swt.widgets.*;

final class Class193 implements Runnable
{
    private final Class38 aClass38_1497;
    
    Class193(final Class38 aClass38_1497) {
        super();
        this.aClass38_1497 = aClass38_1497;
    }
    
    public final void run() {
        int selectionIndex;
        if ((selectionIndex = Class38.method376(this.aClass38_1497).getSelectionIndex()) == 0) {
            return;
        }
        final String method749;
        String string;
        if (!(string = (method749 = Controllers.method749())).equalsIgnoreCase("LEFT") && !string.equalsIgnoreCase("RIGHT") && !string.equalsIgnoreCase("UP") && !string.equalsIgnoreCase("DOWN")) {
            string = "B_" + string;
        }
        --selectionIndex;
        Text text;
        if (((Control)Class38.method405(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 0, method749);
            text = Class38.method405(this.aClass38_1497);
        }
        else if (((Control)Class38.method380(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 1, method749);
            text = Class38.method380(this.aClass38_1497);
        }
        else if (((Control)Class38.method385(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 2, method749);
            text = Class38.method385(this.aClass38_1497);
        }
        else if (((Control)Class38.method388(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 3, method749);
            text = Class38.method388(this.aClass38_1497);
        }
        else if (((Control)Class38.method391(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 4, method749);
            text = Class38.method391(this.aClass38_1497);
        }
        else if (((Control)Class38.method394(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 5, method749);
            text = Class38.method394(this.aClass38_1497);
        }
        else if (((Control)Class38.method397(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 6, method749);
            text = Class38.method397(this.aClass38_1497);
        }
        else if (((Control)Class38.method399(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 7, method749);
            text = Class38.method399(this.aClass38_1497);
        }
        else if (((Control)Class38.method401(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 8, method749);
            text = Class38.method401(this.aClass38_1497);
        }
        else if (((Control)Class38.method403(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 9, method749);
            text = Class38.method403(this.aClass38_1497);
        }
        else if (((Control)Class38.method407(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 10, method749);
            text = Class38.method407(this.aClass38_1497);
        }
        else if (((Control)Class38.method409(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 11, method749);
            text = Class38.method409(this.aClass38_1497);
        }
        else if (((Control)Class38.method411(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 14, method749);
            text = Class38.method411(this.aClass38_1497);
        }
        else if (((Control)Class38.method413(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 15, method749);
            text = Class38.method413(this.aClass38_1497);
        }
        else if (((Control)Class38.method415(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 12, method749);
            text = Class38.method415(this.aClass38_1497);
        }
        else if (((Control)Class38.method417(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 13, method749);
            text = Class38.method417(this.aClass38_1497);
        }
        else if (((Control)Class38.method419(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 16, method749);
            text = Class38.method419(this.aClass38_1497);
        }
        else if (((Control)Class38.method366(this.aClass38_1497)).isFocusControl()) {
            Controllers.method743(selectionIndex, 17, method749);
            text = Class38.method366(this.aClass38_1497);
        }
        else {
            if (!((Control)Class38.method377(this.aClass38_1497)).isFocusControl()) {
                return;
            }
            Controllers.method743(selectionIndex, 18, method749);
            text = Class38.method377(this.aClass38_1497);
        }
        text.setText(string);
    }
}
