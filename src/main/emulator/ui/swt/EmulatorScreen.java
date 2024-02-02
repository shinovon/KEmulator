package emulator.ui.swt;

import org.eclipse.swt.custom.*;
import emulator.graphics2D.swt.ImageSWT;
import emulator.media.*;
import emulator.graphics2D.*;
import emulator.debug.*;
import emulator.*;
import emulator.custom.CustomMethod;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.dnd.*;
import emulator.ui.*;
import java.text.*;
import java.util.*;

import org.eclipse.swt.widgets.*;

import org.eclipse.swt.events.*;

public final class EmulatorScreen implements 
IScreen, Runnable, PaintListener, DisposeListener, 
ControlListener, KeyListener, MouseListener,
MouseMoveListener, SelectionListener, MouseWheelListener,
MouseTrackListener
{
    private static Display display;
    private static int threadCount;
    private long lastPollTime;

    public Shell getShell() {
        return shell;
    }

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
   // private Menu menu3dEngine;
    private Menu aMenu1018;
    private Menu menuInterpolation;
    public static int locX;
    public static int locY;
    private Transform jdField_a_of_type_OrgEclipseSwtGraphicsTransform;
    private Transform jdField_b_of_type_OrgEclipseSwtGraphicsTransform;
    private int jdField_c_of_type_Int;
    private int jdField_d_of_type_Int;
    private int jdField_e_of_type_Int;
    private float zoom;
    private int anInt996;
    private int anInt1003;
    private Image screenImg;
    private ImageSWT screenCopySwt;
    private ImageSWT screenImageSwt;
    private ImageSWT backBufferImageSwt;
    private ImageSWT xrayScreenImageSwt;
    private emulator.graphics2D.awt.d screenCopyAwt;
    private emulator.graphics2D.awt.d screenImageAwt;
    private emulator.graphics2D.awt.d backBufferImageAwt;
    private emulator.graphics2D.awt.d xrayScreenImageAwt;
    private static long aLong982;
    private String aString983;
    private String aString989;
    MenuItem awt2dMenuItem;
    MenuItem swt2dMenuItem;
    //MenuItem swt3dMenuItem;
 //   MenuItem lwj3dMenuItem;
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
   // MenuItem aMenuItem962;
    MenuItem aMenuItem963;
    MenuItem aMenuItem964;
    MenuItem networkKillswitchMenuItem;
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
    private CaretImpl caret;
    private int anInt1020;
    private boolean[] keysState;
    private int mouseXPress;
    private int mouseXRelease;
    private int mouseYPress;
    private int mouseYRelease;
    private boolean mouseDownInfos;
    private Vibrate vibraThread;
    private long vibra;
    private long vibraStart;
	private MenuItem canvasKeyboardMenuItem;
	private MenuItem fpsModeMenuItem;
	private int lastMouseMoveX;
	private boolean fpsWasRight;
	private boolean fpsWasLeft;
	private boolean ignoreNextFps;
	private boolean fpsWasntHor;
	private boolean mset;
	private boolean fpsWasUp;
	private boolean fpsWasBottom;
	private boolean fpsWasntVer;
	private MenuItem rotate90MenuItem;
	private int keysPressed;
	private Vector<Integer> pressedKeys = new Vector<Integer>();
	private int lastDragTime;
	private int lastMouseX;
	private int lastMouseY;
	private boolean shifted;
	static Font f;
    
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
       // this.menu3dEngine = null;
        this.aMenu1018 = null;
        this.aStringArray981 = new String[] { emulator.UILocale.get("MAIN_INFO_BAR_UNLOADED", "UNLOADED"), emulator.UILocale.get("MAIN_INFO_BAR_RUNNING", "RUNNING"), emulator.UILocale.get("MAIN_INFO_BAR_PAUSED", "PAUSED") };
        EmulatorScreen.display = EmulatorImpl.getDisplay();
        this.initShell();
        this.initScreenBuffer(n, n2);
        this.updatePauseState();
    }
    
    private void initScreenBuffer(final int n, final int n2) {
        final int bgcolor = 0xffffff; // 9934483
        if (Settings.g2d == 0) {
            this.screenCopySwt = new ImageSWT(n, n2, false, bgcolor);
            this.screenImageSwt = new ImageSWT(n, n2, false, bgcolor);
            this.backBufferImageSwt = new ImageSWT(n, n2, false, bgcolor);
            this.xrayScreenImageSwt = new ImageSWT(n, n2, true, bgcolor);
            return;
        }
        if (Settings.g2d == 1) {
            this.screenCopyAwt = new emulator.graphics2D.awt.d(n, n2, false, bgcolor);
            this.screenImageAwt = new emulator.graphics2D.awt.d(n, n2, false, bgcolor);
            this.backBufferImageAwt = new emulator.graphics2D.awt.d(n, n2, false, bgcolor);
            this.xrayScreenImageAwt = new emulator.graphics2D.awt.d(n, n2, true, -16777216);
        }
    }
    
    public final void setWindowIcon(final InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        try {
            ((Decorations)this.shell).setImage(new Image((Device)null, inputStream));
        }
        catch (Exception ex) {}
    }
    
    public final void showMessage(final String message) {
        setWindowOnTop(getHandle(shell), true);
        final MessageBox messageBox;
        ((Dialog)(messageBox = new MessageBox(this.shell))).setText(emulator.UILocale.get("MESSAGE_BOX_TITLE", "KEmulator Alert"));
        messageBox.setMessage(message);
        messageBox.open();
    }
    
    private static long getHandle(Control c) {
    	try {
    		Class<?> cl = c.getClass();
    		Field f = cl.getField("handle");
    		return f.getLong(c);
    	} catch (Exception e) {
    		throw new Error(e);
    	}
	}

	private void getWindowPos() {
        EmulatorScreen.locX = this.shell.getLocation().x;
        EmulatorScreen.locY = this.shell.getLocation().y;
    }
    
    public final void start(final boolean midletLoaded) {
        try {
            this.zoom(Settings.canvasScale / 100.0f);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.showMessage(emulator.UILocale.get("LOAD_GDIPLUS_ERROR", "Can't load \" gdiplus.dll \" !!! Plz download & copy to %system32% path."));
            System.exit(1);
            return;
        }
        this.pauseState = (midletLoaded ? 1 : 0);
        this.updatePauseState();
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
        if(win) {
            new Thread("KEmulator keyboard poll thread") {
                boolean b;
                public void run() {
                    try {
                        if(b) {
                            pollKeyboard(canvas);
                            return;
                        }
                        b = true;
                        while (shell != null && !((Widget)shell).isDisposed()) {
                            display.asyncExec(this);
                            Thread.sleep(20);
                        }
                    } catch (Exception e) {
                        System.out.println("Exception in keyboard poll thread");
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    	try {
	        while (this.shell != null && !((Widget)this.shell).isDisposed()) {
	        	//pollKeyboard(canvas);
	            if (!EmulatorScreen.display.readAndDispatch()) {
	                EmulatorScreen.display.sleep();
	            }
	        }
    	} catch (Error e) {
    		e.printStackTrace();
    	} catch (Exception e) {
    		e.printStackTrace();
        	CustomMethod.close();
    		System.exit(1);
    	}
        this.pauseState = 0;
    }
    
    public static final Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		for(Method method : clazz.getDeclaredMethods()) {
			Class<?>[] checkParamTypes = method.getParameterTypes();
			if(method.getName().equals(name) && checkParamTypes.length == parameterTypes.length) {
				boolean matches = true;
				for(int i = 0; i < parameterTypes.length; i++) {
					Class<?> paramType = parameterTypes[i];
					Class<?> checkParamType = checkParamTypes[i];
					
					if(!equals(paramType, checkParamType)) {
						matches = false;
						break;
					}
				}
				if(!matches) {
					continue;
				}
				return method;
			}
		}
		return null;
	}
    
    public static final boolean equals(Class<?> class1, Class<?> class2) {
		if(class1 == null) {
			if(class2 == null) {
				return true;
			}
			return false;
		}
		if(class2 == null) {
			return false;
		}
		return class1.isAssignableFrom(class2) && class2.isAssignableFrom(class1);
	}
    
    // KEYBOARD

	protected static volatile boolean[] lastKeyboardButtonStates = new boolean[256];
	protected static volatile boolean[] keyboardButtonStates = new boolean[lastKeyboardButtonStates.length];
	protected static volatile long[] keyboardButtonDownTimes = new long[keyboardButtonStates.length];
	protected static volatile long[] keyboardButtonHoldTimes = new long[keyboardButtonStates.length];
	private static Class win32OS;
	private static Method win32OSGetKeyState;
	boolean win = System.getProperty("os.name").startsWith("Win");
	
    public synchronized void pollKeyboard(Canvas canvas) {
    	if(Settings.canvasKeyboard || !win || canvas == null || canvas.isDisposed()) return;
        long time = System.currentTimeMillis();
        if(time - lastPollTime < 10) return;
        lastPollTime = time;
        final boolean active = canvas.getDisplay().getActiveShell() == canvas.getShell() && canvas.getShell().isVisible();
        if(win32OS == null) {
            try {
                win32OS = Class.forName("org.eclipse.swt.internal.win32.OS");
            } catch (Exception e) {
            }
            if (win32OS == null)
                return;
        }
        if(win32OSGetKeyState == null) {
            win32OSGetKeyState = getMethod(win32OS, "GetAsyncKeyState", Integer.TYPE);
            if(win32OSGetKeyState == null)
                return;
        }

        long now = System.currentTimeMillis();
        for(int i = 0; i < keyboardButtonStates.length; i++) {
            lastKeyboardButtonStates[i] = keyboardButtonStates[i];
            short keyState;
            try {
                keyState = ((Short) win32OSGetKeyState.invoke(null, Integer.valueOf(i))).shortValue();
            } catch(Exception e) {
                e.printStackTrace();
                return;
            }
            //keyboardButtonStates[i] = active ? keyState/*org.eclipse.swt.internal.win32.OS.GetKeyState(i)*/ > 0 : false;
            boolean pressed = active && ((keyState & 0x8000) == 0x8000 || ((keyState & 0x1) == 0x1));
            if(!keyboardButtonStates[i]) {
                if(pressed) {
                    keyboardButtonStates[i] = true;
                    keyboardButtonHoldTimes[i] = 0;
                    keyboardButtonDownTimes[i] = now;
                    onKeyDown(i);
                }
            } else if(!pressed) {
                keyboardButtonStates[i] = false;
                keyboardButtonHoldTimes[i] = 0;
                onKeyUp(i);
            }
            if(lastKeyboardButtonStates[i] && pressed && now - keyboardButtonDownTimes[i] >= 460) {
                if(keyboardButtonHoldTimes[i] == 0 || keyboardButtonDownTimes[i] > keyboardButtonHoldTimes[i]) {
                    keyboardButtonHoldTimes[i] = now;
                }
                if(now - keyboardButtonHoldTimes[i] >= 40) {
                    keyboardButtonHoldTimes[i] = now;
                    onKeyHeld(i);
                }
            }
        }
	}
    

    private void rotate(int var1, int var2) {
       if(this.pauseState == 1/* && Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()*/) {
          this.initScreenBuffer(var1, var2);
          this.zoom(this.zoom);
          Emulator.getEventQueue().queue(Integer.MIN_VALUE, var1, var2);
       }
    }
    
    private void rotate90degrees(boolean paramBoolean)
    {
      if (this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform == null) {
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform = new Transform(null);
      }
      if (this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform == null) {
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform = new Transform(null);
      }
      if (!paramBoolean)
      {
        this.jdField_c_of_type_Int += 1;
        this.jdField_c_of_type_Int %= 4;
      }
      this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.setElements(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
      this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.setElements(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
      int i1 = getWidth();
      int i2 = getHeight();
      switch (this.jdField_c_of_type_Int)
      {
      case 0: 
        this.jdField_d_of_type_Int = i1;
        this.jdField_e_of_type_Int = i2;
        break;
      case 1: 
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.translate(i2 * zoom, 0.0F);
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.rotate(90.0F);
        this.jdField_d_of_type_Int = i2;
        this.jdField_e_of_type_Int = i1;
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.translate(i2 * zoom, 0.0F);
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.rotate(90.0F);
        break;
      case 2: 
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.translate(i1 * zoom, i2 * zoom);
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.rotate(180.0F);
        this.jdField_d_of_type_Int = i1;
        this.jdField_e_of_type_Int = i2;
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.translate(i1 * zoom, i2 * zoom);
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.rotate(180.0F);
        break;
      case 3: 
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.translate(0.0F, i1 * zoom);
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.rotate(270.0F);
        this.jdField_d_of_type_Int = i2;
        this.jdField_e_of_type_Int = i1;
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.translate(0.0F, i1 * zoom);
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.rotate(270.0F);
      }
      try
      {
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.invert();
      }
      catch (Exception localException) {}
      this.caret.a(this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform, this.jdField_c_of_type_Int);
      if (!paramBoolean) {
        d();
      }
    }
    

    private void zoom(float var1) {
       this.zoom = var1;
       this.anInt996 = (int)((float)this.getWidth() * this.zoom);
       this.anInt1003 = (int)((float)this.getHeight() * this.zoom);
       rotate90degrees(true);
       Settings.canvasScale = (int)(this.zoom * 100.0F);
       this.caret.setWindowZoom(this.zoom);
       this.d();
       this.h();
    }

    
    private void d() {
        int i1 = this.shell.getSize().x - this.canvas.getSize().x;
        int i2 = this.shell.getSize().y - this.canvas.getSize().y;
        this.canvas.setSize((int)(this.jdField_d_of_type_Int * this.zoom) + this.canvas.getBorderWidth() * 2, (int)((float)this.jdField_e_of_type_Int * this.zoom) + this.canvas.getBorderWidth() * 2);
        this.shell.setSize(this.canvas.getSize().x + i1, this.canvas.getSize().y + i2);
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
        if (this.screenImg != null && !this.screenImg.isDisposed()) {
            this.screenImg.dispose();
        }
        this.screenImg = new Image((Device)null, this.getWidth(), this.getHeight());
        final GC gc = new GC((Drawable)this.screenImg);
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
    
    public final IImage getScreenImg() {
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
        if (Settings.playingRecordedKeys) {
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
        else if (Settings.enableKeyCache && !Keyboard.keyCacheStack.empty()) {
            final String s = (String) Keyboard.keyCacheStack.pop();
            if (Settings.recordKeys && !Settings.playingRecordedKeys) {
                Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + s);
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
        return this.getScreenImg().getWidth();
    }
    
    public final int getHeight() {
        return this.getScreenImg().getHeight();
    }
    
    public final void setCommandLeft(final String aString983) {
        this.aString983 = aString983;
        EmulatorImpl.syncExec(new Class41(this));
    }
    
    public final void setCommandRight(final String aString989) {
        this.aString989 = aString989;
        EmulatorImpl.syncExec(new Class40(this));
    }
    

    private void h() {
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
          var9.append(emulator.UILocale.get("MAIN_INFO_BAR_NET", "NET"));
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

    
    private void initShell() {
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
        // 224
        // TODO: Free resize
        ((Decorations)(this.shell = new Shell(SWT.CLOSE | SWT.TITLE /*| SWT.RESIZE | SWT.MAX */| SWT.MIN))).setText(Emulator.getTitleVersionString());
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
            	CustomMethod.close();
            }
          });
        ((Composite)this.shell).setLayout((Layout)layout);
        try {
            if(f == null) {
                FontData fd = shell.getFont().getFontData()[0];
                fd.height = (fd.height / -fd.data.lfHeight) * 12;
                f = new Font(shell.getDisplay(), fd);
            }
        	shell.setFont(f);
        } catch (Error e) {
        }
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
        (menuItemMidlet = new MenuItem(this.aMenu971, 64)).setText(emulator.UILocale.get("MENU_MIDLET", "Midlet"));
        final MenuItem menuItemTool;
        (menuItemTool = new MenuItem(this.aMenu971, 64)).setText(emulator.UILocale.get("MENU_TOOL", "Tool"));
        final MenuItem menuItemView;
        (menuItemView = new MenuItem(this.aMenu971, 64)).setText(emulator.UILocale.get("MENU_VIEW", "View"));
        this.menuView = new Menu(menuItemView);
        (this.infosMenuItem = new MenuItem(this.menuView, 32)).setText(emulator.UILocale.get("MENU_VIEW_INFO", "Infos") + "\tCtrl+I");
        this.infosMenuItem.addSelectionListener((SelectionListener)this);
        (this.xrayViewMenuItem = new MenuItem(this.menuView, 32)).setText(emulator.UILocale.get("MENU_VIEW_XRAY", "X-Ray View") + "\tCtrl+X");
        this.xrayViewMenuItem.addSelectionListener((SelectionListener)this);
        (this.alwaysOnTopMenuItem = new MenuItem(this.menuView, 32)).setText(emulator.UILocale.get("MENU_VIEW_TOP", "Always On Top") + "\tCtrl+O");
        this.alwaysOnTopMenuItem.addSelectionListener((SelectionListener)this);
        this.alwaysOnTopMenuItem.setSelection(Settings.alwaysOnTop);
        (this.rotateScreenMenuItem = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_ROTATE", "Rotate Screen") + "\tCtrl+Y");
        this.rotateScreenMenuItem.addSelectionListener((SelectionListener)this);

        this.rotate90MenuItem = new MenuItem(this.menuView, 8);
        this.rotate90MenuItem.setText("Rotate 90 Degrees");
        this.rotate90MenuItem.addSelectionListener(this);
        (this.forecPaintMenuItem = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_FORCE_PAINT", "Force Paint") + "\tCtrl+F");
        this.forecPaintMenuItem.addSelectionListener((SelectionListener)this);
        setWindowOnTop(getHandle(shell), Settings.alwaysOnTop);
        new MenuItem(this.menuView, 2);
        (this.aMenuItem956 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_KEYPAD", "Keypad"));
        this.aMenuItem956.addSelectionListener((SelectionListener)this);
        (this.aMenuItem958 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_WATCHES", "Watches"));
        this.aMenuItem958.addSelectionListener((SelectionListener)this);
        (this.aMenuItem959 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_PROFILER", "Profiler"));
        this.aMenuItem959.addSelectionListener((SelectionListener)this);
        (this.aMenuItem960 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_METHODS", "Methods"));
        this.aMenuItem960.addSelectionListener((SelectionListener)this);
        (this.aMenuItem961 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_MEMORY", "Memory View"));
        this.aMenuItem961.addSelectionListener((SelectionListener)this);
      //  (this.aMenuItem962 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.uiText("MENU_VIEW_M3GVIEW", "M3G View"));
      //  this.aMenuItem962.addSelectionListener((SelectionListener)this);
        (this.aMenuItem963 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_SMS", "SMS Console"));
        this.aMenuItem963.addSelectionListener((SelectionListener)this);
        (this.aMenuItem964 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_SENSOR", "Sensor Simulator"));
        this.aMenuItem964.addSelectionListener((SelectionListener)this);
        (this.aMenuItem957 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_LOG", "Log"));
        this.aMenuItem957.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuView, 2);
        (this.aMenuItem950 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_OPTIONS", "Options..."));
        this.aMenuItem950.addSelectionListener((SelectionListener)this);
        (this.aMenuItem949 = new MenuItem(this.menuView, 8)).setText(emulator.UILocale.get("MENU_VIEW_HELP", "About"));
        this.aMenuItem949.addSelectionListener((SelectionListener)this);
        menuItemView.setMenu(this.menuView);
        this.menuTool = new Menu(menuItemTool);
        (this.zoomInMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_ZOOMIN", "Zoom In") + "\tPad+");
        this.zoomInMenuItem.addSelectionListener((SelectionListener)this);
        (this.zoomOutMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_ZOOMOUT", "Zoom Out") + "\tPad-");
        this.zoomOutMenuItem.addSelectionListener((SelectionListener)this);
        final MenuItem menuItem4;
        (menuItem4 = new MenuItem(this.menuTool, 64)).setText(emulator.UILocale.get("MENU_TOOL_INTERPOSE", "Interpolation"));
        this.menuInterpolation = new Menu((Decorations)this.shell, 4194308);
        (this.interposeNearestMenuItem = new MenuItem(this.menuInterpolation, 16)).setText(emulator.UILocale.get("MENU_TOOL_INTER_NEAREST", "NearestNeighbor"));
        this.interposeNearestMenuItem.setSelection(true);
        this.interposeNearestMenuItem.addSelectionListener((SelectionListener)new Class52(this));
        (this.interposeLowMenuItem = new MenuItem(this.menuInterpolation, 16)).setText(emulator.UILocale.get("MENU_TOOL_INTER_LOW", "LowQuality"));
        this.interposeLowMenuItem.addSelectionListener((SelectionListener)new Class55(this));
        (this.interposeHightMenuItem = new MenuItem(this.menuInterpolation, 16)).setText(emulator.UILocale.get("MENU_TOOL_INTER_HIGH", "HighQuality"));
        this.interposeHightMenuItem.addSelectionListener((SelectionListener)new Class42(this));
        menuItem4.setMenu(this.menuInterpolation);
        new MenuItem(this.menuTool, 2);
        (this.speedUpMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_SPPEDUP", "Speed Up") + "\tAlt+>");
        this.speedUpMenuItem.addSelectionListener((SelectionListener)this);
        (this.slowDownMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_SPPEDDONW", "Slow Down") + "\tAlt+<");
        this.slowDownMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.startpauseTickMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_TICKSTART", "Start/Pause Tick") + "\tCtrl+K");
        this.startpauseTickMenuItem.addSelectionListener((SelectionListener)this);
        (this.resetTickMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_TICKRESET", "Reset Tick") + "\tCtrl+L");
        this.resetTickMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.recordKeysMenuItem = new MenuItem(this.menuTool, 32)).setText(emulator.UILocale.get("MENU_TOOL_RECORD_KEY", "Record Keys"));
        this.recordKeysMenuItem.addSelectionListener((SelectionListener)this);
        this.recordKeysMenuItem.setSelection(Settings.recordKeys);
        this.recordKeysMenuItem.setEnabled(!Settings.playingRecordedKeys);
        (this.enableAutoplayMenuItem = new MenuItem(this.menuTool, 32)).setText(emulator.UILocale.get("MENU_TOOL_AUTO_PLAY", "Enable Autoplay"));
        this.enableAutoplayMenuItem.addSelectionListener((SelectionListener)this);
        this.enableAutoplayMenuItem.setSelection(Settings.playingRecordedKeys);
        this.enableAutoplayMenuItem.setEnabled(Settings.playingRecordedKeys);
        new MenuItem(this.menuTool, 2);
        (this.captureToFileMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_CAPTURE_FILE", "Capture to File") + "\tCtrl+C");
        this.captureToFileMenuItem.addSelectionListener((SelectionListener)this);
        (this.captureToClipboardMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_CAPTURE_CLIP", "Capture to ClipBoard") + "\tAlt+C");
        this.captureToClipboardMenuItem.setAccelerator(65603);
        this.captureToClipboardMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.startRecordAviMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_START_RECORD_AVI", "Start Record AVI") + "\tCtrl+V");
        this.startRecordAviMenuItem.addSelectionListener((SelectionListener)this);
        (this.stopRecordAviMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_STOP_RECORD_AVI", "Stop Record AVI") + "\tCtrl+B");
        this.stopRecordAviMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuTool, 2);
        (this.connectNetworkMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_CONN_NETWORK", "Connect to Network"));
        this.connectNetworkMenuItem.addSelectionListener((SelectionListener)this);
        (this.disconnectNetworkMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_DISCONN_NETWORK", "Disconnect to Network"));
        this.disconnectNetworkMenuItem.addSelectionListener((SelectionListener)this);
        (this.channelUpMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_CHANNEL_UP", "Channel up") + "\tCtrl+>");
        this.channelUpMenuItem.addSelectionListener((SelectionListener)this);
        this.channelUpMenuItem.setAccelerator(262206);
        (this.channelDownMenuItem = new MenuItem(this.menuTool, 8)).setText(emulator.UILocale.get("MENU_TOOL_CHANNEL_DOWN", "Channel down") + "\tCtrl+<");
        this.channelDownMenuItem.addSelectionListener((SelectionListener)this);
        this.channelDownMenuItem.setAccelerator(262204);
        new MenuItem(this.menuTool, 2);
        (this.showTrackInfoMenuItem = new MenuItem(this.menuTool, 32)).setText(emulator.UILocale.get("MENU_TOOL_SHOW_TRACK_INFO", "Show Track Info") + "\tF3");
        this.showTrackInfoMenuItem.setSelection(Settings.threadMethodTrack);
        this.showTrackInfoMenuItem.addSelectionListener((SelectionListener)this);
        this.showTrackInfoMenuItem.setAccelerator(16777228);
        this.canvasKeyboardMenuItem = new MenuItem(this.menuTool, 32);
        canvasKeyboardMenuItem.setText("QWERTY Mode");
        canvasKeyboardMenuItem.setSelection(Settings.canvasKeyboard);
        canvasKeyboardMenuItem.addSelectionListener((SelectionListener)this);
        this.fpsModeMenuItem = new MenuItem(this.menuTool, 32);
        fpsModeMenuItem.setText("FPS Mode\tAlt+F");
        fpsModeMenuItem.setSelection(Settings.fpsMode);
        fpsModeMenuItem.addSelectionListener((SelectionListener)this);
        fpsModeMenuItem.setAccelerator(SWT.ALT + 'F');
        menuItemTool.setMenu(this.menuTool);
        this.networkKillswitchMenuItem = new MenuItem(this.menuTool, 32);
        networkKillswitchMenuItem.setText("Disable network access");
        networkKillswitchMenuItem.setSelection(Settings.networkNotAvailable);
        networkKillswitchMenuItem.addSelectionListener((SelectionListener)this);
        this.menuMidlet = new Menu(menuItemMidlet);
        (this.loadJarMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_LOAD_JAR", "Load jar..."));
        this.loadJarMenuItem.addSelectionListener((SelectionListener)this);
//        (this.loadWithConsoleMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_LOAD_WITH_CONSOLE", "Load with console"));
//        this.loadWithConsoleMenuItem.addSelectionListener((SelectionListener)this);
        (this.loadAutoPlayMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_LOAD_AUTO_PLAY", "Load auto-play record"));
        this.loadAutoPlayMenuItem.addSelectionListener((SelectionListener)this);
        final MenuItem menuItem5;
        (menuItem5 = new MenuItem(this.menuMidlet, 64)).setText(emulator.UILocale.get("MENU_MIDLET_RECENTLY", "Recent jarfiles"));
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
        (this.openJadMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_JAD", "Open JAD with Notepad") + "\tCtrl+D");
        this.openJadMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuMidlet, 2);
        final MenuItem menuItem7;
        (menuItem7 = new MenuItem(this.menuMidlet, 64)).setText(emulator.UILocale.get("MENU_MIDLET_2DENGINE", "2D Graphics Engine"));
        this.menu2dEngine = new Menu((Decorations)this.shell, 4194308);
        (this.awt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("AWT-Graphics");
        this.awt2dMenuItem.setSelection(Settings.g2d == 1);
        this.awt2dMenuItem.addSelectionListener((SelectionListener)this);
        (this.swt2dMenuItem = new MenuItem(this.menu2dEngine, 16)).setText("SWT-GDI+");
        this.swt2dMenuItem.setSelection(Settings.g2d == 0);
        this.swt2dMenuItem.addSelectionListener((SelectionListener)this);
        menuItem7.setMenu(this.menu2dEngine);
      /*
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

       */
        new MenuItem(this.menuMidlet, 2);
        (this.suspendMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_SUSPEND", "Suspend") + "\tCtrl+S");
        this.suspendMenuItem.addSelectionListener((SelectionListener)this);
        (this.resumeMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_RESUME", "Resume") + "\tCtrl+E");
        this.resumeMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuMidlet, 2);
        (this.pausestepMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_PAUSE_STEP", "Pause/Step") + "\tCtrl+T");
        this.pausestepMenuItem.addSelectionListener((SelectionListener)this);
        (this.playResumeMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_PLAY_RESUME", "Play/Resume") + "\tCtrl+R");
        this.playResumeMenuItem.addSelectionListener((SelectionListener)this);
        new MenuItem(this.menuMidlet, 2);
        (this.restartMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_RESTART", "Restart") + "\tAlt+R");
        this.restartMenuItem.setAccelerator(65618);
        this.restartMenuItem.addSelectionListener((SelectionListener)this);
        (this.exitMenuItem = new MenuItem(this.menuMidlet, 8)).setText(emulator.UILocale.get("MENU_MIDLET_EXIT", "Exit") + "\tESC");
        this.exitMenuItem.setAccelerator(27);
        this.exitMenuItem.addSelectionListener((SelectionListener)this);
        menuItemMidlet.setMenu(this.menuMidlet);

        this.infosMenuItem.setAccelerator(SWT.CONTROL | 73);
        this.xrayViewMenuItem.setAccelerator(SWT.CONTROL | 88);
        this.alwaysOnTopMenuItem.setAccelerator(SWT.CONTROL | 79);
        this.rotateScreenMenuItem.setAccelerator(SWT.CONTROL | 89);
        this.forecPaintMenuItem.setAccelerator(SWT.CONTROL | 70);
        this.speedUpMenuItem.setAccelerator(SWT.ALT | 46);
        this.slowDownMenuItem.setAccelerator(SWT.ALT | 44);
        this.startpauseTickMenuItem.setAccelerator(SWT.CONTROL | 75);
        this.resetTickMenuItem.setAccelerator(SWT.CONTROL | 76);
        this.stopRecordAviMenuItem.setAccelerator(SWT.CONTROL | 66);
        this.suspendMenuItem.setAccelerator(SWT.CONTROL | 83);
        this.resumeMenuItem.setAccelerator(SWT.CONTROL | 69);
        this.openJadMenuItem.setAccelerator(SWT.CONTROL | 68);
        this.pausestepMenuItem.setAccelerator(SWT.CONTROL | 84);
        this.playResumeMenuItem.setAccelerator(SWT.CONTROL | 82);

        toggleMenuAccelerators(!Settings.canvasKeyboard);

        setFpsMode(Settings.fpsMode);
        ((Decorations)this.shell).setMenuBar(this.aMenu971);
    }
    
    
    
    protected void toggleMenuAccelerators(final boolean b) {
        if (b) {

            this.captureToFileMenuItem.setAccelerator(SWT.CONTROL | 67);
            this.startRecordAviMenuItem.setAccelerator(SWT.CONTROL | 86);
            /*
            this.infosMenuItem.setAccelerator(SWT.CONTROL | 73);
            this.xrayViewMenuItem.setAccelerator(SWT.CONTROL | 88);
            this.alwaysOnTopMenuItem.setAccelerator(SWT.CONTROL | 79);
            this.rotateScreenMenuItem.setAccelerator(SWT.CONTROL | 89);
            this.forecPaintMenuItem.setAccelerator(SWT.CONTROL | 70);
            this.speedUpMenuItem.setAccelerator(SWT.ALT | 46);
            this.slowDownMenuItem.setAccelerator(SWT.ALT | 44);
            this.startpauseTickMenuItem.setAccelerator(SWT.CONTROL | 75);
            this.resetTickMenuItem.setAccelerator(SWT.CONTROL | 76);
            this.captureToFileMenuItem.setAccelerator(SWT.CONTROL | 67);
            this.startRecordAviMenuItem.setAccelerator(SWT.CONTROL | 86);
            this.stopRecordAviMenuItem.setAccelerator(SWT.CONTROL | 66);
            this.suspendMenuItem.setAccelerator(SWT.CONTROL | 83);
            this.resumeMenuItem.setAccelerator(SWT.CONTROL | 69);
            this.openJadMenuItem.setAccelerator(SWT.CONTROL | 68);
            this.pausestepMenuItem.setAccelerator(SWT.CONTROL | 84);
            this.playResumeMenuItem.setAccelerator(SWT.CONTROL | 82);
            */
        } else {
            this.captureToFileMenuItem.setAccelerator(0);
            this.startRecordAviMenuItem.setAccelerator(0);
            /*
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
            this.playResumeMenuItem.setAccelerator(0);
            */
        }
    }
    

    protected void setFpsMode(boolean b) {
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
        if (this.screenImg != null && !this.screenImg.isDisposed()) {
            this.screenImg.dispose();
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
                        this.screenCopySwt.saveToFile(string + EmulatorScreen.aString993 + EmulatorScreen.anInt1012 + ".png");
                    }
                    else if (Settings.g2d == 1) {
                        this.screenCopyAwt.saveToFile(string + EmulatorScreen.aString993 + EmulatorScreen.anInt1012 + ".png");
                    }
                    ++EmulatorScreen.anInt1012;
                }
            }
            else if (menuItem.equals(this.captureToClipboardMenuItem)) {
                if (this.pauseState != 0) {
                    if (Settings.g2d == 0) {
                        this.screenCopySwt.copyToClipBoard();
                        return;
                    }
                    if (Settings.g2d == 1) {
                        this.screenCopyAwt.copyToClipBoard();
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
                    ((Dialog)(fileDialog = new FileDialog(this.shell, 8192))).setText(emulator.UILocale.get("SAVE_TO_FILE", "Save to file"));
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
                        this.updatePauseState();
                    }
                    this.method571();
                    return;
                }
                if (menuItem.equals(this.stopRecordAviMenuItem)) {
                    if (EmulatorScreen.aviWriter != null) {
                        EmulatorScreen.aviWriter.method842();
                        EmulatorScreen.aviWriter = null;
                    }
                    this.updatePauseState();
                    return;
                }
                if (menuItem.equals(this.connectNetworkMenuItem)) {
                    Emulator.getNetMonitor().a();
                    Emulator.getEmulator().getLogStream().println("!!!Emulator connect to network " + (true ? "Successful!" : "failed!"));
                    this.updatePauseState();
                    return;
                }
                if (menuItem.equals(this.disconnectNetworkMenuItem)) {
                    Emulator.getNetMonitor().a();
                    Emulator.getEmulator().getLogStream().println("!!!Emulator disconnect to network!");
                    this.updatePauseState();
                    return;
                }
                if (menuItem.equals(this.channelUpMenuItem)) {
                    Emulator.getNetMonitor().a(true);
                    this.updatePauseState();
                    return;
                }
                if(menuItem.equals(canvasKeyboardMenuItem)) {
                	Settings.canvasKeyboard = canvasKeyboardMenuItem.getSelection();
                	toggleMenuAccelerators(!Settings.canvasKeyboard);
                	return;
                }
                
                if(menuItem.equals(fpsModeMenuItem)) {
                	Settings.fpsMode = fpsModeMenuItem.getSelection();
                	setFpsMode(true);
                	return;
                }

                if(menuItem.equals(networkKillswitchMenuItem)) {
                    Settings.networkNotAvailable = !Settings.networkNotAvailable;
                    networkKillswitchMenuItem.setSelection(Settings.networkNotAvailable);
                    return;
                }
                
                if (menuItem.equals(this.channelDownMenuItem)) {
                    Emulator.getNetMonitor().a(false);
                    this.updatePauseState();
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
                    this.h();
                    return;
                }
                if (menuItem.equals(this.resetTickMenuItem)) {
                    EmulatorScreen.aLong1007 = System.currentTimeMillis();
                    EmulatorScreen.aLong991 = 0L;
                    EmulatorScreen.aBoolean967 = true;
                    EmulatorScreen.aBoolean992 = true;
                    this.h();
                    return;
                }
                if (menuItem.equals(this.speedUpMenuItem)) {
                    if (Settings.f == -1) {
                        Settings.f = 1;
                        this.h();
                        return;
                    }
                    if (Settings.f < 100) {
                        ++Settings.f;
                        this.h();
                    }
                }
                else if (menuItem.equals(this.slowDownMenuItem)) {
                    if (Settings.f == 1) {
                        Settings.f = -1;
                        this.h();
                        return;
                    }
                    if (Settings.f > -100) {
                        --Settings.f;
                        this.h();
                    }
                }
                else {
                    if (menuItem.equals(this.recordKeysMenuItem)) {
                        Settings.recordKeys = !Settings.recordKeys;
                        return;
                    }
                    if (menuItem.equals(this.enableAutoplayMenuItem)) {
                        Settings.playingRecordedKeys = !Settings.playingRecordedKeys;
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
                Emulator.loadGame(null, Settings.g2d, 1, false);
            }
            else if (menuItem.equals(this.loadJarMenuItem) /*|| (equals = menuItem.equals(this.loadWithConsoleMenuItem))*/) {
                method554();
                final FileDialog fileDialog2;
                ((Dialog)(fileDialog2 = new FileDialog(this.shell, 4096))).setText(emulator.UILocale.get("OPEN_JAR_FILE", "Open a jar file"));
                fileDialog2.setFilterExtensions(new String[] { "*.jar;*.jad", "*.*" });
                final String open2;
                if ((open2 = fileDialog2.open()) != null) {
                    Settings.recordedKeysFile = null;
                    Emulator.loadGame(open2, Settings.g2d, 1, equals);
                }
                this.method571();
            }
            else if (menuItem.equals(this.loadAutoPlayMenuItem)) {
                method554();
                final FileDialog fileDialog3;
                ((Dialog)(fileDialog3 = new FileDialog(this.shell, 4096))).setText(emulator.UILocale.get("OPEN_REC_FILE", "Open a record file"));
                fileDialog3.setFilterPath(Emulator.getAbsolutePath());
                fileDialog3.setFilterExtensions(new String[] { "*.rec", "*.*" });
                Label_1321: {
                    final String open3;
                    if ((open3 = fileDialog3.open()) != null) {
                        Emulator.getRobot();
                        String s;
                        if ((s = KeyRecords.method701(open3)) == null || !new File(s).exists()) {
                            ((Dialog)fileDialog3).setText(emulator.UILocale.get("LINK_JAR_FILE", "Specify a jar file"));
                            fileDialog3.setFileName("");
                            fileDialog3.setFilterExtensions(new String[] { "*.jar", "*.*" });
                            if ((s = fileDialog3.open()) == null) {
                                break Label_1321;
                            }
                        }
                        Settings.recordedKeysFile = open3;
                        Emulator.loadGame(s, Settings.g2d, 1, false);
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
                this.screenImg.dispose();
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
            this.updatePauseState();
        }
        else if (parent.equals(this.menu2dEngine)) {
            if (menuItem.equals(this.awt2dMenuItem)) {
                if (this.pauseState != 0 && Settings.g2d != 1) {
                    Emulator.loadGame(null, 1, 1, false);
                    return;
                }
                Settings.g2d = 1;
                this.swt2dMenuItem.setSelection(false);
                this.awt2dMenuItem.setSelection(true);
            }
            else if (menuItem.equals(this.swt2dMenuItem)) {
                if (this.pauseState != 0 && Settings.g2d != 0) {
                    Emulator.loadGame(null, 0, 1, false);
                    return;
                }
                Settings.g2d = 0;
                this.awt2dMenuItem.setSelection(false);
                this.swt2dMenuItem.setSelection(true);
            }
        }
        else /*if (parent.equals(this.menu3dEngine)) {
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
        else */if (parent.equals(this.menuView)) {
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
                setWindowOnTop(getHandle(shell), Settings.alwaysOnTop);
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
                    this.rotate(this.getHeight(), this.getWidth());
                    return;
                }

                if (menuItem.equals(this.rotate90MenuItem))
                {
                  rotate90degrees(false);
                  return;
                }
                if (menuItem.equals(this.forecPaintMenuItem)) {
                    if (Settings.g2d == 0) {
                        if (Settings.xrayView) {
                            this.xrayScreenImageSwt.cloneImage(this.screenCopySwt);
                        }
                        else {
                            this.backBufferImageSwt.cloneImage(this.screenCopySwt);
                        }
                    }
                    else if (Settings.g2d == 1) {
                        (Settings.xrayView ? this.xrayScreenImageAwt : this.backBufferImageAwt).cloneImage(this.screenCopyAwt);
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
                else/* if (menuItem.equals(this.aMenuItem962)) {
                    if (((EmulatorImpl)Emulator.getEmulator()).method827().method494()) {
                        ((EmulatorImpl)Emulator.getEmulator()).method827().method507();
                        return;
                    }
                    ((EmulatorImpl)Emulator.getEmulator()).method827().method493();
                }
                else*/ {
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
    
    private static void setWindowOnTop(final long handle, final boolean b) {
    	// XXX: SWT VERSION
    	try {
    		OS.SetWindowPos((int) handle, b ? -1 : -2, 0, 0, 0, 0, 19);
    	} catch(Throwable e) {
    		
    	}
    }
    
    private void updatePauseState() {
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
        //this.aMenuItem962.setEnabled(this.pauseState != 0);
        this.startRecordAviMenuItem.setEnabled(this.pauseState != 0 && EmulatorScreen.aviWriter == null);
        this.stopRecordAviMenuItem.setEnabled(this.pauseState != 0 && EmulatorScreen.aviWriter != null);
        this.connectNetworkMenuItem.setEnabled(this.pauseState != 0 && !Emulator.getNetMonitor().b());
        this.disconnectNetworkMenuItem.setEnabled(this.pauseState != 0 && Emulator.getNetMonitor().b());
        this.channelUpMenuItem.setEnabled(this.pauseState != 0 && Emulator.getNetMonitor().b());
        this.channelDownMenuItem.setEnabled(this.pauseState != 0 && Emulator.getNetMonitor().b());
        this.h();
    }
    
    public final void widgetDefaultSelected(final SelectionEvent selectionEvent) {
    }
    
    private void updateInfos(final int n, final int n2) {
        float[] arrayOfFloat;
        (arrayOfFloat = new float[4])[0] = n;
        arrayOfFloat[1] = n2;
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.transform(arrayOfFloat);
        final int n3 = (int)(arrayOfFloat[0] / this.zoom);
        final int n4 = (int)(arrayOfFloat[1] / this.zoom);
        if (n3 < 0 || n4 < 0 || n3 > this.getWidth() - 1 || n4 > this.getHeight() - 1) {
            return;
        }
        final int rgb;
        final int n5 = (rgb = this.getScreenImg().getRGB(n3, n4)) >> 16 & 0xFF;
        final int n6 = rgb >> 8 & 0xFF;
        final int n7 = rgb & 0xFF;
        this.aString1008 = emulator.UILocale.get("INFO_FRAME_POS", "Pos") + "(" + n3 + "," + n4 + ")\n" + emulator.UILocale.get("INFO_FRAME_COLOR", "Color") + "(";
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
        arrayOfFloat[0] = mouseXPress;
        arrayOfFloat[1] = mouseYPress;
        arrayOfFloat[2] = mouseXRelease;
        arrayOfFloat[3] = mouseYRelease;
        this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.transform(arrayOfFloat);
        this.aString1008 = this.aString1008 + ")\n" + 
        emulator.UILocale.get("INFO_FRAME_RECT", "R")

        + "(" + (int)(arrayOfFloat[0] / zoom) + "," + 
        (int)(arrayOfFloat[1] / zoom) + "," + (int)((arrayOfFloat[2] - arrayOfFloat[0]) / zoom) + 
        "," + (int)((arrayOfFloat[3] - arrayOfFloat[1]) / zoom) + ")";
        /*
        "(" + (int)(this.mouseXPress / this.zoom) + "," + 
        (int)(this.mouseYPress / this.zoom) + "," + (int)((this.mouseXRelease - this.mouseXPress) / this.zoom) +
        "," + (int)((this.mouseYRelease - this.mouseYPress) / this.zoom) + ")";
        */
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
        ((Widget)this.canvas).addListener(SWT.MouseVerticalWheel, (Listener)new Class32(this));
        this.keysState = new boolean[256];
        this.method589();
        this.caret = new CaretImpl(this.canvas);
        shell.layout();
    }
    
    public final void paintControl(final PaintEvent paintEvent) {
        final GC gc;
        (gc = paintEvent.gc).setInterpolation(this.anInt1020);
        gc.setTransform(this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform);
        try {
            if (this.screenImg == null || this.screenImg.isDisposed()) {
                if (this.pauseState == 0) {
                    gc.setBackground(EmulatorScreen.display.getSystemColor(22));
                    gc.fillRectangle(0, 0, this.anInt996, this.anInt1003);
                    gc.setForeground(EmulatorScreen.display.getSystemColor(21));
                    gc.setFont(f);
                    gc.drawText(Emulator.getInfoString(), this.anInt996 >> 3, this.anInt1003 >> 3, true);
                }
                else if (Settings.g2d == 0) {
                    this.screenCopySwt.method13(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.anInt996, this.anInt1003);
                }
                else if (Settings.g2d == 1) {
                    this.screenCopyAwt.method13(gc, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.anInt996, this.anInt1003);
                }
            }
            else {
                gc.drawImage(this.screenImg, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.anInt996, this.anInt1003);
            }
        }
        catch (Exception ex) {}
        gc.setAdvanced(false);
        this.method565(gc);
    }
    
    private void method565(final GC gc) {
        if (this.infosEnabled && (this.mouseXPress != this.mouseXRelease || this.mouseYPress != this.mouseYRelease)) {
        	try {
	            OS_SetROP2(gc, 7);
	            gc.setForeground(EmulatorScreen.display.getSystemColor(1));
	            gc.drawRectangle(this.mouseXPress, this.mouseYPress, this.mouseXRelease - this.mouseXPress, this.mouseYRelease - this.mouseYPress);
	            OS_SetROP2(gc, 13);
        	} catch (Throwable e) {
        		
        	}
        }
    }
    
    private static void OS_SetROP2(GC gc, int j) {
    	try {
    		long i = getHandle(gc);
    		Class<?> os = OS.class;
    		Method m = null;
    		try {
    			m = os.getMethod("SetROP2", int.class, int.class);
    			m.invoke(null, (int)i, j);
    		} catch (Exception e) {
    			m = os.getMethod("SetROP2", long.class, int.class);
    			m.invoke(null, i, j);
    		}
    	} catch (Exception e) {
    		throw new Error(e);
    	}
	}
    
    private static long getHandle(GC c) {
    	try {
    		Class<?> cl = c.getClass();
    		Field f = cl.getField("handle");
    		return f.getLong(c);
    	} catch (Exception e) {
    		throw new Error(e);
    	}
	}
    
    public final void run() {
        if (this.pauseState != 1) {
            return;
        }
        if(Settings.pollKeyboardOnRepaint) pollKeyboard(canvas);
        if (Settings.g2d == 0) {
            this.screenImageSwt.cloneImage(this.screenCopySwt);
        }
        else if (Settings.g2d == 1) {
            this.screenImageAwt.cloneImage(this.screenCopyAwt);
        }
        if (EmulatorScreen.aviWriter != null) {
            EmulatorScreen.aviWriter.method843(this.screenCopyAwt.getData());
        }
        ((Control)this.canvas).redraw();
        if (!EmulatorScreen.aBoolean967 && !EmulatorScreen.aBoolean992) {
            this.h();
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
        if (keyEvent.keyCode == 16777261) {
            this.zoomOut();
            return;
        }
        if (keyEvent.keyCode == 16777259) {
            this.zoomIn();
            return;
        }
        this.caret.keyPressed(keyEvent);
    	if(!Settings.canvasKeyboard && win) {
    		return;
    	}
        int n = keyEvent.keyCode & 0xFEFFFFFF;
        if(keyEvent.character >= 33 && keyEvent.character <= 90 && Settings.canvasKeyboard && !(n >= 48 && n <= 57))
        	n = keyEvent.character;
        handleKeyPress(n);
    }
    
    public final void keyReleased(final KeyEvent keyEvent) {
    	if(!Settings.canvasKeyboard && win) {
    		return;
    	}
        int n = keyEvent.keyCode & 0xFEFFFFFF;
        if(keyEvent.character >= 33 && keyEvent.character <= 90 && Settings.canvasKeyboard && !(n >= 48 && n <= 57))
        	n = keyEvent.character;
    	handleKeyRelease(n);
    }
    
    
    
	protected final void handleKeyPress(int n) {
		if (this.pauseState == 0 || Settings.playingRecordedKeys) {
			return;
		}
		/*
		if (Settings.fpsMode) {
			int g = Keyboard.fpsKey(n);
			if (g != 0) {
				mp(n);
				return;
			}
		}*/
		if ((n < 0 || n >= this.keysState.length) && !Settings.canvasKeyboard) {
			return;
		}
		final String r;
		if ((r = Keyboard.replaceKey(n)) == null) {
			return;
		}
		if (pressedKeys.contains(n)) {
			if (Settings.enableKeyRepeat) {
				Emulator.getEventQueue().keyRepeat(Integer.parseInt(r));
			}
			return;
		}
		synchronized(pressedKeys) {
			if (!pressedKeys.contains(n)) {
				pressedKeys.add(n);
				keysPressed++;
			}
		}
		if (Settings.enableKeyCache) {
			Keyboard.keyCacheStack.push('0' + r);
			return;
		}
		if (Settings.recordKeys && !Settings.playingRecordedKeys) {
			Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '0' + r);
		}
		Emulator.getEventQueue().keyPress(Integer.parseInt(r));
	}

    protected final void handleKeyRelease(int n) {
        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
            return;
        }/*
        if(Settings.fpsMode) {
        	int g = Keyboard.fpsKey(n);
        	if(g != 0) {
        		mr(n);
        		return;
        	}
        }*/
        if ((n < 0 || n >= this.keysState.length) && !Settings.canvasKeyboard) {
            return;
        }
        final String r;
        if ((r = Keyboard.replaceKey(n)) == null) {
            return;
        }
    	Integer in = new Integer(n);
		synchronized(pressedKeys) {
	    	if(!pressedKeys.contains(in)) {
	    		return;
	    	}
	    	pressedKeys.remove(in);
	    	keysPressed--;
		}
    
	    if (Settings.enableKeyCache) {
	        Keyboard.keyCacheStack.push('1' + r);
	        return;
	    }
	    if (Settings.recordKeys && !Settings.playingRecordedKeys) {
	        Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '1' + r);
	    }
	    Emulator.getEventQueue().keyRelease(Integer.parseInt(r));
	}
    
    

    
    private int key(int n) {
    	//System.out.println("key: " + n);
    	//XXX
    	if(n <= 7) return -1;
    	if(n >= 14 && n <= 31) return -1;
    	if(n >= 91 && n <= 95) return -1;
    	if(n >= 41 && n <= 47) return -1;
    	if(n >= 124 && n <= 186) return -1;
    	if(n > 190) return -1;
    	if(n >= 'A' && n <= 'Z') n -= 'A' - 'a';
    	else if(n >= 96 && n <= 105) n = n-96+'0';
    	else if(n >= 112 && n <= 123) n = n-112+10;
    	else switch(n) {
    	case 33:
    		n = 5;
    		break;
    	case 34:
    		n = 6;
    		break;
    	case 35:
    		n = 8;
    		break;
    	case 36:
    		n = 7;
    		break;
    	case 37:
    		n = 3;
    		break;
    	case 38:
    		n = 1;
    		break;
    	case 39:
    		n = 4;
    		break;
    	case 40:
    		n = 2;
    		break;
    	case 106:
    		n = '*';
    		break;
    	case 107:
    		n = '+';
    		break;
    	case 109:
    		n = '-';
    		break;
    	case 110:
    		n = '.';
    		break;
    	case 111:
    		n = '/';
    		break;
    	case 187:
    		n = '=';
    		break;
    	case 188:
    		n = ',';
    		break;
    	case 189:
    		n = '-';
    		break;
    	case 190:
    		n = '.';
    		break;
    	}
		return n;
	}
    
    private void onKeyDown(int n) {
    	if(Settings.canvasKeyboard) return;
    	n = key(n);
    	if(n <= 0) return;
        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
            return;
        }
		final String r;
		if ((r = Keyboard.replaceKey(n)) == null) {
			return;
		}
		if (Settings.enableKeyCache) {
			Keyboard.keyCacheStack.push('0' + r);
			return;
		}
		if (Settings.recordKeys && !Settings.playingRecordedKeys) {
			Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '0' + r);
		}
		Emulator.getEventQueue().keyPress(Integer.parseInt(r));
    }

	private void onKeyHeld(int n) {
    	if(Settings.canvasKeyboard) return;
    	n = key(n);
    	if(n <= 0) return;
        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
            return;
        }
		if (!Settings.enableKeyRepeat) {
			return;
		}
		if (Settings.enableKeyCache) {
			return;
		}
		String r = Keyboard.replaceKey(n);
		if (r == null) {
			return;
		}
		Emulator.getEventQueue().keyRepeat(Integer.parseInt(r));
    }
    
    private void onKeyUp(int n) {
    	if(Settings.canvasKeyboard) return;
    	n = key(n);
    	if(n <= 0) return;
        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
            return;
        }
        final String r;
        if ((r = Keyboard.replaceKey(n)) == null) {
            return;
        }
    	if (Settings.enableKeyCache) {
	        Keyboard.keyCacheStack.push('1' + r);
	        return;
	    }
	    if (Settings.recordKeys && !Settings.playingRecordedKeys) {
	        Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '1' + r);
	    }
	    Emulator.getEventQueue().keyRelease(Integer.parseInt(r));
    }
    
    
    
    
    
    

    protected final void handleKeyPress0(int n) {
	        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
	            return;
	        }
	        if(Settings.fpsMode) {
	        	int g = Keyboard.fpsKey(n);
	        	if(g != 0) {
	        		mp(n);
	        		return;
	        	}
	        }
	        if ((n < 0 || n >= this.keysState.length) && !Settings.canvasKeyboard) {
	            return;
	        }
	        final String r;
	        if ((r = Keyboard.replaceKey(n)) == null) {
	            return;
	        }
	       /* 
		    if(!Settings.canvasKeyboard) {
		        if (this.keysState[n]) {
		            if (Settings.enableKeyRepeat) {
		                Emulator.getEventQueue().keyRepeat(Integer.parseInt(r));
		            }
		            return;
		        }
		        keysPressed++;
		        this.keysState[n] = true;
	        } else {*/
		        if (pressedKeys.contains(n)) {
		            if (Settings.enableKeyRepeat) {
		                Emulator.getEventQueue().keyRepeat(Integer.parseInt(r));
		            }
		            return;
		        }
		        keysPressed++;
		        pressedKeys.add(n);
	       // }
	        if (Settings.enableKeyCache) {
	            Keyboard.keyCacheStack.push('0' + r);
	            return;
	        }
	        //System.out.println("method568 " + n);
	        if (Settings.recordKeys && !Settings.playingRecordedKeys) {
	            Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '0' + r);
	        }
	        Emulator.getEventQueue().keyPress(Integer.parseInt(r));
    }
        
    protected final void handleKeyRelease0(int n) {
        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
            return;
        }
        if(Settings.fpsMode) {
        	int g = Keyboard.fpsKey(n);
        	if(g != 0) {
        		mr(n);
        		return;
        	}
        }

        if ((n < 0 || n >= this.keysState.length) && !Settings.canvasKeyboard) {
            return;
        }
        final String r;
        if ((r = Keyboard.replaceKey(n)) == null) {
            return;
        }
        // XXX: костыль с релизом клавиш
        /*
        if(!Settings.canvasKeyboard) {
	        if (!this.keysState[n]) {
	            return;
	        }
	        keysPressed--;
	        this.keysState[n] = false;
	    }
	    */
	   /* if(!Settings.canvasKeyboard) {
	        if (!this.keysState[n]) {
	            return;
	        }
	    	if(keysPressed > 1) {
	    		for(int i = 0; i < keysState.length; i++) {
	    			if(keysState[i]) {
	    		        final String ir;
	    		        if ((ir = Keyboard.replaceKey(i)) == null) {
	    		            return;
	    		        }
	    		        if (Settings.enableKeyCache) Keyboard.keyCacheStack.push('1' + ir);
	    		        else Emulator.getEventQueue().keyRelease(Integer.parseInt(ir));
	    			}
	    			keysState[i] = false;
	    		}
		        keysPressed = 0;
	    		return;
	    	}
	        keysPressed = 0;
	       keysState[n] = false;
	    } else {*/
	    	Integer in = new Integer(n);
	    	if(!pressedKeys.contains(in)) {
	    		//return;
	    	}
	    	/*
	    	if(keysPressed > 1 && false) {
	    		Vector<Integer> v = (Vector<Integer>) pressedKeys.clone();
	    		for(Integer i: v) {
	    			if((int) i == n) {
	    				continue;
	    			}
	    			// ignore arrows and numbers
	    			if((i >= 1 && i <= 4) || (i >= 48 && i <= 57)) {
	    				continue;
	    			}
    		        pressedKeys.remove((Object) i);
    		        keysPressed--;
    		        final String ir;
    		        if ((ir = Keyboard.replaceKey((int)i)) == null) {
    		            return;
    		        }
    		        if (Settings.enableKeyCache) Keyboard.keyCacheStack.push('1' + ir);
    		        else Emulator.getEventQueue().keyRelease(Integer.parseInt(ir));
	    		}
	    	}
	    	*/
	        pressedKeys.remove(in);
	    	keysPressed--;
	   //}
	    
        if (Settings.enableKeyCache) {
            Keyboard.keyCacheStack.push('1' + r);
            return;
        }
        if (Settings.recordKeys && !Settings.playingRecordedKeys) {
            Emulator.getRobot().print(EmulatorScreen.aLong982 + ":" + '1' + r);
        }
        Emulator.getEventQueue().keyRelease(Integer.parseInt(r));
    }
    
    public final void mouseDoubleClick(final MouseEvent mouseEvent) {
		if (Settings.playingRecordedKeys) {
			return;
		}
        if (this.pauseState == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
            return;
        }
        if (mouseEvent.button != 1 || Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
            return;
        }
        try {
            Emulator.getScreen().invokeKeyPressed(Keyboard.getArrowKeyFromDevice(8));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
		if (Settings.playingRecordedKeys) {
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
            	else if(Settings.fpsGame == 3)
             		mp(Keyboard.soft1());
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

            float[] zoom;
            (zoom = new float[2])[0] = mouseEvent.x;
            zoom[1] = mouseEvent.y;
            this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.transform(zoom);
            final int x = (int)(zoom[0] / this.zoom);
            final int y = (int)(zoom[1] / this.zoom);
        	lastMouseX = x;
        	lastMouseY = y;
            Emulator.getEventQueue().mouseDown(x, y);
            if (Emulator.getCurrentDisplay().getCurrent() == Emulator.getScreen()) {
                this.caret.mouseDown(x, y);
            }
        }
    }
    
    public final void mouseUp(final MouseEvent mouseEvent) {
        this.mouseDownInfos = false;
        if (this.pauseState == 0) {
            return;
        }

		if (Settings.playingRecordedKeys) {
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
            	else if(Settings.fpsGame == 3)
             		mr(Keyboard.soft1());
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
            float[] zoom;
            (zoom = new float[2])[0] = mouseEvent.x;
            zoom[1] = mouseEvent.y;
            this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.transform(zoom);
            final int x = (int)(zoom[0] / this.zoom);
            final int y = (int)(zoom[1] / this.zoom);
        	lastMouseX = x;
        	lastMouseY = y;
            Emulator.getEventQueue().mouseUp(x, y);
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
            //return;
        }
        if (this.pauseState == 0) {
            return;
        }
        final int xoff = 1;
        final int yoff = 1;
        final boolean d = true;
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
        	Point center = canvas.toDisplay(canvas.getSize().x / 2, canvas.getSize().y / 2 - 1);
        	int dx = mouseEvent.x - canvas.getSize().x / 2;
        	int dy = mouseEvent.y - (canvas.getSize().y / 2 - 1);
        	if(canvas.toControl(display.getCursorLocation()).x == canvas.getSize().x / 2 && d) {
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}

        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        		if(fpsWasUp) {
                	mr('3');
                	fpsWasUp = false;
        		}
        		if(fpsWasBottom) {
                	mr('1');
                	fpsWasBottom = false;
        		}
        		return;
        	}
        	display.setCursorLocation(center);
        	if(dx > xoff) {
        		// right
        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        		if(!fpsWasRight) mp(-4);
        		else mrp(-4);
        		fpsWasRight = true;
        	} else if(dx < -xoff) {
        		// left
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}
        		if(!fpsWasLeft) mp(-3);
        		else mrp(-3);
        		fpsWasLeft = true;
        	} else if(dx == 0) {
        		fpsWasntHor = true;
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}

        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        	} else if(Math.abs(dx) <= xoff && !fpsWasntHor) {
        		if(fpsWasRight) {
                	mr(-4);
                	fpsWasRight = false;
        		}

        		if(fpsWasLeft) {
                	mr(-3);
                	fpsWasLeft = false;
        		}
        	}
        	if(dx != 0) fpsWasntHor = false;
        	if(Settings.fpsGame == 3) {
        		if(dy > yoff) {
        			// bottom
            		if(fpsWasUp) {
                    	mr('3');
                    	fpsWasUp = false;
            		}
            		if(!fpsWasBottom) mp('1');
            		else mrp('1');
            		fpsWasBottom = true;
        		} else if(dy < -yoff) {
        			// up
            		if(fpsWasBottom) {
                    	mr('1');
                    	fpsWasBottom = false;
            		}
            		if(!fpsWasUp) mp('3');
            		else mrp('3');
            		fpsWasUp = true;
        		} else if(dy == 0) {
            		fpsWasntVer = true;
            		if(fpsWasBottom) {
                    	mr('1');
                    	fpsWasBottom = false;
            		}

            		if(fpsWasUp) {
                    	mr('3');
                    	fpsWasUp = false;
            		}
        		} else if(Math.abs(dy) <= yoff && !fpsWasntVer) {
            		if(fpsWasBottom) {
                    	mr('1');
                    	fpsWasBottom = false;
            		}

            		if(fpsWasUp) {
                    	mr('3');
                    	fpsWasUp = false;
            		}
            	} 
            	if(dy != 0) fpsWasntVer = false;
        	}
        	lastMouseMoveX = dx;
        	//if(canvas.getSize().x / 2 != mouseEvent.x) ignoreNextFps = true;
        	return;
        } else if(mset) {
			Cursor cursor = new Cursor(display, SWT.CURSOR_ARROW);
			this.canvas.getShell().setCursor(cursor);
			mset = false;
        }
        if ((mouseEvent.stateMask & 0x80000) != 0x0 && Emulator.getCurrentDisplay().getCurrent() != null) {
            float[] zoom;
            (zoom = new float[2])[0] = mouseEvent.x;
            zoom[1] = mouseEvent.y;
            this.jdField_b_of_type_OrgEclipseSwtGraphicsTransform.transform(zoom);
            int x = (int)(zoom[0] / this.zoom);
            int y = (int)(zoom[1] / this.zoom);
            // Drag filter
        	//if(mouseEvent.time - lastDragTime < 5 && Math.abs(x-lastMouseX) < 4 && Math.abs(y-lastMouseY) < 4) {
        	//	return;
        	//}
        	lastMouseX = x;
        	lastMouseY = y;
        	lastDragTime = mouseEvent.time;
            Emulator.getEventQueue().mouseDrag(x, y);
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
        this.getWindowPos();
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
        this.vibra = aLong1013;
        if (this.vibra == 0L) {
            this.stopVibra();
            return;
        }
        if (this.vibraThread == null) {
            this.vibraThread = new Vibrate(this);
            new Thread(this.vibraThread, "KEmulator vibrate-" + (++threadCount)).start();
            return;
        }
        this.vibraStart = System.currentTimeMillis();
    }
    
    public final void stopVibra() {
        if (this.vibraThread != null) {
            this.vibraThread.aBoolean1194 = true;
        }
    }
    
    private void method589() {
        final DropTarget dropTarget;
        (dropTarget = new DropTarget((Control)this.canvas, 19)).setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener((DropTargetListener)new Class29(this));
    }
    
    public final ICaret getCaret() {
        return this.caret;
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
        return class93.keysState;
    }
    
    static int method562(final EmulatorScreen class93, final int anInt1020) {
        return class93.anInt1020 = anInt1020;
    }
    
    static long method559(final EmulatorScreen class93, final long aLong1017) {
        return class93.vibraStart = aLong1017;
    }
    
    static long method567(final EmulatorScreen class93) {
        return class93.vibraStart;
    }
    
    static long method575(final EmulatorScreen class93) {
        return class93.vibra;
    }
    
    static Vibrate method555(final EmulatorScreen class93, final Vibrate aClass119_976) {
        return class93.vibraThread = aClass119_976;
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
            EmulatorScreen.method555(this.aClass93_1196, null);
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
        if (this.pauseState == 0 || Settings.playingRecordedKeys) {
            return;
        }
		int k = 0;
    	if(arg0.count < 0) { 
    		k = Integer.parseInt(Keyboard.replaceKey(2));
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
    		k = Integer.parseInt(Keyboard.replaceKey(1));
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
