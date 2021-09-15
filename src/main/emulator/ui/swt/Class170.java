package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.custom.*;
import java.util.*;

final class Class170 extends SelectionAdapter
{
    Class170(final Class46 class46) {
        super();
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Enumeration<h.MethodInfo> elements = h.aHashtable1061.elements();
        while (elements.hasMoreElements()) {
            final h.MethodInfo methodInfo;
            (methodInfo = elements.nextElement()).anInt1182 = 0;
            methodInfo.aLong1179 = 0L;
            methodInfo.aFloat1175 = 0.0f;
            methodInfo.aFloat1180 = 0.0f;
        }
    }
}
