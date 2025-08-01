package com.nttdocomo.net;

import java.io.ByteArrayOutputStream;

public class URLDecoder {
	public static String decode(String paramString) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		for (int i = 0; i < paramString.length(); i++) {
			int j = paramString.charAt(i);
			switch (j) {
				case 43:
					localByteArrayOutputStream.write(32);
					break;
				case 37:
					try {
						localByteArrayOutputStream.write(Integer.parseInt(paramString.substring(i + 1, i + 3), 16));
					} catch (NumberFormatException localNumberFormatException) {
						throw new IllegalArgumentException("String is not x-www-form-urlencoded");
					}
					i += 2;
					break;
				default:
					if ((j >= 32) && (j <= 126)) {
						localByteArrayOutputStream.write(j);
					} else {
						throw new IllegalArgumentException("String is not x-www-form-urlencoded");
					}
					break;
			}
		}
		return new String(localByteArrayOutputStream.toByteArray());
	}
}
