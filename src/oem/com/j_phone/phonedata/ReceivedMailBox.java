package com.j_phone.phonedata;

import java.io.IOException;

public abstract interface ReceivedMailBox
		extends PhoneData {
	public abstract int getUnReadMailCount()
			throws IOException;
}
