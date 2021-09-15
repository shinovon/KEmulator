package javax.microedition.io;

import java.net.*;
import emulator.*;
import java.io.*;

final class SocketConnectionImpl implements SocketConnection
{
    Socket aSocket315;
    
    public SocketConnectionImpl(final String s) throws IOException {
        super();
        Emulator.getEmulator().getLogStream().println("SocketConnection: " + s);
        final int n = s.indexOf("://") + 3;
        final int n2 = s.lastIndexOf(":") + 1;
        final String substring = s.substring(n, n2 - 1);
        final int int1 = Integer.parseInt(s.substring(n2));
        Emulator.getEmulator().getLogStream().println("host:" + substring);
        Emulator.getEmulator().getLogStream().println("port:" + int1);
        this.aSocket315 = new Socket(substring, int1);
    }
    
    public final String getAddress() throws IOException {
        return this.aSocket315.getInetAddress().getHostAddress();
    }
    
    public final String getLocalAddress() throws IOException {
        return this.aSocket315.getLocalAddress().getHostAddress();
    }
    
    public final int getLocalPort() throws IOException {
        return this.aSocket315.getLocalPort();
    }
    
    public final int getPort() throws IOException {
        return this.aSocket315.getPort();
    }
    
    public final int getSocketOption(final byte b) throws IllegalArgumentException, IOException {
        switch (b) {
            case 0: {
                if (this.aSocket315.getTcpNoDelay()) {
                    return 1;
                }
                return 0;
            }
            case 1: {
                return this.aSocket315.getSoLinger();
            }
            case 2: {
                if (this.aSocket315.getKeepAlive()) {
                    return 1;
                }
                return 0;
            }
            case 3: {
                return this.aSocket315.getReceiveBufferSize();
            }
            case 4: {
                return this.aSocket315.getSendBufferSize();
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public final void setSocketOption(final byte b, final int n) throws IllegalArgumentException, IOException {
        switch (b) {
            case 0: {
                this.aSocket315.setTcpNoDelay(n != 0);
            }
            case 1: {
                this.aSocket315.setSoLinger(n != 0, n);
            }
            case 2: {
                this.aSocket315.setKeepAlive(n != 0);
            }
            case 3: {
                this.aSocket315.setReceiveBufferSize(n);
            }
            case 4: {
                this.aSocket315.setSendBufferSize(n);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public final void close() throws IOException {
        this.aSocket315.close();
    }
    
    public final DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.openInputStream());
    }
    
    public final InputStream openInputStream() throws IOException {
        return this.aSocket315.getInputStream();
    }
    
    public final DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(this.openOutputStream());
    }
    
    public final OutputStream openOutputStream() throws IOException {
        return this.aSocket315.getOutputStream();
    }
}
