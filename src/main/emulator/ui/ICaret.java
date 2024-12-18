package emulator.ui;

public interface ICaret {
	void focusItem(final Object p0, final int p1, final int p2);

	void defocusItem(final Object p0);

	int getCaretPosition();

	void displayableChanged();

    void updateText(Object item, String text);

	void setSelection(int index, int length);

	String getSelection();

	void setCaret(int index);
}
