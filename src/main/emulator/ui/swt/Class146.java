package emulator.ui.swt;

import org.eclipse.swt.widgets.*;
import java.util.*;
import emulator.*;
import emulator.ui.*;
import emulator.graphics2D.*;
import emulator.graphics2D.swt.*;
import emulator.graphics2D.swt.FontSWT;
import emulator.graphics2D.swt.ImageSWT;
import emulator.graphics3D.*;
import org.eclipse.swt.graphics.*;

public final class Class146 implements IEmulator
{
    private static Display aDisplay1378;
    private Vector aVector1379;
    private int anInt1380;
    private Class46 aClass46_1381;
    private Class110 aClass110_1382;
    private Class5 aClass5_1377;
    private Class5 aClass5_1391;
    private Class38 aClass38_1383;
    private EmulatorScreen aClass93_1385;
    private Class11 aClass11_1386;
    private Class161 aClass161_1387;
    private Class108 aClass108_1390;
    private Class90 aClass90_1384;
    private Class83 aClass83_1389;
    public Properties midletProps;
	//private static Hashtable<String, a> swtFontsCache = new Hashtable<String, a>();
    
    public Class146() {
        super();
        Class146.aDisplay1378 = new Display();
        this.aVector1379 = new Vector();
        this.anInt1380 = ((Device)Class146.aDisplay1378).getDepth();
        this.aClass38_1383 = new Class38();
        this.aClass11_1386 = new Class11();
        this.aClass83_1389 = new Class83();
        this.aClass108_1390 = new Class108();
        this.aClass161_1387 = new Class161();
        this.aClass5_1377 = new Class5(0);
        this.aClass5_1391 = new Class5(1);
        this.aClass110_1382 = new Class110();
        this.aClass46_1381 = new Class46();
        this.aClass90_1384 = new Class90();
    }
    
    public static void dispose() {
        if (!((Device)Class146.aDisplay1378).isDisposed()) {
            ((Device)Class146.aDisplay1378).dispose();
        }
    }
    
    public static Display getDisplay() {
        return Class146.aDisplay1378;
    }
    
    public static void syncExec(final Runnable runnable) {
        if (!((Device)Class146.aDisplay1378).isDisposed()) {
            Class146.aDisplay1378.syncExec(runnable);
        }
    }
    
    public static void asyncExec(final Runnable runnable) {
        if (!((Device)Class146.aDisplay1378).isDisposed()) {
            Class146.aDisplay1378.asyncExec(runnable);
        }
    }
    
    public final int getScreenDepth() {
        return this.anInt1380;
    }
    
    public final EmulatorScreen method821() {
        if (this.aClass93_1385 == null) {
            this.aClass93_1385 = new EmulatorScreen(Devices.method617("SCREEN_WIDTH"), Devices.method617("SCREEN_HEIGHT"));
        }
        return this.aClass93_1385;
    }
    
    public final Class5 method822() {
        return this.aClass5_1377;
    }
    
    public final Class5 method829() {
        return this.aClass5_1391;
    }
    
    public final Class110 method823() {
        return this.aClass110_1382;
    }
    
    public final Class46 method824() {
        return this.aClass46_1381;
    }
    
    public final Class108 method825() {
        return this.aClass108_1390;
    }
    
    public final Class161 method826() {
        return this.aClass161_1387;
    }
    
    public final Class90 method827() {
        return this.aClass90_1384;
    }
    
    public final void disposeSubWindows() {
        Settings.showLogFrame = this.aClass11_1386.method327();
        Settings.showInfoFrame = this.aClass108_1390.method610();
        this.aClass161_1387.method836();
        this.aClass11_1386.method330();
        this.aClass83_1389.method482();
        this.aClass108_1390.method608();
        Settings.showMemViewFrame = this.aClass110_1382.method622();
        this.aClass46_1381.method446();
        this.aClass110_1382.method656();
        this.aClass5_1377.method321();
        this.aClass5_1391.method321();
        this.aClass90_1384.method507();
        while (Class5.aVector548.size() > 0) {
            ((Class5) Class5.aVector548.get(0)).method321();
        }
        for (int i = 0; i < this.aVector1379.size(); ++i) {
            ((IPlugin)this.aVector1379.get(i)).close();
        }
    }
    
    public final void pushPlugin(final IPlugin plugin) {
        this.aVector1379.add(plugin);
    }
    
    public final ILogStream getLogStream() {
        return this.aClass11_1386;
    }
    
    public final IProperty getProperty() {
        return this.aClass38_1383;
    }
    
    public final IScreen getScreen() {
        return this.aClass93_1385;
    }
    
    public final IMessage getMessage() {
        return this.aClass83_1389;
    }
    
    public final IFont newFont(final int n, final int n2) {
        if (Settings.g2d == 0) {
        	//String s = this.aClass38_1383.getDefaultFontName() + "." + n + "." + n2;
        	//if(swtFontsCache.containsKey(s)) return swtFontsCache.get(s);
            FontSWT f = new FontSWT(this.aClass38_1383.getDefaultFontName(), n, n2);
            //swtFontsCache.put(s, f);
           // return f;
        }
        if (Settings.g2d == 1) {
            return new emulator.graphics2D.awt.a(this.aClass38_1383.getDefaultFontName(), n, n2);
        }
        return null;
    }
    
    public final IImage newImage(final int n, final int n2, final boolean b) {
        if (Settings.g2d == 0) {
            return new ImageSWT(n, n2, b, -1);
        }
        if (Settings.g2d == 1) {
            return new emulator.graphics2D.awt.d(n, n2, b, -1);
        }
        return null;
    }
    
    public final IImage newImage(int n, int n2, boolean b, int n3) {
        return (IImage)(Settings.g2d == 0?new ImageSWT(n, n2, b, n3):(Settings.g2d == 1?new emulator.graphics2D.awt.d(n, n2, b, n3):null));
     }

    
    public final IImage newImage(final byte[] array) {
        if (Settings.g2d == 0) {
            return new ImageSWT(array);
        }
        if (Settings.g2d == 1) {
            return new emulator.graphics2D.awt.d(array);
        }
        return null;
    }
    
    public final IGraphics3D getGraphics3D() {
        return null;
    }
    
    public final void syncValues() {
        for (int i = 0; i < Class5.aVector548.size(); ++i) {
            asyncExec((Runnable)Class5.aVector548.get(i));
        }
        if (this.aClass46_1381.method438()) {
            asyncExec(this.aClass46_1381);
        }
    }
    
    public final String getAppProperty(final String s) {
        final String property;
        if ((property = this.midletProps.getProperty(s)) != null) {
            return property.trim();
        }
        return null;
    }
}
