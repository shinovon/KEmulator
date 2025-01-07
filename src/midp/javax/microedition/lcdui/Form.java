package javax.microedition.lcdui;

import java.util.ArrayList;

public class Form extends Screen {
	ItemStateListener itemStateListener;
	private final ArrayList<Row> rows = new ArrayList<Row>();
	private int layoutStart;
	private boolean layout;
	private int layoutHeight;
	private int currentIndexInRow;
	private Item scrollCurrentItem;
	private Item scrollTargetItem;
	private int lastScrollDirection;
	private int currentDir;
	private boolean pointerGrabbed;
	private boolean hasGauges;

	public Form(final String s) {
		this(s, null);
	}

	public Form(final String s, final Item[] array) {
		super(s);
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
			Item prev = (Item) super.items.get(n);
			if (prev == focusedItem) focusedItem = null;
			if (prev == scrollCurrentItem) scrollCurrentItem = null;
			if (prev == scrollTargetItem) scrollTargetItem = null;
			prev.screen = null;
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

	protected synchronized void _keyScroll(int key, boolean repeat) {
		if (rows.size() == 0) {
			return;
		}
		if (scrollTargetItem == null && scrollCurrentItem == null) {
			scrollTargetItem = getFirstVisibleAndFocusableItem();
		}
		if (focusedItem != null && focusedItem instanceof CustomItem && ((CustomItem) focusedItem).callTraverse(key)) {
			return;
		}
		if (scrollCurrentItem != null && focusedItem == null) {
			focusItem(scrollCurrentItem);
			repaintScreen();
			return;
		}
		int height = bounds[H];
		final int scrollAmount = Math.max(8, height / 6);

		Row currentRow = getFirstRow(scrollCurrentItem);

		switch (key) {
			case Canvas.UP:
				if (focusedItem != null && focusedItem.keyScroll(key, repeat)) {
					break;
				}
				currentIndexInRow = 0;
				if (lastScrollDirection != key) {
					if (scrollCurrentItem != null && !isVisible(scrollCurrentItem)) {
						scrollCurrentItem = null;
					}
					scrollTargetItem = null;
					lastScrollDirection = key;
				}
				if (scrollCurrentItem != null && scrollTargetItem == null) {
					scrollTargetItem = getNextFocusableItem(scrollCurrentItem, -1);
				}
				if (scrollTargetItem != null && isVisible(scrollTargetItem)) {
					focusItem(scrollTargetItem);
					scrollCurrentItem = scrollTargetItem;
					if (scrollTargetItem instanceof TextField || scrollTargetItem instanceof DateField) {
						if (!isTopVisible(scrollTargetItem)) {
							scroll = Math.min(getFirstRow(scrollTargetItem).y, layoutHeight - height + bounds[Y]);
						}
						scrollTargetItem = null;
						break;
					}
					if (isTopVisible(scrollTargetItem)) {
						scrollTargetItem = null;
						break;
					}
				}
				scroll = Math.max(scroll - scrollAmount, 0);

				if (scrollTargetItem != null && isVisible(scrollTargetItem) && isTopVisible(scrollTargetItem)) {
					scrollTargetItem = null;
					break;
				}
				break;
			case Canvas.DOWN:
				if (focusedItem != null && focusedItem.keyScroll(key, repeat)) {
					break;
				}
				currentIndexInRow = 0;
				if (lastScrollDirection != key) {
					if (scrollCurrentItem != null && !isVisible(scrollCurrentItem)) {
						scrollCurrentItem = null;
					}
					scrollTargetItem = null;
					lastScrollDirection = key;
				}
				if (scrollCurrentItem != null && scrollTargetItem == null) {
					scrollTargetItem = getNextFocusableItem(scrollCurrentItem, 1);
				}
				if (scrollTargetItem != null && isVisible(scrollTargetItem)) {
					focusItem(scrollTargetItem);
					scrollCurrentItem = scrollTargetItem;
					if (scrollTargetItem instanceof TextField || scrollTargetItem instanceof DateField) {
						if (!isEndVisible(scrollTargetItem)) {
							scroll = Math.min(getFirstRow(scrollTargetItem).y, layoutHeight - height + bounds[Y]);
						}
						scrollTargetItem = null;
						break;
					}
					if (isEndVisible(scrollTargetItem)) {
						scrollTargetItem = null;
						break;
					}
				}
				scroll = Math.min(scroll + scrollAmount, layoutHeight - height + bounds[Y]);

				if (scrollTargetItem != null && isVisible(scrollTargetItem) && isEndVisible(scrollTargetItem)) {
					scrollTargetItem = null;
					break;
				}
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
				_keyScroll(Canvas.UP, repeat);
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
				_keyScroll(Canvas.DOWN, repeat);
				return;
		}
		repaintScreen();
	}

	public boolean _invokePointerPressed(final int x, int y) {
		if (super._invokePointerPressed(x, y)) return true;
		int[] r = new int[2];
		Item item = _getItemAt(x, y, r);
		if (item != null && item.isFocusable()) {
			focusItem(item);
			pointerGrabbed = true;
			focusedItem.pointerPressed(r[0], r[1]);
			repaintScreen();
		}
		return false;
	}

	public void _invokePointerDragged(int x, int y) {
		if (!pointerGrabbed) return;
		int[] r = new int[2];
		Item item = _getItemAt(x, y, r);
		if (focusedItem == item && item instanceof CustomItem) {
			((CustomItem) focusedItem).pointerDragged(r[0], r[1]);
		}
	}

	public void _invokePointerReleased(int x, int y) {
		if (!pointerGrabbed) return;
		pointerGrabbed = false;
		int[] r = new int[2];
		Item item = _getItemAt(x, y, r);
		if (focusedItem == item && item instanceof CustomItem) {
			((CustomItem) focusedItem).pointerReleased(r[0], r[1]);
			repaintScreen();
		}
	}

	public Item _getItemAt(int x, int y, int[] transformed) {
		if ((y -= bounds[Y]) < 0) return null;
		int height = bounds[H];
		Row row = getFirstVisibleRow();
		if (row == null) return null;
		do {
			if (row.y + row.height < y + scroll) continue;
			if (height < row.y - scroll) return null;
			int ry = y + scroll - row.y;
			for (Row.RowItem o: row.items) {
				if (o.x <= x && o.x + o.width >= x && o.y <= ry && o.y + o.height >= ry) {
					if (transformed != null) {
						transformed[0] = x - o.x;
						transformed[1] = ry - o.y;
					}
					return o.item;
				}
			}
		} while ((row = getNextRow(null, row)) != null);
		return null;
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

	protected void _paint(final Graphics g) {
		if (rows.size() == 0) {
			doLayout(0);
		} else if (layout) {
			doLayout(layoutStart);
		}
		layout = false;
		layoutStart = Integer.MAX_VALUE;
		int y = bounds[Y],
				w = bounds[W],
				h = bounds[H],
				sy = y;
		g.setClip(0, sy, w, sy+h);

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
			if (y + rh > 0 && h + sy > y) {
				row.paint(g, y, w);
			} else row.hidden();
			y += rh;
		}
	}

	Row getFirstRow(Item item) {
		for (Row row : rows) {
			if (row != null && row.contains(item))
				return row;
		}
		return null;
	}

	Row getLastRow(Item item) {
		Row row = getFirstRow(item);
		if (row != null) {
			for (int i = rows.indexOf(row); i < rows.size(); i++) {
				Row tmp = rows.get(i);
				if (!tmp.contains(item)) break;
				row = tmp;
			}
		}
		return row;
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
		synchronized (rows) {
			Row row = getFirstRow(item);
			if (row == null) return false;
			do {
				if (isVisible(row)) {
					return true;
				}
			} while ((row = getNextRow(item, row)) != null);
		}
		return false;
	}

	boolean isTopVisible(Item item) {
		Row row = getFirstRow(item);
		if (row == null) return false;
		return row.y - scroll >= 0 && bounds[H] > row.y - scroll;
	}

	boolean isEndVisible(Item item) {
		Row row = getLastRow(item);
		if (row == null) return false;
		return row.y + row.height - scroll > 0 && bounds[H] > row.y + row.height - scroll;
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
		synchronized (rows) {
			layoutHeight = 0;
			if (items.isEmpty()) {
				rows.clear();
				return;
			}
			int width = bounds[W];
			Row row = null;
			synchronized (items) {
				while (row == null && i > 0) {
					if (i < items.size()) {
						row = getFirstRow((Item) items.get(i));
					}
					if (row == null) i--;
				}
				if (i == 0) {
					rows.clear();
					row = null;
				} else if (row != null) {
					int rowIdx = rows.indexOf(row);
					if (row.items.size() > 1) {
						i = items.indexOf(row.getFirstItem());
					}
					while (rows.size() > rowIdx) {
						rows.remove(rowIdx);
					}
				}
				if (!rows.isEmpty()) {
					for (Row r : rows) {
						layoutHeight += r.getHeight();
					}
				}
			}

			currentDir = row != null ? row.dir : Item.LAYOUT_LEFT;
			row = null;
			for (int j = i; j < items.size(); j++) {
				Item item = (Item) items.get(j);
				if (item instanceof Gauge) hasGauges = true;
				int itemDir = item.layout & (Item.LAYOUT_CENTER);
				if (itemDir == 0) {
					itemDir = currentDir;
				}
				if (row == null || !((item instanceof StringItem
						|| item instanceof ImageItem
						|| item instanceof Spacer
						|| item instanceof CustomItem)
						|| (item._hasLayout(Item.LAYOUT_2)
						&& !(item instanceof DateField
						|| item instanceof TextField
						|| item instanceof ChoiceGroup)))
						|| item._hasLayout(Item.LAYOUT_NEWLINE_BEFORE)
						|| (itemDir != currentDir)) {
					currentDir = itemDir;
					row = newRow(row);
				}
				String text = null;
				if(item instanceof StringItem
						&& (((text = ((StringItem) item).getText()) != null
						&& !text.trim().isEmpty() && text.startsWith("\n"))
						|| ((StringItem) item).getAppearanceMode() != Item.BUTTON && item.hasLabel())) {
					row = newRow(row);
				}
				item.layout(row);
				// has to be checked after item size calculated
				if (!row.canAdd(item, width)) {
					row = newRow(row);
					item.layout(row);
				}
				if (item instanceof StringItem
						&& ((StringItem) item).getAppearanceMode() != Item.BUTTON && !item.isSizeLocked() && !item.hasLabel()) {
					StringItem s = (StringItem) item;
					int l = s.getRowsCount();
					for (int k = 0; k < l; k++) {
						row.add(s, k, width);
						if (k != l - 1) row = newRow(row);
					}
				} else {
					row.add(item, width);
				}
				if((text != null && text.endsWith("\n"))
						|| item._hasLayout(Item.LAYOUT_NEWLINE_AFTER)
						|| item instanceof ChoiceGroup
						|| item instanceof TextField
						|| item instanceof DateField) {
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
			rows.add(row = new Row(layoutHeight, currentDir));
		} else row.dir = currentDir;
		return row;
	}

	public void _showMenu(Item item, int x, int y) {
		if (item == null) {
			hideSwtMenu();
			return;
		}
		int[] l = getLocationOnScreen(item);
		showSwtMenu(true, x < 0 ? x : l[0] + x, y < 0 ? y : l[1] + y);
	}

	int[] getLocationOnScreen(Item item) {
		int x = 0, y = 0;
		try {
			Row row = getFirstRow(item);
			Row.RowItem o = row.items.get(row.indexOf(item));
			x = o.x;
			y = row.y + bounds[Y] + o.y - scroll;
		} catch (Exception ignored) {}
		return new int[] {x, y};
	}

	public void _invokeSizeChanged(int w, int h) {
		super._invokeSizeChanged(w, h);
		queueLayout(0);
	}

	public void _itemStateChanged(Item item) {
		if (itemStateListener == null) return;
		itemStateListener.itemStateChanged(item);
	}

	void repaintScreen(Item item) {
		if (isVisible(item)) {
			repaintScreen();
		}
	}

	public int _repaintInterval() {
		return hasGauges || ticker != null ? 500 : -1;
	}
}
