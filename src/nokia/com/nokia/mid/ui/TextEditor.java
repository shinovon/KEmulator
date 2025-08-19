package com.nokia.mid.ui;

import emulator.Emulator;
import emulator.lcdui.TextUtils;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class TextEditor extends CanvasItem {
	private Font iFont;
	private int iMaxSize;
	private int iConstraints;
	private String iCharacterSubset;
	private int iRows;
	private boolean iMultiline;
	private TextEditorListener iListener;
	private String iContent;
	private int iBgColor;
	private int iFgColor;
	private int iFocusBgColor;
	private int iFocusFgColor;
	private boolean iFocusBgColorSet;
	private boolean iFocusFgColorSet;
	private boolean iFocused;
	private int iContentHeight;
	private boolean updateFocus;
	private String[] textArr;
	private int caretX, caretY;

	public static TextEditor createTextEditor(
			String text,
			int maxSize,
			int constraints,
			int width,
			int height)
	{
		return new TextEditor(text, maxSize, constraints, width, height, false);
	}

	public static TextEditor createTextEditor(
			int maxSize,
			int constraints,
			int width,
			int rows)
	{
		return new TextEditor("", maxSize, constraints, width, rows, true);
	}

	public void setFocus(boolean focused) {
		if (iFocused == focused || (focused && !iVisible)) return;
		if (!focused) {
			Emulator.getEmulator().getScreen().getCaret().defocusItem(this);
		} else {
			Emulator.getEmulator().getScreen().getCaret().focusItem(this, this.caretX, this.caretY);
		}
		iFocused = focused;
		updateFocus = true;
	}

	public boolean hasFocus() {
		return iFocused;
	}

	public void setParent(java.lang.Object parent) {
		if (((parent != null) && !(parent instanceof Canvas)) ||
				((parent != null) && (iParent != null) && (iParent != parent))) {
			throw new IllegalArgumentException(ERROR_NOT_A_VALID_PARENT_OBJECT);
		}
		if (iParent != null) {
			((Canvas) iParent)._removeNokiaCanvasItem(this);
		}
		iParent = parent;
		if (parent != null) {
			((Canvas) parent)._addNokiaCanvasItem(this);
		} else {
			setFocus(false);
			iVisible = false;
		}
	}

	public void setSize(int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException(ERROR_GIVEN_ARGUMENTS_NOT_VALID);
		}
		iWidth = width;
		iHeight = height;
		updateFocus = true;
		layout();
		repaint();
	}

	public void setPosition(int x, int y) {
		iPositionX = x;
		iPositionY = y;
		updateFocus = true;
		repaint();
	}

	public void setSize(int x, int y, int width, int height) {
		setPosition(x, y);
		setSize(width, height);
	}

	public void setVisible(boolean visible) {
		checkParent();
		if (visible == iVisible) return;
		if (!visible) setFocus(false);
		iVisible = visible;
		repaint();
	}

	public void setZPosition(int z) {
		if (z < 0) {
			throw new IllegalArgumentException();
		}
	}

	public int getZPosition() {
		return 0;
	}

	public int getLineMarginHeight() {
		return 0;
	}

	public int getContentHeight() {
		return iContentHeight;
	}

	public void setCaret(int index) {
		if (iFocused) {
			Emulator.getEmulator().getScreen().getCaret().setCaret(index);
		}
	}

	public int getCaretPosition() {
		if (iFocused) {
			return Emulator.getEmulator().getScreen().getCaret().getCaretPosition();
		}
		return 0;
	}

	public int getVisibleContentPosition() {
		return 0;
	}

	public Font getFont() {
		return iFont == null ? Font.getDefaultFont() : iFont;
	}

	public void setFont(Font font) {
		if (font == null) {
			font = Font.getDefaultFont();
		}
		iFont = font;
		updateFocus = true;
	}

	public int getBackgroundColor() {
		return iBgColor;
	}

	public int getForegroundColor() {
		return iFgColor;
	}

	public void setBackgroundColor(int color) {
		iBgColor = color;
	}

	public void setForegroundColor(int color) {
		iFgColor = color;
	}

	public void setHighlightBackgroundColor(int color) {
		iFocusBgColor = color;
		iFocusBgColorSet = true;
	}

	public void setHighlightForegroundColor(int color) {
		iFocusFgColor = color;
		iFocusFgColorSet = true;
	}

	public void setContent(String content) {
		if (content == null || content.length() > iMaxSize) {
			throw new IllegalArgumentException("content");
		}
		iContent = content;

		if (iVisible)
			Emulator.getEmulator().getScreen().getCaret().updateText(this, iContent);
		
		layout();
		repaint();
	}

	public String getContent() {
		return iContent;
	}

	public void insert(String text, int position) {
		if (text.length() + size() > iMaxSize) {
			throw new IllegalArgumentException(ERROR_GIVEN_ARGUMENTS_NOT_VALID);
		}
		setContent(iContent.substring(0, position) + text + iContent.substring(position));
	}

	public void delete(int offset, int length) {
		setContent(iContent.substring(0, offset) + iContent.substring(offset + length));
	}

	public int getMaxSize() {
		return iMaxSize;
	}

	public int setMaxSize(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException(ERROR_GIVEN_ARGUMENTS_NOT_VALID);
		}
		if (iContent != null && iContent.length() > maxSize) {
			setContent(iContent.substring(0, maxSize - 1));
		}
		return iMaxSize = maxSize;
	}

	public int size() {
		return iContent.length();
	}

	public void setConstraints(int constraints) {
		iConstraints = constraints;
	}

	public int getConstraints() {
		return iConstraints;
	}

	public void setInitialInputMode(String characterSubset) {
		iCharacterSubset = characterSubset;
	}

	public String getInitialInputMode() {
		return iCharacterSubset;
	}

	public void setSelection(int index, int length) {
		if (iFocused) {
			Emulator.getEmulator().getScreen().getCaret().setSelection(index, length);
		}
	}

	public String getSelection() {
		if (iFocused) {
			return Emulator.getEmulator().getScreen().getCaret().getSelection();
		}
		return null;
	}

	public void setTextEditorListener(TextEditorListener listener) {
		iListener = listener;
	}

	public boolean isMultiline() {
		return iMultiline;
	}

	public void setMultiline(boolean aMultiline) {
		// TODO
		iMultiline = aMultiline;
	}

	public int getHeight() {
		if (iHeight == -1) {
			return Math.max(getFont().getHeight() + 4, iContentHeight);
		}
		return iHeight;
	}

	public int getWidth() {
		return iWidth;
	}

	public void setIndicatorVisibility(boolean b) {}

	TextEditor() {}

	TextEditor(String text,
			   int maxSize,
			   int constraints,
			   int width,
			   int height,
			   boolean rows) {
		setMaxSize(maxSize);
		setConstraints(constraints);
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException(ERROR_GIVEN_ARGUMENTS_NOT_VALID);
		}
		iWidth = width;
		setContent(text == null ? "" : text);
		if (rows) {
			setMultiline(height != 1);
			iRows = height;
			iHeight = -1;
		} else {
			iHeight = height;
		}
	}


	public void _invokePaint(Graphics g) {
		int w = getWidth(),
				h = getHeight(),
				x = getPositionX(),
				y = getPositionY();
		Font font = getFont();
		g.setFont(font);

		g._setColor(iFocused && iFocusBgColorSet ? iFocusBgColor : iBgColor);
		g.fillRect(x, y, w, h);
		if ((caretX != x || caretY != y || updateFocus) && iFocused) {
			updateFocus = false;
			caretX = x;
			caretY = y;
			Emulator.getEmulator().getScreen().getCaret().focusItem(this, this.caretX, this.caretY);
		}
		if (iContent.isEmpty() && !iFocused) {
			g.setColor(0xaaaaaa);
			g.drawString("Tap", x + 4, y + 2, 0);
			return;
		}
		g._setColor(iFocused && iFocusFgColorSet ? iFocusFgColor : iFgColor);

		if (!iMultiline) {
			g.drawString(textArr[0], x + 4, y + 2, 0);
			return;
		}
		int th = font.getHeight() + 4;
		int l = 0;
		while(th * textArr.length > h + (th * l)) {
			l++;
		}
		for (int j = l; j < textArr.length; ++j) {
			g.drawString(textArr[j], x + 4, y + 2, 0);
			y += th;
		}
	}

	public void _contentChanged(String s) {
		iContent = s;

		layout();
		repaint();
		if (iListener != null) {
			iListener.inputAction(this, TextEditorListener.ACTION_CONTENT_CHANGE);
		}
	}

	public void _inputAction(int a) {
		if (iListener != null) {
			iListener.inputAction(this, a);
		}
	}

	private void layout() {
		int availableWidth = getWidth() - 8;
		Font font = getFont();
		textArr = TextUtils.textArr(iContent, font, availableWidth, availableWidth);
		iContentHeight = 4 + (font.getHeight() + 4) * textArr.length;
	}

	private void repaint() {
		if (iParent != null) {
			((Canvas) iParent).repaint();
		}
	}
}
