package javax.microedition.lcdui;

import emulator.lcdui.b;
import emulator.lcdui.c;
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
    int anInt349;
    private int anInt30;
    private Vector aVector443;
    boolean aBoolean542;
    int anInt28;
    int anInt29;
    Command aCommand540;

    public ChoiceGroup(String s, int n) {
        this(s, n, new String[0], null);
    }

    public ChoiceGroup(String s, int n, String[] array, Image[] array2) {
        this(s, n, array, array2, false);
    }

    ChoiceGroup(String s, int anInt349, String[] array, Image[] array2, boolean b) {
        super(s);
        if (!(anInt349 == 2 || anInt349 == 1 || anInt349 == 3 && b || anInt349 == 4)) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        while (i < array.length) {
            if (array[i] == null) {
                throw new NullPointerException();
            }
            ++i;
        }
        if (array2 != null && array.length != array2.length) {
            throw new IllegalArgumentException();
        }
        this.anInt349 = anInt349;
        this.anInt30 = 0;
        this.aVector443 = new Vector();
        int j = 0;
        while (j < array.length) {
            this.aVector443.add((Object)new a(array[j], array2 == null ? null : array2[j], null, this));
            ++j;
        }
        if (this.aVector443.size() > 0) {
            ((a)this.aVector443.get((int)0)).aBoolean417 = true;
        }
    }

    public int append(String s, Image image) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.aVector443.add((Object)new a(s, image, null, this));
        return this.aVector443.size() - 1;
    }

    public void delete(int n) {
        if (n < 0 || n >= this.aVector443.size()) {
            throw new IndexOutOfBoundsException();
        }
        this.aVector443.remove(n);
    }

    public void deleteAll() {
        this.aVector443.removeAllElements();
    }

    public void setFitPolicy(int anInt30) {
        this.anInt30 = anInt30;
    }

    public int getFitPolicy() {
        return this.anInt30;
    }

    public void setFont(int n, Font aFont420) {
        if (n < 0 || n >= this.aVector443.size()) {
            throw new IndexOutOfBoundsException();
        }
        ((a)this.aVector443.get((int)n)).aFont420 = aFont420;
    }

    public Font getFont(int n) {
        return ((a)this.aVector443.get((int)n)).aFont420;
    }

    public Image getImage(int n) {
        return ((a)this.aVector443.get((int)n)).anImage419;
    }

    public String getString(int n) {
        return ((a)this.aVector443.get((int)n)).aString418;
    }

    public void insert(int n, String s, Image image) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.aVector443.insertElementAt((Object)new a(s, image, null, this), n);
    }

    public void set(int n, String s, Image image) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.aVector443.set(n, (Object)new a(s, image, null, this));
    }

    public void setSelectedFlags(boolean[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        if (array.length < this.aVector443.size()) {
            throw new IllegalArgumentException();
        }
        if (this.anInt349 != 2) {
            int n = 0;
            int i = 0;
            while (i < this.aVector443.size()) {
                if (n == 0 && array[i]) {
                    ((a)this.aVector443.get((int)i)).aBoolean417 = true;
                    n = 1;
                } else {
                    ((a)this.aVector443.get((int)i)).aBoolean417 = false;
                }
                ++i;
            }
            if (n == 0 && this.aVector443.size() > 0) {
                ((a)this.aVector443.get((int)n)).aBoolean417 = true;
            }
            return;
        }
        int j = 0;
        while (j < this.aVector443.size()) {
            ((a)this.aVector443.get((int)j)).aBoolean417 = array[j];
            ++j;
        }
    }

    public int getSelectedFlags(boolean[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        if (array.length < this.aVector443.size()) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        int i = 0;
        while (i < this.aVector443.size()) {
            array[i] = ((a)this.aVector443.get((int)i)).aBoolean417;
            if (array[i]) {
                ++n;
            }
            ++i;
        }
        return n;
    }

    public int getSelectedIndex() {
        if (this.anInt349 != 2) {
            int i = 0;
            while (i < this.aVector443.size()) {
                if (((a)this.aVector443.get((int)i)).aBoolean417) {
                    return i;
                }
                ++i;
            }
        }
        return 0;
    }

    public boolean isSelected(int n) {
        return ((a)this.aVector443.get((int)n)).aBoolean417;
    }

    public void setSelectedIndex(int n, boolean aBoolean417) {
        if (n < 0 || n >= this.aVector443.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (this.anInt349 != 2) {
            int i = 0;
            while (i < this.aVector443.size()) {
                a a2;
                boolean aBoolean418;
                if (i == n) {
                    a2 = (a)this.aVector443.get(i);
                    aBoolean418 = true;
                } else {
                    a2 = (a)this.aVector443.get(i);
                    aBoolean418 = false;
                }
                a2.aBoolean417 = aBoolean418;
                ++i;
            }
            return;
        }
        ((a)this.aVector443.get((int)n)).aBoolean417 = aBoolean417;
    }

    public int size() {
        return this.aVector443.size();
    }

    protected void itemApplyCommand() {
        super.itemApplyCommand();
        if (this.aBoolean541 && this.aCommand540 != null) {
            this.aScreen176.aCommandListener19.commandAction(this.aCommand540, (Displayable)this.aScreen176);
        }
        if (this.anInt349 == 1) {
            this.setSelectedIndex(this.anInt182, true);
            return;
        }
        if (this.anInt349 == 2) {
            this.setSelectedIndex(this.anInt182, !this.isSelected(this.anInt182));
            return;
        }
        if (this.anInt349 == 4) {
            if (this.aBoolean542) {
                this.setSelectedIndex(this.anInt182, true);
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
            graphics.setFont(Item.aFont173);
            int i = 0;
            while (i < this.aStringArray175.length) {
                graphics.drawString(this.aStringArray175[i], this.anIntArray21[0] + 4, n + 2, 0);
                n += Item.aFont173.getHeight() + 4;
                ++i;
            }
        }
        if (this.aVector443.size() > 0) {
            switch (this.anInt349) {
                case 1: 
                case 2: 
                case 3: {
                    int j = 0;
                    while (j < this.aVector443.size()) {
                        a a2 = (a)this.aVector443.get(j);
                        if (a2.aBoolean424) {
                            a2.method211(graphics, this.aBoolean18 && j == this.anInt182);
                        }
                        ++j;
                    }
                }
                case 4: {
                    if (this.aBoolean542 && this.anIntArray179 != null) {
                        this.anInt28 = Math.max((int)(this.anIntArray21[1] - this.anInt29 / 2 - 4), (int)0);
                        a a2 = (a)this.aVector443.get(0);
                        emulator.lcdui.a.method178((Graphics)graphics, (int)a2.anIntArray421[0], (int)(this.anInt28 - 2), (int)a2.anIntArray421[2], (int)(this.anInt29 + 2));
                        int k = 0;
                        while (k < this.aVector443.size()) {
                            a a3 = (a)this.aVector443.get(k);
                            if (a3.aBoolean424) {
                                a3.method211(graphics, k == this.anInt182);
                            }
                            ++k;
                        }
                        return;
                    }
                    ((a)this.aVector443.get(this.anInt182)).method211(graphics, this.aBoolean18);
                }
            }
        }
    }

    protected void layout() {
        super.layout();
        int n = 0;
        if (this.aString172 != null) {
            int n2 = this.getPreferredWidth() - 8;
            this.aStringArray175 = c.method175((String)this.aString172, (Font)Item.aFont173, (int)n2, (int)n2);
            n = (Item.aFont173.getHeight() + 4) * this.aStringArray175.length;
        } else {
            this.aStringArray175 = null;
        }
        switch (this.anInt349) {
            case 1: 
            case 2: 
            case 3: {
                this.anIntArray179 = new int[this.aVector443.size()];
                int i = 0;
                while (i < this.aVector443.size()) {
                    a a2 = (a)this.aVector443.get(i);
                    a2.method212();
                    a2.anIntArray421[1] = n;
                    n += a2.anIntArray421[3];
                    this.anIntArray179[i] = i;
                    if (this.anInt349 == 3 && i == this.anInt182) {
                        this.setSelectedIndex(i, true);
                    }
                    ++i;
                }
                break;
            }
            case 4: {
                a a2 = (a)this.aVector443.get(this.getSelectedIndex());
                a2.method212();
                n += a2.anIntArray421[3];
                if (this.aBoolean542) {
                    this.anIntArray179 = new int[this.aVector443.size()];
                    this.anInt29 = 0;
                    int j = 0;
                    while (j < this.aVector443.size()) {
                        this.anInt29 += ((a)this.aVector443.get((int)j)).anIntArray421[3];
                        ++j;
                    }
                    int n3 = 0;
                    int k = 0;
                    while (k < this.aVector443.size()) {
                        a a3 = (a)this.aVector443.get(k);
                        a3.method212();
                        a3.anIntArray421[1] = n3;
                        n3 += a3.anIntArray421[3];
                        a3.anIntArray421[0] = this.aScreen176.anIntArray21[2] / 4;
                        a3.anIntArray421[2] = this.aScreen176.anIntArray21[2] / 2;
                        this.anIntArray179[k] = k++;
                    }
                    break;
                }
                this.anIntArray179 = null;
                this.anInt29 = -1;
                this.anInt28 = n - a2.anIntArray421[3];
            }
        }
        this.anIntArray21[3] = Math.min((int)n, (int)this.aScreen176.anIntArray21[3]);
    }

    protected boolean scrollUp() {
        if (this.anInt349 == 4 && this.aBoolean542) {
            if (super.scrollUp()) {
                this.anInt182 = this.anIntArray179.length - 1;
            }
            return false;
        }
        return super.scrollUp();
    }

    protected boolean scrollDown() {
        if (this.anInt349 == 4 && this.aBoolean542) {
            if (super.scrollDown()) {
                this.anInt182 = 0;
            }
            return false;
        }
        return super.scrollDown();
    }

    protected void pointerPressed(int n, int n2) {
        int[] array = new int[4];
        int i = 0;
        while (i < this.aVector443.size()) {
            a a2 = (a)this.aVector443.get(i);
            System.arraycopy((Object)a2.anIntArray421, (int)0, (Object)array, (int)0, (int)4);
            int[] array2 = array;
            boolean n3 = true;
            int[] arrn = array2;
            arrn[1] = arrn[1] + (this.aBoolean542 ? this.anInt28 : this.anIntArray21[1]);
            if (a2.aBoolean424 && b.method172((int[])array, (int)n, (int)n2)) {
                this.anInt182 = i;
            }
            ++i;
        }
    }
}
