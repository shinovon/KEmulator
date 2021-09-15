package emulator.ui.swt;

import java.util.*;
import javax.microedition.lcdui.*;

final class Class15 implements Comparator
{
    private final Class110 aClass110_580;
    
    Class15(final Class110 aClass110_580) {
        super();
        this.aClass110_580 = aClass110_580;
    }
    
    public final int compare(final Object o, final Object o2) {
        int n = 0;
        final Image image = (Image)o;
        final Image image2 = (Image)o2;
        Label_0142: {
            int usedCount = 0;
            int usedCount2 = 0;
            switch (Class110.method645(this.aClass110_580)) {
                case 0: {
                    for (int i = 0; i < Class110.method634().size(); ++i) {
                        if (o == Class110.method634().get(i)) {
                            n = 1;
                            break;
                        }
                        if (o2 == Class110.method634().get(i)) {
                            n = -1;
                            break;
                        }
                    }
                    break Label_0142;
                }
                case 1: {
                    usedCount = image.getWidth() * image.getHeight();
                    usedCount2 = image2.getWidth() * image2.getHeight();
                    break;
                }
                case 2: {
                    usedCount = image.getUsedCount();
                    usedCount2 = image2.getUsedCount();
                    break;
                }
            }
            n = usedCount - usedCount2;
        }
        if (Class110.method660(this.aClass110_580)) {
            return n;
        }
        return -n;
    }
}
