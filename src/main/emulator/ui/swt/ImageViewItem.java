package emulator.ui.swt;

import emulator.debug.MemoryViewImage;
import emulator.debug.MemoryViewImageType;
import emulator.graphics2D.CopyUtils;
import emulator.graphics2D.awt.ImageAWT;
import org.eclipse.swt.graphics.Rectangle;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Image2D;

/**
 * {@link Image} wrapper with some metadata for Images View.
 */
public class ImageViewItem {
	public final Image drawable;
	public final Object source;
	public final MemoryViewImageType type;
	public final boolean released;
	public final String type2;
	public final boolean mutable;
	public final org.eclipse.swt.graphics.Image cache;
	/**
	 * Rect where image was drawn. May be null if image was not drawn.
	 */
	public Rectangle drawnRect;

	public ImageViewItem(Image image, boolean released) {
		this.drawable = image;
		this.released = released;
		if (image instanceof MemoryViewImage) {
			source = ((MemoryViewImage) image).source;
			type = ((MemoryViewImage) image).type;
			if (source instanceof Image2D) {
				int t = ((Image2D) source).getFormat();
				type2 = m3gTypes[t - Image2D.ALPHA];
			} else {
				type2 = ""; //TODO i'm not familiar with micro3d
			}
			mutable = true;
			cache = null;
		} else {
			source = image;
			type = MemoryViewImageType.LCDUI;
			mutable = image.isMutable();
			if (mutable) {
				type2 = "Mutable";
				cache = null;
			} else {
				type2 = "Immutable";
				if (image.getImpl() instanceof ImageAWT)
					cache = new org.eclipse.swt.graphics.Image(null, CopyUtils.toSwt(((ImageAWT) image.getImpl()).getBufferedImage()));
				else
					cache = null;
			}
		}
	}

	public String getCaption() {
		return type.toString();
	}

	private final static String[] m3gTypes = new String[]{"A", "L", "LA", "RGB", "RGBA"};
}
