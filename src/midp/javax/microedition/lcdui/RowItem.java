package javax.microedition.lcdui;

class RowItem {
	Item item;
	int row;

	RowItem(Item item, int i) {
		this.item = item;
		this.row = i;
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
