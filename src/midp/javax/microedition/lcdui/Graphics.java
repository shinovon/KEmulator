package javax.microedition.lcdui;

import java.util.*;
import emulator.*;
import emulator.debug.*;
import emulator.graphics2D.*;

public class Graphics
{
    IGraphics2D impl;
    IImage image;
    IImage copyimage;
    IGraphics2D xrayGraphics;
    IImage xrayImage;
    static Vector xrayCache;
    int tx;
    int ty;
    Font font;
    int[] anIntArray521;
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BOTTOM = 32;
    public static final int BASELINE = 64;
    public static final int SOLID = 0;
    public static final int DOTTED = 1;
    
    public Graphics(final IImage i1, final IImage i2) {
        super();
        this.tx = 0;
        this.ty = 0;
        this.anIntArray521 = new int[6];
        this.image = i1;
        this.impl = i1.createGraphics();
        //this.copyimage = Emulator.getEmulator().newImage(this.image.getWidth(), this.image.getHeight(), false);
        if(i2 != null) {
            this.xrayImage = i2;
            (this.xrayGraphics = i2.createGraphics()).setAlpha(60);
            this.setFont(Font.getDefaultFont());
        }
    }
    
    public Graphics(final IImage anIImage517) {
        this.tx = 0;
        this.ty = 0;
        this.anIntArray521 = new int[6];
        this.image = anIImage517;
        this.impl = anIImage517.createGraphics();
        //this.copyimage = Emulator.getEmulator().newImage(this.image.getWidth(), this.image.getHeight(), false);
        this.setFont(Font.getDefaultFont());
	}
    
    void dispose() {
        xrayImage = null;
        xrayGraphics = null;
    	image = null;
    	impl = null;
    }

	public IGraphics2D getImpl() {
        if (Settings.xrayView && xrayGraphics != null) {
            return this.xrayGraphics;
        }
        return this.impl;
    }
    
    public IImage getImage() {
        return this.image;
    }
    
    public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        ++Profiler.drawCallCount;
        final ITransform transform = this.impl.getTransform().newTransform(n3, n4, 0, n5, n6, n7);
        if(copyimage == null) {
            this.copyimage = Emulator.getEmulator().newImage(this.image.getWidth(), this.image.getHeight(), false);
        }
        this.image.cloneImage(this.copyimage);
        this._drawRegion(this.copyimage, n, n2, n3, n4, transform, 16777215);
    }
    
    public void clipRect(final int n, final int n2, final int n3, final int n4) {
        this.impl.clipRect(n, n2, n3, n4);
        if(xrayGraphics != null)
            this.xrayGraphics.clipRect(n, n2, n3, n4);
    }
    
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        ++Profiler.drawCallCount;
        this.impl.drawArc(n, n2, n3 + 1, n4 + 1, n5, n6);
        this.xrayDrawArc(n, n2, n3 + 1, n4 + 1, n5, n6);
    }
    
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        ++Profiler.drawCallCount;
        this.impl.drawLine(n, n2, n3, n4);
        this.xrayDrawLine(n, n2, n3, n4);
    }
    
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        ++Profiler.drawCallCount;
        this.impl.drawRect(n, n2, n3, n4);
        this.xrayDrawRect(n, n2, n3, n4);
    }
    
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        ++Profiler.drawCallCount;
        this.impl.drawRoundRect(n, n2, n3, n4, n5, n6);
        this.xrayDrawRect(n, n2, n3, n4);
    }

    public void drawImage(Image image, int n, int n2, int n3) {
        ++Profiler.drawCallCount;
       if(image == null) {
          throw new NullPointerException();
       } else if(!method294(n3, 64)) {
          throw new IllegalArgumentException();
       } else {
          int n5;
          int height;
          label32: {
             if((n3 & 8) != 0) {
                n5 = n;
                height = image.getWidth();
             } else {
                if((n3 & 1) == 0) {
                   break label32;
                }

                n5 = n;
                height = image.getWidth() / 2;
             }

             n = n5 - height;
          }

          label26: {
             if((n3 & 32) != 0) {
                n5 = n2;
                height = image.getHeight();
             } else {
                if((n3 & 2) == 0) {
                   break label26;
                }

                n5 = n2;
                height = image.getHeight() / 2;
             }

             n2 = n5 - height;
          }

          this.impl.drawImage(image.getImpl(), n, n2);
          this.method289(image, 0, 0, n, n2, image.getWidth(), image.getHeight());
          ++image.usedCount;
          ++Profiler.drawImageCallCount;
          Profiler.drawImagePixelCount += image.getWidth() * image.getHeight();
       }
    }

    
    public void drawRegion(final IImage image, final int n, final int n2, final int n3, final int n4, final ITransform transform, final int n5) {
        ++Profiler.drawCallCount;
        _drawRegion(image, n, n2, n3, n4, transform, n5);
    }
    
    private void _drawRegion(final IImage image, final int n, final int n2, final int n3, final int n4, final ITransform transform, final int n5) {
        final ITransform transform2 = this.impl.getTransform();
        this.impl.transform(transform);
        this.impl.drawImage(image, n, n2, n3, n4, 0, 0, n3, n4);
        this.impl.setTransform(transform2);
        if (n5 >= 0 && Settings.xrayView && xrayGraphics != null) {
            this.xrayGraphics.transform(transform);
            if (Settings.xrayOverlapScreen) {
                this.xrayGraphics.drawImage(image, n, n2, n3, n4, 0, 0, n3, n4);
            }
            this.xrayFillRect(0, 0, n3, n4, n5);
            this.xrayGraphics.setTransform(transform2);
        }
    }
    
    public void drawRegion(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        ++Profiler.drawCallCount;
        if (image == null) {
            throw new NullPointerException();
        }
        if (n < 0 || n + n3 > image.getWidth() || n2 < 0 || n2 + n4 > image.getHeight()) {
            throw new IllegalArgumentException("region exceeds the bounds of the source image");
        }
        if (image.getImpl() == this.image) {
            throw new IllegalArgumentException("src is the same image as the destination of this Graphics object");
        }
        if (!method294(n8, 64)) {
            throw new IllegalArgumentException();
        }
        final ITransform transform2 = this.impl.getTransform();
        final ITransform transform = transform2.newTransform(n3, n4, n5, n6, n7, n8);
        this.impl.transform(transform);
        this.impl.drawImage(image.getImpl(), n, n2, n3, n4, 0, 0, n3, n4);
        this.impl.setTransform(transform2);
        if(xrayGraphics != null) {
            this.xrayGraphics.transform(transform);
            this.method289(image, n, n2, 0, 0, n3, n4);
            this.xrayGraphics.setTransform(transform2);
        }
        ++image.usedCount;
        ++Profiler.drawRegionCallCount;
        Profiler.drawRegionPixelCount += Math.abs(n3 * n4);
    }
    
    public void drawRGB(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        ++Profiler.drawCallCount;
        if (array == null) {
            throw new NullPointerException();
        }
        this._drawRegion(emulator.graphics2D.b.method163(array, b, n, n2, n5, n6), 0, 0, n5, n6, this.impl.getTransform().newTransform(n5, n6, 0, n3, n4, 0), 16711680);
        ++Profiler.drawRGBCallCount;
        Profiler.drawRGBPixelCount += Math.abs(n5 * n6);
    }
    
    public void drawChar(final char c, final int n, final int n2, final int n3) {
        this.drawString(new String(new char[] { c }), n, n2, n3);
    }
    
    public void drawChars(final char[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.drawString(new String(array, n, n2), n3, n4, n5);
    }
    
    public void drawSubstring(final String s, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.drawString(s.substring(n, n + n2), n3, n4, n5);
    }
    
    public void drawString(final String s, int x, int y, final int a) {
        ++Profiler.drawCallCount;
        if (s == null) {
            throw new NullPointerException();
        }
        if (!method294(a, 2)) {
            throw new IllegalArgumentException();
        }
        final Font font = this.font;
        final int stringWidth = font.stringWidth(s);
        final int height = font.getHeight();
        impl.setFont(font.getImpl());
        if ((a & 0x8) != 0x0) {
            x -= stringWidth;
        } else if ((a & 0x1) != 0x0) {
            x -= stringWidth / 2;
        }
        if ((a & 0x20) != 0x0) {
            y -= height;
        } else if ((a & 0x2) != 0x0) {
            y -= height / 2;
        } else if ((a & 0x40) != 0x0) {
            y -= font.getBaselinePosition();
        }
        this.impl.drawString(s, x, y + font.getBaselinePosition());
        if(xrayGraphics != null)
            this.xrayFillRect(x, y, stringWidth, height, 255);
    }
    
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        ++Profiler.drawCallCount;
        this.impl.fillArc(n, n2, n3, n4, n5, n6);
        if(xrayGraphics != null)
            this.xrayFillArc(n, n2, n3, n4, n5, n6);
    }
    
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        ++Profiler.drawCallCount;
        this.impl.fillRect(n, n2, n3, n4);
        if(xrayGraphics != null)
            this.xrayFillRect(n, n2, n3, n4, 16777215);
    }
    
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        ++Profiler.drawCallCount;
        this.impl.fillRoundRect(n, n2, n3, n4, n5, n6);
        if(xrayGraphics != null)
            this.xrayFillRect(n, n2, n3, n4, 16777215);
    }
    
    public void fillTriangle(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        ++Profiler.drawCallCount;
        this.anIntArray521[0] = n;
        this.anIntArray521[1] = n2;
        this.anIntArray521[2] = n3;
        this.anIntArray521[3] = n4;
        this.anIntArray521[4] = n5;
        this.anIntArray521[5] = n6;
        this.impl.fillPolygon(this.anIntArray521);
    }
    
    private void method289(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        int clipX = n3;
        int clipY = n4;
        int clipX2 = n3 + n5;
        int clipY2 = n4 + n6;
        if (clipX < this.getClipX()) {
            clipX = this.getClipX();
        }
        if (clipX2 < this.getClipX()) {
            clipX2 = this.getClipX();
        }
        if (clipY < this.getClipY()) {
            clipY = this.getClipY();
        }
        if (clipY2 < this.getClipY()) {
            clipY2 = this.getClipY();
        }
        if (clipX > this.getClipX() + this.getClipWidth()) {
            clipX = this.getClipX() + this.getClipWidth();
        }
        if (clipX2 > this.getClipX() + this.getClipWidth()) {
            clipX2 = this.getClipX() + this.getClipWidth();
        }
        if (clipY > this.getClipY() + this.getClipHeight()) {
            clipY = this.getClipY() + this.getClipHeight();
        }
        if (clipY2 > this.getClipY() + this.getClipHeight()) {
            clipY2 = this.getClipY() + this.getClipHeight();
        }
        final int n7 = clipX2 - clipX;
        final int n8 = clipY2 - clipY;
        final int n9 = clipX - (n3 - n);
        final int n10 = clipY - (n4 - n2);
        image.getUsedRegion().setAlpha(n9, n10, n7, n8, 0);
        if (!Settings.xrayView || xrayGraphics == null) {
            return;
        }
        if (Settings.xrayOverlapScreen) {
            this.xrayGraphics.drawImage(image.getImpl(), n, n2, n5, n6, n3, n4, n5, n6);
        }
        if (image.isMutable()) {
            this.xrayGraphics.drawImage(image.getXRayBuffer(), n3, n4);
        }
        this.xrayFillRect(n3, n4, n5, n6, 16777215);
        if (Settings.xrayShowClipBorder) {
            this.xrayGraphics.setColor(255, false);
            this.xrayGraphics.drawRect(n9 + n3 - n, n10 + n4 - n2, n7 - 1, n8 - 1);
            this.xrayGraphics.setColor(0, false);
            this.xrayGraphics.fillRect(n9 - 1 + n3 - n, n10 - 1 + n4 - n2, 3, 3);
        }
    }
    
    private void xrayFillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (Settings.xrayView && xrayGraphics != null) {
            this.xrayGraphics.setColor(16777215, false);
            this.xrayGraphics.fillArc(n, n2, n3, n4, n5, n6);
            this.xrayUpdate();
        }
    }
    
    private void xrayDrawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (Settings.xrayView && xrayGraphics != null) {
            this.xrayGraphics.setColor(16777215, false);
            this.xrayGraphics.drawArc(n, n2, n3, n4, n5, n6);
            this.xrayUpdate();
        }
    }
    
    private void xrayFillRect(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (Settings.xrayView && xrayGraphics != null) {
            this.xrayGraphics.setColor(n5, false);
            this.xrayGraphics.fillRect(n, n2, n3, n4);
            this.xrayUpdate();
        }
    }
    
    private void xrayDrawRect(final int n, final int n2, final int n3, final int n4) {
        if (Settings.xrayView && xrayGraphics != null) {
            this.xrayGraphics.setColor(16777215, false);
            this.xrayGraphics.drawRect(n, n2, n3, n4);
            this.xrayUpdate();
        }
    }
    
    private void xrayDrawLine(final int n, final int n2, final int n3, final int n4) {
        if (Settings.xrayView && xrayGraphics != null) {
            this.xrayGraphics.setColor(16777215, false);
            this.xrayGraphics.drawLine(n, n2, n3, n4);
            this.xrayUpdate();
        }
    }
    
    private void xrayUpdate() {
        if (!Graphics.xrayCache.contains(this.xrayImage)) {
            Graphics.xrayCache.add(this.xrayImage);
        }
    }
    
    protected static void resetXRayCache() {
        for (int i = Graphics.xrayCache.size() - 1; i >= 0; --i) {
            final IImage image;
            final IGraphics2D graphics;
            (graphics = (image = (IImage)Graphics.xrayCache.get(i)).getGraphics()).setColor(0, false);
            graphics.setAlpha(255);
            graphics.setClip(0, 0, image.getWidth(), image.getHeight());
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.setAlpha(60);
        }
        Graphics.xrayCache.clear();
    }
    
    public int getClipHeight() {
        return this.impl.getClipHeight();
    }
    
    public int getClipWidth() {
        return this.impl.getClipWidth();
    }
    
    public int getClipX() {
        return this.impl.getClipX();
    }
    
    public int getClipY() {
        return this.impl.getClipY();
    }
    
    public int getColor() {
        return this.impl.getColor();
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public int getGreenComponent() {
        return this.impl.getColorGreen();
    }
    
    public int getRedComponent() {
        return this.impl.getColorRed();
    }
    
    public int getBlueComponent() {
        return this.impl.getColorBlue();
    }
    
    public int getTranslateX() {
        return this.tx;
    }
    
    public int getTranslateY() {
        return this.ty;
    }
    
    public void setClip(final int n, final int n2, final int n3, final int n4) {
        ++Profiler.drawCallCount;
        this.impl.setClip(n, n2, n3, n4);
        if(xrayGraphics != null)
            this.xrayGraphics.setClip(n, n2, n3, n4);
    }
    
    public void setColor(final int n) {
        ++Profiler.drawCallCount;
        this.impl.setColor(n, false);
    }
    
    public void setColor(final int n, final int n2, final int n3) {
        ++Profiler.drawCallCount;
        this.impl.setColor(n, n2, n3);
    }
    
    public void setFont(final Font font) {
        ++Profiler.drawCallCount;
        this.font = ((font == null) ? Font.getDefaultFont() : font);
        this.impl.setFont(this.font.getImpl());
    }
    
    public int getGrayScale() {
        return (int)(255.0f * this.impl.RGBtoHSB(this.getRedComponent(), this.getGreenComponent(), this.getBlueComponent())[2]);
    }
    
    public void setGrayScale(final int n) {
        if ((n & 0xFFFFFF00) > 0) {
            throw new IllegalArgumentException();
        }
        this.setColor(n, n, n);
    }
    
    public void translate(final int x, final int y) {
    	++Profiler.drawCallCount;
        this.tx += x;
        this.ty += y;
        this.impl.translate(x, y);
        if(xrayGraphics != null)
            this.xrayGraphics.translate(x, y);
    }
    
    public int getDisplayColor(final int n) {
        return n;
    }
    
    public void setStrokeStyle(final int strokeStyle) {
        ++Profiler.drawCallCount;
        this.impl.setStrokeStyle(strokeStyle);
    }
    
    public int getStrokeStyle() {
        return this.impl.getStrokeStyle();
    }
    
    private static boolean method294(final int n, final int n2) {
        if (n == 0) {
            return true;
        }
        boolean b;
        if (b = (n > 0 && n < 128 && (n & n2) == 0x0)) {
            final int n3;
            b = ((n3 = (n & 0x72)) != 0 && (n3 & n3 - 1) == 0x0);
        }
        if (b) {
            final int n4;
            b = ((n4 = (n & 0xD)) != 0 && (n4 & n4 - 1) == 0x0);
        }
        return b;
    }
    
    static {
        Graphics.xrayCache = new Vector();
    }

    public void reset(IImage i1, IImage i2) {
        if(i1 != image) {
            this.image = i1;
            this.impl = i1.createGraphics();
        }
        if(i2 != xrayImage) {
            this.xrayImage = i2;
            if(i2 != null)
                (this.xrayGraphics = i2.createGraphics()).setAlpha(60);
            else
                xrayGraphics = null;
        }
        setColor(0);
        setFont(Font.getDefaultFont());
        setStrokeStyle(SOLID);
        translate(-tx, -ty);
        ty = tx = 0;
        impl.reset();
        if(xrayGraphics != null)
            xrayGraphics.reset();
    }
}
