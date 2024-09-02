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
	private int currentIndexInRow;
	private Item scrollCurrentItem;
	private Item scrollTargetItem;
	private int lastScrollDirection;

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
			Item item = (Item) super.items.get(n);
			if (item == focusedItem) focusedItem = null;
			if (item == scrollCurrentItem) scrollCurrentItem = null;
			if (item == scrollTargetItem) scrollTargetItem = null;
			item.screen = null;
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
		scrollTargetItem = scrollCurrentItem = focusedItem = null;
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
		if (scrollTargetItem == null && scrollCurrentItem == null) {
			scrollTargetItem = getFirstVisibleAndFocusableItem();
		}
		if (focusedItem != null && focusedItem instanceof CustomItem && ((CustomItem) focusedItem).callTraverse(key)) {
			return;
		}
		int height = bounds[H];

		Row currentRow = getFirstRow(scrollCurrentItem);

		switch (key) {
			case Canvas.UP:
				if (focusedItem != null && focusedItem.keyScroll(key, repeat)) {
					break;
				}
				currentIndexInRow = 0;
				if (lastScrollDirection != key) {
					scrollTargetItem = null;
					lastScrollDirection = key;
				}
				if (scrollTargetItem != null && isVisible(scrollTargetItem)) {
					focusItem(scrollTargetItem);
					scrollCurrentItem = scrollTargetItem;
					scrollTargetItem = null;
					break;
				}
				if (scrollCurrentItem != null && scrollTargetItem == null) {
					scrollTargetItem = getNextFocusableItem(scrollCurrentItem, -1);
				}
				if (scrollTargetItem != null && isVisible(scrollTargetItem)) {
					focusItem(scrollTargetItem);
					scrollCurrentItem = scrollTargetItem;
					scrollTargetItem = null;
					break;
				}
				scroll = Math.max(scroll - height / 8, 0);
				break;
			case Canvas.DOWN:
				if (focusedItem != null && focusedItem.keyScroll(key, repeat)) {
					break;
				}
				currentIndexInRow = 0;
				if (lastScrollDirection != key) {
					scrollTargetItem = null;
					lastScrollDirection = key;
				}
				if (scrollTargetItem != null && isVisible(scrollTargetItem)) {
					focusItem(scrollTargetItem);
					scrollCurrentItem = scrollTargetItem;
					scrollTargetItem = null;
					break;
				}
				if (scrollCurrentItem != null && scrollTargetItem == null) {
					scrollTargetItem = getNextFocusableItem(scrollCurrentItem, 1);
				}
				if (scrollTargetItem != null && isVisible(scrollTargetItem)) {
					focusItem(scrollTargetItem);
					scrollCurrentItem = scrollTargetItem;
					scrollTargetItem = null;
					break;
				}
				scroll = Math.min(scroll + height / 8, layoutHeight - height + bounds[Y]);
				break;
			case Canvas.LEFT:
				if (focusedItem != null && focusedItem.keyScroll(key, repeat)) {
					break;
				}
				if (currentRow != null && currentRow.items.size() > 1 && currentIndexInRow > 0) {
					int i = getNextFocusableItemInRow(currentRow, currentIndexInRow, -1);
					if (i != -1) {
						currentIndexInRow = i;
						focusItem(currentRow.items.get(i).item);
						break;
					}
				}
				keyScroll(Canvas.UP, repeat);
				return;
			case Canvas.RIGHT:
				if (focusedItem != null && focusedItem.keyScroll(key, repeat)) {
					break;
				}
				if (currentRow != null && currentRow.items.size() > 1 && currentIndexInRow < currentRow.items.size()) {
					int i = getNextFocusableItemInRow(currentRow, currentIndexInRow, 1);
					if (i != -1) {
						currentIndexInRow = i;
						focusItem(currentRow.items.get(i).item);
						break;
					}
				}
				keyScroll(Canvas.DOWN, repeat);
				return;
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
			for (Row.RowItem o: row.items) {
				if (o.x <= x && o.x + o.width >= x && o.y <= ry && o.y + o.height >= ry) {
					Item item = o.item;
					if (item.isFocusable()) {
						focusItem(item);
						focusedItem.pointerPressed(x, ry);
						repaintScreen();
					}
					return true;
				}
			}
		} while ((row = getNextRow(null, row)) != null);
		return false;
	}

	private void focusItem(Item item) {
		if (focusedItem == item) return;
		if (focusedItem != null) {
			focusedItem.defocus();
		}
		try {
			currentIndexInRow = getFirstRow(item).indexOf(item);
		} catch (Exception ignored) {}
		scrollCurrentItem = item;
		focusedItem = item;
		if (item != null) {
			item.focus();
		}
	}

	void setCurrentItem(Item item) {
		focusItem(item);
		if (!isVisible(item)) scrollTo(item);
	}

	private Row getFirstVisibleRow() {
		for (Row row : rows) {
			if (row.y + row.getHeight() - scroll > 0) return row;
		}
		return null;
	}

	private Item getFirstVisibleAndFocusableItem() {
		try {
			return getNextFocusableRow(null, 1).getFirstFocusableItem();
		} catch (Exception e) {
			return null;
		}
	}

	private Row getNextFocusableRow(Row prevRow, int dir) {
		try {
			for (int i = (prevRow == null ? rows.indexOf(getFirstVisibleRow()) : rows.indexOf(prevRow) + dir); i < rows.size() && i >= 0; i += dir) {
				Row row = rows.get(i);
				for (Row.RowItem o: row.items) {
					if (o.item.isFocusable()) return row;
				}
			}
		} catch (Exception ignored) {}
		return null;
	}

	private int getNextFocusableItemInRow(Row row, int currentIdx, int dir) {
		try {
			for (int i = currentIdx + dir; i < row.items.size() && i >= 0; i += dir) {
				Item item = row.items.get(i).item;
				if (item.isFocusable()) return i;
			}
		} catch (Exception ignored) {}
		return -1;
	}

	private Item getNextFocusableItem(Item prevItem, int dir) {
		try {
			for (int i = (getFirstRowIdx(prevItem) + dir); i < rows.size() && i >= 0; i += dir) {
				Row row = rows.get(i);
				for (Row.RowItem o: row.items) {
					if (o.item != prevItem && o.item.isFocusable()) {
						return o.item;
					}
				}
			}
		} catch (Exception ignored) {}
		return null;
	}

	public int size() {
		return super.items.size();
	}

	protected void paint(final Graphics g) {
		synchronized(items) {
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

			if (focusedItem == null && scrollCurrentItem == null && scrollTargetItem == null) {
				Item item = getFirstVisibleAndFocusableItem();
				if (item != null) {
					focusItem(item);
				}
			} else if (focusedItem != null && scrollCurrentItem == null && !isVisible(focusedItem)) {
				scrollTo(focusedItem);
			}
			scroll = Math.max(0, Math.min(scroll, layoutHeight - h + bounds[Y]));

			y -= scroll;
			for (Row row : rows) {
				int rh = row.getHeight();
				if (y + rh > 0 && h > y) {
					row.paint(g, y, w);
				} else row.hidden();
				y += rh;
			}
		}
	}

	Row getFirstRow(Item item) {
		for (Row row : rows) {
			if (row.contains(item)) return row;
		}
		return null;
	}

	int getFirstRowIdx(Item item) {
		for (int i = 0, l = rows.size(); i < l; ++i) {
			if (rows.get(i).contains(item)) return i;
		}
		return -1;
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

	boolean isVisible(Item item) {
//		Row row = getFirstRow(item);
//		if (row == null) return false;
//		do {
//			if (isVisible(row)) {
//				return true;
//			}
//		} while ((row = getNextRow(item, row)) != null);
//		return false;
		return !item.hidden;
	}

	boolean isVisible(Row row) {
		if (row == null) return false;
		return row.y + row.height - scroll > 0 && bounds[H] > row.y - scroll;
	}

	void scrollTo(Item item) {
		scrollCurrentItem = item;
		try {
			scroll = getFirstRow(item).y;
		} catch (Exception ignored) {}
	}

	void queueLayout(int i) {
		layoutStart = Math.min(layoutStart, i);
		layout = true;
		repaintScreen();
	}

	void queueLayout(Item item) {
		if (item == null) return;
		int i = items.indexOf(item);
		if (i == -1) return;
		queueLayout(i);
	}

	synchronized void doLayout(int i) {
		synchronized (items) {
			layoutHeight = 0;
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
				if (row == null || !((item instanceof StringItem
						|| item instanceof ImageItem
						|| item instanceof Spacer
						|| item instanceof CustomItem)
						|| (item.hasLayout(Item.LAYOUT_2) && !(item instanceof DateField)))
						|| item.hasLayout(Item.LAYOUT_NEWLINE_BEFORE)
						|| item.hasLayout(Item.LAYOUT_CENTER)
						|| !row.canAdd(item, width)) {
					row = newRow(row);
				}
				String text = null;
				if(item instanceof StringItem
						&& (text = ((StringItem) item).getText()) != null
						&& !text.trim().isEmpty() && text.startsWith("\n")) {
					row = newRow(row);
				}
				item.layout(row);
				if (item instanceof StringItem
						&& ((StringItem) item).getAppearanceMode() != Item.BUTTON && !item.isSizeLocked()) {
					StringItem s = (StringItem) item;
					int l = s.getRowsCount();
					for (int k = 0; k < l; k++) {
						row.add(s, k, width);
						if (k != l - 1) row = newRow(row);
					}
				} else {
					row.add(item, width);
				}
				if((text != null && text.endsWith("\n")) || item.hasLayout(Item.LAYOUT_NEWLINE_AFTER)) {
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
