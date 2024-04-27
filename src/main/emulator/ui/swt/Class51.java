package emulator.ui.swt;

import org.eclipse.swt.events.*;

import javax.microedition.m3g.*;

final class Class51 extends MouseAdapter {
    private final Class90 aClass90_802;

    Class51(final Class90 aClass90_802) {
        super();
        this.aClass90_802 = aClass90_802;
    }

    public final void mouseDown(final MouseEvent mouseEvent) {
        if (mouseEvent.button == 3) {
            try {
                final Node node;
                if (Class90.method501(this.aClass90_802).getSelection() != null && (node = (Node) Class90.method501(this.aClass90_802).getSelection()[0].getData()) != null) {
                    new Class5(node).method311(Class90.method499(aClass90_802));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
