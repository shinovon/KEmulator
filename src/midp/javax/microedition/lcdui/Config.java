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

/**
 * Class containing static constants for OpenLCDUI.
 */
final class Config
{

    static final int DISPLAYABLE_DIALOGSHELL_HEIGHT_DISPLACEMENT = 10;

    /**
     * Default scrolling increment for text.
     */
    static final int ALERT_TEXT_SCROLLING_DELTA = 10;

    /**
     * Default timeout for Alerts.
     *
     * @value for DEFAULT_TIMEOUT is 3000.
     */
    static final int ALERT_DEFAULT_TIMEOUT = 3000;

    /**
     * Default bits per pixel.
     */
    static final int DISPLAY_BITS_PER_PIXEL = 8;

    /**
     * Maximum number of visible lines on screen.
     */
    static final int TEXTBOX_MAX_VISIBLE_LINES = 10;

    /**
     * Zero epoch year.
     */
    static final int DATEFIELD_ZERO_EPOCH_YEAR = 1970;

    /**
     * Represents margins inside of TextExtension before the first line and
     * after last line. <br>
     */
    static final int TEXTFIELD_MARGIN = 4;

    /**
     * Maximum screen percentage which a TextField can occupy.
     */
    static final int TEXTFIELD_MAX_SCREEN_PERCENTAGE = 60;


    /**
     * Hyperlink key filter MSK keycode in S60.
     */
    static final int STRINGITEM_MSK_KEYCODE = -5;

    /**
     * Layouting timer's delay.
     */
    static final int FORM_LAYOUT_TIMER_DELAY = 50;

    /**
     * Mouse event generator timer's delay.
     */
    static final int DFI_EVENT_TIMER_DELAY = 100;

    /**
     * Mouse event movement delta in pixels.
     */
    static final int DFI_EVENT_MOVE_DELTA = 20;

    /**
     * Percent at which an item is viewed as visible.
     */
    static final int DFI_VISIBILITY_PERCENT = 10;


    /**
     * Delay between ticker movement in milliseconds.
     */
    static final int TICKER_MOVEMENT_DELAY = 50;

    /**
     * Milliseconds it takes to scroll each character across the screen.
     */
    static final int TICKER_DISPLAY_TIME = 7000;


    private Config()
    {
        super();
    }

}
