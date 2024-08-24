package javax.microedition.lcdui;

import org.eclipse.swt.graphics.Point;

public class Spacer extends Item {
	/**
	 * If Spacer is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_MINIMUMSIZE = UPDATE_ITEM_MAX << 1;

	private int minimumWidth;
	private int minimumHeight;

	public Spacer(final int n, final int n2) {
		super(null);
		this.setMinimumSize(n, n2);
	}

	public void setMinimumSize(int minW, int minH) {
		if(minW < 0 || minH < 0)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			int updateReason = Item.UPDATE_NONE;
			if(minW != getMinimumWidth())
			{
				minimumWidth = minW;
				updateReason |= UPDATE_MINIMUMSIZE | UPDATE_SIZE_CHANGED;
			}
			if(minH != getMinimumHeight())
			{
				minimumHeight = minH;
				updateReason |= UPDATE_MINIMUMSIZE | UPDATE_SIZE_CHANGED;
			}
			if(updateReason != Item.UPDATE_NONE)
			{
				updateParent(updateReason);
			}
		}
	}

	public void addCommand(final Command command) {
		throw new IllegalStateException();
	}

	public void setDefaultCommand(final Command command) {
		throw new IllegalStateException();
	}

	public void setLabel(final String s) {
		if (s != null) throw new IllegalStateException();
	}

	protected void paint(final Graphics graphics) {
		super.paint(graphics);
	}

	protected void layout() {
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	Point calculateMinimumSize()
	{
		return new Point(minimumWidth, minimumHeight);
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	Point calculatePreferredSize()
	{
		return new Point(minimumWidth, minimumHeight);
	}
}
