package emulator.ui.swt;

import emulator.debug.MemoryViewImage;
import emulator.debug.MemoryViewImageType;
import org.eclipse.swt.graphics.Rectangle;

import javax.microedition.lcdui.Image;

/**
 * {@link Image} wrapper with some metadata for Images View.
 */
public class ImageViewItem {
	public final Image image;
	public final MemoryViewImageType type;
	public final boolean released;
	/**
	 * Rect where image was drawn. May be null if image was not drawn.
	 */
	public Rectangle drawnRect;

	public ImageViewItem(Image image, boolean released) {
		this.image = image;
		this.released = released;
		if (image instanceof MemoryViewImage) {
			type = ((MemoryViewImage) image).type;
		} else {
			type = MemoryViewImageType.Lcdui;
		}
	}
}
