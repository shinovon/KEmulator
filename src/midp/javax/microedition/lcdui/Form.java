package javax.microedition.lcdui;

import emulator.lcdui.*;

public class Form extends Screen
{
    ItemStateListener anItemStateListener858;
    
    public Form(final String s) {
        this(s, null);
    }
    
    public Form(final String s, final Item[] array) {
        super(s);
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new NullPointerException();
                }
                if (array[i].aScreen176 != null) {
                    throw new IllegalStateException();
                }
                super.aVector443.add(array[i]);
                array[i].aScreen176 = this;
            }
        }
    }
    
    public int append(final Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.aScreen176 != null) {
            throw new IllegalStateException();
        }
        super.aVector443.add(item);
        item.aScreen176 = this;
        return super.aVector443.size() - 1;
    }
    
    public int append(final String s) {
        final StringItem stringItem = new StringItem(null, s);
        super.aVector443.add(stringItem);
        stringItem.aScreen176 = this;
        return super.aVector443.size() - 1;
    }
    
    public int append(final Image image) {
        final ImageItem imageItem = new ImageItem(null, image, 0, null);
        super.aVector443.add(imageItem);
        imageItem.aScreen176 = this;
        return super.aVector443.size() - 1;
    }
    
    public void insert(final int n, final Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.aScreen176 != null) {
            throw new IllegalStateException();
        }
        super.aVector443.insertElementAt(item, n);
        item.aScreen176 = this;
    }
    
    public void delete(final int n) {
        if (n < 0 || n >= super.aVector443.size()) {
            throw new IndexOutOfBoundsException();
        }
        ((Item)super.aVector443.get(n)).aScreen176 = null;
        super.aVector443.remove(n);
    }
    
    public void deleteAll() {
        for (int i = 0; i < super.aVector443.size(); ++i) {
            ((Item)super.aVector443.get(i)).aScreen176 = null;
        }
        super.aVector443.removeAllElements();
    }
    
    public void set(final int n, final Item item) {
        if (n < 0 || n >= super.aVector443.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.aScreen176 != null) {
            throw new IllegalStateException();
        }
        ((Item)super.aVector443.get(n)).aScreen176 = null;
        super.aVector443.set(n, item);
        item.aScreen176 = null;
    }
    
    public Item get(final int n) {
        if (n < 0 || n >= super.aVector443.size()) {
            throw new IndexOutOfBoundsException();
        }
        return ((Item)super.aVector443.get(n));
    }
    
    public void setItemStateListener(final ItemStateListener anItemStateListener858) {
        this.anItemStateListener858 = anItemStateListener858;
    }
    
    public int size() {
        return super.aVector443.size();
    }
    
    public int getWidth() {
        return super.getWidth();
    }
    
    public int getHeight() {
        return super.getHeight();
    }
    
    protected void paint(final Graphics graphics) {
        this.layout();
        for (int i = 0; i < super.aVector443.size(); ++i) {
            final Item item;
            if ((item = ((Item)super.aVector443.get(i))).aBoolean177) {
                item.paint(graphics);
            }
        }
    }
    
    protected void layout() {
        if (super.anInt182 != -1) {
            int anInt181 = Screen.anInt181;
            for (int i = 0; i < super.aVector443.size(); ++i) {
                final Item item = ((Item)super.aVector443.get(i));
                Item item2;
                boolean method173;
                if (i < super.anInt182) {
                    item2 = item;
                    method173 = false;
                }
                else {
                    item.layout();
                    item.anIntArray21[1] = anInt181;
                    anInt181 += item.anIntArray21[3];
                    item2 = item;
                    method173 = b.method173(super.anIntArray21, item.anIntArray21);
                }
                item2.aBoolean177 = method173;
            }
            return;
        }
        int n = Screen.anInt181 + super.anIntArray21[3];
        for (int j = super.aVector443.size() - 1; j >= 0; --j) {
            final Item item3 = ((Item)super.aVector443.get(j));
            Item item4;
            boolean method174;
            if (j > super.anInt349) {
                item4 = item3;
                method174 = true;
            }
            else {
                item3.layout();
                item3.anIntArray21[1] = n - item3.anIntArray21[3];
                n -= item3.anIntArray21[3];
                item4 = item3;
                method174 = b.method173(super.anIntArray21, item3.anIntArray21);
            }
            item4.aBoolean177 = method174;
        }
        int anInt182 = Screen.anInt181;
        for (int k = 0; k < super.aVector443.size(); ++k) {
            final Item item5;
            if ((item5 = ((Item)super.aVector443.get(k))).aBoolean177) {
                item5.layout();
                item5.anIntArray21[1] = anInt182;
                anInt182 += item5.anIntArray21[3];
                item5.aBoolean177 = b.method173(super.anIntArray21, item5.anIntArray21);
            }
        }
    }
}
