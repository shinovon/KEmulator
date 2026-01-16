/*
Copyright (c) 2025 Arman Jussupgaliyev
*/
package emulator.lcdui;

public interface ITextBoxImpl extends IScreenImpl {
	int getCaretPosition();

	String getString();

	void setString(String newText);

	int getChars(char[] charData);

	void setChars(char[] charData, int offset, int length);

	void insert(String text, int position);

	void delete(int offset, int length);

	int getMaxSize();

	int setMaxSize(int newMaxSize);

	int size();

	void setConstraints(int newConstraints);

	int getConstraints();

	void setInitialInputMode(String inputMode);

	void defocus();

	void focusCaret();
}
