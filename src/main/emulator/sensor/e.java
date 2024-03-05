package emulator.sensor;

import javax.microedition.sensor.*;

final class e {
    private ConditionListener aConditionListener476;
    private Condition aCondition477;

    public e(final ConditionListener aConditionListener476, final Condition aCondition477) {
        super();
        this.aConditionListener476 = aConditionListener476;
        this.aCondition477 = aCondition477;
    }

    public final Condition method265() {
        return this.aCondition477;
    }

    public final boolean method266(final e e) {
        return this.aConditionListener476 == e.aConditionListener476 && this.aCondition477 == e.aCondition477;
    }

    public final boolean method267(final ConditionListener conditionListener) {
        return this.aConditionListener476 == conditionListener;
    }

    public final boolean method268(final ConditionListener conditionListener, final Condition condition) {
        return this.aConditionListener476 == conditionListener && this.aCondition477 == condition;
    }
}
