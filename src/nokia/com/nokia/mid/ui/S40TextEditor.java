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
 * Description:  Interface for S40 Text Editor
 *
 */

package com.nokia.mid.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

/**
 * <p>
 * This interfaces provides access to extended editing-related functionality, that is only
 * available on Series 40 devices.</p>
 * <b>Commands:</b><br>
 * Applications can use {@link com.nokia.mid.ui.S40TextEditor#getTextEditorCommands()} and {@link com.nokia.mid.ui.S40TextEditor#launchTextEditorCommand(Command, int)}
 * to present editing-commands in a customized way. When launching such a command, the application has to specify a
 * command-mode. This is because some commands may require information regarding the key-state that has caused this command to
 * be launched. For example, calling <code>launchTextEditorCommand(cmd, COMMAND_MODE_KEYPRESS)</code> for the "Clear"-command will cause
 * to editor to keep deleting characters until the application calls <code>launchTextEditorCommand(cmd, COMMAND_MODE_KEYRELEASE)</code>.
 * Since not all commands require keystate-information, applications can use {@link com.nokia.mid.ui.S40TextEditor#isCommandKeyWanted(Command)}
 * If no command-key is wanted, COMMAND_MODE_SELECTED should be passed when launching this command <p>
 * <b>Indicator-icons</b><br>
 * It is possible to access the indicator-icons that the platform would normally display as Images, using
 * {@link com.nokia.mid.ui.S40TextEditor#getIndicatorIcons()} <p>
 * <b>Visibility</b><br>
 * Applications may be in the situation where they want an editor to be visible, but to not process keys or be animated, e.g. while
 * a custom options-menu is displayed. In those cases, it is possible to call {@link com.nokia.mid.ui.S40TextEditor#setVisible(int)} <p>
 * <b>Extra properties</b><br>
 * Some properties that are not available in the com.nokia.mid.ui.TextEditor, like writing-direction, native emoticon support
 * and positioning for native popup-windows.  <p>
 * On Series 40 devices this interface is implemented by the object returned by {@link com.nokia.mid.ui.TextEditor#createTextEditor(String, int, int, int, int)} if
 * the MIDlet is manufacturer or operator signed.
 *
 */
public abstract interface S40TextEditor {
	
	/**
	 * command type used to indicate commands
	 * that should be put on the middle softkey
	 */
	public final static int SELECT_SOFTKEY_COMMAND = 9;

    /**
     * command type used to indicate commands
     * that should be put on the right softkey.
     */
    public final static int RIGHT_SOFTKEY_COMMAND = 10;	  
    
    /**
     * command type used to indicate commands
     * that should be put as the last one in the option list.
     */
    public final static int LAST_IN_OPTIONS_COMMAND = 12;

    /**
     * command type used to indicate commands
     * that should be put on the left softkey.
     */
    public final static int LEFT_SOFTKEY_COMMAND = 13;
    	
			
	/**
	 * constant for left-to-right direction
	 */
	public static final int DIRECTION_LTR = 0;
	
	/**
	 * constant for right-to-left direction
	 */
	public static final int DIRECTION_RTL = 1;
	
	/**
     * Constant for Cursor Wrap Off
     * No cursor wrapping occurs.
     * Traverse out callback is active for up / down
     * Traverse out callback is active for left / right when the editor is empty.
     */
    public static final int CURSOR_WRAP_OFF = 0;
    
    /**
     * Constant for Cursor Wrap Left/Right
     * Cursor wrapping is active only for left / right. 
     * Traverse out callback is supported for up / down.
     */
    public static final int CURSOR_WRAP_LEFT_RIGHT = 2;
    
    /**
     * Constant for Cursor Wrap Full
     * Cursor wrapping is active for all directions (up / down / left / right). 
     * Doesnt send traverse out callback.
     */
    public static final int CURSOR_WRAP_FULL = 1;
    
	/**
	 * Event that indicates that the applications options-list should be closed.
	 * The implementation sends this even in a case where the platform has displayed further
	 * nested options as the result of a {@link com.nokia.mid.ui.S40TextEditor#launchTextEditorCommand(Command, int)}-call.
	 * If the user selects one of the nested options (e.g. "copy"), the platform will carry out the
	 * associated functionality, dismiss the native options-popup  and send the 
	 * ACTION_OPTIONS_CLOSED-event, in order to indicate that any options-list that the application
	 * might have displayed should also be closed. 
	 *  
	 */	
	public static final int ACTION_OPTIONS_CLOSED = 0x00010000;		
    
    /**
     * Indicates that the user tries to exit this TextEditor left.
     * This action is only generated when the cursor wrap mode is set to {@link #CURSOR_WRAP_OFF} and the editor
     * is empty.
     */
    public static final int ACTION_TRAVERSE_LEFT = 0x00020000;
    
    /**
     * Indicates that the user tries to exit this TextEditor right.
     * This action is only generated when the cursor wrap mode is set to {@link #CURSOR_WRAP_OFF} and the editor
     * is empty.
     */
    public static final int ACTION_TRAVERSE_RIGHT = 0x00040000;
	
//  native_const(JAVA_TEXTEDITOR)
//  {
	
	/**
	 * Indicates that the text in this TextEditor is masked according to the
	 * Oz Mobile Password Masking Scheme 
	 */
	public static final int PASSWORD_MASKED = 0x400000;
	
	/**
	 * Indicates that the text in this TextEditor is locked according to the
	 * Oz Mobile Password Masking Scheme 
	 */
	public static final int PASSWORD_LOCKED = 0x800000;
//  }	
	
	/**
	 * constant value to indicate a hidden/invisible TextEditor
	 */
	public static final int HIDDEN=1;		
	
	/**
	 * constant value to indicate partial visibility of a TextEditor.
	 * A partially visible TextEditor will still be displayed, but not
	 * receive any key-events, and not have a blinking cursor
	 */
	public static final int PARTIALLY_VISIBLE=2;
	
	/** 
	 * constant value to indicate full visibility of a TextEditor
	 */
	public static final int VISIBLE=3;
			
	
	/**
	 * Command-mode to indicate that a command is launched without keystate-information, e.g. from
	 * an options-menu
	 */
	public static final int COMMAND_MODE_SELECTED=0;
	
	/**
	 * Command-mode to indicate that a command is launched with a keypress, e.g. by pressing RSK 
	 */
	public static final int COMMAND_MODE_KEYPRESS=1;
	
	/**
	 * Command-mode to indicate that a command has been launched with a keypress, and that this key 
	 * has now been released, e.g. by releasing the RSK
	 */
	public static final int COMMAND_MODE_KEYRELEASE=2;
				
	/**
	 * Specifies the current input mode of this TextEditor
	 * @param mode the new input-mode. This should be a value returned by {@link com.nokia.mid.ui.S40TextEditor#getInputMode()} 
	 */
	public void setInputMode(int mode) throws IllegalArgumentException;
	
	/**
	 * Gets the current input mode of this TextEditor
	 * @return the current input mode. This value represents a native input-mode, that can be passed to {@link com.nokia.mid.ui.S40TextEditor#setInputMode(int)} 
	 * 
	 */
	public int getInputMode();
	
	/**
	 * Returns the current set of Editor-Commands
	 * @return the commands
	 */
	public Command[] getTextEditorCommands();
	
	/**
	 * Executes an editor-option. This method will invoke native functionality according to the command. This may
	 * be an action in the editor (copy, delete,..) which then in turn will cause callbacks to this editors
	 * TextEditorListener. It may as well cause platform-controlled panels (nested options, touch-dialog) to cover part of
	 * or the full display. <br>
	 * This method should be called from inside the keyPressed()-method.
	 * @param cmd The command to launch. This has to be a command returned by {@link com.nokia.mid.ui.S40TextEditor#getTextEditorCommands()}
	 * @return true if launching this command has displayed a list of further sub-commands as a popup. if an application
	 * has displayed an options-menu before launching a command, this options-menu should normally stay on the screen
	 * after this launch, to indicate that the native sub-commands are nested and related to the command that has been
	 * launched.   
	 * 
	 * @throws IllegalStateException if the TextEditor is not focussed during this call
	 * @throws IllegalArgumentException if the command is not available in the editor. Commands that are
	 * available in the editor are only those that this editor has provided via {@link com.nokia.mid.ui.S40TextEditor#getTextEditorCommands()}
	 */
	public boolean launchTextEditorCommand(Command cmd,int mode) throws IllegalStateException, IllegalArgumentException;
	
	/**
	 * Gets the menu-state of a Command
	 * @param cmd The Command to query for Menu-state. This has to be a command returned by {@link com.nokia.mid.ui.S40TextEditor#getTextEditorCommands()} 
	 * @return true if this a Menu Command. Launching a Menu Command will show a popup on the display, providing further
	 * commands to the user  
	 */
	public boolean isMenuCommand(Command cmd);
	
	/**
	 * Returns whether a command requires keystate-information to be launched.
	 * @param cmd The command to be queried
	 * @return true if cmd requires keystate-information. In this case, one of COMMAND_MODE_KEYPRESS or COMMAND_MODE_KEYRELEASE
	 * should be passed when launching this command via {@link com.nokia.mid.ui.S40TextEditor#launchTextEditorCommand(Command, int)}
	 * false if cmd does not require keystate-informationl. In that case, COMMAND_MODE_SELECTED should be passed when launching it.
	 */
	public boolean isCommandKeyWanted(Command cmd);
				
	
	/**
	 * Specifies the amount of pixels by which the TextEditor will scroll text horizontally, if multiline
	 * input is disabled.	 
	 * @param offset The scrolling offset. When a cursor-movement requires horizontal scrolling of the
	 * text, this value indicates by how many pixels the text will be moved. 
	 */
	public void setHorizontalScrollingWidth(int offset);
	
	/**
	 * Gets the current horizontal scrolling width
	 * @return the scrolling width
	 */
	public int getHorizontalScrollingWidth();		
	
	/**
	 * Gets the current writing-direction
	 * @return DIRECTION_LTR or DIRECTION_RTL
	 */
	public int getWritingDirection();
	
	/**
	 * Gets the current indicator-icons as images
	 * @return the icons
	 */
	public Image[] getIndicatorIcons();
	
	/**
	 * Specifies the visibility of this TextEditor.
	 * In some cases, for example when an application wants to display a custom Options-Menu, the method
	 * {@link com.nokia.mid.ui.TextEditor#setVisible(boolean)} may not be sufficient. For this purpose,
	 * this method allows to specify a partial visibility, which will display the TextEditor without 
	 * e.g. animating the curosr, an in which the application can receive the key-events from the Canvas.	
	 * @param visible the type of the visibility. Has to be one of VISIBLE,PARTIALLY_VISIBLE or HIDDEN.
	 * Calling this method with VISIBLE has the same effect as calling {@link com.nokia.mid.ui.TextEditor#setVisible(boolean)}
	 * with true, HIDDEN as with false.
	 * @throws IllegalArgumentException if visible has an unspecified value
	 */
	public void setVisible(int visible) throws IllegalArgumentException;

	/**
	 * Specifies whether emoticons should be available for this editor. If not enabled, the implementation
	 * will not provide any facilities to insert emoticons, e.g. via commands or special character-dialog, 
	 * itself
	 * @param enable true to enable emoticons, false to disable	 
	 */
	public void enableEmoticons(boolean enable);	
	
	/**
	 * Gets the emoticons-state of this editor
	 * @return true if emoticons are enabled, false if not
	 */
	public boolean isEnableEmoticons();
	
	/**
	 * Specifies a position on the screen where native popups will be displayed. This is a hint to the
	 * implementation, that may be disregarded.
	 * @param x x-coordinate of the popups position
	 * @param y y-coordinate of the popups position
	 */
	public void setPopupPosition(int x,int y);
	
	/**
	 * Specifies prefix and postfix text to be displayed along with the actual editor-content.
	 * This text will not be part of the actual editor-buffer, and hence can't be edited, and will not
	 * be considered by methods like {@link com.nokia.mid.ui.TextEditor#size()}
	 * @param prefix text to be displayed before the actual content. May be null to disable prefix-text 
	 * @param postfix text to be displayed after the actual content. May be null to disable postfix-text
	 */
	public void setFixedText(String prefix,String postfix);
    
    /**
     * Specifies the Cursor Wrap setting. This is required to allow the canvas editor to change state so
     * that left / right cursor keys are able to traverse out of the editor if the editor is empty.
     * @param wrap new cursor wrap setting 
     */
    public void setCursorWrap(int wrap);
    
    /**
     * Gets the current Cursor Wrap setting
     * @return current cursor wrap setting
     */
    public int getCursorWrap();
    
}
