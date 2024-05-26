package javax.bluetooth;

public class UUID {
	String s;

	public UUID(final long n) {
		super();
		this.s = Long.toString(n);
	}

	public UUID(final String s, final boolean b) {
		super();
		this.s = s;
	}

	public String toString() {
		return this.s;
	}

	public boolean equals(final Object o) {
		return this.s.equals(o);
	}

	public int hashCode() {
		return this.s.hashCode();
	}
}
