package javax.obex;

public class PasswordAuthentication
{
    private byte[] username;
    private byte[] password;
    
    public PasswordAuthentication(final byte[] username, final byte[] password) {
        super();
        this.username = username;
        this.password = password;
    }
    
    public byte[] getUserName() {
        return this.username;
    }
    
    public byte[] getPassword() {
        return this.password;
    }
}
