package emulator.ui;

import javax.microedition.lcdui.*;

public interface ICaret
{
    void focusItem(final TextField p0, final int p1, final int p2);
    
    void defocusItem(final TextField p0);
    
    int getCaretPosition();
}
