package emulator.ui;

import javax.microedition.lcdui.*;

public interface ICaret
{
    void foucsItem(final TextField p0, final int p1, final int p2);
    
    void defoucsItem(final TextField p0);
    
    int getCaretPosition();
}
