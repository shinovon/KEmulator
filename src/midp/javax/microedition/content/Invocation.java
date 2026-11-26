package javax.microedition.content;

import javax.microedition.io.Connection;
import java.io.IOException;

public final class Invocation {
	public static final int ACTIVE = 2;
	public static final int CANCELLED = 6;
	public static final int ERROR = 7;
	public static final int HOLD = 4;
	public static final int INIT = 1;
	public static final int INITIATED = 8;
	public static final int OK = 5;
	public static final int WAITING = 3;

	String url;
	String id;
	boolean responseRequired;
	String action;
	String type;

	public Invocation() {
	}

	public Invocation(String url) {
		this(url, null, null, true, null);
	}

	public Invocation(String url, String type) {
		this(url, type, null, true, null);
	}

	public Invocation(String url, String type, String id) {
		this(url, type, id, true, null);
	}

	public Invocation(String url, String type, String id, boolean responseRequired, String action) {
		this.url = url;
		this.type = type;
		this.id = id;
		this.responseRequired = responseRequired;
		this.action = action;
	}

	public String findType()
			throws IOException, ContentHandlerException, SecurityException {
		String str = null;
		return str;
	}

	public String getAction() {
		return action;
	}

	public String[] getArgs() {
		String[] arrayOfString = null;
		return arrayOfString;
	}

	public byte[] getData() {
		byte[] arrayOfByte = null;
		return arrayOfByte;
	}

	public String getID() {
		return id;
	}

	public String getInvokingAppName() {
		String str = null;
		return str;
	}

	public String getInvokingAuthority() {
		String str = null;
		return str;
	}

	public String getInvokingID() {
		String str = null;
		return str;
	}

	public Invocation getPrevious() {
		Invocation localInvocation = null;
		return localInvocation;
	}

	public boolean getResponseRequired() {
		return responseRequired;
	}

	public int getStatus() {
		int i = 1;
		return i;
	}

	public String getType() {
		return type;
	}

	public String getURL() {
		return url;
	}

	public Connection open(boolean paramBoolean)
			throws IOException, SecurityException {
		Connection localConnection = null;
		return localConnection;
	}

	public void setAction(String paramString) {
		action = paramString;
	}

	public void setArgs(String[] paramArrayOfString) {
	}

	public void setCredentials(String paramString, char[] paramArrayOfChar) {
	}

	public void setData(byte[] paramArrayOfByte) {
	}

	public void setID(String paramString) {
		id = paramString;
	}

	public void setResponseRequired(boolean paramBoolean) {

	}

	public void setType(String paramString) {
		type = paramString;
	}

	public void setURL(String paramString) {
		url = paramString;
	}
}
