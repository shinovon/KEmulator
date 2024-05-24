package com.mascotcapsule.micro3d.v3;

import java.io.IOException;

public class ActionTable {

    public ActionTable(byte[] paramArrayOfByte) {
    }

    public ActionTable(String paramString)
            throws IOException {
    }

    public final void dispose() {
    }

    /**
     * @deprecated
     */
    public final int getNumAction() {
        return getNumActions();
    }

    public final int getNumActions() {
        return 0;
    }

    /**
     * @deprecated
     */
    public final int getNumFrame(int paramInt) {
        return getNumFrames(paramInt);
    }

    public final int getNumFrames(int paramInt) {
        return 0;
    }
}
