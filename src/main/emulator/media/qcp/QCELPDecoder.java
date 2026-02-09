package emulator.media.qcp;

/**
 * QCELP-13K decoder - accurate port from FFmpeg
 */
public class QCELPDecoder implements FrameDecoder {

    private static final int SAMPLES_PER_FRAME = 160;
    private static final int LPC_ORDER = 10;
    private static final float QCELP_SCALE = 8192.0f;

    // LSP VQ tables from IS-733 (values are x*10000, multiply by 0.0001)
    private static final int[][] LSPVQ1 = {
            {327, 118}, {919, 111}, {427, 440}, {1327, 185},
            {469, 50}, {1272, 91}, {892, 59}, {1771, 193},
            {222, 158}, {1100, 127}, {827, 55}, {978, 791},
            {665, 47}, {700, 1401}, {670, 859}, {1913, 1048},
            {471, 215}, {1046, 125}, {645, 298}, {1599, 160},
            {593, 39}, {1187, 462}, {749, 341}, {1520, 511},
            {290, 792}, {909, 362}, {753, 81}, {1111, 1058},
            {519, 253}, {828, 839}, {685, 541}, {1421, 1258},
            {386, 130}, {962, 119}, {542, 387}, {1431, 185},
            {526, 51}, {1175, 260}, {831, 167}, {1728, 510},
            {273, 437}, {1172, 113}, {771, 144}, {1122, 751},
            {619, 119}, {492, 1276}, {658, 695}, {1882, 615},
            {415, 200}, {1018, 88}, {681, 339}, {1436, 325},
            {555, 122}, {1042, 485}, {826, 345}, {1374, 743},
            {383, 1018}, {1005, 358}, {704, 86}, {1301, 586},
            {597, 241}, {832, 621}, {555, 573}, {1504, 839}
    };

    private static final int[][] LSPVQ2 = {
            {255, 293}, {904, 219}, {151, 1211}, {1447, 498},
            {470, 253}, {1559, 177}, {1547, 994}, {2394, 242},
            {91, 813}, {857, 590}, {934, 1326}, {1889, 282},
            {813, 472}, {1057, 1494}, {450, 3315}, {2163, 1895},
            {538, 532}, {1399, 218}, {146, 1552}, {1755, 626},
            {822, 202}, {1299, 663}, {706, 1732}, {2656, 401},
            {418, 745}, {762, 1038}, {583, 1748}, {1746, 1285},
            {527, 1169}, {1314, 830}, {556, 2116}, {1073, 2321},
            {297, 570}, {981, 403}, {468, 1103}, {1740, 243},
            {725, 179}, {1255, 474}, {1374, 1362}, {1922, 912},
            {285, 947}, {930, 700}, {593, 1372}, {1909, 576},
            {588, 916}, {1110, 1116}, {224, 2719}, {1633, 2220},
            {402, 520}, {1061, 448}, {402, 1352}, {1499, 775},
            {664, 589}, {1081, 727}, {801, 2206}, {2165, 1157},
            {566, 802}, {911, 1116}, {306, 1703}, {1792, 836},
            {655, 999}, {1061, 1038}, {298, 2089}, {1110, 1753},
            {361, 311}, {970, 239}, {265, 1231}, {1495, 573},
            {566, 262}, {1569, 293}, {1341, 1144}, {2271, 544},
            {214, 877}, {847, 719}, {794, 1384}, {2067, 274},
            {703, 688}, {1099, 1306}, {391, 2947}, {2024, 1670},
            {471, 525}, {1245, 290}, {264, 1557}, {1568, 807},
            {718, 399}, {1193, 685}, {883, 1594}, {2729, 764},
            {500, 754}, {809, 1108}, {541, 1648}, {1523, 1385},
            {614, 1196}, {1209, 847}, {345, 2242}, {1442, 1747},
            {199, 560}, {1092, 194}, {349, 1253}, {1653, 507},
            {625, 354}, {1376, 431}, {1187, 1465}, {2164, 872},
            {360, 974}, {1008, 698}, {704, 1346}, {2114, 452},
            {720, 816}, {1240, 1089}, {439, 2475}, {1498, 2040},
            {336, 718}, {1213, 187}, {451, 1450}, {1368, 885},
            {592, 578}, {1131, 531}, {861, 1855}, {1764, 1500},
            {444, 970}, {935, 903}, {424, 1687}, {1633, 1102},
            {793, 897}, {1060, 897}, {185, 2011}, {1205, 1855}
    };

    private static final int[][] LSPVQ3 = {
            {225, 283}, {1296, 355}, {543, 343}, {2073, 274},
            {204, 1099}, {1562, 523}, {1388, 161}, {2784, 274},
            {112, 849}, {1870, 175}, {1189, 160}, {1490, 1088},
            {969, 1115}, {659, 3322}, {1158, 1073}, {3183, 1363},
            {517, 223}, {1740, 223}, {704, 387}, {2637, 234},
            {692, 1005}, {1287, 1610}, {952, 532}, {2393, 646},
            {490, 552}, {1619, 657}, {845, 670}, {1784, 2280},
            {191, 1775}, {272, 2868}, {942, 952}, {2628, 1479},
            {278, 579}, {1565, 218}, {814, 180}, {2379, 187},
            {276, 1444}, {1199, 1223}, {1200, 349}, {3009, 307},
            {312, 844}, {1898, 306}, {863, 470}, {1685, 1241},
            {513, 1727}, {711, 2233}, {1085, 864}, {3398, 527},
            {414, 440}, {1356, 612}, {964, 147}, {2173, 738},
            {465, 1292}, {877, 1749}, {1104, 689}, {2105, 1311},
            {580, 864}, {1895, 752}, {652, 609}, {1485, 1699},
            {514, 1400}, {386, 2131}, {933, 798}, {2473, 986},
            {334, 360}, {1375, 398}, {621, 276}, {2183, 280},
            {311, 1114}, {1382, 807}, {1284, 175}, {2605, 636},
            {230, 816}, {1739, 408}, {1074, 176}, {1619, 1120},
            {784, 1371}, {448, 3050}, {1189, 880}, {3039, 1165},
            {424, 241}, {1672, 186}, {815, 333}, {2432, 324},
            {584, 1029}, {1137, 1546}, {1015, 585}, {2198, 995},
            {574, 581}, {1746, 647}, {733, 740}, {1938, 1737},
            {347, 1710}, {373, 2429}, {787, 1061}, {2439, 1438},
            {185, 536}, {1489, 178}, {703, 216}, {2178, 487},
            {154, 1421}, {1414, 994}, {1103, 352}, {3072, 473},
            {408, 819}, {2055, 168}, {998, 354}, {1917, 1140},
            {665, 1799}, {993, 2213}, {1234, 631}, {3003, 762},
            {373, 620}, {1518, 425}, {913, 300}, {1966, 836},
            {402, 1185}, {948, 1385}, {1121, 555}, {1802, 1509},
            {474, 886}, {1888, 610}, {739, 585}, {1231, 2379},
            {661, 1335}, {205, 2211}, {823, 822}, {2480, 1179}
    };

    private static final int[][] LSPVQ4 = {
            {348, 311}, {812, 1145}, {552, 461}, {1826, 263},
            {601, 675}, {1730, 172}, {1523, 193}, {2449, 277},
            {334, 668}, {805, 1441}, {1319, 207}, {1684, 910},
            {582, 1318}, {1403, 1098}, {979, 832}, {2700, 1359},
            {624, 228}, {1292, 979}, {800, 195}, {2226, 285},
            {730, 862}, {1537, 601}, {1115, 509}, {2720, 354},
            {218, 1167}, {1212, 1538}, {1074, 247}, {1674, 1710},
            {322, 2142}, {1263, 777}, {981, 556}, {2119, 1710},
            {193, 596}, {1035, 957}, {694, 397}, {1997, 253},
            {743, 603}, {1584, 321}, {1346, 346}, {2221, 708},
            {451, 732}, {1040, 1415}, {1184, 230}, {1853, 919},
            {310, 1661}, {1625, 706}, {856, 843}, {2902, 702},
            {467, 348}, {1108, 1048}, {859, 306}, {1964, 463},
            {560, 1013}, {1425, 533}, {1142, 634}, {2391, 879},
            {397, 1084}, {1345, 1700}, {976, 248}, {1887, 1189},
            {644, 2087}, {1262, 603}, {877, 550}, {2203, 1307}
    };

    private static final int[][] LSPVQ5 = {
            {360, 222}, {820, 1097}, {601, 319}, {1656, 198},
            {604, 513}, {1552, 141}, {1391, 155}, {2474, 261},
            {269, 785}, {1463, 646}, {1123, 191}, {2015, 223},
            {785, 844}, {1202, 1011}, {980, 807}, {3014, 793},
            {570, 180}, {1135, 1382}, {778, 256}, {1901, 179},
            {807, 622}, {1461, 458}, {1231, 178}, {2028, 821},
            {387, 927}, {1496, 1004}, {888, 392}, {2246, 341},
            {295, 1462}, {1156, 694}, {1022, 473}, {2226, 1364},
            {210, 478}, {1029, 1020}, {722, 181}, {1730, 251},
            {730, 488}, {1465, 293}, {1303, 326}, {2595, 387},
            {458, 584}, {1569, 742}, {1029, 173}, {1910, 495},
            {605, 1159}, {1268, 719}, {973, 646}, {2872, 428},
            {443, 334}, {835, 1465}, {912, 138}, {1716, 442},
            {620, 778}, {1316, 450}, {1186, 335}, {1446, 1665},
            {486, 1050}, {1675, 1019}, {880, 278}, {2214, 202},
            {539, 1564}, {1142, 533}, {984, 391}, {2130, 1089}
    };

    private static final int[][][] LSPVQ = {LSPVQ1, LSPVQ2, LSPVQ3, LSPVQ4, LSPVQ5};

    // Full rate codebook (values in x*100)
    private static final int[] RATE_FULL_CODEBOOK = {
            10, -65, -59, 12, 110, 34, -134, 157,
            104, -84, -34, -115, 23, -101, 3, 45,
            -101, -16, -59, 28, -45, 134, -67, 22,
            61, -29, 226, -26, -55, -179, 157, -51,
            -220, -93, -37, 60, 118, 74, -48, -95,
            -181, 111, 36, -52, -215, 78, -112, 39,
            -17, -47, -223, 19, 12, -98, -142, 130,
            54, -127, 21, -12, 39, -48, 12, 128,
            6, -167, 82, -102, -79, 55, -44, 48,
            -20, -53, 8, -61, 11, -70, -157, -168,
            20, -56, -74, 78, 33, -63, -173, -2,
            -75, -53, -146, 77, 66, -29, 9, -75,
            65, 119, -43, 76, 233, 98, 125, -156,
            -27, 78, -9, 170, 176, 143, -148, -7,
            27, -136, 5, 27, 18, 139, 204, 7,
            -184, -197, 52, -3, 78, -189, 8, -65
    };

    // Half rate codebook (values in x*2)
    private static final int[] RATE_HALF_CODEBOOK = {
            0, -4, 0, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, -3, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
            0, 0, 0, 0, 0, 0, 4, 0, 0, 3, 2, 0, 3, 4, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0,
            -3, 3, 0, 0, -2, 0, 3, 0, 0, 0, 0, 0, 0, 0, -5, 0,
            0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 4,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 6, -3, -4, 0, -3, -3,
            3, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    // Gain table (already scaled by 1/8192)
    private static final float[] G12GA = {
            1.000f / QCELP_SCALE, 1.125f / QCELP_SCALE, 1.250f / QCELP_SCALE, 1.375f / QCELP_SCALE,
            1.625f / QCELP_SCALE, 1.750f / QCELP_SCALE, 2.000f / QCELP_SCALE, 2.250f / QCELP_SCALE,
            2.500f / QCELP_SCALE, 2.875f / QCELP_SCALE, 3.125f / QCELP_SCALE, 3.500f / QCELP_SCALE,
            4.000f / QCELP_SCALE, 4.500f / QCELP_SCALE, 5.000f / QCELP_SCALE, 5.625f / QCELP_SCALE,
            6.250f / QCELP_SCALE, 7.125f / QCELP_SCALE, 8.000f / QCELP_SCALE, 8.875f / QCELP_SCALE,
            10.000f / QCELP_SCALE, 11.250f / QCELP_SCALE, 12.625f / QCELP_SCALE, 14.125f / QCELP_SCALE,
            15.875f / QCELP_SCALE, 17.750f / QCELP_SCALE, 20.000f / QCELP_SCALE, 22.375f / QCELP_SCALE,
            25.125f / QCELP_SCALE, 28.125f / QCELP_SCALE, 31.625f / QCELP_SCALE, 35.500f / QCELP_SCALE,
            39.750f / QCELP_SCALE, 44.625f / QCELP_SCALE, 50.125f / QCELP_SCALE, 56.250f / QCELP_SCALE,
            63.125f / QCELP_SCALE, 70.750f / QCELP_SCALE, 79.375f / QCELP_SCALE, 89.125f / QCELP_SCALE,
            100.000f / QCELP_SCALE, 112.250f / QCELP_SCALE, 125.875f / QCELP_SCALE, 141.250f / QCELP_SCALE,
            158.500f / QCELP_SCALE, 177.875f / QCELP_SCALE, 199.500f / QCELP_SCALE, 223.875f / QCELP_SCALE,
            251.250f / QCELP_SCALE, 281.875f / QCELP_SCALE, 316.250f / QCELP_SCALE, 354.875f / QCELP_SCALE,
            398.125f / QCELP_SCALE, 446.625f / QCELP_SCALE, 501.125f / QCELP_SCALE, 562.375f / QCELP_SCALE,
            631.000f / QCELP_SCALE, 708.000f / QCELP_SCALE, 794.375f / QCELP_SCALE, 891.250f / QCELP_SCALE,
            1000.000f / QCELP_SCALE
    };

    private static final float[] HAMMSINC = {-0.006822f, 0.041249f, -0.143459f, 0.588863f};
    private static final float BANDWIDTH_EXPANSION = 0.9883f;
    private static final float LSP_SPREAD = 0.02f;

    // Decoder state
    private final float[] prevLspf = new float[LPC_ORDER];
    private final float[] lpc = new float[LPC_ORDER + 1];
    private final float[] pitchSynthMem = new float[303];
    private final float[] pitchPreMem = new float[303];
    private final float[] formantMem = new float[170];
    private final float[] pitchGain = new float[4];
    private final int[] pitchLag = new int[4];
    private final int[] pfrac = new int[4];
    private float lastCbGain = 0;
    private int[] prevG1 = {0, 0};
    private int prevBitrate = 0;

    // Unpacked frame
    private final int[] lspv = new int[10];
    private final int[] plag = new int[4];
    private final int[] pgain = new int[4];
    private final int[] cindex = new int[16];
    private final int[] cbgain = new int[16];
    private final int[] cbsign = new int[16];

    public QCELPDecoder() {
        reset();
    }

    @Override
    public void reset() {
        for (int i = 0; i < LPC_ORDER; i++) {
            prevLspf[i] = (i + 1) / 11.0f;
        }
        java.util.Arrays.fill(pitchSynthMem, 0);
        java.util.Arrays.fill(pitchPreMem, 0);
        java.util.Arrays.fill(formantMem, 0);
        java.util.Arrays.fill(pitchGain, 0);
        java.util.Arrays.fill(pitchLag, 0);
        lpc[0] = 1.0f;
        for (int i = 1; i <= LPC_ORDER; i++) lpc[i] = 0;
        lastCbGain = 0;
        prevG1[0] = prevG1[1] = 0;
        prevBitrate = 0;
    }

    @Override
    public void decodeFrame(byte[] data, short[] output) {
        if (data == null || data.length < 1) {
            erasure(output);
            return;
        }

        int rate = data[0] & 0x0F;

        // Clear frame data
        java.util.Arrays.fill(lspv, 0);
        java.util.Arrays.fill(plag, 0);
        java.util.Arrays.fill(pfrac, 0);
        java.util.Arrays.fill(pgain, 0);
        java.util.Arrays.fill(cindex, 0);
        java.util.Arrays.fill(cbgain, 0);
        java.util.Arrays.fill(cbsign, 0);

        switch (rate) {
            case 4: // RATE_FULL
                if (data.length < 35) {
                    erasure(output);
                    return;
                }
                unpackFullRate(data);
                decodeFullRate(output);
                break;
            case 3: // RATE_HALF
                if (data.length < 17) {
                    erasure(output);
                    return;
                }
                unpackHalfRate(data);
                decodeHalfRate(output);
                break;
            case 2: // RATE_QUARTER
                if (data.length < 8) {
                    erasure(output);
                    return;
                }
                unpackQuarterRate(data);
                decodeQuarterRate(output);
                break;
            case 1: // RATE_OCTAVE
                if (data.length < 4) {
                    erasure(output);
                    return;
                }
                unpackOctaveRate(data);
                decodeOctaveRate(data, output);
                break;
            default:
                erasure(output);
                return;
        }
        prevBitrate = rate;
    }

    // Bit unpacker using FFmpeg bitmap order
    private void unpackFullRate(byte[] data) {
        int[] bits = bytesToBits(data, 1, 34);
        int pos = 0;

        // Follow the bitmap order from qcelpdata.h
        lspv[2] |= getBitsFromArray(bits, pos, 3); pos += 3;
        lspv[1] = getBitsFromArray(bits, pos, 7); pos += 7;
        lspv[0] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[4] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[3] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[2] |= getBitsFromArray(bits, pos, 4) << 3; pos += 4;

        cbsign[0] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[0] = getBitsFromArray(bits, pos, 4); pos += 4;
        pfrac[0] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[0] = getBitsFromArray(bits, pos, 7); pos += 7;
        pgain[0] = getBitsFromArray(bits, pos, 3); pos += 3;

        cindex[1] |= getBitsFromArray(bits, pos, 4); pos += 4;
        cbsign[1] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[1] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[0] = getBitsFromArray(bits, pos, 7); pos += 7;

        cbgain[3] |= getBitsFromArray(bits, pos, 1); pos += 1;
        cindex[2] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[2] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[2] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[1] |= getBitsFromArray(bits, pos, 3) << 4; pos += 3;

        plag[1] |= getBitsFromArray(bits, pos, 3); pos += 3;
        pgain[1] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[3] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[3] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[3] |= getBitsFromArray(bits, pos, 2) << 1; pos += 2;

        cindex[4] |= getBitsFromArray(bits, pos, 6); pos += 6;
        cbsign[4] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[4] = getBitsFromArray(bits, pos, 4); pos += 4;
        pfrac[1] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[1] |= getBitsFromArray(bits, pos, 4) << 3; pos += 4;

        cbgain[6] |= getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[5] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[5] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[5] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[4] |= getBitsFromArray(bits, pos, 1) << 6; pos += 1;

        cindex[7] |= getBitsFromArray(bits, pos, 3); pos += 3;
        cbsign[7] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[7] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[6] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[6] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[6] |= getBitsFromArray(bits, pos, 1) << 3; pos += 1;

        cbgain[8] |= getBitsFromArray(bits, pos, 1); pos += 1;
        pfrac[2] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[2] = getBitsFromArray(bits, pos, 7); pos += 7;
        pgain[2] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[7] |= getBitsFromArray(bits, pos, 4) << 3; pos += 4;

        cbsign[9] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[9] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[8] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[8] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[8] |= getBitsFromArray(bits, pos, 3) << 1; pos += 3;

        cindex[10] |= getBitsFromArray(bits, pos, 4); pos += 4;
        cbsign[10] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[10] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[9] = getBitsFromArray(bits, pos, 7); pos += 7;

        pgain[3] |= getBitsFromArray(bits, pos, 2); pos += 2;
        cindex[11] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[11] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[11] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[10] |= getBitsFromArray(bits, pos, 3) << 4; pos += 3;

        cindex[12] |= getBitsFromArray(bits, pos, 2); pos += 2;
        cbsign[12] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[12] = getBitsFromArray(bits, pos, 4); pos += 4;
        pfrac[3] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[3] = getBitsFromArray(bits, pos, 7); pos += 7;
        pgain[3] |= getBitsFromArray(bits, pos, 1) << 2; pos += 1;

        cindex[13] |= getBitsFromArray(bits, pos, 6); pos += 6;
        cbsign[13] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[13] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[12] |= getBitsFromArray(bits, pos, 5) << 2; pos += 5;

        cbgain[15] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[14] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[14] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[14] = getBitsFromArray(bits, pos, 4); pos += 4;
        cindex[13] |= getBitsFromArray(bits, pos, 1) << 6; pos += 1;

        // reserved (2 bits)
        pos += 2;
        cindex[15] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[15] = getBitsFromArray(bits, pos, 1);
    }

    private void unpackHalfRate(byte[] data) {
        int[] bits = bytesToBits(data, 1, 16);
        int pos = 0;

        lspv[2] |= getBitsFromArray(bits, pos, 3); pos += 3;
        lspv[1] = getBitsFromArray(bits, pos, 7); pos += 7;
        lspv[0] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[4] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[3] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[2] |= getBitsFromArray(bits, pos, 4) << 3; pos += 4;

        cbsign[0] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[0] = getBitsFromArray(bits, pos, 4); pos += 4;
        pfrac[0] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[0] = getBitsFromArray(bits, pos, 7); pos += 7;
        pgain[0] = getBitsFromArray(bits, pos, 3); pos += 3;

        plag[1] |= getBitsFromArray(bits, pos, 6); pos += 6;
        pgain[1] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[0] = getBitsFromArray(bits, pos, 7); pos += 7;

        pgain[2] |= getBitsFromArray(bits, pos, 2); pos += 2;
        cindex[1] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[1] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[1] = getBitsFromArray(bits, pos, 4); pos += 4;
        pfrac[1] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[1] |= getBitsFromArray(bits, pos, 1) << 6; pos += 1;

        cindex[2] |= getBitsFromArray(bits, pos, 2); pos += 2;
        cbsign[2] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[2] = getBitsFromArray(bits, pos, 4); pos += 4;
        pfrac[2] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[2] = getBitsFromArray(bits, pos, 7); pos += 7;
        pgain[2] |= getBitsFromArray(bits, pos, 1) << 2; pos += 1;

        pfrac[3] = getBitsFromArray(bits, pos, 1); pos += 1;
        plag[3] = getBitsFromArray(bits, pos, 7); pos += 7;
        pgain[3] = getBitsFromArray(bits, pos, 3); pos += 3;
        cindex[2] |= getBitsFromArray(bits, pos, 5) << 2; pos += 5;

        cindex[3] = getBitsFromArray(bits, pos, 7); pos += 7;
        cbsign[3] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[3] = getBitsFromArray(bits, pos, 4);
    }

    private void unpackQuarterRate(byte[] data) {
        int[] bits = bytesToBits(data, 1, 7);
        int pos = 0;

        lspv[2] |= getBitsFromArray(bits, pos, 3); pos += 3;
        lspv[1] = getBitsFromArray(bits, pos, 7); pos += 7;
        lspv[0] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[4] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[3] = getBitsFromArray(bits, pos, 6); pos += 6;
        lspv[2] |= getBitsFromArray(bits, pos, 4) << 3; pos += 4;

        cbgain[3] = getBitsFromArray(bits, pos, 4); pos += 4;
        cbgain[2] = getBitsFromArray(bits, pos, 4); pos += 4;
        cbgain[1] = getBitsFromArray(bits, pos, 4); pos += 4;
        cbgain[0] = getBitsFromArray(bits, pos, 4); pos += 4;
        // reserved 2 bits
        pos += 2;
        cbgain[4] = getBitsFromArray(bits, pos, 4);
    }

    private void unpackOctaveRate(byte[] data) {
        int[] bits = bytesToBits(data, 1, 3);
        int pos = 0;

        // first16bits is used as seed, reading lspv bits
        pos += 1; // cbsign[15] bit 3
        lspv[0] = getBitsFromArray(bits, pos, 1); pos += 1;
        lspv[1] = getBitsFromArray(bits, pos, 1); pos += 1;
        lspv[2] = getBitsFromArray(bits, pos, 1); pos += 1;
        pos += 1; // cbsign[15] bit 2
        lspv[3] = getBitsFromArray(bits, pos, 1); pos += 1;
        lspv[4] = getBitsFromArray(bits, pos, 1); pos += 1;
        lspv[5] = getBitsFromArray(bits, pos, 1); pos += 1;
        pos += 1; // cbsign[15] bit 1
        lspv[6] = getBitsFromArray(bits, pos, 1); pos += 1;
        lspv[7] = getBitsFromArray(bits, pos, 1); pos += 1;
        lspv[8] = getBitsFromArray(bits, pos, 1); pos += 1;
        pos += 1; // cbsign[15] bit 0
        lspv[9] = getBitsFromArray(bits, pos, 1); pos += 1;
        cbgain[0] = getBitsFromArray(bits, pos, 2);
    }

    private int[] bytesToBits(byte[] data, int start, int len) {
        int[] bits = new int[len * 8];
        for (int i = 0; i < len; i++) {
            int b = data[start + i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = (b >> (7 - j)) & 1;
            }
        }
        return bits;
    }

    private int getBitsFromArray(int[] bits, int pos, int len) {
        int val = 0;
        for (int i = 0; i < len; i++) {
            if (pos + i < bits.length) {
                val = (val << 1) | bits[pos + i];
            }
        }
        return val;
    }

    private void decodeFullRate(short[] output) {
        float[] lspf = new float[LPC_ORDER];
        decodeLsp(lspf);

        float[] gain = new float[16];
        decodeGainFull(gain);

        float[] cdnVector = new float[SAMPLES_PER_FRAME];
        computeSvectorFull(gain, cdnVector);

        applyPitchFilters(cdnVector, 4);
        synthesize(lspf, cdnVector, output);
        System.arraycopy(lspf, 0, prevLspf, 0, LPC_ORDER);
    }

    private void decodeHalfRate(short[] output) {
        float[] lspf = new float[LPC_ORDER];
        decodeLsp(lspf);

        float[] gain = new float[4];
        decodeGainHalf(gain);

        float[] cdnVector = new float[SAMPLES_PER_FRAME];
        computeSvectorHalf(gain, cdnVector);

        applyPitchFilters(cdnVector, 4);
        synthesize(lspf, cdnVector, output);
        System.arraycopy(lspf, 0, prevLspf, 0, LPC_ORDER);
    }

    private void decodeQuarterRate(short[] output) {
        float[] lspf = new float[LPC_ORDER];
        decodeLsp(lspf);

        float[] gain = new float[8];
        decodeGainQuarter(gain);

        float[] cdnVector = new float[SAMPLES_PER_FRAME];
        computeSvectorQuarter(cdnVector, gain);

        synthesize(lspf, cdnVector, output);
        System.arraycopy(lspf, 0, prevLspf, 0, LPC_ORDER);
    }

    private void decodeOctaveRate(byte[] data, short[] output) {
        int first16 = ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);

        float[] gain = new float[8];
        decodeGainOctave(gain);

        float[] cdnVector = new float[SAMPLES_PER_FRAME];
        computeSvectorOctave(first16, gain, cdnVector);

        float[] lspf = new float[LPC_ORDER];
        System.arraycopy(prevLspf, 0, lspf, 0, LPC_ORDER);

        synthesize(lspf, cdnVector, output);
    }

    private void decodeLsp(float[] lspf) {
        // Cumulative LSP decoding as per IS-733
        float tmpLspf = 0;
        for (int i = 0; i < 5; i++) {
            int idx = lspv[i];
            if (idx >= LSPVQ[i].length) idx = LSPVQ[i].length - 1;

            lspf[2 * i] = tmpLspf += LSPVQ[i][idx][0] * 0.0001f;
            lspf[2 * i + 1] = tmpLspf += LSPVQ[i][idx][1] * 0.0001f;
        }

        // Stability check
        lspf[0] = Math.max(lspf[0], LSP_SPREAD);
        for (int i = 1; i < LPC_ORDER; i++) {
            lspf[i] = Math.max(lspf[i], lspf[i - 1] + LSP_SPREAD);
        }
        lspf[9] = Math.min(lspf[9], 1.0f - LSP_SPREAD);
        for (int i = 8; i >= 0; i--) {
            lspf[i] = Math.min(lspf[i], lspf[i + 1] - LSP_SPREAD);
        }
    }

    private void decodeGainFull(float[] gain) {
        int[] g1 = new int[16];
        for (int i = 0; i < 16; i++) {
            g1[i] = 4 * cbgain[i];
            if (((i + 1) & 3) == 0 && i >= 3) {
                int avg = (g1[i - 1] + g1[i - 2] + g1[i - 3]) / 3 - 6;
                g1[i] += Math.max(0, Math.min(32, avg));
            }
            g1[i] = Math.min(g1[i], G12GA.length - 1);
            gain[i] = G12GA[g1[i]];
            if (cbsign[i] != 0) {
                gain[i] = -gain[i];
                cindex[i] = (cindex[i] - 89) & 127;
            }
        }
        prevG1[0] = g1[14];
        prevG1[1] = g1[15];
        lastCbGain = G12GA[g1[15]];
    }

    private void decodeGainHalf(float[] gain) {
        int[] g1 = new int[4];
        for (int i = 0; i < 4; i++) {
            g1[i] = 4 * cbgain[i];
            g1[i] = Math.min(g1[i], G12GA.length - 1);
            gain[i] = G12GA[g1[i]];
            if (cbsign[i] != 0) {
                gain[i] = -gain[i];
                cindex[i] = (cindex[i] - 89) & 127;
            }
        }
        prevG1[0] = g1[2];
        prevG1[1] = g1[3];
        lastCbGain = G12GA[g1[3]];
    }

    private void decodeGainQuarter(float[] gain) {
        int[] g1 = new int[5];
        for (int i = 0; i < 5; i++) {
            g1[i] = 4 * cbgain[i];
            g1[i] = Math.min(g1[i], G12GA.length - 1);
        }

        gain[0] = G12GA[g1[0]];
        gain[1] = 0.6f * G12GA[g1[0]] + 0.4f * G12GA[g1[1]];
        gain[2] = G12GA[g1[1]];
        gain[3] = 0.2f * G12GA[g1[1]] + 0.8f * G12GA[g1[2]];
        gain[4] = 0.8f * G12GA[g1[2]] + 0.2f * G12GA[g1[3]];
        gain[5] = G12GA[g1[3]];
        gain[6] = 0.4f * G12GA[g1[3]] + 0.6f * G12GA[g1[4]];
        gain[7] = G12GA[g1[4]];

        prevG1[0] = g1[3];
        prevG1[1] = g1[4];
        lastCbGain = G12GA[g1[4]];
    }

    private void decodeGainOctave(float[] gain) {
        int g1 = 2 * cbgain[0] + Math.max(0, Math.min(54, (prevG1[0] + prevG1[1]) / 2 - 5));
        g1 = Math.min(g1, G12GA.length - 1);

        float slope = 0.5f * (G12GA[g1] - lastCbGain) / 8;
        for (int i = 0; i < 8; i++) {
            gain[i] = lastCbGain + slope * (i + 1);
        }

        lastCbGain = gain[7];
        prevG1[0] = prevG1[1];
        prevG1[1] = g1;
    }

    private void computeSvectorFull(float[] gain, float[] cdnVector) {
        int outIdx = 0;
        for (int i = 0; i < 16; i++) {
            float tmpGain = gain[i] * 0.01f; // RATE_FULL_CODEBOOK_RATIO
            int cidx = -cindex[i];
            for (int j = 0; j < 10; j++) {
                cdnVector[outIdx++] = tmpGain * RATE_FULL_CODEBOOK[(cidx + j) & 127];
            }
        }
    }

    private void computeSvectorHalf(float[] gain, float[] cdnVector) {
        int outIdx = 0;
        for (int i = 0; i < 4; i++) {
            float tmpGain = gain[i] * 0.5f; // RATE_HALF_CODEBOOK_RATIO
            int cidx = -cindex[i];
            for (int j = 0; j < 40; j++) {
                cdnVector[outIdx++] = tmpGain * RATE_HALF_CODEBOOK[(cidx + j) & 127];
            }
        }
    }

    private void computeSvectorQuarter(float[] cdnVector, float[] gain) {
        int cbseed = (lspv[4] & 0x03) << 14 | (lspv[3] & 0x3F) << 8 |
                (lspv[2] & 0x60) << 1 | (lspv[1] & 0x07) << 3 | (lspv[0] & 0x38) >> 3;

        float sqrt1887 = 1.373681186f;
        int outIdx = 0;
        for (int i = 0; i < 8; i++) {
            float tmpGain = gain[i] * (sqrt1887 / 32768.0f);
            for (int j = 0; j < 20; j++) {
                cbseed = (521 * cbseed + 259) & 0xFFFF;
                cdnVector[outIdx++] = tmpGain * (short) cbseed;
            }
        }
    }

    private void computeSvectorOctave(int first16, float[] gain, float[] cdnVector) {
        int cbseed = first16;
        float sqrt1887 = 1.373681186f;
        int outIdx = 0;
        for (int i = 0; i < 8; i++) {
            float tmpGain = gain[i] * (sqrt1887 / 32768.0f);
            for (int j = 0; j < 20; j++) {
                cbseed = (521 * cbseed + 259) & 0xFFFF;
                cdnVector[outIdx++] = tmpGain * (short) cbseed;
            }
        }
    }

    private void applyPitchFilters(float[] cdnVector, int numSubframes) {
        if (prevBitrate >= 3) { // RATE_HALF or higher
            for (int i = 0; i < 4; i++) {
                pitchGain[i] = plag[i] != 0 ? (pgain[i] + 1) * 0.25f : 0.0f;
                pitchLag[i] = plag[i] + 16;
            }

            float[] synthOut = doPitchFilter(pitchSynthMem, cdnVector, pitchGain, pitchLag, pfrac);

            float[] preGain = new float[4];
            for (int i = 0; i < 4; i++) {
                preGain[i] = 0.5f * Math.min(pitchGain[i], 1.0f);
            }
            float[] preOut = doPitchFilter(pitchPreMem, synthOut, preGain, pitchLag, pfrac);

            applyGainControl(cdnVector, synthOut, preOut);
        } else {
            System.arraycopy(cdnVector, 17, pitchSynthMem, 0, Math.min(143, SAMPLES_PER_FRAME - 17));
            System.arraycopy(cdnVector, 17, pitchPreMem, 0, Math.min(143, SAMPLES_PER_FRAME - 17));
        }
    }

    private float[] doPitchFilter(float[] memory, float[] vIn, float[] gain, int[] lag, int[] frac) {
        float[] vOut = new float[SAMPLES_PER_FRAME];

        for (int i = 0; i < 4; i++) {
            int offset = i * 40;
            for (int j = 0; j < 40; j++) {
                int idx = offset + j;
                float sample = vIn[idx];

                if (gain[i] != 0) {
                    int memIdx = 143 + idx - lag[i];
                    if (memIdx >= 0 && memIdx < memory.length) {
                        float pitchSample;
                        if (frac[i] != 0) {
                            // Fractional pitch interpolation using hammsinc
                            pitchSample = 0;
                            for (int k = 0; k < 4; k++) {
                                int idx1 = memIdx + k - 4;
                                int idx2 = memIdx + 3 - k;
                                if (idx1 >= 0 && idx1 < memory.length)
                                    pitchSample += HAMMSINC[k] * memory[idx1];
                                if (idx2 >= 0 && idx2 < memory.length)
                                    pitchSample += HAMMSINC[k] * memory[idx2];
                            }
                        } else {
                            pitchSample = memory[memIdx];
                        }
                        sample += gain[i] * pitchSample;
                    }
                }
                vOut[idx] = sample;
                if (143 + idx < memory.length) {
                    memory[143 + idx] = sample;
                }
            }
        }

        // Shift memory
        System.arraycopy(memory, SAMPLES_PER_FRAME, memory, 0, 143);
        return vOut;
    }

    private void applyGainControl(float[] vOut, float[] vRef, float[] vIn) {
        for (int i = 0; i < 160; i += 40) {
            float refEnergy = 0;
            float inEnergy = 0;
            for (int j = 0; j < 40; j++) {
                refEnergy += vRef[i + j] * vRef[i + j];
                inEnergy += vIn[i + j] * vIn[i + j];
            }
            float scale = (inEnergy > 0.001f) ? (float) Math.sqrt(refEnergy / inEnergy) : 1.0f;
            for (int j = 0; j < 40; j++) {
                vOut[i + j] = vIn[i + j] * scale;
            }
        }
    }

    private void synthesize(float[] lspf, float[] cdnVector, short[] output) {
        lspfToLpc(lspf);

        float[] formantOut = new float[SAMPLES_PER_FRAME + LPC_ORDER];
        System.arraycopy(formantMem, 160, formantOut, 0, LPC_ORDER);

        for (int subframe = 0; subframe < 4; subframe++) {
            float weight = 0.25f * (subframe + 1);
            float[] interpLpc = new float[LPC_ORDER + 1];
            interpLpc[0] = 1.0f;
            for (int i = 1; i <= LPC_ORDER; i++) {
                float prevLpc = (subframe == 0) ? lpc[i] : lpc[i];
                interpLpc[i] = weight * lpc[i] + (1 - weight) * prevLpc;
            }

            int offset = subframe * 40;
            for (int i = 0; i < 40; i++) {
                int outIdx = LPC_ORDER + offset + i;
                formantOut[outIdx] = cdnVector[offset + i];
                for (int j = 1; j <= LPC_ORDER; j++) {
                    formantOut[outIdx] -= interpLpc[j] * formantOut[outIdx - j];
                }
            }
        }

        System.arraycopy(formantOut, SAMPLES_PER_FRAME, formantMem, 160, LPC_ORDER);
        System.arraycopy(formantOut, LPC_ORDER, formantMem, 10, SAMPLES_PER_FRAME);

        for (int i = 0; i < SAMPLES_PER_FRAME; i++) {
            float sample = formantOut[LPC_ORDER + i] * QCELP_SCALE;
            int s = Math.round(sample);
            output[i] = (short) Math.max(-32768, Math.min(32767, s));
        }
    }

    private void lspfToLpc(float[] lspf) {
        double[] lsp = new double[LPC_ORDER];
        for (int i = 0; i < LPC_ORDER; i++) {
            lsp[i] = Math.cos(Math.PI * lspf[i]);
        }

        double[] p = new double[LPC_ORDER / 2 + 1];
        double[] q = new double[LPC_ORDER / 2 + 1];
        p[0] = q[0] = 1.0;

        for (int i = 0; i < LPC_ORDER / 2; i++) {
            double[] pNew = new double[LPC_ORDER / 2 + 1];
            double[] qNew = new double[LPC_ORDER / 2 + 1];

            for (int j = 0; j <= i + 1; j++) {
                pNew[j] = p[j];
                qNew[j] = q[j];
                if (j >= 1) {
                    pNew[j] -= 2 * lsp[2 * i] * p[j - 1];
                    qNew[j] -= 2 * lsp[2 * i + 1] * q[j - 1];
                }
                if (j >= 2) {
                    pNew[j] += p[j - 2];
                    qNew[j] += q[j - 2];
                }
            }
            System.arraycopy(pNew, 0, p, 0, LPC_ORDER / 2 + 1);
            System.arraycopy(qNew, 0, q, 0, LPC_ORDER / 2 + 1);
        }

        lpc[0] = 1.0f;
        double bwExp = BANDWIDTH_EXPANSION;
        for (int i = 1; i <= LPC_ORDER; i++) {
            double pVal = (i <= LPC_ORDER / 2) ? p[i] : p[LPC_ORDER - i];
            double qVal = (i <= LPC_ORDER / 2) ? q[i] : -q[LPC_ORDER - i];
            lpc[i] = (float) (0.5 * (pVal + qVal) * bwExp);
            bwExp *= BANDWIDTH_EXPANSION;
        }
    }

    private void erasure(short[] output) {
        java.util.Arrays.fill(output, (short) 0);
    }
}