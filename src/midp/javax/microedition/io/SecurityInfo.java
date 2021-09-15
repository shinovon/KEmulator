package javax.microedition.io;

import javax.microedition.pki.*;

public interface SecurityInfo
{
    String getCipherSuite();
    
    String getProtocolName();
    
    String getProtocolVersion();
    
    Certificate getServerCertificate();
}
