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
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

/**
 * Dynamic Text component implementation over the Text/ConstrainedText
 * widgets as a wrapper.
 */
class TextWrapper
{

    private String text;
    private String inputMode;
    private int constraints;
    private int caret;
    private int size;
    private int maxSize;

    private Control control; // owned
    private int style;
    private int width = -1;
    private int height = -1;
    private int x = -1;
    private int y = -1;
    private Color fgCol; // owned
    private Color bgCol; // owned
    private boolean visible = true;
    private boolean focused = true;

    private Font font;   // not owned
    private ModifyListener modifyListener; // not owned
    private SelectionListener selectionListener; // not owned

    private String retSelectedStr;
    private int retLineHeight;
    private int retLineCount;
    private int retTopPixel;

    /**
     * Constructor.
     *
     * @param aText
     * @param aMaxSize
     * @param aConstraints
     */
    TextWrapper(String aText, int aMaxSize, int aConstraints)
    {
        setMaxSize(aMaxSize);
        setConstraints(aConstraints);
        setContent(aText);
    }

    /**
     * Construct eSWT control on specified composite.
     *
     * @param parent composite to create on
     * @param addStyle additional style
     */
    void construct(final Composite parent, final int addStyle)
    {
        Displayable.safeSyncExec(new Runnable()
        {
            public void run()
            {
                swtConstruct(parent, addStyle);
            }
        });
    }

    /**
     * Construct eSWT control on specified composite.
     *
     * @param parent composite to create on
     * @param addStyle additional style
     */
    void swtConstruct(Composite parent, int addStyle)
    {
        style = addStyle | SWT.MULTI | SWT.WRAP | SWT.BORDER;
        if(parent != null)
        {
            swtStoreStateAndDispose();
            swtRestoreStateAndCreate(parent);
        }
        else
        {
            Logger.warning("Trying to construct TextWrapper with null parent");
        }
    }

    /**
     * Delete subset of characters from content.
     *
     * @param offset
     * @param length
     */
    void delete(int offset, int length)
    {
        if((offset + length) > getSize())
        {
            throw new StringIndexOutOfBoundsException();
        }
        StringBuffer sb = new StringBuffer(getContent());
        sb.delete(offset, offset + length);
        setContent(sb.toString());
    }

    /**
     * Dispose eSWT control.
     */
    void dispose()
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtStoreStateAndDispose();
                }
            });
        }
    }

    /**
     * Get caret position.
     */
    int getCaretPosition()
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    caret = swtGetCaretPosition(control);
                }
            });
        }
        return caret;
    }

    /**
     * Get constraints.
     */
    int getConstraints()
    {
        return constraints;
    }

    /**
     * Get text content.
     */
    String getContent()
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    text = swtGetContent(control);
                }
            });
        }
        return text;
    }

    /**
     * Gets the height of this Text control in pixels.
     */
    int getHeight()
    {
        return height;
    }

    /**
     * Get input mode.
     */
    String getInputMode()
    {
        return inputMode;
    }

    /**
     * Get line count.
     */
    int getLineCount()
    {
        retLineCount = 1;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    retLineCount = swtGetLineCount(control);
                }
            });
        }
        return retLineCount;
    }

    /**
     * Get line height (in pixels).
     */
    int getLineHeight()
    {
        retLineHeight = 1;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    retLineHeight = swtGetLineHeight(control);
                }
            });
        }
        return retLineHeight;
    }

    /**
     * Get maximum size (in characters).
     */
    int getMaxSize()
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    maxSize = swtGetMaxSize(control);
                }
            });
        }
        return maxSize;
    }

    /**
     * Get the preferred height (in pixels) of the text control.
     *
     * @param maxVisibleLines maximum visible lines
     */
    int getPreferredHeight(int maxVisibleLines)
    {
        // lineCount (1 .. max) * lineHeight
        return Math.min(Math.max(1, getLineCount()), maxVisibleLines)
               * getLineHeight();
    }

    /**
     * Get selected text content.
     */
    String getSelectedContent()
    {
        retSelectedStr = "";
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    retSelectedStr = swtGetSelectedContent(control);
                }
            });
        }
        return retSelectedStr;
    }

    /**
     * Get content's length.
     */
    int getSize()
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    size = swtGetSize(control);
                }
            });
        }
        return size;
    }

    /**
     * Get visible top pixel position relative to whole content.
     */
    int getTopPixelPosition()
    {
        retTopPixel = 0;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    if(control instanceof Text)
                    {
                        retTopPixel = ((Text) control).getTopPixel();
                    }
                }
            });
        }
        return retTopPixel;
    }

    /**
     * Gets the width of this Text control (in pixels).
     */
    int getWidth()
    {
        return width;
    }

    /**
     * Insert text content.
     *
     * @param aText text
     * @param aPosition position where to instert
     */
    void insert(String aText, int aPosition)
    {
        if(aText == null)
        {
            throw new NullPointerException();
        }
        StringBuffer sb = new StringBuffer(getContent());
        if(aPosition < 0)
        {
            sb.insert(0, aText);
        }
        else if(aPosition > sb.length())
        {
            sb.append(aText);
        }
        else
        {
            sb.insert(aPosition, aText);
        }
        setContent(sb.toString());
    }

    /**
     * Set background color.
     *
     * @param alpha alpha component (currently not used)
     * @param red color component
     * @param green color component
     * @param blue color component
     */
    void setBackgroundColor(final int alpha, final int red, final int green, final int blue)
    {
        Displayable.safeSyncExec(new Runnable()
        {
            public void run()
            {
                if(bgCol != null)
                {
                    bgCol.dispose();
                    bgCol = null;
                }
                bgCol = new Color(EmulatorImpl.getDisplay(),
                                  red, green, blue);
            }
        });
    }

    /**
     * Set the bounds of the Text control.
     *
     * @param aBounds bounding rectangle
     */
    void setBounds(Rectangle aBounds)
    {
        if(aBounds.width < 0 || aBounds.height < 0)
        {
            throw new IllegalArgumentException();
        }
        x = aBounds.x;
        y = aBounds.y;
        width = aBounds.width;
        height = aBounds.height;
        if(control != null && width >= 0 && height >= 0)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    control.setBounds(x, y, width, height);
                }
            });
        }
    }

    /**
     * Set caret position.
     *
     * @param aPosition position
     */
    void setCaretposition(int aPosition)
    {
        caret = aPosition;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtSetSelection(control, caret, caret);
                }
            });
        }
    }

    /**
     * Set constraints. Note: this might reconstruct the eSWT text control.
     *
     * @param aConstraints text input constraints
     */
    void setConstraints(int aConstraints)
    {
        if(!isValidConstraints(aConstraints))
        {
            throw new IllegalArgumentException();
        }
        constraints = aConstraints;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtRestoreStateAndCreate(swtStoreStateAndDispose());
                }
            });
        }
    }

    /**
     * Set text content.
     *
     * @param aText new content
     */
    void setContent(String aText)
    {
        if(aText == null)
        {
            text = "";
        }
        else
        {
            if(aText.length() > maxSize)
            {
                throw new IllegalArgumentException();
            }
            if(!isValidText(aText, constraints))
            {
                throw new IllegalArgumentException();
            }
            text = aText;
        }
        size = text.length();
        caret = text.length();
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtSetContent(control, text);
                    swtSetSelection(control, caret, caret);
                }
            });
        }
    }

    /**
     * Set text font.
     *
     * @param aFont new font
     */
    void setFont(Font aFont)
    {
        font = aFont;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    if(font != null)
                    {
                        // set custom font
                        control.setFont(Font.getSWTFont(font));
                    }
                    else
                    {
                        // set default font
                        control.setFont(
                            Font.getSWTFont(Font.getDefaultFont()));
                    }
                }
            });
        }
    }

    /**
     * Set this Text control focused.
     *
     * @param aFocused
     */
    void setFocused(boolean aFocused)
    {
        focused = aFocused;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtSetFocused(control, focused);
                }
            });
        }
    }

    /**
     * Set foreground color.
     *
     * @param alpha alpha component (currently not used)
     * @param red color component
     * @param green color component
     * @param blue color component
     */
    void setForegroundColor(final int alpha, final int red, final int green, final int blue)
    {
        Displayable.safeSyncExec(new Runnable()
        {
            public void run()
            {
                if(fgCol != null)
                {
                    fgCol.dispose();
                    fgCol = null;
                }
                fgCol = new Color(EmulatorImpl.getDisplay(),
                                  red, green, blue);
            }
        });
    }

    /**
     * Set input mode.
     */
    void setInputMode(String aInputMode)
    {
        inputMode = aInputMode;
        if(control != null && control instanceof Text)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtSetInputMode(control, inputMode, constraints);
                }
            });
        }
    }

    /**
     * Set maximum size (in characters).
     *
     * @param aMaxSize
     */
    void setMaxSize(int aMaxSize)
    {
        if(aMaxSize < 1)
        {
            throw new IllegalArgumentException();
        }

        String content = getContent();
        if(content != null && aMaxSize < content.length())
        {
            // we have to truncate content - validates the new text
            setContent(content.substring(0, aMaxSize));
        }

        maxSize = aMaxSize;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtSetMaxSize(control, maxSize);
                }
            });
        }
    }

    /**
     * Set the modify listener;
     *
     * @param aListener modify listener
     */
    void setModifyListener(final ModifyListener aListener)
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtRemoveModListener(control, modifyListener);
                    swtAddModListener(control, aListener);
                }
            });
        }
        modifyListener = aListener;
    }

    /**
     * Set the selection listener.
     *
     * @param aListener selection listener
     */
    void setSelectionListener(final SelectionListener aListener)
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtRemoveSelListener(control, selectionListener);
                    swtAddSelListener(control, aListener);
                }
            });
        }
        selectionListener = aListener;
    }

    /**
     * Set the location of the eSWT control.
     *
     * @param aX
     * @param aY
     */
    void setPosition(int aX, int aY)
    {
        x = aX;
        y = aY;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    control.setLocation(x, y);
                }
            });
        }
    }

    /**
     * Set the selection in the eSWT text control.
     *
     * @param sta selection start
     * @param end selection end
     */
    void setSelection(final int sta, final int end)
    {
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    swtSetSelection(control, sta, end);
                }
            });
        }
    }

    /**
     * Set the size of the eSWT control.
     */
    void setSize(int aWidth, int aHeight)
    {
        if(aWidth < 0 || aHeight < 0)
        {
            throw new IllegalArgumentException();
        }
        width = aWidth;
        height = aHeight;
        if(control != null && width >= 0 && height >= 0)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    control.setSize(width, height);
                }
            });
        }
    }

    /**
     * Set the visibility of the eSWT control.
     */
    void setVisible(boolean aVisible)
    {
        visible = aVisible;
        if(control != null)
        {
            Displayable.safeSyncExec(new Runnable()
            {
                public void run()
                {
                    control.setVisible(visible);
                }
            });
        }
    }

    /**
     * Restores a stored state and creates eSWT text control.
     *
     * @param parent parent composite
     */
    private void swtRestoreStateAndCreate(Composite parent)
    {
        if(control == null)
        {
            control = swtConstructText(parent, style, constraints);
            swtSetInputMode(control, inputMode, constraints);
            swtSetMaxSize(control, maxSize);
            swtSetContent(control, text);
            swtSetSelection(control, caret, caret);
            if(width >= 0 && height >= 0)
            {
                control.setBounds(x, y, width, height);
            }
            if(fgCol != null)
            {
                control.setForeground(fgCol);
            }
            if(bgCol != null)
            {
                control.setBackground(bgCol);
            }
            if(font != null)
            {
                control.setFont(Font.getSWTFont(font));
            }
            control.setVisible(visible);
            swtSetFocused(control, focused);
            swtAddModListener(control, modifyListener);
            swtAddSelListener(control, selectionListener);
        }
    }

    /**
     * Stores the current state and disposes the eSWT text control.
     *
     * @return the control's parent composite.
     */
    private Composite swtStoreStateAndDispose()
    {
        Composite parent = null;
        if(control != null)
        {
            parent = control.getParent();
            text = swtGetContent(control);
            caret = swtGetCaretPosition(control);
            swtRemoveModListener(control, modifyListener);
            swtRemoveSelListener(control, selectionListener);
            control.dispose();
            control = null;
        }
        return parent;
    }

    /**
     * Add modify listener.
     *
     * @param control text control
     * @param ltnr listener
     */
    static void swtAddModListener(Control control, ModifyListener ltnr)
    {
        if(control != null && ltnr != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).addModifyListener(ltnr);
            }
        }
    }

    /**
     * Add selection listener.
     *
     * @param control text control
     * @param ltnr listener
     */
    static void swtAddSelListener(Control control, SelectionListener ltnr)
    {
        if(control != null && ltnr != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).addSelectionListener(ltnr);
            }
        }
    }

    /**
     * Construct text widget with given parameters.
     *
     * @param parent
     * @param aStyle
     * @param aConstraints
     * @return
     */
    static Control swtConstructText(Composite parent, int aStyle, int aConstraints)
    {

        int style = aStyle;
        int extractedFlag = aConstraints & ~TextField.CONSTRAINT_MASK;
        int extractedConstraint = aConstraints & TextField.CONSTRAINT_MASK;

        if((extractedFlag & TextField.PASSWORD) == TextField.PASSWORD)
        {
            // Text class will remove incompatible flags for SINGLE
            style &= ~SWT.MULTI;
            style |= SWT.SINGLE | SWT.PASSWORD;
        }
        if((extractedFlag & TextField.UNEDITABLE) == TextField.UNEDITABLE)
        {
            style |= SWT.READ_ONLY;
        }

//        if(extractedConstraint == TextField.NUMERIC)
//        {
//            extractedConstraint = TextExtension.NUMERIC;
//        }
//        else if(extractedConstraint == TextField.DECIMAL)
//        {
//            extractedConstraint = TextExtension.DECIMAL;
//        }
//        else if(extractedConstraint == TextField.PHONENUMBER)
//        {
//            extractedConstraint = TextExtension.PHONENUMBER;
//       }
//       else {
//            extractedConstraint = 0;
//       }
        return new Text(parent, style);
    }

    /**
     * Get caret position.
     *
     * @param control text control
     */
    static int swtGetCaretPosition(Control control)
    {
        int ret = 0;
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getCaretPosition();
            }
        }
        return ret;
    }

    /**
     * Get caret line number.
     *
     * @param control text control
     */
    static int swtGetCaretLine(Control control)
    {
        int ret = 0;
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getCaretLineNumber();
            }
        }
        return ret;
    }

    /**
     * Get content.
     *
     * @param control text control
     */
    static String swtGetContent(Control control)
    {
        String ret = "";
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getText();
            }
        }
        return ret;
    }

    /**
     * Get line count.
     *
     * @param control text control
     */
    static int swtGetLineCount(Control control)
    {
        int ret = 1;
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getLineCount();
            }
        }
        return ret;
    }

    /**
     * Get line height.
     *
     * @param control text control
     */
    static int swtGetLineHeight(Control control)
    {
        int ret = 0;
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getLineHeight();
            }
        }
        return ret;
    }

    /**
     * Get maximum content size.
     *
     * @param control text control
     */
    static int swtGetMaxSize(Control control)
    {
        int ret = 0;
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getTextLimit();
            }
        }
        return ret;
    }

    /**
     * Get selected content.
     *
     * @param control text control
     */
    static String swtGetSelectedContent(Control control)
    {
        String ret = "";
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getSelectionText();
            }
        }
        return ret;
    }

    /**
     * Get content size.
     *
     * @param control text control
     */
    static int swtGetSize(Control control)
    {
        int ret = 0;
        if(control != null)
        {
            if(control instanceof Text)
            {
                ret = ((Text) control).getCharCount();
            }
        }
        return ret;
    }

    /**
     * Remove modify listener.
     *
     * @param control text control
     * @param ltnr listener
     */
    static void swtRemoveModListener(Control control, ModifyListener ltnr)
    {
        if(control != null && ltnr != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).removeModifyListener(ltnr);
            }
        }
    }

    /**
     * Remove modify listener.
     *
     * @param control text control
     * @param ltnr listener
     */
    static void swtRemoveSelListener(Control control, SelectionListener ltnr)
    {
        if(control != null && ltnr != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).removeSelectionListener(ltnr);
            }
        }
    }

    /**
     * Set content.
     *
     * @param control text control
     * @param text content
     */
    static void swtSetContent(Control control, String text)
    {
        if(control != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).setText(text);
            }
        }
    }

    /**
     * Set text control focus on/off.
     *
     * @param control text control
     * @param focus
     */
    static void swtSetFocused(Control control, boolean focus)
    {
        if(control != null)
        {
            if(focus)
            {
                control.setFocus();
            }
            else
            {
                control.getParent().forceFocus();
            }
        }
    }

    /**
     * Set input mode.
     *
     * @param control text control
     * @param inputMode input mode
     * @param aConstraints constraints
     */
    static void swtSetInputMode(Control control, String inputMode,
                                 int aConstraints)
    {
//        if(control != null && control instanceof Text)
//        {
//            Text te = (Text) control;
//            int capitalize = getCapitalize(aConstraints);
//            if(inputMode == null)
//            {
//                te.setInitialInputMode(Text.TEXTCASE,
//                                       "UCB_BASIC_LATIN");
//            }
//            else if(inputMode.equals("MIDP_UPPERCASE_LATIN"))
//            {
//                if(capitalize > 0 && !isUrlEmailSet(aConstraints))
//                {
//                    te.setInitialInputMode(capitalize, "UCB_BASIC_LATIN");
//                }
//                else
//                {
//                    te.setInitialInputMode(Text.UPPERCASE,
//                                           "UCB_BASIC_LATIN");
//                }
//            }
//            else if(inputMode.equals("MIDP_LOWERCASE_LATIN"))
//            {
//                if(capitalize > 0 && !isUrlEmailSet(aConstraints))
//                {
//                    te.setInitialInputMode(capitalize, "UCB_BASIC_LATIN");
//                }
//                else
//                {
//                    te.setInitialInputMode(Text.LOWERCASE,
//                                           "UCB_BASIC_LATIN");
//                }
//            }
//            else
//            {
//                te.setInitialInputMode(Text.TEXTCASE, inputMode);
//            }
//        }
    }

    /**
     * Set maximum size.
     *
     * @param control text control
     * @param maxSize maximum size
     */
    static void swtSetMaxSize(Control control, int maxSize)
    {
        if(control != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).setTextLimit(maxSize);
            }
        }
    }

    /**
     * Set selection.
     *
     * @param control text control
     * @param sta start index
     * @param end end index
     */
    static void swtSetSelection(Control control, int sta, int end)
    {
        if(control != null)
        {
            if(control instanceof Text)
            {
                ((Text) control).setSelection(sta, end);
            }
        }
    }

    /**
     * Update vertical scroll bar.
     *
     * @param control text control
     */
    static void swtUpdateVScrollbar(Control control)
    {
        if(control != null)
        {
            if((control.getStyle() & SWT.V_SCROLL) == SWT.V_SCROLL)
            {
                if(control instanceof Text)
                {
                    Text te = (Text) control;
                    ScrollBar sb = te.getVerticalBar();
                    if(sb != null)
                    {
                        int height = te.getLineCount() * te.getLineHeight();
                        sb.setVisible(te.getSize().y < height);
                    }
                }
            }
        }
    }

    /**
     * Get eSWT capitalize style flags for Text.
     *
     * @param aConstraints constraints
     */
    static int getCapitalize(int aConstraints)
    {
        int ret = 0;
//        int extractedFlag = aConstraints & ~TextField.CONSTRAINT_MASK;
//        if((extractedFlag & TextField.INITIAL_CAPS_WORD)
//                == TextField.INITIAL_CAPS_WORD)
//        {
//            ret = Text.TITLECASE;
//        }
//        if((extractedFlag & TextField.INITIAL_CAPS_SENTENCE)
//                == TextField.INITIAL_CAPS_SENTENCE)
//        {
//            ret = Text.TEXTCASE;
//        }
        return ret;
    }

    /**
     * Is Url or Email constraints flags set.
     *
     * @param aConstraints constraints
     */
    static boolean isUrlEmailSet(int aConstraints)
    {
        int extractedConstraint = aConstraints & TextField.CONSTRAINT_MASK;
        return (extractedConstraint == TextField.EMAILADDR
                || extractedConstraint == TextField.URL);
    }

    /**
     * Validates input constraints.
     *
     * @param aConstraints constraints to check.
     * @return true if constraints are OK, false otherwise.
     */
    static boolean isValidConstraints(int aConstraints)
    {
        int smallestFlag;
        int highestFlag;
        smallestFlag = TextField.PASSWORD - 1;
        highestFlag = TextField.PASSWORD + TextField.UNEDITABLE
                      + TextField.SENSITIVE + TextField.NON_PREDICTIVE
                      + TextField.INITIAL_CAPS_WORD + TextField.INITIAL_CAPS_SENTENCE
                      + 1;

        int typeConstraint = getTypeConstraint(aConstraints);
        if(typeConstraint == TextField.ANY
                || typeConstraint == TextField.EMAILADDR
                || typeConstraint == TextField.NUMERIC
                || typeConstraint == TextField.PHONENUMBER
                || typeConstraint == TextField.DECIMAL
                || typeConstraint == TextField.URL)
        {
            int extractedFlag = aConstraints & ~TextField.CONSTRAINT_MASK;
            if(extractedFlag == 0
                    || ((extractedFlag > smallestFlag)
                        && (extractedFlag < highestFlag)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get constraint type.
     *
     * @param aConstraints constraints
     */
    static int getTypeConstraint(int aConstraints)
    {
        return aConstraints & TextField.CONSTRAINT_MASK;
    }

    /**
     * Checks if the text meets the requirements from constraint.
     *
     * @param aText
     * @return
     */
    static boolean isValidText(String aText, int aConstraints)
    {
        return TextFieldLayouter.checkText(aConstraints, aText);
    }

}
