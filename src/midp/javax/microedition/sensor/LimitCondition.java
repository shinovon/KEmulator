package javax.microedition.sensor;

import emulator.sensor.ConditionHelpers;

public class LimitCondition implements Condition {
	private final double limit;
	private final String operator;

	public LimitCondition(final double limit, final String operator) {
		super();
		if (operator == null) {
			throw new NullPointerException();
		}
		if (!ConditionHelpers.checkOperator(operator)) {
			throw new IllegalArgumentException();
		}
		this.limit = limit;
		this.operator = operator;
	}

	public final double getLimit() {
		return this.limit;
	}

	public final String getOperator() {
		return this.operator;
	}

	public boolean isMet(final double n) {
		return ConditionHelpers.checkValue(this.operator, this.limit, n);
	}

	public boolean isMet(final Object o) {
		return false;
	}
}
