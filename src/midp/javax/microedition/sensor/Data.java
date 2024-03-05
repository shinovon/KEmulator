package javax.microedition.sensor;

public interface Data {
    ChannelInfo getChannelInfo();

    double[] getDoubleValues();

    int[] getIntValues();

    Object[] getObjectValues();

    long getTimestamp(final int p0);

    float getUncertainty(final int p0);

    boolean isValid(final int p0);
}
