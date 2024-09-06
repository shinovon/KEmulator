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
 * Description:  Public interface for listening TextEditor changes.
 *
 */

// PACKAGE
package com.nokia.mid.ui;

/**
 * <P>
 * A listener for receiving notification of content changes and other editor
 * events from <code>TextEditor</code> objects. The events are generated on
 * content changes that are either a result of a programmatical change or due to
 * an user input.
 * </P>
 *
 * @since 1.4
 */

public interface TextEditorListener
{
    /**
     * Indicates that the content of this <code>TextEditor</code> has changed.
     */
    public static final int ACTION_CONTENT_CHANGE = 0x1;

    /**
     * Indicates that the options of this <code>TextEditor</code> have changed.
     */
    public static final int ACTION_OPTIONS_CHANGE = 0x2;

    /**
     * Indicates that the caret in this <code>TextEditor</code> has moved.
     */
    public static final int ACTION_CARET_MOVE = 0x4;

    /**
     * Indicates that the user tries to exit this <code>TextEditor</code>
     * upwards.
     */
    public static final int ACTION_TRAVERSE_PREVIOUS = 0x8;

    /**
     * Indicates that the user tries to exit this <code>TextEditor</code>
     * downwards.
     */
    public static final int ACTION_TRAVERSE_NEXT = 0x10;

    /**
     * Indicates that this TextEditor has to be repainted. The application
     * should service this request by refreshing at least the region covered by
     * the TextEditor in its Canvas, which will also cause the TextEditor itself
     * to be redrawn. This can be accomplished by the MIDlet calling either
     * Canvas#repaint(x,y,width,height) or
     * GameCanvas#flushGraphics(x,y,width,height).
     */
    public static final int ACTION_PAINT_REQUEST = 0x20;

    /**
     * Indicates that the direction of the writing-language has changed.
     */
    public static final int ACTION_DIRECTION_CHANGE = 0x40;

    /**
     * Indicates that the current input-mode has changed.
     */
    public static final int ACTION_INPUT_MODE_CHANGE = 0x80;

    /**
     * Indicates that the current input-language has changed.
     */
    public static final int ACTION_LANGUAGE_CHANGE = 0x100;

    /**
     * Indicates that the editor cannot scroll up anymore.
     */
    public static final int ACTION_TRAVERSE_OUT_SCROLL_UP = 0x200;

    /**
     * Indicates that the editor cannot scroll down anymore.
     */
    public static final int ACTION_TRAVERSE_OUT_SCROLL_DOWN = 0x400;

    /**
     * Indicates that scrollbar should be updated.
     */
    public static final int ACTION_SCROLLBAR_CHANGED = 0x800;

    // Events 0x1000 and 0x2000 are used in s60 extension
    // Events 0x10000, 0x20000, 0x40000, 0x400000, 0x800000 are used in s40 extension

    /**
     * This method is called by the platform to notify the client about events
     * in a TextEditor. A call to this method may represent more than one event.
     * The events are masked into the actions-parameter, so an application can
     * determine which events have occured via<br>
     *
     * <code>
     * if( (actions&TextEditorListener.ACTION_CONTENT_CHANGE)!=0){<br>
     *  ...<br>
     * }<br>
     * if( (actions&TextEditorListener.ACTION_CARET_CHANGE)!=0){<br>
     *  ...<br>
     * }<br>
     * </code>
     *
     * @param textEditor
     *            the TextEditor instance where the event occured
     * @param actions
     *            the events that occured.
     */
    public void inputAction(TextEditor textEditor, int actions);
}

// End of file
