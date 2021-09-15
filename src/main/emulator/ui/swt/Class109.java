package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class109 extends SelectionAdapter
{
    private final Class38 aClass38_1078;
    
    Class109(final Class38 aClass38_1078) {
        super();
        this.aClass38_1078 = aClass38_1078;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Settings.frameRate = Class38.method370(this.aClass38_1078).getSelection();
        Class38.method363(this.aClass38_1078).setText(UILocale.uiText("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((Settings.frameRate > 50) ? "\u221e" : String.valueOf(Settings.frameRate)));
    }
}
