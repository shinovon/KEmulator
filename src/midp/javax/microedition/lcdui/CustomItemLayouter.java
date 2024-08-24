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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;
/**
 * Responsible for layouting CustomItem.
 */
class CustomItemLayouter extends ItemLayouter
{

    /**
     * Key name for paint listener.
     */
    private static final String PAINT_LISTENER = "PaintListener";

    /**
     * Key name for mouse listener.
     */
    private static final String MOUSE_LISTENER = "MouseListener";

    boolean noBackground;

    private Timer timer = new Timer();
    private CustomItemTimerTask timerTask;

    private static final int DEFAULT_TIMEOUT = 500;
    private static final int DEFAULT_TWIPS = 200;

    private int pointerDownX;
    private int pointerDownY;
    private int twips;
    private int timeout;
    private boolean disableTapDetection;
    private boolean suppressDragEvent;
    private boolean selectionKeyCompatibility;

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    public CustomItemLayouter(FormLayouter aFormLayouter)
    {
        super(aFormLayouter);

        noBackground = false;


        selectionKeyCompatibility = true;


            setDefaultTapValues();
    }

    private void setDefaultTapValues()
    {
        twips = DEFAULT_TWIPS;
        timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Creates the eSWT control.
     *
     * @param parent where to create.
     * @param item on which it is based. Must be CustomItem.
     * @return Control.
     */
    Control swtGetControl(Composite parent, Item item)
    {
        Control ret = new org.eclipse.swt.widgets.Canvas(parent,
                                          (noBackground ? SWT.NO_BACKGROUND : SWT.NONE));
        return ret;
    }

    /**
     * Set the size of the layouted Control.
     */
    void swtResizeControl(Item item, Control control, int width, int height)
    {
        super.swtResizeControl(item, control, width, height);
        ((CustomItem)item).internalHandleSizeChanged(width, height);
    }

    /**
     * Add listeners to Layouter specific control.
     */
    void swtAddSpecificListeners(Item item, Control control)
    {
        super.swtAddSpecificListeners(item, control);
        Canvas canvas = (Canvas) control;
        CIPaintListener pl = new CIPaintListener((CustomItem) item);
        canvas.addPaintListener(pl);
        canvas.setData(PAINT_LISTENER, pl);

        CIMouseListener ml = new CIMouseListener((CustomItem) item);
        canvas.addMouseListener(ml);
        canvas.addMouseMoveListener(ml);
        canvas.setData(MOUSE_LISTENER, ml);
    }

    /**
     * Remove listeners from Layouter specific control.
     */
    void swtRemoveSpecificListeners(Item item, Control control)
    {
        super.swtRemoveSpecificListeners(item, control);
        Canvas canvas = (Canvas) control;
        CIPaintListener pl = (CIPaintListener) canvas.getData(PAINT_LISTENER);
        if(pl != null)
        {
            canvas.removePaintListener(pl);
            canvas.setData(PAINT_LISTENER, null);
        }
        CIMouseListener ml = (CIMouseListener) canvas.getData(MOUSE_LISTENER);
        if(ml != null)
        {
            canvas.removeMouseListener(ml);
            canvas.removeMouseMoveListener(ml);
            canvas.setData(MOUSE_LISTENER, null);
        }
    }

    /**
     * Returns if this eSWT control is Layouter specific.
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
        return (control instanceof Canvas);
    }

    /**
     * Updates the values of CustomItem.
     */
    void swtUpdateItem(Item item, Control control, int reason, Object param)
    {
//        if(reason == CustomItem.UPDATE_REASON_REPAINT)
//        {
//            Rectangle rect = (Rectangle) param;
//            ((CustomItem)item).updateItem(rect, control);
//        }
    }

    /**
     * Gets Canvas direction based on SWT arrows.
     */
    int getCanvasDirection(int key)
    {
        int ret = 0;
        switch(key)
        {
        case SWT.ARROW_DOWN:
            ret = javax.microedition.lcdui.Canvas.DOWN;
            break;
        case SWT.ARROW_UP:
            ret = javax.microedition.lcdui.Canvas.UP;
            break;
        case SWT.ARROW_LEFT:
            ret = javax.microedition.lcdui.Canvas.LEFT;
            break;
        case SWT.ARROW_RIGHT:
            ret = javax.microedition.lcdui.Canvas.RIGHT;
            break;
        default:
        }
        return ret;
    }

    /**
     * Gets the specified visRect parameter needed for traverse.
     */
    int[] getVisRect(Control control)
    {
        final int[] visRect = new int[4];
        if(control != null)
        {
            Point size = control.getSize();
            visRect[0] = 0;
            visRect[1] = 0;
            visRect[2] = size.x;
            visRect[3] = size.y;
        }
        return visRect;
    }

    /**
     * Returns whether the key event was consumed by CustomItem or not.
     *
     * @param item CustomItem.
     * @param key key code.
     */
    boolean swtOfferKeyPressed(Item item, int key)
    {
        CustomItem customItem = (CustomItem) item;

        if(!((selectionKeyCompatibility == true) && (key == -5)))
        {
//            EventDispatcher eventDispatcher = EventDispatcher.instance();
//            LCDUIEvent e = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_KEYPRESSED, formLayouter.getForm());
//            e.item = customItem;
//            e.keyCode = key;
//            eventDispatcher.postEvent(e);
            customItem.keyPressed(key);
        }

        boolean consumed = true;
        int direction = getCanvasDirection(key);
        if(direction > 0)
        {
            Control control = swtGetFirstControl(item);
            Control ctrl = swtFindSpecificControl(item, control);
            int[] visRect = getVisRect(ctrl);
            // Offer event for inner traversal
            consumed = customItem.traverse(direction,
                                           formLayouter.getFormWidth(), formLayouter.getFormHeight(), visRect);
            if(consumed)
            {
                // if inner focus is on - scroll to inner focus
                Point loc = new Point(0, 0);
                formLayouter.getControlPositionOnComposite(ctrl, loc);
                formLayouter.swtScrolltoRegion(loc.y + visRect[1],
                                       loc.y + visRect[1] + visRect[3], key);
            }
            control.redraw();
        }
        return consumed;
    }

    /* (non-Javadoc)
     * @see ItemLayouter#swtOfferKeyReleased(Item, int)
     */
    boolean swtOfferKeyReleased(Item item, int key)
    {
        CustomItem customItem = (CustomItem) item;

        if(!((selectionKeyCompatibility == true) && (key == -5)))
        {
//            EventDispatcher eventDispatcher = EventDispatcher.instance();
//            LCDUIEvent e = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_KEYRELEASED, formLayouter.getForm());
//            e.item = customItem;
//            e.keyCode = key;
//            eventDispatcher.postEvent(e);
            customItem.keyReleased(key);
        }

        if(getCanvasDirection(key) > 0)
        {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see ItemLayouter#swtFocusGained(Item, int)
     */
    void swtFocusGained(Item item, int swtDir)
    {
        super.swtFocusGained(item, swtDir);
        CustomItem customItem = (CustomItem) item;
        Control control = swtGetFirstControl(item);
        if(control != null)
        {
            Control ctrl = swtFindSpecificControl(item, control);
            int[] visRect = getVisRect(ctrl);
            if(customItem.traverse(getCanvasDirection(swtDir),
                                   formLayouter.getFormWidth(), formLayouter.getFormHeight(), visRect))
            {
                // if inner focus is on - scroll to inner focus
                Point loc = new Point(0, 0);
                formLayouter.getControlPositionOnComposite(ctrl, loc);
                formLayouter.swtScrolltoRegion(loc.y + visRect[1],
                                       loc.y + visRect[1] + visRect[3], swtDir);
            }
            control.redraw();
        }
    }

    /* (non-Javadoc)
     * @see ItemLayouter#swtFocusLost(Item)
     */
    void swtFocusLost(Item item)
    {
        super.swtFocusLost(item);
        CustomItem customItem = (CustomItem) item;
        customItem.traverseOut();
    }

    /* (non-Javadoc)
     * @see temLayouter#swtHandleShow(Item)
     */
    void swtHandleShow(Item item)
    {
//        EventDispatcher eventDispatcher = EventDispatcher.instance();
//        LCDUIEvent event = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_SHOWNOTIFY, formLayouter.getForm());
//        event.item = item;
//        eventDispatcher.postEvent(event);
//        CustomItem customItem = (CustomItem) item;
//        customItem.repaint();
    }

    /* (non-Javadoc)
     * @see ItemLayouter#swtHandleHide(Item)
     */
    void swtHandleHide(Item item)
    {
//        EventDispatcher eventDispatcher = EventDispatcher.instance();
//        LCDUIEvent event = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_HIDENOTIFY, formLayouter.getForm());
//        event.item = item;
//        eventDispatcher.postEvent(event);
    }

    /**
     * Returns the minimum area needed to display a CustomItem.
     *
     * @param customitem CustomItem object.
     * @return Minimum area needed to display CustomItem.
     */
    static Point calculateMinimumBounds(final CustomItem customitem)
    {
        Point minSize = new Point(customitem.getMinContentWidth(),
                                  customitem.getMinContentHeight());
        applyMinMargins(customitem, minSize);
        return minSize;
    }

    /**
     * Returns the preferred area needed to display a CustomItem.
     *
     * @param item CustomItem object.
     * @return preferred area needed to display CustomItem.
     */
    static Point calculatePreferredBounds(final Item item)
    {
        CustomItem customitem = (CustomItem) item;
        Point prefSize = new Point(
            customitem.getPrefContentWidth(item.getLockedPreferredHeight()),
            customitem.getPrefContentHeight(item.getLockedPreferredWidth()));
        applyPrefMargins(item, prefSize);
        return prefSize;
    }

    /**
     * Paint listener.
     */
    class CIPaintListener implements PaintListener
    {

        private CustomItem customItem;

        CIPaintListener(CustomItem customItem)
        {
            this.customItem = customItem;
        }

        public void paintControl(PaintEvent pe)
        {
        	// graphicsBuffer may be null when control
        	// is brought to foreground the first time
//        	if(customItem.graphicsBuffer != null)
//        	{
//        		// If we have initiated a synchronous paint event
//        		// draw the buffer to display here
//        	    if(customItem.graphicsBuffer.isPaintingActive())
//        	    {
//        		    customItem.graphicsBuffer.blitToDisplay(pe.gc.getGCData().internalGc, null);
//        	    }
//        	}
//        	else
//        	{
        	    // Native toolkit is requesting an update of an area that has
                // become invalid. Can't do anything here because the contents
                // need to be queried from the MIDlet in another thread by
                // a paint callback. For this a paint callback event is posted.
        	    // For a moment the native toolkit thinks that the area has
                // been validated when in truth it will be painted later after
                // the paint callback has been executed.
//                EventDispatcher eventDispatcher = EventDispatcher.instance();
//                LCDUIEvent event = eventDispatcher.newEvent(
//                                       LCDUIEvent.CUSTOMITEM_PAINT_NATIVE_REQUEST, formLayouter.getForm());
//                event.x = pe.x;
//                event.y = pe.y;
//                event.width = pe.width;
//                event.height = pe.height;
//                event.widget = pe.widget;
//                event.item = customItem;
//                eventDispatcher.postEvent(event);
//        	}
        }
    }

    /**
     * Mouse listener.
     */
    class CIMouseListener implements MouseListener, MouseMoveListener
    {

        private CustomItem customItem;

        CIMouseListener(CustomItem customItem)
        {
            this.customItem = customItem;
        }

        public void mouseDown(MouseEvent event)
        {
//            EventDispatcher eventDispatcher = EventDispatcher.instance();
//            LCDUIEvent e = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_POINTERPRESSED, formLayouter.getForm());
//            e.item = customItem;
//            e.x = event.x;
//            e.y = event.y;
//            eventDispatcher.postEvent(e);
            customItem.pointerReleased(event.x, event.y);

            if(!disableTapDetection)
            {
                // Supress Drag events
                suppressDragEvent = true;

                pointerDownX = event.x;
                pointerDownY = event.y;

                // Create and Schedule Timer
                timerTask = new CustomItemTimerTask();
                timer.schedule(timerTask, timeout);
            }
        }

        public void mouseUp(MouseEvent event)
        {
            int x = event.x;
            int y = event.y;

            if(!disableTapDetection)
            {
                timerTask.cancel();
                timerTask = null;

                // If Timer not expired and Mouseup is withing rectangle assign
                // PointercDown to Pinter Up
                if(suppressDragEvent && checkWithinRect(event.x, event.y))
                {
                    x = pointerDownX;
                    y = pointerDownY;
                    suppressDragEvent = false;
                }
            }
//            EventDispatcher eventDispatcher = EventDispatcher.instance();
//            LCDUIEvent e = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_POINTERRELEASED, formLayouter.getForm());
//            e.item = customItem;
//            e.x = pointerUpX;
//            e.y = pointerUpY;
//            eventDispatcher.postEvent(e);

            customItem.pointerReleased(x, y);
        }

        public void mouseMove(MouseEvent event)
        {
            // Check for timeout expiry and PointerUp falls outside rectangle
            if(disableTapDetection || (!suppressDragEvent) || !checkWithinRect(event.x, event.y))
            {
//                EventDispatcher eventDispatcher = EventDispatcher.instance();
//                LCDUIEvent e = eventDispatcher.newEvent(LCDUIEvent.CUSTOMITEM_POINTERDRAGGED, formLayouter.getForm());
//                e.item = customItem;
//                e.x = event.x;
//                e.y = event.y;
//                eventDispatcher.postEvent(e);

                customItem.pointerDragged(event.x, event.y);
            }
        }

        public void mouseDoubleClick(MouseEvent event)
        {
        }

        boolean checkWithinRect(int x, int y)
        {
            // Get pixel per inch
            Point P = Display.getCurrent().getDPI();

            float xPxielwidth  = (twips * P.x) / 1440;
            float yPixelHeight = (twips * P.y) / 1440;

            int RightX = pointerDownX + (int) xPxielwidth;

            // If the rectange width falls outside the custom area
            if(RightX > customItem.getContentWidth())
            {
                RightX = customItem.getContentWidth();
            }

            int LeftX = pointerDownX - (int) xPxielwidth;

            // If the rectange width falls outside the custom area
            if(LeftX < 0)
            {
                LeftX = 0;
            }

            int TopY = pointerDownY - (int) yPixelHeight;

            // If the rectange height falls outside the custom area
            if(TopY < 0)
        	{
                TopY = 0;
            }

            int DownY = pointerDownY + (int) yPixelHeight;

            // If the rectange heightfalls outside the custom area.
            if(DownY > customItem.getContentHeight())
            {
                DownY = customItem.getContentHeight();
            }

            // Find the PointerUp is withing rectange
            if((x >= LeftX) && (x <= RightX))
            {
                if((y >= TopY) && (y <= DownY))
                {
                    return true;
                }
            }
            return false;
        }
    }

    class CustomItemTimerTask extends TimerTask
    {

        public void run()
        {
            suppressDragEvent = false;
        }
    }

}
