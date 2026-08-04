package javax.microedition.sensor;

public class ObjectCondition implements Condition {
	private final Object limit;

	public ObjectCondition(final Object limit) {
		super();
		if (limit == null) {
			throw new NullPointerException();
		}
		this.limit = limit;
	}

	public final Object getLimit() {
		return this.limit;
	}

	public boolean isMet(final double n) {
		return false;
	}

	public boolean isMet(final Object o) {
		return this.limit.equals(o);
	}
}
