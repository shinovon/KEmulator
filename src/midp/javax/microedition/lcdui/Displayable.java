package javax.microedition.lcdui;

import java.util.*;

import javax.microedition.media.CapturePlayerImpl;

import com.nokia.mid.ui.DirectGraphicsInvoker;

import emulator.*;
import emulator.Keyboard;
import emulator.debug.*;
import emulator.lcdui.a;

public class Displayable
{
    String aString25;
    Vector aVector26;
    boolean aBoolean18;
    int anInt28;
    CommandListener aCommandListener19;
    Item anItem20;
    int w;
    int h;
    int[] anIntArray21;
    Ticker aTicker22;
    int anInt31;
    private static long aLong23;
    private static long aLong27;
    private static int anInt24;
    
    public Displayable() {
        super();
        this.anItem20 = null;
        this.aCommandListener19 = null;
        this.aVector26 = new Vector();
        this.w = Emulator.getEmulator().getScreen().getWidth();
        this.h = Emulator.getEmulator().getScreen().getHeight();
        this.anIntArray21 = new int[] { 0, Screen.anInt181, this.w - 4, this.h - Screen.anInt181 };
    }
    
    public int getWidth() {
        return this.w;
    }
    
    public int getHeight() {
        return this.h;
    }
    
    public String getTitle() {
        return this.aString25;
    }
    
    public boolean isShown() {
        return Display.current == this;
    }
    
    protected void defocus() {
        if (this.anItem20 != null) {
            this.anItem20.defocus();
            this.anItem20 = null;
        }
    }
    
    protected void setItemCommands(final Item anItem20) {
        this.anItem20 = anItem20;
        if (anItem20.itemCommands.size() > 0) {
            for (int i = 0; i < anItem20.itemCommands.size(); ++i) {
                this.addCommand((Command)anItem20.itemCommands.get(i));
            }
        }
    }
    
    protected void removeItemCommands(final Item item) {
        if (item == null) {
            return;
        }
        if (item.itemCommands.size() > 0) {
            for (int i = 0; i < item.itemCommands.size(); ++i) {
                this.removeCommand((Command)item.itemCommands.get(i));
            }
        }
        this.anItem20 = null;
    }
    
    protected void updateCommands() {
        final String commandLeft = (this.aVector26.size() > 0) ? this.getLeftSoftCommand().getLongLabel() : "";
        final String commandRight = (this.aVector26.size() > 2) ? "Menu" : ((this.aVector26.size() < 2) ? "" : this.getRightSoftCommand().getLongLabel());
        Emulator.getEmulator().getScreen().setCommandLeft(commandLeft);
        Emulator.getEmulator().getScreen().setCommandRight(commandRight);
    }
    
    protected boolean isCommandsEmpty() {
        return this.aVector26.isEmpty();
    }
    
    public void addCommand(final Command command) {
        if (command == null || this.aVector26.contains(command)) {
            return;
        }
        int i;
        for (i = 0; i < this.aVector26.size(); ++i) {
            if (command.getCommandType() == 7) {
                break;
            }
            final Command command2;
            if ((command2 = (Command) this.aVector26.get(i)).getCommandType() != 7) {
                if (command.getCommandType() > command2.getCommandType()) {
                    break;
                }
                if (command.getCommandType() == command2.getCommandType() && command.getPriority() < command2.getPriority()) {
                    break;
                }
            }
        }
        this.aVector26.add(i, command);
        if (Emulator.getCurrentDisplay().getCurrent() == this) {
            this.updateCommands();
        }
    }
    
    public void removeCommand(final Command command) {
        if (this.aVector26.contains(command)) {
            this.aVector26.remove(command);
            if (Emulator.getCurrentDisplay().getCurrent() == this) {
                this.updateCommands();
            }
        }
    }
    
    protected Command getLeftSoftCommand() {
        if (this.aVector26.size() > 0) {
            return (Command) this.aVector26.get(0);
        }
        return null;
    }
    
    protected Command getRightSoftCommand() {
        if (this.aVector26.size() > 1) {
            return (Command) this.aVector26.get(1);
        }
        return null;
    }
    
    public boolean handleSoftKeyAction(final int n, final boolean b) {
        if (this.aCommandListener19 == null && this instanceof Canvas) {
            return false;
        }
        if (Keyboard.method597(n)) {
            final Command leftSoftCommand = this.getLeftSoftCommand();
            if (b && leftSoftCommand != null) {
                Emulator.getEmulator().getLogStream().println("Left command: " + leftSoftCommand);
                if (this instanceof Alert && leftSoftCommand == Alert.DISMISS_COMMAND) {
                    ((Alert)this).close();
                }
                else if (this.anItem20 != null && this.anItem20.itemCommands.contains(leftSoftCommand)) {
                    this.anItem20.itemCommandListener.commandAction(leftSoftCommand, this.anItem20);
                }
                else if (this.aCommandListener19 != null) {
                    this.aCommandListener19.commandAction(leftSoftCommand, this);
                }
            }
            return true;
        }
        if (Keyboard.method603(n)) {
            if (this.aVector26.size() > 2) {
                if (b && this.aBoolean18) {
                    this.aBoolean18 = false;
                    this.refreshSoftMenu();
                }
                else if (b) {
                    this.aBoolean18 = true;
                    this.anInt28 = 0;
                    this.refreshSoftMenu();
                }
            }
            else {
                final Command rightSoftCommand = this.getRightSoftCommand();
                if (b && rightSoftCommand != null) {
                    Emulator.getEmulator().getLogStream().println("Right command: " + rightSoftCommand);
                    if (this instanceof Alert && rightSoftCommand == Alert.DISMISS_COMMAND) {
                        ((Alert)this).close();
                    }
                    else if (this.anItem20 != null && this.anItem20.itemCommands.contains(rightSoftCommand)) {
                        this.anItem20.itemCommandListener.commandAction(rightSoftCommand, this.anItem20);
                    }
                    else if (this.aCommandListener19 != null) {
                        this.aCommandListener19.commandAction(rightSoftCommand, this);
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    public void setCommandListener(final CommandListener aCommandListener19) {
        this.aCommandListener19 = aCommandListener19;
    }
    
    public void setTitle(final String aString25) {
        this.aString25 = aString25;
    }
    
    protected void sizeChanged(final int n, final int n2) {
    }
    
    public Ticker getTicker() {
        return this.aTicker22;
    }
    
    public void setTicker(final Ticker aTicker22) {
        this.aTicker22 = aTicker22;
        final int[] anIntArray21 = this.anIntArray21;
        final int n = 3;
        int n2;
        int anInt181;
        if (this.aTicker22 == null) {
            n2 = this.h;
            anInt181 = Screen.anInt181;
        }
        else {
            n2 = this.h;
            anInt181 = Screen.anInt181 * 2;
        }
        anIntArray21[n] = n2 - anInt181;
        this.anInt31 = this.w;
    }
    
    protected void paintTicker(final Graphics graphics) {
        if (this.aTicker22 == null) {
            return;
        }
        a.method181(graphics, 0, Screen.anInt181 + this.anIntArray21[3] - 1, this.w, Screen.anInt181);
        graphics.setFont(Screen.aFont173);
        graphics.drawString(this.aTicker22.getString(), this.anInt31, Screen.anInt181 + this.anIntArray21[3] - 1 + 2, 0);
        this.anInt31 -= 5;
        if (this.anInt31 < -Screen.aFont173.stringWidth(this.aTicker22.getString())) {
            this.anInt31 = this.w;
        }
    }
    
    void refreshSoftMenu() {
        EventQueue j;
        int n;
        if (this instanceof Canvas) {
            j = Emulator.getEventQueue();
            n = 1;
        }
        else {
            if (!(this instanceof Screen)) {
                return;
            }
            j = Emulator.getEventQueue();
            n = 4;
        }
        j.queue(n);
    }
    
    protected void paintSoftMenu(final Graphics graphics) {
    	CapturePlayerImpl.draw(graphics, this);
        final int translateX = graphics.getTranslateX();
        final int translateY = graphics.getTranslateY();
    	if(Emulator.screenBrightness < 100) {
	        graphics.translate(-translateX, -translateY);
	        int alpha = (int)(((double)(100-Emulator.screenBrightness)/100d) * 255d);
	       // System.out.println(Integer.toHexString(alpha << 24));
	        DirectGraphicsInvoker.getDirectGraphics(graphics).setARGBColor(alpha << 24);
	        graphics.fillRect(0, 0, getWidth(), getHeight());
	        graphics.translate(translateX, translateY);
    	}
        if (!this.aBoolean18) {
            return;
        }
        final int clipX = graphics.getClipX();
        final int clipY = graphics.getClipY();
        final int clipWidth = graphics.getClipWidth();
        final int clipHeight = graphics.getClipHeight();
        graphics.translate(-translateX, -translateY);
        graphics.setClip(0, 0, this.w, this.h);
        final int n = this.w >> 1;
        final int anInt181 = Screen.anInt181;
        final int n3;
        final int n2 = (n3 = this.aVector26.size() - 1) * anInt181;
        final int n4 = n - 1;
        int n5 = this.h - n2 - 1;
        a.method177(graphics, n4, n5, n, n2, true);
        for (int i = 0; i < n3; ++i, n5 += anInt181) {
            graphics.setColor(-16777216);
            if (i == this.anInt28) {
                a.method178(graphics, n4, n5, n, anInt181);
            }
            graphics.drawString(i + 1 + "." + ((Command)this.aVector26.get(i + 1)).getLongLabel(), n4 + 4, n5 + 2, 0);
        }
        graphics.translate(translateX, translateY);
        graphics.setClip(clipX, clipY, clipWidth, clipHeight);
    }
    /*
    protected static void fpsLimiter() {
        if (k.f == 1 && k.d <= 50) {
            final long n = System.currentTimeMillis() - Displayable.aLong23;
            final long n2 = 1000 / k.d;
            try {
                Thread.sleep(n2 - n);
            }
            catch (Exception ex) {}
        }
        Displayable.aLong23 = System.currentTimeMillis();
        ++Displayable.anInt24;
        if (Displayable.aLong23 - Displayable.aLong27 > 2000L) {
            Profiler.FPS = (int)((Displayable.anInt24 * 1000 + 500) / (Displayable.aLong23 - Displayable.aLong27));
            Displayable.aLong27 = Displayable.aLong23;
            Displayable.anInt24 = 0;
        }
    }
    */

    protected static void fpsLimiter() {
       if(Settings.f == 1 && Settings.frameRate <= 50) {
          long var0 = System.currentTimeMillis() - aLong23;
          long var2 = (long)(1000 / Settings.frameRate);

          try {
             Thread.sleep(var2 - var0);
          } catch (Exception var4) {
             ;
          }
       }

       aLong23 = System.currentTimeMillis();
       ++anInt24;
       if(aLong23 - aLong27 > 2000L) {
          Profiler.FPS = (int)((long)(anInt24 * 1000 + 500) / (aLong23 - aLong27));
          aLong27 = aLong23;
          anInt24 = 0;
       }

    }

    protected static void checkForSteps() {
        if (Settings.e >= 0) {
            if (Settings.e == 0) {
                final long currentTimeMillis = System.currentTimeMillis();
                try {
                    while (Settings.e == 0) {
                        Thread.sleep(250L);
                    }
                }
                catch (Exception ex) {
                	  ex.printStackTrace();}
                Settings.aLong1235 += System.currentTimeMillis() - currentTimeMillis;
            }
            --Settings.e;
        }
    }
    
    protected static void resetXRayGraphics() {
        Graphics.resetXRayCache();
    }
    
    static {
        Displayable.aLong23 = System.currentTimeMillis();
        Displayable.aLong27 = Displayable.aLong23;
        Displayable.anInt24 = 0;
    }
}
