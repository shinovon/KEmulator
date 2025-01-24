package emulator.graphics2D.awt;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AWTImageUtils {

    public static BufferedImage resizeProportional(BufferedImage img, int sw, int sh) {
        int iw = img.getWidth();
        int ih = img.getHeight();
        if (sw == iw && sh == ih)
            return img;
        double widthRatio = (double) sw / (double) iw;
        double heightRatio = (double) sh / (double) ih;
        double ratio = Math.min(widthRatio, heightRatio);
        int tw = (int) (iw * ratio);
        int th = (int) (ih * ratio);
        return resize(img, tw, th);
    }

    public static BufferedImage resize(BufferedImage original, int w, int h) {
        if (w == -1) {
            w = (int) (((double) original.getWidth() / (double) original.getHeight()) * (double) h);
        }
        try {
            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(original, 0, 0, w, h, 0, 0, original.getWidth(),
                    original.getHeight(), null);
            g.dispose();
            return resized;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
