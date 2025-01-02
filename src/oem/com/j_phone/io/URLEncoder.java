package com.j_phone.io;

public class URLEncoder {

	public static String encode(String in) {
		if (in == null) throw new NullPointerException();
		return java.net.URLEncoder.encode(in);

	}
}
