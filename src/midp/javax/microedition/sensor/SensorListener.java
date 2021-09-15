package javax.microedition.sensor;

public interface SensorListener
{
    void sensorAvailable(final SensorInfo p0);
    
    void sensorUnavailable(final SensorInfo p0);
}
