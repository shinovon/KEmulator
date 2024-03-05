package javax.microedition.sensor;

public interface DataListener {
    void dataReceived(final SensorConnection p0, final Data[] p1, final boolean p2);
}
