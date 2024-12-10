package com.motorola.game;

public class FileFormatNotSupportedException extends RuntimeException {
	public FileFormatNotSupportedException(Exception e) {
		super(e);
	}

	public FileFormatNotSupportedException(String info) {
		super(info);
	}

	public FileFormatNotSupportedException(String info, Exception e) {
		super(info, e);
	}
}
