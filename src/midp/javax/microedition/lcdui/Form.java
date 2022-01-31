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
    
    protected void paint(final Graphics g) {
        this.layout();
        for (int i = 0; i < super.items.size(); ++i) {
            final Item item = ((Item)super.items.get(i));
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
            int x = 0;
            int lh = Screen.fontHeight4;
            boolean row = false;
            int rowHeight = 0;
            Item prevItem = null;
            for (int i = 0; i < super.items.size(); ++i) {
                final Item item = ((Item)super.items.get(i));
                boolean c;
                Item item2;
                if (i < super.anInt182) {
                    item2 = item;
                    c = false;
                }
                else {
                	/*
                	if(row) {
                        item.bounds[W] = bounds[W] - x - 4;
                	}*/
                    item.layout();
            		/*if((item.isLayoutNewLineBefore() || item.isFullWidthItem()) && row) {
            			h += prevItem.bounds[H];
            			x = 0;
            			row = false;
                        item.bounds[W] = bounds[W];
            			rowHeight = 0;
            		}
            		if(row) {
            			rowHeight = Math.max(rowHeight, item.bounds[H]);
            			item.setInRow(true);
            		}
            		boolean wrow = row;
            		row = false;
            		if(x != 0)
                    item.bounds[X] = x;*/
                    item.bounds[Y] = h;
                   /* lh = item.bounds[H];
                    if(item.allowNextItemPlaceSameRow() && !item.isLayoutNewLineAfter()) {
                    	x = item.getItemWidth() + 1;
                    	row = true;
            			rowHeight = Math.max(rowHeight, item.bounds[H]);
                    } else {
                    	row = false;
                    	x = 0;
                    }
                    if(!row) {
                    	if(wrow) {
                    		h += rowHeight;
                    		rowHeight = 0;
                    	} else*/ h += item.bounds[H];
                    //}
                    item2 = item;
                    c = true;
                    c = BoundsUtils.collides(super.bounds, item.bounds);
                }
                /*if(item2 instanceof CustomItem) {
                    boolean w = item2.shownOnForm;
	                if(!w && c) {
	                	((CustomItem) item2).showNotify();
	                } else if(w && !c) {
	                	((CustomItem) item2).hideNotify();
	                }
                }*/
                prevItem = item;
                item2.shownOnForm = c;
            }
            return;
        }
        int n = Screen.fontHeight4 + super.bounds[H];
        for (int j = super.items.size() - 1; j >= 0; --j) {
            final Item item3 = ((Item)super.items.get(j));
            Item item4;
            boolean c;
            if (j > super.anInt349) {
                item4 = item3;
                c = true;
            }
            else {
                item3.layout();
                item3.bounds[Y] = n - item3.bounds[H];
                n -= item3.bounds[H];
                item4 = item3;
                c = BoundsUtils.collides2(super.bounds, item3.bounds);
            }
            /*if(item4 instanceof CustomItem) {
                boolean w = item4.shownOnForm;
                if(!w && c) {
                	((CustomItem) item4).showNotify();
                } else if(w && !c) {
                	((CustomItem) item4).hideNotify();
                }
            }*/
            item4.shownOnForm = c;
        }
        int h = Screen.fontHeight4;
        for (int k = 0; k < super.items.size(); ++k) {
            final Item item5;
            if ((item5 = ((Item)super.items.get(k))).shownOnForm) {
                item5.layout();
                item5.bounds[Y] = h;
                h += item5.bounds[H];
                item5.shownOnForm = BoundsUtils.collides2(super.bounds, item5.bounds);
                /*if(!item5.shownOnForm && item5 instanceof CustomItem) {
                	((CustomItem) item5).hideNotify();
                }*/
            }
        }
    }
    
    protected void sizeChanged(final int w, final int h) {
		this.bounds = new int[] { 0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4 };
    	layout();
	}
}
