package javax.microedition.midlet;

public class MIDletStateChangeException extends Exception {
	private static final long serialVersionUID = 1L;

	public MIDletStateChangeException() {
		super();
	}

	public MIDletStateChangeException(final String s) {
		super(s);
	}
}
