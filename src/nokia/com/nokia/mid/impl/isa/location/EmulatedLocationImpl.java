package com.nokia.mid.impl.isa.location;

import javax.microedition.location.QualifiedCoordinates;

public class EmulatedLocationImpl extends LocationImpl {
	EmulatedLocationImpl(QualifiedCoordinates coordinates) {
		super(coordinates);
	}

	public boolean isValid() {
		return true;
	}
}
