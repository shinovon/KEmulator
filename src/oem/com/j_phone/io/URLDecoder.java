package com.j_phone.io;

public class URLDecoder {

	public static String decode(String in) {
		if (in == null) throw new NullPointerException();
		return java.net.URLDecoder.decode(in);
	}
}
