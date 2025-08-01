package com.nttdocomo.ui.maker;

import emulator.*;
import com.nttdocomo.ui.*;
import com.nttdocomo.io.*;

import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.image.*;

public class MediaImage extends MediaImpl implements com.nttdocomo.ui.MediaImage {
	public MediaImage(final String s) {
		super(s);
	}

	public MediaImage(final InputStream inputStream) {
		super(inputStream);
	}

	public MediaImage(final byte[] array) {
		super(array);
	}

	public Image getImage() {
		return (Image) this.resource;
	}

	public int getWidth() {
		return this.getImage().getWidth();
	}

	public int getHeight() {
		return this.getImage().getHeight();
	}

	public void use() throws ConnectionException, UIException {
		try {
			this.useResource();
		} catch (Exception ex) {
			System.out.println(ex.toString());
			throw new UIException();
		}
	}

	protected byte[] readPNGFromStream(final DataInputStream dataInputStream) throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		dataOutputStream.writeInt(dataInputStream.readInt());
		dataOutputStream.writeInt(dataInputStream.readInt());
		int i = 0;
		while (i == 0) {
			final int int1 = dataInputStream.readInt();
			final int int2 = dataInputStream.readInt();
			dataOutputStream.writeInt(int1);
			dataOutputStream.writeInt(int2);
			if (int2 == 1229278788) {
				i = 1;
			}
			for (int j = 0; j < int1; ++j) {
				dataOutputStream.writeByte(dataInputStream.readByte());
			}
			dataOutputStream.writeInt(dataInputStream.readInt());
		}
		return byteArrayOutputStream.toByteArray();
	}

	protected Object loadResource(final InputStream inputStream) throws IOException {
		Object o = null;
		if (inputStream.markSupported()) {
			inputStream.mark(4);
			final byte[] array = new byte[4];
			final DataInputStream dataInputStream = new DataInputStream(inputStream);
			dataInputStream.readFully(array);
			if (new String(array).equals("\u2030PNG")) {
				dataInputStream.reset();
				o = this.loadResource(this.readPNGFromStream(dataInputStream));
			} else {
				inputStream.reset();
			}
		}
		if (o == null) {
			o = new ImageImpl(javax.microedition.lcdui.Image.createImage(inputStream));
		}
		return o;
	}

	protected Object loadResource(final byte[] array) throws IOException {
		return new ImageImpl(javax.microedition.lcdui.Image.createImage(array, 0, array.length));
	}

	public void unuse() {
	}

	public void dispose() {
		this.resource = null;
	}
}
