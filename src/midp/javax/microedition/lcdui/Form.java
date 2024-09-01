package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.lcdui.*;
import emulator.ui.IScreen;

import java.util.ArrayList;

public class Form extends Screen {
	ItemStateListener itemStateListener;
	private ArrayList<Row> rows;
	private int layoutStart;
	private boolean layout;

	public Form(final String s) {
		this(s, null);
	}

	public Form(final String s, final Item[] array) {
		super(s);
		rows = new ArrayList<Row>();
		if (array != null) {
			for (Item item : array) {
				if (item == null) {
					throw new NullPointerException();
				}
				if (item.screen != null) {
					throw new IllegalStateException();
				}
				super.items.add(item);
				item.screen = this;
			}
		}
		queueLayout(0);
	}

	public int append(final Item item) {
		synchronized(items) {
			if (item == null) {
				throw new NullPointerException();
			}
			if (item.screen != null) {
				throw new IllegalStateException();
			}
			super.items.add(item);
			item.screen = this;
			queueLayout(items.size() - 1);
			return super.items.size() - 1;
		}
	}

	public int append(final String s) {
		return append(new StringItem(null, s));
	}

	public int append(final Image image) {
		return append(new ImageItem(null, image, 0, null));
	}

	public void insert(final int n, final Item item) {
		if (item == null) {
			throw new NullPointerException();
		}
		if (item.screen != null) {
			throw new IllegalStateException();
		}
		synchronized(items) {
			super.items.insertElementAt(item, n);
			item.screen = this;
		}
		queueLayout(n);
	}

	public void delete(final int n) {
		if (n < 0 || n >= super.items.size()) {
			throw new IndexOutOfBoundsException();
		}
		synchronized (items) {
			((Item) super.items.get(n)).screen = null;
			super.items.remove(n);
		}
		queueLayout(n);
	}

	public void deleteAll() {
		synchronized (items) {
			for (Object item : super.items) {
				((Item) item).screen = null;
			}
			super.items.removeAllElements();
		}
		queueLayout(0);
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
		synchronized (items) {
			((Item) super.items.get(n)).screen = null;
			super.items.set(n, item);
			item.screen = this;
		}
		queueLayout(n);
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

	protected void paint(final Graphics g) {
		synchronized(items) {
//			System.out.println(" == PAINT START == ");
			synchronized (items) {
				if (rows.size() == 0) {
					doLayout(0);
				} else if (layout) {
					doLayout(layoutStart);
				}
				layout = false;
				layoutStart = Integer.MAX_VALUE;
			}
			int y = bounds[Y];
			int w = bounds[W];
			for (Row row : rows) {
				int rh = row.getHeight();
				if (y + rh > 0) {
					row.paint(g, y, w);
				} else {
					System.out.println("paint hidden " + y);
				}
				y += rh;
			}
//			System.out.println(" == PAINT END == ");
		}
	}

	Row getFirstRow(Item item) {
		for (Row row : rows) {
			if (row.contains(item)) return row;
		}
		return null;
	}

	Row getNextRow(Item item, Row prevRow) {
		for (int i = rows.indexOf(prevRow); i < rows.size(); i++) {
			Row row = rows.get(i);
			if (row.contains(item)) return row;
		}
		return null;
	}

	Row getLastRow(Item item) {
		Row row = getFirstRow(item);
		if (row != null) {
			for (int i = rows.indexOf(row); i < rows.size(); i++) {
				Row tmp = rows.get(i);
				if (row.contains(item)) row = tmp;
			}
		}
		return row;
	}

	Row getFirstRow() {
		try {
			return rows.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	Row getLastRow() {
		try {
			return rows.get(rows.size() - 1);
		} catch (Exception e) {
			return null;
		}
	}

	void queueLayout(int i) {
		System.out.println("queueLayout " + i);
		layoutStart = Math.min(layoutStart, i);
		layout = true;
	}

	void queueLayout(Item item) {
		System.out.println("queueLayout " + item);
		if (item == null) return;
		int i = items.indexOf(item);
		if (i == -1) return;
		queueLayout(i);
	}

	void doLayout(int i) {
		System.out.println("doLayout " + i);
		synchronized (items) {
			if (items.size() == 0) {
				rows.clear();
				return;
			}
			int width = bounds[W];
			Row row = null;
			if (i < items.size()) {
				row = getFirstRow((Item) items.get(i));
			}
//			if (i == 0) {
				rows.clear();
//			} else if (row != null) {
//				int rowIdx = rows.indexOf(row);
//				if (row.items.size() > 1) {
//					i = items.indexOf(row.getFirstItem());
//				}
//				while (rows.size() > rowIdx) {
//					rows.remove(rowIdx);
//				}
//			}
			row = null;
			for (int j = i; j < items.size(); j++) {
				Item item = (Item) items.get(j);
				if (row == null) {
					row = newRow(row);
				}
				if (!(item instanceof StringItem || item instanceof ImageItem || item instanceof Spacer)
						|| item.hasLayout(Item.LAYOUT_NEWLINE_BEFORE) || !row.canAdd(item, width)) {
					row = newRow(row);
				}
				item.layout(row);
				if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() != Item.BUTTON) {
					StringItem s = (StringItem) item;
					int l = s.textArr.length;
					for (int k = 0; k < l; k++) {
						row.add(new RowObject(s, k));
						if (k != l - 1) row = newRow(row);
					}
					if(s.getText() != null && s.getText().endsWith("\n")) {
						row = newRow(row);
					}
				} else {
					row.add(item);
				}
				if (item.hasLayout(Item.LAYOUT_EXPAND) || item.hasLayout(Item.LAYOUT_NEWLINE_AFTER)) {
					row = newRow(row);
				}
			}
		}
	}

	private Row newRow(Row row) {
		if (row == null || !row.items.isEmpty()) {
			rows.add(row = new Row());
		}
		return row;
	}

	protected void sizeChanged(final int w, final int h) {
		this.w = w;
		this.h = h;
		this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
		queueLayout(0);
	}

	protected void shown() {
		IScreen s = Emulator.getEmulator().getScreen();
		sizeChanged(s.getWidth(), s.getHeight());
	}
}
