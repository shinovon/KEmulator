package emulator.sensor;

public interface SensorProperties {
    void setProperty(final String p0, final Object p1);

    Object getProperty(final String p0);

    String[] getPropertyNames();
}
