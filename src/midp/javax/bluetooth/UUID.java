package javax.bluetooth;

public class UUID {
	String s;

	public UUID(final long n) {
		this(Long.toHexString(n), true);
	}

	public UUID(final String s, final boolean b) {
		super();
		int length = s.length();
		if (b) {
			if (length < 1 || length > 8) {
				throw new IllegalArgumentException();
			}
			this.s = Integer.toHexString(Integer.parseInt(s, 16)).toUpperCase();
			return;
		}
		if (length < 1 || length > 32) {
			throw new IllegalArgumentException();
		}
		this.s = Long.toHexString(Long.parseLong(s, 16)).toUpperCase();
	}

	public String toString() {
		return this.s;
	}

	public boolean equals(final Object o) {
		return o instanceof UUID && this.s.equals(((UUID) o).s);
	}

	public int hashCode() {
		return this.s.hashCode();
	}
}
