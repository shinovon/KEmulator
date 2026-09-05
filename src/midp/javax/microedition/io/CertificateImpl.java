package javax.microedition.io;

import javax.microedition.pki.Certificate;
import javax.security.cert.X509Certificate;

class CertificateImpl implements Certificate {

	final X509Certificate certificate;

	CertificateImpl(X509Certificate certificate) {
		this.certificate = certificate;
	}

	public String getIssuer() {
		return certificate.getIssuerDN().getName();
	}

	public long getNotAfter() {
		return certificate.getNotAfter().getTime();
	}

	public long getNotBefore() {
		return certificate.getNotBefore().getTime();
	}

	public String getSerialNumber() {
		return certificate.getSerialNumber().toString();
	}

	public String getSigAlgName() {
		return certificate.getSigAlgName();
	}

	public String getSubject() {
		return certificate.getSubjectDN().getName();
	}

	public String getType() {
		// TODO
		return "";
	}

	public String getVersion() {
		return Integer.toString(certificate.getVersion());
	}
}
