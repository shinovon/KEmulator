package javax.microedition.lcdui;

import emulator.lcdui.*;

public class Form extends Screen {
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
        ((Item) super.items.get(n)).screen = null;
        super.items.remove(n);
    }

    public void deleteAll() {
        for (int i = 0; i < super.items.size(); ++i) {
            ((Item) super.items.get(i)).screen = null;
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
        ((Item) super.items.get(n)).screen = null;
        super.items.set(n, item);
        item.screen = this;
    }

    public Item get(final int n) {
        if (n < 0 || n >= super.items.size()) {
            throw new IndexOutOfBoundsException();
        }
        return ((Item) super.items.get(n));
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

    protected void paint(final Graphics g) {
        this.layout();
        for (int i = 0; i < super.items.size(); ++i) {
            final Item item = ((Item) super.items.get(i));
            if (item.shownOnForm) {
                item.paint(g);
            } else {
                item.updateHidden();
            }
        }
    }

    protected void layout() {
        if (super.anInt182 != -1) {
            int h = Screen.fontHeight4;
            for (int i = 0; i < super.items.size(); ++i) {
                final Item item = ((Item) super.items.get(i));
                if (i < super.anInt182) {
//                    if(item instanceof CustomItem && item.shownOnForm) {
//                        ((CustomItem) item).hideNotify();
//                    }
                    item.shownOnForm = false;
                } else {
                    item.layout();
                    item.bounds[Y] = h;
                    h += item.bounds[H];
                    item.shownOnForm = BoundsUtils.collides(super.bounds, item.bounds);
//                    if(!item.shownOnForm && (item.shownOnForm = BoundsUtils.collides(super.bounds, item.bounds)) && item instanceof CustomItem) {
//                        ((CustomItem) item).showNotify();
//                    }
                }
            }
            return;
        }
        int n = Screen.fontHeight4 + super.bounds[H];
        for (int j = super.items.size() - 1; j >= 0; --j) {
            final Item item = ((Item) super.items.get(j));
            boolean c;
            if (j > super.anInt349) {
                item.shownOnForm = true;
            } else {
                item.layout();
                item.bounds[Y] = n - item.bounds[H];
                n -= item.bounds[H];
                item.shownOnForm = BoundsUtils.collides2(super.bounds, item.bounds);
            }
        }
        int h = Screen.fontHeight4;
        for (int k = 0; k < super.items.size(); ++k) {
            final Item item;
            if ((item = ((Item) super.items.get(k))).shownOnForm) {
                item.layout();
                item.bounds[Y] = h;
                h += item.bounds[H];
                item.shownOnForm = BoundsUtils.collides2(super.bounds, item.bounds);
                /*if(!item.shownOnForm && item instanceof CustomItem) {
                	((CustomItem) item5).hideNotify();
                }*/
            }
        }
    }

    protected void sizeChanged(final int w, final int h) {
        this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
        layout();
    }
}
