package com.nttdocomo.ui;

public class UIException extends RuntimeException {
	public static final int UNDEFINED = 0;
	public static final int ILLEGAL_STATE = 1;
	public static final int NO_RESOURCES = 2;
	public static final int BUSY_RESOURCE = 3;
	public static final int UNSUPPORTED_FORMAT = 4;
	protected static final int STATUS_FIRST = 0;
	protected static final int STATUS_LAST = 63;
	private int status = 63;

	public UIException() {
		this(0);
	}

	public UIException(final int n) {
		this(n, "");
	}

	public UIException(final int n, final String s) {
		super(s);
		this.status = n;
	}

	public int getStatus() {
		return status;
	}
}
