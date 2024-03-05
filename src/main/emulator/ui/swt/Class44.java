package emulator.ui.swt;

import java.net.*;

final class MyAuthenticator extends Authenticator {
    private String aString780;
    private String aString781;

    MyAuthenticator(final Property class38, final String aString780, final String aString781) {
        super();
        this.aString780 = aString780;
        this.aString781 = aString781;
    }

    public final PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.aString780, this.aString781.toCharArray());
    }
}
