package javax.microedition.lcdui;

import org.eclipse.swt.graphics.Point;

import java.util.*;

public class DateField extends Item {
	public static final int DATE = 1;
	public static final int TIME = 2;
	public static final int DATE_TIME = 3;
	private int mode;
	private Date date;

	private TimeZone timeZone;

	/**
	 * If DateField is changed, reasons for Re-layouting.
	 */
	static final int UPDATE_DATE = UPDATE_ITEM_MAX << 1;
	static final int UPDATE_INPUTMODE = UPDATE_ITEM_MAX << 2;


	public DateField(final String s, final int n) {
		this(s, n, null);
	}

	public DateField(final String s, final int inputMode, final TimeZone timeZone) {
		super(s);
		this.setInputMode(inputMode);
		this.timeZone = timeZone;
	}

	public Date getDate()
	{
		if(date == null)
		{
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(mode == TIME)
		{
			calendar.set(Calendar.YEAR,Config.DATEFIELD_ZERO_EPOCH_YEAR);
			calendar.set(Calendar.MONTH,Calendar.JANUARY);
			calendar.set(Calendar.DAY_OF_MONTH,1);
		}
		else if(mode == DATE)
		{
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
			calendar.set(Calendar.MILLISECOND,0);
		}
		return calendar.getTime();
	}

	/**
	 * Sets the date to this DateField.
	 *
	 * @param newDate New date. If null, current date is cleared.
	 */
	public void setDate(Date newDate)
	{
		internalSetDate(newDate);
		updateParent((newDate == null ? UPDATE_DATE | UPDATE_SIZE_CHANGED : UPDATE_DATE));
	}

	public int getInputMode() {
		return this.mode;
	}

	public void setInputMode(int inputMode)
	{
		if((inputMode != DATE)
				&& (inputMode != TIME)
				&& (inputMode != DATE_TIME))
		{
			throw new IllegalArgumentException();
		}

		if(getInputMode() != inputMode)
		{
			mode = inputMode;
			updateParent(UPDATE_INPUTMODE | UPDATE_SIZE_CHANGED);
		}
	}

	protected void paint(final Graphics graphics) {
		super.paint(graphics);
	}

	/**
	 * Calculates minimum size of this item.
	 *
	 * @return Minimum size.
	 */
	Point calculateMinimumSize()
	{
		return DateFieldLayouter.calculateMinimumBounds(this);
	}

	/**
	 * Calculates preferred size of this item.
	 *
	 * @return Preferred size.
	 */
	Point calculatePreferredSize()
	{
		return DateFieldLayouter.calculatePreferredBounds(this);
	}

	/**
	 * Gets timezone.
	 *
	 * @return Timezone or null if no timezone set.
	 */
	TimeZone getTimeZone()
	{
		return timeZone;
	}

	/**
	 * Set the new date internally.
	 *
	 * @param newDate new date
	 */
	void internalSetDate(Date newDate)
	{
		if(newDate == null)
		{
			date = null;
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			if(timeZone != null)
			{
				calendar.setTimeZone(timeZone);
			}
			calendar.setTime(newDate);

			switch(mode)
			{
				case TIME:
					if((calendar.get(Calendar.YEAR) != Config.DATEFIELD_ZERO_EPOCH_YEAR)
							|| (calendar.get(Calendar.MONTH) != Calendar.JANUARY)
							|| (calendar.get(Calendar.DAY_OF_MONTH) != 1))
					{
						date = null;
					}
					else
					{
						calendar.set(Calendar.SECOND,0);
						calendar.set(Calendar.MILLISECOND,0);
						date = calendar.getTime();
					}
					break;
				case DATE:
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND,0);
					calendar.set(Calendar.MILLISECOND,0);
					date = calendar.getTime();
					break;
				case DATE_TIME:
					calendar.set(Calendar.SECOND,0);
					calendar.set(Calendar.MILLISECOND,0);
					date = calendar.getTime();
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Return layout with optional custom flags.
	 *
	 * @return layout directive.
	 */
	int internalGetLayout()
	{
		return super.internalGetLayout() | Item.LAYOUT_NEWLINE_BEFORE;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Item#isFocusable()
	 */
	boolean isFocusable()
	{
		return true;
	}
}
