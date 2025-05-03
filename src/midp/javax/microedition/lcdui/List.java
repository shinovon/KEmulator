package javax.microedition.lcdui;

import emulator.UILocale;
import emulator.lcdui.IListImpl;
import emulator.lcdui.ListSWT;

public class List extends Screen implements Choice {
	public static final Command SELECT_COMMAND = new Command(UILocale.get("LCDUI_LIST_SELECT_COMMAND", "Select"), 1, 0);
	private IListImpl impl;

	private int type;
	private Command selectCommand = List.SELECT_COMMAND;

	public List(String s, int n) {
		this(s, n, new String[0], null);
	}

	public List(String title, int type, String[] text, Image[] img) {
		super(title);
		if (type != 2 && type != 1 && type != 3) {
			throw new IllegalArgumentException();
		}
		this.type = type;
		impl = new ListSWT(this, title, type, text, img);
	}

	public void setSelectCommand(Command cmd) {
		if (type == Choice.IMPLICIT) {
			if(cmd == null)
			{
				if(selectCommand != null)
				{
					super.removeCommand(selectCommand);
				}
			}
			else if(cmd != SELECT_COMMAND)
			{
				addCommand(cmd);
			}
			selectCommand = cmd;
		}
	}

	public void removeCommand(Command cmd)
	{
		if(cmd == selectCommand)
		{
			selectCommand = null;
		}
		super.removeCommand(cmd);
	}

	/**
	 * Append item with specified text and image.
	 *
	 * @param text the text
	 * @param img the image
	 * @return index of added item
	 */
	public int append(String text, Image img)
	{
		return impl.append(text, img);
	}

	/**
	 * Insert item with specified text and image.
	 *
	 * @param position the item index
	 * @param text the text
	 * @param img the image
	 */
	public void insert(int position, String text, Image img)
	{
		impl.insert(position, text, img);
	}

	/**
	 * Set item with specified text and image.
	 *
	 * @param position the item index
	 * @param text the text
	 * @param img the image
	 */
	public void set(int position, String text, Image img)
	{
		impl.set(position, text, img);
	}

	/**
	 * Remove item with at specified position.
	 *
	 * @param position the item index
	 */
	public void delete(int position)
	{
		impl.delete(position);
	}

	/**
	 * Remove all items.
	 */
	public void deleteAll()
	{
		impl.deleteAll();
	}

	/**
	 * Get the fit policy of this list.
	 *
	 * @return the lists fir policy
	 */
	public int getFitPolicy()
	{
		return impl.getFitPolicy();
	}

	/**
	 * Get the font used in a list item.
	 *
	 * @param position the index of the item
	 * @return the items font
	 */
	public Font getFont(int position)
	{
		return impl.getFont(position);
	}

	/**
	 * Get the image part of a list item.
	 *
	 * @param position the index of the item
	 * @return the items image part
	 */
	public Image getImage(int position)
	{
		return impl.getImage(position);
	}

	/**
	 * Get the string part of a list item.
	 *
	 * @param position the index of the item
	 * @return the items string part
	 */
	public String getString(int position)
	{
		return impl.getString(position);
	}

	/**
	 * Get selected flags.
	 *
	 * @param selectedArray an array with selected items
	 * @return selected flags
	 */
	public int getSelectedFlags(boolean[] selectedArray)
	{
		return impl.getSelectedFlags(selectedArray);
	}

	/**
	 * Returns the selected index.
	 *
	 * @return the selected index
	 */
	public int getSelectedIndex()
	{
		return impl.getSelectedIndex();
	}

	/**
	 * Returns if the specified element is selected.
	 *
	 * @param position specified element index
	 * @return true if its selected, false otherwise
	 */
	public boolean isSelected(int position)
	{
		return impl.isSelected(position);
	}

	public void setFitPolicy(int newFitPolicy)
	{
		impl.setFitPolicy(newFitPolicy);
	}

	public void setFont(int n, Font font) {
		impl.setFont(n, font);
	}

	/**
	 * Set selected flags.
	 *
	 * @param selectedArray an array with selected items
	 */
	public void setSelectedFlags(boolean[] selectedArray)
	{
		impl.setSelectedFlags(selectedArray);
	}

	/**
	 * Set selected index.
	 *
	 * @param position the index of the item
	 * @param select selected or not
	 */
	public void setSelectedIndex(int position, boolean select)
	{
		impl.setSelectedIndex(position, select);
	}

	/**
	 * Returns the size of the list.
	 *
	 * @return the lists size
	 */
	public int size()
	{
		return impl.size();
	}

	protected void _drawScrollBar(final Graphics graphics) {
		impl.drawScrollBar(graphics);
	}

	protected void _paint(Graphics graphics) {
		impl.paint(graphics);
	}

	protected void layout() {
		impl.layout();
	}

	public Command _getSelectCommand() {
		return selectCommand;
	}

	public boolean _isSWT() {
		return impl.isSWT();
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