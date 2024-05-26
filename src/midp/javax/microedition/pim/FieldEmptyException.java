package javax.microedition.pim;

public class FieldEmptyException
		extends RuntimeException {
	private int j = -1;

	public FieldEmptyException() {
	}

	public FieldEmptyException(String paramString) {
		this(paramString, -1);
	}

	public FieldEmptyException(String paramString, int paramInt) {
		super(paramString);
		this.j = paramInt;
	}

	public int getField() {
		return this.j;
	}
}
