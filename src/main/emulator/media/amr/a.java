package emulator.media.amr;

import java.io.*;

public final class a
{
    public static boolean aBoolean = true;
    public static final int[] arrayint;
    
    public a() {
        super();
    }
    
    public static byte[] method476(final byte[] array) throws Exception/* {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] array2 = null;
        int n = 0;
        try {
            final c c;
            if (!a(c = new c(array))) {
                if (a.aBoolean849) {
                    System.out.println("Not AMR sound.");
                }
                return null;
            }
            if (a.aBoolean849) {
                System.out.println("ARM mode");
            }
            byte b;
            byte[] byteArray;
            short[] array3;
            long currentTimeMillis;
            short[] decode;
            long currentTimeMillis2;
            int n2;
            String s;
            byte[] array4;
            byte b2;
            byte[] method477;
            int n4;
            final int n3;
            String string;
            int n5;
            byte b3;
            int method478;
            Label_0690_Outer:Label_0394_Outer:Block_25_Outer:
            while (true) {
                try {
                    b = (byte)c.method465(1);
                    if (a.aBoolean849) {
                        System.out.println("HeaderBit: " + b);
                    }
                }
                catch (Exception ex) {
                    if (a.aBoolean849) {
                        System.out.println("No more frames left...");
                    }
                    byteArrayOutputStream.flush();
                    array3 = new short[(byteArray = byteArrayOutputStream.toByteArray()).length];
                    for (int i = 0; i < byteArray.length; ++i) {
                        array3[i] = byteArray[i];
                    }
                    if (a.aBoolean849) {
                        System.out.println("\nData to decoder (frames): " + array3.length / 250);
                    }
                    if (AMRDecoder.method221()) {
                        currentTimeMillis = System.currentTimeMillis();
                        decode = AMRDecoder.decode(array3, array3.length / 250);
                        currentTimeMillis2 = System.currentTimeMillis();
                        if (a.aBoolean849) {
                            System.out.println("decoding time: " + (currentTimeMillis2 - currentTimeMillis) + "ms");
                        }
                        array2 = new byte[decode.length * 2];
                        n2 = 0;
                        for (int j = 0; j < decode.length; ++j) {
                            array2[n2++] = (byte)(decode[j] & 0xFF);
                            array2[n2++] = (byte)(decode[j] >> 8 & 0xFF);
                        }
                    }
                    return array2;
                    // iftrue(Label_0394:, !a.aBoolean849)
                    // iftrue(Label_0286:, !a.aBoolean849)
                    // iftrue(Label_0551:, n3 = n4 % 8 <= 0)
                    // iftrue(Label_0726:, !a.aBoolean849)
                    // iftrue(Label_0226:, !a.aBoolean849)
                    // iftrue(Label_0331:, !a.aBoolean849)
                    // iftrue(Label_0361:, !a.aBoolean849)
                    // iftrue(Label_0508:, n5 >= n4)
                    // switch([Lcom.strobel.decompiler.ast.Label;@cd99df9, b2)
                    // iftrue(Label_0412:, b2 == 15)
                    // iftrue(Label_0316:, !a.aBoolean849)
                    // iftrue(Label_0271:, !a.aBoolean849)
                    // iftrue(Label_0429:, b2 != 15)
                    // iftrue(Label_0638:, b2 == 15)
                    // switch([Lcom.strobel.decompiler.ast.Label;@428c120c, n)
                    // iftrue(Label_0346:, !a.aBoolean849)
                    // iftrue(Label_0301:, !a.aBoolean849)
                    // iftrue(Label_0256:, !a.aBoolean849)
                    // iftrue(Label_0241:, !a.aBoolean849)
                    // iftrue(Label_0493:, b2 != 8 || n5 != 0)
                    Block_16: {
                        while (true) {
                        Label_0394:
                            while (true) {
                                Block_18_Outer:Block_11_Outer:
                                while (true) {
                                    Label_0436_Outer:Label_0301_Outer:Block_12_Outer:
                                    while (true) {
                                        Block_20: {
                                            break Block_20;
                                        Label_0276:
                                            while (true) {
                                            Block_15_Outer:
                                                while (true) {
                                                    while (true) {
                                                        while (true) {
                                                            Block_29: {
                                                                while (true) {
                                                                Label_0436:
                                                                    while (true) {
                                                                        Block_19_Outer:Block_13_Outer:Label_0226_Outer:Block_22_Outer:
                                                                        while (true) {
                                                                            Block_28: {
                                                                                while (true) {
                                                                                    while (true) {
                                                                                        Block_10: {
                                                                                            while (true) {
                                                                                                while (true) {
                                                                                                    Block_14: {
                                                                                                        break Block_14;
                                                                                                        Label_0688: {
                                                                                                            s = "not defined!!!";
                                                                                                        }
                                                                                                        break Label_0690;
                                                                                                        Label_0683:
                                                                                                        s = "TX_NO_DATA";
                                                                                                        break Label_0690;
                                                                                                        Label_0508:
                                                                                                        (method477 = a(array4, b2))[0] = (byte)n;
                                                                                                        method477[245] = b2;
                                                                                                    Label_0346:
                                                                                                        while (true) {
                                                                                                            Block_27: {
                                                                                                                break Block_27;
                                                                                                                byteArrayOutputStream.write(method477);
                                                                                                                break Block_28;
                                                                                                                string = "DTX (SID)";
                                                                                                                break Label_0346;
                                                                                                            }
                                                                                                            c.method465(8 - n3);
                                                                                                            continue Block_18_Outer;
                                                                                                        }
                                                                                                        n = 1;
                                                                                                        break Label_0394;
                                                                                                        Label_0216:
                                                                                                        break Block_10;
                                                                                                        Label_0321:
                                                                                                        Label_0726: {
                                                                                                            Label_0331: {
                                                                                                                while (true) {
                                                                                                                    Label_0361: {
                                                                                                                        while (true) {
                                                                                                                            Block_17: {
                                                                                                                                break Block_17;
                                                                                                                                while (true) {
                                                                                                                                    n = 0;
                                                                                                                                    break Label_0394;
                                                                                                                                    string = "No Data";
                                                                                                                                    break Label_0361;
                                                                                                                                    string = "AMR 6.70 kbit/s";
                                                                                                                                    continue Block_19_Outer;
                                                                                                                                }
                                                                                                                                n = 0;
                                                                                                                                break Label_0394;
                                                                                                                                Label_0668:
                                                                                                                                s = "TX_SPEECH_GOOD";
                                                                                                                                break Label_0690;
                                                                                                                                while (true) {
                                                                                                                                    n = 0;
                                                                                                                                    break Label_0394;
                                                                                                                                    string = "AMR 5.15 kbit/s";
                                                                                                                                    continue Block_11_Outer;
                                                                                                                                }
                                                                                                                            }
                                                                                                                            string = "AMR 12.2 kbit/s";
                                                                                                                            break Label_0331;
                                                                                                                            System.out.println("TX_TYPE: " + s);
                                                                                                                            System.out.println("\n");
                                                                                                                            break Label_0726;
                                                                                                                            Label_0351:
                                                                                                                            continue Block_13_Outer;
                                                                                                                        }
                                                                                                                        break Label_0394;
                                                                                                                        n4 = a.anIntArray850[b2];
                                                                                                                        array4 = new byte[250];
                                                                                                                        n5 = 0;
                                                                                                                        continue Label_0436_Outer;
                                                                                                                    }
                                                                                                                    n = 3;
                                                                                                                    break Label_0394;
                                                                                                                    array4[n5 + 1] = b3;
                                                                                                                    ++n5;
                                                                                                                    continue Label_0436_Outer;
                                                                                                                }
                                                                                                                c.method465(3);
                                                                                                                continue Label_0436;
                                                                                                                b2 = (byte)c.method465(4);
                                                                                                                string = "";
                                                                                                                method478 = 0;
                                                                                                                break Block_15_Outer;
                                                                                                                Label_0306:
                                                                                                                break Block_16;
                                                                                                                n = 0;
                                                                                                                continue Label_0394;
                                                                                                            }
                                                                                                            n = 0;
                                                                                                            continue Label_0394;
                                                                                                        }
                                                                                                        continue Label_0690_Outer;
                                                                                                    }
                                                                                                    string = "AMR 7.40 kbit/s";
                                                                                                    break Block_18_Outer;
                                                                                                    string = "AMR 5.90 kbit/s";
                                                                                                    break Block_19_Outer;
                                                                                                    Label_0261: {
                                                                                                        continue Label_0226_Outer;
                                                                                                    }
                                                                                                }
                                                                                                n = ((b3 == 0) ? 1 : 2);
                                                                                                continue Block_22_Outer;
                                                                                            }
                                                                                        }
                                                                                        string = "AMR 4.75 kbit/s";
                                                                                        continue Block_11_Outer;
                                                                                    }
                                                                                    continue Label_0301_Outer;
                                                                                }
                                                                            }
                                                                            System.out.println("Frame Type: " + string + ", " + a.anIntArray850[b2] + " bits in speech frame.");
                                                                            break Block_29;
                                                                            Label_0316: {
                                                                                n = 0;
                                                                            }
                                                                            continue Label_0394;
                                                                            Label_0336:
                                                                            continue Label_0394_Outer;
                                                                        }
                                                                        n = 0;
                                                                        continue Label_0394;
                                                                        Label_0429: {
                                                                            c.method465(2);
                                                                        }
                                                                        continue Label_0436;
                                                                    }
                                                                    string = "AMR 7.95 kbit/s";
                                                                    continue Block_12_Outer;
                                                                }
                                                                Label_0673: {
                                                                    s = "TX_SID_FIRST";
                                                                }
                                                                continue Block_18_Outer;
                                                            }
                                                            System.out.println((method478 != 1) ? "FQI: Bad frame." : "FQI: Good frame.");
                                                            continue Block_15_Outer;
                                                        }
                                                        Label_0291: {
                                                            continue;
                                                        }
                                                    }
                                                    Label_0246: {
                                                        continue Block_25_Outer;
                                                    }
                                                }
                                                method478 = c.method465(1);
                                                continue;
                                            }
                                        }
                                        string = "not defined=" + b2;
                                        continue Label_0394;
                                        Label_0231: {
                                            continue Block_25_Outer;
                                        }
                                    }
                                    Label_0678: {
                                        s = "TX_SID_UPDATE";
                                    }
                                    continue Label_0394_Outer;
                                }
                                n = 0;
                                continue Label_0394;
                            }
                            b3 = (byte)c.method465(1);
                            continue;
                        }
                    }
                    string = "AMR 10.2 kbit/s";
                }
                break;
            }
        }
        catch (Exception ex2) {
            throw new Exception("Cannot parse this type of AMR");
        }
        return array2;
    }*/
    {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte1 = null;
        int i = 0;
        try
        {
          c localc;
		if (a(localc = new c(array)))
          {
            if (aBoolean) {
              System.out.println("AMR mode");
            }
          }
          else
          {
            if (aBoolean) {
              System.out.println("Not AMR sound.");
            }
            return null;
          }
          for (;;)
          {
            try
            {
              int j = (byte)localc.a(1);
              if (aBoolean) {
                System.out.println("HeaderBit: " + j);
              }
            }
            catch (Exception localException1)
            {
              if (aBoolean) {
                System.out.println("No more frames left...");
              }
              break;
            }
            int j = (byte)localc.a(4);
            String localObject = "";
            switch (j)
            {
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
            for (int n = 0; n < m; n++)
            {
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
            if (aBoolean)
            {
              System.out.println("Frame Type: " + localObject.toString() + ", " + arrayint[j] + " bits in speech frame.");
              if (j != 15)
              {
                String str = k != 1 ? "FQI: Bad frame." : "FQI: Good frame.";
                System.out.println(str);
              }
              String tmpTernaryOp;
			switch (i)
              {
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
        	  shortarr[k] = ((short)arrayOfByte2[k]);
          }
          if (aBoolean) {
            System.out.println("\nData to decoder (frames): " + shortarr.length / 250);
          }
          boolean bool;
          if ((bool = AMRDecoder.a()))
          {
            long l1 = System.currentTimeMillis();
            short[] arrayOfShort = AMRDecoder.decode(shortarr, shortarr.length / 250);
            long l2 = System.currentTimeMillis();
            if (aBoolean) {
              System.out.println("decoding time: " + (l2 - l1) + "ms");
            }
            arrayOfByte1 = new byte[arrayOfShort.length * 2];
            int i2 = 0;
            for (int i3 = 0; i3 < arrayOfShort.length; i3++)
            {
              arrayOfByte1[(i2++)] = ((byte)(arrayOfShort[i3] & 0xFF));
              arrayOfByte1[(i2++)] = ((byte)(arrayOfShort[i3] >> 8 & 0xFF));
            }
          }
        }
        catch (Exception localException2)
        {
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
        arrayint = new int[] { 95, 103, 118, 134, 148, 159, 204, 244, 39, 43, 38, 37, 0, 0, 0, 0 };
    }
}
