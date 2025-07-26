package com.nttdocomo.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class URLEncoder {
	public static String encode(String paramString) {
		StringBuffer localStringBuffer = new StringBuffer(paramString.length());
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(10);
		OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localByteArrayOutputStream);
		for (int i = 0; i < paramString.length(); i++) {
			int j = paramString.charAt(i);
			if (dontNeedEncoding(j)) {
				if (j == 32) {
					j = 43;
				}
				localStringBuffer.append((char) j);
			} else {
				try {
					localOutputStreamWriter.write(j);
					localOutputStreamWriter.flush();
				} catch (IOException localIOException) {
					localByteArrayOutputStream.reset();
					continue;
				}
				byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
				for (int k = 0; k < arrayOfByte.length; k++) {
					localStringBuffer.append('%');
					int m = arrayOfByte[k] >> 4 & 0xF;
					char c;
					if (m < 10) {
						c = (char) (48 + m);
					} else {
						c = (char) (55 + m);
					}
					localStringBuffer.append(c);

					m = arrayOfByte[k] & 0xF;
					if (m < 10) {
						c = (char) (48 + m);
					} else {
						c = (char) (55 + m);
					}
					localStringBuffer.append(c);
				}
				localByteArrayOutputStream.reset();
			}
		}
		return localStringBuffer.toString();
	}

	static boolean dontNeedEncoding(int paramInt) {
		return ((paramInt >= 97) && (paramInt <= 122)) || ((paramInt >= 65) && (paramInt <= 90)) || ((paramInt >= 48) && (paramInt <= 57)) || (paramInt == 32) || (paramInt == 45) || (paramInt == 95) || (paramInt == 46) || (paramInt == 42);
	}
}
