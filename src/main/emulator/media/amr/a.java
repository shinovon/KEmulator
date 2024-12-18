package emulator.media.amr;

import java.io.ByteArrayOutputStream;

public final class a {
	public static boolean aBoolean = true;
	public static final int[] arrayint;

	public a() {
		super();
	}

	public static byte[] method476(final byte[] array) throws Exception {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		byte[] arrayOfByte1 = null;
		int i = 0;
		try {
			c localc;
			if (a(localc = new c(array))) {
				if (aBoolean) {
					System.out.println("AMR mode");
				}
			} else {
				if (aBoolean) {
					System.out.println("Not AMR sound.");
				}
				return null;
			}
			for (; ; ) {
				try {
					int j = (byte) localc.a(1);
					if (aBoolean) {
						System.out.println("HeaderBit: " + j);
					}
				} catch (Exception localException1) {
					if (aBoolean) {
						System.out.println("No more frames left...");
					}
					break;
				}
				int j = (byte) localc.a(4);
				String localObject = "";
				switch (j) {
					case 0:
						if (aBoolean) {
							localObject = "AMR 4.75 kbit/s";
						}
						i = 0;
						break;
					case 1:
						if (aBoolean) {
							localObject = "AMR 5.15 kbit/s";
						}
						i = 0;
						break;
					case 2:
						if (aBoolean) {
							localObject = "AMR 5.90 kbit/s";
						}
						i = 0;
						break;
					case 3:
						if (aBoolean) {
							localObject = "AMR 6.70 kbit/s";
						}
						i = 0;
						break;
					case 4:
						if (aBoolean) {
							localObject = "AMR 7.40 kbit/s";
						}
						i = 0;
						break;
					case 5:
						if (aBoolean) {
							localObject = "AMR 7.95 kbit/s";
						}
						i = 0;
						break;
					case 6:
						if (aBoolean) {
							localObject = "AMR 10.2 kbit/s";
						}
						i = 0;
						break;
					case 7:
						if (aBoolean) {
							localObject = "AMR 12.2 kbit/s";
						}
						i = 0;
						break;
					case 8:
						if (aBoolean) {
							localObject = "DTX (SID)";
						}
						i = 1;
						break;
					case 15:
						if (aBoolean) {
							localObject = "No Data";
						}
						i = 3;
						break;
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					default:
						if (aBoolean) {
							localObject = "not defined=" + j;
						}
						break;
				}
				int k = 0;
				if (j != 15) {
					k = localc.a(1);
				}
				if (j == 15) {
					localc.a(3);
				} else {
					localc.a(2);
				}
				int m = arrayint[j];
				byte[] arrayOfByte3 = new byte[250];
				for (int n = 0; n < m; n++) {
					byte i1 = (byte) localc.a(1);
					if ((j == 8) && (n == 0)) {
						i = i1 == 0 ? 1 : 2;
					}
					arrayOfByte3[(n + 1)] = i1;
				}
				(arrayOfByte3 = a(arrayOfByte3, j))[0] = (byte) i;
				arrayOfByte3[245] = (byte) j;
				int n;
				if ((n = m % 8) > 0) {
					localc.a(8 - n);
				}
				localByteArrayOutputStream.write(arrayOfByte3);
				if (aBoolean) {
					System.out.println("Frame Type: " + localObject.toString() + ", " + arrayint[j] + " bits in speech frame.");
					if (j != 15) {
						String str = k != 1 ? "FQI: Bad frame." : "FQI: Good frame.";
						System.out.println(str);
					}
					String tmpTernaryOp;
					switch (i) {
						case 0:
							tmpTernaryOp = "TX_SPEECH_GOOD";
							break;
						case 1:
							tmpTernaryOp = "TX_SID_FIRST";
							break;
						case 2:
							tmpTernaryOp = "TX_SID_UPDATE";
							break;
						case 3:
							tmpTernaryOp = "TX_NO_DATA";
							break;
					}
					String str = "not defined!!!";
					System.out.println("TX_TYPE: " + str);
					System.out.println("\n");
				}
			}
			localByteArrayOutputStream.flush();
			byte[] arrayOfByte2;
			short[] shortarr = new short[(arrayOfByte2 = localByteArrayOutputStream.toByteArray()).length];
			for (int k = 0; k < arrayOfByte2.length; k++) {
				shortarr[k] = ((short) arrayOfByte2[k]);
			}
			if (aBoolean) {
				System.out.println("\nData to decoder (frames): " + shortarr.length / 250);
			}
			boolean bool;
			if ((bool = AMRDecoder.a())) {
				long l1 = System.currentTimeMillis();
				short[] arrayOfShort = AMRDecoder.decode(shortarr, shortarr.length / 250);
				long l2 = System.currentTimeMillis();
				if (aBoolean) {
					System.out.println("decoding time: " + (l2 - l1) + "ms");
				}
				arrayOfByte1 = new byte[arrayOfShort.length * 2];
				int i2 = 0;
				for (int i3 = 0; i3 < arrayOfShort.length; i3++) {
					arrayOfByte1[(i2++)] = ((byte) (arrayOfShort[i3] & 0xFF));
					arrayOfByte1[(i2++)] = ((byte) (arrayOfShort[i3] >> 8 & 0xFF));
				}
			}
		} catch (Exception localException2) {
			throw new Exception("Cannot parse this type of AMR", localException2);
		}
		return arrayOfByte1;
	}

	private static byte[] a(final byte[] array, final int n) {
		final byte[] array2 = new byte[array.length];
		final int[] method467;
		if ((method467 = b.method467(n)) == null) {
			return array;
		}
		for (int i = 0; i < method467.length; ++i) {
			array2[method467[i] + 1] = array[i + 1];
		}
		return array2;
	}

	private static boolean a(final c c) {
		int method465 = 0;
		for (int i = 0; i < 6; ++i) {
			method465 = c.a(8);
		}
		return method465 == 10;
	}

	static {
		a.aBoolean = false;
		arrayint = new int[]{95, 103, 118, 134, 148, 159, 204, 244, 39, 43, 38, 37, 0, 0, 0, 0};
	}
}
