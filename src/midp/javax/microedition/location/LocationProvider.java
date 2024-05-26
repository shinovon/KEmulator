package javax.microedition.location;

import com.nokia.mid.impl.isa.location.EmulatedLocationProvider;
import com.nokia.mid.impl.isa.location.LocationProviderDefault;

public abstract class LocationProvider {
	public static final int AVAILABLE = 1;
	public static final int TEMPORARILY_UNAVAILABLE = 2;
	public static final int OUT_OF_SERVICE = 3;

	public static LocationProvider getInstance(Criteria criteria) throws LocationException {
		return EmulatedLocationProvider.getInstance();
	}

	public abstract Location getLocation(int paramInt) throws LocationException, InterruptedException;

	public abstract void setLocationListener(LocationListener paramLocationListener, int paramInt1, int paramInt2,
											 int paramInt3);

	public static Location getLastKnownLocation() {
		return LocationProviderDefault.getLastLocation();
	}

	public abstract int getState();

	public abstract void reset();

	public static void addProximityListener(ProximityListener listener, Coordinates coordinates, float proximityRadius)
			throws LocationException {
		throw new LocationException();
	}

	public static void removeProximityListener(ProximityListener listener) {
	}
}
