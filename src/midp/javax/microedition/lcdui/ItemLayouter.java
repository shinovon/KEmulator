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

import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

/**
 * Abstract base class for Item layouters.
 */
abstract class ItemLayouter
{

    /**
     * Key name for paint listener.
     */
    private static final String FOCUS_LISTENER = "FocusListener";

    protected static final String MIN_TEXT = "...";

    protected Composite formComposite;

    private static Label staticLabel;
    private static Group staticCC;
    private static Shell staticShell;

    private static Point captionedTrim;

    protected static int formWidth;
    protected static int formHeigh;

    /**
     * Gets static singleton off-screen Shell which can be used by item
     * layouters when creating temporary Controls.
     *
     * @return Static Shell. Never null.
     */
    static Shell swtGetStaticShell()
    {
        if(staticShell == null)
        {
            staticShell = new Shell(EmulatorImpl.getDisplay(), SWT.SYSTEM_MODAL | SWT.VERTICAL);
            staticShell.getVerticalBar().setVisible(true);
            formWidth = staticShell.getClientArea().width;
            formHeigh = staticShell.getClientArea().height;
        }
        return staticShell;
    }

    /**
     * Gets static singleton off-screen Label control.
     */
    private static Label swtGetStaticLabel()
    {
        if(staticLabel == null)
        {
            staticLabel = new Label(swtGetStaticShell(), SWT.NONE);
        }
        return staticLabel;
    }

    /**
     * Gets static singleton off-screen Captioned control.
     */
    private static Group swtGetStaticCC()
    {
        if(staticCC == null)
        {
            staticCC = new Group(swtGetStaticShell(), SWT.VERTICAL);
        }
        return staticCC;
    }

    /**
     * The static "layouting" shell's size is updated.
     */
    static void swtUpdateStaticShellSize(int width, int height)
    {
        if(staticShell != null)
        {
            staticShell.setBounds(staticShell.computeTrim(0, 0, width, height));
            formWidth = width;
            formHeigh = height;
        }
    }
}