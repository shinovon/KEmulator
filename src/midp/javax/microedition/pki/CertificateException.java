package javax.microedition.pki;

import java.io.IOException;

public class CertificateException extends IOException {
	private static final long serialVersionUID = 1L;
	private byte aByte1501;
	private Certificate aCertificate1502;
	public static final byte BAD_EXTENSIONS = 1;
	public static final byte CERTIFICATE_CHAIN_TOO_LONG = 2;
	public static final byte EXPIRED = 3;
	public static final byte UNAUTHORIZED_INTERMEDIATE_CA = 4;
	public static final byte MISSING_SIGNATURE = 5;
	public static final byte NOT_YET_VALID = 6;
	public static final byte SITENAME_MISMATCH = 7;
	public static final byte UNRECOGNIZED_ISSUER = 8;
	public static final byte UNSUPPORTED_SIGALG = 9;
	public static final byte INAPPROPRIATE_KEY_USAGE = 10;
	public static final byte BROKEN_CHAIN = 11;
	public static final byte ROOT_CA_EXPIRED = 12;
	public static final byte UNSUPPORTED_PUBLIC_KEY_TYPE = 13;
	public static final byte VERIFICATION_FAILED = 14;

	public CertificateException(final Certificate aCertificate1502, final byte aByte1501) {
		super(method886(aByte1501));
		this.aCertificate1502 = aCertificate1502;
		this.aByte1501 = aByte1501;
	}

	public CertificateException(final String s, final Certificate aCertificate1502, final byte aByte1501) {
		super(s);
		this.aCertificate1502 = aCertificate1502;
		this.aByte1501 = aByte1501;
	}

	public Certificate getCertificate() {
		return this.aCertificate1502;
	}

	public byte getReason() {
		return this.aByte1501;
	}

	static String method886(final int n) {
		switch (n) {
			case 1: {
				return "Certificate has unrecognized critical extensions";
			}
			case 2: {
				return "Server certificate chain exceeds the length allowed by an issuer's policy";
			}
			case 3: {
				return "Certificate is expired";
			}
			case 4: {
				return "Intermediate certificate in the chain does not have the authority to be an intermediate CA";
			}
			case 5: {
				return "Certificate object does not contain a signature";
			}
			case 6: {
				return "Certificate is not yet valid";
			}
			case 7: {
				return "Certificate does not contain the correct site name";
			}
			case 8: {
				return "Certificate was issued by an unrecognized entity";
			}
			case 9: {
				return "Certificate was signed using an unsupported algorithm";
			}
			case 10: {
				return "Certificate's public key has been used in a way deemed inappropriate by the issuer";
			}
			case 11: {
				return "Certificate in a chain was not issued by the next authority in the chain";
			}
			case 12: {
				return "Root CA's public key is expired";
			}
			case 13: {
				return "Certificate has a public key that is not a supported type";
			}
			case 14: {
				return "Certificate failed verification";
			}
			default: {
				return "Unknown reason (" + n + ")";
			}
		}
	}
}
