package javax.microedition.lcdui;

import java.util.*;
import emulator.*;
import emulator.debug.*;
import emulator.graphics2D.*;

public class Graphics
{
    IGraphics2D anIGraphics2D516;
    IImage anIImage517;
    IImage anIImage522;
    IGraphics2D anIGraphics2D523;
    IImage anIImage525;
    static Vector aVector518;
    int anInt519;
    int anInt524;
    Font aFont520;
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
    
    public Graphics(final IImage anIImage517, final IImage anIImage518) {
        super();
        this.anInt519 = 0;
        this.anInt524 = 0;
        this.anIntArray521 = new int[6];
        this.anIImage517 = anIImage517;
        this.anIGraphics2D516 = anIImage517.createGraphics();
        this.anIImage522 = Emulator.getEmulator().newImage(this.anIImage517.getWidth(), this.anIImage517.getHeight(), false);
        this.anIImage525 = anIImage518;
        (this.anIGraphics2D523 = anIImage518.createGraphics()).setAlpha(60);
        this.setFont(Font.getDefaultFont());
    }
    
    public IGraphics2D getImpl() {
        if (Settings.xrayView) {
            return this.anIGraphics2D523;
        }
        return this.anIGraphics2D516;
    }
    
    public IImage getImage() {
        return this.anIImage517;
    }
    
    public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        final ITransform transform = this.anIGraphics2D516.getTransform().newTransform(n3, n4, 0, n5, n6, n7);
        this.anIImage517.cloneImage(this.anIImage522);
        this.drawRegion(this.anIImage522, n, n2, n3, n4, transform, 16777215);
    }
    
    public void clipRect(final int n, final int n2, final int n3, final int n4) {
        this.anIGraphics2D516.clipRect(n, n2, n3, n4);
        this.anIGraphics2D523.clipRect(n, n2, n3, n4);
    }
    
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.anIGraphics2D516.drawArc(n, n2, n3 + 1, n4 + 1, n5, n6);
        this.method295(n, n2, n3 + 1, n4 + 1, n5, n6);
    }
    
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        this.anIGraphics2D516.drawLine(n, n2, n3, n4);
        this.method296(n, n2, n3, n4);
    }
    
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        this.anIGraphics2D516.drawRect(n, n2, n3, n4);
        this.method292(n, n2, n3, n4);
    }
    
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.anIGraphics2D516.drawRoundRect(n, n2, n3, n4, n5, n6);
        this.method292(n, n2, n3, n4);
    }

    public void drawImage(Image image, int n, int n2, int n3) {
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

          this.anIGraphics2D516.drawImage(image.getImpl(), n, n2);
          this.method289(image, 0, 0, n, n2, image.getWidth(), image.getHeight());
          ++image.anInt1309;
          ++Profiler.drawImageCallCount;
          Profiler.drawImagePixelCount += image.getWidth() * image.getHeight();
       }
    }

    
    public void drawRegion(final IImage image, final int n, final int n2, final int n3, final int n4, final ITransform transform, final int n5) {
        final ITransform transform2 = this.anIGraphics2D516.getTransform();
        this.anIGraphics2D516.transform(transform);
        this.anIGraphics2D516.drawImage(image, n, n2, n3, n4, 0, 0, n3, n4);
        this.anIGraphics2D516.setTransform(transform2);
        if (n5 >= 0 && Settings.xrayView) {
            this.anIGraphics2D523.transform(transform);
            if (Settings.xrayOverlapScreen) {
                this.anIGraphics2D523.drawImage(image, n, n2, n3, n4, 0, 0, n3, n4);
            }
            this.method291(0, 0, n3, n4, n5);
            this.anIGraphics2D523.setTransform(transform2);
        }
    }
    
    public void drawRegion(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        if (image == null) {
            throw new NullPointerException();
        }
        if (n < 0 || n + n3 > image.getWidth() || n2 < 0 || n2 + n4 > image.getHeight()) {
            throw new IllegalArgumentException("region exceeds the bounds of the source image");
        }
        if (image.getImpl() == this.anIImage517) {
            throw new IllegalArgumentException("src is the same image as the destination of this Graphics object");
        }
        if (!method294(n8, 64)) {
            throw new IllegalArgumentException();
        }
        final ITransform transform = this.anIGraphics2D516.getTransform().newTransform(n3, n4, n5, n6, n7, n8);
        final ITransform transform2 = this.anIGraphics2D516.getTransform();
        this.anIGraphics2D516.transform(transform);
        this.anIGraphics2D516.drawImage(image.getImpl(), n, n2, n3, n4, 0, 0, n3, n4);
        this.anIGraphics2D516.setTransform(transform2);
        this.anIGraphics2D523.transform(transform);
        this.method289(image, n, n2, 0, 0, n3, n4);
        this.anIGraphics2D523.setTransform(transform2);
        ++image.anInt1309;
        ++Profiler.drawRegionCallCount;
        Profiler.drawRegionPixelCount += Math.abs(n3 * n4);
    }
    
    public void drawRGB(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        if (array == null) {
            throw new NullPointerException();
        }
        this.drawRegion(emulator.graphics2D.b.method163(array, b, n, n2, n5, n6), 0, 0, n5, n6, this.anIGraphics2D516.getTransform().newTransform(n5, n6, 0, n3, n4, 0), 16711680);
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
    
    public void drawString(final String s, int n, int n2, final int n3) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (!method294(n3, 2)) {
            throw new IllegalArgumentException();
        }
        final Font font;
        final int stringWidth = (font = this.getFont()).stringWidth(s);
        final int height = font.getHeight();
        Label_0077: {
            int n4;
            int n5;
            if ((n3 & 0x8) != 0x0) {
                n4 = n;
                n5 = stringWidth;
            }
            else {
                if ((n3 & 0x1) == 0x0) {
                    break Label_0077;
                }
                n4 = n;
                n5 = stringWidth / 2;
            }
            n = n4 - n5;
        }
        Label_0122: {
            int n6;
            int baselinePosition;
            if ((n3 & 0x20) != 0x0) {
                n6 = n2;
                baselinePosition = height;
            }
            else if ((n3 & 0x2) != 0x0) {
                n6 = n2;
                baselinePosition = height / 2;
            }
            else {
                if ((n3 & 0x40) == 0x0) {
                    break Label_0122;
                }
                n6 = n2;
                baselinePosition = font.getBaselinePosition();
            }
            n2 = n6 - baselinePosition;
        }
        this.anIGraphics2D516.drawString(s, n, n2 + font.getBaselinePosition());
        this.method291(n, n2, stringWidth, height, 255);
    }
    
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.anIGraphics2D516.fillArc(n, n2, n3, n4, n5, n6);
        this.method290(n, n2, n3, n4, n5, n6);
    }
    
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        this.anIGraphics2D516.fillRect(n, n2, n3, n4);
        this.method291(n, n2, n3, n4, 16777215);
    }
    
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.anIGraphics2D516.fillRoundRect(n, n2, n3, n4, n5, n6);
        this.method291(n, n2, n3, n4, 16777215);
    }
    
    public void fillTriangle(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.anIntArray521[0] = n;
        this.anIntArray521[1] = n2;
        this.anIntArray521[2] = n3;
        this.anIntArray521[3] = n4;
        this.anIntArray521[4] = n5;
        this.anIntArray521[5] = n6;
        this.anIGraphics2D516.fillPolygon(this.anIntArray521);
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
        if (!Settings.xrayView) {
            return;
        }
        if (Settings.xrayOverlapScreen) {
            this.anIGraphics2D523.drawImage(image.getImpl(), n, n2, n5, n6, n3, n4, n5, n6);
        }
        if (image.isMutable()) {
            this.anIGraphics2D523.drawImage(image.getXRayBuffer(), n3, n4);
        }
        this.method291(n3, n4, n5, n6, 16777215);
        if (Settings.xrayShowClipBorder) {
            this.anIGraphics2D523.setColor(255, false);
            this.anIGraphics2D523.drawRect(n9 + n3 - n, n10 + n4 - n2, n7 - 1, n8 - 1);
            this.anIGraphics2D523.setColor(0, false);
            this.anIGraphics2D523.fillRect(n9 - 1 + n3 - n, n10 - 1 + n4 - n2, 3, 3);
        }
    }
    
    private void method290(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (Settings.xrayView) {
            this.anIGraphics2D523.setColor(16777215, false);
            this.anIGraphics2D523.fillArc(n, n2, n3, n4, n5, n6);
            this.method293();
        }
    }
    
    private void method295(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (Settings.xrayView) {
            this.anIGraphics2D523.setColor(16777215, false);
            this.anIGraphics2D523.drawArc(n, n2, n3, n4, n5, n6);
            this.method293();
        }
    }
    
    private void method291(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (Settings.xrayView) {
            this.anIGraphics2D523.setColor(n5, false);
            this.anIGraphics2D523.fillRect(n, n2, n3, n4);
            this.method293();
        }
    }
    
    private void method292(final int n, final int n2, final int n3, final int n4) {
        if (Settings.xrayView) {
            this.anIGraphics2D523.setColor(16777215, false);
            this.anIGraphics2D523.drawRect(n, n2, n3, n4);
            this.method293();
        }
    }
    
    private void method296(final int n, final int n2, final int n3, final int n4) {
        if (Settings.xrayView) {
            this.anIGraphics2D523.setColor(16777215, false);
            this.anIGraphics2D523.drawLine(n, n2, n3, n4);
            this.method293();
        }
    }
    
    private void method293() {
        if (!Graphics.aVector518.contains(this.anIImage525)) {
            Graphics.aVector518.add(this.anIImage525);
        }
    }
    
    protected static void resetXRayCache() {
        for (int i = Graphics.aVector518.size() - 1; i >= 0; --i) {
            final IImage image;
            final IGraphics2D graphics;
            (graphics = (image = (IImage)Graphics.aVector518.get(i)).getGraphics()).setColor(0, false);
            graphics.setAlpha(255);
            graphics.setClip(0, 0, image.getWidth(), image.getHeight());
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.setAlpha(60);
        }
        Graphics.aVector518.clear();
    }
    
    public int getClipHeight() {
        return this.anIGraphics2D516.getClipHeight();
    }
    
    public int getClipWidth() {
        return this.anIGraphics2D516.getClipWidth();
    }
    
    public int getClipX() {
        return this.anIGraphics2D516.getClipX();
    }
    
    public int getClipY() {
        return this.anIGraphics2D516.getClipY();
    }
    
    public int getColor() {
        return this.anIGraphics2D516.getColor();
    }
    
    public Font getFont() {
        return this.aFont520;
    }
    
    public int getGreenComponent() {
        return this.anIGraphics2D516.getColorGreen();
    }
    
    public int getRedComponent() {
        return this.anIGraphics2D516.getColorRed();
    }
    
    public int getBlueComponent() {
        return this.anIGraphics2D516.getColorBlue();
    }
    
    public int getTranslateX() {
        return this.anInt519;
    }
    
    public int getTranslateY() {
        return this.anInt524;
    }
    
    public void setClip(final int n, final int n2, final int n3, final int n4) {
        this.anIGraphics2D516.setClip(n, n2, n3, n4);
        this.anIGraphics2D523.setClip(n, n2, n3, n4);
    }
    
    public void setColor(final int n) {
        this.anIGraphics2D516.setColor(n, false);
    }
    
    public void setColor(final int n, final int n2, final int n3) {
        this.anIGraphics2D516.setColor(n, n2, n3);
    }
    
    public void setFont(final Font font) {
        this.aFont520 = ((font == null) ? Font.getDefaultFont() : font);
        this.anIGraphics2D516.setFont(this.aFont520.getImpl());
    }
    
    public int getGrayScale() {
        return (int)(255.0f * this.anIGraphics2D516.RGBtoHSB(this.getRedComponent(), this.getGreenComponent(), this.getBlueComponent())[2]);
    }
    
    public void setGrayScale(final int n) {
        if ((n & 0xFFFFFF00) > 0) {
            throw new IllegalArgumentException();
        }
        this.setColor(n, n, n);
    }
    
    public void translate(final int n, final int n2) {
        this.anInt519 += n;
        this.anInt524 += n2;
        this.anIGraphics2D516.translate(n, n2);
        this.anIGraphics2D523.translate(n, n2);
    }
    
    public int getDisplayColor(final int n) {
        return n;
    }
    
    public void setStrokeStyle(final int strokeStyle) {
        this.anIGraphics2D516.setStrokeStyle(strokeStyle);
    }
    
    public int getStrokeStyle() {
        return this.anIGraphics2D516.getStrokeStyle();
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
        Graphics.aVector518 = new Vector();
    }
}
