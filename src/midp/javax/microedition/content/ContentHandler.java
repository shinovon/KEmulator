package javax.microedition.content;

public abstract interface ContentHandler {
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_EDIT = "edit";
	public static final String ACTION_EXECUTE = "execute";
	public static final String ACTION_INSTALL = "install";
	public static final String ACTION_INSTALL_ONLY = "install_only";
	public static final String ACTION_NEW = "new";
	public static final String ACTION_OPEN = "open";
	public static final String ACTION_PRINT = "print";
	public static final String ACTION_REMOVE = "remove";
	public static final String ACTION_SAVE = "save";
	public static final String ACTION_SELECT = "select";
	public static final String ACTION_SEND = "send";
	public static final String ACTION_STOP = "stop";
	public static final String UNIVERSAL_TYPE = "*";

	public abstract String getAction(int paramInt);

	public abstract int getActionCount();

	public abstract ActionNameMap getActionNameMap();

	public abstract ActionNameMap getActionNameMap(int paramInt);

	public abstract ActionNameMap getActionNameMap(String paramString);

	public abstract int getActionNameMapCount();

	public abstract String getAppName();

	public abstract String getAuthority();

	public abstract String getID();

	public abstract String getSuffix(int paramInt);

	public abstract int getSuffixCount();

	public abstract String getType(int paramInt);

	public abstract int getTypeCount();

	public abstract String getVersion();

	public abstract boolean hasAction(String paramString);

	public abstract boolean hasSuffix(String paramString);

	public abstract boolean hasType(String paramString);
}
