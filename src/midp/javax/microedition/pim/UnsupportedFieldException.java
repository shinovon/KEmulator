package javax.microedition.pim;

public class UnsupportedFieldException
		extends RuntimeException {
	private int j = -1;

	public UnsupportedFieldException() {
	}

	public UnsupportedFieldException(String paramString) {
		this(paramString, -1);
	}

	public UnsupportedFieldException(String paramString, int paramInt) {
		super(paramString);
		this.j = paramInt;
	}

	public int getField() {
		return this.j;
	}
}
