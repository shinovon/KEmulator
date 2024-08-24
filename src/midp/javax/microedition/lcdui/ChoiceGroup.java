package javax.microedition.lcdui;

import org.eclipse.swt.graphics.Point;

public class ChoiceGroup
		extends Item
		implements Choice {
	/**
	 * If ChoiceGroup is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_REASON_APPEND = UPDATE_ITEM_MAX << 1;
	static final int UPDATE_REASON_DELETE = UPDATE_ITEM_MAX << 2;
	static final int UPDATE_REASON_DELETEALL = UPDATE_ITEM_MAX << 3;
	static final int UPDATE_REASON_INSERT = UPDATE_ITEM_MAX << 4;
	static final int UPDATE_REASON_SET = UPDATE_ITEM_MAX << 5;
	static final int UPDATE_FITPOLICY = UPDATE_ITEM_MAX << 6;
	static final int UPDATE_FONT = UPDATE_ITEM_MAX << 7;
	static final int UPDATE_SELECTEDFLAGS = UPDATE_ITEM_MAX << 8;
	static final int UPDATE_SELECTEDINDEX = UPDATE_ITEM_MAX << 9;

	private ChoiceImpl choiceImpl;
	private int type;

	public ChoiceGroup(String label, int choiceType) {
		this(label, choiceType, new String[0], null);
	}

	public ChoiceGroup(String label, int type,
					   String[] textElements,
					   Image[] imgElements) {
		super(label);
		switch(type)
		{
			case Choice.EXCLUSIVE:
			case Choice.POPUP:
				choiceImpl = new ChoiceImpl(false);
				break;
			case Choice.MULTIPLE:
				choiceImpl = new ChoiceImpl(true);
				break;
			default:
				throw new IllegalArgumentException();
		}
		choiceImpl.check(textElements, imgElements);
		this.type = type;
		// append elements
		for(int i = 0; i < textElements.length; i++)
		{
			append(textElements[i], imgElements != null
					? imgElements[i] : null);
		}
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
		int ret = choiceImpl.append(text, img);
		updateParent(UPDATE_REASON_APPEND);
		return ret;
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
		choiceImpl.insert(position, text, img);
		updateParent(UPDATE_REASON_INSERT);
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
		choiceImpl.set(position, text, img);
		updateParent(UPDATE_REASON_SET);
	}

	/**
	 * Remove item at specified position.
	 *
	 * @param position the item index
	 */
	public void delete(int position)
	{
		choiceImpl.delete(position);
		updateParent(UPDATE_REASON_DELETE);
	}

	/**
	 * Remove all items.
	 */
	public void deleteAll()
	{
		choiceImpl.deleteAll();
		updateParent(UPDATE_REASON_DELETEALL);
	}

	/**
	 * Get the fit policy of this ChoiceGroup.
	 *
	 * @return the ChoiceGroup's fit policy
	 */
	public int getFitPolicy()
	{
		return choiceImpl.getFitPolicy();
	}

	/**
	 * Get the font used in a ChoiceGroup item.
	 *
	 * @param position the index of the item
	 * @return the items font
	 */
	public Font getFont(int position)
	{
		return choiceImpl.getFont(position);
	}

	/**
	 * Get the image part of a ChoiceGroup item.
	 *
	 * @param position the index of the item
	 * @return the items image part
	 */
	public Image getImage(int position)
	{
		return choiceImpl.getImage(position);
	}

	/**
	 * Get the string part of a ChoiceGroup item.
	 *
	 * @param position the index of the item
	 * @return the items string part
	 */
	public String getString(int position)
	{
		return choiceImpl.getString(position);
	}

	/**
	 * Get selected flags.
	 *
	 * @param selectedArray an array with selected items
	 * @return selected flags
	 */
	public int getSelectedFlags(boolean[] selectedArray)
	{
		return choiceImpl.getSelectedFlags(selectedArray);
	}

	/**
	 * Returns the selected item's index.
	 *
	 * @return the selected index
	 */
	public int getSelectedIndex()
	{
		return choiceImpl.getSelectedIndex();
	}

	/**
	 * Returns if the specified element is selected.
	 *
	 * @param position specified element index
	 * @return true if its selected, false otherwise
	 */
	public boolean isSelected(int position)
	{
		return choiceImpl.isSelected(position);
	}

	/**
	 * Set the fit policy of this ChoiceGroup.
	 *
	 * @param newFitPolicy the new fit policy
	 */
	public void setFitPolicy(int newFitPolicy)
	{
		choiceImpl.setFitPolicy(newFitPolicy);
		updateParent(UPDATE_FITPOLICY);
	}

	/**
	 * Set the font of a ChoiceGroup item.
	 *
	 * @param position the index of the item
	 * @param font the desired font
	 */
	public void setFont(int position, Font font)
	{
		choiceImpl.setFont(position, font);
		updateParent(UPDATE_FONT);
	}

	/**
	 * Set selected flags.
	 *
	 * @param selectedArray an array with selected items
	 */
	public void setSelectedFlags(boolean[] selectedArray)
	{
		choiceImpl.setSelectedFlags(selectedArray);
		updateParent(UPDATE_SELECTEDFLAGS);
	}

	/**
	 * Set selected index.
	 *
	 * @param position the index of the item
	 * @param select selected or not
	 */
	public void setSelectedIndex(int position, boolean select)
	{
		choiceImpl.setSelected(position, select);
		updateParent(UPDATE_SELECTEDINDEX);
	}

	/**
	 * Returns the size of the ChoiceGroup.
	 *
	 * @return the lists size
	 */
	public int size()
	{
		return choiceImpl.size();
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	Point calculateMinimumSize()
	{
		return ChoiceGroupLayouter.calculateMinimumBounds(this);
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	Point calculatePreferredSize()
	{
		return ChoiceGroupLayouter.calculatePreferredBounds(this);
	}

	/**
	 * Called by widget listeners to update Item value.
	 */
	void internalSetSelectedIndex(int position, boolean select)
	{
		choiceImpl.setSelected(position, select);
		// notify item state listener
		notifyStateChanged();
	}

	/**
	 * Return layout with optional custom flags.
	 *
	 * @return layout directive
	 */
	int internalGetLayout()
	{
		return super.internalGetLayout() | Item.LAYOUT_NEWLINE_BEFORE;
	}

	/**
	 * Return the ChoiceGroup type.
	 */
	final int getType()
	{
		return type;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Item#isFocusable()
	 */
	boolean isFocusable()
	{
		return true;
	}
}
