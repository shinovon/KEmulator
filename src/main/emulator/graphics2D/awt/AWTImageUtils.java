package emulator.graphics2D.awt;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AWTImageUtils {

	public static BufferedImage resizeProportional(BufferedImage img, int sw, int sh) {
		int iw = img.getWidth();
		int ih = img.getHeight();
		if (sw == iw && sh == ih)
			return img;
		return resizeProportional(null, img, sw, sh, null);
	}

	public static BufferedImage resizeProportional(BufferedImage dest, BufferedImage src, int sw, int sh, int[] outSize) {
		int iw = src.getWidth();
		int ih = src.getHeight();
		double widthRatio = (double) sw / (double) iw;
		double heightRatio = (double) sh / (double) ih;
		double ratio = Math.min(widthRatio, heightRatio);
		int tw = (int) (iw * ratio);
		int th = (int) (ih * ratio);
        if (outSize != null) {
            outSize[0] = tw;
            outSize[1] = th;
        }
		return resize(dest, src, tw, th);
	}

	public static BufferedImage resize(BufferedImage original, int w, int h) {
		return resize(null, original, w, h);
	}

	public static BufferedImage resize(BufferedImage dest, BufferedImage src, int w, int h) {
		if (w == -1) {
			w = (int) (((double) src.getWidth() / (double) src.getHeight()) * (double) h);
		}
		try {
			if (dest == null || dest.getWidth() < w || dest.getHeight() < h) {
				dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D g = dest.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(src, 0, 0, w, h, 0, 0, src.getWidth(),
					src.getHeight(), null);
			g.dispose();
			return dest;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return dest;
	}

}
