package javax.wireless.messaging;

import java.util.*;

public class TextMessageImpl implements TextMessage {
	protected String url;
	protected String data;
	protected long timeStamp;

	public TextMessageImpl(final String url, final String data) {
		super();
		this.data = data;
		this.url = url;
		this.timeStamp = System.currentTimeMillis();
	}

	public String getPayloadText() {
		return this.data;
	}

	public void setPayloadText(final String data) {
		this.data = data;
	}

	public String getAddress() {
		return this.url;
	}

	public void setAddress(final String url) {
		this.url = url;
	}

	public Date getTimestamp() {
		return new Date(this.timeStamp);
	}
}
