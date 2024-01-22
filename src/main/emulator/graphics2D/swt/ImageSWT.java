package emulator.graphics2D.swt;

import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.graphics.*;
import emulator.graphics2D.*;
import java.io.*;
import javax.imageio.*;

/*
 * swt image
 */
public final class ImageSWT implements IImage
{
    Image img;
    ImageData imgdata;
    Graphics2DSWT graphics;
    int[] rgb;
    int len;
    boolean transparent;
    boolean disposed;
    private static final PaletteData aPaletteData537;
    
    public ImageSWT(final byte[] array) {
        this(new ByteArrayInputStream(array));
    }
    
    public ImageSWT(final InputStream inputStream) {
        super();
        this.imgdata = new ImageData(inputStream);
        this.transparent = (this.imgdata.getTransparencyType() != 0);
        this.disposed = false;
        this.len = this.imgdata.width * this.imgdata.height;
        this.rgb = new int[this.len];
        if (this.transparent && !this.imgdata.palette.isDirect && this.imgdata.transparentPixel != -1) {
            final RGB rgb = this.imgdata.palette.colors[this.imgdata.transparentPixel];
            for (int i = this.imgdata.palette.colors.length - 1; i >= 0; --i) {
                if (i != this.imgdata.transparentPixel) {
                    if (rgb.equals((Object)this.imgdata.palette.colors[i])) {
                        RGB rgb2;
                        int red;
                        if (this.imgdata.palette.colors[i].red == 255) {
                            rgb2 = this.imgdata.palette.colors[i];
                            red = 254;
                        }
                        else {
                            red = (rgb2 = this.imgdata.palette.colors[i]).red + 1;
                        }
                        rgb2.red = red;
                    }
                }
            }
        }
    }
    
    public ImageSWT(final int n, final int n2, final boolean transparent, final int n3) {
    	this.img = new Image((Device)null, n, n2);
    	if(!transparent) {
            GC gc = new GC(this.img);
            Color background = new Color((Device)null, n3 >> 16 & 255, n3 >> 8 & 255, n3 & 255);
            gc.setBackground(background);
            gc.fillRectangle(0, 0, n, n2);
            gc.dispose();
            background.dispose();
    	} else {
            GC gc = new GC(this.img);
            gc.setAlpha(0);
            gc.fillRectangle(0, 0, n, n2);
            gc.dispose();
        }
        this.imgdata = this.img.getImageData();
        this.img.dispose();
        this.disposed = false;
        this.transparent = transparent;
        this.len = n * n2;
        this.rgb = new int[this.len];
    }
    
    public final void finalize() {
        EmulatorImpl.asyncExec(new Runnable() {
            public void run() {
                if (img != null && !img.isDisposed()) {
                    img.dispose();
                }
                if (graphics != null) {
                    graphics.finalize();
                    graphics = null;
                }
                disposed = false;
            }
        });
    }
    
    public final IGraphics2D createGraphics() {
        if (this.disposed) {
            if (this.graphics == null) {
                this.graphics = new Graphics2DSWT(this.img);
            }
            else {
                this.graphics.setTransform(new TransformSWT());
            }
        }
        else {
            this.img = new Image((Device)null, this.imgdata.width, this.imgdata.height);
            final GC gc = new GC((Drawable)this.img);
            final Image image = new Image((Device)null, this.imgdata);
            gc.drawImage(image, 0, 0);
            gc.dispose();
            image.dispose();
            if (this.graphics != null) {
                this.graphics.finalize();
                this.graphics = null;
            }
            this.graphics = new Graphics2DSWT(this.img);
            this.disposed = true;
        }
        return this.graphics;
    }
    
    public final IGraphics2D getGraphics() {
        return this.graphics;
    }
    
    public final void method12(final GC gc, final int n, final int n2) {
        if (this.disposed) {
            gc.drawImage(this.img, n, n2);
            return;
        }
        gc.drawImage(this.img = new Image((Device)null, this.imgdata), n, n2);
        this.img.dispose();
    }
    
    public final void method13(final GC gc, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        if (this.disposed) {
            gc.drawImage(this.img, n, n2, n3, n4, n5, n6, n7, n8);
            return;
        }
        gc.drawImage(this.img = new Image((Device)null, this.imgdata), n, n2, n3, n4, n5, n6, n7, n8);
        this.img.dispose();
    }
    
    public final int getWidth() {
        return this.imgdata.width;
    }
    
    public final int getHeight() {
        return this.imgdata.height;
    }
    
    public final int[] getData() {
        if (this.disposed) {
            this.imgdata = this.img.getImageData();
        }
        this.imgdata.getPixels(0, 0, this.len, this.rgb, 0);
        if (this.imgdata.alphaData != null) {
            for (int i = this.len - 1; i >= 0; --i) {
                final RGB rgb = this.imgdata.palette.getRGB(this.rgb[i]);
                this.rgb[i] = (this.imgdata.alphaData[i] << 24) + ((rgb.red & 0xFF) << 16) + ((rgb.green & 0xFF) << 8) + (rgb.blue & 0xFF);
            }
        }
        else if (this.imgdata.transparentPixel != -1 && !this.imgdata.palette.isDirect) {
            for (int j = this.len - 1; j >= 0; --j) {
                int[] array;
                int n;
                int n2;
                if (this.rgb[j] == this.imgdata.transparentPixel) {
                    array = this.rgb;
                    n = j;
                    n2 = 0;
                }
                else {
                    final RGB rgb2 = this.imgdata.palette.getRGB(this.rgb[j]);
                    array = this.rgb;
                    n = j;
                    n2 = -16777216 + ((rgb2.red & 0xFF) << 16) + ((rgb2.green & 0xFF) << 8) + (rgb2.blue & 0xFF);
                }
                array[n] = n2;
            }
        }
        else {
            for (int k = this.len - 1; k >= 0; --k) {
                final RGB rgb3 = this.imgdata.palette.getRGB(this.rgb[k]);
                this.rgb[k] = -16777216 + ((rgb3.red & 0xFF) << 16) + ((rgb3.green & 0xFF) << 8) + (rgb3.blue & 0xFF);
            }
        }
        return this.rgb;
    }
    
    public final void setAlpha(int n, int n2, int n3, int n4, final int n5) {
        if (n >= this.imgdata.width || n2 >= this.imgdata.height) {
            return;
        }
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        if (n + n3 > this.imgdata.width) {
            n3 = this.imgdata.width - n;
        }
        if (n2 + n4 > this.imgdata.height) {
            n4 = this.imgdata.height - n2;
        }
        final byte[] array = new byte[n3];
        for (int i = 0; i < n3; ++i) {
            array[i] = (byte)n5;
        }
        if (this.imgdata.alphaData == null) {
            this.imgdata.alphaData = new byte[this.imgdata.width * this.imgdata.height];
        }
        for (int j = n2; j < n2 + n4; ++j) {
            System.arraycopy(array, 0, this.imgdata.alphaData, j * this.imgdata.width + n, n3);
        }
        if (this.disposed) {
            this.disposed = false;
            if (this.img != null && !this.img.isDisposed()) {
                this.img.dispose();
            }
            if (this.graphics != null) {
                this.graphics.finalize();
                this.graphics = null;
            }
        }
    }
    
    public final void setData(final int[] array) {
        this.imgdata = new ImageData(this.imgdata.width, this.imgdata.height, 32, ImageSWT.aPaletteData537);
        int n = this.len * 4 - 1;
        if (this.transparent) {
            if (this.imgdata.alphaData == null) {
                this.imgdata.alphaData = new byte[this.imgdata.width * this.imgdata.height];
            }
            for (int i = this.len - 1; i >= 0; --i) {
                this.imgdata.alphaData[i] = (byte)(array[i] >> 24 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[i] >> 24 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[i] >> 16 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[i] >> 8 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[i] & 0xFF);
            }
        }
        else {
            for (int j = this.len - 1; j >= 0; --j) {
                this.imgdata.data[n--] = (byte)(array[j] >> 24 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[j] >> 16 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[j] >> 8 & 0xFF);
                this.imgdata.data[n--] = (byte)(array[j] & 0xFF);
            }
        }
        if (this.disposed) {
            this.disposed = false;
            if (this.img != null && !this.img.isDisposed()) {
                this.img.dispose();
            }
            if (this.graphics != null) {
                this.graphics.finalize();
                this.graphics = null;
            }
        }
    }
    
    public final int getRGB(final int n, final int n2) {
        if (this.disposed) {
            try {
                return this.method300(this.img.getImageData().getPixel(n, n2));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return this.method300(this.imgdata.getPixel(n, n2));
            }
        }
        return this.method300(this.imgdata.getPixel(n, n2));
    }
    
    private int method300(final int n) {
        final RGB rgb = this.imgdata.palette.getRGB(n);
        return -16777216 + ((rgb.red & 0xFF) << 16) + ((rgb.green & 0xFF) << 8) + (rgb.blue & 0xFF);
    }
    
    public final void saveToFile(final String s) {
        if (this.disposed) {
            try {
                this.imgdata = this.img.getImageData();
            }
            catch (Exception ex) {
                ex.printStackTrace();}
        }
        try {
            ImageIO.write(emulator.graphics2D.c.method167(this.imgdata), "png", new File(s));
        }
        catch (Exception ex2) {
            ex2.printStackTrace();}
    }
    
    public final void copyToClipBoard() {
        if (this.disposed) {
            this.imgdata = this.img.getImageData();
        }
        emulator.graphics2D.c.method169(emulator.graphics2D.c.method167(this.imgdata));
    }
    
    public final void cloneImage(final IImage image) {
        ((ImageSWT)image).setData(this.getData());
    }
    
    static {
        aPaletteData537 = new PaletteData(65280, 16711680, -16777216);
    }

	public int size() {
		return len * 4;
	}

	public Object getNative() {
		return img;
	}
}
