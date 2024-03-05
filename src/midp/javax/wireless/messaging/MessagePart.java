package javax.wireless.messaging;

import java.io.*;

public class MessagePart {
    static int anInt158;
    byte[] aByteArray159;
    String aString160;
    String aString161;
    String aString162;
    String aString163;

    final void method54(final byte[] array, final int n, final int n2, final String aString163, final String aString164, final String aString165, final String aString166) throws SizeExceededException {
        if (n2 > MessagePart.anInt158) {
            throw new SizeExceededException("InputStream data exceeds " + MessagePart.anInt158 + " byte MessagePart size limit");
        }
        if (aString163 == null) {
            throw new IllegalArgumentException("mimeType must be specified");
        }
        method55(aString164);
        method57(aString165);
        if (n2 < 0) {
            throw new IllegalArgumentException("length must be >= 0");
        }
        if (array != null && n + n2 > array.length) {
            throw new IllegalArgumentException("offset + length exceeds contents length");
        }
        if (n < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        if (array != null) {
            System.arraycopy(array, n, this.aByteArray159 = new byte[n2], 0, n2);
        }
        this.aString163 = aString163;
        this.aString160 = aString164;
        this.aString161 = aString165;
        this.aString162 = aString166;
    }

    public MessagePart(final byte[] array, final int n, final int n2, final String s, final String s2, final String s3, final String s4) throws SizeExceededException {
        super();
        this.method54(array, n, n2, s, s2, s3, s4);
    }

    public MessagePart(final byte[] array, final String s, final String s2, final String s3, final String s4) throws SizeExceededException {
        super();
        this.method54(array, 0, (array == null) ? 0 : array.length, s, s2, s3, s4);
    }

    public MessagePart(final InputStream inputStream, final String s, final String s2, final String s3, final String s4) throws IOException, SizeExceededException {
        super();
        byte[] byteArray = new byte[0];
        if (inputStream != null) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] array = new byte[2048];
            int read;
            while ((read = inputStream.read(array)) != -1) {
                byteArrayOutputStream.write(array, 0, read);
            }
            byteArray = byteArrayOutputStream.toByteArray();
        }
        this.method54(byteArray, 0, byteArray.length, s, s2, s3, s4);
    }

    public byte[] getContent() {
        if (this.aByteArray159 == null) {
            return null;
        }
        final byte[] array = new byte[this.aByteArray159.length];
        System.arraycopy(this.aByteArray159, 0, array, 0, this.aByteArray159.length);
        return array;
    }

    public InputStream getContentAsStream() {
        if (this.aByteArray159 == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(this.aByteArray159);
    }

    public String getContentID() {
        return this.aString160;
    }

    public String getContentLocation() {
        return this.aString161;
    }

    public String getEncoding() {
        return this.aString162;
    }

    public int getLength() {
        if (this.aByteArray159 == null) {
            return 0;
        }
        return this.aByteArray159.length;
    }

    public String getMIMEType() {
        return this.aString163;
    }

    static void method55(final String s) throws IllegalArgumentException {
        if (s == null) {
            throw new IllegalArgumentException("contentId must be specified");
        }
        if (s.length() > 100) {
            throw new IllegalArgumentException("contentId exceeds 100 char limit");
        }
        if (method56(s)) {
            throw new IllegalArgumentException("contentId must not contain non-US-ASCII characters");
        }
    }

    static void method57(final String s) throws IllegalArgumentException {
        if (s != null) {
            if (method56(s)) {
                throw new IllegalArgumentException("contentLocation must not contain non-US-ASCII characters");
            }
            if (s.length() > 100) {
                throw new IllegalArgumentException("contentLocation exceeds 100 char limit");
            }
        }
    }

    static boolean method56(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1;
            if ((char1 = s.charAt(i)) < ' ' || char1 != (char1 & '\u007f')) {
                return true;
            }
        }
        return false;
    }

    static {
        MessagePart.anInt158 = 30720;
    }
}
