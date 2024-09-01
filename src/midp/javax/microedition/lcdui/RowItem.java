package javax.microedition.lcdui;

class RowItem {
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
		if (w == -1 || item.hasLayout(Item.LAYOUT_EXPAND)) {
			return available;
		}
		return w;
	}

	int getHeight() {
		if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() != Item.BUTTON) {
			return item.getMinimumHeight();
		}
		return item.getPreferredHeight();
	}

}
