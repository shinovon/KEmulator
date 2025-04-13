package emulator.ui.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

final class ImagesCanvasRepainter implements PaintListener {
	private final MemoryView mv;

	ImagesCanvasRepainter(final MemoryView mv) {
		super();
		this.mv = mv;
	}

	public final void paintControl(final PaintEvent paintEvent) {
		this.mv.imagesCanvasWidth = this.mv.imagesCanvas.getSize().x;
		this.mv.imagesCanvasHeight = this.mv.imagesCanvas.getSize().y;
		this.mv.imagesCanvasScroll = this.mv.imagesCanvas.getVerticalBar().getSelection();
		this.mv.paintImagesCanvas(paintEvent.gc);
	}
}
