package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Menu;

final class ImagesCanvasListener extends MouseAdapter implements SelectionListener, PaintListener {
	private final MemoryView mv;
	private final Canvas canvas;

	ImagesCanvasListener(final MemoryView mv, final Canvas canvas) {
		super();
		this.mv = mv;
		this.canvas = canvas;
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		mv.selectImageClicked(mouseEvent.x, mouseEvent.y);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		canvas.redraw();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public final void paintControl(final PaintEvent paintEvent) {
		mv.imagesCanvasScroll = canvas.getVerticalBar().getSelection();
		mv.paintImagesCanvas(paintEvent.gc);
	}
}
