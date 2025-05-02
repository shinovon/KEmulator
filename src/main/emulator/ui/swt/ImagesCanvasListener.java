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
		boolean selectionResult = this.mv.selectImageClicked(mouseEvent.x, mouseEvent.y);
		if (mouseEvent.button == 3) {
			this.mv.imagesCanvas.forceFocus();
			Canvas canvas = this.mv.imagesCanvas;
			Menu menu = selectionResult ? mv.menuSaveOne : mv.menuSaveAll;
			canvas.setMenu(menu);
		}
	}
}
