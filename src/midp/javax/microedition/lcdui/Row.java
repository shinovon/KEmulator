package javax.microedition.lcdui;

import java.util.ArrayList;

class Row {
	ArrayList<RowItem> items = new ArrayList<RowItem>();
	int width;
	int height;
	int y;
	int dir;

	Row(int y, int dir) {
		this.y = y;
		this.dir = dir;
	}

	void paint(Graphics g, int y, int w) {
		int x = dir == Item.LAYOUT_RIGHT ? w - width : dir == Item.LAYOUT_CENTER ? (w - width) / 2 : 0;
		int rowHeight = height;
		for (int i = 0, l = items.size(); i < l; ++i) {
			RowItem o = items.get(i);
			Item item = o.item;
			int availableWidth = w - x,
				itemWidth = o.getWidth(availableWidth),
				itemHeight = rowHeight,
				itemy = y;

			// vertical align
			if (!item._hasLayout(Item.LAYOUT_VEXPAND)) {
				if ((itemHeight = o.getHeight()) != rowHeight) {
					if (item._hasLayout(Item.LAYOUT_VCENTER)) {
						itemy += (rowHeight - itemHeight) / 2;
					} else if (!item._hasLayout(Item.LAYOUT_TOP)) {
						// bottom by default
						itemy += rowHeight - itemHeight;
					}
				}
			}

//			if (i == l - 1 && item.hasLayout(Item.LAYOUT_CENTER)) {
//				x += (availableWidth - itemWidth) / 2;
//			}

			o.x = x;
			o.y = itemy - y;
			o.width = itemWidth;
			o.height = itemHeight;

			item.hidden = false;

			item.paint(g, x, itemy, itemWidth, itemHeight, o.row);
			x += itemWidth;
		}
	}

	int getHeight() {
		return height;
	}

	int getWidth(int available) {
		int x = 0;
		for (RowItem o: items) {
			x += o.getWidth(available - x);
		}
		return x;
	}

	int getAvailableWidth(int max) {
		return max - getWidth(max);
	}

	void add(Item item, int maxWidth) {
		RowItem o;
		items.add(o = new RowItem(item, 0, width));
		width += o.getWidth(maxWidth - width);
		int h = o.getHeight();
		if (h > height) height = h;
	}

	void add(Item item, int itemRow, int maxWidth) {
		RowItem o;
		items.add(o = new RowItem(item, itemRow, width));
		width += o.getWidth(maxWidth - width);
		int h = o.getHeight();
		if (h > height) height = h;
	}

	boolean canAdd(Item item, int maxWidth) {
		if (items.isEmpty()) return true;
		Item lastItem = items.get(items.size() - 1).item;
		return !lastItem._hasLayout(Item.LAYOUT_EXPAND)
				&& !lastItem._hasLayout(Item.LAYOUT_NEWLINE_AFTER)
				&& width != maxWidth && width + item.getMinimumWidth() < maxWidth;
	}

	boolean contains(Item item) {
		for (RowItem o: items) {
			if (o.item == item) return true;
		}
		return false;
	}

	Item getFirstItem() {
		try {
			return items.get(0).item;
		} catch (Exception e) {
			return null;
		}
	}

	public void hidden() {
		for (RowItem o: items) {
			Item item = o.item;
			if (!item.hidden) {
				item.hidden();
			}
		}
	}

	public Item getFirstFocusableItem() {
		for (RowItem o: items) {
			Item item = o.item;
			if (item.isFocusable()) return item;
		}
		return null;
	}

	public int indexOf(Item item) {
		for (int i = 0, l = items.size(); i < l; i++) {
			if (items.get(i).item == item) return i;
		}
		return -1;
	}

	static class RowItem {
		Item item;
		int row;

		int x, y, width, height;

		RowItem(Item item, int i, int x) {
			this.item = item;
			this.row = i;
			this.x = x;
		}

		int getWidth(int available) {
			int w;
			if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() != Item.BUTTON && !item.isSizeLocked()) {
				w = ((StringItem) item).getRowWidth(row);
			} else {
				w = item.getPreferredWidth();
			}
			if (w > available) {
				return available;
			}
			if (w == -1 || item._hasLayout(Item.LAYOUT_EXPAND)) {
				return available;
			}
			return w;
		}

		int getHeight() {
			if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() != Item.BUTTON && !item.isSizeLocked()) {
				return item.getMinimumHeight();
			}
			return item.getPreferredHeight();
		}

		public String toString() {
			return "{RowItem:" + item + "}";
		}

	}
}


