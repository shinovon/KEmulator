package javax.microedition.sensor;

public interface Channel {
    void addCondition(final ConditionListener p0, final Condition p1);

    ChannelInfo getChannelInfo();

    Condition[] getConditions(final ConditionListener p0);

    String getChannelUrl();

    void removeAllConditions();

    void removeCondition(final ConditionListener p0, final Condition p1);

    void removeConditionListener(final ConditionListener p0);
}
