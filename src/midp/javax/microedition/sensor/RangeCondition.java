package javax.microedition.sensor;

import emulator.sensor.ConditionHelpers;

public class RangeCondition implements Condition {
	private final double aDouble378;
	private final double aDouble380;
	private final String aString379;
	private final String aString381;

	public RangeCondition(final double aDouble378, final String aString379, final double aDouble379, final String aString380) {
		super();
		if (!ConditionHelpers.checkLowerOperator(aString379) || !ConditionHelpers.checkUpperOperator(aString380)) {
			throw new IllegalArgumentException();
		}
		if (aDouble378 > aDouble379) {
			throw new IllegalArgumentException();
		}
		if (aDouble378 == aDouble379 && (!"ge".equals(aString379) || !"le".equals(aString380))) {
			throw new IllegalArgumentException();
		}
		this.aDouble378 = aDouble378;
		this.aDouble380 = aDouble379;
		this.aString379 = aString379;
		this.aString381 = aString380;
	}

	public final double getLowerLimit() {
		return this.aDouble378;
	}

	public final String getLowerOp() {
		return this.aString379;
	}

	public final double getUpperLimit() {
		return this.aDouble380;
	}

	public final String getUpperOp() {
		return this.aString381;
	}

	public boolean isMet(final double n) {
		return ConditionHelpers.checkValue(this.aString379, this.aDouble378, n) && ConditionHelpers.checkValue(this.aString381, this.aDouble380, n);
	}

	public boolean isMet(final Object o) {
		return false;
	}
}
