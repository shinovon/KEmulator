package javax.microedition.content;

import java.io.IOException;
import javax.microedition.io.Connection;

public final class Invocation {
	public static final int ACTIVE = 2;
	public static final int CANCELLED = 6;
	public static final int ERROR = 7;
	public static final int HOLD = 4;
	public static final int INIT = 1;
	public static final int INITIATED = 8;
	public static final int OK = 5;
	public static final int WAITING = 3;

	public Invocation() {
	}

	public Invocation(String paramString) {
	}

	public Invocation(String paramString1, String paramString2) {
	}

	public Invocation(String paramString1, String paramString2, String paramString3) {
	}

	public Invocation(String paramString1, String paramString2, String paramString3, boolean paramBoolean, String paramString4) {
	}

	public String findType()
			throws IOException, ContentHandlerException, SecurityException {
		String str = null;
		return str;
	}

	public String getAction() {
		String str = null;
		return str;
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
		String str = null;
		return str;
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
		boolean bool = true;
		return bool;
	}

	public int getStatus() {
		int i = 1;
		return i;
	}

	public String getType() {
		String str = null;
		return str;
	}

	public String getURL() {
		String str = null;
		return str;
	}

	public Connection open(boolean paramBoolean)
			throws IOException, SecurityException {
		Connection localConnection = null;
		return localConnection;
	}

	public void setAction(String paramString) {
	}

	public void setArgs(String[] paramArrayOfString) {
	}

	public void setCredentials(String paramString, char[] paramArrayOfChar) {
	}

	public void setData(byte[] paramArrayOfByte) {
	}

	public void setID(String paramString) {
	}

	public void setResponseRequired(boolean paramBoolean) {
	}

	public void setType(String paramString) {
	}

	public void setURL(String paramString) {
	}
}
