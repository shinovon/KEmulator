/*
Copyright (c) 2025 Arman Jussupgaliyev
*/
package ru.nnproject.symbiangl;

public class GLException extends RuntimeException {
	
	public GLException() {
		super();
	}
	
	public GLException(String message) {
		super(message);
	}
	
	public GLException(String function, String message) {
		super(function + ": " + message);
	}

}
