package javax.wireless.messaging;

import java.util.*;

final class BinaryMessageImpl implements BinaryMessage {
    protected String aString355;
    protected byte[] aByteArray356;
    protected long aLong357;

    protected BinaryMessageImpl(final String aString355, final byte[] aByteArray356) {
        super();
        this.aByteArray356 = aByteArray356;
        this.aString355 = aString355;
        this.aLong357 = System.currentTimeMillis();
    }

    public final byte[] getPayloadData() {
        return this.aByteArray356;
    }

    public final void setPayloadData(final byte[] aByteArray356) {
        this.aByteArray356 = aByteArray356;
    }

    public final String getAddress() {
        return this.aString355;
    }

    public final void setAddress(final String aString355) {
        this.aString355 = aString355;
    }

    public final Date getTimestamp() {
        return new Date(this.aLong357);
    }
}
