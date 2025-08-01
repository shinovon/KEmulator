package com.nttdocomo.ui;

public final class Dialog extends Frame {
	public static final int DIALOG_INFO = 0;
	public static final int DIALOG_WARNING = 1;
	public static final int DIALOG_ERROR = 2;
	public static final int DIALOG_YESNO = 3;
	public static final int DIALOG_YESNOCANCEL = 4;
	public static final int BUTTON_OK = 1;
	public static final int BUTTON_CANCEL = 2;
	public static final int BUTTON_YES = 4;
	public static final int BUTTON_NO = 8;

	public Dialog(final int n, final String s) {
	}

	public void setBackground(final int n) {
	}

	public void setText(final String s) {
	}

	public int show() {
		return 0;
	}

	public void setSoftLabel(final int n, final String s) {
	}

	public void setFont(final Font font) {
	}
}
