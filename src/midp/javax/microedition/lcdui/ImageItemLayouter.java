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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

/**
 * Corresponds for correct layouting of ImageItems.
 */
class ImageItemLayouter extends ItemLayouter
{

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    ImageItemLayouter(FormLayouter aFormLayouter)
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
     * Creates the eSWT Group or Composite for this item.
     */
    Control swtGetControl(Composite parent, Item item)
    {
        Control ret = swtCreateControl(parent, item);
        swtCaptionedResize(item, ret, item.getLayoutWidth(), item.getLayoutHeight());
        return ret;
    }

    /**
     * Creates the eSWT Group or Composite for this item.
     */
    static Control swtCreateControl(Composite parent, Item item)
    {
		Composite captioned;
			captioned = new Composite(parent, SWT.NONE);

		if(((ImageItem)item).getImage() != null)
		{
			swtSpecificControl(captioned, item);
		}
		else
		{
			updateControlSize(null, captioned, (ImageItem)item);
		}

        return captioned;
    }

    /**
     * Creates the eSWT specific control for this item.
     */
    static Control swtSpecificControl(Composite parent, Item item)
    {
        ImageItem imgItem = (ImageItem) item;

        if(imgItem.getNumCommands() == 0)
        {
            Label imageLabel = new Label(parent, SWT.CENTER);
			constructLabel(imageLabel, parent, imgItem);
            return imageLabel;
        }
        else
        {
            Button button;

            switch(imgItem.getAppearanceMode())
            {
            case ImageItem.BUTTON:
                button = new Button(parent, SWT.PUSH | SWT.NONE);
                break;
            case ImageItem.HYPERLINK:
            case ImageItem.PLAIN:
            default:
                button = new Button(parent, SWT.FLAT | SWT.NONE);
                break;
            }
			constructButton(button, parent, imgItem);
            return button;
        }
    }

	static private void constructLabel(Label imageLabel, Composite parent, ImageItem imgItem)
	{
        imageLabel.setImage(Image.getSWTImage(imgItem.getImage()));
        imageLabel.pack();
		updateControlSize(imageLabel, parent, imgItem);
	}

	static private void constructButton(Button button, Composite parent, ImageItem imgItem)
	{
		button.setImage(Image.getSWTImage(imgItem.getImage()));
		button.pack();
		updateControlSize(button, parent, imgItem);
	}

	static private void updateControlSize(Control control, Composite parent, ImageItem imgItem)
	{
		int controlWidth;
		int controlHeight;
		int maxWidth = getMaximumItemWidth(imgItem);

		if(control != null)
		{
			controlWidth = control.getBounds().width;
			controlHeight = control.getBounds().height;
			
			if(controlWidth > maxWidth)
			{
				//Image Resize has to be done.
				Point size = control.computeSize(maxWidth, SWT.DEFAULT);
				control.setSize(size.x, size.y);
				controlWidth = size.x;
				controlHeight = size.y;
			}
		}
		else
		{
			controlWidth = maxWidth;
		}
		
		if(imgItem.hasLabel())
		{
			int labelWidth = Font.getDefaultFont().stringWidth(imgItem.getLabel());
		
			if(labelWidth > controlWidth)
			{
				Point size = parent.computeSize(Math.min(labelWidth, maxWidth), SWT.DEFAULT);
				parent.setSize(size.x, size.y);
				return;
			}
		}
		else if(control == null)
		{
			parent.setSize(0, 0);
			return;
		}

		parent.pack();
	}

    /**
     * Returns if this eSWT control is Layouter specific.
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
        return ((control instanceof Label) || (control instanceof Button));
    }

    /**
     * Updates the values of ImageItem.
     */
    void swtUpdateItem(Item item, Control control, int aReason, Object param)
    {
   		if(!(control instanceof Composite))
		{
			return;
		}
			
    	ImageItem imgItem = (ImageItem)item;
		int reason = aReason & Item.UPDATE_SIZE_MASK;

		switch(reason)
		{
		case Item.UPDATE_NONE:
		case ImageItem.UPDATE_ALTTEXT:
			break;

		case Item.UPDATE_LABEL:
		{
			String label = imgItem.getLabel();
			if(label == null)
			{
				label = "";
			}

			if (control instanceof Group) {
				((Group)control).setText(label);
			}
			Control sCtrl = swtFindSpecificControl(imgItem, control);
			updateControlSize(sCtrl, (Composite)control, imgItem);
			break;
		}

		case ImageItem.UPDATE_IMAGE:
		{
			Control sCtrl = swtFindSpecificControl(imgItem, control);
			if(sCtrl != null)
			{
				if(imgItem.getImage() != null)
				{
					if(sCtrl instanceof Label)
					{
						constructLabel((Label)sCtrl, (Composite)control, imgItem);
					}
					else if(sCtrl instanceof Button)
					{
						constructButton((Button)sCtrl, (Composite)control, imgItem);
					}
				}
				else
				{
					sCtrl.dispose();
					updateControlSize(null, (Composite)control, imgItem);						
				}
			}
			else
			{
				if(imgItem.getImage() != null)
				{
					swtSpecificControl((Composite)control, imgItem);
				}
			}
			break;
		}

		case Item.UPDATE_ADDCOMMAND:
		{
			Control sCtrl = swtFindSpecificControl(imgItem, control);
			if(sCtrl != null)
			{
				if((sCtrl instanceof Label) && (imgItem.getNumCommands()==1))
				{
					sCtrl.dispose();
					swtSpecificControl((Composite)control, imgItem);
				}
			}
		}
		break;
		
		case Item.UPDATE_REMOVECOMMAND:
		{
			Control sCtrl = swtFindSpecificControl(imgItem, control);
			if(sCtrl != null)
			{
				if((sCtrl instanceof Button) && (imgItem.getNumCommands()==0))
				{
					sCtrl.dispose();
					swtSpecificControl((Composite)control, imgItem);
				}
			}
		}
		break;

		default:
		{
			break;
		}
		}
    }

    /**
     * Returns the minimum area needed to display an ImageItem.
     *
     * @param imageItem ImageItem object.
     * @return Minimum area needed to display ImageItem.
     */
    static Point calculateMinimumBounds(final ImageItem imageItem)
    {
        final Point minSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Point size = null;
                if(imageItem.getNumCommands() > 0)
                {
                    Button button = new Button(swtGetStaticShell(), SWT.PUSH);
                    button.setImage(Image.getSWTImage(imageItem.getImage()));
                    button.pack();
                    int buttonWidth = button.getBounds().width;
                    int maxWidth = getMaximumItemWidth(imageItem);
                    if(buttonWidth > maxWidth)
                    {
                        size = button.computeSize(maxWidth, SWT.DEFAULT);
                    }
                    else
                    {
                        size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                    }
                    button.dispose();
                }
                else
                {
                    Label imageLabel = new Label(swtGetStaticShell(), SWT.NONE);
                    imageLabel.setImage(Image.getSWTImage(imageItem.getImage()));
                    imageLabel.pack();
                    int imageWidth = imageLabel.getBounds().width;
                    int maxWidth = getMaximumItemWidth(imageItem);
                    if(imageWidth > maxWidth)
                    {
                        size = imageLabel.computeSize(maxWidth, SWT.DEFAULT);
                    }
                    else
                    {
                        size = imageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                    }
                    imageLabel.dispose();
                }
                minSize.x = size.x;
                minSize.y = size.y;
                applyMinMargins(imageItem, minSize);
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
    static Point calculatePreferredBounds(final ImageItem imageItem)
    {
        final Point prefSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Control ctrl = swtCreateControl(swtGetStaticShell(), imageItem);
                Point size = ctrl.getSize();
                ctrl.dispose();
                prefSize.x = size.x;
                prefSize.y = size.y;
            }
        });
        return prefSize;
    }

}
