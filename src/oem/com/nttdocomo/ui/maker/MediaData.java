package com.nttdocomo.ui.maker;

import com.nttdocomo.io.*;
import com.nttdocomo.ui.*;

import java.io.*;

public class MediaData extends MediaImpl implements com.nttdocomo.ui.MediaData {
	public MediaData(final String s) {
		super(s);
	}

	public MediaData(final InputStream inputStream) {
		super(inputStream);
	}

	public MediaData(final byte[] array) {
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
