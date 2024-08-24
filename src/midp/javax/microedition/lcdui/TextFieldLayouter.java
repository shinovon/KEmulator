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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Responsible for correct layout of TextField in a Form.
 */
class TextFieldLayouter extends ItemLayouter
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

    /**
     * Constructor.
     *
     * @param aFormLayouter FormLayouter used for layouting.
     */
    TextFieldLayouter(FormLayouter aFormLayouter)
    {
        super(aFormLayouter);
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

    /**
     * eSWT specific calls to implement getControl.
     *
     * @param parent for the control.
     * @param item TextField item.
     */
    Control swtGetControl(Composite parent, Item item)
    {
        TextField textfield = (TextField) item;
        if (!item.hasLabel()) {
            return swtGetTextControl(parent, textfield);
        }
        Composite c = new Composite(parent, SWT.NONE);
        int w = getMaximumItemWidth(item);
        Label header = new Label(c, SWT.WRAP);
        header.setText(item.label);
        header.pack();
        Control t = swtGetTextControl(parent, textfield);
        t.pack();
        t.setSize(w, t.getSize().y);
        t.setLocation(0, header.getBounds().height);
        c.pack();
        c.setSize(w, c.getSize().y);
        System.out.println(t.getSize() + " " + c.getSize() + " " + header.getSize());
        return t;
    }

    Control swtGetTextControl(Composite parent, TextField textfield) {
        Control te = TextWrapper.swtConstructText(parent,
                SWT.WRAP | SWT.MULTI | SWT.BORDER, textfield.getConstraints());
        TextWrapper.swtSetMaxSize(te, textfield.getMaxSize());
        TextWrapper.swtSetContent(te, textfield.getString());
        TextWrapper.swtSetSelection(te,
                textfield.getCaretPosition(), textfield.getCaretPosition());

        if(textfield.getInitialInputMode() != null)
        {
            swtUpdateItem(textfield, te, TextField.UPDATE_INITIALINPUTMODE,
                    null);
        }
        return te;
    }

    /**
     * Returns true if this eSWT control is suitable to be used for updating.
     *
     * @param item Item.
     * @param control eSWT control.
     *
     * @return true if this control is suitable for update
     */
    boolean swtIsSpecificControl(Item item, Control control)
    {
        return (control instanceof Text);
    }

    /**
     * Updates the values of TextField.
     *
     * @param item Item.
     * @param control eSWT control.
     * @param reason reason to update.
     */
    void swtUpdateItem(Item item, Control control, int reason, Object param)
    {
        TextField textfield = (TextField) item;
        if(reason == TextField.UPDATE_INITIALINPUTMODE)
        {
            TextWrapper.swtSetInputMode(control,
                                         textfield.getInitialInputMode(),
                                         textfield.getConstraints());
        }
        else
        {
            TextWrapper.swtSetContent(control, textfield.getString());
        }

//        if (reason == Item.UPDATE_LABEL)
        if (item.hasLabel() && control instanceof Composite) {
            for (Control c: ((Composite) control).getChildren()) {
                if (c instanceof Label) {
                    ((Label) c).setText(item.getLabel());
                    break;
                }
            }
        }
    }

    /**
     * Update size of TextField.
     *
     * @param item TextField.
     * @param control Control which represents TextField.
     * @param width which control must occupy.
     */
    void swtResizeControl(Item item, Control control, int width, int height)
    {
        super.swtResizeControl(item, control, width, height);
        if(control instanceof Text)
        {
            Text te = (Text) control;
            ((TextField) item).internalSetLinesCount(te.getLineCount());
        }
        control.setSize(width, control.computeSize(-1, -1).y);
    }

    void swtLayoutItem(Row row, Item item) {
        LayoutObject lo = getLayoutObject(item);
        formLayouter.swtAddNewLayoutObject(lo);
        if (item.hasLabel()) {
            Control p = lo.getControl();
            int w = getMaximumItemWidth(item);
            p.setSize(w, p.getSize().y);
            if (p instanceof Composite) {
                for (Control c: ((Composite) p).getChildren()) {
                    if (c instanceof Label) {
                        ((Label) c).setText(item.getLabel());
                    }
                    c.setSize(w, c.getSize().y);
                }
            }
        }
    }

    /**
     * Returns true if that key was consumed by TextField.
     *
     * @param item TextField.
     * @param key keyCode.
     */
    boolean swtOfferKeyPressed(Item item, int key)
    {
        TextField tf = (TextField) item;
        if(item.hasLayout(Item.LAYOUT_SHRINK))
        {
            if((key == SWT.ARROW_LEFT
                    && tf.getCaretPosition() == 0)
                    || (key == SWT.ARROW_RIGHT
                        && tf.getCaretPosition() == tf.size()))
            {
                return false;
            }
        }
        if(((key == SWT.ARROW_UP)
                && (tf.getCaretPosition() == 0))
                || ((key == SWT.ARROW_DOWN)
                    && (tf.getCaretPosition() == tf.size())))
        {
            return false;
        }
        return true;
    }

    /**
     * Responsible for reacting on focusGained event, and according to direction
     * from which that event comes sets the caret of the TextField.
     *
     * @param item TextField.
     * @param dir direction from which focus came, in case if it was set with
     *            setCurrentItem() default direction is used (-1).
     */
    void swtFocusGained(Item item, int dir)
    {
        super.swtFocusGained(item, dir);
        TextField tf = (TextField) item;
        // direction = dir;
        resetCaretPosition(tf, dir);
        Control control = swtGetFirstSpecificControl(item);
        TextWrapper.swtSetSelection(control,
                                     tf.getCaretPosition(), tf.getCaretPosition());
    }

    /**
     * Returns the minimum area needed to display a TextField.
     *
     * @param textField TextField object
     * @return Minimum area needed to display TextField.
     */
    static Point calculateMinimumBounds(final TextField textField)
    {
        final Point minSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Text tempExt = (Text) swtGetStaticTextControl(TextField.ANY);
                tempExt.setText(ItemLayouter.MIN_TEXT);
                tempExt.pack();
                minSize.x = tempExt.getSize().x;
                minSize.y = tempExt.getSize().y + Config.TEXTFIELD_MARGIN;
                applyMinMargins(textField, minSize);
            }
        });
        return minSize;
    }

    /**
     * Returns the preferred area needed to display an Item.
     *
     * @param item Item.
     * @return Preferred area needed to display Item. x is width and y is
     *         height.
     */
    static Point calculatePreferredBounds(Item item)
    {
        final TextField textfield = (TextField) item;
        final Point prefSize = new Point(0, 0);
        Displayable.syncExec(new Runnable()
        {
            public void run()
            {
                Text te = (Text) swtGetStaticTextControl(TextField.ANY);
                te.setText(textfield.getString());

                int maxHeight = (formHeigh
                                 * Config.TEXTFIELD_MAX_SCREEN_PERCENTAGE / TOTAL_PERCENTAGE)
                                - Config.TEXTFIELD_MARGIN;
                textfield.internalSetMaxVisibleLines(maxHeight
                                                     / te.getLineHeight());

                prefSize.x = getMaximumItemWidth(textfield);
                prefSize.y = Config.TEXTFIELD_MARGIN + Math.min(
                                 te.computeSize(prefSize.x, SWT.DEFAULT).y, maxHeight);
                // prefSize.y = Math.min(calc.y, maxHeight) + MARGIN;
                applyPrefMargins(textfield, prefSize);
            }
        });
        return prefSize;
    }

    /**
     * Update caret position based on direction.
     *
     * @param textfield TextField for which to update caret position.
     * @param dir direction of scrolling.
     */
    private void resetCaretPosition(TextField textfield, int dir)
    {
        switch(dir)
        {
        case SWT.ARROW_DOWN:
            textfield.internalSetCaretPosition(0);
            break;
        case SWT.ARROW_RIGHT:
            textfield.internalSetCaretPosition(0);
            break;
        case SWT.ARROW_UP:
            textfield.internalSetCaretPosition(textfield.size());
            break;
        case SWT.ARROW_LEFT:
            textfield.internalSetCaretPosition(textfield.size());
            break;
        default:
            break;
        }
    }

    /**
     * Add TextField listeners when form is activated.
     *
     * @param item TextField.
     * @param control Control which represents TextField.
     */
    void swtAddSpecificListeners(Item item, Control control)
    {
        super.swtAddSpecificListeners(item, control);
        TextField textfield = (TextField) item;
        ModifyListener listener = new TextFieldModifyListener(textfield);
        TextWrapper.swtAddModListener(control, listener);
        control.setData(MODIFY_LISTENER, listener);
        KeyListener listener2 = new TextFieldKeyListener(textfield);
        control.addKeyListener(listener2);
        control.setData(KEY_LISTENER, listener2);
        MouseListener listener4 = new AllMouseListener(textfield);
        control.addMouseListener(listener4);
        control.setData(MOUSE_LISTENER, listener4);
    }

    /**
     * Remove listeners from a TextField if the form goes to background.
     *
     * @param item TextField.
     * @param control Control which represents TextField.
     */
    void swtRemoveSpecificListeners(Item item, Control control)
    {
        super.swtRemoveSpecificListeners(item, control);
        ModifyListener l1 = (ModifyListener) control.getData(MODIFY_LISTENER);
        if(l1 != null)
        {
            TextWrapper.swtRemoveModListener(control, l1);
            control.setData(MODIFY_LISTENER, null);
        }
        KeyListener l2 = (KeyListener) control.getData(KEY_LISTENER);
        if(l2 != null)
        {
            control.removeKeyListener(l2);
            control.setData(KEY_LISTENER, null);
        }
        MouseListener l4 = (MouseListener) control.getData(MOUSE_LISTENER);
        if(l4 != null)
        {
            control.removeMouseListener(l4);
            control.setData(MOUSE_LISTENER, null);
        }
    }

    /**
     * Class that receives ModifyEvents from Text and updates values of
     * TextField.
     */
    class TextFieldModifyListener implements ModifyListener
    {

        private TextField textfield;

        TextFieldModifyListener(TextField textField)
        {
            this.textfield = textField;
        }

        private void handleLinesChange(Text te)
        {
            int lines = te.getLineCount();
            int visibleLines = te.getSize().y / te.getLineHeight();
            if(lines != textfield.internalGetLinesCount())
            {
                textfield.internalSetLinesCount(lines);
                Control control = swtGetFirstControl(textfield);
                if(control.getSize().y + te.getLineHeight()
                        + Config.TEXTFIELD_MARGIN <= formLayouter.getFormHeight())
                {
                    textfield.updateParent(Item.UPDATE_HEIGHT_CHANGED);
                }
                if(textfield.internalGetLinesCount() > lines)
                {
                    if((te.getTopIndex() + visibleLines) > lines)
                    {
                        te.setTopIndex(Math.max(0, lines - visibleLines));
                    }
                    if(visibleLines > lines)
                    {
                        textfield.updateParent(Item.UPDATE_HEIGHT_CHANGED);
                    }
                }

                te.setTopIndex(Math.max(te.getCaretLineNumber() + 1
                                        - textfield.internalGetMaxVisibleLines(), 0));
            }
        }

        public void modifyText(ModifyEvent modifyEvent)
        {
            Control te = (Control) modifyEvent.widget;
            if(textfield.internalSetString(TextWrapper.swtGetContent(te)))
            {
//                Logger.method(textfield, "modify", modifyEvent);
                textfield.internalSetCaretPosition(
                    TextWrapper.swtGetCaretPosition(te));
                if(te instanceof Text)
                {
                    handleLinesChange((Text) te);
                }
                textfield.notifyStateChanged();
            }
        }
    }

    /**
     * Class that receives KeyEvents from Text and updates
     * caret position for TextField.
     */
    class TextFieldKeyListener implements KeyListener
    {

        private TextField textfield;

        TextFieldKeyListener(TextField textField)
        {
            this.textfield = textField;
        }

        public void keyPressed(KeyEvent keyEvent)
        {
            Control te = (Control) keyEvent.widget;
            int caretPos = TextWrapper.swtGetCaretPosition(te);
            int caretLine = TextWrapper.swtGetCaretLine(te);

            if(keyEvent.keyCode == SWT.ARROW_UP && caretLine == 0)
            {
                caretPos = 0;
                TextWrapper.swtSetSelection(te, caretPos, caretPos);
            }
            else if(keyEvent.keyCode == SWT.ARROW_DOWN
                    && (caretLine == (TextWrapper.swtGetLineCount(te) - 1)))
            {
                caretPos = textfield.size();
                TextWrapper.swtSetSelection(te, caretPos, caretPos);
            }

            textfield.internalSetCaretPosition(caretPos);
        }

        public void keyReleased(KeyEvent keyEvent)
        {
            // this is needed if focus was changed with touch.
            // so ne scrolling was done in DFI.
            if(!formLayouter.isItemFullyVisible(textfield))
            {
                formLayouter.swtScrollToItem(textfield);
            }
            textfield.internalSetCaretPosition(
                TextWrapper.swtGetCaretPosition((Control) keyEvent.widget));
        }

    }

    class AllMouseListener implements MouseListener, MouseMoveListener
    {

        private TextField textfield;
        private boolean isEnabled;

        AllMouseListener(TextField tf)
        {
            textfield = tf;
        }

        public void enable(boolean enabled)
        {
            isEnabled = enabled;
        }

        public void mouseUp(MouseEvent me)
        {
            if(isEnabled)
            {
                //
            }
        }

        public void mouseDown(MouseEvent me)
        {
            textfield.internalSetCaretPosition(
                TextWrapper.swtGetCaretPosition((Control) me.widget));
        }

        public void mouseMove(MouseEvent me)
        {
        }

        public void mouseDoubleClick(MouseEvent me)
        {
        }

    }

}
