package emulator.graphics2D.swt;

import org.eclipse.swt.graphics.*;
import emulator.graphics2D.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

/*
 * swt image
 */
public final class ImageSWT implements IImage
{
    Image anImage531;
    ImageData anImageData532;
    Graphics2DSWT ac533;
    int[] anIntArray534;
    int anInt535;
    boolean transparent;
    boolean disposed;
    private static final PaletteData aPaletteData537;
    
    public ImageSWT(final byte[] array) {
        this(new ByteArrayInputStream(array));
    }
    
    public ImageSWT(final InputStream inputStream) {
        super();
        this.anImageData532 = new ImageData(inputStream);
        this.transparent = (this.anImageData532.getTransparencyType() != 0);
        this.disposed = false;
        this.anInt535 = this.anImageData532.width * this.anImageData532.height;
        this.anIntArray534 = new int[this.anInt535];
        if (this.transparent && !this.anImageData532.palette.isDirect && this.anImageData532.transparentPixel != -1) {
            final RGB rgb = this.anImageData532.palette.colors[this.anImageData532.transparentPixel];
            for (int i = this.anImageData532.palette.colors.length - 1; i >= 0; --i) {
                if (i != this.anImageData532.transparentPixel) {
                    if (rgb.equals((Object)this.anImageData532.palette.colors[i])) {
                        RGB rgb2;
                        int red;
                        if (this.anImageData532.palette.colors[i].red == 255) {
                            rgb2 = this.anImageData532.palette.colors[i];
                            red = 254;
                        }
                        else {
                            red = (rgb2 = this.anImageData532.palette.colors[i]).red + 1;
                        }
                        rgb2.red = red;
                    }
                }
            }
        }
    }
    
    public ImageSWT(final int n, final int n2, final boolean aBoolean536, final int n3) {
    	this.anImage531 = new Image((Device)null, n, n2);
        GC gc = new GC(this.anImage531);
        Color background = new Color((Device)null, n3 >> 16 & 255, n3 >> 8 & 255, n3 & 255);
        gc.setBackground(background);
        gc.fillRectangle(0, 0, n, n2);
        gc.dispose();
        background.dispose();
        this.anImageData532 = this.anImage531.getImageData();
        this.anImage531.dispose();
        this.disposed = false;
        this.transparent = aBoolean536;
        this.anInt535 = n * n2;
        this.anIntArray534 = new int[this.anInt535];
    }
    
    public final void finalize() {
        if (this.anImage531 != null && !this.anImage531.isDisposed()) {
            this.anImage531.dispose();
        }
        if (this.ac533 != null) {
            this.ac533.finalize();
            this.ac533 = null;
        }
        this.disposed = false;
    }
    
    public final IGraphics2D createGraphics() {
        if (this.disposed) {
            if (this.ac533 == null) {
                this.ac533 = new Graphics2DSWT(this.anImage531);
            }
            else {
                this.ac533.setTransform(new TransformSWT());
            }
        }
        else {
            this.anImage531 = new Image((Device)null, this.anImageData532.width, this.anImageData532.height);
            final GC gc = new GC((Drawable)this.anImage531);
            final Image image = new Image((Device)null, this.anImageData532);
            gc.drawImage(image, 0, 0);
            gc.dispose();
            image.dispose();
            if (this.ac533 != null) {
                this.ac533.finalize();
                this.ac533 = null;
            }
            this.ac533 = new Graphics2DSWT(this.anImage531);
            this.disposed = true;
        }
        return this.ac533;
    }
    
    public final IGraphics2D getGraphics() {
        return this.ac533;
    }
    
    public final void method12(final GC gc, final int n, final int n2) {
        if (this.disposed) {
            gc.drawImage(this.anImage531, n, n2);
            return;
        }
        gc.drawImage(this.anImage531 = new Image((Device)null, this.anImageData532), n, n2);
        this.anImage531.dispose();
    }
    
    public final void method13(final GC gc, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        if (this.disposed) {
            gc.drawImage(this.anImage531, n, n2, n3, n4, n5, n6, n7, n8);
            return;
        }
        gc.drawImage(this.anImage531 = new Image((Device)null, this.anImageData532), n, n2, n3, n4, n5, n6, n7, n8);
        this.anImage531.dispose();
    }
    
    public final int getWidth() {
        return this.anImageData532.width;
    }
    
    public final int getHeight() {
        return this.anImageData532.height;
    }
    
    public final int[] getData() {
        if (this.disposed) {
            this.anImageData532 = this.anImage531.getImageData();
        }
        this.anImageData532.getPixels(0, 0, this.anInt535, this.anIntArray534, 0);
        if (this.anImageData532.alphaData != null) {
            for (int i = this.anInt535 - 1; i >= 0; --i) {
                final RGB rgb = this.anImageData532.palette.getRGB(this.anIntArray534[i]);
                this.anIntArray534[i] = (this.anImageData532.alphaData[i] << 24) + ((rgb.red & 0xFF) << 16) + ((rgb.green & 0xFF) << 8) + (rgb.blue & 0xFF);
            }
        }
        else if (this.anImageData532.transparentPixel != -1 && !this.anImageData532.palette.isDirect) {
            for (int j = this.anInt535 - 1; j >= 0; --j) {
                int[] array;
                int n;
                int n2;
                if (this.anIntArray534[j] == this.anImageData532.transparentPixel) {
                    array = this.anIntArray534;
                    n = j;
                    n2 = 0;
                }
                else {
                    final RGB rgb2 = this.anImageData532.palette.getRGB(this.anIntArray534[j]);
                    array = this.anIntArray534;
                    n = j;
                    n2 = -16777216 + ((rgb2.red & 0xFF) << 16) + ((rgb2.green & 0xFF) << 8) + (rgb2.blue & 0xFF);
                }
                array[n] = n2;
            }
        }
        else {
            for (int k = this.anInt535 - 1; k >= 0; --k) {
                final RGB rgb3 = this.anImageData532.palette.getRGB(this.anIntArray534[k]);
                this.anIntArray534[k] = -16777216 + ((rgb3.red & 0xFF) << 16) + ((rgb3.green & 0xFF) << 8) + (rgb3.blue & 0xFF);
            }
        }
        return this.anIntArray534;
    }
    
    public final void setAlpha(int n, int n2, int n3, int n4, final int n5) {
        if (n >= this.anImageData532.width || n2 >= this.anImageData532.height) {
            return;
        }
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        if (n + n3 > this.anImageData532.width) {
            n3 = this.anImageData532.width - n;
        }
        if (n2 + n4 > this.anImageData532.height) {
            n4 = this.anImageData532.height - n2;
        }
        final byte[] array = new byte[n3];
        for (int i = 0; i < n3; ++i) {
            array[i] = (byte)n5;
        }
        if (this.anImageData532.alphaData == null) {
            this.anImageData532.alphaData = new byte[this.anImageData532.width * this.anImageData532.height];
        }
        for (int j = n2; j < n2 + n4; ++j) {
            System.arraycopy(array, 0, this.anImageData532.alphaData, j * this.anImageData532.width + n, n3);
        }
        if (this.disposed) {
            this.disposed = false;
            if (this.anImage531 != null && !this.anImage531.isDisposed()) {
                this.anImage531.dispose();
            }
            if (this.ac533 != null) {
                this.ac533.finalize();
                this.ac533 = null;
            }
        }
    }
    
    public final void setData(final int[] array) {
        this.anImageData532 = new ImageData(this.anImageData532.width, this.anImageData532.height, 32, ImageSWT.aPaletteData537);
        int n = this.anInt535 * 4 - 1;
        if (this.transparent) {
            if (this.anImageData532.alphaData == null) {
                this.anImageData532.alphaData = new byte[this.anImageData532.width * this.anImageData532.height];
            }
            for (int i = this.anInt535 - 1; i >= 0; --i) {
                this.anImageData532.alphaData[i] = (byte)(array[i] >> 24 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[i] >> 24 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[i] >> 16 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[i] >> 8 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[i] & 0xFF);
            }
        }
        else {
            for (int j = this.anInt535 - 1; j >= 0; --j) {
                this.anImageData532.data[n--] = (byte)(array[j] >> 24 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[j] >> 16 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[j] >> 8 & 0xFF);
                this.anImageData532.data[n--] = (byte)(array[j] & 0xFF);
            }
        }
        if (this.disposed) {
            this.disposed = false;
            if (this.anImage531 != null && !this.anImage531.isDisposed()) {
                this.anImage531.dispose();
            }
            if (this.ac533 != null) {
                this.ac533.finalize();
                this.ac533 = null;
            }
        }
    }
    
    public final int getRGB(final int n, final int n2) {
        if (this.disposed) {
            try {
                return this.method300(this.anImage531.getImageData().getPixel(n, n2));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return this.method300(this.anImageData532.getPixel(n, n2));
            }
        }
        return this.method300(this.anImageData532.getPixel(n, n2));
    }
    
    private int method300(final int n) {
        final RGB rgb = this.anImageData532.palette.getRGB(n);
        return -16777216 + ((rgb.red & 0xFF) << 16) + ((rgb.green & 0xFF) << 8) + (rgb.blue & 0xFF);
    }
    
    public final void saveToFile(final String s) {
        if (this.disposed) {
            try {
                this.anImageData532 = this.anImage531.getImageData();
            }
            catch (Exception ex) {
                ex.printStackTrace();}
        }
        try {
            ImageIO.write(emulator.graphics2D.c.method167(this.anImageData532), "png", new File(s));
        }
        catch (Exception ex2) {
            ex2.printStackTrace();}
    }
    
    public final void copyToClipBoard() {
        if (this.disposed) {
            this.anImageData532 = this.anImage531.getImageData();
        }
        emulator.graphics2D.c.method169(emulator.graphics2D.c.method167(this.anImageData532));
    }
    
    public final void cloneImage(final IImage image) {
        ((ImageSWT)image).setData(this.getData());
    }
    
    static {
        aPaletteData537 = new PaletteData(65280, 16711680, -16777216);
    }
}
