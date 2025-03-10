package emulator.media.vlc;

import uk.co.caprica.vlcj.media.callback.nonseekable.NonSeekableInputStreamMedia;

import java.io.IOException;
import java.io.InputStream;

public class VLCCallbackStream extends NonSeekableInputStreamMedia {

	private InputStream stream;
	private long size;

	public VLCCallbackStream(InputStream is, long size) {
		super();
		this.stream = is;
		this.size = size;
	}

	@Override
	protected InputStream onOpenStream() throws IOException {
		return stream;
	}

	@Override
	protected void onCloseStream(InputStream inputStream) throws IOException {
		stream.close();
	}

	@Override
	protected long onGetSize() {
		return size;
	}

}
