package com.jblend.net;

import java.io.UnsupportedEncodingException;

public class HttpUrlEncoder {
	public static String encode(String in) {
		if (in == null) throw new NullPointerException();
		try {
			return java.net.URLEncoder.encode(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return java.net.URLEncoder.encode(in);
		}
	}
}
