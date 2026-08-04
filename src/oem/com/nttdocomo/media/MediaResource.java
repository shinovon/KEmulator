package com.nttdocomo.media;

import com.nttdocomo.io.ConnectionException;

public abstract interface MediaResource {
    public abstract void use()
            throws ConnectionException;

    public abstract void use(MediaResource paramMediaResource, boolean paramBoolean)
            throws ConnectionException;

    public abstract void unuse();

    public abstract void dispose();

    public abstract String getProperty(String paramString);

    public abstract void setProperty(String paramString1, String paramString2);

    public abstract boolean isRedistributable();

    public abstract boolean setRedistributable(boolean paramBoolean);
}
