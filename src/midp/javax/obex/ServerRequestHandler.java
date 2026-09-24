package javax.obex;

import emulator.bluetooth.obex.HeaderSetImpl;

/**
 * Base class for OBEX server request handling.
 * Applications should extend this and override onPut, onGet, etc.
 */
public class ServerRequestHandler {

    private long connectionID;

    protected ServerRequestHandler() {
        super();
    }

    /**
     * Creates a new HeaderSet for responses.
     * Now returns a real implementation.
     */
    public final HeaderSet createHeaderSet() {
        return new HeaderSetImpl();
    }

    public void setConnectionID(final long id) {
        this.connectionID = id;
    }

    public long getConnectionID() {
        return this.connectionID;
    }

    public int onConnect(final HeaderSet request, final HeaderSet reply) {
        return ResponseCodes.OBEX_HTTP_OK;
    }

    public void onDisconnect(final HeaderSet request, final HeaderSet reply) {
    }

    public int onSetPath(final HeaderSet request, final HeaderSet reply, final boolean backup, final boolean create) {
        return ResponseCodes.OBEX_HTTP_OK;
    }

    public int onDelete(final HeaderSet request, final HeaderSet reply) {
        return ResponseCodes.OBEX_HTTP_OK;
    }

    public int onPut(final Operation op) {
        return ResponseCodes.OBEX_HTTP_OK;
    }

    public int onGet(final Operation op) {
        return ResponseCodes.OBEX_HTTP_OK;
    }

    public void onAuthenticationFailure(final byte[] userName) {
    }
}
