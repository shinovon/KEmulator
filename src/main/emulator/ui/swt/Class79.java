package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class79 extends SelectionAdapter
{
    private final Class46 aClass46_863;
    
    Class79(final Class46 aClass46_863) {
        super();
        this.aClass46_863 = aClass46_863;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final String text;
        if ((text = Class46.method434(this.aClass46_863).getText()).trim().length() > 0) {
            Class46.method445(this.aClass46_863, Class46.method440(this.aClass46_863).getText().indexOf(text, Class46.method437(this.aClass46_863)));
            if (Class46.method437(this.aClass46_863) != -1) {
                Class46.method440(this.aClass46_863).setSelection(Class46.method437(this.aClass46_863), Class46.method437(this.aClass46_863) + text.length());
            }
            Class46.method447(this.aClass46_863);
        }
    }
}
