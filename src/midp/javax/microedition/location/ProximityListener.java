package javax.microedition.location;

public abstract interface ProximityListener {
    public abstract void proximityEvent(Coordinates paramCoordinates, Location paramLocation);

    public abstract void monitoringStateChanged(boolean paramBoolean);
}
