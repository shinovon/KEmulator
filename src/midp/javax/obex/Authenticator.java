package javax.obex;

public interface Authenticator
{
    PasswordAuthentication onAuthenticationChallenge(final String p0, final boolean p1, final boolean p2);
    
    byte[] onAuthenticationResponse(final byte[] p0);
}
