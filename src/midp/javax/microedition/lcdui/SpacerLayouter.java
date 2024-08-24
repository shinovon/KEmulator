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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Class responsible for correct layout of Spacer item.
 */
class SpacerLayouter extends ItemLayouter
{

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    SpacerLayouter(FormLayouter aFormLayouter)
    {
        super(aFormLayouter);
    }

    /**
     * Creates eSWT Control for the given Item.
     *
     * @param item Item to create the Control
     * @param parent parent to create the Control
     * @return eSWT Control
     */
	Control createItemControl(Composite parent, Item item)
	{
		return swtGetControl(parent, item);
	}

    /**
     * eSWT specific calls to implement getControl.
     *
     * @param parent for the control.
     * @param item Spacer item.
     */
    Control swtGetControl(Composite parent, Item item)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setSize(item.getMinimumWidth(), item.getMinimumHeight());
        return composite;
    }

    /**
     * Returns true if this eSWT control is suitable to be used for updating.
     *
     * @param item Item.
     * @param control eSWT control.
     * @return true if this control is suitable for update
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
	    return (control instanceof Composite);
    }

    /**
     * Updates the values of Spacer. (Not used because of Spacer specific use)
     *
     * @param item Item.
     * @param control eSWT control.
     */
    void swtUpdateItem(Item item, Control control, int aReason, Object param)
    {
		/*if(control instanceof  Composite)
		{
			control.setSize(item.getMinimumWidth(), item.getMinimumHeight());
		}*/

   		if(!(control instanceof  Composite))
		{
			return;
		}
			
    	Spacer spacer = (Spacer)item;
		int reason = aReason & Item.UPDATE_SIZE_MASK;

		switch(reason)
		{
		case Item.UPDATE_NONE:
			break;

		case Spacer.UPDATE_MINIMUMSIZE:
		{
			Control sCtrl = swtFindSpecificControl(spacer, control);
			sCtrl.setSize(item.getMinimumWidth(), item.getMinimumHeight());
			break;
		}

		default:
		{
			break;
		}
		}
    }

}
