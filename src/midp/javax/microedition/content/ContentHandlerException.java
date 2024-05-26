package javax.microedition.content;

import java.io.IOException;

public class ContentHandlerException
		extends IOException {
	public static final int CAPACITY_EXCEEDED = 4;
	public static final int AMBIGUOUS = 3;
	public static final int NO_REGISTERED_HANDLER = 1;
	public static final int TYPE_UNKNOWN = 2;

	public ContentHandlerException(String paramString, int paramInt) {
		super(paramString);
	}

	public int getErrorCode() {
		return TYPE_UNKNOWN;
	}
}
