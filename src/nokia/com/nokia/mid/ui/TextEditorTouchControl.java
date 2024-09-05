/*
* Copyright (c) 2010 Nokia Corporation and/or its subsidiary(-ies).
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
* Description:  TextEditor-API alignment for Nokia-UI
*
*/
package com.nokia.mid.ui;

import com.nokia.mid.ui.TextEditorListener;

/**
 * Touch-devices can use a virtual control-panel to replace physical keys, and to facilitate
 * textual input. The purpose of this interface is to provide some level of access and control over such a panel, when
 * using the TextEditor-API. <p>
 *
 * MIDlets can obtain an instance of this interface via the {@link TextEditorExtensionAccess} in the
 * following way:
 * <pre>
 * TextEditor editor = com.nokia.mid.ui.TextEditor.createTextEditor("hello world", 50, TextField.ANY, 100, 100);
 * TextEditorTouchControl touchControl = ((TextEditorExtensionAccess)editor).getTouchControl();
 * if(touchControl!=null){
 *   // this is a touch-device
 * }
 * else{
 *   // this is a non-touch-device
 * }
 * </pre>
 * The instance is associated with the editor it has been obtained from, and controls only this editor.<p>
 *
 * Because a control-panel covers parts of the display which would otherwise be available to the application, MIDlets may
 * choose to only display the panel at certain times, e.g. when the editor is focussed. This is possible with
 * {@link TextEditorTouchControl#setPanelMode(int)}. Furthermore, a panel can be positioned anywhere an the display via
 * {@link TextEditorTouchControl#setPanelPosition(int, int)}. This interface also allows to query the size of the panel, so
 * that applications can integrate it into the Canvas. <p>
 *
 * On touch-devices, the platform can make use of pointer-input to let the user interact with a TextEditor on a Canvas, without
 * that the application knows about this. For example, if the user taps with his stylus or finger somewhere inside a visible
 * and focussed TextEditor, the MIDlet might not receive any notification about this via the Canvas-methods pointerPressed,
 * pointerDragged or pointerReleased. Instead, the platform could change the position of the cursor and generate the
 * according event for the editors {@link TextEditorListener}. Or it may even just ignore this event completely. In general,
 * an application should not expect to receive any pointer-events after a "press" inside a focussed TextEditor.
 * Under certain circumstances however, for example as a response to the {@link com.nokia.mid.ui.TextEditorListener#ACTION_TRAVERSE_OUT_SCROLL_UP}
 * or {@link com.nokia.mid.ui.TextEditorListener#ACTION_TRAVERSE_OUT_SCROLL_DOWN} event, an application can decide at any time to override this
 * behaviour, and force the platform to deliver all subsequent pointer-events to the Canvas.
 * This can be done with {@link TextEditorTouchControl#setTouchEnabled(boolean)}. Note that a TextEditor which has been touch-disabled
 * this way will not be able to perform any interaction like cursor-positioning or scrolling via touch-input, until is has been
 * enabled again.
 */
public interface TextEditorTouchControl
{


    //native_const(JAVA_TEXTEDITOR)
    //{

    /**
     * Constant to disable the display of a keypad/control-panel
     */
    public static final int PANEL_OFF = 1;

    /**
     * Constant to enable the display of a keypad/control-panel.
     * In this mode the user cannot change the position of the panel, however
     * the MIDlet can still do so via {@link TextEditorTouchControl#setPanelPosition(int, int)}
     */
    public static final int PANEL_FIXED_POSITION = 2;

    /**
     * Constant to enable the display of a keypad/control-panel.
     * In this mode, the user can change the position of the panel of the display e.g. by
     * dragging, if this is supported by the device. Changes to the position will generate the
     * {@link TextEditorTouchControl#ACTION_PANEL_MOVED}-event. The current panel-position is
     * provided via {@link TextEditorTouchControl#getPanelX()} and {@link TextEditorTouchControl#getPanelY()}
     */
    public static final int PANEL_FLOATING = 3;
    //}

    /**
     * Event to indicate that the keypad/control-panel has been moved by the user
     */
    public static final int ACTION_PANEL_MOVED = 0x10000000;


    /**
     * Controls the behaviour of the virtual keypad/control-panel, if the device provides one.
     * This is a hint which may be disregarded if the device does not support a virtual keypad/control-panel.
     * @param mode One of {@link TextEditorTouchControl#PANEL_OFF} {@link TextEditorTouchControl#PANEL_FIXED_POSITION}
     * or {@link TextEditorTouchControl#PANEL_FLOATING}
     */
    public void setPanelMode(int mode);

    /**
     * Gets the current panel-mode
     * @return the mode, see {@link TextEditorTouchControl#setPanelMode(int)}
     */
    public int getPanelMode();

    /**
     * Sets the position of the panel on the display.
     * @param x x-position of the keypad
     * @param y y-position of the keypad
     */
    public void setPanelPosition(int x, int y);

    /**
     * Gets the width of the panel
     * @return the width
     */
    public int getPanelWidth();

    /**
     * Gets the height of the panel
     * @return the height
     */
    public int getPanelHeight();

    /**
     * Gets the current x-position of the panel on the display
     * @return the x-coordinate
     */
    public int getPanelX();

    /**
     * Gets the current y-position of the panel on the display
     * @return the y-coordinate
     */
    public int getPanelY();

    /**
     * Specifies whether or not the editor will receive touch-events. This is enabled by default.
     * An editor with touch-event disabled won't be able to perform any touch-related functionality
     * such as scrolling or positioning the cursor. It may however still be controlled via the
     * virtual keypad/control-panel if that is enabled, or receive other input e.g. via physical
     * keys
     * @param enabled true to enabled touch-event, false to disable
     */
    public void setTouchEnabled(boolean enabled);

    /**
     * Gets the current touch-enabled state
     * @return true if the editor is touch-enabled, false otherwise
     */
    public boolean isTouchEnabled();

    /**
     * Sets the caret as close as possible to a given x/y location. This is a hint to the implementation
     * that may be disregarded.
     * @param x new x-coordinate for the caret, relative to the editors origin
     * @param y new y-coordinate for the caret, relative to the editors origin
     */
    public void setCaret(int x, int y);


}
