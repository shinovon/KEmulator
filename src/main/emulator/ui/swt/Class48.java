package emulator.ui.swt;

import org.eclipse.swt.widgets.*;
import javax.microedition.m3g.*;
import javax.microedition.m3g.Group;

final class Class48 implements Listener
{
    Class48(final Class90 class90) {
        super();
    }
    
    public final void handleEvent(final Event event) {
        final Widget widget;
        final TreeItem[] items;
        if ((items = ((TreeItem)(widget = event.item)).getItems()).length != 1) {
            return;
        }
        if (((Widget)items[0]).getData() != null) {
            return;
        }
        ((Widget)items[0]).dispose();
        if (widget.getData() instanceof Group) {
            final Group group = (Group)widget.getData();
            for (int i = 0; i < group.getChildCount(); ++i) {
                final Node child;
                final String name = (child = group.getChild(i)).getClass().getName();
                final Widget widget2;
                ((TreeItem)(widget2 = (Widget)new TreeItem((TreeItem)widget, 0))).setText(name.substring(name.lastIndexOf(".") + 1) + "_" + child.getUserID());
                widget2.setData((Object)child);
                if (child instanceof Group) {
                    new TreeItem((TreeItem)widget2, 0);
                }
            }
        }
    }
}
