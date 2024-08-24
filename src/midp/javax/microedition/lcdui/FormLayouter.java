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

import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * FormLayouter implements form layout algorithm.
 */
class FormLayouter
{

    /**
     * Form instance (not owned).
     */
    private Form form;

    /**
     * Form's scrolled composite (not owned).
     */
    protected ScrolledComposite formComposite;

    /**
     * Controls vector.
     */
    private Vector layoutObjects = new Vector();

    /**
     * Rows vector.
     */
    private Vector rows = new Vector();

    /**
     * Form's current horizontal alignment
     */
    private int currentHLayoutDirective;

    private ImageItemLayouter imIL;

    private StringItemLayouter sIL;

    private GaugeLayouter gL;

    private TextFieldLayouter tfL;

    private DateFieldLayouter dfL;

    private ChoiceGroupLayouter cgL;

    private CustomItemLayouter ciL;

    private SpacerLayouter sL;

    private int vPosition;

    private Item deferredScrollToItem;

    private boolean isCurrent;

    private Control itemMainControl;

    private static final int NO_DIRECTION = -1;

    private Item currentSelectedItem;

    private int direction = NO_DIRECTION;

    private boolean mousePressed;

    private LayoutObject currentlyUnderMouse;


    /**
     * Constructor.
     *
     * @param form Form to perform layout on.
     */
    FormLayouter(Form form)
    {
        this.form = form;
        formComposite = form.getFormComposite();
        imIL = new ImageItemLayouter(this);
        sIL = new StringItemLayouter(this);
        gL = new GaugeLayouter(this);
        dfL = new DateFieldLayouter(this);
        tfL = new TextFieldLayouter(this);
        cgL = new ChoiceGroupLayouter(this);
        ciL = new CustomItemLayouter(this);
        sL = new SpacerLayouter(this);
    }

    /**
     * Dispose and cleanup layouted items.
     */
    void dispose()
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                swtClearRows(0, null);
    			removeAllLayoutObjects();
				rows = null;
				layoutObjects = null;
            }
        });
    }

    /**
     * Log out a control with all its children at the given indentation level.
     */
    void logControl(int level, Control control)
    {
//        if(control != null)
//        {
//            String s2 = control.toString();
//            s2 = s2.substring(0, s2.indexOf('}') - 1);
//            String s3 = control.getBounds().toString();
//            s3 = s3.substring(s3.indexOf('{'));
//            Logger.verbose(Logger.indent(s2 + s3, level * 2));
//            if(control instanceof Composite)
//            {
//                Control[] arr = ((Composite) control).getChildren();
//                for(int i = 0; i < arr.length; i++)
//                {
//                    logControl(level + 1, arr[i]);
//                }
//            }
//        }
    }

    /**
     * Log out all layoutobjects for each item.
     */
    void logFormLayout()
    {
//        Logger.verbose(form + " ---------------------------------------------");
//        LayoutObject lo = null;
//        Item item = null;
//        int line = -1;
//        while((lo = getNextLayoutObjectOfItem(lo, null)) != null)
//        {
//            if(lo.getOwningItem() != item)
//            {
//                item = lo.getOwningItem();
//                Logger.verbose(item.toString());
//            }
//            if(lo.getRowIdx() != line)
//            {
//                line = lo.getRowIdx();
//            }
//            Logger.verbose(Logger.indent("Row:" + lo.getRowIdx(), 2));
//            logControl(2, lo.getControl());
//        }
//        Logger.verbose(form + " ---------------------------------------------");
    }

    /**
     * Called when Form is about to be shown.<br>
     * NOTE: this is called from eSWT UI-thread
     */
    void handleShowCurrentEvent()
    {
        Logger.method(this, "handleShowCurrentEvent");
        isCurrent = true;

        // restore our scrolling position (eSWT resets it to 0 by default)
        swtSetScrollingPosition(vPosition, true);

        Item item = null;
        LayoutObject lo = null;
        while((lo = getNextLayoutObjectOfItem(lo, null)) != null)
        {
            if(lo.getOwningItem() != item)
            {
                // item border
                item = lo.getOwningItem();
                getItemLayouter(item).swtAddListeners(item, lo);
            }
        }

		swtApplyCurrentFocus();	
    }

    /**
     * Called when Form is about to be hidden.<br>
     * NOTE: this is called from eSWT UI-thread
     */
    void handleHideCurrentEvent()
    {
        Logger.method(this, "handleHideCurrentEvent");
        isCurrent = false;

        Item item = null;
        LayoutObject lo = null;
        while((lo = getNextLayoutObjectOfItem(lo, null)) != null)
        {
            if(lo.getOwningItem() != item)
            {
                // item border
                item = lo.getOwningItem();
                getItemLayouter(item).swtRemoveListeners(item, lo);
                getItemLayouter(item).swtHandleVisibilityChange(item, false);
            }
        }

		direction = NO_DIRECTION;
    }

    /**
     * Called when Form is beeing resized.<br>
     * NOTE: this is called from eSWT UI-thread
     */
    void handleResizeEvent(int width, int height)
    {
        // Logger.method(this, "handleResizeEvent");
        int numitems = getItemCount();
        for(int i = 0; i < numitems; i++)
        {
            getItem(i).invalidateCachedSizes();
        }
        ItemLayouter.swtUpdateStaticShellSize(width, height);
    }

    /**
     * Do form layout according to startIndex.
     *
     */
    void layoutForm(final int startIndex)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                swtLayoutForm(startIndex);
            }
        });
    }

    /**
     * eSWT callback for doLayout().
     */
    void swtLayoutForm(int startIndex)
    {
        int numItems = getItemCount();
        Logger.method(this, "swtLayoutForm", startIndex + " / " + numItems);

        swtUpdateFormComposite(false);
        if(numItems > 0)
        {
            Item previousItem = null;
            int startRowIndex = 0;

            if(startIndex > 0)
            {
                // Find row with previous item.
                previousItem = getItem(startIndex - 1);
                Row prevItemRow = getLastRowOfItem(previousItem);
                if(prevItemRow != null)
                {
                    startRowIndex = rows.indexOf(prevItemRow);
                }
            }

            // Clear rows starting from item - partial re-layouting
            swtClearRows(startRowIndex, previousItem);
            // Layout items
            for(int i = startIndex; i < numItems; i++)
            {
                swtLayoutItem(getItem(i));
            }
            // Update last row
            swtUpdateRow(getLastRow());
        }
        else
        {
            swtClearRows(0, null);
        }
        // check if we need to scroll to a particular item
        if(deferredScrollToItem != null)
        {
            swtSetCurrentItem(deferredScrollToItem);
            deferredScrollToItem = null;
        }
        swtUpdateFormComposite(true);
        swtHandleVisibilityChanges();

        if(Logger.isLogVerbose())
        {
            logFormLayout();
        }

		// clear invalid selected item
		swtCheckCurrentSelectedItem();
		
		if(currentSelectedItem != null
				&& (currentSelectedItem.isFocusable()))
		{
			swtApplyCurrentFocus();
		}
		else
		{
			// If there's no item currently selected try to find first
			// focusable item and set it current (if found):
			Item found = swtGetNextFocusableItem(
							 getItem(startIndex - 1), SWT.ARROW_RIGHT);
			if(found != null)
			{
				swtSetCurrentSelectedItem(found, NO_DIRECTION);
			}
			else
			{
				swtApplyCurrentFocus();
			}
		}		
    }

    /**
     * Returns if the parameter is a eSWT directional key code.
     *
     * @param keyCode key code
     */
    private boolean isDirectionKey(int keyCode)
    {
        return (keyCode == SWT.ARROW_DOWN || keyCode == SWT.ARROW_UP
                || keyCode == SWT.ARROW_LEFT || keyCode == SWT.ARROW_RIGHT);
    }

    /**
     * Handler for key events.<br>
     * The implementation moves focus and/or scrolls the form when
     * needed. The method is called by the Form.
     *
     * @param keyCode eSWT key code.
     * @param keyType eSWT key type.
     */
    final void handleKeyEvent(int keyCode, int keyType)
    {
//        Logger.method(this, "handleKeyEvent", currentSelectedItem,
//                      String.valueOf(keyCode), String.valueOf(keyType));

        boolean isDirectionalKey = isDirectionKey(keyCode);
        if(keyType == SWT.KeyDown && isDirectionalKey)
        {
            swtCheckCurrentSelectedItem();
        }

        if(currentSelectedItem != null)
        {
            if(getItemLayouter(currentSelectedItem).swtOfferKeyEvent(
                        currentSelectedItem, keyCode, keyType))
            {
                // if the key has been consumed
                return;
            }
        }

        // scrolling/focus traverse only happens on directional key's down event
        if(keyType == SWT.KeyDown && isDirectionalKey)
        {
            // try to find next focusable item
            Item next = swtGetNextFocusableItem(currentSelectedItem, keyCode);

            // if no visible & focusable item was found to transfer focus
            if(next == currentSelectedItem)
            {
                // try to scroll a bit
                swtSetScrollingPosition(getNextScrollingPosition(keyCode),
                                         true);
                // find next focusable after scrolling
                next = swtGetNextFocusableItem(currentSelectedItem, keyCode);
            }

            if(next != currentSelectedItem)
            {
                //textfield always have to be fully visible when focused.
                if(next instanceof TextField)
                {
                    swtScrollToItem(next);
                }
                swtSetCurrentSelectedItem(next, keyCode);
            }
        }
    }

    /**
     * Handler for pointer events.<br>
     * The method is called by the Form.
     *
     * @param x coordinate relative to scrolledComposite
     * @param y coordinate relative to scrolledComposite
     * @param type event type: SWT.MouseDown, SWT.MouseMove, SWT.MouseUp
     */
    final void handlePointerEvent(int x, int y, int type)
    {
//        Logger.method(this, "handlePointerEvent", String.valueOf(x),
//                      String.valueOf(y), String.valueOf(type));

        // TODO: change when DirectUI style arrives.
        /*
        Item item;
        if (type == SWT.MouseMove) {
            if (currentlyUnderMouse == null
                    || !currentlyUnderMouse.contains(x, y)) {
                if (currentlyUnderMouse != null) {
                    //currentlyUnderMouse.getControl().setCapture(false);
                }
                item = swtFindItemUnderMouse(x, y);
                if (item != null && item != currentSelectedItem
                        && item.isFocusable()) {
                    setCurrentItem(item);
                    item.internalSetFocused(true);
                    swtSetCurrentSelectedItem(item);
                    //following method causes all mouse events delivered to it

                    currentlyUnderMouse.getControl().setCapture(true);
                    Logger.warning("seting capture to:" + item);
                }
            }
            int currentVPosition = getVPosition();
            boolean isMouseDirectionUp = false;
            boolean doScrolling = false;
            int localY = y;

            if (y <= currentVPosition) {
                localY = Math.max(0, y);
                swtSetScrollingPosition(localY, true);
                isMouseDirectionUp = true;
                doScrolling = true;
            }
            else if (y > (currentVPosition + getFormHeight())) {
                //check for maxVPosition
                if (y > (swtGetMaxVPosition() + getFormHeight())) {
                    localY = swtGetMaxVPosition() + getFormHeight();
                }
                else {
                    localY = y;
                }
                currentVPosition = localY - getFormHeight();
                swtSetScrollingPosition(currentVPosition, true);

                isMouseDirectionUp = false;
                doScrolling = true;
            }
            if (mousePressed && doScrolling) {
                resetEventTimer(isMouseDirectionUp, localY);
            }
        }
        else
        if (type == SWT.MouseDown) {
            mousePressed = true;
            item = swtFindItemUnderMouse(x, y);
            if (item != null && item != currentSelectedItem
                    && item.isFocusable() && getForm().getShell() ==
                        getForm().getShell().getDisplay().getActiveShell()) {
                //swtScrollToItem(item);
                //following method causes all mouse events delivered to it

                //currentlyUnderMouse.getControl().setCapture(true);
            }
        }
        else if (type == SWT.MouseUp) {
            mousePressed = false;
            if (currentlyUnderMouse != null) {
                //currentlyUnderMouse.getControl().setCapture(false);
            }
        }*/
    }

    /**
     * Find item at the specified point.
     *
     * @param x coordinate.
     * @param y coordinate.
     * @return Item.
     */
    Item swtFindItemUnderMouse(int x, int y)
    {
        Row itemRow;
        for(int i = 0; i < getRowCount(); i++)
        {
            itemRow = getRow(i);
            if(itemRow.getYShift() <= y && y <= itemRow.getBottomPosition())
            {
                LayoutObject lo;
                for(int j = 0; j < itemRow.size(); j++)
                {
                    lo = itemRow.getLayoutObject(j);
                    if(lo.contains(x, y))
                    {
                        Logger.info("Item under mouse: "
                                    + lo.getOwningItem());
                        currentlyUnderMouse = lo;
                        return lo.getOwningItem();
                    }
                }
                break;
            }
        }
        return null;
    }

    /**
     * Gets next (or nearest) focusable item.
     *
     * @param fromItem Item where to start to search the next focusable item.
     * @param dir Search direction, one of the arrow key constants defined
     *      in class SWT.
     *
     * @return Nearest focusable item or null if no item found.
     */
    final Item swtGetNextFocusableItem(Item fromItem, int dir)
    {
        Item nextItem = fromItem;

        switch(dir)
        {
        case SWT.ARROW_RIGHT:
        {
            LayoutObject obj = getLastLayoutObjectOfItem(fromItem);
            while((obj = getNextLayoutObjectOfItem(obj, null)) != null)
            {
                Item owner = obj.getOwningItem();
                if(owner != null && owner != fromItem
                        && owner.isFocusable()
                        && isPartiallyVisible(obj, Config.DFI_VISIBILITY_PERCENT))
                {
                    nextItem = owner;
                    break;
                }
            }
            break;
        }

        case SWT.ARROW_LEFT:
        {
            LayoutObject obj = getFirstLayoutObjectOfItem(fromItem);
            while((obj = getPrevLayoutObjectOfItem(obj, null)) != null)
            {
                Item owner = obj.getOwningItem();
                if(owner != null && owner != fromItem
                        && owner.isFocusable()
                        && isPartiallyVisible(obj, Config.DFI_VISIBILITY_PERCENT))
                {
                    nextItem = owner;
                    break;
                }
            }
            break;
        }

        case SWT.ARROW_DOWN:
        {
            int minDist = Integer.MAX_VALUE;
            LayoutObject start = getLastLayoutObjectOfItem(fromItem);
            LayoutObject obj = start;
            while((obj = getNextLayoutObjectOfItem(obj, null)) != null)
            {
                Item owner = obj.getOwningItem();
                if(owner != null && owner != fromItem
                        && owner.isFocusable() && obj.isBelow(start)
                        && isPartiallyVisible(obj, Config.DFI_VISIBILITY_PERCENT))
                {
                    int dist = obj.distanceTo(start);
                    if(dist < minDist)
                    {
                        minDist = dist;
                        nextItem = owner;
                    }
                }
            }
            break;
        }

        case SWT.ARROW_UP:
        {
            int minDist = Integer.MAX_VALUE;
            LayoutObject start = getFirstLayoutObjectOfItem(fromItem);
            LayoutObject obj = start;
            while((obj = getPrevLayoutObjectOfItem(obj, null)) != null)
            {
                Item owner = obj.getOwningItem();
                if(owner != null && owner != fromItem
                        && owner.isFocusable() && obj.isAbove(start)
                        && isPartiallyVisible(obj, Config.DFI_VISIBILITY_PERCENT))
                {
                    int dist = obj.distanceTo(start);
                    if(dist < minDist)
                    {
                        minDist = dist;
                        nextItem = owner;
                    }
                }
            }
            break;
        }

        default:
        }

        return nextItem;
    }

    /**
     * Check if the currentSelectedItem is valid and visible. If not then it
     * sets it to null.
     */
    final void swtCheckCurrentSelectedItem()
    {
        if(currentSelectedItem != null)
        {
            if(currentSelectedItem.getParent() != getForm()
                    || !currentSelectedItem.isVisible())
            {
                // we need to find another
                Logger.method(this, "swtCheckCurrentSelectedItem");
                swtSetCurrentSelectedItem(null, NO_DIRECTION);
            }
        }
    }

    /**
     * Sets currentSelectedItem and sets focus to it.<br>
     * If one of form's items is already selected when this method is called,
     * removes focus from old item and then moves focus to new one.
     *
     * @param item Item to set as current selected. If null, nothing happens.
     * @param dir Direction which is delivered to layouter.
     */
    void swtSetCurrentSelectedItem(Item item, int dir)
    {
        if(currentSelectedItem != item)
        {
            Logger.info(this + "::SelectedItem: "
                        + currentSelectedItem + " --(" + dir + ")--> " + item);

            // Save direction
            direction = dir;
            // Remove focus from currentSelectedItem and notify its Layouter.
            if(currentSelectedItem != null)
            {
                getItemLayouter(currentSelectedItem).swtFocusLost(
                    currentSelectedItem);
            }

            // Set new currentSelectedItem, must be focusable or null
            currentSelectedItem = item;

            // Set focus to currentSelectedItem and notify its Layouter.
            if(currentSelectedItem != null)
            {
                getItemLayouter(currentSelectedItem).swtFocusGained(
                    currentSelectedItem, dir);
            }

            // Apply eSWT focus to currentSelectedItem's control
            swtApplyCurrentFocus();
        }
    }

    /**
     * Sets currentSelectedItem and sets focus to it.<br>
     * If one of form's items is already selected when this method is called,
     * removes focus from old item and then moves focus to new one.
     *
     * @param item Item to set as current selected. If null, nothing happens.
     */
    void swtSetCurrentSelectedItem(Item item)
    {
        if(currentSelectedItem != item)
        {
            Logger.info(this + "::SelectedItem: "
                        + currentSelectedItem + " ---> " + item);

            // Remove focus from currentSelectedItem and notify its Layouter.
            if(currentSelectedItem != null)
            {
                getItemLayouter(currentSelectedItem).swtFocusLost(
                    currentSelectedItem);
            }

            // Set new currentSelectedItem, must be focusable or null
            currentSelectedItem = item;

            // Set focus to currentSelectedItem and notify its Layouter.
            if(currentSelectedItem != null)
            {
                getItemLayouter(currentSelectedItem).swtFocusGained(
                    currentSelectedItem, NO_DIRECTION);
            }

            // Apply eSWT focus to currentSelectedItem's control
            //swtApplyCurrentFocus();
        }
    }

    /**
     * Sets focus to currentSelectedItem's control if its partially visible.
     * Otherwise it sets dummy focus to form's composite.<br>
     * <br>
     * Note that this method applies focus only to eSWT control. Item focus
     * update and layouter notifications are handled in method
     * <code>swtSetCurrentSelectedItem()</code>.<br>
     * If currentSelectedItem is null or form is not shown, this method has no
     * effect.
     */
    void swtApplyCurrentFocus()
    {
        if(isFormCurrent())
        {
            // if any of the Item's LayoutObjects is visible
            if(isItemPartiallyVisible(currentSelectedItem))
            {
//                Logger.method(this, "ApplyFocus", currentSelectedItem);
                swtSetFocusToFirstControl(currentSelectedItem);
            }
            else
            {
                Logger.method(this, "ApplyFocus", "dummy");
                formComposite.forceFocus();
            }
        }
    }

    /**
     * If the Item is valid and it is layouted, then sets the Item's first
     * LayoutObject focused.
     *
     * @param item an item which first LayoutObject is set focused.
     */
    void swtSetFocusToFirstControl(Item item)
    {
        if(item != null && item.isFocusable())
        {
            LayoutObject lo = getFirstLayoutObjectOfItem(item);
            if(lo != null)
            {
                lo.getControl().forceFocus();
            }
        }
    }

    /**
     * Gets Current selected item.
     *
     * @return Current selected item. May also return null.
     */
    Item getCurrentSelectedItem()
    {
        return currentSelectedItem;
    }

    /**
     * Get the direction of scrolling.
     *
     * @return direction of scrolling.
     */
    int getDirection()
    {
        return direction;
    }

    /**
     * Set focus to an item if it is focusable and scroll form to make it
     * visible if it is not.
     *
     * @param item Item to set as current item.
     */
    void setCurrentItem(final Item item)
    {
//        Logger.method(this, "setCurrentItem", item);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                swtSetCurrentItem(item);
            }
        });
    }

    /**
     * eSWT callback for setCurrentItem.
     */
    boolean swtSetCurrentItem(Item item)
    {
        if(item != null)
        {
            if(isItemLayouted(item))
            {
                swtScrollToItem(item);
                deferredScrollToItem = null;

				if(item.isFocusable())
				{
					swtSetCurrentSelectedItem(item, NO_DIRECTION);
					Logger.info("swtSetCurrentItem" + item);
				}
                return true;
            }
            else
            {
                deferredScrollToItem = item;
            }
        }
        return false;
    }

    void swtScrollToItem(Item item)
    {
        if(item != null)
        {
            int pos = getItemBottomPosition(item) - getFormHeight();
            if(!isItemPartiallyVisible(item))
            {
                swtSetScrollingPosition(pos, true);
            }
            else if(item instanceof TextField && !isItemFullyVisible(item))
            {
                swtSetScrollingPosition(pos, true);
            }
        }
    }

    void swtScrolltoRegion(int yTop, int yBottom, int swtDir)
    {
        if(yTop < vPosition || yBottom > vPosition + getFormHeight())
        {
            // if the region is somewhat outside the screen
            if(swtDir == SWT.ARROW_DOWN || swtDir == SWT.ARROW_RIGHT)
            {
                // align to top
                swtSetScrollingPosition(yTop, true);
            }
            else
            {
                // align to bottom
                swtSetScrollingPosition(yBottom - getFormHeight(), true);
            }
        }
    }

    void swtScrollIfNeeded(final int top, final int bottom)
    {
        if(bottom > vPosition + getFormHeight())
        {
            swtSetScrollingPosition(bottom - (getFormHeight() / 2), true);
        }
        else if(top < vPosition)
        {
            swtSetScrollingPosition(bottom - (getFormHeight() / 2), true);
        }
    }

    /**
     * Get control's position relative to composite.
     */
    void getControlPositionOnComposite(Control control, Point location)
    {
        if(control != formComposite)
        {
            Point rel = control.getLocation();
            location.x += rel.x;
            location.y += rel.y;
            getControlPositionOnComposite(control.getParent(), location);
        }
    }

    /**
     * eSWT specific calls to do update ScrolledComposite.
     */
    private void swtUpdateFormComposite(boolean show)
    {
        if(getRowCount() > 0)
        {
            if(show)
            {
                formComposite.updateScrollbar(getLastRow().getBottomPosition());
                formComposite.pack();
            }
        }
        // Could happen if changing item from very tall to very short.
        // so we have to update VPosition
        if(getVPosition() > swtGetMaxVPosition())
        {
            swtSetScrollingPosition(swtGetMaxVPosition(), false);
        }

        formComposite.setRedraw(show);
        formComposite.setVisible(show);
    }

    /**
     * Clean all form rows starting from startIndex.
     *
     * @param startIndex Start row from which to clean.
     * @param keepItem - item in a startRow which shouldn't be recreated.
     */
    private void swtClearRows(int startIndex, Item keepItem)
    {
//        Logger.method(this, "clearRows", String.valueOf(startIndex), keepItem);
        Row row = null;
        for(int i = (getRowCount() - 1); i >= startIndex; i--)
        {
            row = getRow(i);
            if(row.cleanRow(keepItem))
            {
                break;
            }
            else
            {
                rows.removeElement(row);
            }
        }

        // one row always should be available.
        if((getRowCount() == 0))
        {
            // rows.addElement(tempRow);
            currentHLayoutDirective = Item.LAYOUT_DEFAULT;
            Row newRow = new Row(ItemLayouter.getMaximumItemWidth(null),
                                 getCurrentHLayoutDirective());
            rows.addElement(newRow);
        }
    }

    /**
     * Update Row's internal layout. Handles LAYOUT_2 related post-processing.
     *
     * @param row Row
     */
    private void swtUpdateRow(Row row)
    {
        if(row != null)
        {
            //Logger.verbose("updateRow start: " + row);
            int numShrink = row.getNumLayoutObjects(Item.LAYOUT_SHRINK);
            int numExpand = row.getNumLayoutObjects(Item.LAYOUT_EXPAND);
            //Logger.verbose("shrink: " + numShrink + " expand: " + numExpand);

            int vMask = Item.LAYOUT_VSHRINK | Item.LAYOUT_VEXPAND;
            // Expand items vertically with VSHRINK or VEXPAND layout directive
            LayoutObject lo = null;
            int maxHeight = row.getRowHeight(vMask);
            while((lo = row.getNextLayoutObject(lo, vMask)) != null)
            {
                if(lo.getOwningItem().hasLayout(Item.LAYOUT_VSHRINK))
                {
                    int pref = lo.getOwningItem().getPreferredHeight();
                    getItemLayouter(lo.getOwningItem()).swtResizeObject(lo,
                            lo.getWidth(), Math.min(pref, maxHeight));
                }
                else if(lo.getOwningItem().hasLayout(Item.LAYOUT_VEXPAND))
                {
                    getItemLayouter(lo.getOwningItem()).swtResizeObject(lo,
                            lo.getWidth(), maxHeight);
                }
            }

            // Expand items with SHRINK layout directive
            if(numShrink > 0)
            {
                // Get extra space before shrink and expand
                int offset = row.getFreeSpace() / numShrink;
                // Logger.verbose("shrinkOffset: " + offset);
                if(offset >= 0)
                {
                    while((lo = row.getNextLayoutObject(lo, Item.LAYOUT_SHRINK)) != null)
                    {
                        int pref = lo.getOwningItem().getPreferredWidth();
                        int min = lo.getOwningItem().getMinimumWidth();
                        int itemWidth = Math.min(pref, min + offset);
                        getItemLayouter(lo.getOwningItem()).swtResizeObject(lo,
                                itemWidth, lo.getHeight());
                    }
                }
            }

            // Expand items with EXPAND layout directive
            if(numExpand > 0)
            {
                // Get extra space after shrink but before expand
                int offset = row.getFreeSpace(Item.LAYOUT_EXPAND) / numExpand;
                if(offset >= 0)
                {
                    // Logger.verbose("expandOffset: " + offset);
                    while((lo = row.getNextLayoutObject(lo, Item.LAYOUT_EXPAND)) != null)
                    {
                        int pref = lo.getOwningItem().getPreferredWidth();
                        getItemLayouter(lo.getOwningItem()).swtResizeObject(lo,
                                pref + offset, lo.getHeight());
                    }
                }
            }

            //if (numShrink > 0 || numExpand > 0) {
            row.updateRowInternals();
            //}

            row.updateRowLayout(form.getLeftRightLanguage());
            // Logger.verbose("updateRow: " + row);
        }
    }

    /**
     * Add a new Row.
     */
    private Row swtAddNewRow()
    {
        Row lastRow = getLastRow();
        swtUpdateRow(lastRow);
        int yShift = (lastRow == null ? 0 : lastRow.getBottomPosition());
        // create new Row
        Row newRow = new Row(ItemLayouter.getMaximumItemWidth(null),
                             getCurrentHLayoutDirective());
        newRow.setYShift(yShift);
        rows.addElement(newRow);
        return newRow;
    }

    /**
     * Add a LayoutObject to the last Row.
     *
     * @param layoutObject the layout object
     */
    void swtAddNewLayoutObject(LayoutObject layoutObject)
    {
    	if(!layoutObjects.contains(layoutObject))
    	{
    		layoutObjects.addElement(layoutObject);
    	}
        Row lastRow = getLastRow();
        // check if the current Row is full
        if(!lastRow.isEmpty()
                && lastRow.getFreeSpace() < layoutObject.getWidth())
        {
            lastRow = swtAddNewRow();
        }
        lastRow.swtAddLayoutObject(layoutObject);
        layoutObject.setRowIdx(getRowCount() - 1);
    }

    /**
     * Optionally add a new Row and adds a LayoutObject.
     *
     * @param layoutObject the layout object
     * @param newRow adds a new row if true. If false, adds new row only if
     *            there's no space for layoutObject in current row.
     */
    void swtAddNewLayoutObject(LayoutObject layoutObject, boolean newRow)
    {
        if(newRow)
        {
            swtAddNewRow();
        }
        swtAddNewLayoutObject(layoutObject);
    }

    /**
     * Layout item in a row, if needed new row is added.
     *
//     * @param row - where to startLayout.
     * @param item - Item to Layout
     */
    private void swtLayoutItem(Item item)
    {
        Row lastRow = getLastRow();
        boolean hlChange = setCurrentHLayoutDirective(item.internalGetLayout());
        if(hlChange || getItemNewLineBefore(item))
        {
            // newline directive or horizontal layout changed
            if(lastRow.isEmpty())
            {
                // if the current/last row is empty - use that
                lastRow.setRowHLayout(getCurrentHLayoutDirective());
            }
            else
            {
                swtAddNewRow();
            }
        }

        // Use the specific layouter to layout item in the last row
        getItemLayouter(item).swtLayoutItem(getLastRow(), item);

        if(form.swtIsShown())
        {
            LayoutObject lo = getFirstLayoutObjectOfItem(item);
            if(lo != null)
            {
                getItemLayouter(item).swtAddListeners(item, lo);
            }
        }

        if(getItemNewLineAfter(item))
        {
            swtAddNewRow();
        }
    }

    /**
     * Set Form's Layout directive. if it differ from current set flag
     * startFromNewLine = true;
     *
     * @param newLayoutDirective
     * @return true if a layout change has occured
     */
    private boolean setCurrentHLayoutDirective(int newLayoutDirective)
    {
        int newHLayoutDirective = Item.getHorizontalLayout(newLayoutDirective);
        if((newHLayoutDirective != currentHLayoutDirective)
                && (newHLayoutDirective != Item.LAYOUT_DEFAULT))
        {
            currentHLayoutDirective = newHLayoutDirective;
            return true;
        }
        return false;
    }

    /**
     * Get Form current Layout directive.
     *
     * @return current Layout directive for Form.
     */
    private int getCurrentHLayoutDirective()
    {
        if(currentHLayoutDirective == Item.LAYOUT_DEFAULT)
        {
            return getLanguageSpecificLayoutDirective();
        }
        return currentHLayoutDirective;
    }

    /**
     * Returns language specific layout directive.
     *
     * @return LAYOUT_LEFT or LAYOUT_RIGHT.
     */
    int getLanguageSpecificLayoutDirective()
    {
        if(form.getLeftRightLanguage())
        {
            return Item.LAYOUT_LEFT;
        }
        else
        {
            return Item.LAYOUT_RIGHT;
        }
    }

    private boolean getItemNewLineBefore(Item item)
    {
        return ((item.internalGetLayout() & Item.LAYOUT_NEWLINE_BEFORE) != 0)
                /*|| item.hasLabel()*/
                || (item instanceof StringItem && ((StringItem)item).getText().startsWith("\n"))
                || item instanceof ChoiceGroup || item instanceof TextField || item instanceof Gauge || item instanceof DateField
                ;
    }

    private boolean getItemNewLineAfter(Item item)
    {
        return ((item.internalGetLayout() & Item.LAYOUT_NEWLINE_AFTER) != 0)
                || (item instanceof StringItem && ((StringItem)item).getText().endsWith("\n"))
                || item instanceof ChoiceGroup || item instanceof TextField || item instanceof Gauge || item instanceof DateField
                ;
    }

    boolean isItemLayouted(Item item)
    {
        return (getFirstLayoutObjectOfItem(item) != null);
    }

    /**
     * Returns if the form is shown.
     */
    boolean isFormCurrent()
    {
        return isCurrent;
    }

    /**
     * Returns if the region is partially visible.
     *
     * @return true if visible
     */
    boolean isPartiallyVisible(int yTop, int yBottom)
    {
        int vBottomPosition = vPosition + getFormHeight();
        if((vPosition <= yTop && vBottomPosition <= yTop)
                || (vPosition >= yBottom && vBottomPosition >= yBottom))
        {
            return false;
        }
        return true;
    }

    /**
     * Returns if at least the region's given percentage is visible.
     */
    boolean isPartiallyVisible(int yTop, int yBottom, int minPercent)
    {
        int visPercent = getVisibilityPercent(yTop, yBottom);
        if(visPercent > minPercent)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns the region's visibility percentage.
     */
    int getVisibilityPercent(int yTop, int yBottom)
    {
        if(yTop >= yBottom)
        {
            return 0;
        }
        int vBottomPosition = vPosition + getFormHeight();
        int r1 = Math.max(vPosition, Math.min(yTop, vBottomPosition));
        int r2 = Math.min(vBottomPosition, Math.max(yBottom, vPosition));
        return ((r2 - r1) * 100) / (yBottom - yTop);
    }

    /**
     * Returns if the LayoutObject is partially visible.
     */
    boolean isPartiallyVisible(LayoutObject lo)
    {
        if(lo != null)
        {
            return isPartiallyVisible(lo.getY(), lo.getY() + lo.getHeight());
        }
        return false;
    }

    /**
     * Returns if at least the LayoutObject's given percentage is visible.
     */
    boolean isPartiallyVisible(LayoutObject lo, int minPercent)
    {
        if(lo != null)
        {
            return isPartiallyVisible(lo.getY(), lo.getY() + lo.getHeight(),
                                      minPercent);
        }
        return false;
    }

    /**
     * Returns if the Item is partially visible (if one of its LayoutObjects is
     * partially visible).
     *
     * @param item the Item
     * @return true if partially visible
     */
    boolean isItemPartiallyVisible(Item item)
    {
        if(item != null)
        {
            LayoutObject lo = null;
            while((lo = getNextLayoutObjectOfItem(lo, item)) != null)
            {
                if(isPartiallyVisible(lo))
                {
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * Returns true if item is fully visible.
     *
     * @param item the Item.
     * @return true if fully visible.
     */
    boolean isItemFullyVisible(Item item)
    {
        if(item != null)
        {
            LayoutObject lo = null;
            while((lo = getNextLayoutObjectOfItem(lo, item)) != null)
            {
                if(!isLOFullyVisible(lo))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns if the LayoutObject is fully visible.
     */
    boolean isLOFullyVisible(LayoutObject lo)
    {
        if(lo != null)
        {
            return isFullyVisible(lo.getY(), lo.getY() + lo.getHeight());
        }
        return false;
    }

    /**
     * Returns if the region is fully visible.
     *
     * @return true if visible
     */
    boolean isFullyVisible(int yTop, int yBottom)
    {
        int vBottomPosition = vPosition + getFormHeight();
        if((vPosition <= yTop && vBottomPosition >= yBottom))
        {
            return true;
        }
        return false;
    }

    int getItemTopPosition(Item item)
    {
        LayoutObject lo = getFirstLayoutObjectOfItem(item);
        if(lo != null)
        {
            return lo.getY();
        }
        return 0;
    }

    int getItemBottomPosition(Item item)
    {
        LayoutObject lo = getLastLayoutObjectOfItem(item);
        if(lo != null)
        {
            return lo.getY() + lo.getHeight();
        }
        return 0;
    }

    int getItemCount()
    {
        return form.size();
    }

    Item getItem(int index)
    {
        try
        {
            return (Item) form.getItems().elementAt(index);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            // Logger.exception(e);
            return null;
        }
    }

    int getItemIndex(Item item)
    {
        return form.getItems().indexOf(item);
    }

    int getRowCount()
    {
        return rows.size();
    }

    /**
     * Return the Row with the given index.
     *
     * @param index Row's index
     * @return a Row
     */
    Row getRow(int index)
    {
        try
        {
            return (Row) rows.elementAt(index);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            Logger.exception("getRow", e);
            return null;
        }
    }

    /**
     * Return the Row to which the given LayoutObject belongs.
     *
     * @param lo LayoutObject
     * @return the owning Row
     */
    Row getRow(LayoutObject lo)
    {
        try
        {
            return getRow(lo.getRowIdx());
        }
        catch(NullPointerException e)
        {
            // Logger.exception("getRow", e);
            return null;
        }
    }

    /**
     * Returns the last row of the form.
     */
    Row getLastRow()
    {
        try
        {
            return (Row) rows.lastElement();
        }
        catch(NoSuchElementException nse)
        {
            // Logger.exception("getLastRow", nse);
            return null;
        }
    }

    /**
     * Find last row which contains corresponding Item.
     *
     * @param item Item in a Row.
     * @return Last Row with that item.
     */
    Row getLastRowOfItem(Item item)
    {
        return getRow(getLastLayoutObjectOfItem(item));
    }

    /**
     * Get Form which requires layout.
     *
     * @return form.
     */
    Form getForm()
    {
        return form;
    }

    /**
     * Form's content height.
     */
    int getFormHeight()
    {
        return form.getHeight();
    }

    /**
     * Form's content width.
     */
    int getFormWidth()
    {
        return form.getWidth();
    }

    /**
     * Returns LayoutObject for the given Item.
     *
     * @param item Item to layout
     * @return LayoutObject
     */
    LayoutObject getLayoutObject(Item item)
	{
		int num = layoutObjects.size();

		for(int i = 0; i < num; i++)
		{
			if(((LayoutObject)layoutObjects.elementAt(i)).getOwningItem() == item)
			{
				LayoutObject lo = (LayoutObject)(layoutObjects.elementAt(i));
				lo.setRowIdx(-1);
				return lo;
			}
		}

		return null;
	}

    /**
     * Removes Layout Object for the given Item.
     *
     * @param item Item to remove LayoutObject
     */
    void removeLayoutObject(Item item)
	{

		int num = layoutObjects.size();

		for(int i = 0; i < num; i++)
		{
			if(((LayoutObject)layoutObjects.elementAt(i)).getOwningItem() == item)
			{
				LayoutObject lo = (LayoutObject)(layoutObjects.elementAt(i));
				layoutObjects.removeElement(lo);
				lo.dispose();
				break;
			}
		}
	}

    void removeAllLayoutObjects(Item item)
    {

        int num = layoutObjects.size();

        for(int i = 0; i < num; i++)
        {
            if(((LayoutObject)layoutObjects.elementAt(i)).getOwningItem() == item)
            {
                while (layoutObjects.size() > i && ((LayoutObject)layoutObjects.elementAt(i)).getOwningItem() == item) {
                    LayoutObject lo = (LayoutObject)(layoutObjects.elementAt(i));
                    layoutObjects.removeElement(lo);
                    lo.dispose();
                }
                break;
            }
        }
    }

    /**
     * Removes all Layout Objects from the FormLayouter.
     */
    void removeAllLayoutObjects()
	{
		Enumeration e = layoutObjects.elements();

		while(e.hasMoreElements())
		{
			LayoutObject lo = (LayoutObject)e.nextElement();
			layoutObjects.removeElement(lo);
			lo.dispose();
		}
	}

    /**
     * Returns the next LayoutObject belonging to this Item.<br>
     *
     * @param lo starting LayoutObject; if null then it start from first.
     * @param item specifies the parent Item; null means any Item
     * @return the next LayoutObject in the layout.
     */
    LayoutObject getNextLayoutObjectOfItem(LayoutObject lo, Item item)
    {
        int startRow = (lo == null ? 0 : lo.getRowIdx());
        LayoutObject temp = null;
        for(int i = startRow; i < getRowCount(); i++)
        {
            temp = getRow(i).getNextLayoutObject(lo, item);
            if(temp != null && temp != lo)
            {
                return temp;
            }
        }
        return null;
    }

    /**
     * Returns the previous LayoutObject belonging to this Item.<br>
     *
     * @param lo starting LayoutObject; if null then it start from last.
     * @param item specifies the parent Item; null means any Item
     * @return the previous LayoutObject in the layout.
     */
    LayoutObject getPrevLayoutObjectOfItem(LayoutObject lo, Item item)
    {
        int startRow = (lo == null ? rows.size() - 1 : lo.getRowIdx());
        LayoutObject temp = null;
        for(int i = startRow; i >= 0; i--)
        {
            temp = getRow(i).getPrevLayoutObject(lo, item);
            if(temp != null && temp != lo)
            {
                return temp;
            }
        }
        return null;
    }

    /**
     * Returns the first LayoutObject of a layouted item.
     *
     * @param item
     * @return the LO, or NULL if the item is not layouted
     */
    LayoutObject getFirstLayoutObjectOfItem(Item item)
    {
        if(item != null)
        {
            return getNextLayoutObjectOfItem(null, item);
        }
        return null;
    }

    /**
     * Returns the last LayoutObject of a layouted item.
     *
     * @param item
     * @return the LO, or NULL if the item is not layouted
     */
    LayoutObject getLastLayoutObjectOfItem(Item item)
    {
        if(item != null)
        {
            return getPrevLayoutObjectOfItem(null, item);
        }
        return null;
    }

    /**
     * Update item state in form.
     *
     * @param item
     * @param updateReason
     * @param param additional parameter
     */
    void updateItemState(Item item, int updateReason, Object param)
    {
//        Logger.method(this, "updateItemState", item,
//                      String.valueOf(updateReason), param);

		LayoutObject lo = getFirstLayoutObjectOfItem(item);

		if(lo != null)
		{
			getItemLayouter(item).updateItem(item, lo.getControl(), updateReason,
										 param);
		}

        // Clean reason - without resizing flags
        int reason = updateReason & Item.UPDATE_SIZE_MASK;
        switch(reason)
        {
        case Item.UPDATE_ADDCOMMAND:
        {
            if(isFormCurrent() && param != null)
            {
                if(lo != null && param instanceof Command)
                {
                    lo.addCommand((Command) param);
                }
            }
            break;
        }
        case Item.UPDATE_REMOVECOMMAND:
        {
            if(isFormCurrent() && param != null)
            {
                if(lo != null && param instanceof Command)
                {
                    lo.removeCommand((Command) param);
                }
            }
            break;
        }
        }

        // Check this always - because this is a flag
        if((updateReason & Item.UPDATE_HEIGHT_CHANGED)
                == Item.UPDATE_HEIGHT_CHANGED)
        {
            resizeItemAndShift(item);
        }
    }

    int swtGetMaxVPosition()
    {
        return formComposite.getSize().y - getFormHeight();
    }

    /**
     * Called by key Form to compute new vertical coordinate to position form's
     * content.
     *
     * @param swtDir scrolling direction.
     * @return New vertical position of Form's content.
     */
    protected int getNextScrollingPosition(int swtDir)
    {
        boolean scrollDown = (swtDir == SWT.ARROW_DOWN
                              || swtDir == SWT.ARROW_RIGHT);
        int formHeight = getFormHeight();
        int refPoint;
        int ret = vPosition;
        if(scrollDown)
        {
            ret += formHeight / 5;
            refPoint = (vPosition + 1) + formHeight;
        }
        else
        {
            ret -= formHeight / 5;
            refPoint = (vPosition - 1);
        }

        Row row = null;
        for(int i = 0; i < getRowCount(); i++)
        {
            row = getRow(i);
            if(row.isInsideRow(refPoint)
                    && (row.getRowHeight() < formHeight))
            {
                if(scrollDown)
                {
                    ret = row.getBottomPosition() - formHeight;
                }
                else
                {
                    ret = row.getYShift();
                }
                break;
            }
        }

        return ret;
    }

    /**
     * Set the scrolling to the specified position.<br>
     * This method also updates the Form's scrollbars.
     *
     * @param position new position
     */
    void swtSetScrollingPosition(int position, boolean keyNav)
    {
        // check constraints
        int newVPosition = position;
        int maxVPos = swtGetMaxVPosition();
        newVPosition = Math.min(newVPosition, maxVPos);
        newVPosition = Math.max(newVPosition, 0);

        vPosition = newVPosition;
        formComposite.setRedraw(false);
        formComposite.setOrigin(0, vPosition, keyNav);
        formComposite.pack();
        formComposite.setRedraw(true);

        swtHandleVisibilityChanges();
    }

    /**
     * Returns the scrolling position.
     */
    protected int getScrollingPosition()
    {
        return vPosition;
    }

    /**
     * Updates visibility status of all items.
     */
    protected void swtHandleVisibilityChanges()
    {
        // Logger.method(this, "swtHandleVisibilityChanges");
        boolean shown = false;
        Item item = null;
        LayoutObject lo = null;
        // Go through all LayoutObjects and check/update visibilities
        while((lo = getNextLayoutObjectOfItem(lo, null)) != null)
        {
            // check if owning item is changing
            if(lo.getOwningItem() != item)
            {
                if(item != null)
                {
                    // set current item's visibility
                    getItemLayouter(item).swtHandleVisibilityChange(item, shown);
                }
                // new item
                item = lo.getOwningItem();
                shown = false;
            }

            // track current item's visibility
            if(!shown && isFormCurrent() && isPartiallyVisible(lo))
            {
                shown = true;
            }
        }

        // call it for last item
        if(item != null)
        {
            getItemLayouter(item).swtHandleVisibilityChange(item, shown);
        }

		swtCheckCurrentSelectedItem();
    }

    /**
     * Changes item size and does shift of all Rows.
     *
     * @param item - item to changeSize.
     */
    void resizeItemAndShift(final Item item)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                int newVPosition = swtResizeItemAndShift(item);
                if(newVPosition != vPosition)
                {
                    swtSetScrollingPosition(newVPosition, true);
                }
                else
                {
                    swtHandleVisibilityChanges();
                }
            }
        });
    }

    /**
     * eSWT callback for resizeItemAndShift.
     *
     * @param item Item to be resized.
     * @return new scrolling vPosition
     */
    int swtResizeItemAndShift(Item item)
    {
		// save the state of the last row before resizing and Shifting.
		boolean itemWasVisible = isItemPartiallyVisible(item);
        Row row = getLastRowOfItem(item);
        if (row == null) {
            return 0;
        }
        int deltaYShift = row.getRowHeight();
        // if we un-comment this then when we set true,
        // focus will jump to first item.
        // swtUpdateFormComposite(false);
        LayoutObject lo = getFirstLayoutObjectOfItem(item);
        if(lo != null)
        {
            getItemLayouter(item).swtResizeObject(lo);
        }

        swtUpdateRow(row);

        /*
        // to avoid double call of updateRowInternals
        if (row.getNumLayoutObjects(Item.LAYOUT_SHRINK) > 0
                || row.getNumLayoutObjects(Item.LAYOUT_EXPAND) > 0) {
            swtUpdateRow(row);
        }
        else {
            row.updateRowInternals();
        }
        */
        deltaYShift = row.getRowHeight() - deltaYShift;
        // row's height change - all remaining rows are shifted with this.

        Row lastRow = getLastRow();
        if(row != lastRow)
        {
            for(int i = rows.indexOf(row) + 1; i < getRowCount(); i++)
            {
                row = getRow(i);
                row.setYShift(row.getYShift() + deltaYShift);
                swtUpdateRow(row);
            }
        }

        // following code updates scrolling if needed.
        // need to check where in the form resize happeneed.

        int itemRowYShift = getLastRowOfItem(item).getYShift();
        // vPosition should be changed in syncexec
        int newVPosition = vPosition;

        // we need to scroll only if changes happened above the screen.
        if(newVPosition >= itemRowYShift)
        {
            newVPosition = Math.max(0, newVPosition + deltaYShift);
        }
        // check to avoid gap in the bottom of the form
        if(newVPosition + getFormHeight() > lastRow.getBottomPosition())
        {
            newVPosition = Math.max(0,
                                    lastRow.getBottomPosition() - getFormHeight());
        }

        swtUpdateFormComposite(true);

        // formComposite.pack();
        // formComposite.updateScrollbarSize(lastRow.getBottomPosition());

		if(item == currentSelectedItem)
		{
			if(itemWasVisible)
			{
				int itemTop = getItemTopPosition(item);
				int itemBottom = getItemBottomPosition(item);
				// currentSelectedItem has to be focused if it was focused
				// before resizing e.g TextField when it is resized by adding a
				// new row and it was in the bottom of the Screen.
				if(newVPosition <= itemTop
						&& (newVPosition + getFormHeight()) >= itemBottom)
				{
					// do not change vPosition;
				}
				else if(newVPosition > itemTop)
				{
					newVPosition = itemTop;
				}
				else if((newVPosition + getFormHeight()) < itemBottom)
				{
					newVPosition = itemBottom - getFormHeight();
				}
			}
		}
		return newVPosition;
    }

    /**
     * Gets layouter that can layout the specified item.
     *
     * @param item Item to be layouted.
     * @return ItemLayouter or null if no Layouter found.
     */
    protected final ItemLayouter getItemLayouter(Item item)
    {
        if(item instanceof StringItem)
        {
            return sIL;
        }
        else if(item instanceof ImageItem)
        {
            return imIL;
        }
        else if(item instanceof Gauge)
        {
            return gL;
        }
        else if(item instanceof TextField)
        {
            return tfL;
        }
        else if(item instanceof DateField)
        {
            return dfL;
        }
        else if(item instanceof ChoiceGroup)
        {
            return cgL;
        }
        else if(item instanceof CustomItem)
        {
            return ciL;
        }
        else if(item instanceof Spacer)
        {
            return sL;
        }
        return null;
    }

    /**
     * Returns eSWT Control that represents the item specified.
     */
    Control getItemControl(final Item item)
    {
        final LayoutObject lo = getFirstLayoutObjectOfItem(item);
        if(lo != null)
        {
            Displayable.syncExec(new Runnable()
            {
                public void run()
                {
                    itemMainControl = getItemLayouter(item)
                                      .swtFindSpecificControl(item, lo.getControl());
                }
            });
            return itemMainControl;
        }
        return null;
    }

    void updateScrolling(final int value, final boolean keyNav)
    {
        Logger.method("updateScrolling", String.valueOf(value));
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {

                swtSetScrollingPosition(value, keyNav);

            }
        });
    }

    /**
     * Get current scrolling value.
     *
     * @return vPosition.
     */
    int getVPosition()
    {
        return vPosition;
    }


	private Timer eventTimer = new Timer();
	private EventGeneratorTask eventTask;

	/**
	 * Reset timer for do layout with a given start index.
	 */
	private void resetEventTimer(boolean directionUp, int y)
	{
		if(eventTimer != null)
		{
			if(eventTask != null)
			{
				eventTask.cancel();
				eventTask = null;
			}
			// schedule new timer
			eventTask = new EventGeneratorTask(directionUp, y);
			eventTimer.schedule(eventTask, Config.DFI_EVENT_TIMER_DELAY);
		}
	}

	/**
	 * Form Timer task. Triggers the formComposite to Layout.
	 */
	class EventGeneratorTask extends TimerTask
	{

		private boolean isUpDirection;
		private int localY;

		public EventGeneratorTask(boolean direction, int y)
		{
			isUpDirection = direction;
			localY = y;
			Logger.info("y is " + localY);
		}

		public void run()
		{
			if(isUpDirection)
			{
				localY -= Config.DFI_EVENT_MOVE_DELTA;
			}
			else
			{
				localY += Config.DFI_EVENT_MOVE_DELTA;
			}
			handlePointerEvent(0, localY, SWT.MouseMove);
		}
	}

}
