package com.nokia.mid.impl.isa.location;

import javax.microedition.location.AddressInfo;
import javax.microedition.location.Location;
import javax.microedition.location.QualifiedCoordinates;

public class LocationImpl extends Location {
	private long timestamp;
	private float speed;
	private float course;
	private int locationMethod;
	private boolean isValid = false;
	private QualifiedCoordinates coordinates;
	private String extraInfo = null;
	private static final String NMEA = "application/X-jsr179-location-nmea";

	LocationImpl(QualifiedCoordinates coordinates) {
		this.coordinates = coordinates;
	}

	private LocationImpl(LocationImpl other) {
		this.timestamp = other.timestamp;
		this.coordinates = other.getQualifiedCoordinates();
		this.speed = other.speed;
		this.course = other.course;
		this.locationMethod = other.locationMethod;
		this.extraInfo = other.extraInfo;
		this.isValid = other.isValid();
	}

	public LocationImpl clone() {
		LocationImpl c = new LocationImpl(this);
		return c;
	}

	void setTimestamp(long time) {
		this.timestamp = time;
	}

	public boolean isValid() {
		return this.isValid;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public QualifiedCoordinates getQualifiedCoordinates() {
		if ((isValid()) && (this.coordinates != null)) {
			try {
				return new QualifiedCoordinates(this.coordinates.getLatitude(), this.coordinates
						.getLongitude(), this.coordinates.getAltitude(), this.coordinates
						.getHorizontalAccuracy(), this.coordinates.getVerticalAccuracy());
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}

	public float getSpeed() {
		return this.speed;
	}

	public float getCourse() {
		return this.course;
	}

	public int getLocationMethod() {
		return this.locationMethod;
	}

	public AddressInfo getAddressInfo() {
		return null;
	}

	public String getExtraInfo(String mimetype) {
		if ("application/X-jsr179-location-nmea".equals(mimetype)) {
			return this.extraInfo;
		}
		return null;
	}
}
