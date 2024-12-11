package javax.microedition.pim;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class PIMImpl extends PIM {
    public PIMList openPIMList(int paramInt1, int paramInt2) throws PIMException {
        return new PIMListImpl();
    }

    public PIMList openPIMList(int paramInt1, int paramInt2, String paramString) throws PIMException {
        return new PIMListImpl();
    }

    public String[] listPIMLists(int paramInt) {
        return new String[0];
    }

    public PIMItem[] fromSerialFormat(InputStream paramInputStream, String paramString) throws PIMException, UnsupportedEncodingException {
        return new PIMItem[0];
    }

    public void toSerialFormat(PIMItem paramPIMItem, OutputStream paramOutputStream, String paramString1, String paramString2) throws PIMException, UnsupportedEncodingException {

    }

    public String[] supportedSerialFormats(int paramInt) {
        return new String[0];
    }
}
