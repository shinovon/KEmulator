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

import javax.microedition.lcdui.Item;
import org.eclipse.swt.widgets.Control;

interface ItemControlStateChangeListener
{

    public void notifyControlAvailable(Control ctrl,Item item);

    public void notifyControlDisposed(Item item);

}