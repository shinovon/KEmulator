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
import org.eclipse.swt.widgets.*;

class TextBoxLayouter
{

    /**
     * Key name for modify listener.
     */
    private static final String MODIFY_LISTENER = "modify";

    /**
     * Key name for key listener.
     */
    private static final String KEY_LISTENER = "key";

    /**
     * Key name for mouse listener.
     */
    private static final String MOUSE_LISTENER = "mouse";


    /**
     * Percentage of the whole screen.
     */
    private static final int TOTAL_PERCENTAGE = 100;


    // private static Control[] staticControls = new Control[6];

    private static boolean isCorrectText;

    private static Shell staticShell;

    static Shell swtGetStaticShell()
    {
        if(staticShell == null)
        {
            staticShell = new Shell(EmulatorImpl.getDisplay(), SWT.SYSTEM_MODAL | SWT.VERTICAL);
            staticShell.getVerticalBar().setVisible(true);
        }
        return staticShell;
    }

    /**
     * Get static eSWT control (ConstraintText or Text).
     *
     * @param constraint
     */
    static Control swtGetStaticTextControl(int constraint)
    {
        Control ret = null;

        /*
        int maskedConstraint = constraint & TextField.CONSTRAINT_MASK;

        if (staticControls[maskedConstraint] == null) {
            staticControls[maskedConstraint] = TextWrapper.swtConstructText(
                    swtGetStaticShell(), SWT.MULTI | SWT.WRAP, constraint);
            ret = staticControls[maskedConstraint];
        }
        */

        return new Text(swtGetStaticShell(), SWT.MULTI | SWT.WRAP | SWT.BORDER);
    }

    /**
     * Check that text satisfies specified constraints.
     *
     * @param constraint TextField.NUMERIC etc.
     * @return true if text is correct for specified constraint.
     */
    static boolean checkText(final int constraint, final String text)
    {
        isCorrectText = true;

        try
        {
         	  if(constraint == TextField.NUMERIC && !text.equals(""))
            {
                Integer.parseInt(text);
            }
            else if(constraint == TextField.DECIMAL && !text.equals(""))
            {
                Float.parseFloat(text);
            }
        }
        catch( NumberFormatException e )
        {
        	  // Illegal text
            return false;
        }

        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                try
                {
                    TextWrapper.swtSetContent(
                        swtGetStaticTextControl(constraint), text);
                }
                catch(IllegalArgumentException e)
                {
                    isCorrectText = false;
                }
            }
        });
        return isCorrectText;
    }

}