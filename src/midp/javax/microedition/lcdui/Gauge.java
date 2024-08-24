package javax.microedition.lcdui;

import org.eclipse.swt.graphics.Point;

public class Gauge extends Item {
	public static final int INDEFINITE = -1;
	public static final int CONTINUOUS_IDLE = 0;
	public static final int INCREMENTAL_IDLE = 1;
	public static final int CONTINUOUS_RUNNING = 2;
	public static final int INCREMENTAL_UPDATING = 3;
	private int maxValue;
	private int value;
	private boolean interactive;

	/**
	 * If Gauge is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_MAXVALUE = UPDATE_ITEM_MAX << 1;
	static final int UPDATE_VALUE = UPDATE_ITEM_MAX << 2;

	public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
		super(label);
		maxValue = validateMaxValue(maxValue, interactive);
		value = validateValue(initialValue, maxValue);
		this.interactive = interactive;
	}

	public void setLabel(String label) {
		super.setLabel(label);
	}

	public void setLayout(int layout) {
		super.setLayout(layout);
	}

	public void addCommand(Command cmd) {
		super.addCommand(cmd);
	}

	public void setItemCommandListener(ItemCommandListener l) {
		super.setItemCommandListener(l);
	}

	public void setPreferredSize(int width, int height) {
		super.setPreferredSize(width, height);
	}

	public void setDefaultCommand(Command cmd) {
		super.setDefaultCommand(cmd);
	}

	public void setValue(int aValue) {
		value = validateValue(aValue, maxValue);
		updateParent(UPDATE_VALUE);
	}

	public int getValue() {
		return value;
	}

	public void setMaxValue(int aMaxValue)
	{
		if (maxValue == aMaxValue)
		{
			return;
		}
		int tempMaxValue = validateMaxValue(aMaxValue, interactive);
		int tempValue    = validateValue(getValue(), tempMaxValue, maxValue);

		maxValue = tempMaxValue;
		value = tempValue;
		updateParent(UPDATE_MAXVALUE);
	}

	public int getMaxValue() {
		return maxValue;
	}

	public boolean isInteractive() {
		return interactive;
	}

	protected void paint(Graphics g) {
	}

	public void layout() {
	}

	protected int getItemWidth() {
		return 0;
	}

	protected boolean allowNextItemPlaceSameRow() {
		return false;
	}

	protected boolean isFullWidthItem() {
		return true;
	}

    public boolean isIndefinite() {
		return maxValue == INDEFINITE;
    }

	private int validateMaxValue(int aMaxVal, boolean aInteractive)
	{
		if((!aInteractive) && (aMaxVal == INDEFINITE))
		{
			return aMaxVal;
		}

		if(aMaxVal <= 0)
		{
			throw new IllegalArgumentException();
		}

		return aMaxVal;
	}

	/**
	 * Check value validity.
	 *
	 * @param aValue the value.
	 * @param aMaxVal the maximum value.
	 * @return validated value.
	 */
	private int validateValue(int aValue, int aMaxVal)
	{
		if (aMaxVal == INDEFINITE)
		{
			switch (aValue)
			{
				case CONTINUOUS_IDLE:
				case INCREMENTAL_IDLE:
				case CONTINUOUS_RUNNING:
				case INCREMENTAL_UPDATING:
					break;
				default:
					throw new IllegalArgumentException();
			}
			return aValue;
		}
		else
		{
			return clampValue(aValue, aMaxVal);
		}
	}

	/**
	 * Check value validity.
	 *
	 * @param aValue the value.
	 * @param aNewMaxVal the new maximum value.
	 * @param aOlddMaxVal the old maximum value.
	 * @return validated value.
	 */
	private int validateValue(int aValue, int aNewMaxVal, int aOlddMaxVal)
	{
		if (aNewMaxVal == INDEFINITE)
		{
			return CONTINUOUS_IDLE;
		}
		else if (aOlddMaxVal == INDEFINITE)
		{
			return 0;
		}
		else
		{
			return clampValue(aValue, aNewMaxVal);
		}
	}

	/**
	 * Validates the value against the range.
	 *
	 * @param aValue the value.
	 * @param aMaxVal the maximum value.
	 * @return validated value.
	 */
	private static int clampValue(int aValue, int aMaxVal)
	{
		aValue = Math.min(aValue, aMaxVal);
		aValue = Math.max(aValue, 0);
		return aValue;
	}

	boolean isFocusable()
	{
		return (isInteractive() || (getNumCommands() > 0));
	}

	/**
	 * Called by widget listeners to update Item value.
	 */
	void internalSetValue(int newValue)
	{
		value = validateValue(newValue, maxValue);
		updateParent(UPDATE_VALUE);
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
	 * Updates Form or Alert.
	 *
	 * @see javax.microedition.lcdui.Item#updateParent(int)
	 */
	void updateParent(int updateReason)
	{
		if(isContainedInAlert())
		{
			((Alert) getParent()).updateIndicator();
		}
		else
		{
			super.updateParent(updateReason);
		}
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	Point calculateMinimumSize()
	{
		return GaugeLayouter.calculateMinimumBounds(this);
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	Point calculatePreferredSize()
	{
		return GaugeLayouter.calculatePreferredBounds(this);
	}

}
