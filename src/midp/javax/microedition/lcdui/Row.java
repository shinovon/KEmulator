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

import java.util.Vector;

/**
 * Class which represents one row in the Form.
 */
class Row
{

    private Vector layoutObjects = new Vector();

    private int xShift;
    private int yShift;
    private int rowHeight;
    private int rowWidth;
    private int occupiedSpace;

    private int rowHLayout;

    /**
     * Constructor.
     *
     * @param rowWidth - total row width.
     * @param hLayout horizontal layout
     */
    Row(int rowWidth, int hLayout)
    {
        this.rowWidth = rowWidth;
        setRowHLayout(hLayout);
    }

    /**
     * Add new LayoutObject to the Row.
     *
     * @param layoutObj structure which represents Item or part Of Item.
     */
    void addLayoutObject(final LayoutObject layoutObj)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                swtAddLayoutObject(layoutObj);
            }
        });
    }

    /**
     * eSWT specific calls to implement addLayoutObject.
     *
     * @param lo structure which represents Item or part Of Item.
     */
    void swtAddLayoutObject(LayoutObject lo)
    {
        layoutObjects.addElement(lo);
        // update actual occupiedSpace
        occupiedSpace += lo.getWidth();
        // update rowHeight
        rowHeight = Math.max(rowHeight, lo.getHeight());
    }

    /**
     * Get the number of items with the given layout.
     */
    int getNumLayoutObjects(int layoutMask)
    {
        int ret = 0;
        int num = layoutObjects.size();
        LayoutObject lo = null;
        for(int i = 0; i < num; i++)
        {
            lo = (LayoutObject) layoutObjects.elementAt(i);
            if(lo.getOwningItem().hasLayout(layoutMask))
            {
                ret++;
            }
        }
        return ret;
    }

    /**
     * Get available space in the Row for layout calculation.
     *
     * @return space available for Items on that Row in pixels.
     */
    int getFreeSpace()
    {
        return getFreeSpace(Item.LAYOUT_SHRINK | Item.LAYOUT_EXPAND);
    }

    /**
     * Get free space based on LAYOUT mask. Item matching the layout mask will
     * be counted with their layouting size (not actual size). Used for LAYOUT_2
     * row resizing.
     *
     * @param layoutMask layout mask
     */
    int getFreeSpace(int layoutMask)
    {
        int ret = 0;
        int num = layoutObjects.size();
        LayoutObject lo = null;
        for(int i = 0; i < num; i++)
        {
            lo = (LayoutObject) layoutObjects.elementAt(i);
            ret += getObjectWidth(lo, layoutMask);
        }
        return rowWidth - ret;
    }

    /**
     * Get row height based on LAYOUT mask. Item matching the layout mask will
     * be counted with their layouting size (not actual size). Used for LAYOUT_2
     * row resizing.
     *
     * @param layoutMask layout mask
     */
    int getRowHeight(int layoutMask)
    {
        int ret = 0;
        int num = layoutObjects.size();
        LayoutObject lo = null;
        for(int i = 0; i < num; i++)
        {
            lo = (LayoutObject) layoutObjects.elementAt(i);
            ret = Math.max(ret, getObjectHeight(lo, layoutMask));
        }
        return ret;
    }

    private int getObjectWidth(LayoutObject lo, int layoutMask)
    {
        if(lo.getOwningItem().hasLayout(layoutMask))
        {
            // this returns the min/pref width
            return lo.getOwningItem().getLayoutWidth();
        }
        else
        {
            return lo.getWidth();
        }
    }

    private int getObjectHeight(LayoutObject lo, int layoutMask)
    {
        if(lo.getOwningItem().hasLayout(layoutMask))
        {
            // this returns the min/pref width
            return lo.getOwningItem().getLayoutHeight();
        }
        else
        {
            return lo.getHeight();
        }
    }

    /**
     * Clean row, free resources.
     *
     * @param keepItem Last Item to leave in a row.
     */
    boolean cleanRow(Item keepItem)
    {
        LayoutObject lo = null;
        for(int i = (layoutObjects.size() - 1); i >= 0; i--)
        {
            lo = (LayoutObject) layoutObjects.elementAt(i);
            if(keepItem != null && keepItem == lo.getOwningItem())
            {
                updateRowInternals();
                return true;
            }
            else
            {
                layoutObjects.removeElement(lo);
            }
        }
        updateRowInternals();
        return false;
    }

    /**
     * Update item positions in a row.<br>
     * This method called by FormLayouter when row is full and
     * ready for layout.
     */
    void updateRowLayout(boolean isLeftToRight)
    {
        // update xShift
        xShift = ItemLayouter.getXLocation(rowWidth, occupiedSpace, rowHLayout);
        int xDelta = 0;
        LayoutObject lo = null;
        if(isLeftToRight)
        {
            for(int i = 0; i < layoutObjects.size(); i++)
            {
                lo = (LayoutObject) layoutObjects.elementAt(i);
                int yDelta = ItemLayouter.getYLocation(rowHeight,
                                                       lo.getHeight(), lo.getVerticalLayout());
                lo.swtSetLocation(xShift + xDelta, yShift + yDelta);
                xDelta += lo.getWidth();
            }
        }
        else
        {
            for(int i = layoutObjects.size() - 1; i > 0; i--)
            {
                lo = (LayoutObject) layoutObjects.elementAt(i);
                int yDelta = ItemLayouter.getYLocation(rowHeight,
                                                       lo.getHeight(), lo.getVerticalLayout());
                lo.swtSetLocation(xShift + xDelta, yShift + yDelta);
                xDelta += lo.getWidth();
            }
        }
    }

    /**
     * Update width and height of a row if some layout objects were removed or
     * changed.
     */
    void updateRowInternals()
    {
        int newRowHeight = 0;
        int newOccupiedSpace = 0;
        int num = layoutObjects.size();
        LayoutObject lo = null;
        for(int i = 0; i < num; i++)
        {
            lo = (LayoutObject) layoutObjects.elementAt(i);
            newRowHeight = Math.max(newRowHeight, lo.getHeight());
            newOccupiedSpace += lo.getWidth();
        }
        rowHeight = newRowHeight;
        occupiedSpace = newOccupiedSpace;
    }

    /**
     * Set the yShift to corresponding in ScrolledComposite which represents
     * Form.
     *
     * @param aYShift yPosition of the row inside ScrolledComposite
     */
    void setYShift(int aYShift)
    {
        yShift = aYShift;
    }

    /**
     * Get the yPosition of a Row inside of ScrolledComposite.
     *
     * @return yShift in pixels
     */
    int getYShift()
    {
        return yShift;
    }

    /**
     * Gets the width of the row in pixels.
     *
     * @return - row's total width.
     */
    int getRowWidth()
    {
        return rowWidth;
    }

    /**
     * Get the height of a row in pixels (height of tallest item).
     *
     * @return height of a row;
     */
    int getRowHeight()
    {
        return rowHeight;
    }

    /**
     * Get the bottom (yPosition + rowHeight) of a Row .
     */
    int getBottomPosition()
    {
        return yShift + rowHeight;
    }

    /**
     * Get the yPosition of a Row's height inside of ScrolledComposite.
     *
     * @param yPosition the y position
     * @return yShift + rowHeight in pixels
     */
    boolean isInsideRow(int yPosition)
    {
        return (yShift <= yPosition && yPosition < yShift + rowHeight);
    }

    /**
     * set new horizontal Layout of a row.
     *
     * @param aRowHLayout - set new horizontal of the row.
     */
    void setRowHLayout(int aRowHLayout)
    {
        rowHLayout = aRowHLayout;
    }

    /**
     * Get horizontal Layout of row.
     *
     * @return horizontal Layout of a row;
     */
    int getRowHLayout()
    {
        return rowHLayout;
    }

    boolean isEmpty()
    {
        return ((occupiedSpace == 0) && (layoutObjects.size() == 0));
    }

    LayoutObject getNextLayoutObject(LayoutObject lo, Item item)
    {
        int startIdx = layoutObjects.indexOf(lo);
        startIdx = (startIdx < 0 ? 0 : startIdx + 1);
        int num = layoutObjects.size();
        LayoutObject temp = null;
        for(int i = startIdx; i < num; i++)
        {
            temp = getLayoutObject(i);
            if(item == null || item == temp.getOwningItem())
            {
                return temp;
            }
        }
        return null;
    }

    LayoutObject getPrevLayoutObject(LayoutObject lo, Item item)
    {
        int startIdx = layoutObjects.indexOf(lo);
        startIdx = (startIdx < 0 ? layoutObjects.size() - 1 : startIdx - 1);
        LayoutObject temp = null;
        for(int i = startIdx; i >= 0; i--)
        {
            temp = getLayoutObject(i);
            if(item == null || item == temp.getOwningItem())
            {
                return temp;
            }
        }
        return null;
    }

    LayoutObject getNextLayoutObject(LayoutObject lo, int layoutMask)
    {
        int startIdx = layoutObjects.indexOf(lo);
        startIdx = (startIdx < 0 ? 0 : startIdx + 1);
        int num = layoutObjects.size();
        LayoutObject temp = null;
        for(int i = startIdx; i < num; i++)
        {
            temp = getLayoutObject(i);
            if(temp.getOwningItem().hasLayout(layoutMask))
            {
                return temp;
            }
        }
        return null;
    }

    LayoutObject getLayoutObject(int index)
    {
        return (LayoutObject) layoutObjects.elementAt(index);
    }

    int size()
    {
        return layoutObjects.size();
    }

}
