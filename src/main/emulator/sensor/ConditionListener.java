package emulator.sensor;

import javax.microedition.sensor.Condition;

//ConditionListenerPair from
//https://github.com/hbao/phonemefeaturedevices/blob/master/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/ChannelImpl.java#L84
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
    //method266 = equals; but I'm scared of renaming
	public final boolean method266(final ConditionListener ConditionListener) {
		return this.listener == ConditionListener.listener && this.condition == ConditionListener.condition;
	}

	public final boolean matches(final javax.microedition.sensor.ConditionListener conditionListener) {
		return this.listener == conditionListener;
	}

	public final boolean matches(final javax.microedition.sensor.ConditionListener conditionListener, final Condition condition) {
		return this.listener == conditionListener && this.condition == condition;
	}
}
