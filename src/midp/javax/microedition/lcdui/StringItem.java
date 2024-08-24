package javax.microedition.lcdui;

import emulator.lcdui.*;
import org.eclipse.swt.graphics.Point;

public class StringItem extends Item {
	/**
	 * If StringItem is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_FONT = UPDATE_ITEM_MAX << 1;
	static final int UPDATE_TEXT = UPDATE_ITEM_MAX << 2;

	private String text;
	private int appearanceMode;
	private Font font;

	public StringItem(final String label, final String text) {
		this(label, text, 0);
	}

	public StringItem(String label, String txt, int appMode)
	{
		super(label);
		if((appMode != Item.PLAIN) && (appMode != Item.BUTTON)
				&& (appMode != Item.HYPERLINK))
		{
			throw new IllegalArgumentException();
		}

		appearanceMode = appMode;
		setText(txt);
		font = Font.getDefaultFont();
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String newTxt) {
		if(newTxt == null)
		{
			text = null;
		}
		else
		{
			text = newTxt;
		}
		updateParent(UPDATE_TEXT);
	}

	public int getAppearanceMode() {
		return this.appearanceMode;
	}

	public void setFont(final Font f) {
		font = (f == null ? Font.getDefaultFont() : f);
		updateParent(UPDATE_FONT);
	}

	public Font getFont() {
		return this.font;
	}

	public void setPreferredSize(final int n, final int n2) {
		super.setPreferredSize(n, n2);
	}

	boolean isFocusable()
	{
		return (getNumCommands() > 0);
	}

	protected void paint(final Graphics g) {
	}

	protected void layout() {
	}

	public void addCommand(Command command)
	{
		super.addCommand(command);

		if(getCommands().size() == 1)
		{
			updateParent(UPDATE_SIZE_CHANGED);
		}
	}

	public void removeCommand(Command command)
	{
		super.removeCommand(command);

		if(getCommands().size() == 0)
		{
			updateParent(UPDATE_SIZE_CHANGED);
		}
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	Point calculateMinimumSize()
	{
		return StringItemLayouter.calculateMinimumBounds(this);
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	Point calculatePreferredSize()
	{
		return StringItemLayouter.calculatePreferredBounds(this);
	}

	/**
	 * Return layout with optional custom flags.
	 *
	 * @return layout directive
	 */
	int internalGetLayout()
	{
		if(hasLabel() && !isSizeLocked())
		{
			return super.internalGetLayout() | Item.LAYOUT_NEWLINE_BEFORE;
		}
		else
		{
			return super.internalGetLayout();
		}
	}
}
