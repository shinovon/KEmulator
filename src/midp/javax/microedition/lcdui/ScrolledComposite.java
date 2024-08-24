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

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Implementation of <code>ScrolledComposite</code> control.
 */
class ScrolledComposite extends Composite implements FocusListener,
    ControlListener
{

    private ScrollBar vBar;

    /**
     * Constructor.
     *
     * @param parent
     * @param style
     */
    public ScrolledComposite(Composite parent, int style)
    {
        super(parent, style);
        vBar = parent.getVerticalBar();
        if(vBar != null)
        {
            vBar.addSelectionListener(new ScrollBarListener());
        }

        // addKeyListener(new ScrollKeyListener());
        addTraverseListener(new ScrollTraverseListener());
        addControlListener(new ScrollControlListener());
    }

    /**
     * Set the position of this Composite relative to its parent.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    void setOrigin(int x, int y, boolean keyNavigation)
    {
        //setRedraw(false);

        if(vBar != null)
        {
            if(keyNavigation)
            {
                vBar.setSelection(y);
            }
            y = -vBar.getSelection();
        }
        else
        {
            y = 0;
        }

        this.setLocation(x, y);
        //setRedraw(true);
    }

    /**
     * Updates range and thumb size of the ScrollBars.
     */
    void updateScrollbar(int height)
    {
        Rectangle clientArea = getParent().getClientArea();
        if(vBar != null)
        {
            vBar.setMinimum(0);
            vBar.setMaximum(height);
            vBar.setThumb(clientArea.height);
        }
    }

    private class ScrollBarListener implements SelectionListener
    {

        public void widgetDefaultSelected(SelectionEvent e)
        {
            Logger.verbose(e.toString());
        }

        public void widgetSelected(SelectionEvent e)
        {
            Logger.verbose(e.toString());
        }
    }

    private class ScrollKeyListener implements KeyListener
    {

        public void keyPressed(KeyEvent e)
        {
            Logger.verbose(e.toString());
        }

        public void keyReleased(KeyEvent e)
        {
            Logger.verbose(e.toString());
        }
    }

    private class ScrollTraverseListener implements TraverseListener
    {

        public void keyTraversed(TraverseEvent e)
        {
        }
    }

    private class ScrollControlListener implements ControlListener
    {

        public void controlMoved(ControlEvent e)
        {
        }

        public void controlResized(ControlEvent e)
        {
        }
    }

    public void focusGained(FocusEvent e)
    {
        // makeVisible((Control) e.widget);
    }

    public void focusLost(FocusEvent e)
    {
    }

    public void controlMoved(ControlEvent e)
    {
        updateScrollbar(0);
    }

    public void controlResized(ControlEvent e)
    {
        updateScrollbar(0);
    }

}
