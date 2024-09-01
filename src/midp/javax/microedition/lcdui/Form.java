package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.ui.IScreen;

import java.util.ArrayList;

public class Form extends Screen {
	ItemStateListener itemStateListener;
	private ArrayList<Row> rows;
	private int layoutStart;
	private boolean layout;
	private int layoutHeight;
	private Row currentRow;
	private int currentIndexInRow;

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

	protected synchronized void keyScroll(int key, boolean repeat) {
		if (rows.size() == 0) {
			return;
		}
		switch (key) { // TODO
			case Canvas.UP:
				scroll -= 40;
				if (scroll < 0) scroll = 0;
				focusItem(null);
				break;
			case Canvas.DOWN:
				scroll += 40;
				focusItem(null);
				break;
			case Canvas.LEFT:
				break;
			case Canvas.RIGHT:
				break;
		}
		repaintScreen();
	}

	public boolean invokePointerPressed(final int x, int y) {
		if (super.invokePointerPressed(x, y)) return true;
		if ((y -= bounds[Y]) < 0) return false;
		int height = bounds[H];
		Row row = getFirstVisibleRow();
		if (row == null) return false;
		do {
			if (row.y + row.height < y + scroll) continue;
			if (height < row.y - scroll) return false;
			int ry = y + scroll - row.y;
			for (RowItem o: row.items) {
				if (o.x <= x && o.x + o.width >= x && o.y <= ry && o.y + o.height >= ry) {
					Item item = o.item;
					System.out.println("Hit " + o.item);
					if (item.isFocusable()) {
						System.out.println("Focused");
						if (focusedItem == item) {
							focusedItem.pointerPressed(x, ry);
						} else {
							focusItem(item);
						}
						repaintScreen();
					}
					return true;
				}
			}
		} while ((row = getNextRow(null, row)) != null);
		return false;
	}

	private void focusItem(Item item) {
		if (focusedItem != null) {
			focusedItem.defocus();
		}
		focusedItem = item;
		if (item != null) {
			item.focus();
		}
	}

	private Row getFirstVisibleRow() {
		for (Row row : rows) {
			if (row.y + row.getHeight() - scroll > 0) return row;
		}
		return null;
	}

	public int size() {
		return super.items.size();
	}

	protected void paint(final Graphics g) {
		synchronized(items) {
//			System.out.println(" == PAINT START == ");
			if (rows.size() == 0) {
				doLayout(0);
			} else if (layout) {
				doLayout(layoutStart);
			}
			layout = false;
			layoutStart = Integer.MAX_VALUE;
			int y = bounds[Y],
				w = bounds[W],
				h = bounds[H];
			g.setClip(0, y, w, h);

			if (focusedItem != null && !isVisible(focusedItem)) {
				scrollTo(focusedItem);
			}
			if (scroll > layoutHeight - h)
				scroll = layoutHeight - h;
			if (scroll < 0)
				scroll = 0;

			y -= scroll;
			for (Row row : rows) {
				int rh = row.getHeight();
				if (y + rh > 0 && h > y) {
					row.paint(g, y, w);
				} else row.hidden();
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
		if (prevRow == null) return getFirstRow(item);
		try {
			for (int i = rows.indexOf(prevRow) + 1; i < rows.size(); i++) {
				Row row = rows.get(i);
				if (item == null || row.contains(item)) return row;
			}
		} catch (Exception ignored) {}
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

	boolean isVisible(Item item) {
		Row row = getFirstRow(item);
		if (row == null) return false;
		do {
			if (isVisible(row)) return true;
		} while ((row = getNextRow(item, row)) != null);
		return false;
	}

	boolean isVisible(Row row) {
		if (row == null) return false;
		return row.y + row.height - scroll > 0 && bounds[H] > row.y - scroll;
	}

	void scrollTo(Item item) {
		try {
			scroll = getFirstRow(item).y;
		} catch (Exception ignored) {}
	}

	void queueLayout(int i) {
		System.out.println("queueLayout " + i);
		layoutStart = Math.min(layoutStart, i);
		layout = true;
		repaintScreen();
	}

	void queueLayout(Item item) {
		System.out.println("queueLayout " + item);
		if (item == null) return;
		int i = items.indexOf(item);
		if (i == -1) return;
		queueLayout(i);
	}

	synchronized void doLayout(int i) {
		System.out.println("doLayout " + i);
		synchronized (items) {
			layoutHeight = 0;
			currentRow = null;
			if (items.size() == 0) {
				rows.clear();
				return;
			}
			int width = bounds[W];
			Row row = null;
//			if (i < items.size()) {
//				row = getFirstRow((Item) items.get(i));
//			}
//			if (i == 0) {
			rows.clear();
			i = 0;
//			} else if (row != null) {
//				int rowIdx = rows.indexOf(row);
//				if (row.items.size() > 1) {
//					i = items.indexOf(row.getFirstItem());
//				}
//				while (rows.size() > rowIdx) {
//					rows.remove(rowIdx);
//				}
//			}
//			row = null;
			for (int j = i; j < items.size(); j++) {
				Item item = (Item) items.get(j);
				if (row == null) {
					row = newRow(row);
				}
				if (!((item instanceof StringItem
						|| item instanceof ImageItem
						|| item instanceof Spacer
						|| item instanceof CustomItem)
						|| (item.hasLayout(Item.LAYOUT_2) && !(item instanceof DateField)))
						|| item.hasLayout(Item.LAYOUT_NEWLINE_BEFORE)
						|| item.hasLayout(Item.LAYOUT_CENTER)
						|| !row.canAdd(item, width)) {
					row = newRow(row);
				}
				item.layout(row);
				if(item instanceof StringItem
						&& ((StringItem) item).getText() != null
						&& ((StringItem) item).getText().startsWith("\n")) {
					row = newRow(row);
				}
				if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() != Item.BUTTON && !item.isSizeLocked()) {
					StringItem s = (StringItem) item;
					int l = s.getRowsCount();
					for (int k = 0; k < l; k++) {
						row.add(s, k, width);
						if (k != l - 1) row = newRow(row);
					}
				} else {
					row.add(item, width);
				}
				if(item instanceof StringItem
						&& ((StringItem) item).getText() != null
						&& ((StringItem) item).getText().endsWith("\n")) {
					row = newRow(row);
				}
				if (item.hasLayout(Item.LAYOUT_EXPAND) || item.hasLayout(Item.LAYOUT_NEWLINE_AFTER)) {
					row = newRow(row);
				}
			}
			if (row != null && !row.items.isEmpty()) {
				layoutHeight += row.getHeight();
			}
		}
	}

	private Row newRow(Row row) {
		if (row == null || !row.items.isEmpty()) {
			if (row != null) layoutHeight += row.getHeight();
			rows.add(row = new Row(layoutHeight));
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
