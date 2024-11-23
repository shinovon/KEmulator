package javax.microedition.lcdui;

public class Command implements Comparable {
	public static final int SCREEN = 1;
	public static final int BACK = 2;
	public static final int CANCEL = 3;
	public static final int OK = 4;
	public static final int HELP = 5;
	public static final int STOP = 6;
	public static final int EXIT = 7;
	public static final int ITEM = 8;
	private int type;
	private int priority;
	private String label;
	private String longLabel;

	public Command(final String s, final int n, final int n2) {
		this(s, s, n, n2);
	}

	public Command(final String aString361, final String aString362, final int anInt360, final int anInt361) {
		super();
		this.type = anInt360;
		this.priority = anInt361;
		this.label = aString361;
		this.longLabel = aString362;
	}

	public int getCommandType() {
		return this.type;
	}

	public String getLabel() {
		return this.label;
	}

	public String getLongLabel() {
		return this.longLabel;
	}

	public int getPriority() {
		return this.priority;
	}

	public String toString() {
		return this.longLabel;
	}

	public int compareTo(Object o) {
		if (!(o instanceof Command)) return 0;
		return priority - ((Command) o).priority;
	}
}
