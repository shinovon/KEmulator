package emulator.graphics2D.swt;

import emulator.graphics2D.IFont;
import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.ITransform;
import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

public final class Graphics2DSWT implements IGraphics2D {
	GC gc;
	Color color;
	int width;
	int height;

	public Graphics2DSWT(final Image image) {
		super();
		this.gc = new GC(image);
		gc.setAntialias(SWT.OFF);
		final Transform transform = new Transform(null);
		this.gc.getTransform(transform);
		transform.dispose();
		this.color = new Color(null, 0, 0, 0);
		this.width = image.getImageData().width;
		this.height = image.getImageData().height;
	}

	public final GC gc() {
		return this.gc;
	}

	public final void finalize() {
		EmulatorImpl.asyncExec(() -> {
			try {
				if (!gc.isDisposed()) {
					gc.dispose();
				}
				if (!color.isDisposed()) {
					color.dispose();
				}
			} catch (Exception ignored) {
			}
		});
	}

	public final void setAlpha(final int alpha) {
		this.gc.setAlpha(alpha);
	}

	public final void clipRect(final int n, final int n2, final int n3, final int n4) {
		final Rectangle intersection = this.gc.getClipping().intersection(new Rectangle(n, n2, n3, n4));
		this.gc.setClipping(intersection.x - 1, intersection.y - 1, intersection.width + 1, intersection.height + 1);
	}

	public final void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
//        this.gc.setForeground(this.color);
		this.gc.drawArc(n, n2, n3, n4, n5, n6);
	}

	public final void drawLine(final int n, final int n2, final int n3, final int n4) {
//        this.gc.setForeground(this.color);
		this.gc.drawLine(n, n2, n3, n4);
	}

	public final void drawRect(final int n, final int n2, final int n3, final int n4) {
//        this.gc.setForeground(this.color);
		this.gc.drawRectangle(n, n2, n3, n4);
	}

	public final void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
//        this.gc.setForeground(this.color);
		this.gc.drawRoundRectangle(n, n2, n3, n4, n5, n6);
	}

	public final void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
//        this.gc.setBackground(this.color);
		this.gc.fillArc(n, n2, n3, n4, n5, n6);
	}

	public final void fillRect(final int n, final int n2, final int n3, final int n4) {
//    	this.gc.setBackground(this.color);
		this.gc.fillRectangle(n, n2, n3, n4);
	}

	public final void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
//        this.gc.setBackground(this.color);
		this.gc.fillRoundRectangle(n, n2, n3, n4, n5, n6);
	}

	public final void fillPolygon(final int[] array) {
//        this.gc.setBackground(this.color);
		this.gc.fillPolygon(array);
	}

	public final void drawPolygon(final int[] array) {
		this.gc.drawPolygon(array);
	}

	public final void setFont(final IFont font) {
		this.gc.setFont(((FontSWT) font).getSWTFont());
	}

	public final void drawString(final String s, final int n, final int n2) {
		FontMetrics f = gc.getFontMetrics();
		this.gc.drawString(s, n, n2 - f.getAscent() - 1, true);
	}

	public final void drawImage(final IImage image, final int n, final int n2) {
		((ImageSWT) image).method12(this.gc, n, n2);
	}

	public final void drawImage(final IImage image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
		((ImageSWT) image).copyToScreen(this.gc, n, n2, n3, n4, n5, n6, n7, n8);
	}

	public final void setClip(final int n, final int n2, final int n3, final int n4) {
		this.gc.setClipping(n, n2, n3, n4);
	}

	public final void setColor(final int n, final boolean b) {
		final int a = n >> 24 & 0xFF;
		final int n2 = n >> 16 & 0xFF;
		final int n3 = n >> 8 & 0xFF;
		final int n4 = n & 0xFF;
		this.color.dispose();
		this.color = new Color(null, n2, n3, n4);
		gc.setAlpha(b ? a : a > 0 ? a : 255);
		gc.setBackground(this.color);
		gc.setForeground(this.color);
	}

	public final void setColor(final int n, final int n2, final int n3) {
		this.color.dispose();
		this.color = new Color(null, n, n2, n3);
		gc.setBackground(this.color);
		gc.setForeground(this.color);
	}

	public final int getClipHeight() {
		if (this.gc.isClipped()) {
			return this.gc.getClipping().height;
		}
		return this.height;
	}

	public final int getClipWidth() {
		if (this.gc.isClipped()) {
			return this.gc.getClipping().width;
		}
		return this.width;
	}

	public final int getClipX() {
		if (this.gc.isClipped()) {
			return this.gc.getClipping().x;
		}
		return 0;
	}

	public final int getClipY() {
		if (this.gc.isClipped()) {
			return this.gc.getClipping().y;
		}
		return 0;
	}

	public final int getColor() {
		return
				((gc.getAlpha() & 0xFF) << 24) +
						((this.color.getRed() & 0xFF) << 16) +
						((this.color.getGreen() & 0xFF) << 8) +
						(this.color.getBlue() & 0xFF);
	}

	public final int getColorRed() {
		return this.color.getRed();
	}

	public final int getColorGreen() {
		return this.color.getGreen();
	}

	public final int getColorBlue() {
		return this.color.getBlue();
	}

	public final float[] RGBtoHSB(final int n, final int n2, final int n3) {
		final float[] array = new float[3];
		int n4 = (n <= n2) ? n2 : n;
		if (n3 > n4) {
			n4 = n3;
		}
		int n5 = (n >= n2) ? n2 : n;
		if (n3 < n5) {
			n5 = n3;
		}
		final float n6 = n4 / 255.0f;
		final float n7;
		float n14 = 0.0f;
		Label_0184:
		{
			float n8;
			if ((n7 = ((n4 != 0) ? ((n4 - n5) / n4) : 0.0f)) == 0.0f) {
				n8 = 0.0f;
			} else {
				final float n9 = (n4 - n) / (n4 - n5);
				final float n10 = (n4 - n2) / (n4 - n5);
				final float n11 = (n4 - n3) / (n4 - n5);
				float n12;
				float n13;
				if (n == n4) {
					n12 = n11;
					n13 = n10;
				} else if (n2 == n4) {
					n12 = 2.0f + n9;
					n13 = n11;
				} else {
					n12 = 4.0f + n10;
					n13 = n9;
				}
				if ((n14 = (n12 - n13) / 6.0f) >= 0.0f) {
					break Label_0184;
				}
				n8 = n14 + 1.0f;
			}
			n14 = n8;
		}
		array[0] = n14;
		array[1] = n7;
		array[2] = n6;
		return array;
	}

	public final ITransform getTransform() {
		final Transform transform = new Transform(null);
		this.gc.getTransform(transform);
		final TransformSWT b = new TransformSWT(transform);
		transform.dispose();
		return b;
	}

	public final void setTransform(final ITransform transform) {
		final Transform t = ((TransformSWT) transform).create();
		this.gc.setTransform(t);
		t.dispose();
	}

	public final void transform(final ITransform transform) {
		final Transform transform2 = new Transform(null);
		this.gc.getTransform(transform2);
		final TransformSWT b = new TransformSWT(transform2);
		transform2.dispose();
		b.transform(transform);
		final Transform t = b.create();
		this.gc.setTransform(t);
		t.dispose();
	}

	public final void translate(final int n, final int n2) {
		final Transform transform = new Transform(null);
		this.gc.getTransform(transform);
		transform.translate((float) n, (float) n2);
		this.gc.setTransform(transform);
		transform.dispose();
	}

	public final void setStrokeStyle(final int n) {
		GC gc;
		int lineStyle;
		if (n == 0) {
			gc = this.gc;
			lineStyle = 1;
		} else {
			if (n != 1) {
				return;
			}
			gc = this.gc;
			lineStyle = 3;
		}
		gc.setLineStyle(lineStyle);
	}

	public final int getStrokeStyle() {
		final boolean b;
		return (b = (this.gc.getLineStyle() != 1)) ? 1 : 0;
	}

	public IFont getFont() {
		return null;
	}

	public void reset() {
		this.gc.setTransform(new Transform(null));
		this.gc.setClipping(0, 0, width, height);
	}
}
