package emulator.ui.swt;

import org.eclipse.swt.events.*;

import javax.microedition.m3g.*;

final class M3GViewNodeRightClickListener extends MouseAdapter {
	private final M3GViewUI aClass90_802;

	M3GViewNodeRightClickListener(final M3GViewUI aClass90_802) {
		super();
		this.aClass90_802 = aClass90_802;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		if (mouseEvent.button == 3) {
			try {
				final Node node;
				if (M3GViewUI.method501(this.aClass90_802).getSelection() != null && (node = (Node) M3GViewUI.method501(this.aClass90_802).getSelection()[0].getData()) != null) {
					new Class5(node).method311(M3GViewUI.method499(aClass90_802));
				}
			} catch (Exception ignored) {
			}
		}
	}
}
