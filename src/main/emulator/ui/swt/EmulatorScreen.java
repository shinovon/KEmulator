package emulator.ui.swt;

import org.eclipse.swt.custom.*;
import emulator.graphics2D.swt.*;
import emulator.graphics2D.swt.ImageSWT;
import emulator.media.*;
import emulator.graphics2D.*;
import emulator.debug.*;
import emulator.*;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.*;
import java.io.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.dnd.*;
import emulator.ui.*;
import java.text.*;
import java.util.*;

import javax.microedition.lcdui.Graphics;
import javax.microedition.media.CapturePlayerImpl;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

public final class EmulatorScreen implements 
IScreen, Runnable, PaintListener, DisposeListener, 
ControlListener, KeyListener, MouseListener,
MouseMoveListener, SelectionListener, MouseWheelListener,
MouseTrackListener
{
    private static Display display;
    private Shell shell;
    private Canvas canvas;
    private CLabel aCLabel970;
    private CLabel aCLabel984;
    private CLabel aCLabel994;
    private Menu aMenu971;
    private Menu menuMidlet;
    private Menu menuTool;
    private Menu menuView;
    private Menu menu2dEngine;
    private Menu menu3dEngine;
    private Menu aMenu1018;
    private Menu menuInterpolation;
    public static int locX;
    public static int locY;
    private float zoom;
    private int anInt996;
    private int anInt1003;
    private Image anImage974;
    private ImageSWT ad979;
    private ImageSWT screenImageSwt;
    private ImageSWT backBufferImageSwt;
    private ImageSWT xrayScreenImageSwt;
    private emulator.graphics2D.awt.d ad980;
    private emulator.graphics2D.awt.d screenImageAwt;
    private emulator.graphics2D.awt.d backBufferImageAwt;
    private emulator.graphics2D.awt.d xrayScreenImageAwt;
    private static long aLong982;
    private String aString983;
    private String aString989;
    MenuItem awt2dMenuItem;
    MenuItem swt2dMenuItem;
    MenuItem swt3dMenuItem;
    MenuItem lwj3dMenuItem;
    MenuItem loadJarMenuItem;
    MenuItem loadWithConsoleMenuItem;
    MenuItem loadAutoPlayMenuItem;
    MenuItem openJadMenuItem;
    MenuItem suspendMenuItem;
    MenuItem resumeMenuItem;
    MenuItem pausestepMenuItem;
    MenuItem playResumeMenuItem;
    MenuItem restartMenuItem;
    MenuItem exitMenuItem;
    MenuItem zoomInMenuItem;
    MenuItem zoomOutMenuItem;
    MenuItem interposeNearestMenuItem;
    MenuItem interposeLowMenuItem;
    MenuItem interposeHightMenuItem;
    MenuItem speedUpMenuItem;
    MenuItem slowDownMenuItem;
    MenuItem startpauseTickMenuItem;
    MenuItem resetTickMenuItem;
    MenuItem recordKeysMenuItem;
    MenuItem enableAutoplayMenuItem;
    MenuItem captureToFileMenuItem;
    MenuItem captureToClipboardMenuItem;
    MenuItem startRecordAviMenuItem;
    MenuItem stopRecordAviMenuItem;
    MenuItem connectNetworkMenuItem;
    MenuItem disconnectNetworkMenuItem;
    MenuItem channelUpMenuItem;
    MenuItem channelDownMenuItem;
    MenuItem showTrackInfoMenuItem;
    MenuItem aMenuItem949;
    MenuItem aMenuItem950;
    MenuItem xrayViewMenuItem;
    MenuItem infosMenuItem;
    MenuItem alwaysOnTopMenuItem;
    MenuItem rotateScreenMenuItem;
    MenuItem forecPaintMenuItem;
    MenuItem aMenuItem956;
    MenuItem aMenuItem957;
    MenuItem aMenuItem958;
    MenuItem aMenuItem959;
    MenuItem aMenuItem960;
    MenuItem aMenuItem961;
    MenuItem aMenuItem962;
    MenuItem aMenuItem963;
    MenuItem aMenuItem964;
    private static AVIWriter aviWriter;
    private static int anInt1012;
    private static String aString993;
    private static long aLong991;
    private static long aLong1000;
    private static long aLong1007;
    private static boolean aBoolean967;
    private static boolean aBoolean992;
    private int pauseState;
    private final String[] aStringArray981;
    private boolean infosEnabled;
    private String aString1008;
    private Class67 aClass67_973;
    private int anInt1020;
    private boolean[] aBooleanArray978;
    private int mouseXPress;
    private int mouseXRelease;
    private int mouseYPress;
    private int mouseYRelease;
    private boolean mouseDownInfos;
    private Vibrate aClass119_976;
    private long aLong1013;
    private long aLong1017;
	private MenuItem canvasKeyboardMenuItem;
	private MenuItem fpsModeMenuItem;
	private int lastMouseMoveX;
	private boolean fpsWasRight;
	private boolean fpsWasLeft;
	private boolean ignoreNextFps;
	private boolean fpsWasnt;
	private boolean mset;
    
    public EmulatorScreen(final int n, final int n2) {
        super();
        this.shell = null;
        this.canvas = null;
        this.aCLabel970 = null;
        this.aCLabel984 = null;
        this.aCLabel994 = null;
        this.aMenu971 = null;
        this.menuMidlet = null;
        this.menuTool = null;
        this.menuView = null;
        this.menu2dEngine = null;
        this.menu3dEngine = null;
        this.aMenu1018 = null;
        this.aStringArray981 = new String[] { emulator.UILocale.uiText("MAIN_INFO_BAR_UNLOADED", "UNLOADED"), emulator.UILocale.uiText("MAIN_INFO_BAR_RUNNING", "RUNNING"), emulator.UILocale.uiText("MAIN_INFO_BAR_PAUSED", "PAUSED") };
        EmulatorScreen.display = EmulatorImpl.getDisplay();
        this.method585();
        this.method550(n, n2);
        this.method587();
    }
    
    private void method550(final int n, final int n2) {
        if (Settings.g2d == 0) {
            this.ad979 = new ImageSWT(n, n2, false, 9934483);
            this.screenImageSwt = new ImageSWT(n, n2, false, 9934483);
            this.backBufferImageSwt = new ImageSWT(n, n2, false, 9934483);
            this.xrayScreenImageSwt = new ImageSWT(n, n2, true, 9934483);
            return;
        }
        if (Settings.g2d == 1) {
            this.ad980 = new emulator.graphics2D.awt.d(n, n2, false, 9934483);
            this.screenImageAwt = new emulator.graphics2D.awt.d(n, n2, false, 9934483);
            this.backBufferImageAwt = new emulator.graphics2D.awt.d(n, n2, false, 9934483);
            this.xrayScreenImageAwt = new emulator.graphics2D.awt.d(n, n2, true, -16777216);
        }
    }
    
    public final void method551(final InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        try {
            ((Decorations)this.shell).setImage(new Image((Device)null, inputStream));
        }
        catch (Exception ex) {}
    }
    
    public final void method552(final String message) {
        method557(((Control)this.shell).handle, true);
        final MessageBox messageBox;
        ((Dialog)(messageBox = new MessageBox(this.shell))).setText(emulator.UILocale.uiText("MESSAGE_BOX_TITLE", "KEmulator Alert"));
        messageBox.setMessage(message);
        messageBox.open();
    }
    
    private void method576() {
        EmulatorScreen.locX = this.shell.getLocation().x;
        EmulatorScreen.locY = this.shell.getLocation().y;
    }
    
    public final void method553(final boolean anInt1016) {
        try {
            this.zoom(Settings.canvasScale / 100.0f);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.method552(emulator.UILocale.uiText("LOAD_GDIPLUS_ERROR", "Can't load \" gdiplus.dll \" !!! Plz download & copy to %system32% path."));
            return;
        }
        this.pauseState = (anInt1016 ? 1 : 0);
        this.method587();
        if (EmulatorScreen.locX < 0) {
            EmulatorScreen.locX = EmulatorScreen.display.getClientArea().width - this.shell.getSize().x >> 1;
        }
        if (EmulatorScreen.locY < 0) { 
            EmulatorScreen.locY = EmulatorScreen.display.getClientArea().height - this.shell.getSize().y >> 1;
        }
        ((Control)this.shell).setLocation(EmulatorScreen.locX, EmulatorScreen.locY);
        EmulatorImpl.asyncExec(new WindowOpen(this, 0));
        EmulatorImpl.asyncExec(new WindowOpen(this, 1));
        EmulatorImpl.asyncExec(new WindowOpen(this, 2));
        this.shell.open();
        ((Widget)this.shell).addDisposeListener((DisposeListener)this);
        ((Control)this.shell).addControlListener((ControlListener)this);

    	try
    	{
	        while (this.shell != null && !((Widget)this.shell).isDisposed()) {
	            if (!EmulatorScreen.display.readAndDispatch()) {
	                EmulatorScreen.display.sleep();
	            }
	        }
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		System.exit(-1);
    	}
        this.pauseState = 0;
    }
    

    private void method569(int var1, int var2) {
       if(this.pauseState == 1 && Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
          this.method550(var1, var2);
          this.zoom(this.zoom);
          Emulator.getEventQueue().queue(Integer.MIN_VALUE, var1, var2);
       }
    }

    

    private void zoom(float var1) {
       this.zoom = var1;
       this.anInt996 = (int)((float)this.getWidth() * this.zoom);
       this.anInt1003 = (int)((float)this.getHeight() * this.zoom);
       Settings.canvasScale = (int)(this.zoom * 100.0F);
       this.aClass67_973.method469(this.zoom);
       this.method579();
       this.method584();
    }

    
    private void method579() {
        int var1 = this.shell.getSize().x - this.canvas.getSize().x;
        int var2 = this.shell.getSize().y - this.canvas.getSize().y;
        this.canvas.setSize((int)((float)this.getWidth() * this.zoom) + this.canvas.getBorderWidth() * 2, (int)((float)this.getHeight() * this.zoom) + this.canvas.getBorderWidth() * 2);
        this.shell.setSize(this.canvas.getSize().x + var1, this.canvas.getSize().y + var2);
        this.canvas.redraw();
     }

    
    private void zoomIn() {
        if (this.zoom < 5.0) {
            this.zoom(this.zoom + 0.5f);
        }
    }
    
    private void zoomOut() {
        if (this.zoom > 1.0f) {
            this.zoom(this.zoom - 0.5f);
        }
    }
    
    private void method583() {
        if (this.anImage974 != null && !this.anImage974.isDisposed()) {
            this.anImage974.dispose();
        }
        this.anImage974 = new Image((Device)null, this.getWidth(), this.getHeight());
        final GC gc = new GC((Drawable)this.anImage974);
        if (Settings.g2d == 0) {
            this.screenImageSwt.method13(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight());
        }
        else if (Settings.g2d == 1) {
            this.screenImageAwt.method13(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight());
        }
        if (this.pauseState == 2) {
            gc.setAlpha(100);
            gc.setBackground(EmulatorScreen.display.getSystemColor(15));
            gc.fillRectangle(0, 0, this.getWidth(), this.getHeight());
        }
        gc.dispose();
    }
    
    public final IImage getScreenImage() {
        if (Settings.g2d == 0) {
            return this.screenImageSwt;
        }
        if (Settings.g2d == 1) {
            return this.screenImageAwt;
        }
        return null;
    }
    
    public final IImage getBackBufferImage() {
        if (Settings.g2d == 0) {
            return this.backBufferImageSwt;
        }
        if (Settings.g2d == 1) {
            return this.backBufferImageAwt;
        }
        return null;
    }
    
    public final IImage getXRayScreenImage() {
        if (Settings.g2d == 0) {
            return this.xrayScreenImageSwt;
        }
        if (Settings.g2d == 1) {
            return this.xrayScreenImageAwt;
        }
        return null;
    }
    
    public final void repaint() {
        if (Emulator.getCurrentDisplay().getCurrent() == null || this.pauseState == 0) {
            return;
        }
        if (Settings.q) {
            KeyRecords h = Emulator.getRobot();
            String method698;
            while ((method698 = h.method698(EmulatorScreen.aLong982)) != null && method698.length() > 1) {
                final char char1 = method698.charAt(0);
                final String substring = method698.substring(1);
                if (char1 == '0') {
                    method578(Integer.parseInt(substring));
                }
                else if (char1 == '1') {
                    method580(Integer.parseInt(substring));
                }
                h = Emulator.getRobot();
            }
        }
        else if (Settings.enableKeyCache && !Keyboard.aStack1063.empty()) {
            final String s = (String) Keyboard.aStack1063.pop();
            if (Settings.recordKeys && !Settings.q) {
                Emulator.getRobot().method697(EmulatorScreen.aLong982 + ":" + s);
            }
            final char char2 = s.charAt(0);
            final String substring2 = s.substring(1);
            if (char2 == '0') {
                method578(Integer.parseInt(substring2));
            }
            else if (char2 == '1') {
                method580(Integer.parseInt(substring2));
            }
        }
        EmulatorImpl.asyncExec(this);
    }
    
    public final int getWidth() {
        return this.getScreenImage().getWidth();
    }
    
    public final int getHeight() {
        return this.getScreenImage().getHeight();
    }
    
    public final void setCommandLeft(final String aString983) {
        this.aString983 = aString983;
        EmulatorImpl.syncExec(new Class41(this));
    }
    
    public final void setCommandRight(final String aString989) {
        this.aString989 = aString989;
        EmulatorImpl.syncExec(new Class40(this));
    }
    

    private void method584() {
       long var1 = 0L;
       long var3;
       long var5 = (var3 = ((aBoolean967?aLong1007:System.currentTimeMillis()) - aLong1000) / 1000L) / 60L;
       var3 %= 60L;
       String var7 = var5 < 10L?0 + String.valueOf(var5):String.valueOf(var5);
       var7 = var7 + ":" + (var3 < 10L?0 + String.valueOf(var3):String.valueOf(var3));
       String var8 = this.zoom == 1.0F?" ":"  ";
       StringBuffer var9;
       (var9 = new StringBuffer()).append((int)(this.zoom * 100.0F));
       var9.append("%");
       var9.append(var8);
       if(this.pauseState != 0 && Emulator.getNetMonitor().b()) {
          var9.append(emulator.UILocale.uiText("MAIN_INFO_BAR_NET", "NET"));
          var9.append("(");
          var9.append(Emulator.getNetMonitor().aInt());
          var9.append(")");
       } else {
          var9.append(this.aStringArray981[this.pauseState]);
       }

       var9.append(var8);
       var9.append(var7);
       var9.append(var8);
       if(Settings.f > 0) {
          var9.append("x");
       }

       var9.append(Settings.f);
       this.aCLabel994.setText(var9.toString());
    }

    
    private void method585() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalAlignment = 4;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.verticalAlignment = 2;
        final GridData layoutData2;
        (layoutData2 = new GridData()).horizontalAlignment = 3;
        layoutData2.verticalSpan = 1;
        layoutData2.grabExcessHorizontalSpace = false;
        layoutData2.verticalAlignment = 2;
        final GridData layoutData3;
        (layoutData3 = new GridData()).horizontalAlignment = 1;
        layoutData3.verticalSpan = 1;
        layoutData3.grabExcessHorizontalSpace = false;
        layoutData3.verticalAlignment = 2;
        final GridLayout layout;
        (layout = new GridLayout()).numColumns = 3;
        layout.horizontalSpacing = 5;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.makeColumnsEqualWidth = false;
        ((Decorations)(this.shell = new Shell(224))).setText(Emulator.getVersionString());
        ((Composite)this.shell).setLayout((Layout)layout);
        this.method588();
        (this.aCLabel970 = new CLabel((Composite)this.shell, 0)).setText("\t");
        ((Control)this.aCLabel970).setLayoutData((Object)layoutData3);
        ((Control)this.aCLabel970).addMouseListener((MouseListener)new Class43(this));
        (this.aCLabel994 = new CLabel((Composite)this.shell, 16777216)).setText("");
        ((Control)this.aCLabel994).setLayoutData((Object)layoutData);
        (this.aCLabel984 = new CLabel((Composite)this.shell, 131072)).setText("\t");
        ((Control)this.aCLabel984).setLayoutData((Object)layoutData2);
        ((Control)this.aCLabel984).addMouseListener((MouseListener)new Class50(this));
        this.method586();
        ((Decorations)this.shell).setImage(new Image((Device)Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
        this.shell.addShellListener((ShellListener)new Class53(this));
    }
    
    private void method586() {
        this.aMenu971 = new Menu((Decorations)this.shell, 2);
        final MenuItem menuItemMidlet;
        (menuItemMidlet = new MenuItem(this.aMenu971, 64)).setText(emulator.UILocale.uiText("MENU_MIDLET", "Midlet"));
        final MenuItem menuItemTool;
        (menuItemTool = new MenuItem(this.aMenu971, 64)).setText(emulator.UILocale.uiText("MENU_TOOL", "Tool"));
        final MenuItem menuItemView;
        (menuItemView = new MenuItem(this.aMenu971, 64)).setText(emulator.UILocale.uiText("MENU_VIEW", "View"));
        this.menuView = new Menu(menuItemView);
        (this.infosMenuItem = new MenuItem(this.menuView, 32)).setText(emulator.UILocale.uiText("MENU_VIEW_INFO", "Infos") + "\tI");
        this.infosMenuItem.addSelectionListener((SelectionListener)this);
        (this.xrayViewMenuItem = new MenuItem(this.menuView, 32)).setText(emulator.UILocale.uiText("MENU_VIEW_XRAY", "X-Ray View") + "\tX");
        this.xrayViewMenuItem.addSelectionListener((SelectionListener)this);
        (this.alwaysOnTopMenuItem = new MenuItem(this.menuView, 32)).setText(emulator.UILocale.uiText("MENU_VIEW_TOP", "Always On Top") + "\tO");
        this.alwaysOnTopMenuItem.addSelectionListener((SelectionListener)this);
        this.alwaysOnTopMenuItem.setSelection(Settings.alwaysOnTop);
        (this.rotateScreenMenuItem = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_ROTATE", "Rotate Screen") + "\tY");
        this.rotateScreenMenuItem.addSelectionListener((SelectionListener)this);
        (this.forecPaintMenuItem = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_FORCE_PAINT", "Force Paint") + "\tF");
        this.forecPaintMenuItem.addSelectionListener((SelectionListener)this);
        method557(((Control)this.shell).handle, Settings.alwaysOnTop);
        new MenuItem(this.menuView, 2);
        (this.aMenuItem956 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_KEYPAD", "Keypad"));
        this.aMenuItem956.addSelectionListener((SelectionListener)this);
        (this.aMenuItem958 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_WATCHES", "Watches"));
        this.aMenuItem958.addSelectionListener((SelectionListener)this);
        (this.aMenuItem959 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_PROFILER", "Profiler"));
        this.aMenuItem959.addSelectionListener((SelectionListener)this);
        (this.aMenuItem960 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_METHODS", "Methods"));
        this.aMenuItem960.addSelectionListener((SelectionListener)this);
        (this.aMenuItem961 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_MEMORY", "Memory View"));
        this.aMenuItem961.addSelectionListener((SelectionListener)this);
        (this.aMenuItem962 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_M3GVIEW", "M3G View"));
        this.aMenuItem962.addSelectionListener((SelectionListener)this);
        (this.aMenuItem963 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_SMS", "SMS Console"));
        this.aMenuItem963.addSelectionListener((SelectionListener)this);
        (this.aMenuItem964 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_SENSOR", "Sensor Simulator"));
        this.aMenuItem964.addSelectionListener((SelectionListener)this);
        (this.aMenuItem957 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_LOG", "Log"));
        this.aMenuItem957.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuView, 2);
        (this.aMenuItem950 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_OPTIONS", "Options..."));
        this.aMenuItem950.addSelectionListener((SelectionListener)this);
        (this.aMenuItem949 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_HELP", "Help"));
        this.aMenuItem949.addSelectionListener((SelectionListener)this);
        menuItemView.setMenu(this.menuView);
        this.menuTool = new Menu(menuItemTool);
        (this.zoomInMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_ZOOMIN", "Zoom In") + "\tPad +");
        this.zoomInMenuItem.addSelectionListener((SelectionListener)this);
        (this.zoomOutMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_ZOOMOUT", "Zoom Out") + "\tPad -");
        this.zoomOutMenuItem.addSelectionListener((SelectionListener)this);
        final MenuItem menuItem4;
        (menuItem4 = new MenuItem(this.menuTool, 64)).setText(emulator.UILocale.uiText("MENU_TOOL_INTERPOSE", "Interpolation"));
        this.menuInterpolation = new Menu((Decorations)this.shell, 4194308);
        (this.interposeNearestMenuItem = new MenuItem(this.menuInterpolation, 16)).setText(emulator.UILocale.uiText("MENU_TOOL_INTER_NEAREST", "NearestNeighbor"));
        this.interposeNearestMenuItem.setSelection(true);
        this.interposeNearestMenuItem.addSelectionListener((SelectionListener)new Class52(this));
        (this.interposeLowMenuItem = new MenuItem(this.menuInterpolation, 16)).setText(emulator.UILocale.uiText("MENU_TOOL_INTER_LOW", "LowQuality"));
        this.interposeLowMenuItem.addSelectionListener((SelectionListener)new Class55(this));
        (this.interposeHightMenuItem = new MenuItem(this.menuInterpolation, 16)).setText(emulator.UILocale.uiText("MENU_TOOL_INTER_HIGH", "HighQuality"));
        this.interposeHightMenuItem.addSelectionListener((SelectionListener)new Class42(this));
        menuItem4.setMenu(this.menuInterpolation);
        new MenuItem(this.menuTool, 2);
        (this.speedUpMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_SPPEDUP", "Speed Up") + "\t>");
        this.speedUpMenuItem.addSelectionListener((SelectionListener)this);
        (this.slowDownMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_SPPEDDONW", "Slow Down") + "\t<");
        this.slowDownMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.startpauseTickMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_TICKSTART", "Start/Pause Tick") + "\tK");
        this.startpauseTickMenuItem.addSelectionListener((SelectionListener)this);
        (this.resetTickMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_TICKRESET", "Reset Tick") + "\tL");
        this.resetTickMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.recordKeysMenuItem = new MenuItem(this.menuTool, 32)).setText(emulator.UILocale.uiText("MENU_TOOL_RECORD_KEY", "Record Keys"));
        this.recordKeysMenuItem.addSelectionListener((SelectionListener)this);
        this.recordKeysMenuItem.setSelection(Settings.recordKeys);
        this.recordKeysMenuItem.setEnabled(!Settings.q);
        (this.enableAutoplayMenuItem = new MenuItem(this.menuTool, 32)).setText(emulator.UILocale.uiText("MENU_TOOL_AUTO_PLAY", "Enable Autoplay"));
        this.enableAutoplayMenuItem.addSelectionListener((SelectionListener)this);
        this.enableAutoplayMenuItem.setSelection(Settings.q);
        this.enableAutoplayMenuItem.setEnabled(Settings.q);
        new MenuItem(this.menuTool, 2);
        (this.captureToFileMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_CAPTURE_FILE", "Capture to File") + "\tC");
        this.captureToFileMenuItem.addSelectionListener((SelectionListener)this);
        (this.captureToClipboardMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_CAPTURE_CLIP", "Capture to ClipBoard") + "\tAlt+C");
        this.captureToClipboardMenuItem.setAccelerator(65603);
        this.captureToClipboardMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.startRecordAviMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_START_RECORD_AVI", "Start Record AVI") + "\tV");
        this.startRecordAviMenuItem.addSelectionListener((SelectionListener)this);
        (this.stopRecordAviMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_STOP_RECORD_AVI", "Stop Record AVI") + "\tB");
        this.stopRecordAviMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.connectNetworkMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_CONN_NETWORK", "Connect to Network"));
        this.connectNetworkMenuItem.addSelectionListener((SelectionListener)this);
        (this.disconnectNetworkMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_DISCONN_NETWORK", "Disconnect to Network"));
        this.disconnectNetworkMenuItem.addSelectionListener((SelectionListener)this);
        (this.channelUpMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_CHANNEL_UP", "Channel up") + "\tCtrl+>");
        this.channelUpMenuItem.addSelectionListener((SelectionListener)this);
        this.channelUpMenuItem.setAccelerator(262206);
        (this.channelDownMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.uiText("MENU_TOOL_CHANNEL_DOWN", "Channel down") + "\tCtrl+<");
        this.channelDownMenuItem.addSelectionListener((SelectionListener)this);
        this.channelDownMenuItem.setAccelerator(262204);
        new MenuItem(this.menuTool, 2);
        (this.showTrackInfoMenuItem = new MenuItem(this.menuTool, 32)).setText(emulator.UILocale.uiText("MENU_TOOL_SHOW_TRACK_INFO", "Show Track Info") + "\tF3");
        this.showTrackInfoMenuItem.setSelection(Settings.threadMethodTrack);
        this.showTrackInfoMenuItem.addSelectionListener((SelectionListener)this);
        this.showTrackInfoMenuItem.setAccelerator(16777228);
        this.canvasKeyboardMenuItem = new MenuItem(this.menuTool, 32);
        canvasKeyboardMenuItem.setText("Canvas Keyboard");
        canvasKeyboardMenuItem.setSelection(Settings.canvasKeyboard);
        canvasKeyboardMenuItem.addSelectionListener((SelectionListener)this);
        this.fpsModeMenuItem = new MenuItem(this.menuTool, 32);
        fpsModeMenuItem.setText("FPS Mode");
        fpsModeMenuItem.setSelection(Settings.fpsMode);
        fpsModeMenuItem.addSelectionListener((SelectionListener)this);
        fpsModeMenuItem.setAccelerator(SWT.MOD1 + 'F');
        menuItemTool.setMenu(this.menuTool);
        this.menuMidlet = new Menu(menuItemMidlet);
        (this.loadJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_LOAD_JAR", "Load jar ..."));
        this.loadJarMenuItem.addSelectionListener((SelectionListener)this);
        (this.loadWithConsoleMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_LOAD_WITH_CONSOLE", "Load with console"));
        this.loadWithConsoleMenuItem.addSelectionListener((SelectionListener)this);
        (this.loadAutoPlayMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_LOAD_AUTO_PLAY", "Load auto-play record"));
        this.loadAutoPlayMenuItem.addSelectionListener((SelectionListener)this);
        final MenuItem menuItem5;
        (menuItem5 = new MenuItem(this.menuMidlet, 64)).setText(emulator.UILocale.uiText("MENU_MIDLET_RECENTLY", "Recently jarfiles"));
        this.aMenu1018 = new Menu((Decorations)this.shell, 4194308);
        for (int n = 1; n < 5 && !Settings.aArray[n].equals(""); ++n) {
            final String s;
            String s2;
            String s3;
            int n2;
            if ((s = Settings.aArray[n]).lastIndexOf(92) > 0) {
                s2 = s;
                s3 = s;
                n2 = 92;
            }
            else {
                s2 = s;
                s3 = s;
                n2 = 47;
            }
            final String trim = s2.substring(s3.lastIndexOf(n2) + 1).trim();
            StringBuffer sb;
            String s4;
            if (s.length() > 10) {
                sb = new StringBuffer().append("[").append(s.substring(0, 10));
                s4 = "...]";
            }
            else {
                sb = new StringBuffer().append("[").append(s.substring(0, s.length()));
                s4 = "]";
            }
            final String string = sb.append(s4).toString();
            final int n3 = n;
            final MenuItem menuItem6;
            (menuItem6 = new MenuItem(this.aMenu1018, 8)).setText("&" + n + " " + trim + " " + string);
            menuItem6.setAccelerator(SWT.MOD1 + 49 + n - 1);
            menuItem6.addSelectionListener((SelectionListener)new Class45(this, n3));
        }
        menuItem5.setMenu(this.aMenu1018);
        new MenuItem(this.menuMidlet, 2);
        (this.openJadMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_JAD", "Open JAD with Notepad") + "\tD");
        this.openJadMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuMidlet, 2);
        final MenuItem menuItem7;
        (menuItem7 = new MenuItem(this.menuMidlet, 64)).setText(emulator.UILocale.uiText("MENU_MIDLET_2DENGINE", "2D Graphics Engine"));
        this.menu2dEngine = new Menu((Decorations)this.shell, 4194308);
        (this.awt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("AWT-Graphics");
        this.awt2dMenuItem.setSelection(Settings.g2d == 1);
        this.awt2dMenuItem.addSelectionListener((SelectionListener)this);
        (this.swt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("SWT-GDI+");
        this.swt2dMenuItem.setSelection(Settings.g2d == 0);
        this.swt2dMenuItem.addSelectionListener((SelectionListener)this);
        menuItem7.setMenu(this.menu2dEngine);
        final MenuItem menuItem8;
        (menuItem8 = new MenuItem(this.menuMidlet, 64)).setText(emulator.UILocale.uiText("MENU_MDILET_3DENGINE", "3D Graphics Engine"));
        menuItem8.setMenu(this.menu3dEngine = new Menu((Decorations)this.shell, 4194308));
        (this.lwj3dMenuItem = new MenuItem(this.menu3dEngine, 16)).setText("LWJ-OpenGL");
        this.lwj3dMenuItem.setSelection(Settings.g3d == 1);
        this.lwj3dMenuItem.addSelectionListener((SelectionListener)this);
        this.lwj3dMenuItem.setEnabled(false);
        (this.swt3dMenuItem = new MenuItem(this.menu3dEngine, 16)).setText("SWT-OpenGL");
        this.swt3dMenuItem.setSelection(Settings.g3d == 0);
        this.swt3dMenuItem.addSelectionListener((SelectionListener)this);
        this.swt3dMenuItem.setEnabled(false);
        menuItem8.setMenu(this.menu3dEngine);
        new MenuItem(this.menuMidlet, 2);
        (this.suspendMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_SUSPEND", "Suspend") + "\tS");
        this.suspendMenuItem.addSelectionListener((SelectionListener)this);
        (this.resumeMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_RESUME", "Resume") + "\tE");
        this.resumeMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuMidlet, 2);
        (this.pausestepMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_PAUSE_STEP", "Pause/Step") + "\tT");
        this.pausestepMenuItem.addSelectionListener((SelectionListener)this);
        (this.playResumeMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_PLAY_RESUME", "Play/Resume") + "\tR");
        this.playResumeMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuMidlet, 2);
        (this.restartMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_RESTART", "Restart") + "\tAlt+R");
        this.restartMenuItem.setAccelerator(65618);
        this.restartMenuItem.addSelectionListener((SelectionListener)this);
        (this.exitMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.uiText("MENU_MIDLET_EXIT", "Exit") + "\tESC");
        this.exitMenuItem.setAccelerator(27);
        this.exitMenuItem.addSelectionListener((SelectionListener)this);
        menuItemMidlet.setMenu(this.menuMidlet);
        this.method570(!Settings.canvasKeyboard);
        setFpsMode(Settings.fpsMode);
        ((Decorations)this.shell).setMenuBar(this.aMenu971);
    }
    
    
    
    protected final void method570(final boolean b) {
        MenuItem menuItem;
        int accelerator;
        if (b) {
            this.infosMenuItem.setAccelerator(73);
            this.xrayViewMenuItem.setAccelerator(88);
            this.alwaysOnTopMenuItem.setAccelerator(79);
            this.rotateScreenMenuItem.setAccelerator(89);
            this.forecPaintMenuItem.setAccelerator(70);
            this.speedUpMenuItem.setAccelerator(46);
            this.slowDownMenuItem.setAccelerator(44);
            this.startpauseTickMenuItem.setAccelerator(75);
            this.resetTickMenuItem.setAccelerator(76);
            this.captureToFileMenuItem.setAccelerator(67);
            this.startRecordAviMenuItem.setAccelerator(86);
            this.stopRecordAviMenuItem.setAccelerator(66);
            this.suspendMenuItem.setAccelerator(83);
            this.resumeMenuItem.setAccelerator(69);
            this.openJadMenuItem.setAccelerator(68);
            this.pausestepMenuItem.setAccelerator(84);
            menuItem = this.playResumeMenuItem;
            accelerator = 82;
        }
        else {
            this.infosMenuItem.setAccelerator(0);
            this.xrayViewMenuItem.setAccelerator(0);
            this.alwaysOnTopMenuItem.setAccelerator(0);
            this.rotateScreenMenuItem.setAccelerator(0);
            this.forecPaintMenuItem.setAccelerator(0);
            this.speedUpMenuItem.setAccelerator(0);
            this.slowDownMenuItem.setAccelerator(0);
            this.startpauseTickMenuItem.setAccelerator(0);
            this.resetTickMenuItem.setAccelerator(0);
            this.captureToFileMenuItem.setAccelerator(0);
            this.startRecordAviMenuItem.setAccelerator(0);
            this.stopRecordAviMenuItem.setAccelerator(0);
            this.suspendMenuItem.setAccelerator(0);
            this.resumeMenuItem.setAccelerator(0);
            this.openJadMenuItem.setAccelerator(0);
            this.pausestepMenuItem.setAccelerator(0);
            menuItem = this.playResumeMenuItem;
            accelerator = 0;
        }
        menuItem.setAccelerator(accelerator);
    }
    

    protected final void setFpsMode(final boolean b) {
        if (b) {
        	Point pt = canvas.toDisplay(canvas.getSize().x / 2, canvas.getSize().y / 2);
        	display.setCursorLocation(pt);
        	//TODO
        } else {
        	
        }
    }
    
    protected static void method554() {
        Settings.e = 1;
        if (!EmulatorScreen.aBoolean992 && EmulatorScreen.aLong991 == 0L) {
            EmulatorScreen.aLong991 = System.currentTimeMillis();
        }
    }
    
    protected final void method571() {
        Settings.e = -1;
        if (this.anImage974 != null && !this.anImage974.isDisposed()) {
            this.anImage974.dispose();
        }
        if (!EmulatorScreen.aBoolean992 && this.pauseState == 1 && EmulatorScreen.aLong991 != 0L) {
            EmulatorScreen.aLong1000 += System.currentTimeMillis() - EmulatorScreen.aLong991;
            EmulatorScreen.aLong991 = 0L;
        }
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final MenuItem menuItem;
        final Menu parent;
        if ((parent = (menuItem = (MenuItem)((TypedEvent)selectionEvent).widget).getParent()).equals(this.menuTool)) {
            if (menuItem.equals(this.captureToFileMenuItem)) {
                if (this.pauseState != 0) {
                    final String string = Emulator.getAbsolutePath() + "/capture/";
                    final File file;
                    if (!(file = new File(string)).exists() || !file.isDirectory()) {
                        file.mkdir();
                    }
                    if (Settings.g2d == 0) {
                        this.ad979.saveToFile(string + EmulatorScreen.aString993 + EmulatorScreen.anInt1012 + ".png");
                    }
                    else if (Settings.g2d == 1) {
                        this.ad980.saveToFile(string + EmulatorScreen.aString993 + EmulatorScreen.anInt1012 + ".png");
                    }
                    ++EmulatorScreen.anInt1012;
                }
            }
            else if (menuItem.equals(this.captureToClipboardMenuItem)) {
                if (this.pauseState != 0) {
                    if (Settings.g2d == 0) {
                        this.ad979.copyToClipBoard();
                        return;
                    }
                    if (Settings.g2d == 1) {
                        this.ad980.copyToClipBoard();
                    }
                }
            }
            else {
                if (menuItem.equals(this.startRecordAviMenuItem)) {
                    method554();
                    final String string2 = Emulator.getAbsolutePath() + "/capture/";
                    final File file2;
                    if (!(file2 = new File(string2)).exists() || !file2.isDirectory()) {
                        file2.mkdir();
                    }
                    final FileDialog fileDialog;
                    ((Dialog)(fileDialog = new FileDialog(this.shell, 8192))).setText(emulator.UILocale.uiText("SAVE_TO_FILE", "Save to file"));
                    fileDialog.setFilterPath(string2);
                    fileDialog.setFilterExtensions(new String[] { "*.avi", "*.*" });
                    fileDialog.setFileName(EmulatorScreen.aString993 + EmulatorScreen.anInt1012 + ".avi");
                    final String open;
                    if ((open = fileDialog.open()) != null) {
                        EmulatorScreen.aviWriter = new AVIWriter();
                        if (!EmulatorScreen.aviWriter.method841(open, Profiler.FPS, this.getWidth(), this.getHeight())) {
                            EmulatorScreen.aviWriter.method842();
                            EmulatorScreen.aviWriter = null;
                        }
                        ++EmulatorScreen.anInt1012;
                        this.method587();
                    }
                    this.method571();
                    return;
                }
                if (menuItem.equals(this.stopRecordAviMenuItem)) {
                    if (EmulatorScreen.aviWriter != null) {
                        EmulatorScreen.aviWriter.method842();
                        EmulatorScreen.aviWriter = null;
                    }
                    this.method587();
                    return;
                }
                if (menuItem.equals(this.connectNetworkMenuItem)) {
                    Emulator.getNetMonitor().a();
                    Emulator.getEmulator().getLogStream().println("!!!Emulator connect to network " + (true ? "Successful!" : "failed!"));
                    this.method587();
                    return;
                }
                if (menuItem.equals(this.disconnectNetworkMenuItem)) {
                    Emulator.getNetMonitor().a();
                    Emulator.getEmulator().getLogStream().println("!!!Emulator disconnect to network!");
                    this.method587();
                    return;
                }
                if (menuItem.equals(this.channelUpMenuItem)) {
                    Emulator.getNetMonitor().a(true);
                    this.method587();
                    return;
                }
                if(menuItem.equals(canvasKeyboardMenuItem)) {
                	Settings.canvasKeyboard = canvasKeyboardMenuItem.getSelection();
                	method570(!Settings.canvasKeyboard);
                	return;
                }
                
                if(menuItem.equals(fpsModeMenuItem)) {
                	Settings.fpsMode = fpsModeMenuItem.getSelection();
                	setFpsMode(true);
                	return;
                }
                
                if (menuItem.equals(this.channelDownMenuItem)) {
                    Emulator.getNetMonitor().a(false);
                    this.method587();
                    return;
                }
                if (menuItem.equals(this.showTrackInfoMenuItem)) {
                    Settings.threadMethodTrack = !Settings.threadMethodTrack;
                    return;
                }
                if (menuItem.equals(this.zoomInMenuItem)) {
                    this.zoomIn();
                    return;
                }
                if (menuItem.equals(this.zoomOutMenuItem)) {
                    this.zoomOut();
                    return;
                }
                if (menuItem.equals(this.startpauseTickMenuItem)) {
                    EmulatorScreen.aBoolean967 = !EmulatorScreen.aBoolean967;
                    if (EmulatorScreen.aBoolean967) {
                        EmulatorScreen.aLong1007 = System.currentTimeMillis();
                    }
                    if (EmulatorScreen.aBoolean992) {
                        EmulatorScreen.aLong1000 = System.currentTimeMillis();
                        EmulatorScreen.aBoolean992 = false;
                    }
                    this.method584();
                    return;
                }
                if (menuItem.equals(this.resetTickMenuItem)) {
                    EmulatorScreen.aLong1007 = System.currentTimeMillis();
                    EmulatorScreen.aLong991 = 0L;
                    EmulatorScreen.aBoolean967 = true;
                    EmulatorScreen.aBoolean992 = true;
                    this.method584();
                    return;
                }
                if (menuItem.equals(this.speedUpMenuItem)) {
                    if (Settings.f == -1) {
                        Settings.f = 1;
                        this.method584();
                        return;
                    }
                    if (Settings.f < 100) {
                        ++Settings.f;
                        this.method584();
                    }
                }
                else if (menuItem.equals(this.slowDownMenuItem)) {
                    if (Settings.f == 1) {
                        Settings.f = -1;
                        this.method584();
                        return;
                    }
                    if (Settings.f > -100) {
                        --Settings.f;
                        this.method584();
                    }
                }
                else {
                    if (menuItem.equals(this.recordKeysMenuItem)) {
                        Settings.recordKeys = !Settings.recordKeys;
                        return;
                    }
                    if (menuItem.equals(this.enableAutoplayMenuItem)) {
                        Settings.q = !Settings.q;
                    }
                }
            }
        }
        else if (parent.equals(this.menuMidlet)) {
            boolean equals = false;
            if (menuItem.equals(this.exitMenuItem)) {
                this.shell.dispose();
                return;
            }
            if (menuItem.equals(this.restartMenuItem)) {
                Emulator.loadGame(null, Settings.g2d, Settings.g3d, false);
            }
            else if (menuItem.equals(this.loadJarMenuItem) || (equals = menuItem.equals(this.loadWithConsoleMenuItem))) {
                method554();
                final FileDialog fileDialog2;
                ((Dialog)(fileDialog2 = new FileDialog(this.shell, 4096))).setText(emulator.UILocale.uiText("OPEN_JAR_FILE", "Open a jar file"));
                fileDialog2.setFilterExtensions(new String[] { "*.jar;*.jad", "*.*" });
                final String open2;
                if ((open2 = fileDialog2.open()) != null) {
                    Settings.bString = null;
                    Emulator.loadGame(open2, Settings.g2d, Settings.g3d, equals);
                }
                this.method571();
            }
            else if (menuItem.equals(this.loadAutoPlayMenuItem)) {
                method554();
                final FileDialog fileDialog3;
                ((Dialog)(fileDialog3 = new FileDialog(this.shell, 4096))).setText(emulator.UILocale.uiText("OPEN_REC_FILE", "Open a record file"));
                fileDialog3.setFilterPath(Emulator.getAbsolutePath());
                fileDialog3.setFilterExtensions(new String[] { "*.rec", "*.*" });
                Label_1321: {
                    final String open3;
                    if ((open3 = fileDialog3.open()) != null) {
                        Emulator.getRobot();
                        String s;
                        if ((s = KeyRecords.method701(open3)) == null || !new File(s).exists()) {
                            ((Dialog)fileDialog3).setText(emulator.UILocale.uiText("LINK_JAR_FILE", "Specify a jar file"));
                            fileDialog3.setFileName("");
                            fileDialog3.setFilterExtensions(new String[] { "*.jar", "*.*" });
                            if ((s = fileDialog3.open()) == null) {
                                break Label_1321;
                            }
                        }
                        Settings.bString = open3;
                        Emulator.loadGame(s, Settings.g2d, Settings.g3d, false);
                    }
                }
                this.method571();
            }
            else if (menuItem.equals(this.suspendMenuItem)) {
                if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                    return;
                }
                this.pauseState = 2;
                Emulator.getEventQueue().queue(16);
                this.method583();
                ((Control)this.canvas).redraw();
                if (!EmulatorScreen.aBoolean992 && EmulatorScreen.aLong991 == 0L) {
                    EmulatorScreen.aLong991 = System.currentTimeMillis();
                }
            }
            else if (menuItem.equals(this.resumeMenuItem)) {
                if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                    return;
                }
                this.pauseState = 1;
                Emulator.getEventQueue().queue(15);
                this.anImage974.dispose();
                if (Settings.e == 0) {
                    this.method583();
                    ((Control)this.canvas).redraw();
                }
                else {
                    Emulator.getCanvas().repaint();
                }
                if (!EmulatorScreen.aBoolean992 && Settings.e == -1 && EmulatorScreen.aLong991 != 0L) {
                    EmulatorScreen.aLong1000 += System.currentTimeMillis() - EmulatorScreen.aLong991;
                    EmulatorScreen.aLong991 = 0L;
                }
            }
            else if (menuItem.equals(this.pausestepMenuItem)) {
                method554();
            }
            else if (menuItem.equals(this.playResumeMenuItem)) {
                this.method571();
            }
            else if (menuItem.equals(this.openJadMenuItem)) {
                try {
                    final String jadPath;
                    if ((jadPath = Emulator.getJadPath()) != null) {
                        Runtime.getRuntime().exec("notepad.exe " + jadPath);
                    }
                }
                catch (Exception ex) {}
            }
            this.method587();
        }
        else if (parent.equals(this.menu2dEngine)) {
            if (menuItem.equals(this.awt2dMenuItem)) {
                if (this.pauseState != 0 && Settings.g2d != 1) {
                    Emulator.loadGame(null, 1, Settings.g3d, false);
                    return;
                }
                Settings.g2d = 1;
                this.swt2dMenuItem.setSelection(false);
                this.awt2dMenuItem.setSelection(true);
            }
            else if (menuItem.equals(this.swt2dMenuItem)) {
                if (this.pauseState != 0 && Settings.g2d != 0) {
                    Emulator.loadGame(null, 0, Settings.g3d, false);
                    return;
                }
                Settings.g2d = 0;
                this.awt2dMenuItem.setSelection(false);
                this.swt2dMenuItem.setSelection(true);
            }
        }
        else if (parent.equals(this.menu3dEngine)) {
            if (menuItem.equals(this.swt3dMenuItem)) {
                if (this.pauseState != 0 && Settings.g3d != 0) {
                    Emulator.loadGame(null, Settings.g2d, 0, false);
                    return;
                }
                Settings.g3d = 0;
                this.lwj3dMenuItem.setSelection(false);
                this.swt3dMenuItem.setSelection(true);
            }
            else if (menuItem.equals(this.lwj3dMenuItem)) {
                if (this.pauseState != 0 && Settings.g3d != 1) {
                    Emulator.loadGame(null, Settings.g2d, 1, false);
                    return;
                }
                Settings.g3d = 1;
                this.swt3dMenuItem.setSelection(false);
                this.lwj3dMenuItem.setSelection(true);
            }
        }
        else if (parent.equals(this.menuView)) {
            if (menuItem.equals(this.aMenuItem949)) {
                new Class54().method454(this.shell);
                return;
            }
            if (menuItem.equals(this.aMenuItem950)) {
                ((Property)Emulator.getEmulator().getProperty()).method354(this.shell);
                return;
            }
            if (menuItem.equals(this.alwaysOnTopMenuItem)) {
                Settings.alwaysOnTop = this.alwaysOnTopMenuItem.getSelection();
                method557(((Control)this.shell).handle, Settings.alwaysOnTop);
                return;
            }
            if (menuItem.equals(this.aMenuItem957)) {
                if (((Class11)Emulator.getEmulator().getLogStream()).method327()) {
                    ((Class11)Emulator.getEmulator().getLogStream()).method330();
                    return;
                }
                ((Class11)Emulator.getEmulator().getLogStream()).method329(this.shell);
            }
            else if (menuItem.equals(this.aMenuItem956)) {
                if (((EmulatorImpl)Emulator.getEmulator()).method826().method834()) {
                    ((EmulatorImpl)Emulator.getEmulator()).method826().method836();
                    return;
                }
                ((EmulatorImpl)Emulator.getEmulator()).method826().method835(this.shell);
            }
            else if (menuItem.equals(this.infosMenuItem)) {
                this.infosEnabled = this.infosMenuItem.getSelection();
                if (this.infosEnabled) {
                    ((Control)this.canvas).setCursor(new Cursor((Device)EmulatorScreen.display, 2));
                    ((EmulatorImpl)Emulator.getEmulator()).method825().method607(this.shell);
                    return;
                }
                ((Control)this.canvas).setCursor(new Cursor((Device)EmulatorScreen.display, 0));
                ((Control)this.canvas).redraw();
                ((EmulatorImpl)Emulator.getEmulator()).method825().method608();
            }
            else {
                if (menuItem.equals(this.rotateScreenMenuItem)) {
                    this.method569(this.getHeight(), this.getWidth());
                    return;
                }
                if (menuItem.equals(this.forecPaintMenuItem)) {
                    if (Settings.g2d == 0) {
                        if (Settings.xrayView) {
                            this.xrayScreenImageSwt.cloneImage(this.ad979);
                        }
                        else {
                            this.backBufferImageSwt.cloneImage(this.ad979);
                        }
                    }
                    else if (Settings.g2d == 1) {
                        (Settings.xrayView ? this.xrayScreenImageAwt : this.backBufferImageAwt).cloneImage(this.ad980);
                    }
                    ((Control)this.canvas).redraw();
                    return;
                }
                if (menuItem.equals(this.aMenuItem964)) {
                    final File file3;
                    if ((file3 = new File(Emulator.getAbsolutePath() + "/sensorsimulator.jar")).exists()) {
                        try {
                            final String[] array;
                            (array = new String[2])[0] = "cmd.exe";
                            array[1] = "/c \" java -jar " + file3.getAbsolutePath() + " \"";
                            Runtime.getRuntime().exec(array);
                        }
                        catch (Exception ex2) {}
                    }
                    return;
                }
                if (menuItem.equals(this.aMenuItem963)) {
                    if (((Class83)Emulator.getEmulator().getMessage()).method479()) {
                        ((Class83)Emulator.getEmulator().getMessage()).method482();
                        return;
                    }
                    ((Class83)Emulator.getEmulator().getMessage()).method481(this.shell);
                }
                else if (menuItem.equals(this.aMenuItem962)) {
                    if (((EmulatorImpl)Emulator.getEmulator()).method827().method494()) {
                        ((EmulatorImpl)Emulator.getEmulator()).method827().method507();
                        return;
                    }
                    ((EmulatorImpl)Emulator.getEmulator()).method827().method493();
                }
                else {
                    if (menuItem.equals(this.xrayViewMenuItem)) {
                        Settings.xrayView = this.xrayViewMenuItem.getSelection();
                        return;
                    }
                    if (menuItem.equals(this.aMenuItem958)) {
                        if (((EmulatorImpl)Emulator.getEmulator()).method822().method313()) {
                            ((EmulatorImpl)Emulator.getEmulator()).method822().method321();
                            return;
                        }
                        ((EmulatorImpl)Emulator.getEmulator()).method822().method311(this.shell);
                    }
                    else if (menuItem.equals(this.aMenuItem959)) {
                        if (((EmulatorImpl)Emulator.getEmulator()).method829().method313()) {
                            ((EmulatorImpl)Emulator.getEmulator()).method829().method321();
                            return;
                        }
                        ((EmulatorImpl)Emulator.getEmulator()).method829().method311(this.shell);
                    }
                    else if (menuItem.equals(this.aMenuItem960)) {
                        if (((EmulatorImpl)Emulator.getEmulator()).method824().method438()) {
                            ((EmulatorImpl)Emulator.getEmulator()).method824().method446();
                            return;
                        }
                        ((EmulatorImpl)Emulator.getEmulator()).method824().method436();
                    }
                    else if (menuItem.equals(this.aMenuItem961)) {
                        if (((EmulatorImpl)Emulator.getEmulator()).method823().method622()) {
                            ((EmulatorImpl)Emulator.getEmulator()).method823().method656();
                            return;
                        }
                        ((EmulatorImpl)Emulator.getEmulator()).method823().method621();
                    }
                }
            }
        }
    }
    
    private static void method557(final int n, final boolean b) {
        OS.SetWindowPos(n, b ? -1 : -2, 0, 0, 0, 0, 19);
    }
    
    private void method587() {
        this.suspendMenuItem.setEnabled(this.pauseState == 1);
        this.resumeMenuItem.setEnabled(this.pauseState == 2);
        this.restartMenuItem.setEnabled(this.pauseState != 0);
        this.xrayViewMenuItem.setSelection(Settings.xrayView);
        this.forecPaintMenuItem.setEnabled(this.pauseState != 0);
        this.pausestepMenuItem.setEnabled(this.pauseState != 0);
        this.playResumeMenuItem.setEnabled(Settings.e >= 0 && this.pauseState != 0);
        this.openJadMenuItem.setEnabled(this.pauseState != 0);
        this.aMenuItem958.setEnabled(this.pauseState != 0);
        this.aMenuItem959.setEnabled(this.pauseState != 0);
        this.aMenuItem961.setEnabled(this.pauseState != 0);
        this.aMenuItem960.setEnabled(this.pauseState != 0);
        this.aMenuItem962.setEnabled(this.pauseState != 0);
        this.startRecordAviMenuItem.setEnabled(this.pauseState != 0 && EmulatorScreen.aviWriter == null);
        this.stopRecordAviMenuItem.setEnabled(this.pauseState != 0 && EmulatorScreen.aviWriter != null);
        this.connectNetworkMenuItem.setEnabled(this.pauseState != 0 && !Emulator.getNetMonitor().b());
        this.disconnectNetworkMenuItem.setEnabled(this.pauseState != 0 && Emulator.getNetMonitor().b());
        this.channelUpMenuItem.setEnabled(this.pauseState != 0 && Emulator.getNetMonitor().b());
        this.channelDownMenuItem.setEnabled(this.pauseState != 0 && Emulator.getNetMonitor().b());
        this.method584();
    }
    
    public final void widgetDefaultSelected(final SelectionEvent selectionEvent) {
    }
    
    private void updateInfos(final int n, final int n2) {
        final int n3 = (int)(n / this.zoom);
        final int n4 = (int)(n2 / this.zoom);
        if (n3 < 0 || n4 < 0 || n3 > this.getWidth() - 1 || n4 > this.getHeight() - 1) {
            return;
        }
        final int rgb;
        final int n5 = (rgb = this.getScreenImage().getRGB(n3, n4)) >> 16 & 0xFF;
        final int n6 = rgb >> 8 & 0xFF;
        final int n7 = rgb & 0xFF;
        this.aString1008 = emulator.UILocale.uiText("INFO_FRAME_POS", "Pos") + "(" + n3 + "," + n4 + ")\n" + emulator.UILocale.uiText("INFO_FRAME_COLOR", "Color") + "(";
        EmulatorScreen class93;
        StringBuffer sb2;
        if (Settings.infoColorHex) {
            final StringBuffer sb = new StringBuffer();
            class93 = this;
            sb2 = sb.append(this.aString1008).append(n5).append(",").append(n6).append(",").append(n7);
        }
        else {
            final StringBuffer sb3 = new StringBuffer();
            class93 = this;
            sb2 = sb3.append(this.aString1008).append("0x").append(Integer.toHexString(rgb).toUpperCase());
        }
        class93.aString1008 = sb2.toString();
        this.aString1008 = this.aString1008 + ")\n" + 
        emulator.UILocale.uiText("INFO_FRAME_RECT", "R") + 
        "(" + (int)(this.mouseXPress / this.zoom) + "," + 
        (int)(this.mouseYPress / this.zoom) + "," + (int)((this.mouseXRelease - this.mouseXPress) / this.zoom) +
        "," + (int)((this.mouseYRelease - this.mouseYPress) / this.zoom) + ")";
        ((EmulatorImpl)Emulator.getEmulator()).method825().method609(this.aString1008);
    }
    
    private void method588() {
        final GridData layoutData;
        (layoutData = new GridData()).horizontalSpan = 3;
        layoutData.verticalAlignment = 4;
        layoutData.verticalSpan = 1;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = 4;
        ((Control)(this.canvas = new Canvas((Composite)this.shell, 537135104))).setLayoutData((Object)layoutData);
        ((Control)this.canvas).addKeyListener((KeyListener)this);
        ((Control)this.canvas).addMouseListener((MouseListener)this);
        ((Control)this.canvas).addMouseWheelListener((MouseWheelListener)this);
        ((Control)this.canvas).addMouseMoveListener((MouseMoveListener)this);
        this.canvas.getShell().addMouseTrackListener(this);
        ((Control)this.canvas).addPaintListener((PaintListener)this);
        ((Widget)this.canvas).addListener(37, (Listener)new Class32(this));
        this.aBooleanArray978 = new boolean[256];
        this.method589();
        this.aClass67_973 = new Class67(this.canvas);
    }
    
    public final void paintControl(final PaintEvent paintEvent) {
        final GC gc;
        (gc = paintEvent.gc).setInterpolation(this.anInt1020);
        try {
            if (this.anImage974 == null || this.anImage974.isDisposed()) {
                if (this.pauseState == 0) {
                    gc.setBackground(EmulatorScreen.display.getSystemColor(22));
                    gc.fillRectangle(0, 0, this.anInt996, this.anInt1003);
                    gc.setForeground(EmulatorScreen.display.getSystemColor(21));
                    gc.drawText(Emulator.getInfoString(), this.anInt996 >> 3, this.anInt1003 >> 3, true);
                }
                else if (Settings.g2d == 0) {
                    this.ad979.method13(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.anInt996, this.anInt1003);
                }
                else if (Settings.g2d == 1) {
                    this.ad980.method13(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.anInt996, this.anInt1003);
                }
            }
            else {
                gc.drawImage(this.anImage974, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.anInt996, this.anInt1003);
            }
        }
        catch (Exception ex) {}
        gc.setAdvanced(false);
        this.method565(gc);
    }
    
    private void method565(final GC gc) {
        if (this.infosEnabled && (this.mouseXPress != this.mouseXRelease || this.mouseYPress != this.mouseYRelease)) {
            OS.SetROP2(gc.handle, 7);
            gc.setForeground(EmulatorScreen.display.getSystemColor(1));
            gc.drawRectangle(this.mouseXPress, this.mouseYPress, this.mouseXRelease - this.mouseXPress, this.mouseYRelease - this.mouseYPress);
            OS.SetROP2(gc.handle, 13);
        }
    }
    
    public final void run() {
        if (this.pauseState != 1) {
            return;
        }
        if (Settings.g2d == 0) {
            this.screenImageSwt.cloneImage(this.ad979);
        }
        else if (Settings.g2d == 1) {
            this.screenImageAwt.cloneImage(this.ad980);
        }
        if (EmulatorScreen.aviWriter != null) {
            EmulatorScreen.aviWriter.method843(this.ad980.getData());
        }
        ((Control)this.canvas).redraw();
        if (!EmulatorScreen.aBoolean967 && !EmulatorScreen.aBoolean992) {
            this.method584();
        }
        ++EmulatorScreen.aLong982;
        Emulator.getEmulator().syncValues();
        Profiler.reset();
    }
    
    private static void method578(final int n) {
        if (Emulator.getCurrentDisplay().getCurrent() == null) {
            return;
        }
        if (!Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(n, true)) {
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokeKeyPressed(n);
                return;
            }
            Emulator.getScreen().invokeKeyPressed(n);
        }
    }
    
    private static void method580(final int n) {
        if (Emulator.getCurrentDisplay().getCurrent() == null) {
            return;
        }
        if (!Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(n, false)) {
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
                Emulator.getCanvas().invokeKeyReleased(n);
                return;
            }
            Emulator.getScreen().invokeKeyReleased(n);
        }
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
    	//System.out.println(keyEvent.character + " " + keyEvent.keyCode);
        if (keyEvent.keyCode == 16777261) {
            this.zoomOut();
            return;
        }
        if (keyEvent.keyCode == 16777259) {
            this.zoomIn();
            return;
        }
        this.aClass67_973.method470(keyEvent);
        int n;
        if ((n = (keyEvent.keyCode & 0xFEFFFFFF)) == 13 && (keyEvent.keyCode & 0x1000000) != 0x0) {
            n = 22;
        }
        if(keyEvent.character >= 33 && keyEvent.character <= 90 && Settings.canvasKeyboard) n = keyEvent.character;
        this.handleKeyPress(n);
    }
    
    public final void keyReleased(final KeyEvent keyEvent) {
        int n;
        if ((n = (keyEvent.keyCode & 0xFEFFFFFF)) == 13 && (keyEvent.keyCode & 0x1000000) != 0x0) {
            n = 22;
        }
        if(keyEvent.character >= 33 && keyEvent.character <= 90) n = keyEvent.character;
        this.handleKeyRelease(n);
    }
    
    protected final void handleKeyPress(int n) {
        if (this.pauseState == 0 || Settings.q) {
            return;
        }
        if(Settings.fpsMode && Settings.fpsGame == 2) {
        	boolean b = true;
        	//up
        	if(n == 'w') n = -1;
        	//down
        	else if(n == 's') n = -2;
        	
        	//num1
        	else if(n == 'a') n = 49;
        	//num3
        	else if(n == 'd') n = 51;
        	//num7
        	else if(n == 'q') n = 55;
        	//num9
        	else if(n == 'e') n = 57;
        	//num0
        	else if(n == 'c') n = 48;
        	//star
        	else if(n == 'x') n = 42;
        	//hash
        	else if(n == 'z') n = 35;
        	else b = false;
	        if(b) {
	        	mp(n);
	        	return;
        	}
        }
        if ((n < 0 || n >= this.aBooleanArray978.length) && !Settings.canvasKeyboard) {
            return;
        }
        final String method605;
        if ((method605 = Keyboard.method605(n)) == null) {
            return;
        }
	    if(!Settings.canvasKeyboard) {
	        if (this.aBooleanArray978[n]) {
	            if (Settings.enableKeyRepeat) {
	                Emulator.getEventQueue().keyRepeat(Integer.parseInt(method605));
	            }
	            return;
	        }
	        this.aBooleanArray978[n] = true;
	        if (Settings.enableKeyCache) {
	            Keyboard.aStack1063.push('0' + method605);
	            return;
	        }
        }
        //System.out.println("method568 " + n);
        if (Settings.recordKeys && !Settings.q) {
            Emulator.getRobot().method697(EmulatorScreen.aLong982 + ":" + '0' + method605);
        }
        Emulator.getEventQueue().keyPress(Integer.parseInt(method605));
    }
    
    protected final void handleKeyRelease(int n) {
        if (this.pauseState == 0 || Settings.q) {
            return;
        }

        if(Settings.fpsMode && Settings.fpsGame == 2) {
        	boolean b = true;
        	//up
        	if(n == 'w') n = -1;
        	//down
        	else if(n == 's') n = -2;
        	
        	//num1
        	else if(n == 'a') n = 49;
        	//num3
        	else if(n == 'd') n = 51;
        	//num7
        	else if(n == 'q') n = 55;
        	//num9
        	else if(n == 'e') n = 57;
        	//num0
        	else if(n == 'c') n = 48;
        	//star
        	else if(n == 'x') n = 42;
        	//hash
        	else if(n == 'z') n = 35;
        	else b = false;
	        if(b) {
	        	mr(n);
	        	return;
        	}
        }

        if ((n < 0 || n >= this.aBooleanArray978.length) && !Settings.canvasKeyboard) {
            return;
        }
        final String method605;
        if ((method605 = Keyboard.method605(n)) == null) {
            return;
        }
	    if(!Settings.canvasKeyboard) {
	        if (!this.aBooleanArray978[n]) {
	            return;
	        }
	        this.aBooleanArray978[n] = false;
	    }
        if (Settings.enableKeyCache) {
            Keyboard.aStack1063.push('1' + method605);
            return;
        }
        if (Settings.recordKeys && !Settings.q) {
            Emulator.getRobot().method697(EmulatorScreen.aLong982 + ":" + '1' + method605);
        }
        Emulator.getEventQueue().keyRelease(Integer.parseInt(method605));
    }
    
    public final void mouseDoubleClick(final MouseEvent mouseEvent) {
        if (this.pauseState == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
            return;
        }
        if (mouseEvent.button != 1 || Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
            return;
        }
        Emulator.getScreen().invokeKeyPressed(Keyboard.getArrowKeyFromDevice(8));
    }
    
    public final void mouseDown(final MouseEvent mouseEvent) {
        if (this.infosEnabled && !this.mouseDownInfos) {
            this.mouseXPress = mouseEvent.x;
            this.mouseYPress = mouseEvent.y;
            this.mouseXRelease = mouseEvent.x;
            this.mouseYRelease = mouseEvent.y;
            this.mouseDownInfos = true;
        }
        if (this.pauseState == 0) {
            return;
        }
        if (Emulator.getCurrentDisplay().getCurrent() != null) {
        	//System.out.println(mouseEvent.button);
            if(Settings.fpsMode && mouseEvent.button == 3) {
            	if(Settings.fpsGame == 2)
            		handleKeyPress(57);
            	else if(Settings.fpsGame == 1)
            		mp(42);
            	else if(Settings.fpsGame == 0)
             		mp(Keyboard.soft2());
            	return;
            }
            if(Settings.fpsMode && mouseEvent.button == 2) {
            	if(Settings.fpsGame == 1)
             		mp(Keyboard.soft2());
            	else if(Settings.fpsGame == 0)
             		mp(Keyboard.soft1());
            	return;
            }
            if(Settings.fpsMode && mouseEvent.button == 1) {
            	if(Settings.fpsGame == 2)
            		handleKeyPress(13);
            	else
            		mp(Keyboard.getArrowKeyFromDevice(8));
            	return;
            }
            final int n = (int)(mouseEvent.x / this.zoom);
            final int n2 = (int)(mouseEvent.y / this.zoom);
            Emulator.getEventQueue().mouseDown(n, n2);
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getScreen()) {
                this.aClass67_973.method474(n, n2);
            }
        }
    }
    
    public final void mouseUp(final MouseEvent mouseEvent) {
        this.mouseDownInfos = false;
        if (this.pauseState == 0) {
            return;
        }
        if (Emulator.getCurrentDisplay().getCurrent() != null) {
            if(Settings.fpsMode && mouseEvent.button == 3) {
            	if(Settings.fpsGame == 2)
            		handleKeyRelease(57);
            	else if(Settings.fpsGame == 1)
            		mr(42);
            	else if(Settings.fpsGame == 0)
             		mr(Keyboard.soft2());
            	return;
            }

            if(Settings.fpsMode && mouseEvent.button == 2) {
            	if(Settings.fpsGame == 1)
             		mr(Keyboard.soft2());
            	else if(Settings.fpsGame == 0)
             		mr(Keyboard.soft1());
            	return;
            }
            if(Settings.fpsMode && mouseEvent.button == 1) {
            	if(Settings.fpsGame == 2)
            		handleKeyRelease(13);
            	else
            		mr(Keyboard.getArrowKeyFromDevice(8));
            	return;
            }
            Emulator.getEventQueue().mouseUp((int)(mouseEvent.x / this.zoom), (int)(mouseEvent.y / this.zoom));
        }
    }
    
    private void mp(int i) {
    	if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			Emulator.getCanvas().invokeKeyPressed(i);
		} else {
			Emulator.getScreen().invokeKeyPressed(i);
		}
    }
    private void mr(int i) {
    	if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			Emulator.getCanvas().invokeKeyReleased(i);
		} else {
			Emulator.getScreen().invokeKeyReleased(i);
		}
    }
    private void mrp(int i) {
    	if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
			Emulator.getCanvas().invokeKeyRepeated(i);
		} else {
		}
    }
    


	@Override
	public void mouseEnter(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExit(MouseEvent e) {
        if(Settings.fpsMode) {
        	System.out.println("mouse exited");
        	Point pt = canvas.toDisplay(canvas.getSize().x / 2, canvas.getSize().y / 2 - 1);
        	display.setCursorLocation(pt);
        }
	}

	@Override
	public void mouseHover(MouseEvent arg0) {
		
	}
	
    public final void mouseMove(final MouseEvent mouseEvent) {
        if (this.infosEnabled) {
            if (this.mouseDownInfos) {
                this.mouseXRelease = mouseEvent.x;
                this.mouseYRelease = mouseEvent.y;
                ((Control)this.canvas).redraw();
            }
            this.updateInfos(mouseEvent.x, mouseEvent.y);
            return;
        }
        if (this.pauseState == 0) {
            return;
        }
        if(Settings.fpsMode) {
        	if(!mset) {
        	    Color white = display.getSystemColor(SWT.COLOR_WHITE);
        	    Color black = display.getSystemColor(SWT.COLOR_BLACK);
        	    PaletteData palette = new PaletteData(new RGB[] { white.getRGB(), black.getRGB() });
        	    ImageData sourceData = new ImageData(16, 16, 1, palette);
        	    sourceData.transparentPixel = 0;
        	    Cursor cursor = new Cursor(display, sourceData, 0, 0);
                this.canvas.getShell().setCursor(cursor);
                mset = true;
        	}
        	Point pt = canvas.toDisplay(canvas.getSize().x / 2, canvas.getSize().y / 2 - 1);
        	int dx = mouseEvent.x - canvas.getSize().x / 2;
        	if(canvas.toControl(display.getCursorLocation()).x == canvas.getSize().x / 2) {
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}

        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        		return;
        	}
        	display.setCursorLocation(pt);
        	if(dx > 1) {
        		// right
        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        		if(!fpsWasRight) mp(-4);
        		else mrp(-4);
        		fpsWasRight = true;
        	} else if(dx < -1) {
        		// left
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}
        		if(!fpsWasLeft) mp(-3);
        		else mrp(-3);
        		fpsWasLeft = true;
        	} else if(Math.abs(dx) == 1 && !fpsWasnt) {
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}

        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        	}else if(dx == 0) {
        		fpsWasnt = true;
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}

        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        		return;
        	}
        	fpsWasnt = false;
        	lastMouseMoveX = mouseEvent.x;
        	//if(canvas.getSize().x / 2 != mouseEvent.x) ignoreNextFps = true;
        	return;
        } else {
        	if(mset) {
        	    Cursor cursor = new Cursor(display, SWT.CURSOR_ARROW);
                this.canvas.getShell().setCursor(cursor);
                mset = false;
        	}
        }
        if ((mouseEvent.stateMask & 0x80000) != 0x0 && Emulator.getCurrentDisplay().getCurrent() != null) {
            Emulator.getEventQueue().mouseDrag((int)(mouseEvent.x / this.zoom), (int)(mouseEvent.y / this.zoom));
        }
    }
    
    public final void widgetDisposed(final DisposeEvent disposeEvent) {
        if (EmulatorScreen.aviWriter != null) {
            EmulatorScreen.aviWriter.method842();
            EmulatorScreen.aviWriter = null;
        }
        ((EmulatorImpl)Emulator.getEmulator()).disposeSubWindows();
        Emulator.notifyDestroyed();
        if (this.pauseState != 0) {
            Emulator.getEventQueue().queue(11);
        }
    }
    
    public final void controlMoved(final ControlEvent controlEvent) {
        this.method576();
        if (((Class11)Emulator.getEmulator().getLogStream()).method327()) {
            final Shell method328 = ((Class11)Emulator.getEmulator().getLogStream()).method328();
            if (((Class11)Emulator.getEmulator().getLogStream()).method333() && !((Widget)method328).isDisposed()) {
                ((Control)method328).setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
            }
        }
        if (((Class83)Emulator.getEmulator().getMessage()).method479()) {
            final Shell method329 = ((Class83)Emulator.getEmulator().getMessage()).method480();
            if (((Class83)Emulator.getEmulator().getMessage()).method488() && !((Widget)method329).isDisposed()) {
                ((Control)method329).setLocation(this.shell.getLocation().x - method329.getSize().x, this.shell.getLocation().y);
            }
        }
        final Shell method330;
        if (((EmulatorImpl)Emulator.getEmulator()).method825().method610() && !((Widget)(method330 = ((EmulatorImpl)Emulator.getEmulator()).method825().method611())).isDisposed()) {
            ((Control)method330).setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
        }
        final Shell method331;
        if (((EmulatorImpl)Emulator.getEmulator()).method826().method834() && !((Widget)(method331 = ((EmulatorImpl)Emulator.getEmulator()).method826().method833())).isDisposed()) {
            ((Control)method331).setLocation(this.shell.getLocation().x + this.shell.getSize().x, this.shell.getLocation().y);
        }
    }
    
    public final void controlResized(final ControlEvent controlEvent) {
        this.controlMoved(controlEvent);
    }
    
    public final void startVibra(final long aLong1013) {
        if (!Settings.enableVibration) {
            return;
        }
        this.aLong1013 = aLong1013;
        if (this.aLong1013 == 0L) {
            this.stopVibra();
            return;
        }
        if (this.aClass119_976 == null) {
            this.aClass119_976 = new Vibrate(this);
            new Thread(this.aClass119_976).start();
            return;
        }
        this.aLong1017 = System.currentTimeMillis();
    }
    
    public final void stopVibra() {
        if (this.aClass119_976 != null) {
            this.aClass119_976.aBoolean1194 = true;
        }
    }
    
    private void method589() {
        final DropTarget dropTarget;
        (dropTarget = new DropTarget((Control)this.canvas, 19)).setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener((DropTargetListener)new Class29(this));
    }
    
    public final ICaret getCaret() {
        return this.aClass67_973;
    }
    
    static Shell method561(final EmulatorScreen class93) {
        return class93.shell;
    }
    
    public static Display method564() {
        return EmulatorScreen.display;
    }
    
    static Canvas method558(final EmulatorScreen class93) {
        return class93.canvas;
    }
    
    static String method560(final EmulatorScreen class93) {
        return class93.aString983;
    }
    
    static CLabel method563(final EmulatorScreen class93) {
        return class93.aCLabel970;
    }
    
    static String method573(final EmulatorScreen class93) {
        return class93.aString989;
    }
    
    static CLabel method574(final EmulatorScreen class93) {
        return class93.aCLabel984;
    }
    
    static int method566(final EmulatorScreen class93) {
        return class93.pauseState;
    }
    
    static boolean[] method556(final EmulatorScreen class93) {
        return class93.aBooleanArray978;
    }
    
    static int method562(final EmulatorScreen class93, final int anInt1020) {
        return class93.anInt1020 = anInt1020;
    }
    
    static long method559(final EmulatorScreen class93, final long aLong1017) {
        return class93.aLong1017 = aLong1017;
    }
    
    static long method567(final EmulatorScreen class93) {
        return class93.aLong1017;
    }
    
    static long method575(final EmulatorScreen class93) {
        return class93.aLong1013;
    }
    
    static Vibrate method555(final EmulatorScreen class93, final Vibrate aClass119_976) {
        return class93.aClass119_976 = aClass119_976;
    }
    
    static {
        EmulatorScreen.anInt1012 = 1;
        EmulatorScreen.aString993 = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", Locale.ENGLISH).format(Calendar.getInstance().getTime()) + "_";
        EmulatorScreen.aLong991 = 0L;
        EmulatorScreen.aLong1000 = 0L;
        EmulatorScreen.aLong1007 = 0L;
        EmulatorScreen.aBoolean967 = true;
        EmulatorScreen.aBoolean992 = true;
    }
    
    final class ShellPosition implements Runnable
    {
        int anInt1478;
        int anInt1481;
        boolean aBoolean1479;
        private final EmulatorScreen aClass93_1480;
        
        ShellPosition(final EmulatorScreen aClass93_1480, final int anInt1478, final int anInt1479, final boolean aBoolean1479) {
            super();
            this.aClass93_1480 = aClass93_1480;
            this.anInt1478 = anInt1478;
            this.anInt1481 = anInt1479;
            this.aBoolean1479 = aBoolean1479;
        }
        
        public final void run() {
            if (this.aBoolean1479) {
                this.anInt1478 = EmulatorScreen.method561(this.aClass93_1480).getLocation().x;
                this.anInt1481 = EmulatorScreen.method561(this.aClass93_1480).getLocation().y;
                return;
            }
            ((Control)EmulatorScreen.method561(this.aClass93_1480)).setLocation(this.anInt1478, this.anInt1481);
        }
    }
    
    private final class Vibrate implements Runnable
    {
        int anInt1193;
        boolean aBoolean1194;
        Random aRandom1195;
        int anInt1197;
        int anInt1198;
        private final EmulatorScreen aClass93_1196;
        
        Vibrate(final EmulatorScreen aClass93_1196) {
            super();
            EmulatorScreen.method559(this.aClass93_1196 = aClass93_1196, System.currentTimeMillis());
            this.aBoolean1194 = false;
            this.aRandom1195 = new Random();
        }
        
        public final void run() {
            this.anInt1193 = 10;
            final ShellPosition shellPosition = this.aClass93_1196.new ShellPosition(aClass93_1196, 0, 0, true);
            while (System.currentTimeMillis() - EmulatorScreen.method567(this.aClass93_1196) < EmulatorScreen.method575(this.aClass93_1196) && !this.aBoolean1194) {
                if (EmulatorScreen.method566(this.aClass93_1196) != 2) {
                    EmulatorImpl.syncExec(shellPosition);
                    this.anInt1197 = shellPosition.anInt1478;
                    this.anInt1198 = shellPosition.anInt1481;
                    if (this.anInt1193++ > 10) {
                        EmulatorImpl.asyncExec(this.aClass93_1196.new ShellPosition(aClass93_1196, this.anInt1197 + this.aRandom1195.nextInt() % 5, this.anInt1198 + this.aRandom1195.nextInt() % 5, false));
                        this.anInt1193 = 0;
                    }
                    EmulatorImpl.asyncExec(this.aClass93_1196.new ShellPosition(aClass93_1196, this.anInt1197, this.anInt1198, false));
                }
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {}
            }
            EmulatorScreen.method555(this.aClass93_1196, this);
        }
    }
    
    final class WindowOpen implements Runnable
    {
        int anInt1058;
        private final EmulatorScreen aClass93_1059;
        
        WindowOpen(final EmulatorScreen aClass93_1059, final int anInt1058) {
            super();
            this.aClass93_1059 = aClass93_1059;
            this.anInt1058 = 0;
            this.anInt1058 = anInt1058;
        }
        
        public final void run() {
            switch (this.anInt1058) {
                case 0: {
                    if (Settings.showMemViewFrame) {
                        this.aClass93_1059.aMenuItem961.setSelection(true);
                        ((EmulatorImpl)Emulator.getEmulator()).method823().method621();
                        return;
                    }
                    break;
                }
                case 1: {
                    if (Settings.showLogFrame) {
                        this.aClass93_1059.aMenuItem957.setSelection(true);
                        ((Class11)Emulator.getEmulator().getLogStream()).method329(EmulatorScreen.method561(this.aClass93_1059));
                        return;
                    }
                    break;
                }
                case 2: {
                    if (Settings.showInfoFrame) {
                        this.aClass93_1059.infosMenuItem.setSelection(true);
                        ((Control)EmulatorScreen.method558(this.aClass93_1059)).setCursor(new Cursor((Device)EmulatorScreen.method564(), 2));
                        ((EmulatorImpl)Emulator.getEmulator()).method825().method607(EmulatorScreen.method561(this.aClass93_1059));
                        break;
                    }
                    break;
                }
            }
        }
    }

	public void mouseScrolled(MouseEvent arg0) {
        if (this.pauseState == 0 || Settings.q) {
            return;
        }
		int k = 0;
    	if(arg0.count < 0) { 
    		k = Integer.parseInt(Keyboard.method605(2));
    		/*
    		Event e = new Event();
    		e.widget = this.canvas;
    		KeyEvent ke = new KeyEvent(e);
    		ke.keyCode = 16777218;
    		ke.character = '\0';
    		ke.doit = true;
    		ke.stateMask = 0;
    		this.keyPressed(ke);
    		this.keyReleased(ke);
    		*/
    	} else if(arg0.count > 0) { 
    		k = Integer.parseInt(Keyboard.method605(1));
    		/*
    		Event e = new Event();
    		e.widget = this.canvas;
    		KeyEvent ke = new KeyEvent(e);
    		ke.keyCode = 16777217;
    		ke.character = '\0';
    		ke.doit = true;
    		ke.stateMask = 0;
    		this.keyPressed(ke);
    		this.keyReleased(ke);
    		*/
    	}
    	if(k != 0) {
    		mp(k);
    		mr(k);
    	}
		
	}
}
