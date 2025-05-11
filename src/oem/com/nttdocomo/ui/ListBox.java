package com.nttdocomo.ui;

public final class ListBox extends Component implements Interactable {
	public static final int SINGLE_SELECT = 0;
	public static final int RADIO_BUTTON = 1;
	public static final int CHECK_BOX = 2;
	public static final int NUMBERED_LIST = 3;
	public static final int MULTIPLE_SELECT = 4;
	public static final int CHOICE = 5;

	public ListBox(final int n) {
	}

	public ListBox(final int n, final int n2) {
	}

	public void setItems(final String[] array) {
	}

	public void append(final String s) {
	}

	public void removeAll() {
	}

	public int getItemCount() {
		return 0;
	}

	public String getItem(final int n) {
		return null;
	}

	public void select(final int n) {
	}

	public void deselect(final int n) {
	}

	public boolean isIndexSelected(final int n) {
		return false;
	}

	public int getSelectedIndex() {
		return 0;
	}

	public void setEnabled(final boolean b) {
	}

	public void requestFocus() {
	}
}
