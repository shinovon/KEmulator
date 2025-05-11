package com.nttdocomo.ui.maker;

import com.nttdocomo.io.maker.*;
import emulator.*;
import com.nttdocomo.io.*;
import emulator.custom.CustomJarResources;

import java.io.*;

public abstract class MediaImpl {
	protected String location;
	protected InputStream in;
	protected byte[] data;
	protected Object resource;

	MediaImpl(final String location) {
		if (location == null) {
			throw new NullPointerException();
		}
		this.location = location;
	}

	MediaImpl(final InputStream in) {
		if (in == null) {
			throw new NullPointerException();
		}
		this.in = in;
	}

	MediaImpl(final byte[] array) {
		if (array == null) {
			throw new NullPointerException();
		}
		System.arraycopy(array, 0, this.data = new byte[array.length], 0, array.length);
	}

	protected abstract Object loadResource(final InputStream p0) throws IOException;

	protected abstract Object loadResource(final byte[] p0) throws IOException;

	protected void useResource() throws ConnectionException {
		if (this.resource == null) {
			try {
				if (this.location != null) {
					if (this.location.startsWith("scratchpad:")) {
						final ScratchPadConnection open = ScratchPadConnection.open(this.location);
						final DataInputStream openDataInputStream = open.openDataInputStream();
						if (open.getAccessLength() > 0) {
							final byte[] array = new byte[open.getAccessLength()];
							openDataInputStream.readFully(array);
							this.resource = this.loadResource(array);
						} else {
							this.resource = this.loadResource(openDataInputStream);
						}
						openDataInputStream.close();
						open.close();
					} else if (this.location.startsWith("resource:")) {
						final InputStream resourceAsStream = CustomJarResources.getResourceAsStream(this.location.substring(11));
						this.resource = this.loadResource(resourceAsStream);
						resourceAsStream.close();
					}
				} else if (this.in != null) {
					this.resource = this.loadResource(this.in);
				} else {
					this.resource = this.loadResource(this.data);
				}
			} catch (IOException ex) {
				throw new ConnectionException(2);
			}
		}
	}
}
