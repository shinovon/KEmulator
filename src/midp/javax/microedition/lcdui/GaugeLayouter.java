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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;


/**
 * Class for layouting gauges.
 */
class GaugeLayouter extends ItemLayouter
{

    /**
     * Key name for selection listener.
     */
    private static final String SELECTION_LISTENER = "SelectionListener";
    private static final String MAX_LABEL_DATA = "MaxLabel";
    private static final String CURRENT_LABEL_DATA = "CurrentLabel";


    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    GaugeLayouter(FormLayouter aFormLayouter)
    {
        super(aFormLayouter);
    }

    /**
     * Creates the eSWT ProgressBar/Slider for this item.
     */
    Control swtGetControl(Composite parent, Item item)
    {
        return swtCreateControl(parent, item);
    }

    /**
     * Construts a gauge control surrounded with composite.
     */
    static Control swtCreateControl(Composite parent, Item item)
    {
        Gauge gauge = (Gauge) item;
        // create an owning composite every time
        Composite comp = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		comp.setLayout(layout);

        if(gauge.isInteractive())
        {
            //Current Value Label
            Label currentlabel = new Label(comp, SWT.WRAP);
            currentlabel.setText(Integer.toString(gauge.getValue()));
            currentlabel.setAlignment(SWT.LEAD);
			currentlabel.setData(CURRENT_LABEL_DATA, CURRENT_LABEL_DATA);
			//Current Value Label Data
            FormData currLabelData = new FormData();
            currLabelData.left = new FormAttachment(0);
            currentlabel.setLayoutData(currLabelData);
			//Slider
            Slider slider = new Slider(comp, SWT.HORIZONTAL);
            slider.setMinimum(0);
            slider.setMaximum(gauge.getMaxValue());
            slider.setSelection(gauge.getValue());
            slider.setIncrement(1);
            slider.setPageIncrement(1);
			//Slider Data
            FormData sliderLayoutData = new FormData();
            sliderLayoutData.right = new FormAttachment(100);
            sliderLayoutData.left = new FormAttachment(currentlabel);
            slider.setLayoutData(sliderLayoutData);
            //Min Value Label
            Label minlabel = new Label(comp, SWT.WRAP);
            minlabel.setText("0");
            minlabel.setAlignment(SWT.LEAD);
			//Min Value Label Data
            FormData minLabelData = new FormData();
            minLabelData.left = new FormAttachment(slider, 0, SWT.LEFT);
            minLabelData.top =  new FormAttachment(slider);
            minlabel.setLayoutData(minLabelData);
            //Max Value Label
            Label maxlabel = new Label(comp, SWT.WRAP);
            maxlabel.setText(Integer.toString(gauge.getMaxValue()));
            maxlabel.setAlignment(SWT.LEAD);
			maxlabel.setData(MAX_LABEL_DATA, MAX_LABEL_DATA);
			//Max Value Label Data
            FormData maxLabelData = new FormData();
            maxLabelData.right = new FormAttachment(slider, 0, SWT.RIGHT);
            maxLabelData.top =  new FormAttachment(slider);
            maxlabel.setLayoutData(maxLabelData);
        }
        else
        {
            ProgressBar progressBar = null;
            if(gauge.isIndefinite())
            {
                switch(gauge.getValue())
                {
                case Gauge.CONTINUOUS_IDLE:
                    // TODO: eSWT support required
                    // Gauge like busy-state indicator with no activity:
                    progressBar = new ProgressBar(comp,
                                                  SWT.HORIZONTAL | SWT.INDETERMINATE);
                    break;
                case Gauge.CONTINUOUS_RUNNING:
                    // Gauge like busy-state indicator with continuous activity:
                    progressBar = new ProgressBar(comp,
                                                  SWT.HORIZONTAL | SWT.INDETERMINATE);
                    break;
                case Gauge.INCREMENTAL_IDLE:
                    // TODO: eSWT support required
                    // Gauge like icremental updating, but no activity.
                    progressBar = new ProgressBar(comp,
                                                  SWT.HORIZONTAL | SWT.INDETERMINATE);
                    break;
                case Gauge.INCREMENTAL_UPDATING:
                    // TODO: eSWT support required
                    //Gauge like icremental updating, with indefinite activity.
                    progressBar = new ProgressBar(comp,
                                                  SWT.HORIZONTAL | SWT.INDETERMINATE);
                    break;
                default:
                    // This is invalid case and should never occur, as Gauge should take care of it.
                    Logger.warning("Unexpected gauge value: " + gauge.getValue());
                    break;
                }
            }
            else
            {
                progressBar = new ProgressBar(comp, SWT.HORIZONTAL);
                progressBar.setMaximum(gauge.getMaxValue());
                progressBar.setSelection(gauge.getValue());
            }

			if(progressBar != null)
			{
				FormData progressBarData = new FormData();
				progressBarData.left = new FormAttachment(0);
				progressBarData.right = new FormAttachment(100);
				progressBar.setLayoutData(progressBarData);
			}
        }
		parent.pack();
        return parent;
    }

    /**
     * returns the matching data Label of Gauge control.
     */
	Label swtGetDataLabel(Control control, String data)
	{
        Control ret = null;
		
        if(control instanceof Label)
        {
			if(data.equals((String)control.getData(data)))
            ret = control;
        }
        else if(control instanceof Composite)
        {
            Control[] children = ((Composite) control).getChildren();
            for(int i = 0; i < children.length; i++)
            {
                Control result = swtGetDataLabel(children[i], data);
                if(result != null)
                {
                    ret = result;
                    break;
                }
            }
        }
        return (Label)ret;
	}

    /**
     * Set the size of the layouted Control.
     */
    void swtResizeControl(Item item, Control control, int width, int height)
    {
		if((control instanceof Slider) || (control instanceof ProgressBar))
		{
			Control comp = control.getParent();
			Control capt = comp.getParent();
			comp.pack();
			Point size = comp.computeSize(width, comp.getBounds().height);
			comp.setSize(size.x, size.y);
			Point loc = comp.getLocation();
			if((height-size.y) != 0)
			{
				comp.setLocation(loc.x, loc.y + (height - size.y)/2);
			}
		}
    }

    /**
     * Add listeners to Layouter specific control.
     */
    void swtAddSpecificListeners(Item item, Control control)
    {
    	if(control instanceof Slider)
   		{
	        Slider slider = (Slider)control;
			SelectionListener listener = (SelectionListener)slider.getData(SELECTION_LISTENER);
	        if(listener == null)
	        {
		        super.swtAddSpecificListeners(item, control);
				Gauge gauge = (Gauge)item;
		        listener = new GaugeSelectionListener(gauge);
				slider.addSelectionListener(listener);
				slider.setData(SELECTION_LISTENER, listener);
	        }
    	}
    }

    /**
     * Remove listeners from Layouter specific control.
     */
    void swtRemoveSpecificListeners(Item item, Control control)
    {
    	if(control instanceof Slider)
   		{
	        Slider slider = (Slider)control;
			SelectionListener listener = (SelectionListener)slider.getData(SELECTION_LISTENER);
	        if(listener != null)
	        {
		        super.swtRemoveSpecificListeners(item, control);
				slider.removeSelectionListener(listener);
                slider.setData(SELECTION_LISTENER, null);
	        }
    	}
    }

    /**
     * Returns if this eSWT control is Layouter specific.
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
        return((control instanceof Slider) || (control instanceof ProgressBar));
    }

    /**
     * Updates the values of Gauge.
     */
    void swtUpdateItem(Item item, Control control, int aReason, Object param)
    {
		if(!(control instanceof Composite))
		{
			return;
		}
		
		Gauge gauge = (Gauge)item;
		int reason = aReason & Item.UPDATE_SIZE_MASK;
		
		switch(reason)
		{
		case Item.UPDATE_NONE:
			break;
		
		case Item.UPDATE_LABEL:
		{
			String label = gauge.getLabel();
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
		
		case Gauge.UPDATE_VALUE:
		{
			//Setting the value to the control irrespective of the reason,
			//as trade of for reason filtration.
			Control ctrl = swtFindSpecificControl(gauge, control);
		
			if(ctrl instanceof Slider)
			{
				Slider slider = (Slider)ctrl;
				slider.setSelection(gauge.getValue());
			}
			else if(ctrl instanceof ProgressBar)
			{
				ProgressBar progressbar = (ProgressBar)ctrl;
				progressbar.setSelection(gauge.getValue());
			}

			Label currLabel = swtGetDataLabel(control, CURRENT_LABEL_DATA);
			if(currLabel != null)
			{
				currLabel.setText(Integer.toString(gauge.getValue()));
				currLabel.getParent().layout();
			}
			break;
		}
		
		case Gauge.UPDATE_MAXVALUE:
		{
			Control ctrl = swtFindSpecificControl(gauge, control);
		
			if(ctrl instanceof Slider)
			{
				Slider slider = (Slider)ctrl;
				slider.setMaximum(gauge.getMaxValue());
			}
			else if(ctrl instanceof ProgressBar)
			{
				ProgressBar progressbar = (ProgressBar)ctrl;
				progressbar.setMaximum(gauge.getMaxValue());
			}

			Label maxLabel = swtGetDataLabel(control, MAX_LABEL_DATA);
			if(maxLabel != null)
			{
				maxLabel.setText(Integer.toString(gauge.getMaxValue()));			
				maxLabel.getParent().layout();
			}

			Label currLabel = swtGetDataLabel(control, CURRENT_LABEL_DATA);
			if(currLabel != null)
			{
				currLabel.setText(Integer.toString(gauge.getValue()));
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
     * Returns true if that key was consumed by Gauge.
     */
    boolean swtOfferKeyPressed(Item item, int key)
    {
        Gauge gauge = (Gauge) item;
        if(gauge.isInteractive())
        {
            if(key == SWT.ARROW_RIGHT)
            {
            	if(gauge.getValue() != gauge.getMaxValue())
				{
	                gauge.internalSetValue(gauge.getValue() + 1);
	                gauge.setLayout(gauge.internalGetLayout());
					return true;
            	}

            }
            else if(key == SWT.ARROW_LEFT)
            {
            	if(gauge.getValue() > 0)
            	{
	                gauge.internalSetValue(gauge.getValue() - 1);
	                gauge.setLayout(gauge.internalGetLayout());
					return true;
            	}
            }
        }
        return false;
    }

    /**
     * Returns the minimum area needed to display a Gauge.
     *
     * @param gauge Gauge object.
     * @return Minimum area needed to display Gauge.
     */
    static Point calculateMinimumBounds(final Gauge gauge)
    {
	    final Point minSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Composite captioned = new Composite(swtGetStaticShell(), SWT.VERTICAL);
		        if(gauge.hasLabel())
		        {
//			        captioned.setText(MIN_TEXT);
				}
		        swtCreateControl(captioned, gauge);
				int maxWidth = getMaximumItemWidth(gauge);
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
     * @return Preferred area needed to display Item. x is width
     *      and y is height.
     */
    static Point calculatePreferredBounds(final Gauge gauge)
    {
	    final Point prefSize = new Point(0, 0);
	    Displayable.syncExec(new Runnable()
	    {
	        public void run()
	        {
		        Composite captioned = new Composite(swtGetStaticShell(), SWT.VERTICAL);
		        if(gauge.hasLabel())
		        {
//			        captioned.setText(gauge.getLabel());
				}
		        swtCreateControl(captioned, gauge);
				int maxWidth = getMaximumItemWidth(gauge);
		        Point size = captioned.computeSize(maxWidth, SWT.DEFAULT);
		        captioned.dispose();
		        prefSize.x = size.x;
		        prefSize.y = size.y;
	        }
	    });
		return prefSize;
    }

    /**
     * Class that receives events from slider and updates gauge's value.
     */
    class GaugeSelectionListener implements SelectionListener
    {
        private Gauge gauge;

        /**
         * Constructor.
         * @param gauge Gauge to be updated.
         */
        public GaugeSelectionListener(Gauge gauge)
        {
            this.gauge = gauge;
        }

        public void widgetDefaultSelected(SelectionEvent e)
		{
			Logger.method(this, "widgetDefaultSelected");
		}

        /**
         * Called by eSWT when Slider's value is changed.
         * Updates Gauge's value.
         */
        public void widgetSelected(SelectionEvent e)
        {
        	//Implement for other Gauge types with Commands
            int newValue = ((Slider) e.getSource()).getSelection();

            // Actions needed only if value is adjusted:
            if(newValue != gauge.getValue())
            {
                // set Gauge value
                gauge.internalSetValue(newValue);
                gauge.setLayout(gauge.internalGetLayout());
            }
        }
    }
}
