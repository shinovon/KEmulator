package javax.microedition.lcdui;

import emulator.*;
import emulator.graphics2D.IImage;
import emulator.lcdui.BoundsUtils;

public abstract class Canvas extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;
    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;
    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;
    protected int m_keyStates;
    private Graphics graphics;
    private int vKeyStates;

    protected Canvas() {
        super();
        this.setFullScreenMode(false);
    }

    public void invokePaint(IImage buffer, IImage xray) {
        if (!Settings.xrayView) xray = null;
        if (graphics == null) {
            graphics = new Graphics(buffer, xray);
        }
        graphics.reset(buffer, xray);
        this.paint(graphics);
        graphics.reset(buffer, xray); // paintTicker fix
        this.paintTicker(graphics);
        this.paintSoftMenu(graphics);
    }

    public void invokePaint(IImage buffer, IImage xray, int x, int y, int w, int h) {
        if (!Settings.xrayView) xray = null;
        if (graphics == null) {
            graphics = new Graphics(buffer, xray);
        }
        graphics.reset(buffer, xray);
        graphics.setClip(x, y, w, h);
        this.paint(graphics);
        graphics.reset(buffer, xray); // paintTicker fix
        this.paintTicker(graphics);
        this.paintSoftMenu(graphics);
    }

    public void invokeKeyReleased(final int n) {
        int i = 1 << this.getGameAction(n);
        if((m_keyStates & i) != 0)
            m_keyStates &= ~i;

        i = getKeyBit(n);
        if((vKeyStates & i) != 0)
            vKeyStates &= ~i;

        this.keyReleased(n);
    }

    public void invokeKeyPressed(final int n) {
        if (super.aBoolean18) {
            if (n >= 49 && n <= 57) {
                final int n2;
                if ((n2 = n - 49 + 1) < super.commands.size()) {
                    super.cmdListener.commandAction((Command) super.commands.get(n2), this);
                    super.aBoolean18 = false;
                }
            } else if (n == KeyMapping.getArrowKeyFromDevice(1)) {
                if (super.anInt28 > 0) {
                    --super.anInt28;
                }
            } else if (n == KeyMapping.getArrowKeyFromDevice(6)) {
                if (super.anInt28 < super.commands.size() - 2) {
                    ++super.anInt28;
                }
            } else {
                final int n3;
                if (n == KeyMapping.getArrowKeyFromDevice(8) && (n3 = super.anInt28 + 1) < super.commands.size()) {
                    super.cmdListener.commandAction((Command) super.commands.get(n3), this);
                    super.aBoolean18 = false;
                }
            }
            this.refreshSoftMenu();
            return;
        }
        this.m_keyStates |= 1 << this.getGameAction(n);
        this.vKeyStates |= getKeyBit(n);
        this.keyPressed(n);
    }

    public void invokeKeyRepeated(final int n) {
        this.keyRepeated(n);
    }

    public void invokeHideNotify() {
        this.hideNotify();
    }

    public void invokeShowNotify() {
        this.showNotify();
    }

    public void invokeSizeChanged(final int n, final int n2) {
        super.w = Emulator.getEmulator().getScreen().getWidth();
        super.h = Emulator.getEmulator().getScreen().getHeight();
        this.sizeChanged(n, n2);
        Emulator.getEventQueue().queueRepaint();
    }

    protected abstract void paint(final Graphics g);

    protected void keyReleased(final int n) {
    }

    protected void keyPressed(final int n) {
    }

    protected void keyRepeated(final int n) {
    }

    public void repaint() {
        repaint(0, 0, w, h);
    }

    public void repaint(final int x, final int y, final int w, final int h) {
        if (this != Emulator.getCurrentDisplay().getCurrent()) {
            return;
        }
        Emulator.getEventQueue().queueRepaint(x, y, w, h);
    }

    public void serviceRepaints() {
        if (this != Emulator.getCurrentDisplay().getCurrent()) {
            return;
        }
        Emulator.getEventQueue().serviceRepaints();
    }

    protected void hideNotify() {
    }

    protected void showNotify() {
    }

    public void setFullScreenMode(final boolean b) {
        if (!Settings.ignoreFullScreen) {
            super.h = Emulator.getEmulator().getScreen().getHeight();
            if (b) {
//                this.setTicker(null);
                return;
            }
//            this.setTicker(new Ticker("setFullScreenMode(true) to remove me"));
            super.h -= Screen.fontHeight4;
        }
    }

    protected void sizeChanged(final int n, final int n2) {
    }

    protected void pointerPressed(final int n, final int n2) {
    }

    protected void pointerReleased(final int n, final int n2) {
    }

    protected void pointerDragged(final int n, final int n2) {
    }

    public void invokePointerPressed(final int n, final int n2) {
        if (super.aBoolean18) {
            final int n3 = super.w >> 1;
            final int anInt181 = Screen.fontHeight4;
            final int n5;
            final int n4 = (n5 = super.commands.size() - 1) * anInt181;
            final int n6 = n3 - 1;
            final int n7 = super.h - n4 - 1;
            final int[] array;
            if (BoundsUtils.collides(array = new int[]{n6, n7, n3, n4}, n, n2)) {
                array[0] = n6;
                array[1] = n7;
                array[2] = n3;
                array[3] = anInt181;
                int[] array2;
                int n8;
                for (int i = 0; i < n5; ++i, array2 = array, n8 = 1, array2[n8] += anInt181) {
                    if (BoundsUtils.collides(array, n, n2)) {
                        super.cmdListener.commandAction((Command) super.commands.get(i + 1), this);
                        super.aBoolean18 = false;
                        return;
                    }
                }
            }
            return;
        }
        this.pointerPressed(n, n2);
    }

    public void invokePointerReleased(final int n, final int n2) {
        this.pointerReleased(n, n2);
    }

    public void invokePointerDragged(final int n, final int n2) {
        this.pointerDragged(n, n2);
    }

    public int getGameAction(final int n) {
        int n2 = 0;
        int n3 = 0;
        switch (n) {
            case 49: {
                n3 = 9;
                break;
            }
            case 51: {
                n3 = 10;
                break;
            }
            case 55: {
                n3 = 11;
                break;
            }
            case 57: {
                n3 = 12;
                break;
            }
            default: {
                if (n == KeyMapping.getArrowKeyFromDevice(1) || n == 50) {
                    n3 = 1;
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(6) || n == 56) {
                    n3 = 6;
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(2) || n == 52) {
                    n3 = 2;
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(5) || n == 54) {
                    n3 = 5;
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(8) || n == 53) {
                    n3 = 8;
                    break;
                }
                return n2;
            }
        }
        n2 = n3;
        return n2;
    }

    public int getKeyCode(final int n) {
        int n2 = 0;
        int n3 = 0;
        switch (n) {
            case 1: {
                n3 = KeyMapping.getArrowKeyFromDevice(1);
                break;
            }
            case 6: {
                n3 = KeyMapping.getArrowKeyFromDevice(6);
                break;
            }
            case 2: {
                n3 = KeyMapping.getArrowKeyFromDevice(2);
                break;
            }
            case 5: {
                n3 = KeyMapping.getArrowKeyFromDevice(5);
                break;
            }
            case 8: {
                n3 = KeyMapping.getArrowKeyFromDevice(8);
                break;
            }
            case 9: {
                n3 = 49;
                break;
            }
            case 10: {
                n3 = 51;
                break;
            }
            case 11: {
                n3 = 55;
                break;
            }
            case 12: {
                n3 = 57;
                break;
            }
            default: {
                return n2;
            }
        }
        n2 = n3;
        return n2;
    }

    public String getKeyName(final int n) {
        if (n == 8) return "Backspace";
        if (n == 32) return "Space";
        if (n >= 32 && n <= 126) {
            return "" + (char) n;
        }
        String s = "";
        String s2 = null;
        switch (n) {
            case 48: {
                s2 = "0";
                break;
            }
            case 49: {
                s2 = "1";
                break;
            }
            case 50: {
                s2 = "2";
                break;
            }
            case 51: {
                s2 = "3";
                break;
            }
            case 52: {
                s2 = "4";
                break;
            }
            case 53: {
                s2 = "5";
                break;
            }
            case 54: {
                s2 = "6";
                break;
            }
            case 55: {
                s2 = "7";
                break;
            }
            case 56: {
                s2 = "8";
                break;
            }
            case 57: {
                s2 = "9";
                break;
            }
            case 42: {
                s2 = "*";
                break;
            }
            case 35: {
                s2 = "#";
                break;
            }
            default: {
                if (n == KeyMapping.getArrowKeyFromDevice(1)) {
                    s2 = "Up";
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(6)) {
                    s2 = "Down";
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(2)) {
                    s2 = "Left";
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(5)) {
                    s2 = "Right";
                    break;
                }
                if (n == KeyMapping.getArrowKeyFromDevice(8)) {
                    s2 = "Select";
                    break;
                }
                return s;
            }
        }
        s = s2;
        return s;
    }

    public boolean hasPointerEvents() {
        if (h < 320) return false;
        return true;
    }

    public boolean hasPointerMotionEvents() {
        if (h < 320) return false;
        return true;
    }

    public boolean hasRepeatEvents() {
        return Settings.enableKeyRepeat;
    }

    public boolean isDoubleBuffered() {
        return true;
    }

    // Copyright Yury Kharchenko
    private int getKeyBit(int vKey) {
        switch(getGameAction(vKey)) {
            case UP:
                return 1 << 12; // 12 Up
            case LEFT:
                return 1 << 13; // 13 Left
            case RIGHT:
                return 1 << 14; // 14 Right
            case DOWN:
                return 1 << 15; // 15 Down
            case FIRE:
                return 1 << 16; // 16 Select
            case GAME_C:
                return 1 << 19; // 19 Softkey 3
        }
        switch (vKey) {
            case KEY_NUM0:
                return 1; //  0 0
            case KEY_NUM1:
                return 1 <<  1; //  1 1
            case KEY_NUM2:
                return 1 <<  2; //  2 2
            case KEY_NUM3:
                return 1 <<  3; //  3 3
            case KEY_NUM4:
                return 1 <<  4; //  4 4
            case KEY_NUM5:
                return 1 <<  5; //  5 5
            case KEY_NUM6:
                return 1 <<  6; //  6 6
            case KEY_NUM7:
                return 1 <<  7; //  7 7
            case KEY_NUM8:
                return 1 <<  8; //  8 8
            case KEY_NUM9:
                return 1 <<  9; //  9 9
            case KEY_STAR:
                return 1 << 10; // 10 *
            case KEY_POUND:
                return 1 << 11; // 11 #
            default:
                if(KeyMapping.isLeftSoft(vKey))
                    return 1 << 17; // 17 Softkey 1
                if(KeyMapping.isRightSoft(vKey))
                    return 1 << 18; // 18 Softkey 2
                return 0;
        }
    }

    public int getKeyStatesVodafone() {
        return vKeyStates;
    }
}
