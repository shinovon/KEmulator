package emulator.ui.swt;

import emulator.*;
import org.eclipse.swt.widgets.*;

final class Class193 implements Runnable {
    private final Property aClass38_1497;

    Class193(final Property aClass38_1497) {
        super();
        this.aClass38_1497 = aClass38_1497;
    }

    public final void run() {
        int selectionIndex;
        if ((selectionIndex = Property.method376(this.aClass38_1497).getSelectionIndex()) == 0) {
            return;
        }
        final String method749;
        String string;
        if (!(string = (method749 = Controllers.method749())).equalsIgnoreCase("LEFT") && !string.equalsIgnoreCase("RIGHT") && !string.equalsIgnoreCase("UP") && !string.equalsIgnoreCase("DOWN")) {
            string = "B_" + string;
        }
        --selectionIndex;
        Text text;
        if (((Control) Property.method405(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 0, method749);
            text = Property.method405(this.aClass38_1497);
        } else if (((Control) Property.method380(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 1, method749);
            text = Property.method380(this.aClass38_1497);
        } else if (((Control) Property.method385(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 2, method749);
            text = Property.method385(this.aClass38_1497);
        } else if (((Control) Property.method388(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 3, method749);
            text = Property.method388(this.aClass38_1497);
        } else if (((Control) Property.method391(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 4, method749);
            text = Property.method391(this.aClass38_1497);
        } else if (((Control) Property.method394(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 5, method749);
            text = Property.method394(this.aClass38_1497);
        } else if (((Control) Property.method397(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 6, method749);
            text = Property.method397(this.aClass38_1497);
        } else if (((Control) Property.method399(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 7, method749);
            text = Property.method399(this.aClass38_1497);
        } else if (((Control) Property.method401(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 8, method749);
            text = Property.method401(this.aClass38_1497);
        } else if (((Control) Property.method403(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 9, method749);
            text = Property.method403(this.aClass38_1497);
        } else if (((Control) Property.method407(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 10, method749);
            text = Property.method407(this.aClass38_1497);
        } else if (((Control) Property.method409(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 11, method749);
            text = Property.method409(this.aClass38_1497);
        } else if (((Control) Property.method411(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 14, method749);
            text = Property.method411(this.aClass38_1497);
        } else if (((Control) Property.method413(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 15, method749);
            text = Property.method413(this.aClass38_1497);
        } else if (((Control) Property.method415(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 12, method749);
            text = Property.method415(this.aClass38_1497);
        } else if (((Control) Property.method417(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 13, method749);
            text = Property.method417(this.aClass38_1497);
        } else if (((Control) Property.method419(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 16, method749);
            text = Property.method419(this.aClass38_1497);
        } else if (((Control) Property.method366(this.aClass38_1497)).isFocusControl()) {
            Controllers.bind(selectionIndex, 17, method749);
            text = Property.method366(this.aClass38_1497);
        } else {
            if (!((Control) Property.method377(this.aClass38_1497)).isFocusControl()) {
                return;
            }
            Controllers.bind(selectionIndex, 18, method749);
            text = Property.method377(this.aClass38_1497);
        }
        text.setText(string);
    }
}
