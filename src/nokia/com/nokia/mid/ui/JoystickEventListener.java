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
* Description:  A listener interface to handle Joystick events
*
*/


package com.nokia.mid.ui;

/**
 * <p>
 *  A listener interface to learn when the state of a Joystick changes.
 * <p>
 * This API is an optional feature in Nokia UI API, and depending on the device
 * capabilities there may be implementations that do not support joystick
 * events. The System.getProperty("com.nokia.mid.ui.joystick_event") will
 * return "true" if this feature is available in the device.
 * <p>
 * A device may support analogue joystick which detects 360 directions and
 * force instead of standard 5 way key navigation.
 * (Up, Down, Left, Right and Centre).
 * <p>
 * The joystick sends events every time its position or force changes.
 * This events are represented by x (horizontal) and y (vertical) vector
 * components. In its original position (centre), the value for (x, y)
 * are (0,0). As an example, suppose that the (-5,5) event was received.
 * It means that the joystick is held in the left and up diagonal.
 * Now, if the (-25,25) event is sent, it means that the Joystick is still
 * in the same direction of (-5,5), however more force were applied.
 * <p>
 * The maximum and minimum values of x and y are -127 and 127.
 * <p>
 * If the MIDlet needs to receive joystick events, it must provide
 * an implementation of this interface from a Canvas class or subclass thereof.
 * The Canvas will still receive standard key / game key events too.
 * The Joystick events are sent only if the Canvas is in visible state.
 */
public interface JoystickEventListener
{

    /**
     * Called when the Joystick sends an event.
     * <p>
     * The events are relative to its original position (0,0)
     *
     * @param x the horizontal component. The value is negative if the
     *      joystick is moved to the left direction and positive if moved
     *      to right direction.
     * @param y the vertical component. The value is negative if the joystick
     *      is moved to the down direction and positive if moved
     *      to up direction.
     */
    void joystickEvent(int x, int y);
}
