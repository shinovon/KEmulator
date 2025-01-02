package com.jblend.micro.lcdui;

import javax.microedition.lcdui.TextField;

public class LocalizedTextField extends TextField {

	public static final int INTERNET = 0;
	public static final int HANKAKU = 0;

	public LocalizedTextField(String label, String text, int maxSize, int constraints) {
		super(label, text, maxSize, constraints);
	}

	public void setCharConstraints(int charConstraints) {
	}

	public void setInputMode(int mode) {
	}

}
