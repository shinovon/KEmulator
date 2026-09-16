package emulator.bluetooth.obex;

import javax.obex.HeaderSet;
import javax.obex.Operation;
import java.io.*;

/**
 * Implementation of OBEX Operation (PUT/GET).
 * Wraps input/output streams and headers.
 */
public class OperationImpl implements Operation {

    private final HeaderSetImpl receivedHeaders;
    private HeaderSetImpl sentHeaders = new HeaderSetImpl();
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ByteArrayOutputStream buffer;
    private final boolean isPut;
    private int responseCode = 0xA0; // OBEX_HTTP_OK
    private boolean closed = false;

    // For server side: data received from client
    private byte[] requestData;

    public OperationImpl(HeaderSetImpl receivedHeaders, InputStream in, OutputStream out, boolean isPut) {
        this.receivedHeaders = receivedHeaders;
        this.inputStream = in;
        this.outputStream = out;
        this.isPut = isPut;
        this.buffer = new ByteArrayOutputStream();
    }

    public OperationImpl(HeaderSetImpl receivedHeaders, byte[] requestData, boolean isPut) {
        this.receivedHeaders = receivedHeaders;
        this.requestData = requestData;
        this.inputStream = requestData != null ? new ByteArrayInputStream(requestData) : new ByteArrayInputStream(new byte[0]);
        this.buffer = new ByteArrayOutputStream();
        this.outputStream = buffer;
        this.isPut = isPut;
    }

    @Override
    public void abort() throws IOException {
        closed = true;
        responseCode = 0x90; // OBEX_HTTP_BAD_REQUEST?
    }

    @Override
    public HeaderSet getReceivedHeaders() throws IOException {
        return receivedHeaders;
    }

    @Override
    public void sendHeaders(HeaderSet headers) throws IOException {
        if (headers instanceof HeaderSetImpl) {
            sentHeaders.setHeaders(((HeaderSetImpl) headers).getHeaders());
        }
    }

    @Override
    public int getResponseCode() throws IOException {
        return responseCode;
    }

    public void setResponseCode(int code) {
        this.responseCode = code;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (closed) throw new IOException("Closed");
        return inputStream;
    }

    @Override
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        if (closed) throw new IOException("Closed");
        return outputStream;
    }

    @Override
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    @Override
    public void close() throws IOException {
        closed = true;
        try { inputStream.close(); } catch (IOException ignored) {}
        try { outputStream.close(); } catch (IOException ignored) {}
    }

    @Override
    public String getType() {
        try {
            Object t = receivedHeaders.getHeader(HeaderSet.TYPE);
            return t != null ? t.toString() : null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public long getLength() {
        try {
            Object len = receivedHeaders.getHeader(HeaderSet.LENGTH);
            if (len instanceof Long) return (Long) len;
            if (len instanceof Integer) return ((Integer) len).longValue();
        } catch (IOException ignored) {}
        return -1;
    }

    public byte[] getOutputData() {
        return buffer.toByteArray();
    }

    public HeaderSetImpl getSentHeaders() {
        return sentHeaders;
    }

    public boolean isPut() {
        return isPut;
    }
}
