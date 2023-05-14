package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class109 extends SelectionAdapter
{
    private final Property aClass38_1078;
    
    Class109(final Property aClass38_1078) {
        super();
        this.aClass38_1078 = aClass38_1078;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Settings.frameRate = Property.method370(this.aClass38_1078).getSelection();
        Property.method363(this.aClass38_1078).setText(UILocale.uiText("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((Settings.frameRate > 120) ? "\u221e" : String.valueOf(Settings.frameRate)));
    }
}
