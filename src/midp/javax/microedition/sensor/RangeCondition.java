package javax.microedition.sensor;

import emulator.sensor.ConditionHelpers;

public class RangeCondition implements Condition {
	private final double lowerLimit;
	private final double upperLimit;
	private final String lowerOp;
	private final String upperOp;

	public RangeCondition(final double lowerLimit, final String lowerOp, final double aDouble379, final String aString380) {
		super();
		if (!ConditionHelpers.checkLowerOperator(lowerOp) || !ConditionHelpers.checkUpperOperator(aString380)) {
			throw new IllegalArgumentException();
		}
		if (lowerLimit > aDouble379) {
			throw new IllegalArgumentException();
		}
		if (lowerLimit == aDouble379 && (!"ge".equals(lowerOp) || !"le".equals(aString380))) {
			throw new IllegalArgumentException();
		}
		this.lowerLimit = lowerLimit;
		this.upperLimit = aDouble379;
		this.lowerOp = lowerOp;
		this.upperOp = aString380;
	}

	public final double getLowerLimit() {
		return this.lowerLimit;
	}

	public final String getLowerOp() {
		return this.lowerOp;
	}

	public final double getUpperLimit() {
		return this.upperLimit;
	}

	public final String getUpperOp() {
		return this.upperOp;
	}

	public boolean isMet(final double n) {
		return ConditionHelpers.checkValue(this.lowerOp, this.lowerLimit, n) && ConditionHelpers.checkValue(this.upperOp, this.upperLimit, n);
	}

	public boolean isMet(final Object o) {
		return false;
	}
}
