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

import java.util.Vector;

import emulator.Emulator;
import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * Layouter for StringItems.
 */
class StringItemLayouter extends ItemLayouter
{

    /**
     * Key name for hyperlink filter.
     */
    private static final String HYPERLINK_FILTER = "hyperlink";

    private static final String LINE_CUT_INDICATOR = "...";

    private HyperLinkListener hLinkListener = new HyperLinkListener();

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    StringItemLayouter(FormLayouter aFormLayouter)
    {
        super(aFormLayouter);
    }

    /**
     * Creates the eSWT control. This method creates only StringItems of type
     * BUTTON and those items must have at least one command. Otherwise
     * StringItem is displayed as PLAIN and that case is handled elsewhere.
     *
     * @param parent where to create
     * @param item on which it is based. Must be StringItem of type BUTTON and
     *            at least one command in it.
     * @return Control
     */
    Control swtGetControl(Composite parent, Item item)
    {
        StringItem stringItem = (StringItem) item;
        if(stringItem.getAppearanceMode() == Item.BUTTON
                && item.getNumCommands() > 0)
        {
            Button button = new Button(parent, SWT.PUSH);
            button.setFont(Font.getSWTFont(stringItem.getFont()));
            int areaWidth = 0;
            String line = stringItem.getText();
            if((areaWidth = item.getLockedPreferredWidth()) == -1)
            {
                areaWidth = getMaximumItemWidth(item);
            }
            line = truncateIfNewLine(line);
            if(stringItem.getFont().stringWidth(line) > areaWidth)
            {
                // Wrap string to find out how many lines it would take:
                Vector strings = StringWrapper.wrapString(line,
                                 stringItem.getFont(), true, areaWidth, areaWidth);
                if(strings != null && strings.size() > 1)
                {

                    line = (String) strings.elementAt(0);
                    line = line.substring(0, line.length())
                           + LINE_CUT_INDICATOR;
                }
            }
            button.setText(line);
            return button;
        }
        return null;
    }

    /**
     * Add listeners to Layouter specific control.
     */
    void swtAddSpecificListeners(Item item, Control control)
    {
        super.swtAddSpecificListeners(item, control);
        Logger.method(this, "swtAddSpecificListener");
    }

    /**
     * Remove listeners from Layouter specific control.
     */
    void swtRemoveSpecificListeners(Item item, Control control)
    {
        super.swtRemoveSpecificListeners(item, control);
        Logger.method(this, "swtRemoveSpecificListener");
    }

    /**
     * Returns true if this eSWT control is Layouter specific.
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
        if(item.getNumCommands() > 0)
        {
            int am = ((StringItem) item).getAppearanceMode();
            if(am == Item.BUTTON)
            {
                if(control instanceof Button)
                {
                    return true;
                }
                return false;
            }
            else if(am == Item.HYPERLINK)
            {
                return true;
            }
        }
        // Item is plain:
        return true;
    }

    /**
     * Updates the values of StringItem.
     */
    void swtUpdateItem(Item item, Control control, int reason, Object param)
    {
        // No implementation needed.
    }

    /**
     * Layout Item in a row.
     *
     * @param row current Row
     * @param item Item to layout
     */
    void swtLayoutItem(Row row, Item item)
    {
        StringItem stringItem = (StringItem) item;
        if(stringItem.getNumCommands() > 0)
        {
            if(stringItem.getAppearanceMode() == StringItem.BUTTON)
            {
                // BUTTON
                LayoutObject lo = formLayouter.getLayoutObject(item);
                formLayouter.swtAddNewLayoutObject(lo == null ? 
					new LayoutObject(item, swtGetGroup(formComposite, item)) : lo);
            }
            else
            {
                // HYPERLINK
                swtLayoutPlainStringItem(row, stringItem, true);
                if(item.isFocused())
                {
                    // Highlight hyperlink if it is focused:
                    swtHighlightHyperlinkItem(item, true);
                }
            }
        }
        else
        {
            // PLAIN
            swtLayoutPlainStringItem(row, stringItem, false);
        }
    }

    /**
     * Layout StringItem in a row.
     *
     * @param row current Row
     * @param item StringItem to layout
     * @param isHyperlink If true, StringItem of type Hyperlink is created.
     *            Otherwise creates plain StringItem.
     */
    private void swtLayoutPlainStringItem(Row row, StringItem item,
                                           boolean isHyperlink)
    {
        if(item.isSizeLocked())
        {
            swtLayoutLockedPlainStringItem(item, isHyperlink);
            return;
        }

        String label = item.getLabel();

        Vector strings = StringWrapper.wrapString(item.getText(),
                         item.getFont(), formLayouter.getForm().getLeftRightLanguage(),
                         row.getRowWidth(), row.getFreeSpace());

        if(strings != null)
        {
            formLayouter.removeAllLayoutObjects(item);
            for(int i = 0; i < strings.size(); i++)
            {
                // create primitive StringItem
                Control control = swtCreateLabeledPrimitiveStringItem(
                                      formLayouter.getForm().getFormComposite(),
                                      (String) strings.elementAt(i),
                                      label, item.getFont(), isHyperlink,
                                      getMaximumItemWidth(item));

                // because add label to first primitive only:
                label = null;

                // layoutObject which represent primitive StringItem
                formLayouter.swtAddNewLayoutObject(new LayoutObject(item, control), i != 0);
            }
        }
    }

    /**
     * Layout StringItem which height and/or width is locked.
     * This kind of stringitems are always layouted to rectangular
     * area. If the area is not large enough to display all content
     * of the StringItem then the method adds three dots to the end of the
     * last visible line.
     *
     * @param item StringItem to layout
     * @param isHyperlink If true, StringItem of type Hyperlink is
     *      created. Otherwise creates plain StringItem.
     */
    private void swtLayoutLockedPlainStringItem(StringItem item,
            boolean isHyperlink)
    {

        int width = item.getPreferredWidth();
        int height = item.getPreferredHeight();

        Vector strings = StringWrapper.wrapString(item.getText(),
                         item.getFont(), formLayouter.getForm().getLeftRightLanguage(),
                         width, width);

        // Create composite which will contain the lines of locked stringitem:
        Composite comp = new Composite(formComposite, SWT.NONE);
        comp.setSize(width, height);

        if(strings != null)
        {
            // layout labels in vertical rows
            comp.setLayout(new RowLayout(SWT.VERTICAL));

            // Calculate line height:
            String label = item.getLabel();
            int lineHeight = StringItemLayouter.calculateMinimumBounds(item).y;

            // Calculate the index of last visible line:
            int spaceForRows = height;
            if(item.hasLabel())
            {
                spaceForRows -= getLabelSize(LINE_CUT_INDICATOR).y;
            }
            int indexOfLastVisibleRow =
                Math.max(spaceForRows / lineHeight - 1, 0);

            boolean lastLineReached = false;
            for(int i = 0; !lastLineReached && i < strings.size(); i++)
            {
                String line = (String) strings.elementAt(i);

                // If there are rows left after this row but no more space ...
                if((i < strings.size() - 1) && (i == indexOfLastVisibleRow))
                {
                    // ... display three dots at the end of the line:
                    if(line.length() >= LINE_CUT_INDICATOR.length())
                    {
                        line = line.substring(0, line.length()
                                              - LINE_CUT_INDICATOR.length())
                               + LINE_CUT_INDICATOR;
                    }
                    else
                    {
                        line = LINE_CUT_INDICATOR;
                    }
                    lastLineReached = true;
                }

                // create primitive StringItem
                swtCreateLabeledPrimitiveStringItem(comp, line, label, item
                                                     .getFont(), isHyperlink,width);

                // because add label to first primitive only:
                label = null;
            }
        }
		LayoutObject lo = formLayouter.getLayoutObject(item);
        formLayouter.swtAddNewLayoutObject((lo == null ? new LayoutObject(item, comp) : lo), false);
    }

    /**
     * Create labeled 'primitive' one-row StringItems.
     *
     * @param parent parent used to layout eSWT control.
     * @param txt text for that primitive StringItem.
     * @param label header of StringItem.
     * @param font Item's font.
     * @param hyperlink If true, StringItem of type Hyperlink is created.
     *            Otherwise creates plain StringItem.
     * @return Control created.
     */
    private Control swtCreateLabeledPrimitiveStringItem(Composite parent,
            String txt, String label, Font font, boolean hyperlink,
            int availableWidth)
    {
        if(label == null || label.length()<1)
        {
            Control text = swtCreatePrimitiveStringItem(parent, txt, font, hyperlink);
            if(hyperlink)
            {
                ((Link) text).addSelectionListener(hLinkListener);
            }
            return text;
        }
        else
        {
            Composite comp = new Composite(parent, SWT.NONE);

            Label header = new Label(comp, SWT.WRAP);
            header.setText(label);
            header.pack();

            int headerWidth = header.getBounds().width;
            if(headerWidth > availableWidth)
            {
                Point size = header.computeSize(availableWidth,SWT.DEFAULT);
                header.setBounds(0,0,size.x,size.y);
                headerWidth = header.getBounds().width;
            }

            Control text = swtCreatePrimitiveStringItem(comp, txt, font,
                           hyperlink);
            if(hyperlink)
            {
                ((Link) text).addSelectionListener(hLinkListener);
            }
            int textWidth = text.getBounds().width;
            int objectWidth = Math.max(textWidth, headerWidth);

            header.setLocation(ItemLayouter.getXLocation(objectWidth,
                               headerWidth, formLayouter.getLanguageSpecificLayoutDirective()), 0);

            text.setLocation(ItemLayouter.getXLocation(objectWidth, textWidth,
                             formLayouter.getLanguageSpecificLayoutDirective()),
                             header.getBounds().height);

            comp.pack();
            return comp;
        }
    }

    /**
     * Create primitive StringItem.
     *
     * @param parent
     * @param txt
     * @param font
     * @param isHyperlink
     * @return
     */
    private static Control swtCreatePrimitiveStringItem(Composite parent, String txt,
            Font font, boolean isHyperlink)
    {
        Control text = null;
        if(isHyperlink)
        {
            text = new Link(parent, SWT.NONE);
            ((Link) text).setText(txt);
        }
        else
        {
            text = new Label(parent, SWT.NONE);
            ((Label) text).setText(txt);
        }
        text.setFont(Font.getSWTFont(font));
        text.pack();
        return text;
    }

    /**
     * Returns the minimum area needed to display a StringItem with specified
     * font.
     *
     * @param stringItem StringItem object
     * @return Minimum area needed to display StringItem with specified Font.
     */
    static Point calculateMinimumBounds(final StringItem stringItem)
    {
        final Point minSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Point size = null;
                if(stringItem.getNumCommands() > 0)
                {
                    if(stringItem.getAppearanceMode() == StringItem.BUTTON)
                    {
                        // BUTTON:
                        Button but = new Button(swtGetStaticShell(), SWT.PUSH);
                        but.setFont(Font.getSWTFont(stringItem.getFont()));
                        but.setText(LINE_CUT_INDICATOR);
                        but.pack();
                        size = but.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
                        but.dispose();
                        applyMinMargins(stringItem, size);
                    }
                    else
                    {
                        // HYPERLINK:
                        Link hl = new Link(swtGetStaticShell(),
                                                     SWT.NONE);
                        hl.setFont(Font.getSWTFont(stringItem.getFont()));
                        hl.setText(LINE_CUT_INDICATOR);
                        hl.pack();
                        size = hl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        hl.dispose();
                        if(stringItem.hasLabel())
                        {
                            Label header = new Label(swtGetStaticShell(),SWT.NONE);
                            header.setText(LINE_CUT_INDICATOR);
                            header.pack();
                            size.y += (header.computeSize(SWT.DEFAULT,SWT.DEFAULT)).y;
                            header.dispose();
                        }
                    }
                }
                else
                {
                    Label text = new Label(swtGetStaticShell(), SWT.NONE); // SWT.LEFT
                    text.setFont(Font.getSWTFont(stringItem.getFont()));
                    text.setText(LINE_CUT_INDICATOR);
                    text.pack();
                    size = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                    text.dispose();
                    if(stringItem.hasLabel())
                    {
                        Label header = new Label(swtGetStaticShell(),SWT.NONE);
                        header.setText(LINE_CUT_INDICATOR);
                        header.pack();
                        size.y += (header.computeSize(SWT.DEFAULT,SWT.DEFAULT)).y;
                        header.dispose();
                    }
                }
                minSize.x = size.x;
                minSize.y = size.y;
            }
        });
        return minSize;
    }

    /**
     * Returns the preferred area needed to display an Item.
     *
     * @param item Item.
     * @return Preferred size needed to display Item. x is width and y is
     *         height.
     */
    static Point calculatePreferredBounds(Item item)
    {
        StringItem stringItem = (StringItem) item;
        if(stringItem.getNumCommands() > 0)
        {
            if(stringItem.getAppearanceMode() == StringItem.BUTTON)
            {
                // BUTTON
                return calculateButtonPreferredBounds(stringItem);
            }
            else
            {
                // HYPERLINK
                return calculateStringItemPreferredBounds(stringItem, true);
            }
        }
        else
        {
            // PLAIN
            return calculateStringItemPreferredBounds(stringItem, false);
        }
    }

    /**
     * Calculates unlocked preferred size for StringItem which is displayed as
     * button.
     *
     * @param item Item.
     * @return Preferred size.
     */
    private static Point calculateButtonPreferredBounds(final StringItem item)
    {
        final Point prefSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Button button = new Button(swtGetStaticShell(), SWT.PUSH);
                button.setFont(Font.getSWTFont(item.getFont()));

                int areaWidth = 0;
                String line = item.getText();
                if((areaWidth = item.getLockedPreferredWidth()) == -1)
                {
                    areaWidth = getMaximumItemWidth(item);
                }
                line = truncateIfNewLine(line);
                if(item.getFont().stringWidth(line) < areaWidth)
                {
                    areaWidth = SWT.DEFAULT;
                }
                button.setText(line);
                button.pack();
                Point size = button.computeSize(areaWidth, SWT.DEFAULT);
                button.dispose();
                prefSize.x = size.x;
                prefSize.y = size.y;
                applyPrefMargins(item, prefSize);
            }
        });
        return prefSize;
    }

    /**
     * Calculates preferred size for StringItem which is displayed as plain or
     * as hyperlink. If there are many lines in item and item's width is
     * unlocked, then the returned width is form's content area's width. If
     * width is locked, then returned width is same as locked width and height
     * grows so that text fits to item's area. Locked height is handled during
     * layouting.
     *
     * @param item Item.
     * @param isHyperlink True if item is hyperlink, false if plain.
     * @return Preferred size.
     */
    private static Point calculateStringItemPreferredBounds(
        final StringItem item, final boolean isHyperlink)
    {
        final Point preferredSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                // Find out the width of the area where the StringItem should
                // be layouted:
                int areaWidth = 0;
                if(item.getLockedPreferredWidth() != -1)
                {
                    areaWidth = item.getLockedPreferredWidth();
                }
                else
                {
                    areaWidth = getMaximumItemWidth(item);
                }

                // Wrap string to find out how many lines it would take:
                Vector strings = StringWrapper.wrapString(item.getText(),
                                 item.getFont(), true, areaWidth, areaWidth);

                if(strings != null && strings.size() > 0)
                {
                    // Calculate the size of the first line:
                    Control ctrl = swtCreatePrimitiveStringItem(
                                       swtGetStaticShell(),
                                       (String) strings.elementAt(0),
                                       item.getFont(),
                                       isHyperlink);
                    Point size = ctrl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                    ctrl.dispose();

                    // If there are more than one line, then the width would
                    // be form's content area width or locked width and the
                    // height would be the height of a line times number of
                    // lines:
                    if(strings.size() > 1)
                    {
                        size.x = areaWidth;
                        size.y *= strings.size();
                    }
                    if(item.hasLabel())
                    {
                        Label header = new Label(swtGetStaticShell(),SWT.NONE);
                        header.setText(item.getLabel());
                        header.pack();
                        size.y += (header.computeSize(areaWidth,SWT.DEFAULT)).y;
                        header.dispose();
                    }
                    preferredSize.x = size.x;
                    preferredSize.y = size.y;
                }
            }
        });
        return preferredSize;
    }

    /**
     * Returns if this Item is hyperlink.
     */
    private boolean isHyperlink(Item item)
    {
        if(item.getNumCommands() > 0
                && item instanceof StringItem
                && (((StringItem) item).getAppearanceMode() != Item.BUTTON))
        {
            return true;
        }
        return false;
    }

    /**
     * Called when item gains focus.
     */
    void swtFocusGained(Item item, int dir)
    {
        super.swtFocusGained(item, dir);
        if(isHyperlink(item))
        {
            // Highlight hyperlink:
            swtHighlightHyperlinkItem(item, true);
        }
    }

    /**
     * Called when item losts focus.
     */
    void swtFocusLost(Item item)
    {
        super.swtFocusLost(item);
        if(isHyperlink(item))
        {
            // Remove hyperlink highlighting:
            swtHighlightHyperlinkItem(item, false);
        }
    }

    /**
     * Set highlight on hyperlink.
     *
     * @param item highlight StringItem
     * @param highlight enable or disable
     */
    private void swtHighlightHyperlinkItem(Item item, boolean highlight)
    {
        LayoutObject lo = null;
        Control c = null;
        while((lo = formLayouter.getNextLayoutObjectOfItem(lo, item)) != null)
        {
            if((c = lo.getControl()) != null)
            {
                swtHighlightHyperlink(c, highlight);
            }
        }
    }

    private static Color fgColor;
    private static Color bgColor;

    private static Color getFgColor()
    {
        if(fgColor == null)
        {
            fgColor = new Color(EmulatorImpl.getDisplay(),
                                0x99, 0x00, 0x00);
        }
        return fgColor;
    }

    private static Color getBgColor()
    {
        if(bgColor == null)
        {
            bgColor = new Color(EmulatorImpl.getDisplay(),
                                0xee, 0xee, 0xee);
        }
        return bgColor;
    }

    /**
     * Highlights hyperlink. this should be called when hyperlink receives
     * focus. Method adds highlighting to provided Control and all of its
     * children.
     *
     * @param c Control where to start highlighting.
     */
    private void swtHighlightHyperlink(Control c, boolean highlight)
    {
        // TODO: eSWT support required - hyperlink highlighting.
        if(c instanceof Composite)
        {
            Control[] children = ((Composite) c).getChildren();
            for(int i = 0; i < children.length; i++)
            {
                Control child = children[i];
                if(child instanceof Composite)
                {
                    swtHighlightHyperlink(child, highlight);
                }
            }
        }
        if(highlight)
        {
            c.setBackground(getBgColor());
            c.setForeground(getFgColor());
        }
        else
        {
            c.setBackground(null);
            c.setForeground(null);
        }
    }

    /**
     * Checks if there's new line character(s) in string. If one or more is
     * found, truncates string so that the characters beginning from first
     * newline character are replaced with three dots. Supported newline
     * characters are:
     * <ul>
     * <li>CRLF (\r\n)
     * <li>LF (\n)
     * <li>CR (\r)
     * <li>LS (\u2028)
     * </ul>
     *
     * @param text String to be checked.
     * @return truncated String if newline characters found. Otherwise returns
     *         the string provided as parameter.
     */
    private static String truncateIfNewLine(String text)
    {
        int indexOfFirstNewLineChar = -1;
        for(int i = 0; i < text.length(); i++)
        {
            if((text.charAt(i) == '\r')
                    || (text.charAt(i) == '\n')
                    || (text.charAt(i) == '\u2028'))
            {
                indexOfFirstNewLineChar = i;
                break;
            }
        }
        if(indexOfFirstNewLineChar != -1)
        {
            return text.substring(0, indexOfFirstNewLineChar)
                   + LINE_CUT_INDICATOR;
        }
        return text;
    }

    /**
     * Filter that blocks events if current selected item is a StringItem of
     * type HYPERLINK (or plain with commands).
     */
    class HyperLinkListener implements SelectionListener
    {

        public void widgetDefaultSelected(SelectionEvent e)
        {
            Logger.method(this, "widgetDefaultSelected");
        }

        public void widgetSelected(SelectionEvent e)
        {
            Logger.method(this, "widgetSelected");
            e.doit = false;
            Item item = formLayouter.getCurrentSelectedItem();
            item.callCommandAction(item.getMSKCommand());
        }
    }

}
