package com.sprintpcs.media;

import javax.microedition.media.PlayerImpl;

public class DualTone {
    byte[] aByteArray513;
    int anInt514;
    int anInt515;

    public DualTone(int[] arrn, int[] arrn2, int[] arrn3, int n, int n2) {
        int n3;
        if (this.anInt515 < 0) {
            throw new IllegalArgumentException("DualTone constructor: negative vibration");
        }
        if (arrn.length != arrn2.length || arrn.length != arrn3.length) {
            throw new IllegalArgumentException("DualTone arrays and duration must have the same length");
        }
        for (n3 = 0; n3 < arrn.length; ++n3) {
            if (arrn[n3] >= 0 && arrn2[n3] >= 0 && arrn3[n3] >= 0) continue;
            throw new IllegalArgumentException("DualTone constructor: negative arguments");
        }
        this.anInt514 = n;
        this.anInt515 = n2;
        this.aByteArray513 = new byte[6 + 4 * arrn.length];
        this.aByteArray513[0] = -2;
        this.aByteArray513[1] = 1;
        this.aByteArray513[2] = -3;
        this.aByteArray513[3] = 60;
        this.aByteArray513[4] = -4;
        this.aByteArray513[5] = 96;
        for (n3 = 0; n3 < arrn.length; ++n3) {
            byte by = DualTone.method213(arrn[n3]);
            byte by2 = DualTone.method213(arrn2[n3]);
            int n4 = arrn3[n3] * 96 * 60 / 240000;
            if (n4 > 255) {
                n4 = 255;
            }
            this.aByteArray513[6 + 4 * n3] = by;
            this.aByteArray513[7 + 4 * n3] = (byte) n4;
            this.aByteArray513[8 + 4 * n3] = by2;
            this.aByteArray513[9 + 4 * n3] = (byte) n4;
        }
    }

    public DualTone(int n, int n2, int n3, int n4, int n5) {
        if (n5 < 0 || n < 0 || n2 < 0 || n3 < 0) {
            throw new IllegalArgumentException("DualTone constructor: negative vibration");
        }
        this.anInt514 = n4;
        this.anInt515 = n5;
        this.aByteArray513 = new byte[10];
        this.aByteArray513[0] = -2;
        this.aByteArray513[1] = 1;
        this.aByteArray513[2] = -3;
        this.aByteArray513[3] = 60;
        this.aByteArray513[4] = -4;
        this.aByteArray513[5] = 96;
        int n6 = n3 * 96 * 60 / 240000;
        if (n6 > 255) {
            n6 = 255;
        }
        this.aByteArray513[6] = DualTone.method213(n);
        this.aByteArray513[7] = (byte) n6;
        this.aByteArray513[8] = DualTone.method213(n2);
        this.aByteArray513[9] = (byte) n6;
    }

    private static byte method213(int n) {
        if (n < 0) {
            return -1;
        }
        if (n > 0 && n <= 9) {
            return 2;
        }
        if (n == 10) {
            return 3;
        }
        if (n == 11) {
            return 5;
        }
        if (n == 12) {
            return 7;
        }
        if (n == 13) {
            return 8;
        }
        if (n == 14) {
            return 9;
        }
        if (n == 15) {
            return 11;
        }
        if (n == 16) {
            return 12;
        }
        if (n == 17) {
            return 13;
        }
        if (n == 18) {
            return 14;
        }
        if (n == 19) {
            return 15;
        }
        if (n == 20 || n == 21) {
            return 16;
        }
        if (n == 22) {
            return 17;
        }
        if (n == 23) {
            return 18;
        }
        if (n == 24 || n == 25) {
            return 19;
        }
        if (n == 26) {
            return 20;
        }
        if (n == 27 || n == 28) {
            return 21;
        }
        if (n == 29) {
            return 22;
        }
        if (n == 30 || n == 31) {
            return 23;
        }
        if (n == 32 || n == 33) {
            return 24;
        }
        if (n == 34 || n == 35) {
            return 25;
        }
        if (n == 36 || n == 37) {
            return 26;
        }
        if (n == 38 || n == 39) {
            return 27;
        }
        if (n >= 40 && n <= 42) {
            return 28;
        }
        if (n >= 43 && n <= 44) {
            return 29;
        }
        if (n >= 45 && n <= 47) {
            return 30;
        }
        if (n >= 48 && n <= 50) {
            return 31;
        }
        if (n >= 51 && n <= 53) {
            return 32;
        }
        if (n >= 54 && n <= 56) {
            return 33;
        }
        if (n >= 57 && n <= 59) {
            return 34;
        }
        if (n >= 60 && n <= 63) {
            return 35;
        }
        if (n >= 64 && n <= 67) {
            return 36;
        }
        if (n >= 68 && n <= 71) {
            return 37;
        }
        if (n >= 72 && n <= 75) {
            return 38;
        }
        if (n >= 76 && n <= 79) {
            return 39;
        }
        if (n >= 80 && n <= 84) {
            return 40;
        }
        if (n >= 85 && n <= 89) {
            return 41;
        }
        if (n >= 90 && n <= 95) {
            return 42;
        }
        if (n >= 96 && n <= 100) {
            return 43;
        }
        if (n >= 101 && n <= 106) {
            return 44;
        }
        if (n >= 107 && n <= 113) {
            return 45;
        }
        if (n >= 114 && n <= 119) {
            return 46;
        }
        if (n >= 120 && n <= 126) {
            return 47;
        }
        if (n >= 127 && n <= 134) {
            return 48;
        }
        if (n >= 135 && n <= 142) {
            return 49;
        }
        if (n >= 143 && n <= 150) {
            return 50;
        }
        if (n >= 151 && n <= 159) {
            return 51;
        }
        if (n >= 160 && n <= 169) {
            return 52;
        }
        if (n >= 170 && n <= 179) {
            return 53;
        }
        if (n >= 180 && n <= 190) {
            return 54;
        }
        if (n >= 191 && n <= 201) {
            return 55;
        }
        if (n >= 202 && n <= 213) {
            return 56;
        }
        if (n >= 214 && n <= 226) {
            return 57;
        }
        if (n >= 227 && n <= 239) {
            return 58;
        }
        if (n >= 240 && n <= 253) {
            return 59;
        }
        if (n >= 254 && n <= 269) {
            return 60;
        }
        if (n >= 270 && n <= 284) {
            return 61;
        }
        if (n >= 285 && n <= 301) {
            return 62;
        }
        if (n >= 302 && n <= 319) {
            return 63;
        }
        if (n >= 320 && n <= 338) {
            return 64;
        }
        if (n >= 339 && n <= 359) {
            return 65;
        }
        if (n >= 360 && n <= 380) {
            return 66;
        }
        if (n >= 381 && n <= 403) {
            return 67;
        }
        if (n >= 404 && n <= 427) {
            return 68;
        }
        if (n >= 428 && n <= 452) {
            return 69;
        }
        if (n >= 453 && n <= 479) {
            return 70;
        }
        if (n >= 480 && n <= 507) {
            return 71;
        }
        if (n >= 508 && n <= 538) {
            return 72;
        }
        if (n >= 539 && n <= 569) {
            return 73;
        }
        if (n >= 570 && n <= 603) {
            return 74;
        }
        if (n >= 604 && n <= 639) {
            return 75;
        }
        if (n >= 639 && n <= 677) {
            return 76;
        }
        if (n >= 678 && n <= 718) {
            return 77;
        }
        if (n >= 719 && n <= 760) {
            return 78;
        }
        if (n >= 761 && n <= 806) {
            return 79;
        }
        if (n >= 807 && n <= 854) {
            return 80;
        }
        if (n >= 855 && n <= 904) {
            return 81;
        }
        if (n >= 905 && n <= 958) {
            return 82;
        }
        if (n >= 959 && n <= 1015) {
            return 83;
        }
        if (n >= 1016 && n <= 1076) {
            return 84;
        }
        if (n >= 1077 && n <= 1139) {
            return 85;
        }
        if (n >= 1140 && n <= 1207) {
            return 86;
        }
        if (n >= 1208 && n <= 1279) {
            return 87;
        }
        if (n >= 1280 && n <= 1355) {
            return 88;
        }
        if (n >= 1356 && n <= 1436) {
            return 89;
        }
        if (n >= 1437 && n <= 1521) {
            return 90;
        }
        if (n >= 1522 && n <= 1612) {
            return 91;
        }
        if (n >= 1613 && n <= 1709) {
            return 92;
        }
        if (n >= 1710 && n <= 1809) {
            return 93;
        }
        if (n >= 1810 && n <= 1917) {
            return 94;
        }
        if (n >= 1918 && n <= 2031) {
            return 95;
        }
        if (n >= 2032 && n <= 2152) {
            return 96;
        }
        if (n >= 2153 && n <= 2279) {
            return 97;
        }
        if (n >= 2280 && n <= 2415) {
            return 98;
        }
        if (n >= 2416 && n <= 2559) {
            return 99;
        }
        if (n >= 2560 && n <= 2711) {
            return 100;
        }
        if (n >= 2712 && n <= 2872) {
            return 101;
        }
        if (n >= 2873 && n <= 3043) {
            return 102;
        }
        if (n >= 3044 && n <= 3224) {
            return 103;
        }
        if (n >= 3225 && n <= 3416) {
            return 104;
        }
        if (n >= 3417 && n <= 3619) {
            return 105;
        }
        if (n >= 3620 && n <= 3834) {
            return 106;
        }
        if (n >= 3835 && n <= 4062) {
            return 107;
        }
        if (n >= 4063 && n <= 4304) {
            return 108;
        }
        if (n >= 4305 && n <= 4559) {
            return 109;
        }
        if (n >= 4560 && n <= 4831) {
            return 110;
        }
        if (n >= 4832 && n <= 5118) {
            return 111;
        }
        if (n >= 5119 && n <= 5422) {
            return 112;
        }
        if (n >= 5423 && n <= 5745) {
            return 113;
        }
        if (n >= 5746 && n <= 6086) {
            return 114;
        }
        if (n >= 6087 && n <= 6448) {
            return 115;
        }
        if (n >= 6449 && n <= 6832) {
            return 116;
        }
        if (n >= 6833 && n <= 7238) {
            return 117;
        }
        if (n >= 7239 && n <= 7668) {
            return 118;
        }
        if (n >= 7669 && n <= 8124) {
            return 119;
        }
        if (n >= 8125 && n <= 8608) {
            return 120;
        }
        if (n >= 8609 && n <= 9119) {
            return 121;
        }
        if (n >= 9120 && n <= 9662) {
            return 122;
        }
        if (n >= 9663 && n <= 10236) {
            return 123;
        }
        if (n >= 10237 && n <= 10846) {
            return 124;
        }
        if (n >= 10847 && n <= 11490) {
            return 125;
        }
        if (n >= 11490 && n <= 12173) {
            return 126;
        }
        return (byte) (n < 12174 || n > 20000 ? 2 : 127);
    }

    public int getState() {
        int n = -1;
        javax.microedition.media.Player player = null;
        if (Player.aDualTone910 == this) {
            player = Player.aPlayer916;
        }
        if (player != null && player instanceof PlayerImpl) {
            n = player.getState();
        }
        return n;
    }
}
