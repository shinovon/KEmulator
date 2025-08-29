package emulator.debug;

import java.util.Objects;

public class IdentityWrapper {
	public final Object value;

	public IdentityWrapper(Object value) {
		this.value = value;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof IdentityWrapper) {
			return value == ((IdentityWrapper) o).value;
		}
		return false;

	}

	@Override
	public int hashCode() {
		return System.identityHashCode(value);
	}
}
