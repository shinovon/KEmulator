package com.nokia.microedition.media;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.SourceStream;

public class InputStreamSourceStream implements SourceStream {
	protected InputStream iInputStream = null;
	private static final String SEEK_CONTROL = "SeekControl";
	private InputStreamSeekControl iSeekControl;

	public InputStreamSourceStream(InputStream aInputStream) {
		setStream(aInputStream);
		this.iSeekControl = new InputStreamSeekControl(aInputStream);
	}

	public InputStreamSourceStream() {
	}

	public final void setStream(InputStream aInputStream) {
		this.iInputStream = aInputStream;
	}

	public void close() throws IOException {
		this.iInputStream.close();
	}

	public int read(byte[] aBuffer, int aOffset, int aLength) throws IOException {
		return this.iInputStream.read(aBuffer, aOffset, aLength);
	}

	public int getSeekType() {
		if ((this.iInputStream != null) && (this.iInputStream.markSupported() == true)) {
			return 1;
		}
		return 0;
	}

	public int getTransferSize() {
		return 0;
	}

	public long seek(long aWhere) throws IOException {
		return 0L;
	}

	public long tell() {
		return 0L;
	}

	public ContentDescriptor getContentDescriptor() {
		return new ContentDescriptor("");
	}

	public long getContentLength() {
		return 0L;
	}

	public Control[] getControls() {
		Control[] aControls = new Control[1];

		aControls[0] = this.iSeekControl;

		return aControls;
	}

	public Control getControl(String aControlType) {
		if ((aControlType.equals("SeekControl")) && (getSeekType() == 1)) {
			return this.iSeekControl;
		}
		return null;
	}
}
