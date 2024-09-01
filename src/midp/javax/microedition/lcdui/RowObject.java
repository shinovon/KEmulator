package javax.microedition.lcdui;

class RowObject {
	Item item;
	int row;

	RowObject(Item item, int i) {
		this.item = item;
		this.row = i;
	}

	int getMinimumWidth() {
		return item.getMinimumWidth();
	}

	int getWidth(int available) {
		int w;
		if (item instanceof StringItem && ((StringItem) item).getAppearanceMode() != Item.BUTTON) {
			// TODO
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
