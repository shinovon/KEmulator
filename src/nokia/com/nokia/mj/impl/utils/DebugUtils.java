package com.nokia.mj.impl.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class DebugUtils {

	public static String getStackTrace(Throwable t) {
		String res = null;
		if (t != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				t.printStackTrace(new PrintStream(baos));
				res = baos.toString();
			} catch (Throwable t2) {
				System.err.println("Failure in getting stack trace.");
				t2.printStackTrace();
			}
		}
		return res;
	}

	public static void closeThreadDumper() {
	}

	public static void doThreadDump() {
		Thread.dumpStack();
	}
}
