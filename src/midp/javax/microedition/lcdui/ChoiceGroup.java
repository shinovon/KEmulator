package javax.microedition.lcdui;

import emulator.lcdui.b;
import emulator.lcdui.c;

import java.util.Arrays;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.a;

public class ChoiceGroup
extends Item
implements Choice {
    boolean aBoolean541;
    int choiceType;
    private int fitPolicy;
    private Vector items;
    boolean aBoolean542;
    int anInt28;
    int anInt29;
    Command aCommand540;
	private int currentSelect;

    public ChoiceGroup(String label, int choiceType) {
        this(label, choiceType, new String[0], null);
    }

    public ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements) {
        this(label, choiceType, stringElements, imageElements, false);
    }

    ChoiceGroup(String label, int choiceType, String[] strs, Image[] imgs, boolean b) {
        super(label);
        if (!(choiceType == 2 || choiceType == 1 || choiceType == 3 && b || choiceType == 4)) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        while (i < strs.length) {
            if (strs[i] == null) {
                throw new NullPointerException();
            }
            ++i;
        }
        if (imgs != null && strs.length != imgs.length) {
            throw new IllegalArgumentException();
        }
        this.choiceType = choiceType;
        this.fitPolicy = 0;
        this.items = new Vector();
        int j = 0;
        while (j < strs.length) {
            this.items.add(new a(strs[j], imgs == null ? null : imgs[j], null, this));
            ++j;
        }
        if (this.items.size() > 0) {
            ((a)this.items.get((int)0)).sel = true;
        }
    }

    public int append(String s, Image image) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.items.add(new a(s, image, null, this));
        return this.items.size() - 1;
    }

    public void delete(int n) {
        if (n < 0 || n >= this.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        this.items.remove(n);
    }

    public void deleteAll() {
        this.items.removeAllElements();
    }

    public void setFitPolicy(int anInt30) {
        this.fitPolicy = anInt30;
    }

    public int getFitPolicy() {
        return this.fitPolicy;
    }

    public void setFont(int n, Font aFont420) {
        if (n < 0 || n >= this.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        ((a)this.items.get((int)n)).font = aFont420;
    }

    public Font getFont(int n) {
        return ((a)this.items.get((int)n)).font;
    }

    public Image getImage(int n) {
        return ((a)this.items.get((int)n)).image;
    }

    public String getString(int n) {
        return ((a)this.items.get((int)n)).string;
    }

    public void insert(int n, String s, Image image) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.items.insertElementAt((Object)new a(s, image, null, this), n);
    }

    public void set(int n, String s, Image image) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.items.set(n, (Object)new a(s, image, null, this));
    }

    public void setSelectedFlags(boolean[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        if (array.length < this.items.size()) {
            throw new IllegalArgumentException();
        }
        if (this.choiceType != 2) {
            int n = 0;
            int i = 0;
            while (i < this.items.size()) {
                if (n == 0 && array[i]) {
                    ((a)this.items.get((int)i)).sel = true;
                    n = 1;
                } else {
                    ((a)this.items.get((int)i)).sel = false;
                }
                ++i;
            }
            if (n == 0 && this.items.size() > 0) {
                ((a)this.items.get((int)n)).sel = true;
            }
            return;
        }
        int j = 0;
        while (j < this.items.size()) {
            ((a)this.items.get((int)j)).sel = array[j];
            ++j;
        }
    }

    public int getSelectedFlags(boolean[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        if (array.length < this.items.size()) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        int i = 0;
        while (i < this.items.size()) {
            array[i] = ((a)this.items.get((int)i)).sel;
            if (array[i]) {
                ++n;
            }
            ++i;
        }
        return n;
    }

    public int getSelectedIndex() {
        if (this.choiceType != 2) {
            int i = 0;
            while (i < this.items.size()) {
                if (((a)this.items.get((int)i)).sel) {
                    return i;
                }
                ++i;
            }
        }
        return 0;
    }

    public boolean isSelected(int n) {
        return ((a)this.items.get((int)n)).sel;
    }

    public void setSelectedIndex(int n, boolean flag) {
        if (n < 0 || n >= this.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (this.choiceType != 2) {
        	if(flag) {
        		currentSelect = n;
	            int i = 0;
	            while (i < this.items.size()) {
	               ((a)this.items.get(i)).sel = i == n;
	                ++i;
	            }
        	}
            return;
        }
        ((a)this.items.get((int)n)).sel = flag;
    }

    public int size() {
        return this.items.size();
    }

    protected void itemApplyCommand() {
        super.itemApplyCommand();
        if (this.aBoolean541 && this.aCommand540 != null) {
            this.screen.cmdListener.commandAction(this.aCommand540, (Displayable)this.screen);
        }
        if (this.choiceType == 1) {
            this.setSelectedIndex(this.currentPos, true);
            return;
        }
        if (this.choiceType == 2) {
            this.setSelectedIndex(this.currentPos, !this.isSelected(this.currentPos));
            return;
        }
        if (this.choiceType == 4) {
            if (this.aBoolean542) {
                this.setSelectedIndex(this.currentPos, true);
            }
            this.aBoolean542 = !this.aBoolean542;
        }
    }

    protected void paint(Graphics graphics) {
        if (!this.aBoolean541) {
            super.paint(graphics);
        } else {
            graphics.setColor(-16777216);
        }
        int n = this.anIntArray21[1];
        if (this.aStringArray175 != null && this.aStringArray175.length > 0) {
            graphics.setFont(Item.font);
            int i = 0;
            while (i < this.aStringArray175.length) {
                graphics.drawString(this.aStringArray175[i], this.anIntArray21[0] + 4, n + 2, 0);
                n += Item.font.getHeight() + 4;
                ++i;
            }
        }
        if (this.items.size() > 0) {
            switch (this.choiceType) {
                case 1: 
                case 2: 
                case 3: {
                    int j = 0;
                    while (j < this.items.size()) {
                        a a2 = (a)this.items.get(j);
                        if (a2.aBoolean424) {
                            a2.method211(graphics, this.inFocus && j == this.currentPos);
                        }
                        ++j;
                    }
                }
                case 4: {
                    if (this.aBoolean542 && this.anIntArray179 != null) {
                        this.anInt28 = Math.max((int)(this.anIntArray21[1] - this.anInt29 / 2 - 4), (int)0);
                        a a2 = (a)this.items.get(0);
                        emulator.lcdui.a.method178((Graphics)graphics, (int)a2.anIntArray421[0], (int)(this.anInt28 - 2), (int)a2.anIntArray421[2], (int)(this.anInt29 + 2));
                        int k = 0;
                        while (k < this.items.size()) {
                            a a3 = (a)this.items.get(k);
                            if (a3.aBoolean424) {
                                a3.method211(graphics, k == this.currentPos);
                            }
                            ++k;
                        }
                        return;
                    }
                    ((a)this.items.get(this.currentSelect)).method211(graphics, this.inFocus);
                }
            }
        }
    }

    protected void layout() {
        super.layout();
        int n = 0;
        if (this.label != null) {
            int n2 = this.getPreferredWidth() - 8;
            this.aStringArray175 = c.method175((String)this.label, (Font)Item.font, (int)n2, (int)n2);
            n = (Item.font.getHeight() + 4) * this.aStringArray175.length;
        } else {
            this.aStringArray175 = null;
        }
        switch (this.choiceType) {
            case 1: 
            case 2: 
            case 3: {
                this.anIntArray179 = new int[this.items.size()];
                int i = 0;
                while (i < this.items.size()) {
                    a a2 = (a)this.items.get(i);
                    a2.method212();
                    a2.anIntArray421[1] = n;
                    n += a2.anIntArray421[3];
                    this.anIntArray179[i] = i;
                    if (this.choiceType == 3 && i == this.currentPos) {
                        this.setSelectedIndex(i, true);
                    }
                    ++i;
                }
                break;
            }
            case 4: {
                a a2 = (a)this.items.get(this.getSelectedIndex());
                a2.method212();
                n += a2.anIntArray421[3];
                if (this.aBoolean542) {
                    this.anIntArray179 = new int[this.items.size()];
                    this.anInt29 = 0;
                    int j = 0;
                    while (j < this.items.size()) {
                        this.anInt29 += ((a)this.items.get((int)j)).anIntArray421[3];
                        ++j;
                    }
                    int n3 = 0;
                    int k = 0;
                    while (k < this.items.size()) {
                        a a3 = (a)this.items.get(k);
                        a3.method212();
                        a3.anIntArray421[1] = n3;
                        n3 += a3.anIntArray421[3];
                        a3.anIntArray421[0] = this.screen.bounds[2] / 4;
                        a3.anIntArray421[2] = this.screen.bounds[2] / 2;
                        this.anIntArray179[k] = k++;
                    }
                    break;
                }
                this.anIntArray179 = null;
                this.anInt29 = -1;
                this.anInt28 = n - a2.anIntArray421[3];
            }
        }
        this.anIntArray21[3] = Math.min((int)n, (int)this.screen.bounds[3]);
    }

    protected boolean scrollUp() {
        if (this.choiceType == 4 && this.aBoolean542) {
            if (super.scrollUp()) {
                this.currentPos = this.anIntArray179.length - 1;
            }
            return false;
        }
        return super.scrollUp();
    }

    protected boolean scrollDown() {
        if (this.choiceType == 4 && this.aBoolean542) {
            if (super.scrollDown()) {
                this.currentPos = 0;
            }
            return false;
        }
        return super.scrollDown();
    }

    protected void pointerPressed(int n, int n2) {
        int[] array = new int[4];
        int i = 0;
        while (i < this.items.size()) {
            a a2 = (a)this.items.get(i);
            System.arraycopy((Object)a2.anIntArray421, (int)0, (Object)array, (int)0, (int)4);
            int[] array2 = array;
            boolean n3 = true;
            int[] arrn = array2;
            arrn[1] = arrn[1] + (this.aBoolean542 ? this.anInt28 : this.anIntArray21[1]);
            if (a2.aBoolean424 && b.method172((int[])array, (int)n, (int)n2)) {
                this.currentPos = i;
            }
            ++i;
        }
    }
}
