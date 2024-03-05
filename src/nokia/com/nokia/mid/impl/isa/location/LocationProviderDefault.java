package com.nokia.mid.impl.isa.location;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

public class LocationProviderDefault extends LocationProvider {
    private static final int LOC_STATUS_OK = 0;
    private static final int LOC_STATUS_FAIL = 1;
    private static final int LOC_STATUS_SECURITY_FAIL = 2;
    private static final int UNINIT_STATE = 0;
    private static final int ANY_METHOD = 0;
    private static final int RESET_BOTH = 0;
    private static final int RESET_GET_LOCATION_ONLY = 1;
    private static final int RESET_LISTENER_ONLY = 2;
    private static int listenerIDGen = 0;
    private Criteria criteria;
    private boolean haveAccess = false;
    private static LocationImpl lastLocation;
    private static boolean access;
    private volatile boolean isReset = false;
    private int nativeTransId;
    boolean isReadyForReturn;
    private boolean privacyUIDenied = false;
    LocationImpl singleLocation;
    LocationImpl listenLocation;
    int state = 0;
    private boolean sessionDenied = false;
    private int method = 0;
    LocationListener listener = null;
    private static int overallState = 0;

    LocationProviderDefault(Criteria src_criteria) {
        this.criteria = new Criteria();
        if (src_criteria != null) {
            this.criteria.setAltitudeRequired(src_criteria.isAltitudeRequired());
            this.criteria.setCostAllowed(src_criteria.isAllowedToCost());
            this.criteria.setHorizontalAccuracy(src_criteria.getHorizontalAccuracy());
            this.criteria.setPreferredPowerConsumption(src_criteria.getPreferredPowerConsumption());
            this.criteria.setPreferredResponseTime(src_criteria.getPreferredResponseTime());
            this.criteria.setSpeedAndCourseRequired(src_criteria.isSpeedAndCourseRequired());
            this.criteria.setVerticalAccuracy(src_criteria.getVerticalAccuracy());
            this.criteria.setAddressInfoRequired(src_criteria.isAddressInfoRequired());
        }
        this.singleLocation = null;
    }

    public static LocationProvider getProvider(Criteria criteria) throws LocationException {
        LocationProviderDefault lp = new LocationProviderDefault(criteria);
        if (lp != null) {
            lp.state = 1;
        }
        return lp;
    }

    public static Location getLastLocation() {
        if (lastLocation != null) {
            return lastLocation.clone();
        }
        return null;
    }

    public Location getLocation(int timeout) throws LocationException, InterruptedException {
        if ((timeout == 0) || (timeout < -1)) {
            throw new IllegalArgumentException();
        }
        this.singleLocation = getLocationInstance();
        if (this.isReset) {
            this.isReset = false;
            this.singleLocation = null;

            throw new InterruptedException();
        }
        this.nativeTransId = 0;
        LocationImpl loc = this.singleLocation;
        this.singleLocation = null;

        updateTempUnavailState();
        if (loc != null) {
            loc.setTimestamp(System.currentTimeMillis() - loc.getTimestamp());
            if (loc.isValid()) {
                if (this.isReset) {
                    this.isReset = false;
                }
                lastLocation = loc.clone();
                return loc;
            }
        }
        if (this.isReset) {
            this.isReset = false;
            throw new InterruptedException();
        }
        throw new LocationException();
    }

    public void setLocationListener(LocationListener listener, int interval, int timeout, int maxage) {
        synchronizedSetLocationListener(listener, interval, timeout, maxage);
    }

    public int getState() {
        return this.state;
    }

    public void reset() {
        if (this.singleLocation != null) {
            this.isReset = true;
            this.singleLocation = null;
        }
    }

    private static boolean accessLocationSession(boolean start) {
        boolean boRes = true;
        if (start) {
            if (!access) {
                access = true;
            } else {
                boRes = false;
            }
        } else {
            access = false;
        }
        return boRes;
    }

    private void doSetReturn(boolean doReturn) {
        this.isReadyForReturn = doReturn;
    }

    private boolean isReturnable() {
        return this.isReadyForReturn;
    }

    private void waitForReturn() {
    }

    private void updateLastLocation(LocationImpl l) {
        if (l.isValid()) {
            lastLocation = l.clone();
        }
    }

    private static LocationImpl getLocationInstance() {
        QualifiedCoordinates qc = new QualifiedCoordinates(1.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        return new LocationImpl(qc);
    }

    private void stopLocationListener() {
        this.listener = null;
    }

    private void synchronizedSetLocationListener(LocationListener listener, int interval, int timeout, int maxage) {
        if (listener == null) {
            stopLocationListener();
            return;
        }
    }

    void getFirstLocation(int interval, int timeout, int maxage, int listenerID) throws SecurityException {
        this.listenLocation = getLocationInstance();

        if (!isReturnable()) {
            doSetReturn(true);
        }
    }

    Location getNextLocation(int listenerID) {
        this.listenLocation = getLocationInstance();

        this.listenLocation.setTimestamp(System.currentTimeMillis() - this.listenLocation.getTimestamp());
        updateLastLocation(this.listenLocation);
        return this.listenLocation;
    }

    private static void updateTempUnavailState() {

    }

    int validateState() {
        int state = 0;
        switch (this.method) {
            case 19:
                state = overallState;
                break;
            case 18:
                state = overallState >> 2;
                break;
            case 17:
                state = overallState >> 4;
        }
        return state & 0x3;
    }
}
