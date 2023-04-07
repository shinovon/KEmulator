package javax.microedition.lcdui;

import emulator.graphics2D.*;
import emulator.debug.*;
import emulator.*;
import emulator.custom.*;
import java.io.*;

public class Image
{
    private boolean mutable;
    private IImage imageImpl;
    private IImage xrayBuffer;
    private IImage usedRegion;
    int usedCount;
	private boolean disposed;
    
    public Image(final IImage img) {
        super();
        this.imageImpl = img;
        this.usedRegion = Emulator.getEmulator().newImage(this.imageImpl.getWidth(), this.imageImpl.getHeight(), true);
        this.resetUsedRegion();
        Profiler.totalImagePixelCount += this.getWidth() * this.getHeight();
        ++Profiler.totalImageInstances;
    }
    
    public void finalize() {
        Profiler.totalImagePixelCount -= this.getWidth() * this.getHeight();
        --Profiler.totalImageInstances;
    }
    
    public IImage getImpl() {
        return this.imageImpl;
    }
    
    protected IImage getXRayBuffer() {
    	return this.xrayBuffer;
    }
    
    public IImage getUsedRegion() {
    	return this.usedRegion;
    }
    
    public void resetUsedRegion() {
        this.usedRegion.setAlpha(0, 0, this.getWidth(), this.getHeight(), 128);
        this.usedCount = 0;
    }
    
    public int getUsedCount() {
        return this.usedCount;
    }
    
    public Graphics getGraphics() {
        if (!this.mutable && !Devices.curPlatform.hasNokiaUI()) {
            throw new IllegalStateException("the image is immutable.");
        }
         if (this.xrayBuffer == null) {
            this.xrayBuffer = Emulator.getEmulator().newImage(this.getWidth(), this.getHeight(), true);
        }
        
        return new Graphics(this.imageImpl, xrayBuffer);
    }
    
    public int getWidth() {
        return this.imageImpl.getWidth();
    }
    
    public int getHeight() {
        return this.imageImpl.getHeight();
    }
    
    public boolean isMutable() {
        return this.mutable;
    }
    
    private static Image decode(final byte[] array) throws IllegalArgumentException {
        try {
            return new Image(Emulator.getEmulator().newImage(array));
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            Emulator.getEmulator().getLogStream().println("*** createImage error!! check it ***");
            throw new IllegalArgumentException(ex.toString());
        }
    }
    
    public static Image createImage(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return decode(array2);
    }
    
    public static Image createImage(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        try {
            return decode(emulator.custom.CustomJarResources.getBytes(inputStream));
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new IOException();
        }
    }
    
    public static Image createImage(final int n, final int n2) {
    	if(n <= 0 || n2 <= 0) throw new IllegalArgumentException();
        final Image image;
        (image = new Image(Emulator.getEmulator().newImage(n, n2, false))).mutable = true;
        image.xrayBuffer = Emulator.getEmulator().newImage(n, n2, true);
        return image;
    }
    
    public static Image createImage(final Image image) {
        return createImage(image, 0, 0, image.getWidth(), image.getHeight(), 0);
    }
    
    public static Image createImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        final boolean b;
        final int n6 = (b = (((n5 != 4 && n5 != 5 && n5 != 6 && n5 != 7) ? 1 : 0) != 0)) ? n3 : n4;
        final int n7 = b ? n4 : n3;
        final Image image2;
        (image2 = new Image(Emulator.getEmulator().newImage(n6, n7, true, 0))).mutable = true;
        image2.xrayBuffer = Emulator.getEmulator().newImage(n6, n7, true, 0);
        image2.getGraphics().drawRegion(image, n, n2, n3, n4, n5, 0, 0, 20);
        image2.mutable = false;
        return image2;
    }
    
    public static Image createImage(String string) throws IOException {
        if (string == null) {
            throw new NullPointerException();
        }
        try {
	        if (!string.startsWith("/")) {
	            string = "/" + string;
	        }
            return createImage(emulator.custom.CustomJarResources.getResourceStream(string));
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new IOException(string);
        }
    }
    
    public static Image createRGBImage(final int[] array, final int n, final int n2, final boolean b) {
        final Image image;
        emulator.graphics2D.b.method162((image = new Image(Emulator.getEmulator().newImage(n, n2, b))).imageImpl, array, b, 0, n, n, n2);
        return image;
    }
    
    public void getRGB(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        emulator.graphics2D.b.method165(this.imageImpl, array, n, n2, n3, n4, n5, n6);
    }
    
    void dispose() {
    	imageImpl = null;
    	usedRegion = null;
    	disposed = true;
    }

	public int size() {
		if(disposed) return 5;
		int i = 5 + imageImpl.size();
		if(xrayBuffer != null) i += xrayBuffer.size();
		if(usedRegion != null) i += usedRegion.size();
		return i;
	}
}
