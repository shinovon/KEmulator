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
		MemoryView.method669(this.mv, this.mv.imagesCanvas.getSize().x);
		MemoryView.method675(this.mv, this.mv.imagesCanvas.getSize().y);
		MemoryView.method680(this.mv, this.mv.imagesCanvas.getVerticalBar().getSelection());
		this.mv.paintImagesCanvas(paintEvent.gc);
	}
}
