package javax.microedition.io;

import javax.microedition.pki.Certificate;

public interface SecurityInfo {
	String getCipherSuite();

	String getProtocolName();

	String getProtocolVersion();

	Certificate getServerCertificate();
}
