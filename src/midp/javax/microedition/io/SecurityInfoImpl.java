package javax.microedition.io;

import javax.microedition.pki.Certificate;
import javax.net.ssl.SSLSession;

class SecurityInfoImpl implements SecurityInfo {
	final SSLSession session;
	CertificateImpl certificate;

	SecurityInfoImpl(SSLSession handshakeSession) {
		this.session = handshakeSession;
		try {
			this.certificate = new CertificateImpl(handshakeSession.getPeerCertificateChain()[0]);
		} catch (Exception ignored) {}
	}

	public String getCipherSuite() {
		return session.getCipherSuite();
	}

	public String getProtocolName() {
		return session.getProtocol();
	}

	public String getProtocolVersion() {
		return session.getProtocol();
	}

	public Certificate getServerCertificate() {
		return certificate;
	}
}
