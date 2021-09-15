package javax.microedition.lcdui;

import emulator.graphics2D.*;
import emulator.debug.*;
import emulator.*;
import emulator.custom.*;
import java.io.*;

public class Image
{
    private boolean aBoolean1307;
    private IImage anIImage1308;
    private IImage anIImage1310;
    private IImage anIImage1311;
    int anInt1309;
    
    public Image(final IImage anIImage1308) {
        super();
        this.anIImage1308 = anIImage1308;
        this.anIImage1311 = Emulator.getEmulator().newImage(this.anIImage1308.getWidth(), this.anIImage1308.getHeight(), true);
        this.resetUsedRegion();
        Profiler.totalImagePixelCount += this.getWidth() * this.getHeight();
        ++Profiler.totalImageInstances;
    }
    
    public void finalize() {
        Profiler.totalImagePixelCount -= this.getWidth() * this.getHeight();
        --Profiler.totalImageInstances;
    }
    
    public IImage getImpl() {
        return this.anIImage1308;
    }
    
    protected IImage getXRayBuffer() {
        return this.anIImage1310;
    }
    
    public IImage getUsedRegion() {
        return this.anIImage1311;
    }
    
    public void resetUsedRegion() {
        this.anIImage1311.setAlpha(0, 0, this.getWidth(), this.getHeight(), 128);
        this.anInt1309 = 0;
    }
    
    public int getUsedCount() {
        return this.anInt1309;
    }
    
    public Graphics getGraphics() {
        if (!this.aBoolean1307 && !Devices.curPlatform.hasNokiaUI()) {
            throw new IllegalStateException("the image is immutable.");
        }
        if (this.anIImage1310 == null) {
            this.anIImage1310 = Emulator.getEmulator().newImage(this.getWidth(), this.getHeight(), true);
        }
        return new Graphics(this.anIImage1308, this.anIImage1310);
    }
    
    public int getWidth() {
        return this.anIImage1308.getWidth();
    }
    
    public int getHeight() {
        return this.anIImage1308.getHeight();
    }
    
    public boolean isMutable() {
        return this.aBoolean1307;
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
        final Image image;
        (image = new Image(Emulator.getEmulator().newImage(n, n2, false))).aBoolean1307 = true;
        image.anIImage1310 = Emulator.getEmulator().newImage(n, n2, true);
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
        (image2 = new Image(Emulator.getEmulator().newImage(n6, n7, true, 0))).aBoolean1307 = true;
        image2.anIImage1310 = Emulator.getEmulator().newImage(n6, n7, true, 0);
        image2.getGraphics().drawRegion(image, n, n2, n3, n4, n5, 0, 0, 20);
        image2.aBoolean1307 = false;
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
            throw new IOException(string);
        }
    }
    
    public static Image createRGBImage(final int[] array, final int n, final int n2, final boolean b) {
        final Image image;
        emulator.graphics2D.b.method162((image = new Image(Emulator.getEmulator().newImage(n, n2, b))).anIImage1308, array, b, 0, n, n, n2);
        return image;
    }
    
    public void getRGB(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        emulator.graphics2D.b.method165(this.anIImage1308, array, n, n2, n3, n4, n5, n6);
    }
}
