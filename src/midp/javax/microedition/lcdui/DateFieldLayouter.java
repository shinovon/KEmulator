/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
* All rights reserved.
* This component and the accompanying materials are made available
* under the terms of "Eclipse Public License v1.0"
* which accompanies this distribution, and is available
* at the URL "http://www.eclipse.org/legal/epl-v10.html".
*
* Initial Contributors:
* Nokia Corporation - initial contribution.
*
* Contributors:
*
* Description:
*
*/
package javax.microedition.lcdui;

import java.util.Date;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;


/**
 * Super class for all DateField Layouters. Contains their common
 * functionality.
 */
class DateFieldLayouter extends ItemLayouter
{

    /**
     * Key name for modify listener.
     */
    private static final String MODIFY_LISTENER = "ModifyListener";

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    DateFieldLayouter(FormLayouter aFormLayouter)
    {
        super(aFormLayouter);
    }

    /**
     * Creates the eSWT Control for this item.
     *
     * @param parent where to create.
     * @param item DateField Item.
     * @return Control.
     */
    Control swtGetControl(Composite parent, Item item)
    {
	    return swtCreateControl(parent, item);
    }

    /**
     * Creates the eSWT Control for this item.
     *
     * @param parent where to create.
     * @param item DateField Item.
     * @return Control.
     */
    static Control swtCreateControl(Composite parent, Item item)
    {
        DateField dateField = (DateField) item;
        DateTime dateEditor = null;

        int style = SWT.NONE;
        switch(dateField.getInputMode())
        {
        case DateField.TIME:
            style = SWT.TIME;
            break;
        case DateField.DATE:
            style = SWT.DATE;
            break;
        case DateField.DATE_TIME:
            style = SWT.TIME | SWT.DATE;
            break;
        default:
            break;
        }
        dateEditor = new DateTime(parent, style);

//        if(dateField.getTimeZone() != null)
//        {
//            dateEditor.setTimeZone(dateField.getTimeZone());
//        }
//        if(dateField.getDate() != null)
//        {
//            dateEditor.setDate(dateField.getDate());
//        }
		parent.pack();
        return parent;
    }

    /**
     * Add listeners to Layouter specific control.
     */
    void swtAddSpecificListeners(Item item, Control control)
    {
        DateTime dateEditor = (DateTime) control;
        ModifyListener listener = (ModifyListener)dateEditor.getData(MODIFY_LISTENER);
        if(listener == null)
        {
	        super.swtAddSpecificListeners(item, control);
	        DateField dateField = (DateField) item;
	        listener = new DateTimeModifyListener(dateField);
//	        dateEditor.addModifyListener(listener);
	        dateEditor.setData(MODIFY_LISTENER, listener);
        }
    }

    /**
     * Remove listeners from Layouter specific control.
     */
    void swtRemoveSpecificListeners(Item item, Control control)
    {
        DateTime dateEditor = (DateTime) control;
        ModifyListener listener = (ModifyListener)dateEditor.getData(MODIFY_LISTENER);
        if(listener != null)
        {
            super.swtRemoveSpecificListeners(item, control);
//            dateEditor.removeModifyListener(listener);
            dateEditor.setData(MODIFY_LISTENER, null);
        }
    }

    /**
     * Returns if this eSWT control is Layouter specific.
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
        return (control instanceof DateTime);
    }

    /**
     * Updates the values of DateField.
     */
    void swtUpdateItem(Item item, Control control, int aReason, Object param)
    {
		if(!(control instanceof Composite))
		{
			return;
		}
		
		DateField dateField = (DateField)item;
		int reason = aReason & Item.UPDATE_SIZE_MASK;
		
		switch(reason)
		{
		case Item.UPDATE_NONE:
			break;

		case Item.UPDATE_LABEL:
		{
			String label = dateField.getLabel();
			if(label == null)
			{
				label = "";
			}
            if (control instanceof Group) {
                ((Group) control).setText(label);
            }
			control.pack();
			break;
		}

		case DateField.UPDATE_DATE:
		{
			Control ctrl = swtFindSpecificControl(dateField, control);

			if(ctrl instanceof DateTime)
			{
				DateTime dateEditor = (DateTime)ctrl;
				Date date = dateField.getDate();

				if(date != null)
				{
//					if(dateField.getTimeZone() != null)
//					{
//						dateEditor.setTimeZone(dateField.getTimeZone());
//					}
//					dateEditor.setDate(date);
				}
				else
				{
					//It is expected that on dispose of  DateTime it will remove ModifyListener.
					dateEditor.dispose();
					swtGetControl((Composite)control, item);
				}			
			}
			break;
		}

		case DateField.UPDATE_INPUTMODE:
		{
			Control ctrl = swtFindSpecificControl(dateField, control);

			if(ctrl instanceof DateTime)
			{
				DateTime dateEditor = (DateTime)ctrl;
				//It is expected that on dispose of  DateTime it will remove ModifyListener.
				dateEditor.dispose();
				swtGetControl((Composite)control, item);
			}
			break;
		}

		default:
		{
			break;
		}
		}
    }

    /**
     * Handles key events when DateField is selected. If moving to the right or
     * left focus stays in DateField and only the selection inside of DateField
     * is changed. Otherwise method returns false and allows form to transfer
     * focus.
     */
    boolean swtOfferKeyPressed(Item item, int key)
    {
        if(key == SWT.ARROW_LEFT || key == SWT.ARROW_RIGHT)
        {
            //return true;
        }
        return false;
    }

    /**
     * Returns the minimum area needed to display a DateField.
     *
     * @param datefield DateField object.
     * @return Minimum area needed to display DateField.
     */
    static Point calculateMinimumBounds(final DateField datefield)
    {
	    final Point minSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
		        Composite captioned = new Composite(swtGetStaticShell(), SWT.VERTICAL);
		        if(datefield.hasLabel())
		        {
//			        captioned.setText(MIN_TEXT);
				}
		        swtCreateControl(captioned, datefield);
				int maxWidth = getMaximumItemWidth(datefield);
		        Point size = captioned.computeSize(maxWidth, SWT.DEFAULT);
		        captioned.dispose();
		        minSize.x = size.x;
		        minSize.y = size.y;
            }
        });
		return minSize;
    }

    /**
     * Returns the preferred area needed to display an Item.
     *
     * @return Preferred area needed to display Item. x is width and y is
     *         height.
     */
    static Point calculatePreferredBounds(final DateField datefield)
    {
	    final Point prefSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
		        Composite captioned = new Composite(swtGetStaticShell(), SWT.VERTICAL);
		        if(datefield.hasLabel())
		        {
//			        captioned.setText(datefield.getLabel());
				}
		        swtCreateControl(captioned, datefield);
				int maxWidth = getMaximumItemWidth(datefield);
		        Point size = captioned.computeSize(maxWidth, SWT.DEFAULT);
		        captioned.dispose();
		        prefSize.x = size.x;
		        prefSize.y = size.y;
            }
        });
		return prefSize;
    }

    /**
     * Class that receives events from DateTime and updates DateField's value.
     */
    class DateTimeModifyListener implements ModifyListener
    {
        private DateField dateField;

        /**
          * Constructor.
          * @param dateField DateField to be updated.
          */
        public DateTimeModifyListener(DateField dateField)
        {
            this.dateField = dateField;
        }

        /**
          * Called by eSWT when DateTime's value is modified.
          *
          * @param e Event.
          */
        public void modifyText(ModifyEvent e)
        {
            // Actions needed only if value is adjusted. Compare values
            // depending of dateField's type and exit if values are same.
            if(!isEqualDate(dateField, (DateTime) e.getSource()))
            {
//                Logger.method(dateField, MODIFY_LISTENER, e);
//                dateField.internalSetDate(((DateTime) e.getSource()).getDate());
                // notify item state listener
                dateField.notifyStateChanged();
            }
        }
    }

    /**
     * Compares date-values of DateField and DateTime and if values are same,
     * returns true. Method checks the input mode of DateField and compares only
     * values that are visible in DateField.
     *
     * @param dateField  DateField to compare.
     * @param dateEditor DateTime to compare.
     *
     * @return true if values are equal.
     */
    private boolean isEqualDate(DateField dateField, DateTime dateEditor)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

//        if(dateField.getDate() != null && dateEditor.getDate() != null)
//        {
//            // set dates of calendars
//            c1.setTime(dateField.getDate());
//            c2.setTime(dateEditor.getDate());
//
//            switch(dateField.getInputMode())
//            {
//            case DateField.DATE:
//                if((c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
//                        && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
//                        && (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)))
//                {
//                    return true;
//                }
//                break;
//            case DateField.TIME:
//                if((c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE))
//                        && (c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)))
//                {
//                    return true;
//                }
//                break;
//            case DateField.DATE_TIME:
//                if((c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
//                        && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
//                        && (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
//                        && (c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE))
//                        && (c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)))
//                {
//                    return true;
//                }
//                break;
//            default:
//                break;
//            }
//        }
        return false;
    }

}
