package emulator.bluetooth.obex;

import javax.obex.HeaderSet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of OBEX HeaderSet.
 * Stores headers as map id -> object.
 */
public class HeaderSetImpl implements HeaderSet {

    private final Map<Integer, Object> headers = new HashMap<>();
    private int responseCode = -1;
    private String authChallengeRealm;
    private boolean authChallengeUserIdRequired;
    private boolean authChallengeAccess;

    public HeaderSetImpl() {}

    @Override
    public void setHeader(int headerID, Object headerValue) {
        if (headerValue == null) {
            headers.remove(headerID);
        } else {
            headers.put(headerID, headerValue);
        }
    }

    @Override
    public Object getHeader(int headerID) throws IOException {
        return headers.get(headerID);
    }

    @Override
    public int[] getHeaderList() throws IOException {
        int[] list = new int[headers.size()];
        int i = 0;
        for (Integer id : headers.keySet()) {
            list[i++] = id;
        }
        return list;
    }

    @Override
    public void createAuthenticationChallenge(String realm, boolean userId, boolean access) {
        this.authChallengeRealm = realm;
        this.authChallengeUserIdRequired = userId;
        this.authChallengeAccess = access;
    }

    @Override
    public int getResponseCode() throws IOException {
        return responseCode;
    }

    public void setResponseCode(int code) {
        this.responseCode = code;
    }

    public Map<Integer, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<Integer, Object> newHeaders) {
        headers.clear();
        if (newHeaders != null) {
            headers.putAll(newHeaders);
        }
    }
}
