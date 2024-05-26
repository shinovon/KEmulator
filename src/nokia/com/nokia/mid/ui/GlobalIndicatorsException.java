package com.nokia.mid.ui;

public class GlobalIndicatorsException extends Exception {
	private int errorCode;

	protected GlobalIndicatorsException() {
	}

	public GlobalIndicatorsException(String info) {
		super(info);
	}

	public GlobalIndicatorsException(String info, int errorCode) {
		super(info);
		this.errorCode = errorCode;
	}

	public String toString() {
		if (this.errorCode == 0) {
			return super.toString();
		}
		return super.toString() + " Native error: " + this.errorCode;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}
