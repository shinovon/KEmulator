package com.nttdocomo.io.maker;

import javax.microedition.io.*;
import java.io.*;

import emulator.*;
import emulator.custom.ResourceManager;

import java.util.*;

public class ScratchPadConnection implements StreamConnection {
	RandomAccessFile a;
	String b;
	int c;
	int d;

	public ScratchPadConnection(final String b, final int c, final int d) throws IOException {
		this.b = b;
		this.c = c;
		this.d = d;

	}

	public void close() throws IOException {
		if (a != null) this.a.close();
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	public InputStream openInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ResourceManager.getBytes((InputStream) new BufferedInputStream(new FileInputStream(this.b))));
		byteArrayInputStream.skip(64 + this.c);
		return (InputStream) new ScratchpadInputStream((InputStream) byteArrayInputStream);
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(this.openOutputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		if (a == null) {
			this.a = new RandomAccessFile(b, "rw");
			if (this.a.length() == 0L) {
				this.a.setLength(Integer.parseInt(Emulator.getEmulator().getAppProperty("SPsize")));
			}
		}
		this.a.seek(64 + this.c);
		return (OutputStream) new ScratchpadOutputStream((OutputStream) new FileOutputStream(this.a.getFD()));
	}

	public int getAccessLength() {
		return this.d;
	}

	public static ScratchPadConnection open(final String s) throws IOException {
		System.out.print("ScratchPadConnection: " + s + " => ");
		int n = s.indexOf(";");
		if (n == -1) {
			n = s.length();
		}
		final int int1 = Integer.parseInt(s.substring("scratchpad:///".length(), n));
		final String s2 = (n < s.length()) ? s.substring(n + 1) : "";
		final Hashtable<String, String> hashtable = new Hashtable<String, String>();
		final StringTokenizer stringTokenizer = new StringTokenizer(s2, ",");
		while (stringTokenizer.hasMoreTokens()) {
			final String nextToken = stringTokenizer.nextToken();
			final int index = nextToken.indexOf(61);
			hashtable.put(nextToken.substring(0, index).trim(), nextToken.substring(index + 1).trim());
		}
		final String midletJar = Emulator.midletJar;
		String string = midletJar.substring(0, midletJar.length() - 4);
		if (int1 != 0) {
			string += int1;
		}
		string += ".sp";
		String s3 = hashtable.get("pos");
		if (s3 == null) {
			s3 = "0";
		}
		String s4 = hashtable.get("length");
		if (s4 == null) {
			s4 = "0";
		}
		System.out.println(string);
		return new ScratchPadConnection(string, Integer.parseInt(s3), Integer.parseInt(s4));
	}
}
