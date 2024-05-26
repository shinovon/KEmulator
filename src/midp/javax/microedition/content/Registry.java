package javax.microedition.content;

import java.io.IOException;

public class Registry {
	public static Registry getRegistry(String paramString) {
		Registry localRegistry = null;
		return localRegistry;
	}

	public static ContentHandlerServer getServer(String paramString)
			throws ContentHandlerException {
		ContentHandlerServer localContentHandlerServer = null;
		return localContentHandlerServer;
	}

	public ContentHandlerServer register(String paramString1, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, ActionNameMap[] paramArrayOfActionNameMap, String paramString2, String[] paramArrayOfString4)
			throws SecurityException, IllegalArgumentException, ClassNotFoundException, ContentHandlerException {
		ContentHandlerServer localContentHandlerServer = null;
		return localContentHandlerServer;
	}

	public boolean unregister(String paramString) {
		boolean bool = true;
		return bool;
	}

	public String[] getTypes() {
		String[] arrayOfString = null;
		return arrayOfString;
	}

	public String[] getIDs() {
		String[] arrayOfString = null;
		return arrayOfString;
	}

	public String[] getActions() {
		String[] arrayOfString = null;
		return arrayOfString;
	}

	public String[] getSuffixes() {
		String[] arrayOfString = null;
		return arrayOfString;
	}

	public ContentHandler[] forType(String paramString) {
		ContentHandler[] arrayOfContentHandler = null;
		return arrayOfContentHandler;
	}

	public ContentHandler[] forAction(String paramString) {
		ContentHandler[] arrayOfContentHandler = null;
		return arrayOfContentHandler;
	}

	public ContentHandler[] forSuffix(String paramString) {
		ContentHandler[] arrayOfContentHandler = null;
		return arrayOfContentHandler;
	}

	public ContentHandler forID(String paramString, boolean paramBoolean) {
		ContentHandler localContentHandler = null;
		return localContentHandler;
	}

	public ContentHandler[] findHandler(Invocation paramInvocation)
			throws IOException, ContentHandlerException, SecurityException {
		ContentHandler[] arrayOfContentHandler = null;
		return arrayOfContentHandler;
	}

	public boolean invoke(Invocation paramInvocation1, Invocation paramInvocation2)
			throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
		boolean bool = true;
		return bool;
	}

	public boolean invoke(Invocation paramInvocation)
			throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
		boolean bool = true;
		return bool;
	}

	public boolean reinvoke(Invocation paramInvocation)
			throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
		boolean bool = true;
		return bool;
	}

	public Invocation getResponse(boolean paramBoolean) {
		Invocation localInvocation = null;
		return localInvocation;
	}

	public void cancelGetResponse() {
	}

	public void setListener(ResponseListener paramResponseListener) {
	}

	public String getID() {
		String str = null;
		return str;
	}

	private Registry(int paramInt) {
	}
}
