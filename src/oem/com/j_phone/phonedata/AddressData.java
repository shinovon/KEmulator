package com.j_phone.phonedata;

public abstract interface AddressData
		extends DataElement {
	public static final int MEMORYDIAL_NO_INFO = 1;
	public static final int NAME_INFO = 2;
	public static final int KANA_INFO = 3;
	public static final int PHONE_NUMBER_INFO = 4;
	public static final int EMAIL_INFO = 5;
	public static final int GROUP_NO_INFO = 6;
	public static final int SECRET_INFO = 7;
	public static final int PHOTO_INFO = 8;
	public static final int GEO_ACCURACY = 9;
	public static final int GEO_GEODETIC_DATUM = 10;
	public static final int GEO_LATITUDE = 11;
	public static final int GEO_LONGITUDE = 12;
}
