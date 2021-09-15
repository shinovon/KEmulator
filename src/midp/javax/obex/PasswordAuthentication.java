package javax.obex;

public class PasswordAuthentication
{
    private byte[] aByteArray514;
    private byte[] aByteArray515;
    
    public PasswordAuthentication(final byte[] aByteArray514, final byte[] aByteArray515) {
        super();
        this.aByteArray514 = aByteArray514;
        this.aByteArray515 = aByteArray515;
    }
    
    public byte[] getUserName() {
        return this.aByteArray514;
    }
    
    public byte[] getPassword() {
        return this.aByteArray515;
    }
}
