package emulator.sensor;

import javax.microedition.sensor.Condition;
import javax.microedition.sensor.ConditionListener;
//ConditionListenerPair from
//https://github.com/hbao/phonemefeaturedevices/blob/master/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/ChannelImpl.java#L84
final class e {
	private ConditionListener listener;
	private Condition condition;

	public e(final ConditionListener listener, final Condition condition) {
		super();
		this.listener = listener;
		this.condition = condition;
	}

	public final Condition getCondition() {
		return this.condition;
	}
    //method266 = equals; but I'm scared of renaming
	public final boolean method266(final e e) {
		return this.listener == e.listener && this.condition == e.condition;
	}

	public final boolean matches(final ConditionListener conditionListener) {
		return this.listener == conditionListener;
	}

	public final boolean matches(final ConditionListener conditionListener, final Condition condition) {
		return this.listener == conditionListener && this.condition == condition;
	}
}
