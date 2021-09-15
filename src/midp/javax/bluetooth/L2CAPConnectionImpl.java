package javax.bluetooth;

import java.net.*;
import java.io.*;
import javax.microedition.io.*;

public class L2CAPConnectionImpl implements L2CAPConnection
{
    Socket aSocket315;
    InputStream anInputStream436;
    OutputStream anOutputStream437;
    
    protected L2CAPConnectionImpl(final Socket aSocket315) throws IOException {
        super();
        this.aSocket315 = aSocket315;
        this.anInputStream436 = aSocket315.getInputStream();
        this.anOutputStream437 = aSocket315.getOutputStream();
        aSocket315.setReceiveBufferSize(672);
        aSocket315.setSendBufferSize(672);
    }
    
    public int getTransmitMTU() throws IOException {
        return this.aSocket315.getSendBufferSize();
    }
    
    public int getReceiveMTU() throws IOException {
        return this.aSocket315.getReceiveBufferSize();
    }
    
    public void send(final byte[] array) throws IOException {
        this.anOutputStream437.write(array, 0, Math.min(672, array.length));
    }
    
    public int receive(final byte[] array) throws IOException {
        return this.anInputStream436.read(array, 0, Math.min(672, array.length));
    }
    
    public boolean ready() throws IOException {
        return this.anInputStream436.available() > 0;
    }
    
    public void close() throws IOException {
        this.aSocket315.close();
    }
    
    public static Connection open(final String s) throws IOException {
        final int n = s.indexOf("://") + 3;
        final String substring = s.substring(n, s.indexOf(":", n));
        final int index = s.indexOf(";");
        return new L2CAPConnectionImpl(new Socket(substring, Integer.parseInt(s.substring(s.indexOf(":", n) + 1, (index < 0) ? s.length() : index))));
    }
}
