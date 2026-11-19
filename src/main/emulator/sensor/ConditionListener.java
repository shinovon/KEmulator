package emulator.sensor;

import javax.microedition.sensor.Condition;

final class ConditionListener {
	private javax.microedition.sensor.ConditionListener listener;
	private Condition condition;

	public ConditionListener(final javax.microedition.sensor.ConditionListener listener, final Condition condition) {
		super();
		this.listener = listener;
		this.condition = condition;
	}

	public final Condition getCondition() {
		return this.condition;
	}

	public final boolean equals(final ConditionListener ConditionListener) {
		return this.listener == ConditionListener.listener && this.condition == ConditionListener.condition;
	}

	public final boolean matches(final javax.microedition.sensor.ConditionListener conditionListener) {
		return this.listener == conditionListener;
	}

	public final boolean matches(final javax.microedition.sensor.ConditionListener conditionListener, final Condition condition) {
		return this.listener == conditionListener && this.condition == condition;
	}
}
