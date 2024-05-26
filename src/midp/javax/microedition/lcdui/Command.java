package javax.microedition.lcdui;

public class Command {
	public static final int SCREEN = 1;
	public static final int BACK = 2;
	public static final int CANCEL = 3;
	public static final int OK = 4;
	public static final int HELP = 5;
	public static final int STOP = 6;
	public static final int EXIT = 7;
	public static final int ITEM = 8;
	private int anInt360;
	private int anInt362;
	private String aString361;
	private String aString363;

	public Command(final String s, final int n, final int n2) {
		this(s, s, n, n2);
	}

	public Command(final String aString361, final String aString362, final int anInt360, final int anInt361) {
		super();
		this.anInt360 = anInt360;
		this.anInt362 = anInt361;
		this.aString361 = aString361;
		this.aString363 = aString362;
	}

	public int getCommandType() {
		return this.anInt360;
	}

	public String getLabel() {
		return this.aString361;
	}

	public String getLongLabel() {
		return this.aString363;
	}

	public int getPriority() {
		return this.anInt362;
	}

	public String toString() {
		return this.aString363;
	}
}
