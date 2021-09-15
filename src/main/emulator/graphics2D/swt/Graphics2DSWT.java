package emulator.graphics2D.swt;

import org.eclipse.swt.graphics.*;
import emulator.graphics2D.*;

public final class Graphics2DSWT implements IGraphics2D
{
    GC aGC529;
    Color aColor530;
    int anInt10;
    int anInt11;
    
    public Graphics2DSWT(final Image image) {
        super();
        this.aGC529 = new GC((Drawable)image);
        final Transform transform = new Transform((Device)null);
        this.aGC529.getTransform(transform);
        transform.dispose();
        this.aColor530 = new Color((Device)null, 0, 0, 0);
        this.anInt10 = image.getImageData().width;
        this.anInt11 = image.getImageData().height;
    }
    
    public final GC method299() {
        return this.aGC529;
    }
    
    public final void finalize() {
        if (!this.aGC529.isDisposed()) {
            this.aGC529.dispose();
        }
        if (!this.aColor530.isDisposed()) {
            this.aColor530.dispose();
        }
    }
    
    public final void setAlpha(final int alpha) {
        this.aGC529.setAlpha(alpha);
    }
    
    public final void clipRect(final int n, final int n2, final int n3, final int n4) {
        final Rectangle intersection = this.aGC529.getClipping().intersection(new Rectangle(n, n2, n3, n4));
        this.aGC529.setClipping(intersection.x - 1, intersection.y - 1, intersection.width + 1, intersection.height + 1);
    }
    
    public final void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.aGC529.setForeground(this.aColor530);
        this.aGC529.drawArc(n, n2, n3, n4, n5, n6);
    }
    
    public final void drawLine(final int n, final int n2, final int n3, final int n4) {
        this.aGC529.setForeground(this.aColor530);
        this.aGC529.drawLine(n, n2, n3, n4);
    }
    
    public final void drawRect(final int n, final int n2, final int n3, final int n4) {
        this.aGC529.setForeground(this.aColor530);
        this.aGC529.drawRectangle(n, n2, n3, n4);
    }
    
    public final void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.aGC529.setForeground(this.aColor530);
        this.aGC529.drawRoundRectangle(n, n2, n3, n4, n5, n6);
    }
    
    public final void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.aGC529.setBackground(this.aColor530);
        this.aGC529.fillArc(n, n2, n3, n4, n5, n6);
    }
    
    public final void fillRect(final int n, final int n2, final int n3, final int n4) {
        this.aGC529.setBackground(this.aColor530);
        this.aGC529.fillRectangle(n, n2, n3, n4);
    }
    
    public final void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.aGC529.setBackground(this.aColor530);
        this.aGC529.fillRoundRectangle(n, n2, n3, n4, n5, n6);
    }
    
    public final void fillPolygon(final int[] array) {
        this.aGC529.setBackground(this.aColor530);
        this.aGC529.fillPolygon(array);
    }
    
    public final void drawPolygon(final int[] array) {
        this.aGC529.setForeground(this.aColor530);
        this.aGC529.drawPolygon(array);
    }
    
    public final void drawString(final String s, final int n, final int n2) {
        this.aGC529.setForeground(this.aColor530);
        this.aGC529.drawString(s, n, n2 - this.aGC529.getFontMetrics().getAscent(), true);
    }
    
    public final void drawImage(final IImage image, final int n, final int n2) {
        ((ImageSWT)image).method12(this.aGC529, n, n2);
    }
    
    public final void drawImage(final IImage image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        ((ImageSWT)image).method13(this.aGC529, n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public final void setClip(final int n, final int n2, final int n3, final int n4) {
        this.aGC529.setClipping(n, n2, n3, n4);
    }
    
    public final void setColor(final int n, final boolean b) {
        final int n2 = n >> 16 & 0xFF;
        final int n3 = n >> 8 & 0xFF;
        final int n4 = n & 0xFF;
        this.aColor530.dispose();
        this.aColor530 = new Color((Device)null, n2, n3, n4);
    }
    
    public final void setColor(final int n, final int n2, final int n3) {
        this.aColor530.dispose();
        this.aColor530 = new Color((Device)null, n, n2, n3);
    }
    
    public final void setFont(final IFont font) {
        this.aGC529.setFont(((FontSWT)font).method297());
    }
    
    public final int getClipHeight() {
        if (this.aGC529.isClipped()) {
            return this.aGC529.getClipping().height;
        }
        return this.anInt11;
    }
    
    public final int getClipWidth() {
        if (this.aGC529.isClipped()) {
            return this.aGC529.getClipping().width;
        }
        return this.anInt10;
    }
    
    public final int getClipX() {
        if (this.aGC529.isClipped()) {
            return this.aGC529.getClipping().x;
        }
        return 0;
    }
    
    public final int getClipY() {
        if (this.aGC529.isClipped()) {
            return this.aGC529.getClipping().y;
        }
        return 0;
    }
    
    public final int getColor() {
        return ((this.aColor530.getRed() & 0xFF) << 16) + ((this.aColor530.getGreen() & 0xFF) << 8) + (this.aColor530.getBlue() & 0xFF);
    }
    
    public final int getColorRed() {
        return this.aColor530.getRed();
    }
    
    public final int getColorGreen() {
        return this.aColor530.getGreen();
    }
    
    public final int getColorBlue() {
        return this.aColor530.getBlue();
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
        Label_0184: {
            float n8;
            if ((n7 = ((n4 != 0) ? ((n4 - n5) / n4) : 0.0f)) == 0.0f) {
                n8 = 0.0f;
            }
            else {
                final float n9 = (n4 - n) / (n4 - n5);
                final float n10 = (n4 - n2) / (n4 - n5);
                final float n11 = (n4 - n3) / (n4 - n5);
                float n12;
                float n13;
                if (n == n4) {
                    n12 = n11;
                    n13 = n10;
                }
                else if (n2 == n4) {
                    n12 = 2.0f + n9;
                    n13 = n11;
                }
                else {
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
        final Transform transform = new Transform((Device)null);
        this.aGC529.getTransform(transform);
        final TransformSWT b = new TransformSWT(transform);
        transform.dispose();
        return b;
    }
    
    public final void setTransform(final ITransform transform) {
        final Transform method298 = ((TransformSWT)transform).method298();
        this.aGC529.setTransform(method298);
        method298.dispose();
    }
    
    public final void transform(final ITransform transform) {
        final Transform transform2 = new Transform((Device)null);
        this.aGC529.getTransform(transform2);
        final TransformSWT b = new TransformSWT(transform2);
        transform2.dispose();
        b.transform(transform);
        final Transform method298 = b.method298();
        this.aGC529.setTransform(method298);
        method298.dispose();
    }
    
    public final void translate(final int n, final int n2) {
        final Transform transform = new Transform((Device)null);
        this.aGC529.getTransform(transform);
        transform.translate((float)n, (float)n2);
        this.aGC529.setTransform(transform);
        transform.dispose();
    }
    
    public final void setStrokeStyle(final int n) {
        GC gc;
        int lineStyle;
        if (n == 0) {
            gc = this.aGC529;
            lineStyle = 1;
        }
        else {
            if (n != 1) {
                return;
            }
            gc = this.aGC529;
            lineStyle = 3;
        }
        gc.setLineStyle(lineStyle);
    }
    
    public final int getStrokeStyle() {
        final boolean b;
        return (b = (this.aGC529.getLineStyle() != 1)) ? 1 : 0;
    }
}
