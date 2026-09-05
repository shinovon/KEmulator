package emulator.media.amr;

import java.io.IOException;

/**
 * AMR-NB (narrowband) decoding front-end used by the media subsystem.
 *
 * <p>Since the removal of the native {@code amrdecoder} library this is backed by
 * {@link AmrNbDecoder}, a pure Java port of opencore-amr 0.1.6, so no platform
 * specific binaries have to be shipped or loaded anymore.</p>
 */
public final class AMRDecoder {

	/**
	 * The ported decoder keeps a fair amount of static scratch state, so only one
	 * decode may run at a time.
	 */
	private static final Object LOCK = new Object();

	private AMRDecoder() {
	}

	/**
	 * Decodes an AMR-NB stream (IETF storage format, with or without the
	 * {@code #!AMR\n} magic) into raw 16 bit signed little-endian PCM,
	 * 8000 Hz, mono.
	 *
	 * @param amrData the whole AMR file/stream contents
	 * @return the decoded PCM data, never {@code null}
	 * @throws IOException if the data cannot be decoded
	 */
	public static byte[] decode(byte[] amrData) throws IOException {
		if (amrData == null) {
			throw new IOException("No AMR data");
		}
		short[] pcm;
		try {
			synchronized (LOCK) {
				pcm = new AmrNbDecoder().decodeAll(amrData);
			}
		} catch (RuntimeException e) {
			throw new IOException("Failed to decode AMR data: " + e);
		}
		if (pcm == null || pcm.length == 0) {
			throw new IOException("No AMR frames decoded");
		}
		byte[] out = new byte[pcm.length * 2];
		for (int i = 0, j = 0; i < pcm.length; i++) {
			short s = pcm[i];
			out[j++] = (byte) (s & 0xFF);
			out[j++] = (byte) ((s >> 8) & 0xFF);
		}
		return out;
	}
}
