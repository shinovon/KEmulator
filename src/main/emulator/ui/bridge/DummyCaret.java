package emulator.ui.bridge;

import emulator.ui.ICaret;

public class DummyCaret implements ICaret {
	@Override
	public void focusItem(Object p0, int p1, int p2) {

	}

	@Override
	public void defocusItem(Object p0) {

	}

	@Override
	public int getCaretPosition() {
		return 0;
	}

	@Override
	public void displayableChanged() {

	}

	@Override
	public void updateText(Object item, String text) {

	}

	@Override
	public void setSelection(int index, int length) {

	}

	@Override
	public String getSelection() {
		return "";
	}

	@Override
	public void setCaret(int index) {

	}
}
