package javax.microedition.lcdui;

import java.util.ArrayList;

class Row {
	ArrayList<RowItem> items = new ArrayList<RowItem>();
	int width = 0;
	int height = 0;

	void paint(Graphics g, int y, int w) {
		int x = 0;
		int rowHeight = height;
//		System.out.println("+ROW " + this + " Y: " + y);
		for (int i = 0, l = items.size(); i < l; ++i) {
			RowItem o = items.get(i);
			Item item = o.item;
			int availableWidth = w - x,
				itemWidth = o.getWidth(availableWidth),
				itemHeight = rowHeight,
				itemy = y;

			// vertical align
			if (!item.hasLayout(Item.LAYOUT_VEXPAND)) {
				if ((itemHeight = o.getHeight()) != rowHeight) {
					if (item.hasLayout(Item.LAYOUT_VCENTER)) {
						itemy += (rowHeight - itemHeight) / 2;
					} else if (!item.hasLayout(Item.LAYOUT_TOP)) {
						// bottom by default
						itemy += rowHeight - itemHeight;
					}
				}
			}

			// horizontal align
			if (i == l - 1) {
				if (item.hasLayout(Item.LAYOUT_CENTER)) {
					x += (availableWidth - itemWidth) / 2;
				} else if (item.hasLayout(Item.LAYOUT_RIGHT)) {
					x = Math.max(x, w - itemWidth);
				}
			}

//			System.out.println(" ITEM " + item + " X: " + x + " W: " + itemWidth + " R: " + o.row);
			item.paint(g, x, itemy, itemWidth, itemHeight, o.row);
			x += itemWidth;
		}
//		System.out.println("-ROW " + this + " Y: " + y);
//		System.out.println();
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

	void add(Item item) {
		items.add(new RowItem(item, 0));
		width += item.getPreferredWidth();
		int h = item.getPreferredHeight();
		if (h > height) height = h;
	}

	void add(RowItem o, int maxWidth) {
		items.add(o);
		width += o.getWidth(width - maxWidth);
		int h = o.getHeight();
		if (h > height) height = h;
	}

	boolean canAdd(Item item, int maxWidth) {
		if (items.isEmpty()) return true;
		Item lastItem = items.get(items.size() - 1).item;
		return !lastItem.hasLayout(Item.LAYOUT_EXPAND)
				&& !lastItem.hasLayout(Item.LAYOUT_NEWLINE_AFTER)
				&& width != maxWidth && width + item.getMinimumWidth() < maxWidth;
	}

	boolean contains(Item item) {
		for (RowItem o: items) {
			return o.item == item;
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
}
