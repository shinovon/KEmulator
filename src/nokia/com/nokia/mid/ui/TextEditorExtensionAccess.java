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
* Description: Touch-support for TextEditor-API
*
*/

package com.nokia.mid.ui;

import com.nokia.mid.ui.TextEditorTouchControl;

/**
 * This interface provides access to extended functionality related to the TextEditor-API
 * The object returned by the method {@link com.nokia.mid.ui.TextEditor#createTextEditor(int, int, int, int)} and
 * {@link com.nokia.mid.ui.TextEditor#createTextEditor(int, int, int, int)} implements this interface.
 * It depends on the capabilities of the device whether the extensions provided via this interface are
 * supported or not. For unsupported extensions, the getter-methods of this interface will return null.
 */
public interface TextEditorExtensionAccess
{

    /**
     * Gets the Touch-extensions of the TextEditor-API. See also {@link TextEditorTouchControl}
     * @return the extension, or null if the device does not support touch.
     */
    public TextEditorTouchControl getTouchControl();

}
