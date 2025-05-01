package javax.microedition.lcdui;

import emulator.lcdui.ITextBoxImpl;
import emulator.lcdui.TextBoxSWT;

public class TextBox extends Screen {
	
	ITextBoxImpl impl;

	public TextBox(String title, String text, int maxSize, int constraints) {
		super(title);
		impl = new TextBoxSWT(title, text, maxSize, constraints);
	}

	protected void focusCaret() {
		impl.focusCaret();
	}

	/**
	 * Get current caret position.
	 *
	 * @return current caret position
	 */
	public int getCaretPosition()
	{
		return impl.getCaretPosition();
	}

	/**
	 * Returns String with the content of TextBox.
	 *
	 * @return String with TexBox content
	 */
	public String getString()
	{
		return impl.getString();
	}

	/**
	 * Set new text into TextBox. Old content is substituted with newTxt
	 *
	 * @param newText - String to set into TextBox
	 */
	public void setString(String newText)
	{
		impl.setString(newText);
	}

	/**
	 * Copies TextBox content into char[] charData.
	 *
	 * @param charData array where to copy TextBox content
	 * @return number of copied characters.
	 */
	public int getChars(char[] charData)
	{
		return impl.getChars(charData);
	}

	/**
	 * Set data from char[] array into TextBox. Previous content from TextBox is
	 * substituted. Behavior is quite the same as TextBox.SetString().
	 *
	 * @param charData array of chars from where to copy.
	 * @param offset start index in charData.
	 * @param length how many characters to copy.
	 */
	public void setChars(char[] charData, int offset, int length)
	{
		impl.setChars(charData, offset, length);
	}

	/**
	 * Inserts text into specified position.
	 *
	 * @param text text to insert, must not be null.
	 * @param position where to insert.
	 */
	public void insert(String text, int position)
	{
		impl.insert(text, position);
	}

	/**
	 * Inserts into TextBox range of characters from []charData array.
	 *
	 * @param charData array of chars to copy from.
	 * @param offset start index in array to copy from.
	 * @param length number of characters to copy.
	 * @param position in TextBox where to insert.
	 */
	public void insert(char[] charData, int offset, int length, int position)
	{
		if(charData == null)
		{
			throw new NullPointerException();
		}
		String extractedString = null;
		try
		{
			extractedString = new String(charData, offset, length);
		}
		catch(IndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		impl.insert(extractedString, position);
	}

	/**
	 * Delete range of characters from TextBox.
	 *
	 * @param offset - start index in TextBox to delete from.
	 * @param length number of characters to delete.
	 */
	public void delete(int offset, int length)
	{
		impl.delete(offset, length);
	}

	/**
	 * get number if characters TextBox can contain.
	 *
	 * @return number of characters allowed for the TextBox.
	 */
	public int getMaxSize()
	{
		return impl.getMaxSize();
	}

	/**
	 * Set Max number of characters. The actual maximum size
	 * may be less then newMaxSize due to platform limitations.
	 *
	 * @param newMaxSize sets the capacity of TextBox.
	 * @return maxSize that was set.
	 */
	public int setMaxSize(int newMaxSize)
	{
		return impl.setMaxSize(newMaxSize);
	}

	/**
	 * Get number of characters in the TextBox.
	 *
	 * @return number if inputed Characters.
	 */
	public int size()
	{
		return impl.size();
	}

	/**
	 * Set constraint for the TextBox.
	 *
	 * @param newConstraints constraint to apply to TextBox
	 */
	public void setConstraints(int newConstraints)
	{
		impl.setConstraints(newConstraints);
	}

	/**
	 * Get Current applied constraints.
	 *
	 * @return current applied constraints
	 */
	public int getConstraints()
	{
		return impl.getConstraints();
	}

	/**
	 * Set initial input mode.
	 *
	 * @param inputMode input mode to set.
	 */
	public void setInitialInputMode(String inputMode)
	{
		impl.setInitialInputMode(inputMode);
	}

	protected void _paint(final Graphics graphics) {
		impl.paint(graphics);
	}

	protected void layout() {
		impl.layout();
	}

	protected void defocus() {
		impl.defocus();
	}

	public void _swtShown() {
		impl.swtShown();
	}

	public void _swtHidden() {
		impl.swtHidden();
	}

	public void _swtUpdateSizes() {
		impl.swtUpdateSizes();
	}

	public Object _getSwtContent() {
		return impl.getSwtContent();
	}

}