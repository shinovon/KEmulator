package com.motorola.funlight;

class Factory {
	public static Region getRegion(int i) {
		return FunLight.getRegion(i);
	}

	public static Region[] getRegions() {
		return FunLight.getRegions();
	}
}
