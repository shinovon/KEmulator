package javax.wireless.messaging;

import java.io.IOException;

public class SizeExceededException extends IOException {
	private static final long serialVersionUID = 1L;

	public SizeExceededException(final String s) {
		super(s);
	}
}
