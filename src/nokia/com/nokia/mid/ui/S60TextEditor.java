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
 * Description:  Interface for S60 Text Editor
 *
 */

// PACKAGE
package com.nokia.mid.ui;

/**
 * <P>
 * S60 platform specific extensions to <code>TextEditor</code>. These methods
 * are only available in Java Runtime for Symbian.
 * </P>
 *
 * <P>
 * <code>TextEditor</code> does not support DSA over its area. Playing video or
 * using camera over <code>TextEditor</code> area can lead to artifacts on
 * screen.
 * </P>
 *
 * <h3>Editing state indicators</h3>
 * <p>
 * By default, the implementation uses platform specific indicators for the
 * <code>
 * TextEditor</code>. Typically these indicators are shown in the status pane
 * and the visibility is related to the focus of the associated
 * <code>TextEditor</code>. When the associated editor is focused, the platform
 * displays the editing state indicators for the user and when focus is lost
 * from the editor the indicators are hidden.
 * </p>
 * <p>
 * The application can also choose to control the input indicators if needed. In
 * this case, the visibility and position needs to be controlled separately when
 * focusing the associated editor. It should be noted that if no indicators are
 * shown to the user, the usability maybe diminished especially with complex
 * input methods.
 * </p>
 * <p>
 * When using custom indicator position, the background color of the indicator
 * area is equal to the associated <code>TextEditor</code>'s background color.
 * </p>
 *
 * <h3>Touch input methods</h3>
 * <p>
 * The application has a possibility to control the touch input methods with
 * this class. It is possible to set the disabled touch input modes and the
 * preferred touch mode if needed. It must be noted that supported touch input
 * modes depend on the used device thus some of the touch modes cannot be set as
 * preferred or enabled.
 * </p>
 *
 * <h3>Touch screen virtual keyboard</h3>
 * <p>
 * In touch-only devices when user taps on a text editor touch screen
 * virtual keyboard is opened. The keyboard takes part of the screen space, so
 * canvas is smaller when virtual keyboard is visible.
 * Application is notified about this by events, so it can react on this
 * change and resize, move TextEditor to the visible area and adjust the
 * whole Canvas.
 * </p>
 * 
 * <h3>Touch settings</h3>
 * <p>
 * The TextEditor receives touch events in touch devices. This can be 
 * disabled/enabled by {@link com.nokia.mid.ui.S60TextEditor#setTouchEnabled(boolean)}
 * An editor with touch-event disabled won't be able to perform any
 * touch-related functionality.
 * </p>
 * @see TextEditor
 * @since 1.4
 */
public abstract interface S60TextEditor
{

    /**
     * Indicates that all input methods are available.
     * <p>
     * Can be used in {@link #setDisabledTouchInputModes}.
     */
    public static final int TOUCH_INPUT_ALL_AVAILABLE = 0;

    /**
     * Handwriting recognition
     */
    public static final int TOUCH_INPUT_HWR = 1;

    /**
     * Virtual QWERTY keyboard
     */
    public static final int TOUCH_INPUT_VKB = 2;

    /**
     * Full screen QWERTY keyboard
     */
    public static final int TOUCH_INPUT_FSQ = 4;

    /**
     * Alphanumeric keyboard with ITU-T input
     */
    public static final int TOUCH_INPUT_ITUT = 8;

    /**
     * Full screen handwriting recognition
     */
    public static final int TOUCH_INPUT_FSC = 16;

    /**
     * Mini ITU-T for Japanese devices
     */
    public static final int TOUCH_INPUT_MINI_ITUT = 32;

    /**
     * Indicates that the touch screen virtual keyboard is opened.
     */
    public static final int ACTION_VIRTUAL_KEYBOARD_OPEN = 0x1000;

    /**
     * Indicates that the touch screen virtual keyboard is closed.
     */
    public static final int ACTION_VIRTUAL_KEYBOARD_CLOSE = 0x2000;

    /**
     * <P>
     * If the default indicator location is not used then sets the drawing
     * location for input indicators relative to the <code>TextEditor</code>'s
     * parent.
     * </P>
     *
     * <P>
     * The anchor point given is relative to the upper left corner of the
     * <code>Canvas</code>. The location may be outside the
     * <code>TextEditor</code> boundaries. The z-order of the indicators is the
     * same as <code>TextEditor</code> textual content. If indicators and the
     * editor content overlap indicators are rendered on top of the editor
     * content.
     * </P>
     *
     * <P>
     * The application should first query the size of the input indicators with
     * {@link #getIndicatorSize} method to determine that the indicators will
     * not be clipped outside the available display area when drawn to the
     * requested location.
     * </P>
     *
     * <P>
     * If there are no indicators present, the usability of complex device
     * specific input methods may be compromised.
     * </P>
     *
     * @param x
     *            the x coordinate of the anchor point, in pixels.
     * @param y
     *            the y coordinate of the anchor point, in pixels.
     *
     */
    public void setIndicatorLocation(int x, int y);

    /**
     * <P>
     * Resets the implementation provided input indicators to their default
     * position.
     * </P>
     *
     * <P>
     * This position may be outside the area of parent in case the default
     * position is in status area. In this case if the status area is missing
     * (full screen mode Canvas) the default position is inside the parent area
     * e.g. on top of the editor. When default position is in use the
     * <code>TextEditor</code> automatically positions the indicators relative
     * to the <code>TextEditor</code> even when <code>TextEditor</code> location
     * is changed. However, client is responsible of making sure indicators are
     * visible inside the parent area positioning <code>TextEditor</code> so
     * that indicators fit to the visible area e.g. on top of the
     * <code>TextEditor</code>. Positioning <code>TextEditor</code> directly on
     * top of <code>Canvas</code> may mean that indicators in the default
     * position are not visible.
     * </P>
     *
     * @throws IllegalStateException
     *             If the <code>TextEditor</code> is not added to
     *             <code>Canvas</code>
     */
    public void setDefaultIndicators();

    /**
     * <P>
     * By default indicators visibility is set to <code>true</code> and they are
     * made visible when the associated <code>TextEditor</code> is focused.
     * </P>
     * <P>
     * If the application controls the position of the indicators, those can be
     * explicitly made not visible by calling
     * <code>setIndicatorVisibility(false)</code>. Indicators are never visible
     * if <code>TextEditor</code> itself is not visible so having indicator
     * visibility set to <code>true</code> does not make them visible unless
     * editor itself is set visible.
     * </P>
     *
     * @param visible
     *            controls indicator visibility state
     *
     * @see #setIndicatorLocation(int, int)
     * @see #setDefaultIndicators()
     */
    public void setIndicatorVisibility(boolean visible);

    /**
     * Gets the size of the area needed for drawing the input indicators.
     * <p>
     * The returned array contains two integers for width (array index 0) and
     * height (array index 1) of the indicator graphics. Size (0,0) is returned
     * if the device UI does not use any input indicators in the text editors or
     * if the indicators are curretly positioned outside parent area e.g. in
     * status area. This happens if {@link #setIndicatorLocation(int, int)
     * setIndicatorLocation(int x, int y)} has not been ever called for the
     * editor, or if {@link #setDefaultIndicators() setDefaultIndicators()} has
     * been called.
     * <p>
     *
     * @return the width and height of area needed for drawing input indicators
     * @see #setDefaultIndicators()
     */
    public int[] getIndicatorSize();

    /**
     * Disables one or multiple touch input modes from use.
     * <p>
     * User is not able to switch to the disable touch input modes. Multiple
     * touch input modes may be combined together in <code>touchInputMode</code>
     * parameter using bitwise or operator. The method does not have any impact
     * if called in non-touch device. A device may not support all touch input
     * modes specified. Specifying not supported input modes is silently
     * ignored.
     * <p>
     * The possible values are defined in the class with TOUCH_INPUT_* starting
     * constant values.
     * <p>
     *
     * @param touchInputModes
     *            bitwise or combined list of disabled touch input modes
     * @throws IllegalArgumentException
     *             if the given input modes are not valid.
     *             <p>
     * @see #setPreferredTouchMode(int)
     * @see #getDisabledTouchInputModes()
     */
    public void setDisabledTouchInputModes(int touchInputModes);

    /**
     * By default all supported touch input modes are available. Returns the
     * disabled touch input modes set with {@link #setDisabledTouchInputModes}.
     * <p>
     * Note that the disabled touch input modes may be device specific so this
     * method may return some modes as disabled by default.
     * <p>
     * Note that if the device does not support touch input this method returns
     * all modes.
     * <p>
     *
     * @return The disabled touch input modes.
     * @see #setDisabledTouchInputModes(int)
     * @see #setPreferredTouchMode(int)
     */
    public int getDisabledTouchInputModes();

    /**
     * Set the preferred touch input mode overriding the device default
     * preferred mode. User may still change the touch input mode from touch
     * input window to all available modes.
     * <p>
     * The possible values are defined in the class with TOUCH_INPUT_* starting
     * constant values.
     * <p>
     * Note that if the device does not support touch input this method has no
     * effect.
     * <p>
     *
     * @param touchInputModes
     *            a touch input mode to be set as preferred one.
     * @throws IllegalArgumentException
     *             if the input mode is not valid or if it contains multiple
     *             input modes as bit mask.
     * @see #setDisabledTouchInputModes(int)
     * @see #getPreferredTouchMode()
     *
     */
    public void setPreferredTouchMode(int touchInputModes);

    /**
     * Gets the preferred touch input mode.
     * <p>
     * Note that if the device does not support touch input this method returns
     * <code>0</code>
     * <p>
     *
     * @return The preferred touch input mode.
     *         <p>
     * @see #setPreferredTouchMode(int)
     * @see #setDisabledTouchInputModes(int)
     */
    public int getPreferredTouchMode();

    /**
     * Sets the caret in the Editor at x, y location.
     *
     * @param x
     *      The x coordinate of the wanted caret position.
     *
     * @param y
     *      The y coordinate of the wanted caret position.
     */
    public void setCaretXY(int x, int y);
    
    /**
     * Specifies whether or not the editor will receive touch-events.
     * <p>
     * This is enabled by default.
     * An editor with touch-event disabled won't be able to perform any
     * touch-related functionality such as scrolling or positioning the
     * cursor. It may however still be controlled via the
     * virtual keypad/control-panel if that is enabled, or receive other +
     * input e.g. via physical keys
     * <p>
     * @param enabled
     *              true to enabled touch-event, false to disable
     */
    public void setTouchEnabled(boolean enabled);

    /**
     * Gets the current touch-enabled state
     * <p>
     * @return true if the editor is touch-enabled, false otherwise
     */
    public boolean isTouchEnabled();
}

// End of file
