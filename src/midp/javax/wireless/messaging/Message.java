package javax.wireless.messaging;

import java.util.Date;

public interface Message {
	String getAddress();

	void setAddress(final String p0);

	Date getTimestamp();
}
