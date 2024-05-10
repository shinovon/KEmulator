package emulator.graphics2D.awt;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;

import emulator.graphics2D.*;
import java.awt.*;
import java.awt.Color;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.util.Arrays;

/**
 * ImageAWT
 */
public final class d implements IImage
{
    private BufferedImage img;
    private b graphics;
    private Graphics2D g2d;
    private int[] data;

    public d(final byte[] array) {
        super();
        img = emulator.graphics2D.c.toAwt(new ImageData(new ByteArrayInputStream(array)));
    }
    
    public d(final BufferedImage bi) {
        super();
        img = bi;
    }
    
    public d(final int n, final int n2, final boolean b, final int n3) {
        super();
        img = new BufferedImage(n, n2, b ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = g2d = img.createGraphics();
        g.setColor(new Color(n3, b));
        g.fillRect(0, 0, n, n2);
    }
    
    public final BufferedImage getBufferedImage() {
        return img;
    }
    
    public final void copyToScreen(Object g, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        GC gc = (GC) g;
        final Image image = new Image(null, emulator.graphics2D.c.toSwt(img));
        gc.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8);
        image.dispose();
    }

    public boolean isTransparent() {
        return img.getType() == BufferedImage.TYPE_INT_ARGB;
    }

    public void fill(int color) {
        Arrays.fill(getInternalData(), color);
    }

    public final void copyToScreen(Object g) {
        GC gc = (GC) g;
        final Image image = new Image(null, emulator.graphics2D.c.toSwt(img));
        gc.drawImage(image, 0, 0);
        image.dispose();
    }
    
    public final IGraphics2D createGraphics() {
        return this.graphics = new b(img);
    }
    
    public final IGraphics2D getGraphics() {
        return this.graphics;
    }
    
    public final int getWidth() {
        return img.getWidth();
    }
    
    public final int getHeight() {
        return img.getHeight();
    }
    
    public final int[] getData() {
    	try {
	        final int[] data = getInternalData();
	        if (!img.getColorModel().hasAlpha()) {
	            for (int i = data.length - 1; i >= 0; --i) {
	                data[i] |= 0xFF000000;
	            }
	        }
	        return data;
    	} catch (ClassCastException e) {
    		e.printStackTrace();
            final byte[] data = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
            final int[] intdata = new int[data.length];
            for(int i = 0; i < data.length; i++) {
                intdata[i] = ((data[i++] & 0xFF) << 24) +
                        ((data[i++] & 0xFF) << 16) +
                        ((data[i++] & 0xFF) << 8) +
                        (data[i++] & 0xFF);
//            	intdata[i] = data[i];
            }
            return intdata;
    	}
    }
    
    public final void setData(final int[] array) {
        final int[] data = getInternalData();
        if(array.length != data.length) return;
        System.arraycopy(array, 0, data, 0, array.length);
    }
    
    public final void setAlpha(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n5 == 0) {
            Graphics2D g = g2d;
            if(g == null) g = img.createGraphics();
            g.setComposite(AlphaComposite.getInstance(1, 0.0f));
            g.fillRect(n, n2, n3, n4);
            return;
        }
        final int[] data = this.data != null ? this.data : ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        final int[] array = new int[n3];
        for (int i = 0; i < n3; ++i) {
            array[i] = n5 << 24;
        }
        for (int j = n2; j < n2 + n4; ++j) {
            System.arraycopy(array, 0, data, n + j * this.getWidth(), array.length);
        }
    }
    
    public final int getRGB(int x, int y) {
        return img.getRGB(x, y);
    }

    public void setRGB(int x, int y, int color) {
        img.setRGB(x, y, color | 0xFF000000);
    }
    
    public final void saveToFile(final String s) {
        try {
            ImageIO.write(img, "png", new File(s));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public final void copyToClipBoard() {
        emulator.graphics2D.c.setClipboard(img);
    }
    
    public final void cloneImage(final IImage image) {
        System.arraycopy(getInternalData(), 0, ((d)image).getInternalData(), 0, data.length);
    }

    public void cloneImage(IImage image, int x, int y, int w, int h) {
        Graphics2D g = ((d)image).g2d;
        if(g == null) {
            if (((d) image).graphics != null) {
                g = ((d) image).graphics.g;
            } else {
                g = ((d) image).g2d = img.createGraphics();
            }
        }
        g.drawImage(img, x, y, w, h, null);
    }

    private int[] getInternalData() {
        if(data == null) {
            data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        }
        return data;
    }

    public int size() {
		return getInternalData().length;
	}

	public Object getNative() {
		return getBufferedImage();
	}
}
