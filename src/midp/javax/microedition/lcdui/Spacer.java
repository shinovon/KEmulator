package javax.microedition.lcdui;

public class Spacer extends Item {
	private int w;
	private int h;

	public Spacer(final int n, final int n2) {
		super(null);
		this.setMinimumSize(n, n2);
	}

	public void setMinimumSize(final int anInt349, final int anInt350) {
		this.w = anInt349;
		this.h = anInt350;
		if (screen != null) {
			((Form) screen).queueLayout(this);
		}
	}

	public void addCommand(final Command command) {
		throw new IllegalStateException();
	}

	public void setDefaultCommand(final Command command) {
		throw new IllegalStateException();
	}

	public void setLabel(final String s) {
		throw new IllegalStateException();
	}

	protected void layout(Row row) {
		super.layout(row);
		super.bounds[W] = Math.min(this.w + 4, row.getAvailableWidth(screen.bounds[W]));
		super.bounds[H] = Math.min(this.h + 4, super.screen.bounds[H]);
	}

	public int getMinimumWidth() {
		return w;
	}

	public int getMinimumHeight() {
		return h;
	}

	public int getPreferredWidth() {
		return getMinimumWidth();
	}

	public int getPreferredHeight() {
		return getMinimumHeight();
	}

	boolean isFocusable() {
		return false;
	}
}
