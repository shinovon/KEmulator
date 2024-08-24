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

import java.util.Enumeration;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


/**
 * Structure which represents Control stored in Form Row.
 */
final class LayoutObject
{

    /**
     * Control which is represented by this LayoutObject.
     */
    private Control control;

    /**
     * If control is Composite (for example if Item has label), then
     * commandControl will contain composite's child that will receive events.
     * Otherwise commandControl will be same as control.
     */
    private Control commandControl;
    private Item owningItem;
    private boolean commandsActivated;

    private int x;
    private int y;
    private int width;
    private int height;

    private EswtItemCommandListener swtItemCommandListener =
        new EswtItemCommandListener();

    // index of the row current LayoutObject belongs to
    private int layoutRowIdx;

    /**
     * Constructor.
     *
     * @param owningItem - Item which this LayoutObject corresponds to
     * @param control - Control for that LayoutObject
     */
    LayoutObject(Item owningItem, Control control)
    {
        this.owningItem = owningItem;
        setControl(control);
    }

    /**
     * Set the Control for that LayoutObject.
     *
     * @param newControl Control to set
     */
    private void setControl(final Control newControl)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                control = newControl;
                swtUpdateSize();
                Control temp = swtGetCommandControl(control);
                commandControl = (temp != null ? temp : control);

                Enumeration e = owningItem.getCommands().elements();
                Command cmd = null;
                while(e.hasMoreElements())
                {
                    cmd = (Command) e.nextElement();
                    swtAddCommand(cmd);
                }
            }
        });
    }

    /**
     * Gets Control which should be used with commands.<br>
     * This might be LayoutObject's control or one of its children in case of
     * Button. Button should be handled separately here because of MSK.
     *
     * @param ctrl Control of this LayoutObject.
     * @return Control.
     */
    Control swtGetCommandControl(Control ctrl)
    {
        Control ret = null;

        //If the Control is focusable add the commands to the same.
        if((ctrl instanceof Group) || (ctrl instanceof Button)
			|| (ctrl.isFocusControl()))
        {
            return ctrl;
        }

        if(ctrl instanceof Composite)
        {
            Control[] children = ((Composite) ctrl).getChildren();
            for(int i = 0; i < children.length; i++)
            {
                ret = swtGetCommandControl(children[i]);
                if(ret != null)
                {
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Dispose LayoutObject.
     */
    void dispose()
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
            	owningItem = null;
                control.dispose();
                control = null;
                commandControl = null;
            }
        });
    }

    /**
     * Returns the Item's vertical layout.
     */
    int getVerticalLayout()
    {
        return Item.getVerticalLayout(owningItem.getLayout());
    }

    /**
     * Get Control of the layoutObject.
     *
     * @return control
     */
    Control getControl()
    {
        return control;
    }

    /**
     * Get Item to which the LayoutObjecy belongs.
     *
     * @return owning Item
     */
    Item getOwningItem()
    {
        return owningItem;
    }

    /**
     * Set location of the LayoutObject and the control.
     *
     * @param xCoord x coordinate
     * @param yCoord y coordinate
     */
    void swtSetLocation(int xCoord, int yCoord)
    {
        x = xCoord;
        y = yCoord;
        control.setLocation(xCoord, yCoord);
    }

    /**
     * Update LayoutObject width and height.
     */
    void swtUpdateSize()
    {
        Rectangle bounds = control.getBounds();
        width = bounds.width;
        height = bounds.height;
    }

    /**
     * Get x Coordinate of LayoutObject,
     *
     * @return xCoordinate of LayoutObject.
     */
    int getX()
    {
        return x;
    }

    /**
     * Get y Coordinate of LayoutObject,
     *
     * @return yCoordinate of LayoutObject.
     */
    int getY()
    {
        return y;
    }

    /**
     * Get width of LayoutObject.
     *
     * @return width of a control
     */
    int getWidth()
    {
        return width;
    }

    /**
     * Get width of LayoutObject.
     *
     * @return width of a control
     */
    int getHeight()
    {
        return height;
    }

    /**
     * Sets the row index of the Row where this LayoutObject is placed.
     *
     * @param row Row index.
     */
    void setRowIdx(int row)
    {
        layoutRowIdx = row;
    }

    /**
     * Returns row index where this LayoutObject is placed.
     *
     * @return Index of the row.
     */
    int getRowIdx()
    {
        return layoutRowIdx;
    }

    boolean contains(int xCoord, int yCoord)
    {
        return (y <= yCoord && yCoord <= y + height
                && x <= xCoord && xCoord <= x + width);
    }

    /**
     * Returns if this LayoutObject is below the other.
     */
    boolean isBelow(LayoutObject other)
    {
        if(other != null)
        {
            return (this.getRowIdx() > other.getRowIdx());
        }
        return true;
    }

    /**
     * Returns if this LayoutObject is above the other.
     */
    boolean isAbove(LayoutObject other)
    {
        if(other != null)
        {
            return (this.getRowIdx() < other.getRowIdx());
        }
        return true;
    }

    /**
     * Returns the horizontal middle of the LayoutObject.
     *
     * @return x horizontal coordinate of the middle of LayoutObject.
     */
    private int getXMiddle()
    {
        return (x + width / 2);
    }

    /**
     * Returns the vertical middle of the LayoutObject.
     *
     * @return y vertical coordinate of the middle of LayoutObject.
     */
    private int getYMiddle()
    {
        return (y + height / 2);
    }

    /**
     * Returns the horizontal distance between the middle of the LayoutObjects.
     * The method is used to find next focusable item.
     *
     * @param other another LayoutObject
     * @return positive distance between the middles
     */
    int distanceTo(LayoutObject other)
    {
        if(other != null)
        {
            int xd = Math.abs(getXMiddle() - other.getXMiddle());
            int yd = Math.abs(getYMiddle() - other.getYMiddle());
            return (int) Math.sqrt(xd * xd + yd * yd);
        }
        else
        {
            return Integer.MAX_VALUE - 1;
        }
    }

    /**
     * Activates commands associated with owning item.
     */
    void swtAddSelectionListenerForCommands()
    {
        // Logger.method(this, "swtAddSelectionListenerForCommands");
        commandsActivated = true;

        // Add new control to commands in owning item:
        Enumeration e = owningItem.getCommands().elements();
        Command cmd = null;
        while(e.hasMoreElements())
        {
            cmd = (Command) e.nextElement();
//            cmd.swtAddCommandSelectionListener(commandControl,
//                                                swtItemCommandListener);
        }
        // Add SelectionListener to Button control.
        // Listener is activated when command mapped to MSK is clicked.
        if(commandControl instanceof Button && !commandControl.isDisposed())
        {
            ((Button) commandControl)
            .addSelectionListener(swtItemCommandListener);
        }
    }

    /**
     * DeActivates commands associated with owning item.
     */
    void swtRemoveSelectionListenerForCommands()
    {
        // Logger.method(this, "swtRemoveSelectionListenerForCommands");
        commandsActivated = false;

        // Add new control to commands in owning item:
        Enumeration e = owningItem.getCommands().elements();
        Command cmd = null;
        while(e.hasMoreElements())
        {
            cmd = (Command) e.nextElement();
//            cmd.swtRemoveCommandSelectionListener(commandControl,
//                                                   swtItemCommandListener);
        }
        // Remove SelectionListener from Button control.
        // Listener is activated when command mapped to MSK is clicked.
        if(commandControl instanceof Button && !commandControl.isDisposed())
        {
            ((Button) commandControl)
            .removeSelectionListener(swtItemCommandListener);
        }
    }

    /**
     * Adds command to the owning item.
     *
     * @param command The command to be added.
     */
    void addCommand(final Command command)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                swtAddCommand(command);
            }
        });
    }

    /**
     * eSWT callback to add a Command.
     */
    void swtAddCommand(Command command)
    {
        //Logger.method(this, "swtAddCommand", command);
        if(command != null && commandControl != null)
        {
//            command.swtAddSWTCommand(commandControl,
//                                       (command == owningItem.getDefaultCommand()));
//            if(commandsActivated)
//            {
//                command.swtAddCommandSelectionListener(commandControl,
//                                                        swtItemCommandListener);
//            }
        }
    }

    /**
     * Removes command from the owning item.
     *
     * @param command The command to be removed.
     */
    void removeCommand(final Command command)
    {
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                swtRemoveCommand(command);
            }
        });
    }

    /**
     * eSWT callback to remove a Command.
     */
    private void swtRemoveCommand(Command command)
    {
        //Logger.method(this, "swtRemoveCommand", command);
        if(command != null && commandControl != null)
        {
//            if(commandsActivated)
//            {
//                command.swtRemoveCommandSelectionListener(commandControl,
//                        swtItemCommandListener);
//            }
//            command.swtRemoveESWTCommand(commandControl);
        }
    }

    /**
     * Inner class which receives SelectionEvents from eSWT and convert and
     * forwards those events to LCDUI Item's ItemCommandListener.
     */
    class EswtItemCommandListener implements SelectionListener
    {

        public void widgetDefaultSelected(SelectionEvent e)
        {
        }

        /**
         * Executed by eSWT when event occurs. This method will then call
         * Item's ItemCommandListener if event source matches with the
         * Commands added to the Item.
         */
        public void widgetSelected(SelectionEvent event)
        {
            // Go through all Commands added to owning item:
            Enumeration e = owningItem.getCommands().elements();
            Command cmd = null;
            while(e.hasMoreElements())
            {
                cmd = (Command) e.nextElement();
                // get eSWT Command of this Control from Command and compare
                // it to the widget which launched the event:
//                if(cmd.getSWTCommand(commandControl) == event.widget)
//                {
//                    owningItem.callCommandAction(cmd);
//                    break;
//                }

                // Handle MSK:
                Command mskCmd = owningItem.getMSKCommand();
                if(mskCmd != null && cmd == mskCmd)
                {
                    if(commandControl == event.widget)
                    {
                        owningItem.callCommandAction(cmd);
                        break;
                    }
                }
            }
        }
    }

    public String toString()
    {
        return owningItem + " [line:" + layoutRowIdx + "]";
    }

}
