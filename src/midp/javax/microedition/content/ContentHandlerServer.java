package javax.microedition.content;

public abstract interface ContentHandlerServer
        extends ContentHandler {
    public abstract int accessAllowedCount();

    public abstract void cancelGetRequest();

    public abstract boolean finish(Invocation paramInvocation, int paramInt);

    public abstract String getAccessAllowed(int paramInt);

    public abstract Invocation getRequest(boolean paramBoolean);

    public abstract boolean isAccessAllowed(String paramString);

    public abstract void setListener(RequestListener paramRequestListener);
}
