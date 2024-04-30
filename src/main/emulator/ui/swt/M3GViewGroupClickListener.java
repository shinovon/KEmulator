package emulator.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import javax.microedition.m3g.Node;
import javax.microedition.m3g.Group;

final class M3GViewGroupClickListener implements Listener {
    M3GViewGroupClickListener(final M3GViewUI m3gViewUI) {
        super();
    }

    public final void handleEvent(final Event event) {
        final TreeItem groupWidget = (TreeItem) event.item;
        final TreeItem[] items = groupWidget.getItems();

        if (items.length != 1) return;
        if (items[0].getData() != null) return;

        items[0].dispose();

        if (groupWidget.getData() instanceof Group) {
            final Group group = (Group) groupWidget.getData();

            for (int i = 0; i < group.getChildCount(); ++i) {
                final Node child = group.getChild(i);
                final String name = child.getClass().getName();
                final TreeItem childWidget = new TreeItem(groupWidget, 0);

                childWidget.setText(name.substring(name.lastIndexOf(".") + 1) + "_" + child.getUserID());
                if(!child.isRenderingEnabled()) childWidget.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

                childWidget.setData(child);
                if (child instanceof Group) {
                    new TreeItem((TreeItem) childWidget, 0);
                }
            }
        }
    }
}
