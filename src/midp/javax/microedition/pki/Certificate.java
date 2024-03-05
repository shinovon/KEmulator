package javax.microedition.pki;

public interface Certificate {
    String getIssuer();

    long getNotAfter();

    long getNotBefore();

    String getSerialNumber();

    String getSigAlgName();

    String getSubject();

    String getType();

    String getVersion();
}
