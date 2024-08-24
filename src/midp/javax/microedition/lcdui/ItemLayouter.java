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

import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

/**
 * Abstract base class for Item layouters.
 */
abstract class ItemLayouter
{

    /**
     * Key name for paint listener.
     */
    private static final String FOCUS_LISTENER = "FocusListener";

    protected static final String MIN_TEXT = "...";

    protected FormLayouter formLayouter;

    protected Composite formComposite;

    private static Label staticLabel;
    private static Group staticCC;
    private static Shell staticShell;

    private static Point captionedTrim;

    protected static int formWidth;
    protected static int formHeigh;

    /**
     * Gets static singleton off-screen Shell which can be used by item
     * layouters when creating temporary Controls.
     *
     * @return Static Shell. Never null.
     */
    static Shell swtGetStaticShell()
    {
        if(staticShell == null)
        {
            staticShell = new Shell(EmulatorImpl.getDisplay(), SWT.SYSTEM_MODAL | SWT.VERTICAL);
            staticShell.getVerticalBar().setVisible(true);
            formWidth = staticShell.getClientArea().width;
            formHeigh = staticShell.getClientArea().height;
        }
        return staticShell;
    }

    /**
     * Gets static singleton off-screen Label control.
     */
    private static Label swtGetStaticLabel()
    {
        if(staticLabel == null)
        {
            staticLabel = new Label(swtGetStaticShell(), SWT.NONE);
        }
        return staticLabel;
    }

    /**
     * Gets static singleton off-screen Captioned control.
     */
    private static Group swtGetStaticCC()
    {
        if(staticCC == null)
        {
            staticCC = new Group(swtGetStaticShell(), SWT.VERTICAL);
        }
        return staticCC;
    }

    /**
     * The static "layouting" shell's size is updated.
     */
    static void swtUpdateStaticShellSize(int width, int height)
    {
        if(staticShell != null)
        {
            staticShell.setBounds(staticShell.computeTrim(0, 0, width, height));
            formWidth = width;
            formHeigh = height;
        }
    }

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    ItemLayouter(FormLayouter aFormLayouter)
    {
        formLayouter = aFormLayouter;
        formComposite = formLayouter.getForm().getFormComposite();
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                ItemLayouter.swtGetStaticShell();
            }
        });
    }

    /**
     * Label alignment directive.
     */
    int swtGetLabelAlignmentDirective()
    {
        return formLayouter.getLanguageSpecificLayoutDirective();
    }

    /**
     * Layout Item in a row.
     *
     * @param row current Row
     * @param item Item to layout
     */
    void swtLayoutItem(Row row, Item item)
    {
    	LayoutObject lo = getLayoutObject(item);
        formLayouter.swtAddNewLayoutObject(lo);
        if(item instanceof CustomItem)
        {
            ItemControlStateChangeListener listener = item.getItemControlStateChangeListener();
            if(null != listener)
            {
                listener.notifyControlAvailable(lo.getControl(),item);
                lo.getControl().addListener(SWT.Dispose, new EventListener(item));
            }
        }
    }

    /**
     * Creates LayoutObject for the given Item.
     *
     * @param item Item to layout
     * @return LayoutObject
     */
    LayoutObject getLayoutObject(Item item)
    {
    	LayoutObject lo = formLayouter.getLayoutObject(item);
    	if(lo == null)
    	{
        	lo = new LayoutObject(item, createItemControl(formComposite, item));
    	}
		return lo;
    }

    /**
     * Creates eSWT Control for the given Item.
     *
     * For any Item, if it is needed to create non CaptionControl based Control
     * then this fucntion can be overrided in the derived Item Layouter.
     *
     * @param item Item to create the Control
     * @param parent formComposite as parent to create the Control
     * @return eSWT Control
     */
	Control createItemControl(Composite parent, Item item)
	{
		return swtGetGroup(parent, item);
	}

    /**
     * Wraps this item's control in the necessary composites.<br>
     * Based on the item, the result of this method can be:
     * <li> specific Control
     * <li> labeled Group (containing the specific Control) <br>
     * <br>
     * The method will set the size of these using the swtCaptionedResize
     * method.
     *
     * @param item Item to be layouted
     */
    final Control swtGetGroup(Composite parent, Item item)
    {
        Composite captioned;
            captioned = new Composite(parent, SWT.NONE);
        swtGetControl(captioned, item);
        swtCaptionedResize(item, captioned, item.getLayoutWidth(), item.getLayoutHeight());
        return captioned;
    }

    /**
     * This abstract method creates the eSWT control.
     *
     * @param parent where to create
     * @param item on which it is based
     * @return Control
     */
    abstract Control swtGetControl(Composite parent, Item item);

    /**
     * Update size of an LayoutObject.
     */
    final void swtResizeObject(LayoutObject lo)
    {
        Item item = lo.getOwningItem();
        swtCaptionedResize(item, lo.getControl(), item.getLayoutWidth(), item.getLayoutHeight());
        lo.swtUpdateSize();
    }

    /**
     * Set the size of a LayoutObject.
     *
     * @param lo LayoutObject
     * @param width
     * @param height
     */
    final void swtResizeObject(LayoutObject lo, int width, int height)
    {
        swtCaptionedResize(lo.getOwningItem(), lo.getControl(), width, height);
        lo.swtUpdateSize();
    }

    final void swtCaptionedResize(Item item, Control control, int width, int height)
    {
        if(control instanceof Composite)
        {
            Composite cc = (Composite) control;
            cc.setSize(width, height);
            Rectangle ccArea = cc.getClientArea();
            swtResizeControl(item, swtFindSpecificControl(item, control),
                              ccArea.width, ccArea.height);
        }
        else
        {
            swtResizeControl(item, swtFindSpecificControl(item, control),
                              width, height);
        }
    }

    /**
     * Should be implemented in sub-classes where resizing might happen.
     *
     * @param item Item
     * @param control layouted Control
     * @param width item width.
     * @param height item height
     */
    void swtResizeControl(Item item, Control control, int width, int height)
    {
    	if(control != null)
    	{
        	control.setSize(width, height);
    	}
    }

    /**
     * Add listeners to layouted control.
     *
     * @param item Item
     * @param lo LayoutObject
     */
    void swtAddListeners(Item item, LayoutObject lo)
    {
        lo.swtAddSelectionListenerForCommands();
        Control specific = swtFindSpecificControl(item, lo.getControl());
        if(specific != null)
        {
            swtAddSpecificListeners(item, specific);
        }
        else
        {
            Logger.warning(this + "::swtAddListeners didnt find control for " + item);
        }
    }

    /**
     * Add listeners to Layouter specific control.
     *
     * @param item Item
     * @param control specific Control
     */
    void swtAddSpecificListeners(Item item, Control control)
    {
        if(item.isFocusable())
        {
            ItemFocusListener ifl = new ItemFocusListener(item);
            control.addFocusListener(ifl);
            control.setData(FOCUS_LISTENER, ifl);
        }
    }

    /**
     * Remove listeners from layouted control.
     *
     * @param item Item
     * @param lo LayoutObject
     */
    void swtRemoveListeners(Item item, LayoutObject lo)
    {
        lo.swtRemoveSelectionListenerForCommands();
        Control specific = swtFindSpecificControl(item, lo.getControl());
        if(specific != null)
        {
            swtRemoveSpecificListeners(item, specific);
        }
        else
        {
            Logger.warning(this + "::swtRemoveListeners didnt find control for " + item);
        }
    }

    /**
     * Remove listeners from Layouter specific control.
     *
     * @param item Item
     * @param control specific Control
     */
    void swtRemoveSpecificListeners(Item item, Control control)
    {
        if(item.isFocusable())
        {
            ItemFocusListener ifl = (ItemFocusListener) control
                                    .getData(FOCUS_LISTENER);
            if(ifl != null)
            {
                control.removeFocusListener(ifl);
                control.setData(FOCUS_LISTENER, null);
            }
        }
    }

    /**
     * Update control of an Item.
     *
     * @param item Item to update
     * @param reason reason of update
     * @param param optional parameter
     */
    final void updateItem(final Item item, final Control control,
                          final int reason, final Object param)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                if(control != null)
                {
                    if(!control.isDisposed())
                    {
                        swtUpdateItem(item, control, reason, param);
                    }
                    else
                    {
                        Logger.warning(ItemLayouter.this
                                       + "::updateItem found a disposed widget for " + item);
                    }
                }
                else
                {
                    Logger.warning(ItemLayouter.this
                                   + "::updateItem didnt find control for " + item);
                }
            }
        });
    }

    /**
     * This abstract method updates the eSWT control.
     *
     * @param item Item to update
     * @param control layouted eSWT control
     * @param reason reason of update
     * @param param optional parameter
     */
    abstract void swtUpdateItem(Item item, Control control, int reason,
                                 Object param);

    /**
     * Finds the Layouter specific eSWT control in the eSWT Composite tree.
     *
     * @param item Item
     * @param control eSWT control
     * @return a specific control or null if not found
     */
    final Control swtFindSpecificControl(Item item, Control control)
    {
        Control ret = null;
        if(swtIsSpecificControl(item, control))
        {
            ret = control;
        }
        else if(control instanceof Composite)
        {
            Control[] children = ((Composite) control).getChildren();
            for(int i = 0; i < children.length; i++)
            {
                Control result = swtFindSpecificControl(item, children[i]);
                if(result != null)
                {
                    ret = result;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Returns the unlocked preferred size needed to display an Item.
     * Subclasses may overwrite this. By default returns (0,0).
     *
     * @param item Item.
     * @return Preferred area needed to display Item. x is width
     *      and y is height.
     */
    static Point calculatePreferredBounds(Item item)
    {
        return new Point(0, 0);
    }

    /**
     * Returns if this eSWT control is Layouter specific.
     *
     * @param item Item
     * @param control eSWT control
     * @return true if this is Layouter specific
     */
    abstract boolean swtIsSpecificControl(Item item, Control control);

    /**
     * Gets the first control of an Item.
     *
     * @param item Item
     * @return layouted Control
     */
    Control swtGetFirstControl(Item item)
    {
        LayoutObject lo = formLayouter.getFirstLayoutObjectOfItem(item);
        if(lo != null)
        {
            return lo.getControl();
        }
        return null;
    }

    /**
     * Gets the first layouter specific control of an Item.
     *
     * @param item Item
     * @return layouted specific Control
     */
    Control swtGetFirstSpecificControl(Item item)
    {
        LayoutObject lo = formLayouter.getFirstLayoutObjectOfItem(item);
        if(lo != null)
        {
            Control control = lo.getControl();
            if(control != null)
            {
                return swtFindSpecificControl(item, control);
            }
        }
        return null;
    }

    /**
     * Offers a key event to be consumed by the control.<br>
     * If the key is not consumed (default) then it's used for scrolling.
     *
     * @param item Item
     * @param key eSWT key code
     * @param type eSWT key type
     * @return if the key was consumed or not
     */
    boolean swtOfferKeyEvent(Item item, int key, int type)
    {
        if(type == SWT.KeyDown)
        {
            return swtOfferKeyPressed(item, key);
        }
        else if(type == SWT.KeyUp)
        {
            return swtOfferKeyReleased(item, key);
        }
        else
        {
            return swtOfferKeyRepeated(item, key);
        }
    }

    boolean swtOfferKeyPressed(Item item, int key)
    {
        // Do not consume these by default
        return false;
    }

    boolean swtOfferKeyRepeated(Item item, int key)
    {
        // Do not consume these by default
        return false;
    }

    boolean swtOfferKeyReleased(Item item, int key)
    {
        // Do not consume these by default
        return false;
    }

    /**
     * Processing for item when it gets focus.
     *
     * @param item
     * @param dir
     */
    void swtFocusGained(Item item, int dir)
    {
        Logger.method(item, "focusGained", String.valueOf(dir));
        item.internalSetFocused(true);
    }

    /**
     * Processing for item when it looses focus.
     *
     * @param item item which looses focus.
     */
    void swtFocusLost(Item item)
    {
        Logger.method(item, "focusLost");
        item.internalSetFocused(false);
    }

    final void swtHandleVisibilityChange(Item item, boolean visible)
    {
        if(item.isVisible() != visible)
        {
            item.internalSetVisible(visible);
            if(visible)
            {
                swtHandleShow(item);
            }
            else
            {
                swtHandleHide(item);
            }
        }
    }

    /**
     * Special processing of Item when it becomes visible.
     *
     * @param item which becomes visible.
     */
    void swtHandleShow(Item item)
    {
        // Implementation not needed. Subclasses may override.
    }

    /**
     * Special processing for item which becomes not visible due to scrolling.
     *
     * @param item which becomes hidden.
     */
    void swtHandleHide(Item item)
    {
        // Implementation not needed. Subclasses may override.
    }

    static void applyMinMargins(Item item, Point size)
    {
        if(item.hasLabel())
        {
//            applyCaptionedTrim(MIN_TEXT, size);
        }
        size.x = Math.min(size.x, formWidth);
    }

    static void applyPrefMargins(Item item, Point size)
    {
        if(item.hasLabel())
        {
            applyCaptionedTrim(item.getLabel(), size);
        }
        size.x = Math.min(size.x, formWidth);
    }


    static final void applyCaptionedTrim(final String text, Point size)
    {
        final Point localSize = size;
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Group cc = swtGetStaticCC();
                cc.setText(text);
                Rectangle rect = cc.computeTrim(0, 0, localSize.x, localSize.y);
                captionedTrim = new Point(rect.width, rect.height);
            }
        });
        size.x = captionedTrim.x;
        size.y = captionedTrim.y;
    }

    /**
     * Applies the label size to the unlocked preferred area needed to display
     * an Item.
     *
     */
    static Point getLabelSize(final String labelStr)
    {
        final Point size = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Label label = swtGetStaticLabel();
                label.setText(labelStr);
                Point temp = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                size.x = Math.min(temp.x, formWidth);
                size.y = temp.y;
            }
        });
        return size;
    }

    /**
     * Calculate X position based on horizontal layout.
     *
     * @param owningWidth owning area width
     * @param objectWidth object's width
     * @param horizontalLayout horizontal layout
     * @return x-position of object
     */
    static final int getXLocation(int owningWidth, int objectWidth,
                                  int horizontalLayout)
    {
        switch(horizontalLayout)
        {
        case Item.LAYOUT_RIGHT:
            return owningWidth - objectWidth;
        case Item.LAYOUT_CENTER:
            return (owningWidth - objectWidth) / 2;
        case Item.LAYOUT_LEFT:
        default:
            return 0;
        }
    }

    /**
     * Calculate Y position based on vertical layout.
     *
     * @param owningHeight owning area height
     * @param objectHeight object's height
     * @param verticalLayout vertical layout
     * @return y-position of object
     */
    static final int getYLocation(int owningHeight, int objectHeight,
                                  int verticalLayout)
    {
        switch(verticalLayout)
        {
        case Item.LAYOUT_VCENTER:
            return (owningHeight - objectHeight) / 2;
        case Item.LAYOUT_TOP:
            return 0;
        case Item.LAYOUT_BOTTOM:
            return owningHeight - objectHeight;
        default:
            return owningHeight - objectHeight;
        }
    }

    /**
     * Gets maximum width of an item. The maximum width is same for all items
     * and it's the width of form's content area.
     *
     * @param item Item which maximum width is returned. The width is same for
     *            all items but this parameter is useful because method could
     *            use item's parent to calculate the width.
     * @return Maximum width of an item.
     */
    static int getMaximumItemWidth(final Item item)
    {
        if(item != null && item.hasLabel())
        {
            Point temp = new Point(0, 0);
            applyCaptionedTrim("", temp);
            return formWidth - temp.x;
        }
        return formWidth;
    }

    /**
     * Item focus Listener reacts on eSWT focusGained event.
     */
    class ItemFocusListener implements FocusListener
    {

        private Item item;

        ItemFocusListener(Item item)
        {
            this.item = item;
        }

        public void focusGained(FocusEvent focusEvent)
        {
            if(!item.isFocused())
            {
                // Logger.method(item, "focusGained");
                formLayouter.swtSetCurrentSelectedItem(item);
            }
        }

        public void focusLost(FocusEvent fe)
        {
            // Logger.method(item, "focusLost");
        }
    }

    class EventListener implements Listener
    {
        Item itm;
        EventListener(Item item)
        {
            itm = item;
        }
        public void handleEvent(Event e)
        {
            (itm.getItemControlStateChangeListener()).notifyControlDisposed(itm);
        }
    }
}
