package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Menu;

final class ImagesCanvasListener extends MouseAdapter {
	private final MemoryView mv;

	ImagesCanvasListener(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		this.mv.imagesCanvas.forceFocus();
		if (mouseEvent.button == 3) {
			Canvas canvas = this.mv.imagesCanvas;
			Menu menu;
			if (this.mv.selectImageClicked(mouseEvent.x, mouseEvent.y)) {
				menu = this.mv.menuSaveOne;
			} else {
				menu = this.mv.menuSaveAll;
			}
			canvas.setMenu(menu);
		}
	}
}
