package com.nttdocomo.io;

import javax.microedition.io.*;

public interface ObexConnection extends StreamConnection {
	public static final int DISCONNECT = 129;
	public static final int PUT = 130;
	public static final int GET = 131;
	public static final int CONTINUE = 16;
	public static final int SUCCESS = 32;
	public static final int CREATED = 33;
	public static final int ACCEPTED = 34;
	public static final int NON_AUTHORITATIVE_INFORMATION = 35;
	public static final int NO_CONTENT = 36;
	public static final int RESET_CONTENT = 37;
	public static final int PARTIAL_CONTENT = 38;
	public static final int MULTIPLE_CHOICES = 48;
	public static final int MOVED_PERMANENTLY = 49;
	public static final int MOVED_TEMPORARILY = 50;
	public static final int SEE_OTHER = 51;
	public static final int NOT_MODIFIED = 52;
	public static final int USE_PROXY = 53;
	public static final int BAD_REQUEST = 64;
	public static final int UNAUTHORIZED = 65;
	public static final int PAYMENT_REQUIRED = 66;
	public static final int FORBIDDEN = 67;
	public static final int NOT_FOUND = 68;
	public static final int METHOD_NOT_ALLOWED = 69;
	public static final int NOT_ACCEPTABLE = 70;
	public static final int PROXY_AUTHENTICATION_REQUIRED = 71;
	public static final int REQUEST_TIME_OUT = 72;
	public static final int CONFLICT = 73;
	public static final int GONE = 74;
	public static final int LENGTH_REQUIRED = 75;
	public static final int PRECONDITION_FAILED = 76;
	public static final int REQUEST_ENTITY_TOO_LARGE = 77;
	public static final int REQUEST_URL_TOO_LARGE = 78;
	public static final int UNSUPPORTED_MEDIA_TYPE = 79;
	public static final int INTERNAL_SERVER_ERROR = 80;
	public static final int NOT_IMPLEMENTED = 81;
	public static final int BAD_GATEWAY = 82;
	public static final int SERVICE_UNAVAILABLE = 83;
	public static final int GATEWAY_TIMEOUT = 84;
	public static final int HTTP_VERSION_NOT_SUPPORTED = 85;
	public static final int DATABASE_FULL = 96;
	public static final int DATABASE_LOCKED = 97;

	int getContentLength();

	void setName(final String p0);

	String getName();

	void setType(final String p0);

	String getType();

	void setTime(final long p0);

	long getTime();
}
