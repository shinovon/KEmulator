package javax.microedition.lcdui;

import java.util.*;

import emulator.*;
import emulator.lcdui.*;

public abstract class Screen extends Displayable {
    static final Font font;
    static final int fontHeight;
    static final int fontHeight4;
    Vector items;
    int anInt182;
    int anInt349;
    private long lastPressTime;

    Screen() {
        this("");
    }

    Screen(final String s) {
        super();
        super.title = ((s == null) ? "" : s);
        this.items = new Vector();
        this.anInt182 = 0;
        this.anInt349 = -1;
    }

    public void invokeKeyPressed(final int n) {
        final long currentTimeMillis;
        if ((currentTimeMillis = System.currentTimeMillis()) - this.lastPressTime < 100L) {
            return;
        }
        this.lastPressTime = currentTimeMillis;
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
        if (selectedItem != null && selectedItem instanceof CustomItem && ((CustomItem) selectedItem).callTraverse(n)) {
            return;
        }
        if (selectedItem != null && n == KeyMapping.getArrowKeyFromDevice(8)) {
            selectedItem.itemApplyCommand();
            return;
        }
        if (n == KeyMapping.getArrowKeyFromDevice(1)) {
            if (this.items.size() > 0) {
                final int index = this.items.indexOf(selectedItem);
                if (selectedItem != null) {
                    if (!selectedItem.scrollUp()) {
                        return;
                    }
                    selectedItem.defocus();
                }
                Screen screen;
                Vector vector;
                int n4;
                if (index == -1) {
                    screen = this;
                    vector = this.items;
                    n4 = this.items.size() - 1;
                } else if (index > 0) {
                    screen = this;
                    vector = this.items;
                    n4 = index - 1;
                } else {
                    screen = this;
                    vector = this.items;
                    n4 = 0;
                }
                screen.selectedItem = (Item) vector.get(n4);
                selectedItem.focus();
                if (!selectedItem.shownOnForm) {
                    this.anInt182 = this.items.indexOf(selectedItem);
                    this.anInt349 = -1;
                }
            }
        } else if (n == KeyMapping.getArrowKeyFromDevice(6) && this.items.size() > 0) {
            final int index2 = this.items.indexOf(selectedItem);
            if (selectedItem != null) {
                if (!selectedItem.scrollDown()) {
                    return;
                }
                selectedItem.defocus();
            }
            Screen screen2;
            Vector vector2;
            int n5;
            if (index2 == -1) {
                screen2 = this;
                vector2 = this.items;
                n5 = 0;
            } else if (index2 < this.items.size() - 1) {
                screen2 = this;
                vector2 = this.items;
                n5 = index2 + 1;
            } else {
                screen2 = this;
                vector2 = this.items;
                n5 = this.items.size() - 1;
            }
            screen2.selectedItem = (Item) vector2.get(n5);
            selectedItem.focus();
            if (!selectedItem.shownOnForm) {
                this.anInt182 = -1;
                this.anInt349 = this.items.indexOf(selectedItem);
            }
        }
    }

    public void invokeKeyReleased(final int n) {
    }

    public void invokePointerPressed(final int x, final int y) {
        if (super.aBoolean18) {
            final int n3 = super.w >> 1;
            final int anInt181 = Screen.fontHeight4;
            final int n5;
            final int n4 = (n5 = super.commands.size() - 1) * anInt181;
            final int n6 = n3 - 1;
            final int n7 = super.h - n4 - 1;
            final int[] array;
            if (BoundsUtils.collides(array = new int[]{n6, n7, n3, n4}, x, y)) {
                array[0] = n6;
                array[1] = n7;
                array[2] = n3;
                array[3] = anInt181;
                int[] array2;
                int n8;
                for (int i = 0; i < n5; ++i, array2 = array, n8 = 1, array2[n8] += anInt181) {
                    if (BoundsUtils.collides(array, x, y)) {
                        super.cmdListener.commandAction((Command) super.commands.get(i + 1), this);
                        super.aBoolean18 = false;
                        return;
                    }
                }
            }
            return;
        }
        if (selectedItem != null && selectedItem instanceof ChoiceGroup && ((ChoiceGroup) selectedItem).aBoolean542) {
            selectedItem.pointerPressed(x, y);
            return;
        }
        if (this.items.size() > 0) {
            int j = 0;
            while (j < this.items.size()) {
                final Item futureSelect;
                if ((futureSelect = (Item) this.items.get(j)).shownOnForm && BoundsUtils.collides(futureSelect.bounds, x, y)) {
                    if (futureSelect == selectedItem) {
                        selectedItem.pointerPressed(x, y);
                        return;
                    }
                    if (selectedItem != null) {
                        selectedItem.defocus();
                    }
                    (selectedItem = futureSelect).focus();
                } else {
                    ++j;
                }
            }
        }
    }

    public void invokePointerReleased(final int n, final int n2) {
    }

    public void invokePointerDragged(final int n, final int n2) {
    }

    protected abstract void paint(final Graphics p0);

    public void invokePaint(final Graphics graphics) {
        Displayable.resetXRayGraphics();
        final int color = graphics.getColor();
        final int strokeStyle = graphics.getStrokeStyle();
        final Font font = graphics.getFont();
        graphics.setFont(Screen.font);
        graphics.setStrokeStyle(0);
        emulator.lcdui.a.method177(graphics, 0, 0, super.w, super.h, false);
        this.drawTitleBar(graphics);
        this.paint(graphics);
        this.drawScrollBar(graphics);
        this.paintTicker(graphics);
        this.paintSoftMenu(graphics);
        graphics.setColor(color);
        graphics.setFont(font);
        graphics.setStrokeStyle(strokeStyle);
    }

    protected void drawTitleBar(final Graphics graphics) {
        final int n;
        final String value = String.valueOf(n = ((selectedItem != null) ? (this.items.indexOf(selectedItem) + 1) : this.items.size()));
        final int n2 = (Screen.fontHeight4 >> 1) - 1;
        final int stringWidth = Screen.font.stringWidth(super.title);
        final int stringWidth2 = Screen.font.stringWidth(value);
        final int n3 = (super.w - stringWidth >> 1) + 2;
        final int n4 = super.w - stringWidth2 - 2;
        graphics.setColor(8617456);
        graphics.fillRect(2, n2, (super.w - stringWidth >> 1) - 2, 2);
        graphics.fillRect(n3 + stringWidth + 2, n2, n4 - n3 - stringWidth - 4, 2);
        graphics.setColor(-16777216);
        graphics.setFont(Screen.font);
        graphics.drawString(super.title, n3, 1, 0);
        graphics.drawString(value, n4, 1, 0);
    }

    protected void sizeChanged(final int w, final int h) {
    }

    protected void drawScrollBar(final Graphics graphics) {
        emulator.lcdui.a.method179(graphics, bounds[W] + 1, Screen.fontHeight4 - 1, 2, bounds[H] - 2, this.items.size(), (selectedItem != null) ? this.items.indexOf(selectedItem) : -1);
    }

    static {
        font = Font.getDefaultFont();
        fontHeight = Screen.font.getHeight();
        fontHeight4 = Screen.fontHeight + 4;
    }

    protected void shown() {

    }
}
