package com.nttdocomo.ui.maker;

import com.nttdocomo.io.*;
import com.nttdocomo.ui.*;

import java.io.*;

public class MediaSound extends MediaImpl implements com.nttdocomo.ui.MediaSound {
	public MediaSound(final String s) {
		super(s);
	}

	public MediaSound(final InputStream inputStream) {
		super(inputStream);
	}

	public MediaSound(final byte[] array) {
		super(array);
	}

	public void use() throws ConnectionException, UIException {
		this.useResource();
	}

	protected Object loadResource(final InputStream inputStream) throws IOException {
		return new Object();
	}

	protected Object loadResource(final byte[] array) throws IOException {
		return new Object();
	}

	public void unuse() {
	}

	public void dispose() {
	}
}
