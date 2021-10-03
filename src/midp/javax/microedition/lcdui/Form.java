package javax.microedition.lcdui;

import emulator.lcdui.*;

public class Form extends Screen
{
    ItemStateListener itemStateListener;
    
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
                if (array[i].screen != null) {
                    throw new IllegalStateException();
                }
                super.items.add(array[i]);
                array[i].screen = this;
            }
        }
    }
    
    public int append(final Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.screen != null) {
            throw new IllegalStateException();
        }
        super.items.add(item);
        item.screen = this;
        return super.items.size() - 1;
    }
    
    public int append(final String s) {
        final StringItem stringItem = new StringItem(null, s);
        super.items.add(stringItem);
        stringItem.screen = this;
        return super.items.size() - 1;
    }
    
    public int append(final Image image) {
        final ImageItem imageItem = new ImageItem(null, image, 0, null);
        super.items.add(imageItem);
        imageItem.screen = this;
        return super.items.size() - 1;
    }
    
    public void insert(final int n, final Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.screen != null) {
            throw new IllegalStateException();
        }
        super.items.insertElementAt(item, n);
        item.screen = this;
    }
    
    public void delete(final int n) {
        if (n < 0 || n >= super.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        ((Item)super.items.get(n)).screen = null;
        super.items.remove(n);
    }
    
    public void deleteAll() {
        for (int i = 0; i < super.items.size(); ++i) {
            ((Item)super.items.get(i)).screen = null;
        }
        super.items.removeAllElements();
    }
    
    public void set(final int n, final Item item) {
        if (n < 0 || n >= super.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.screen != null) {
            throw new IllegalStateException();
        }
        ((Item)super.items.get(n)).screen = null;
        super.items.set(n, item);
        item.screen = null;
    }
    
    public Item get(final int n) {
        if (n < 0 || n >= super.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        return ((Item)super.items.get(n));
    }
    
    public void setItemStateListener(final ItemStateListener anItemStateListener858) {
        this.itemStateListener = anItemStateListener858;
    }
    
    public int size() {
        return super.items.size();
    }
    
    public int getWidth() {
        return super.getWidth();
    }
    
    public int getHeight() {
        return super.getHeight();
    }
    
    protected void paint(final Graphics graphics) {
        this.layout();
        for (int i = 0; i < super.items.size(); ++i) {
            final Item item;
            if ((item = ((Item)super.items.get(i))).aBoolean177) {
                item.paint(graphics);
            }
        }
    }
    
    protected void layout() {
        if (super.anInt182 != -1) {
            int anInt181 = Screen.fontHeight4;
            for (int i = 0; i < super.items.size(); ++i) {
                final Item item = ((Item)super.items.get(i));
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
                    method173 = b.method173(super.bounds, item.anIntArray21);
                }
                item2.aBoolean177 = method173;
            }
            return;
        }
        int n = Screen.fontHeight4 + super.bounds[3];
        for (int j = super.items.size() - 1; j >= 0; --j) {
            final Item item3 = ((Item)super.items.get(j));
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
                method174 = b.method173(super.bounds, item3.anIntArray21);
            }
            item4.aBoolean177 = method174;
        }
        int anInt182 = Screen.fontHeight4;
        for (int k = 0; k < super.items.size(); ++k) {
            final Item item5;
            if ((item5 = ((Item)super.items.get(k))).aBoolean177) {
                item5.layout();
                item5.anIntArray21[1] = anInt182;
                anInt182 += item5.anIntArray21[3];
                item5.aBoolean177 = b.method173(super.bounds, item5.anIntArray21);
            }
        }
    }
}
