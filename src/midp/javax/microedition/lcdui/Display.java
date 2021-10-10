package javax.microedition.lcdui;

import java.util.*;
import javax.microedition.midlet.*;
import emulator.*;
import emulator.ui.swt.EmulatorScreen;

public class Display
{
    public static final int LIST_ELEMENT = 1;
    public static final int CHOICE_GROUP_ELEMENT = 2;
    public static final int ALERT = 3;
    public static final int COLOR_BACKGROUND = 0;
    public static final int COLOR_FOREGROUND = 1;
    public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
    public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
    public static final int COLOR_BORDER = 4;
    public static final int COLOR_HIGHLIGHTED_BORDER = 5;
    static Displayable current;
    static Hashtable displays;
	private MIDlet midlet;
	private long from;
	protected boolean fadeComplete;
	private boolean pausd;
    
    public Display() {
        super();
    }
    
    public Display(MIDlet midlet) {
		this.midlet = midlet;
		resetTimer();
		new Thread() {
			public void run() {
				this.setPriority(2);
				while(Display.this.midlet != null) {
					timerTick();
					Thread.yield();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}.start();
	}

	public boolean isColor() {
        return true;
    }
    
    public int numColors() {
        return 65536;
    }
    
    public int numAlphaLevels() {
        return 256;
    }
    
    public Displayable getCurrent() {
        return Display.current;
    }
    
    public void setCurrent(Displayable d) {
        if (d == Display.current) {
            return;
        }
        if (Display.current != null) {
            Display.current.defocus();
        }
        if ((Display.current = d) != null) {
            if (d instanceof Canvas) {
                if (Settings.aBoolean1274) {
                    ((Canvas)d).setFullScreenMode(true);
                }
                Emulator.setCanvas((Canvas)d);
                Emulator.getEventQueue().queue(15);
                Emulator.getEventQueue().queue(1);
                Emulator.getEventQueue().waitRepainted2();
            }
            else if (d instanceof Screen) {
                Emulator.setScreen((Screen)d);
                Emulator.getEventQueue().queue(4);
                ((Screen)d).shown();
                if (d instanceof TextBox) {
                    ((TextBox)d).focusCaret();
                }
            }
            d.updateCommands();
        }
    }
    
    public static Display getDisplay(final MIDlet midlet) {
        if (Display.displays.get(midlet) == null) {
            Display.displays.put(midlet, new Display(midlet));
        }
        return (Display) Display.displays.get(midlet);
    }
    
    public void callSerially(final Runnable aRunnable1219) {
        EventQueue.aRunnable1219 = aRunnable1219;
        Emulator.getEventQueue().queue(2);
    }
    
    public boolean flashBacklight(final long n) {
    	Emulator.screenBrightness = 100;
    	resetTimer();
        return false;
    }

	public void fadeOut() {
    	if(fadeComplete) {
			Emulator.screenBrightness = 15;
			new Thread() {
				public void run() {
					while(true) {
						Emulator.screenBrightness = lerp(Emulator.screenBrightness, 100, 15, 100);
						p();
						if(Emulator.screenBrightness > 80)
							break;
						try {
							Thread.sleep(1000 / 24);
						} catch (InterruptedException e) {
							return;
						}
					}
					Emulator.screenBrightness = 100;
				}
			}.start();
    	} else {
			Emulator.screenBrightness = 100;
    	}
	}
    
    public void resetTimer() {
		from = System.currentTimeMillis(); 
    	fadeComplete = false;
    	if(pausd) {
			 Emulator.getMIDlet().invokeStartApp();
    		 if (Emulator.getCanvas() != null && Emulator.getCurrentDisplay().getCurrent() == Emulator.getCanvas()) {
    			// Emulator.getCanvas().invokeShowNotify();
    		 }
    	}
    	pausd = false;
	}
    
    public int getInactiveTime() {
		return (int) (System.currentTimeMillis() - from);
	}
    
	private static int lerp(final int start, final int target, final int mul, final int div) {
		return start + ((target - start) * mul / div);
	}
    
    private void timerTick() {
		int i = (int) (System.currentTimeMillis() - from);
		if(Emulator.inactivityTimer <= 0) {
			return;
		}
		if(i > Emulator.inactivityTimer && !fadeComplete) {
			Emulator.screenBrightness = 80;
		}
		if(i > Emulator.inactivityTimer + 5000 && !fadeComplete) {
			Emulator.screenBrightness = 80;
			new Thread() {
				public void run() {
					fadeComplete = true;
					while(Emulator.screenBrightness <= 99 && Emulator.screenBrightness > 15) {
						Emulator.screenBrightness = lerp(Emulator.screenBrightness, 1, 15, 100);
						p();
						try {
							Thread.sleep(1000 / 24);
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			}.start();
		}
		if(i > Emulator.inactivityTimer + 6000) {
			Emulator.screenBrightness = 1;
		}
		if(i > Emulator.inactivityTimer + 7500 && !pausd) {
			Emulator.getMIDlet().invokePauseApp();
	    	pausd = true;
            if (Emulator.getCanvas() == null) {
                return;
            }
            if (Emulator.getCurrentDisplay().getCurrent() != Emulator.getCanvas()) {
                return;
            }
            Emulator.getCanvas().invokeHideNotify();
		}
	}
    
    private void p() {
    	 if (Display.current != null) {
	            if (Display.current instanceof Canvas) {
	                if (Settings.aBoolean1274) {
	                    ((Canvas)Display.current).setFullScreenMode(true);
	                }
	                Emulator.getEventQueue().queue(15);
	                Emulator.getEventQueue().queue(1);
	                Emulator.getEventQueue().waitRepainted2();
	            }
	            else if (Display.current instanceof Screen) {
	                Emulator.getEventQueue().queue(4);
	            }
	        }
    }

	public boolean vibrate(final int n) {
		resetTimer();
    	if(n == 0)
    		Emulator.getEmulator().getScreen().stopVibra();
    	else
    		Emulator.getEmulator().getScreen().startVibra(n);
        return true;
    }
    
    public int getBestImageWidth(final int n) {
    	switch(n) {
    	case Display.LIST_ELEMENT:
    	case Display.CHOICE_GROUP_ELEMENT:
    	case Display.ALERT:
    		return 16;
    	}
        return 0;
    }
    
    public int getBestImageHeight(final int n) {
    	switch(n) {
    	case Display.LIST_ELEMENT:
    	case Display.CHOICE_GROUP_ELEMENT:
    	case Display.ALERT:
    		return 16;
    	}
        return 0;
    }
    
    public int getBorderStyle(final boolean b) {
        return 0;
    }
    
    public int getColor(final int n) {
        return 0;
    }
    
    public void dispose() {
        Display.displays.put(midlet, null);
    }
    
    public void setCurrent(final Alert alert, final Displayable ret) {
        if (alert == null || ret == null) {
            throw new NullPointerException();
        }
        if (ret instanceof Alert) {
            throw new IllegalArgumentException();
        }
        if (Display.current != null) {
            Display.current.defocus();
        }
        Display.current = alert;
        alert.lastDisplayed = ret;
        Emulator.setScreen(alert);
        Emulator.getEventQueue().queue(4);
        alert.updateCommands();
    }
    
    public void setCurrentItem(final Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.screen == null || item.screen instanceof Alert) {
            throw new IllegalArgumentException();
        }
        this.setCurrent(item.screen);
    }
    
    static {
        Display.displays = new Hashtable();
    }
}
