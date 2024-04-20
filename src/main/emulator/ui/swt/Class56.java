package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class56 extends MouseAdapter {
    private final Class90 aClass90_822;

    Class56(final Class90 aClass90_822) {
        super();
        this.aClass90_822 = aClass90_822;
    }

    public final void mouseDown(final MouseEvent mouseEvent) {
        Label_0205:
        {
            if (mouseEvent.button == 3) {
                Class90.method515(this.aClass90_822);
                Class90.method522(this.aClass90_822, 4);
                MenuItem menuItem = null;
                Label_0166:
                {
                    switch (Class90.method523(this.aClass90_822)) {
                        case 0: {
                            Class90.method534(this.aClass90_822).setSelection(false);
                            menuItem = Class90.method539(this.aClass90_822);
                            break Label_0166;
                        }
                        case 1: {
                            Class90.method539(this.aClass90_822).setSelection(false);
                            menuItem = Class90.method541(this.aClass90_822);
                            break Label_0166;
                        }
                        case 2: {
                            Class90.method541(this.aClass90_822).setSelection(false);
                            if (Class90.method500(this.aClass90_822) == 0) {
                                menuItem = Class90.method505(this.aClass90_822);
                                break Label_0166;
                            }
                            Class90.method515(this.aClass90_822);
                            break;
                        }
                        case 3: {
                            Class90.method505(this.aClass90_822).setSelection(false);
                            break;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    menuItem = Class90.method534(this.aClass90_822);
                }
                menuItem.setSelection(true);
            } else if (mouseEvent.button == 1) {
                Class90.method530(this.aClass90_822, mouseEvent.x);
                Class90.method535(this.aClass90_822, mouseEvent.y);
            }
        }
        ((Control) Class90.method231(this.aClass90_822)).forceFocus();
    }
}
