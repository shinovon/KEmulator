package emulator.media.amr;

/**
 * Pure Java AMR-NB (narrowband) decoder, ported 1:1 from opencore-amr 0.1.6
 * (via the repository's JS reference port) and verified byte-exact against it
 * on all test fixtures (8 modes, DTX/SID, BFI concealment).
 *
 * Layout: every class and function below keeps the name of the C source it
 * was transcribed from (d_plsf.cpp, dtx_dec.cpp, dec_gain.cpp, ...), so each
 * block can be diffed against the original opencore-amr files. All tables
 * (same layout as the *_tbl.cpp files) live in Tables.java.
 *
 * Usage:
 *   AmrNbDecoder dec = new AmrNbDecoder();
 *   short[] pcm = new short[160];
 *   dec.decode(frame, pcm, 0);   // frame: one IETF frame incl. ToC byte
 *   short[] all = dec.decodeAll(amrBytes); // or an entire file (no magic ok)
 *
 * Java 1.6 compatible (-source/-target 1.6), no dependencies, fixed-point only.
 */
public final class AmrNbDecoder {

    /** Frame sizes in bytes (incl. ToC byte) per frame type, IETF storage format */
    static final int[] FRAME_SIZE = { 13, 14, 16, 18, 20, 21, 27, 32, 6, 1, 1, 1, 1, 1, 1, 1 };
    static final byte[] MAGIC = { 0x23, 0x21, 0x41, 0x4d, 0x52, 0x0a }; /* #!AMR\n */

    private final SpDec.State state = AmrDecode.Decoder_Interface_init();

    /** Reset decoder state (as after construction). */
    public void reset() {
        state.reset();
    }

    /** Decode one IETF frame (incl. ToC byte) to 160 PCM samples (8 kHz). */
    public void decode(byte[] frame, short[] pcm, int bfi) {
        AmrDecode.Decoder_Interface_Decode(state, frame, pcm, bfi);
    }

    /** Decode an entire AMR buffer (with or without "#!AMR\n" magic). */
    public short[] decodeAll(byte[] data) {
        int off = 0;
        if (data.length >= 6) {
            boolean magic = true;
            for (int i = 0; i < MAGIC.length; i++) {
                if (data[i] != MAGIC[i]) {
                    magic = false;
                    break;
                }
            }
            if (magic) {
                off = 6;
            }
        }
        int nFrames = 0;
        int p = off;
        while (p + 1 <= data.length) {
            int size = FRAME_SIZE[(data[p] >> 3) & 0x0f];
            if (p + size > data.length) {
                break;
            }
            p += size;
            nFrames++;
        }
        short[] out = new short[nFrames * 160];
        byte[] frame = new byte[32];
        short[] pcm = new short[160];
        int n = 0;
        while (off + 1 <= data.length) {
            int size = FRAME_SIZE[(data[off] >> 3) & 0x0f];
            if (off + size > data.length) {
                break;
            }
            System.arraycopy(data, off, frame, 0, size);
            AmrDecode.Decoder_Interface_Decode(state, frame, pcm, 0);
            System.arraycopy(pcm, 0, out, n, 160);
            n += 160;
            off += size;
        }
        return out;
    }

}

/**
 * Constants and enums, ported from opencore-amr 0.1.6:
 *   .../amr_nb/common/include/cnst.h, mode.h, frame_type_3gpp.h, frame.h
 * (via src/common/cnst.js of the JS reference port).
 */
final class Cnst {
    private Cnst() {}

    /* cnst.h */
    public static final int L_TOTAL = 320;      /* Total size of speech buffer.             */
    public static final int L_WINDOW = 240;     /* Window size in LP analysis               */
    public static final int L_FRAME = 160;      /* Frame size                               */
    public static final int L_FRAME_BY2 = 80;   /* Frame size divided by 2                  */
    public static final int L_SUBFR = 40;       /* Subframe size                            */
    public static final int L_CODE = 40;        /* codevector length                        */
    public static final int NB_TRACK = 5;       /* number of tracks                         */
    public static final int STEP = 5;           /* codebook step size                       */
    public static final int NB_TRACK_MR102 = 4; /* number of tracks mode mr102              */
    public static final int STEP_MR102 = 4;     /* codebook step size mode mr102            */
    public static final int M = 10;             /* Order of LP filter                       */
    public static final int MP1 = M + 1;        /* Order of LP filter + 1                   */
    public static final int LSF_GAP = 205;      /* Min distance between LSF after quant.    */
    public static final int LSP_PRED_FAC_MR122 = 21299; /* MR122 LSP pred factor (0.65 Q15) */
    public static final int AZ_SIZE = 4 * M + 4; /* Size of array of LP filters in 4 subfrs */
    public static final int PIT_MIN_MR122 = 18; /* Minimum pitch lag (MR122 mode)           */
    public static final int PIT_MIN = 20;       /* Minimum pitch lag (all other modes)      */
    public static final int PIT_MAX = 143;      /* Maximum pitch lag                        */
    public static final int L_INTERPOL = 10 + 1; /* Length of filter for interpolation      */
    public static final int L_INTER_SRCH = 4;   /* Length of filter for CL LTP search       */
    public static final int MU = 26214;         /* Factor for tilt compensation filter 0.8  */
    public static final int AGC_FAC = 29491;    /* Factor for automatic gain control 0.9    */
    public static final int L_NEXT = 40;        /* Overhead in LP analysis                  */
    public static final int SHARPMAX = 13017;   /* Maximum value of pitch sharpening        */
    public static final int SHARPMIN = 0;       /* Minimum value of pitch sharpening        */
    public static final int MAX_PRM_SIZE = 57;  /* max. num. of params                      */
    public static final int MAX_SERIAL_SIZE = 244; /* max. num. of serial bits              */
    public static final int GP_CLIP = 15565;    /* Pitch gain clipping = 0.95               */
    public static final int N_FRAME = 7;        /* old pitch gains in average calculation   */
    public static final int EHF_MASK = 0x0008;  /* encoder homing frame pattern             */

    /* mode.h enum Mode */
    public static final int MR475 = 0;
    public static final int MR515 = 1;
    public static final int MR59 = 2;
    public static final int MR67 = 3;
    public static final int MR74 = 4;
    public static final int MR795 = 5;
    public static final int MR102 = 6;
    public static final int MR122 = 7;
    public static final int MRDTX = 8;
    public static final int N_MODES = 9;

    /* frame_type_3gpp.h enum Frame_Type_3GPP */
    public static final int AMR_475 = 0;
    public static final int AMR_515 = 1;
    public static final int AMR_59 = 2;
    public static final int AMR_67 = 3;
    public static final int AMR_74 = 4;
    public static final int AMR_795 = 5;
    public static final int AMR_102 = 6;
    public static final int AMR_122 = 7;
    public static final int AMR_SID = 8;
    public static final int GSM_EFR_SID = 9;
    public static final int TDMA_EFR_SID = 10;
    public static final int PDC_EFR_SID = 11;
    public static final int FOR_FUTURE_USE1 = 12;
    public static final int FOR_FUTURE_USE2 = 13;
    public static final int FOR_FUTURE_USE3 = 14;
    public static final int AMR_NO_DATA = 15;

    /* frame.h enum RXFrameType */
    public static final int RX_SPEECH_GOOD = 0;
    public static final int RX_SPEECH_DEGRADED = 1;
    public static final int RX_ONSET = 2;
    public static final int RX_SPEECH_BAD = 3;
    public static final int RX_SID_FIRST = 4;
    public static final int RX_SID_UPDATE = 5;
    public static final int RX_SID_BAD = 6;
    public static final int RX_NO_DATA = 7;
    public static final int RX_N_FRAMETYPES = 8;
}

/**
 * Fixed-point basic operations for AMR-NB, ported from opencore-amr 0.1.6
 * (via src/common/basicop.js of the JS reference port):
 *   .../amr_nb/common/include/basic_op.h
 *   .../amr_nb/common/include/basic_op_c_equivalent.h
 *   .../amr_nb/common/src/{add,sub,shr,mult_r,round,shr_r,div_s,l_shr_r,negate,
 *                          norm_l,norm_s,extract_h,extract_l,l_deposit_h,l_deposit_l}.cpp
 *
 * Conventions (whole project):
 *  - Word32 is a Java int normalized by the same expressions the JS port uses
 *    (`| 0` after every operation that can leave the int32 range; Java int
 *    arithmetic wraps mod 2^32 identically).
 *  - Word16 values are kept normalized in [-32768, 32767]; a C `(Word16)` cast
 *    is written as `(x << 16) >> 16`.
 *  - pOverflow is an int[1]; functions write pOverflow[0] = 1 exactly where the
 *    C code writes *pOverflow = 1. Functions whose C source ignores pOverflow
 *    ignore it here as well — do not "fix" this, bit-exactness depends on it.
 *  - `>>` only (never `>>>`): C arithmetic shifts on negative values must keep
 *    the sign. Shift counts >= 32 rely on identical masking (low 5 bits) in
 *    C(x86)/wasm/JS/Java only where the C source itself has no guard.
 *  - 16x16 multiplies are exact in both double and int (max 2^30); anything
 *    with wider operands mirrors the JS `Math.imul` (== Java int multiply).
 */
final class Basicop {
    private Basicop() {}

    public static final int MAX_32 = 0x7fffffff;
    public static final int MIN_32 = -0x80000000;
    public static final int MAX_16 = 0x7fff;
    public static final int MIN_16 = -0x8000;

    /** ECMAScript ToInt32(double): truncate toward zero, wrap mod 2^32. */
    static int toInt32(double d) {
        if (d != d || d == 0.0 || d == -0.0 || Double.isInfinite(d)) {
            return 0;
        }
        double m = d % 4294967296.0; /* IEEE % is exact for integral doubles */
        if (m < 0) {
            m += 4294967296.0;
        }
        return (int) m;
    }

    /** basic_op_c_equivalent.h L_add */
    public static int L_add(int L_var1, int L_var2, int[] pOverflow) {
        int L_sum = L_var1 + L_var2;

        if ((L_var1 ^ L_var2) >= 0) {
            if (((L_sum ^ L_var1) >> 31) != 0) {
                L_sum = (L_var1 >> 31) != 0 ? MIN_32 : MAX_32;
                pOverflow[0] = 1;
            }
        }
        return L_sum;
    }

    /** basic_op_c_equivalent.h L_sub */
    public static int L_sub(int L_var1, int L_var2, int[] pOverflow) {
        int L_diff = L_var1 - L_var2;

        if (((L_var1 ^ L_var2) >> 31) != 0) {
            if ((L_diff ^ L_var1) != 0 && ((L_diff ^ L_var1) & MIN_32) != 0) {
                L_diff = (L_var1 >> 31) != 0 ? MIN_32 : MAX_32;
                pOverflow[0] = 1;
            }
        }
        return L_diff;
    }

    /** basic_op_c_equivalent.h L_mac */
    public static int L_mac(int L_var3, int var1, int var2, int[] pOverflow) {
        int L_sum;
        int result = var1 * var2; /* 16x16, exact */
        if (result != 0x40000000) {
            L_sum = (result << 1) + L_var3;

            /* Check if L_sum and L_var_3 share the same sign */
            if ((L_var3 ^ result) > 0) {
                if (((L_sum ^ L_var3) >> 31) != 0) {
                    L_sum = (L_var3 >> 31) != 0 ? MIN_32 : MAX_32;
                    pOverflow[0] = 1;
                }
            }
        } else {
            pOverflow[0] = 1;
            L_sum = MAX_32;
        }
        return L_sum;
    }

    /** basic_op_c_equivalent.h L_mult */
    public static int L_mult(int var1, int var2, int[] pOverflow) {
        int L_product = var1 * var2; /* 16x16, exact */

        if (L_product != 0x40000000) {
            L_product <<= 1; /* Multiply by 2 */
        } else {
            pOverflow[0] = 1;
            L_product = MAX_32;
        }
        return L_product;
    }

    /** basic_op_c_equivalent.h L_msu */
    public static int L_msu(int L_var3, int var1, int var2, int[] pOverflow) {
        int result = L_mult(var1, var2, pOverflow);
        result = L_sub(L_var3, result, pOverflow);
        return result;
    }

    /**
     * basic_op_c_equivalent.h Mpy_32 (pOverflow intentionally unused, as in C).
     * The first product is computed in double exactly like the JS port
     * (which uses a plain JS multiply there, not Math.imul).
     */
    public static int Mpy_32(int L_var1_hi, int L_var1_lo, int L_var2_hi, int L_var2_lo, int[] pOverflow) {
        double L_product_d = (double) L_var1_hi * (double) L_var2_hi;
        int L_product;
        int L_sum;
        int product32;

        if (L_product_d != 0x40000000) {
            L_product = toInt32(L_product_d) << 1;
        } else {
            L_product = MAX_32;
        }

        /* result = mult (L_var1_hi, L_var2_lo, pOverflow); */
        product32 = (L_var1_hi * L_var2_lo) >> 15;

        /* L_product = L_mac (L_product, result, 1, pOverflow); */
        L_sum = L_product + (product32 << 1);

        if ((L_product ^ product32) > 0) {
            if (((L_sum ^ L_product) >> 31) != 0) {
                L_sum = (L_product >> 31) != 0 ? MIN_32 : MAX_32;
            }
        }

        L_product = L_sum;

        /* result = mult (L_var1_lo, L_var2_hi, pOverflow); */
        product32 = (L_var1_lo * L_var2_hi) >> 15;

        /* L_product = L_mac (L_product, result, 1, pOverflow); */
        L_sum = L_product + (product32 << 1);

        if ((L_product ^ product32) > 0) {
            if (((L_sum ^ L_product) >> 31) != 0) {
                L_sum = (L_product >> 31) != 0 ? MIN_32 : MAX_32;
            }
        }
        return L_sum;
    }

    /** basic_op_c_equivalent.h Mpy_32_16 */
    public static int Mpy_32_16(int L_var1_hi, int L_var1_lo, int var2, int[] pOverflow) {
        int L_product = L_var1_hi * var2; /* exact, then ToInt32 == int wrap */
        int L_sum;
        int result;

        if (L_product != 0x40000000) {
            L_product <<= 1;
        } else {
            pOverflow[0] = 1;
            L_product = MAX_32;
        }

        result = (L_var1_lo * var2) >> 15;

        L_sum = L_product + (result << 1);

        if ((L_product ^ result) > 0) {
            if (((L_sum ^ L_product) >> 31) != 0) {
                L_sum = (L_product >> 31) != 0 ? MIN_32 : MAX_32;
                pOverflow[0] = 1;
            }
        }
        return L_sum;
    }

    /** basic_op_c_equivalent.h mult */
    public static int mult(int var1, int var2, int[] pOverflow) {
        int product = (var1 * var2) >> 15;

        /* Saturate result (if necessary). */
        /* var1 * var2 >0x00007fff is the only case */
        /* that saturation occurs. */
        if (product > 0x00007fff) {
            pOverflow[0] = 1;
            product = MAX_16;
        }
        return (product << 16) >> 16;
    }

    /** basic_op_c_equivalent.h amrnb_fxp_mac_16_by_16bb (Word32 multiply, wraps) */
    public static int amrnb_fxp_mac_16_by_16bb(int L_var1, int L_var2, int L_var3) {
        return L_var3 + L_var1 * L_var2;
    }

    /** basic_op_c_equivalent.h amrnb_fxp_msu_16_by_16bb (Word32 multiply, wraps) */
    public static int amrnb_fxp_msu_16_by_16bb(int L_var1, int L_var2, int L_var3) {
        return L_var3 - L_var1 * L_var2;
    }

    /** basic_op.h Mac_32 */
    public static int Mac_32(int L_var3, int L_var1_hi, int L_var1_lo, int L_var2_hi, int L_var2_lo, int[] pOverflow) {
        int product;

        L_var3 = L_mac(L_var3, L_var1_hi, L_var2_hi, pOverflow);

        product = mult(L_var1_hi, L_var2_lo, pOverflow);
        L_var3 = L_mac(L_var3, product, 1, pOverflow);

        product = mult(L_var1_lo, L_var2_hi, pOverflow);
        L_var3 = L_mac(L_var3, product, 1, pOverflow);

        return L_var3;
    }

    /** basic_op.h Mac_32_16 */
    public static int Mac_32_16(int L_var3, int L_var1_hi, int L_var1_lo, int var2, int[] pOverflow) {
        int product;

        L_var3 = L_mac(L_var3, L_var1_hi, var2, pOverflow);

        product = mult(L_var1_lo, var2, pOverflow);
        L_var3 = L_mac(L_var3, product, 1, pOverflow);

        return L_var3;
    }

    /** basic_op.h negate (also negate.cpp) */
    public static int negate(int var1) {
        return var1 == MIN_16 ? MAX_16 : -var1;
    }

    /** basic_op.h shl (pOverflow intentionally unused, as in C) */
    public static int shl(int var1, int var2, int[] pOverflow) {
        int var_out = 0;

        if (var2 < 0) {
            var2 = -var2;
            if (var2 < 15) {
                var_out = var1 >> var2;
            }
        } else {
            var_out = (var1 << var2 << 16) >> 16; /* C: (Word16) assignment truncates */
            if (var_out >> var2 != var1) {
                var_out = (var1 >> 15) ^ MAX_16;
            }
        }
        return var_out;
    }

    /** basic_op.h L_shl (pOverflow intentionally unused, as in C) */
    public static int L_shl(int L_var1, int var2, int[] pOverflow) {
        int L_var_out = 0;

        if (var2 > 0) {
            L_var_out = L_var1 << var2;
            if (L_var_out >> var2 != L_var1) {
                L_var_out = (L_var1 >> 31) ^ MAX_32;
            }
        } else {
            var2 = -var2;
            if (var2 < 31) {
                L_var_out = L_var1 >> var2;
            }
            /* C: shifts >= 31 intentionally return the initialized 0, even for
               negative L_var1 (not -1). Bit-exactness depends on this. */
        }
        return L_var_out;
    }

    /** basic_op.h L_shr (pOverflow intentionally unused, as in C) */
    public static int L_shr(int L_var1, int var2, int[] pOverflow) {
        int L_var_out = 0;

        if (var2 > 0) {
            if (var2 < 31) {
                L_var_out = L_var1 >> var2;
            }
            /* C: var2 >= 31 intentionally returns 0 (see L_shl note) */
        } else {
            var2 = -var2;

            L_var_out = L_var1 << var2;
            if (L_var_out >> var2 != L_var1) {
                L_var_out = (L_var1 >> 31) ^ MAX_32;
            }
        }
        return L_var_out;
    }

    /** basic_op.h abs_s */
    public static int abs_s(int var1) {
        /* C: Word16 y = var1 - (var1 < 0) — 16-bit wrap makes abs_s(-32768) = 32767 */
        int y = ((var1 - (var1 < 0 ? 1 : 0)) << 16) >> 16;
        y = y ^ (y >> 15);
        return y;
    }

    /** add.cpp add_16 */
    public static int add_16(int var1, int var2, int[] pOverflow) {
        int sum = var1 + var2;

        /* Saturate result (if necessary). */
        if (sum > 0x00007fff) {
            pOverflow[0] = 1;
            sum = MAX_16;
        } else if (sum < -32768) {
            pOverflow[0] = 1;
            sum = MIN_16;
        }
        return (sum << 16) >> 16;
    }

    /** sub.cpp sub */
    public static int sub(int var1, int var2, int[] pOverflow) {
        int diff = var1 - var2;

        /* C: if ((UWord32)(diff + 32768) > 0x000FFFF) — unsigned compare */
        if (((long) (diff + 32768) & 0xffffffffL) > 0x0000ffffL) {
            if (diff > 0x00007fff) {
                diff = MAX_16;
            } else {
                diff = MIN_16;
            }
            pOverflow[0] = 1;
        }
        return (diff << 16) >> 16;
    }

    /** shr.cpp shr */
    public static int shr(int var1, int var2, int[] pOverflow) {
        int result;
        if (var2 != 0) {
            if (var2 > 0) {
                if (var2 > 15) {
                    var2 = 15;
                }
                result = var1 >> var2;
            } else {
                var2 = -var2; /* Shift right negative is equivalent */
                if (var2 > 15) {
                    var2 = 15;
                }
                result = (var1 << var2 << 16) >> 16; /* C: (Word16) assignment truncates */
                if (result >> var2 != var1) {
                    pOverflow[0] = 1;
                    result = var1 > 0 ? MAX_16 : MIN_16;
                }
            }
        } else {
            result = var1;
        }
        return result;
    }

    /** mult_r.cpp mult_r */
    public static int mult_r(int var1, int var2, int[] pOverflow) {
        int L_product_arr = var1 * var2; /* product */
        L_product_arr += 0x00004000; /* round */
        L_product_arr >>= 15; /* shift */
        /* sign extend when necessary */
        L_product_arr |= -(L_product_arr & 0x00010000);

        /* Saturate result (if necessary). */
        if (L_product_arr > 0x00007fff) {
            pOverflow[0] = 1;
            L_product_arr = MAX_16;
        } else if (L_product_arr < -32768) {
            pOverflow[0] = 1;
            L_product_arr = MIN_16;
        }
        return (L_product_arr << 16) >> 16;
    }

    /** round.cpp pv_round */
    public static int pv_round(int L_var1, int[] pOverflow) {
        L_var1 = L_add(L_var1, 0x00008000, pOverflow);
        return (L_var1 >> 16) << 16 >> 16;
    }

    /** shr_r.cpp shr_r */
    public static int shr_r(int var1, int var2, int[] pOverflow) {
        int var_out;

        if (var2 > 15) {
            var_out = 0;
        } else {
            var_out = shr(var1, var2, pOverflow);
            if (var2 > 0) {
                if ((var1 & (1 << (var2 - 1))) != 0) {
                    var_out++;
                }
            }
        }
        return var_out;
    }

    /** div_s.cpp div_s */
    public static int div_s(int var1, int var2) {
        int var_out = 0;
        int iteration;
        int L_num;
        int L_denom;
        int L_denom_by_2;
        int L_denom_by_4;

        if (var1 > var2 || var1 < 0) {
            return 0; /* C: used to exit(0) */
        }
        if (var1 != 0) {
            if (var1 != var2) {
                L_num = var1;
                L_denom = var2;
                L_denom_by_2 = L_denom << 1;
                L_denom_by_4 = L_denom << 2;
                for (iteration = 5; iteration > 0; iteration--) {
                    var_out <<= 3;
                    L_num <<= 3;
                    if (L_num >= L_denom_by_4) {
                        L_num -= L_denom_by_4;
                        var_out |= 4;
                    }
                    if (L_num >= L_denom_by_2) {
                        L_num -= L_denom_by_2;
                        var_out |= 2;
                    }
                    if (L_num >= L_denom) {
                        L_num -= L_denom;
                        var_out |= 1;
                    }
                }
            } else {
                var_out = MAX_16;
            }
        }
        return var_out;
    }

    /** l_shr_r.cpp L_shr_r */
    public static int L_shr_r(int L_var1, int var2, int[] pOverflow) {
        int result;

        if (var2 > 31) {
            result = 0;
        } else {
            result = L_shr(L_var1, var2, pOverflow);
            if (var2 > 0) {
                if ((L_var1 & (1 << (var2 - 1))) != 0) {
                    result++;
                }
            }
        }
        return result;
    }

    /** norm_l.cpp norm_l */
    public static int norm_l(int L_var1) {
        int var_out = 0;

        if (L_var1 != 0) {
            /* C: Word32 y = L_var1 - (L_var1 < 0) — wraps at MIN_32 */
            int y = L_var1 - (L_var1 < 0 ? 1 : 0);
            L_var1 = y ^ (y >> 31);
            while ((0x40000000 & L_var1) == 0) {
                var_out++;
                if ((0x20000000 & L_var1) != 0) {
                    break;
                }
                var_out++;
                if ((0x10000000 & L_var1) != 0) {
                    break;
                }
                var_out++;
                if ((0x08000000 & L_var1) != 0) {
                    break;
                }
                var_out++;
                L_var1 <<= 4;
            }
        }
        return var_out;
    }

    /** norm_s.cpp norm_s */
    public static int norm_s(int var1) {
        int var_out = 0;

        if (var1 != 0) {
            /* C: Word16 y = var1 - (var1 < 0) — 16-bit wrap at -32768 */
            int y = ((var1 - (var1 < 0 ? 1 : 0)) << 16) >> 16;
            var1 = y ^ (y >> 15);
            while ((0x4000 & var1) == 0) {
                var_out++;
                if ((0x2000 & var1) != 0) {
                    break;
                }
                var_out++;
                if ((0x1000 & var1) != 0) {
                    break;
                }
                var_out++;
                if ((0x0800 & var1) != 0) {
                    break;
                }
                var_out++;
                var1 <<= 4;
            }
        }
        return var_out;
    }

    /** extract_h.cpp */
    public static int extract_h(int L_var1) {
        return (L_var1 >> 16) << 16 >> 16;
    }

    /** extract_l.cpp */
    public static int extract_l(int L_var1) {
        return (L_var1 << 16) >> 16;
    }

    /** l_deposit_h.cpp */
    public static int L_deposit_h(int var1) {
        return var1 << 16;
    }

    /** l_deposit_l.cpp */
    public static int L_deposit_l(int var1) {
        return var1;
    }
}

/**
 * Math helper functions, ported from opencore-amr 0.1.6 common/src:
 *   gmed_n.cpp, inv_sqrt.cpp, log2_norm.cpp, log2.cpp, pow2.cpp, sqrt_l.cpp
 * (via src/common/mathops.js of the JS reference port).
 * C output pointers (Word16 *exponent, ...) become short[1] parameters so
 * call sites read like the C code. gmed_n uses shared temp buffers exactly
 * like the JS module-level Int16Arrays.
 */
final class Mathops {
    private Mathops() {}

    private static final int NMAX = 9; /* largest N used in median calculation */
    private static final short[] gmedTmp = new short[NMAX];
    private static final short[] gmedTmp2 = new short[NMAX];

    /** gmed_n.cpp gmed_n — median of ind[indOff .. indOff+n-1] */
    public static int gmed_n(short[] ind, int indOff, int n) {
        int ix = 0;
        int max;
        final short[] tmp = gmedTmp;
        final short[] tmp2 = gmedTmp2;

        for (int i = 0; i < n; i++) {
            tmp2[i] = ind[indOff + i];
        }

        for (int i = 0; i < n; i++) {
            max = -32767;
            for (int j = 0; j < n; j++) {
                if (tmp2[j] >= max) {
                    max = tmp2[j];
                    ix = j;
                }
            }
            tmp2[ix] = -32768;
            tmp[i] = (short) ix;
        }

        final int medianIndex = tmp[n >> 1]; /* account for complex addressing */
        return ind[indOff + medianIndex];
    }

    /** inv_sqrt.cpp Inv_sqrt (pOverflow intentionally unused, as in C) */
    public static int Inv_sqrt(int L_x, int[] pOverflow) {
        int exp;
        int i;
        int a;
        int tmp;
        int L_y;

        if (L_x <= 0) {
            return 0x3fffffff;
        }

        exp = Basicop.norm_l(L_x);
        L_x <<= exp; /* L_x is normalize */
        exp = 30 - exp;

        if ((exp & 1) == 0) {
            /* If exponent even -> shift right */
            L_x >>= 1;
        }
        exp >>= 1;
        exp += 1;

        L_x >>= 9;
        i = (L_x >> 16) << 16 >> 16; /* Extract b25-b31 */
        a = (L_x >> 1) << 16 >> 16;  /* Extract b10-b24 */
        a &= 0x7fff;

        i -= 16;

        L_y = Tables.inv_sqrt_tbl[i] << 16; /* inv_sqrt_tbl[i] << 16 */
        tmp = Tables.inv_sqrt_tbl[i] - Tables.inv_sqrt_tbl[i + 1];
        /* always a positive number less than 200 */
        L_y = L_y - ((tmp * a) << 1); /* L_y -= tmp*a*2 */
        L_y >>= exp; /* denormalization, exp always 0< exp < 31 */
        return L_y;
    }

    /**
     * log2_norm.cpp Log2_norm.
     * @param exponent short[1] out
     * @param fraction short[1] out
     */
    public static void Log2_norm(int L_x, int exp, short[] exponent, short[] fraction) {
        int i;
        int a;
        int tmp;
        int L_y;

        if (L_x <= 0) {
            exponent[0] = 0;
            fraction[0] = 0;
        } else {
            /* Calculate exponent portion of Log2 */
            exponent[0] = (short) (30 - exp);

            /* Shift L_x to the right by 10 to extract bits 10-31 */
            L_x >>= 10;
            i = (L_x >> 15) << 16 >> 16; /* Extract b25-b31 */
            a = L_x & 0x7fff;            /* Extract b10-b24 of fraction */

            i -= 32;

            L_y = Tables.log2_tbl[i] << 16; /* table[i] << 16 */
            tmp = Tables.log2_tbl[i] - Tables.log2_tbl[i + 1]; /* table[i] - table[i+1] */
            L_y = L_y - ((tmp * a) << 1); /* L_y -= tmp*a*2 */
            fraction[0] = (short) ((L_y >> 16) << 16 >> 16);
        }
    }

    /**
     * log2.cpp Log2 (pOverflow intentionally unused, as in C).
     * @param pExponent short[1] out
     * @param pFraction short[1] out
     */
    public static void Log2(int L_x, short[] pExponent, short[] pFraction, int[] pOverflow) {
        final int exp = Basicop.norm_l(L_x);
        Log2_norm(L_x << exp, exp, pExponent, pFraction);
    }

    /** pow2.cpp Pow2 */
    public static int Pow2(int exponent, int fraction, int[] pOverflow) {
        int exp;
        int i;
        int a;
        int tmp;
        int L_x;

        L_x = Basicop.L_mult(fraction, 32, pOverflow); /* L_x = fraction<<6 */

        /* Extract b0-b16 of fraction */
        i = ((L_x >> 16) << 16 >> 16) & 31; /* ensure index i is bounded */
        a = ((L_x >> 1) & 0x7fff) << 16 >> 16;

        L_x = Tables.pow2_tbl[i] << 16; /* pow2_tbl[i] << 16 */
        tmp = Tables.pow2_tbl[i] - Tables.pow2_tbl[i + 1];
        L_x = Basicop.L_msu(L_x, tmp, a, pOverflow); /* L_x -= tmp*a*2 */

        exp = 30 - exponent;
        L_x = Basicop.L_shr_r(L_x, exp, pOverflow);

        return L_x;
    }

    /**
     * sqrt_l.cpp sqrt_l_exp.
     * @param pExp short[1] out (right shift to apply to result, Q1)
     */
    public static int sqrt_l_exp(int L_x, short[] pExp, int[] pOverflow) {
        int e;
        int i;
        int a;
        int tmp;
        int L_y;

        if (L_x <= 0) {
            pExp[0] = 0;
            return 0;
        }

        e = Basicop.norm_l(L_x) & 0xfffe; /* get next lower EVEN norm. exp */
        L_x = Basicop.L_shl(L_x, e, pOverflow); /* L_x is normalized to [0.25..1) */
        pExp[0] = (short) e; /* return 2*exponent (or Q1) */

        L_x >>= 10;
        i = ((L_x >> 15) << 16 >> 16) & 63; /* Extract b25-b31, 16<= i <=63 */
        a = (L_x << 16) >> 16; /* Extract b10-b24 */
        a &= 0x7fff;

        if (i > 15) {
            i -= 16; /* 0 <= i <= 47 */
        }

        L_y = Tables.sqrt_l_tbl[i] << 16; /* sqrt_l_tbl[i] << 16 */
        tmp = Tables.sqrt_l_tbl[i] - Tables.sqrt_l_tbl[i + 1];
        L_y = Basicop.L_msu(L_y, tmp, a, pOverflow); /* L_y -= tmp*a*2 */

        /* denormalization done by caller */
        return L_y;
    }
}

/**
 * Filtering primitives, ported from opencore-amr 0.1.6 common/src:
 *   weight_a.cpp, residu.cpp, syn_filt.cpp, pred_lt.cpp
 * (via src/common/filters.js of the JS reference port).
 * Pointer arithmetic rewritten as (array, offset) index pairs.
 *
 * Word32 accumulators built with raw `+=` may exceed int32; Java int
 * accumulation wraps mod 2^32 at every step, which equals the C mod-2^32
 * wrap of the running sum and the JS ToInt32 of the exact double sum
 * (all intermediate sums are < 2^53, so modular arithmetic agrees).
 * Unsigned-compare sites normalize with a long mask first.
 */
final class Filters {
    private Filters() {}

    public static final int UP_SAMP_MAX = 6;
    public static final int L_INTER10 = 10; /* L_INTERPOL - 1 */

    /* pred_lt.cpp: (1/6) resolution interpolation filter table (Word16) in Q15 */
    public static final short[] inter_6_pred_lt = {
        29443,
        28346, 25207, 20449, 14701, 8693, 3143,
        -1352, -4402, -5865, -5850, -4673, -2783,
        -672, 1211, 2536, 3130, 2991, 2259,
        1170, 0, -1001, -1652, -1868, -1666,
        -1147, -464, 218, 756, 1060, 1099,
        904, 550, 135, -245, -514, -634,
        -602, -451, -231, 0, 191, 308,
        340, 296, 198, 78, -36, -120,
        -163, -165, -132, -79, -19, 34,
        73, 91, 89, 70, 38, 0,
    };

    /** weight_a.cpp Weight_Ai: a[M+1] -> a_exp[M+1] with spectral expansion fac[M] */
    public static void Weight_Ai(short[] a, int aOff, short[] fac, int facOff,
                                 short[] a_exp, int a_expOff) {
        a_exp[a_expOff] = a[aOff];
        for (int i = 1; i <= Cnst.M; i++) {
            a_exp[a_expOff + i] =
                (short) (((a[aOff + i] * fac[facOff + i - 1] + 0x00004000) >> 15) << 16 >> 16);
        }
    }

    /** residu.cpp Residu: LP residual, processes input_len samples (multiple of 4) */
    public static void Residu(short[] coef, int coefOff, short[] input, int inputOff,
                              short[] residual, int residualOff, int input_len) {
        int s1, s2, s3, s4;
        int pRes = residualOff + input_len - 1;
        int pIn = inputOff + input_len - 1 - Cnst.M;

        for (int i = input_len >> 2; i != 0; i--) {
            s1 = 0x0000800;
            s2 = 0x0000800;
            s3 = 0x0000800;
            s4 = 0x0000800;
            int pCoef = coefOff + Cnst.M;
            int p1 = pIn--;
            int p2 = pIn--;
            int p3 = pIn--;
            int p4 = pIn--;

            for (int j = Cnst.M >> 1; j != 0; j--) {
                s1 += coef[pCoef] * input[p1++];
                s2 += coef[pCoef] * input[p2++];
                s3 += coef[pCoef] * input[p3++];
                s4 += coef[pCoef--] * input[p4++];
                s1 += coef[pCoef] * input[p1++];
                s2 += coef[pCoef] * input[p2++];
                s3 += coef[pCoef] * input[p3++];
                s4 += coef[pCoef--] * input[p4++];
            }
            s1 += coef[pCoef] * input[p1];
            s2 += coef[pCoef] * input[p2];
            s3 += coef[pCoef] * input[p3];
            s4 += coef[pCoef] * input[p4];

            residual[pRes--] = (short) ((s1 >> 12) << 16 >> 16);
            residual[pRes--] = (short) ((s2 >> 12) << 16 >> 16);
            residual[pRes--] = (short) ((s3 >> 12) << 16 >> 16);
            residual[pRes--] = (short) ((s4 >> 12) << 16 >> 16);
        }
    }

    private static final short[] synTmp = new short[2 * Cnst.M]; /* C: Word16 tmp[2*M] scratch */

    /** syn_filt.cpp Syn_filt: synthesis filter 1/A(z), lg samples (40) */
    public static void Syn_filt(short[] a, int aOff, short[] x, int xOff,
                                short[] y, int yOff, int lg, short[] mem, int memOff, int update) {
        int s1, s2;
        int temp;
        final short[] yy = synTmp;

        /* Copy mem[] to yy[] */
        for (int i = 0; i < Cnst.M; i++) {
            yy[i] = mem[memOff + i];
        }
        int yyi = Cnst.M;

        /* Do the filtering. */
        int pY = yOff;
        int pX = xOff;
        int pYY1 = yyi - 1; /* index into yy */

        for (int i = Cnst.M >> 1; i != 0; i--) {
            int pA = aOff;
            s1 = Basicop.amrnb_fxp_mac_16_by_16bb(x[pX++], a[pA], 0x00000800);
            s2 = Basicop.amrnb_fxp_mac_16_by_16bb(x[pX++], a[pA++], 0x00000800);
            s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], yy[pYY1], s1);

            for (int j = (Cnst.M >> 1) - 2; j != 0; j--) {
                s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA], yy[pYY1--], s2);
                s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], yy[pYY1], s1);
                s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA], yy[pYY1--], s2);
                s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], yy[pYY1], s1);
                s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA], yy[pYY1--], s2);
                s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], yy[pYY1], s1);
            }

            /* check for overflow on s1 */
            if (((long) (s1 + 134217728) & 0xffffffffL) < 0x0fffffffL) {
                temp = ((s1 >> 12) << 16) >> 16;
            } else if (s1 > 0x07ffffff) {
                temp = Basicop.MAX_16;
            } else {
                temp = Basicop.MIN_16;
            }

            s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[aOff + 1], temp, s2);

            yy[yyi++] = (short) temp;
            y[pY++] = (short) temp;
            pYY1 = yyi; /* C: p_yy1 = yy (next unwritten slot, filled by s2 below) */

            /* check for overflow on s2 */
            if (((long) (s2 + 134217728) & 0xffffffffL) < 0x0fffffffL) {
                temp = ((s2 >> 12) << 16) >> 16;
            } else if (s2 > 0x07ffffff) {
                temp = Basicop.MAX_16;
            } else {
                temp = Basicop.MIN_16;
            }
            yy[yyi++] = (short) temp;
            y[pY++] = (short) temp;
        }

        /* remaining samples read past outputs from y[] itself */
        int pYY1y = yOff + Cnst.M - 1; /* index into y */
        for (int i = (lg - Cnst.M) >> 1; i != 0; i--) {
            int pA = aOff;
            s1 = Basicop.amrnb_fxp_mac_16_by_16bb(x[pX++], a[pA], 0x00000800);
            s2 = Basicop.amrnb_fxp_mac_16_by_16bb(x[pX++], a[pA++], 0x00000800);
            s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], y[pYY1y], s1);

            for (int j = (Cnst.M >> 1) - 2; j != 0; j--) {
                s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA], y[pYY1y--], s2);
                s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], y[pYY1y], s1);
                s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA], y[pYY1y--], s2);
                s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], y[pYY1y], s1);
                s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA], y[pYY1y--], s2);
                s1 = Basicop.amrnb_fxp_msu_16_by_16bb(a[pA++], y[pYY1y], s1);
            }

            if (((long) (s1 + 134217728) & 0xffffffffL) < 0x0fffffffL) {
                temp = ((s1 >> 12) << 16) >> 16;
            } else if (s1 > 0x07ffffff) {
                temp = Basicop.MAX_16;
            } else {
                temp = Basicop.MIN_16;
            }

            s2 = Basicop.amrnb_fxp_msu_16_by_16bb(a[aOff + 1], temp, s2);

            y[pY++] = (short) temp;
            pYY1y = pY; /* C: p_yy1 = p_y (slot written by s2 below) */

            if (((long) (s2 + 134217728) & 0xffffffffL) < 0x0fffffffL) {
                y[pY++] = (short) ((s2 >> 12) << 16 >> 16);
            } else if (s2 > 0x07ffffff) {
                y[pY++] = Basicop.MAX_16;
            } else {
                y[pY++] = Basicop.MIN_16;
            }
        }

        /* Update of memory if update==1 */
        if (update != 0) {
            for (int i = 0; i < Cnst.M; i++) {
                mem[memOff + i] = y[yOff + lg - Cnst.M + i];
            }
        }
    }

    private static final short[] predLtCoeff = new short[L_INTER10 << 1]; /* C: Word16 Coeff_1[20] */

    /**
     * pred_lt.cpp Pred_lt_3or6: adaptive codebook prediction, writes
     * exc[excOff .. excOff+L_subfr-1] interpolating from exc[excOff-T0 ...].
     * (pOverflow intentionally unused, as in C)
     */
    public static void Pred_lt_3or6(short[] exc, int excOff, int T0, int frac,
                                    int L_subfr, int flag3, int[] pOverflow) {
        int s1, s2;
        final short[] Coeff_1 = predLtCoeff;

        int pX0 = excOff - T0;

        /* frac goes between -3 and 3 */
        frac = -frac;

        if (flag3 != 0) {
            frac <<= 1; /* inter_3l[k] = inter_6[2*k] -> k' = 2*k */
        }

        if (frac < 0) {
            frac += UP_SAMP_MAX;
            pX0--;
        }

        int pC1ref = frac;               /* &inter_6_pred_lt[frac] */
        int pC2ref = UP_SAMP_MAX - frac; /* &inter_6_pred_lt[UP_SAMP_MAX - frac] */
        int pC1 = 0;
        int k = 0;

        for (int i = L_INTER10 >> 1; i > 0; i--) {
            Coeff_1[pC1++] = inter_6_pred_lt[pC1ref + k];
            Coeff_1[pC1++] = inter_6_pred_lt[pC2ref + k];
            k += UP_SAMP_MAX;
            Coeff_1[pC1++] = inter_6_pred_lt[pC1ref + k];
            Coeff_1[pC1++] = inter_6_pred_lt[pC2ref + k];
            k += UP_SAMP_MAX;
        }

        int pExc = excOff;
        for (int j = L_subfr >> 1; j != 0; j--) {
            pX0++;
            int pX2 = pX0;
            int pX3 = pX0++;
            pC1 = 0;
            s1 = 0x00004000;
            s2 = 0x00004000;

            for (int i = L_INTER10 >> 1; i > 0; i--) {
                s2 += exc[pX3--] * Coeff_1[pC1];
                s1 += exc[pX3] * Coeff_1[pC1++];
                s1 += exc[pX2++] * Coeff_1[pC1];
                s2 += exc[pX2] * Coeff_1[pC1++];
                s2 += exc[pX3--] * Coeff_1[pC1];
                s1 += exc[pX3] * Coeff_1[pC1++];
                s1 += exc[pX2++] * Coeff_1[pC1];
                s2 += exc[pX2] * Coeff_1[pC1++];
            }
            exc[pExc++] = (short) ((s1 >> 15) << 16 >> 16);
            exc[pExc++] = (short) ((s2 >> 15) << 16 >> 16);
        }
    }
}

/**
 * LSP/LSF conversion functions, ported from opencore-amr 0.1.6 common/src:
 *   lsp_az.cpp (Get_lsp_pol, Lsp_Az), lsp_lsf.cpp (Lsf_lsp, Lsp_lsf),
 *   az_lsp.cpp (Chebps, Az_lsp), reorder.cpp (Reorder_lsf),
 *   lsp_lsf_tbl.cpp, grid_tbl.cpp
 * (via src/common/lsp_fns.js of the JS reference port).
 */
final class LspFns {
    private LspFns() {}

    public static final int NC = Cnst.M / 2;
    public static final int grid_points = 60;

    private static final int[] f1Pol = new int[6];
    private static final int[] f2Pol = new int[6];

    /** lsp_az.cpp Get_lsp_pol (static): f is int[6] */
    private static void Get_lsp_pol(short[] lsp, int lspOff, int[] f, int[] pOverflow) {
        int hi;
        int lo;
        int t0;
        int fi = 0;
        int li = lspOff;

        /* f[0] = 1.0 */
        f[fi++] = 0x01000000;
        f[fi++] = -lsp[li++] << 10; /* f[1] = -2.0 * lsp[0] */
        li++; /* Advance lsp pointer */

        for (int i = 2; i <= 5; i++) {
            f[fi] = f[fi - 2];

            for (int j = 1; j < i; j++) {
                hi = (f[fi - 1] >> 16) << 16 >> 16;
                lo = ((f[fi - 1] >> 1) - (hi << 15)) << 16 >> 16;

                t0 = hi * lsp[li];
                t0 += (lo * lsp[li]) >> 15;

                f[fi] = f[fi] + f[fi - 2]; /* *f += f[-2] */
                f[fi] = f[fi] - (t0 << 2); /* *f -= t0 */
                fi--;
            }

            f[fi] = f[fi] - (lsp[li++] << 10);
            fi += i;
            li++;
        }
    }

    /** lsp_az.cpp Lsp_Az: lsp[10] -> a[11] (Q12) */
    public static void Lsp_Az(short[] lsp, int lspOff, short[] a, int aOff, int[] pOverflow) {
        int t0;
        int t1;
        final int[] f1 = f1Pol;
        final int[] f2 = f2Pol;

        Get_lsp_pol(lsp, lspOff, f1, pOverflow);
        Get_lsp_pol(lsp, lspOff + 1, f2, pOverflow);

        int pF1 = 5;
        int pF2 = 5;
        for (int i = 5; i > 0; i--) {
            f1[pF1] = f1[pF1] + f1[i - 1]; /* C: *(p_f1--) += f1[i-1] */
            pF1--;
            f2[pF2] = f2[pF2] - f2[i - 1]; /* C: *(p_f2--) -= f2[i-1] */
            pF2--;
        }

        int pA = aOff;
        a[pA++] = 4096;
        int iF1 = 1;
        int iF2 = 1;
        for (int i = 1, j = 10; i <= 5; i++, j--) {
            t0 = f1[iF1] + f2[iF2];     /* f1[i] + f2[i] */
            t1 = f1[iF1++] - f2[iF2++]; /* f1[i] - f2[i] */

            t0 = t0 + (1 << 12);
            t1 = t1 + (1 << 12);

            a[pA++] = (short) ((t0 >> 13) << 16 >> 16);
            a[aOff + j] = (short) ((t1 >> 13) << 16 >> 16);
        }
    }

    /** lsp_lsf.cpp Lsf_lsp: lsf[m] -> lsp[m] */
    public static void Lsf_lsp(short[] lsf, int lsfOff, short[] lsp, int lspOff, int m, int[] pOverflow) {
        for (int i = 0; i < m; i++) {
            final int ind = lsf[lsfOff + i] >> 8;       /* ind    = b8-b15 of lsf[i] */
            final int offset = lsf[lsfOff + i] & 0x00ff; /* offset = b0-b7 of lsf[i] */

            /* lsp[i] = Tables.table[ind] + ((Tables.table[ind+1]-Tables.table[ind])*offset) / 256 */
            final int L_tmp = ((Tables.table[ind + 1] - Tables.table[ind]) * offset) >> 8;
            lsp[lspOff + i] = (short) ((Tables.table[ind] + ((L_tmp << 16) >> 16)) << 16 >> 16);
        }
    }

    /** lsp_lsf.cpp Lsp_lsf: lsp[m] -> lsf[m] (pOverflow intentionally unused) */
    public static void Lsp_lsf(short[] lsp, int lspOff, short[] lsf, int lsfOff, int m, int[] pOverflow) {
        int ind = 63; /* begin at end of table -1 */
        int pLsp = lspOff + m - 1;
        int pLsf = lsfOff + m - 1;

        for (int i = m - 1; i >= 0; i--) {
            /* find value in table that is just greater than lsp[i] */
            final int temp = lsp[pLsp--];
            while (Tables.table[ind] < temp) {
                ind--;
            }

            /* acos(lsp[i]) = ind*256 + ((lsp[i]-Tables.table[ind]) * Tables.slope[ind])/4096 */
            int L_tmp = (temp - Tables.table[ind]) * Tables.slope[ind];
            L_tmp = ((L_tmp + 0x00000800)) >> 12;
            lsf[pLsf--] = (short) ((((L_tmp << 16) >> 16) + (ind << 8)) << 16 >> 16);
        }
    }

    /** reorder.cpp Reorder_lsf (pOverflow intentionally unused) */
    public static void Reorder_lsf(short[] lsf, int lsfOff, int min_dist, int n, int[] pOverflow) {
        int lsf_min = min_dist;
        int p = lsfOff;
        for (int i = 0; i < n; i++) {
            if (lsf[p] < lsf_min) {
                lsf[p++] = (short) lsf_min;
                lsf_min = (lsf_min + min_dist) << 16 >> 16;
            } else {
                lsf_min = (lsf[p++] + min_dist) << 16 >> 16;
            }
        }
    }

    /** az_lsp.cpp Chebps (static; pOverflow intentionally unused) */
    private static int Chebps(int x, short[] f, int n, int[] pOverflow) {
        int cheb;
        int b1_h;
        int b1_l;
        int t0;
        int L_temp;
        int pF = 1;

        /* L_temp = 1.0 */
        L_temp = 0x01000000;

        t0 = (x << 10) + (f[pF++] << 14);

        /* b1 = t0 = 2*x + f[1] */
        b1_h = (t0 >> 16) << 16 >> 16;
        b1_l = ((t0 >> 1) - (b1_h << 15)) << 16 >> 16;

        for (int i = 2; i < n; i++) {
            /* t0 = 2.0*x*b1 */
            t0 = b1_h * x;
            t0 += (b1_l * x) >> 15;
            t0 <<= 2;

            /* t0 = 2.0*x*b1 - b2 */
            t0 -= L_temp;

            /* t0 = 2.0*x*b1 - b2 + f[i] */
            t0 += (f[pF++] << 14);

            L_temp = (b1_h << 16) + (b1_l << 1);

            /* b0 = 2.0*x*b1 - b2 + f[i] */
            b1_h = (t0 >> 16) << 16 >> 16;
            b1_l = ((t0 >> 1) - (b1_h << 15)) << 16 >> 16;
        }

        /* t0 = x*b1 */
        t0 = b1_h * x;
        t0 += (b1_l * x) >> 15;
        t0 <<= 1;

        /* t0 = x*b1 - b2 */
        t0 -= L_temp;

        /* t0 = x*b1 - b2 + f[i]/2 */
        t0 += (f[pF] << 13);

        if (((long) (t0 + 33554432) & 0xffffffffL) < 67108863L) {
            cheb = (t0 >> 10) << 16 >> 16;
        } else if (t0 > 0x01ffffff) {
            cheb = Basicop.MAX_16;
        } else {
            cheb = Basicop.MIN_16;
        }

        return cheb;
    }

    private static final short[] azF1 = new short[NC + 1];
    private static final short[] azF2 = new short[NC + 1];

    /** az_lsp.cpp Az_lsp: a[MP1] -> lsp[M] (falls back to old_lsp if <10 roots) */
    public static void Az_lsp(short[] a, int aOff, short[] lsp, int lspOff,
                              short[] old_lsp, int old_lspOff, int[] pOverflow) {
        int xlow, ylow, xhigh, yhigh, xmid, ymid, xint;
        int x, y, sign, exp;
        final short[] f1 = azF1;
        final short[] f2 = azF2;

        f1[0] = 1024; /* f1[0] = 1.0 */
        f2[0] = 1024; /* f2[0] = 1.0 */

        for (int i = 0; i < NC; i++) {
            final int L_temp1 = a[aOff + i + 1];
            final int L_temp2 = a[aOff + Cnst.M - i];
            /* x = (a[i+1] + a[M-i]) >> 2 */
            x = ((L_temp1 + L_temp2) >> 2) << 16 >> 16;
            /* y = (a[i+1] - a[M-i]) >> 2 */
            y = ((L_temp1 - L_temp2) >> 2) << 16 >> 16;
            /* f1[i+1] = a[i+1] + a[M-i] - f1[i] */
            f1[i + 1] = (short) (x - f1[i]); /* short store truncates as the C Word16 does */
            /* f2[i+1] = a[i+1] - a[M-i] + f2[i] */
            f2[i + 1] = (short) (y + f2[i]);
        }

        int nf = 0; /* number of found frequencies */
        int ip = 0; /* indicator for f1 or f2 */
        short[] coef = f1;

        xlow = Tables.grid[0];
        ylow = Chebps(xlow, coef, NC, pOverflow);

        int j = 0;
        while (nf < Cnst.M && j < grid_points) {
            j++;
            xhigh = xlow;
            yhigh = ylow;
            xlow = Tables.grid[j];
            ylow = Chebps(xlow, coef, NC, pOverflow);

            if (ylow * yhigh <= 0) {
                /* divide 4 times the interval */
                for (int i = 4; i != 0; i--) {
                    /* xmid = (xlow + xhigh)/2 */
                    x = xlow >> 1;
                    y = xhigh >> 1;
                    xmid = (x + y) << 16 >> 16;

                    ymid = Chebps(xmid, coef, NC, pOverflow);

                    if (ylow * ymid <= 0) {
                        yhigh = ymid;
                        xhigh = xmid;
                    } else {
                        ylow = ymid;
                        xlow = xmid;
                    }
                }

                /* Linear interpolation: xint = xlow - ylow*(xhigh-xlow)/(yhigh-ylow) */
                x = (xhigh - xlow) << 16 >> 16;
                y = (yhigh - ylow) << 16 >> 16;

                if (y == 0) {
                    xint = xlow;
                } else {
                    sign = y;
                    y = Basicop.abs_s(y);
                    exp = Basicop.norm_s(y);
                    y = (y << exp) << 16 >> 16;
                    y = Basicop.div_s(16383, y);
                    y = ((x * y) >> (19 - exp)) << 16 >> 16;

                    if (sign < 0) {
                        y = (-y) << 16 >> 16;
                    }

                    /* xint = xlow - ylow*y */
                    xint = (xlow - ((ylow * y) >> 10)) << 16 >> 16;
                }

                lsp[lspOff + nf] = (short) xint;
                xlow = xint;
                nf++;

                if (ip == 0) {
                    ip = 1;
                    coef = f2;
                } else {
                    ip = 0;
                    coef = f1;
                }
                ylow = Chebps(xlow, coef, NC, pOverflow);
            }
        }

        /* Check if M roots found */
        if (nf < Cnst.M) {
            for (int i = 0; i < Cnst.M; i++) {
                lsp[lspOff + i] = old_lsp[old_lspOff + i];
            }
        }
    }
}

/**
 * LPC interpolation, ported from opencore-amr 0.1.6 common/src/int_lpc.cpp
 * and lsfwt.cpp (via src/common/int_lpc.js of the JS reference port).
 */
final class IntLpc {
    private IntLpc() {}

    private static final short[] lspTmp = new short[Cnst.M];

    /** int_lpc.cpp Int_lpc_1and3: Az[AZ_SIZE] for all 4 subframes */
    public static void Int_lpc_1and3(short[] lsp_old, int lsp_oldOff, short[] lsp_mid, int lsp_midOff,
                                     short[] lsp_new, int lsp_newOff, short[] Az, int AzOff, int[] pOverflow) {
        final short[] lsp = lspTmp;

        /* lsp[i] = lsp_mid[i] * 0.5 + lsp_old[i] * 0.5 */
        for (int i = 0; i < Cnst.M; i++) {
            lsp[i] = (short) ((lsp_old[lsp_oldOff + i] >> 1) + (lsp_mid[lsp_midOff + i] >> 1));
        }

        LspFns.Lsp_Az(lsp, 0, Az, AzOff, pOverflow); /* Subframe 1 */
        LspFns.Lsp_Az(lsp_mid, lsp_midOff, Az, AzOff + Cnst.MP1, pOverflow); /* Subframe 2 */

        for (int i = 0; i < Cnst.M; i++) {
            lsp[i] = (short) ((lsp_mid[lsp_midOff + i] >> 1) + (lsp_new[lsp_newOff + i] >> 1));
        }

        LspFns.Lsp_Az(lsp, 0, Az, AzOff + 2 * Cnst.MP1, pOverflow); /* Subframe 3 */
        LspFns.Lsp_Az(lsp_new, lsp_newOff, Az, AzOff + 3 * Cnst.MP1, pOverflow); /* Subframe 4 */
    }

    /** int_lpc.cpp Int_lpc_1and3_2: only subframes 1 and 3 (2,4 already known) */
    public static void Int_lpc_1and3_2(short[] lsp_old, int lsp_oldOff, short[] lsp_mid, int lsp_midOff,
                                       short[] lsp_new, int lsp_newOff, short[] Az, int AzOff, int[] pOverflow) {
        final short[] lsp = lspTmp;

        for (int i = 0; i < Cnst.M; i++) {
            lsp[i] = (short) ((lsp_old[lsp_oldOff + i] >> 1) + (lsp_mid[lsp_midOff + i] >> 1));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff, pOverflow); /* Subframe 1 */

        for (int i = 0; i < Cnst.M; i++) {
            lsp[i] = (short) ((lsp_mid[lsp_midOff + i] >> 1) + (lsp_new[lsp_newOff + i] >> 1));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff + 2 * Cnst.MP1, pOverflow); /* Subframe 3 */
    }

    /** int_lpc.cpp Int_lpc_1to3: Az[AZ_SIZE] for all 4 subframes */
    public static void Int_lpc_1to3(short[] lsp_old, int lsp_oldOff, short[] lsp_new, int lsp_newOff,
                                    short[] Az, int AzOff, int[] pOverflow) {
        final short[] lsp = lspTmp;
        int temp;

        for (int i = 0; i < Cnst.M; i++) {
            temp = (lsp_old[lsp_oldOff + i] - (lsp_old[lsp_oldOff + i] >> 2)) << 16 >> 16;
            lsp[i] = (short) (temp + (lsp_new[lsp_newOff + i] >> 2));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff, pOverflow); /* Subframe 1 */

        for (int i = 0; i < Cnst.M; i++) {
            lsp[i] = (short) ((lsp_new[lsp_newOff + i] >> 1) + (lsp_old[lsp_oldOff + i] >> 1));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff + Cnst.MP1, pOverflow); /* Subframe 2 */

        for (int i = 0; i < Cnst.M; i++) {
            temp = (lsp_new[lsp_newOff + i] - (lsp_new[lsp_newOff + i] >> 2)) << 16 >> 16;
            lsp[i] = (short) (temp + (lsp_old[lsp_oldOff + i] >> 2));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff + 2 * Cnst.MP1, pOverflow); /* Subframe 3 */

        LspFns.Lsp_Az(lsp_new, lsp_newOff, Az, AzOff + 3 * Cnst.MP1, pOverflow); /* Subframe 4 */
    }

    /** int_lpc.cpp Int_lpc_1to3_2: only subframes 1, 2, 3 (4 already known) */
    public static void Int_lpc_1to3_2(short[] lsp_old, int lsp_oldOff, short[] lsp_new, int lsp_newOff,
                                      short[] Az, int AzOff, int[] pOverflow) {
        final short[] lsp = lspTmp;
        int temp;

        for (int i = 0; i < Cnst.M; i++) {
            temp = (lsp_old[lsp_oldOff + i] - (lsp_old[lsp_oldOff + i] >> 2)) << 16 >> 16;
            lsp[i] = (short) (temp + (lsp_new[lsp_newOff + i] >> 2));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff, pOverflow); /* Subframe 1 */

        for (int i = 0; i < Cnst.M; i++) {
            lsp[i] = (short) ((lsp_new[lsp_newOff + i] >> 1) + (lsp_old[lsp_oldOff + i] >> 1));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff + Cnst.MP1, pOverflow); /* Subframe 2 */

        for (int i = 0; i < Cnst.M; i++) {
            temp = (lsp_new[lsp_newOff + i] - (lsp_new[lsp_newOff + i] >> 2)) << 16 >> 16;
            lsp[i] = (short) (temp + (lsp_old[lsp_oldOff + i] >> 2));
        }
        LspFns.Lsp_Az(lsp, 0, Az, AzOff + 2 * Cnst.MP1, pOverflow); /* Subframe 3 */
    }

    /** lsfwt.cpp Lsf_wt (pOverflow intentionally unused) */
    public static void Lsf_wt(short[] lsf, int lsfOff, short[] wf, int wfOff, int[] pOverflow) {
        int temp;
        int wgt_fct;
        int pWf = wfOff;
        int pLsf = lsfOff;
        int pLsf2 = lsfOff + 1;

        /* wf[0] = lsf[1] - 0 */
        wf[pWf++] = lsf[pLsf2++];
        for (int i = 4; i != 0; i--) {
            wf[pWf++] = (short) (lsf[pLsf2++] - lsf[pLsf++]);
            wf[pWf++] = (short) (lsf[pLsf2++] - lsf[pLsf++]);
        }
        /* wf[9] = 4000 - lsf[8] */
        wf[pWf] = (short) (16384 - lsf[pLsf]);

        pWf = wfOff;
        for (int i = 10; i != 0; i--) {
            /* (wf[i] - 450); 1843 == 450 Hz (Q15 considering 7FFF = 8000 Hz) */
            wgt_fct = wf[pWf];
            temp = (wgt_fct - 1843) << 16 >> 16;

            if (temp > 0) {
                temp = (temp * 6242) >> 15 << 16 >> 16;
                wgt_fct = (1843 - temp) << 16 >> 16;
            } else {
                temp = (wgt_fct * 28160) >> 15 << 16 >> 16;
                wgt_fct = (3427 - temp) << 16 >> 16;
            }

            wf[pWf++] = (short) (wgt_fct << 3); /* short store truncates as C Word16 */
        }
    }
}

/**
 * Codebook gain MA prediction, ported from opencore-amr 0.1.6
 * common/src/gc_pred.cpp + common/include/gc_pred.h
 * (via src/common/gc_pred.js of the JS reference port).
 * C output pointers (Word16 *exp_gcode0 etc.) become short[1] parameters.
 */
final class GcPred {
    private GcPred() {}

    public static final int NPRED = 4; /* number of prediction taps */
    public static final int MEAN_ENER_MR122 = 783741; /* 36/(20*log10(2)) (Q17) */
    public static final int MIN_ENERGY = -14336;      /* 14                 Q10 */
    public static final int MIN_ENERGY_MR122 = -2381; /* 14 / (20*log10(2)) Q10 */

    /* MA prediction coefficients (Q13) and MR122 version (Q6) */
    public static final short[] pred = { 5571, 4751, 2785, 1556 };
    public static final short[] pred_MR122 = { 44, 37, 22, 12 };

    /** gc_pred.h gc_predState */
    public static final class State {
        public short[] past_qua_en;       /* normal MA memory, Q10 */
        public short[] past_qua_en_MR122; /* MR122 MA memory,  Q10 */

        public State() {
            this.past_qua_en = new short[NPRED];
            this.past_qua_en_MR122 = new short[NPRED];
            reset();
        }

        /** gc_pred.cpp gc_pred_reset */
        public int reset() {
            for (int i = 0; i < NPRED; i++) {
                past_qua_en[i] = (short) MIN_ENERGY;
                past_qua_en_MR122[i] = (short) MIN_ENERGY_MR122;
            }
            return 0;
        }
    }

    private static final short[] scratchExp = new short[1];
    private static final short[] scratchFrac = new short[1];

    /** gc_pred.cpp gc_pred */
    public static void gc_pred(State st, int mode, short[] code, int codeOff,
                               short[] exp_gcode0, short[] frac_gcode0,
                               short[] exp_en, short[] frac_en, int[] pOverflow) {
        int L_temp1, L_temp2;
        int L_tmp;
        int ener_code;
        int ener;
        int exp_code, gcode0;
        int tmp;
        int pCode = codeOff;

        /* energy of code: ener_code = sum(code[i]^2) */
        ener_code = 0;
        /* MR122: Q12*Q12 -> Q25 ; others: Q13*Q13 -> Q27 */
        for (int i = Cnst.L_SUBFR >> 2; i != 0; i--) {
            tmp = code[pCode++];
            ener_code += (tmp * tmp) >> 3;
            tmp = code[pCode++];
            ener_code += (tmp * tmp) >> 3;
            tmp = code[pCode++];
            ener_code += (tmp * tmp) >> 3;
            tmp = code[pCode++];
            ener_code += (tmp * tmp) >> 3;
        }
        ener_code <<= 4; /* C Word32 shift wraps */

        if ((ener_code >> 31) != 0) {
            /* Check for saturation */
            ener_code = Basicop.MAX_32;
        }

        if (mode == Cnst.MR122) {
            /* ener_code = ener_code / lcode; lcode = 40; 1/40 = 26214 Q20 */
            ener_code = (Basicop.pv_round(ener_code, pOverflow) * 26214) << 1;

            /* ener_code = 1/2 * Log2(ener_code); Note: Log2=log2+30 */
            Mathops.Log2(ener_code, scratchExp, scratchFrac, pOverflow);
            final int exp = scratchExp[0];
            final int frac = scratchFrac[0];

            /* Q16 for log() -> Q17 for 1/2 log() */
            L_temp1 = (exp - 30) << 16;
            ener_code = L_temp1 + (frac << 1);

            /* predicted energy: ener(Q24) = MEAN_ENER + sum(pred[i]*past_qua_en[i]) */
            ener = MEAN_ENER_MR122; /* Q24 (Q17) */
            for (int i = 0; i < NPRED; i++) {
                L_temp1 = (st.past_qua_en_MR122[i] * pred_MR122[i]) << 1;
                ener = Basicop.L_add(ener, L_temp1, pOverflow);
                /* Q10 * Q6 -> Q17 */
            }

            /* predicted codebook gain: gc0 = Pow2(ener - ener_code) */
            /* Q16 */
            L_temp1 = Basicop.L_sub(ener, ener_code, pOverflow);
            exp_gcode0[0] = (short) ((L_temp1 >> 17) << 16 >> 16);
            L_temp2 = exp_gcode0[0] << 15;
            L_temp1 >>= 2;
            frac_gcode0[0] = (short) (((L_temp1 - L_temp2) << 16) >> 16);
        } else {
            /* all modes except 12.2 */
            /* Compute: means_ener - 10log10(ener_code/L_SUBFR) */
            exp_code = Basicop.norm_l(ener_code);
            ener_code = Basicop.L_shl(ener_code, exp_code, pOverflow);

            /* Log2 = log2 + 27 */
            Mathops.Log2_norm(ener_code, exp_code, scratchExp, scratchFrac);
            final int exp = scratchExp[0];
            final int frac = scratchFrac[0];

            /* fact = 10/log2(10) = 3.01 = 24660 Q13 */
            L_temp2 = (exp * -24660) << 1;
            L_tmp = (frac * -24660) >> 15;

            /* Sign-extend resulting product */
            if ((L_tmp & 0x00010000) != 0) {
                L_tmp = L_tmp | 0xffff0000;
            }
            L_tmp <<= 1;
            L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow);

            if (mode == Cnst.MR102) {
                /* mean = 33 dB */
                L_temp2 = 16678 << 7;
                L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow); /* Q14 */
            } else if (mode == Cnst.MR795) {
                /* exp_en = -11-exp_code */
                frac_en[0] = (short) ((ener_code >> 16) << 16 >> 16);
                exp_en[0] = (short) (-11 - exp_code);

                /* mean = 36 dB */
                L_temp2 = 17062 << 7;
                L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow); /* Q14 */
            } else if (mode == Cnst.MR74) {
                /* mean = 30 dB */
                L_temp2 = 32588 << 6;
                L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow); /* Q14 */
            } else if (mode == Cnst.MR67) {
                /* mean = 28.75 dB */
                L_temp2 = 32268 << 6;
                L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow); /* Q14 */
            } else {
                /* MR59, MR515, MR475: mean = 33 dB */
                L_temp2 = 16678 << 7;
                L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow); /* Q14 */
            }

            /* Compute gcode0: Sum(pred[i]*past_qua_en[i]) - ener_code + mean_ener */
            /* Q24 */
            if (L_tmp > 0x001fffff) {
                pOverflow[0] = 1;
                L_tmp = Basicop.MAX_32;
            } else if (L_tmp < -2097152) {
                pOverflow[0] = 1;
                L_tmp = Basicop.MIN_32;
            } else {
                L_tmp <<= 10;
            }

            for (int i = 0; i < 4; i++) {
                L_temp2 = (pred[i] * st.past_qua_en[i]) << 1;
                L_tmp = Basicop.L_add(L_tmp, L_temp2, pOverflow); /* Q13 * Q10 -> Q24 */
            }

            gcode0 = (L_tmp >> 16) << 16 >> 16; /* Q8 */

            /* gcode0 = pow(10.0, gcode0/20) = pow(2, 0.166*gcode0) */
            if (mode == Cnst.MR74) {
                /* For IS641 bitexactness: 5439 Q15 = 0.165985 */
                L_tmp = (gcode0 * 5439) << 1; /* Q8 * Q15 -> Q24 */
            } else {
                L_tmp = (gcode0 * 5443) << 1; /* Q8 * Q15 -> Q24 */
            }

            if (L_tmp < 0) {
                L_tmp = ~((~L_tmp) >> 8);
            } else {
                L_tmp >>= 8; /* -> Q16 */
            }

            exp_gcode0[0] = (short) ((L_tmp >> 16) << 16 >> 16);

            if (L_tmp < 0) {
                L_temp1 = ~((~L_tmp) >> 1);
            } else {
                L_temp1 = L_tmp >> 1;
            }
            L_temp2 = exp_gcode0[0] << 15;
            frac_gcode0[0] = (short) ((Basicop.L_sub(L_temp1, L_temp2, pOverflow) << 16) >> 16);
            /* -> Q0.Q15 */
        }
    }

    /** gc_pred.cpp gc_pred_update */
    public static void gc_pred_update(State st, int qua_ener_MR122, int qua_ener) {
        st.past_qua_en[3] = st.past_qua_en[2];
        st.past_qua_en_MR122[3] = st.past_qua_en_MR122[2];

        st.past_qua_en[2] = st.past_qua_en[1];
        st.past_qua_en_MR122[2] = st.past_qua_en_MR122[1];

        st.past_qua_en[1] = st.past_qua_en[0];
        st.past_qua_en_MR122[1] = st.past_qua_en_MR122[0];

        st.past_qua_en_MR122[0] = (short) qua_ener_MR122; /*    log2 (qua_err), Q10 */
        st.past_qua_en[0] = (short) qua_ener;             /* 20*log10(qua_err), Q10 */
    }

    /**
     * gc_pred.cpp gc_pred_average_limited.
     * @param ener_avg_MR122 short[1] out
     * @param ener_avg short[1] out
     */
    public static void gc_pred_average_limited(State st, short[] ener_avg_MR122,
                                               short[] ener_avg, int[] pOverflow) {
        int av_pred_en;

        /* do average in MR122 mode (log2() domain) */
        av_pred_en = 0;
        for (int i = 0; i < NPRED; i++) {
            av_pred_en = Basicop.add_16(av_pred_en, st.past_qua_en_MR122[i], pOverflow);
        }

        /* av_pred_en = 0.25*av_pred_en (with sign-extension) */
        if (av_pred_en < 0) {
            av_pred_en = ((av_pred_en >> 2) | 0xc000) << 16 >> 16;
        } else {
            av_pred_en >>= 2;
        }

        if (av_pred_en < MIN_ENERGY_MR122) {
            av_pred_en = MIN_ENERGY_MR122;
        }
        ener_avg_MR122[0] = (short) av_pred_en;

        /* do average for other modes (20*log10() domain) */
        av_pred_en = 0;
        for (int i = 0; i < NPRED; i++) {
            av_pred_en = Basicop.add_16(av_pred_en, st.past_qua_en[i], pOverflow);
        }

        if (av_pred_en < 0) {
            av_pred_en = ((av_pred_en >> 2) | 0xc000) << 16 >> 16;
        } else {
            av_pred_en >>= 2;
        }

        if (av_pred_en < MIN_ENERGY) {
            av_pred_en = MIN_ENERGY;
        }
        ener_avg[0] = (short) av_pred_en;
    }
}

/**
 * AGC, ported from opencore-amr 0.1.6 dec/src/agc.cpp (energy_old,
 * energy_new, agcState, agc_reset, agc, agc2)
 * (via src/dec/agc.js of the JS reference port).
 */
final class Agc {
    private Agc() {}

    /** agc.h agcState */
    public static final class State {
        public int past_gain;

        public State() {
            reset();
        }

        /** agc.cpp agc_reset */
        public int reset() {
            this.past_gain = 4096; /* initial value of past_gain = 1.0 */
            return 0;
        }
    }

    /** agc.cpp energy_old (static) */
    private static int energy_old(short[] input, int inOff, int l_trm, int[] pOverflow) {
        int s = 0;
        int temp;
        for (int i = 0; i < l_trm; i++) {
            temp = input[inOff + i] >> 2;
            s = Basicop.L_mac(s, temp, temp, pOverflow);
        }
        return s;
    }

    /** agc.cpp energy_new (static) */
    private static int energy_new(short[] input, int inOff, int l_trm, int[] pOverflow) {
        int s = 0;
        final int ov_save = pOverflow[0]; /* save in case energy_old must be called */

        for (int i = 0; i < l_trm; i++) {
            s = Basicop.L_mac(s, input[inOff + i], input[inOff + i], pOverflow);
        }

        /* check for overflow */
        if (s != Basicop.MAX_32) {
            /* s is a sum of squares, so it won't be negative */
            s = s >> 4;
        } else {
            pOverflow[0] = ov_save; /* restore overflow flag */
            s = energy_old(input, inOff, l_trm, pOverflow);
        }
        return s;
    }

    /** agc.cpp agc */
    public static void agc(State st, short[] sig_in, int sig_inOff, short[] sig_out, int sig_outOff,
                           int agc_fac, int l_trm, int[] pOverflow) {
        int i;
        int exp;
        int gain_in;
        int gain_out;
        int g0;
        int gain;
        int s;
        int L_temp;
        int temp;

        /* calculate gain_out with exponent */
        s = energy_new(sig_out, sig_outOff, l_trm, pOverflow);
        if (s == 0) {
            st.past_gain = 0;
            return;
        }
        exp = (Basicop.norm_l(s) - 1) << 16 >> 16;
        L_temp = Basicop.L_shl(s, exp, pOverflow);
        gain_out = Basicop.pv_round(L_temp, pOverflow);

        /* calculate gain_in with exponent */
        s = energy_new(sig_in, sig_inOff, l_trm, pOverflow);
        if (s == 0) {
            g0 = 0;
        } else {
            i = Basicop.norm_l(s);
            /* L_temp = L_shl(s, i, pOverflow); */
            L_temp = s << i;
            gain_in = Basicop.pv_round(L_temp, pOverflow);
            exp = (exp - i) << 16 >> 16;

            /* g0 = (1-agc_fac) * sqrt(gain_in/gain_out) */
            /* s = gain_out / gain_in */
            temp = Basicop.div_s(gain_out, gain_in);
            s = temp;
            s = s << 7;
            s = Basicop.L_shr(s, exp, pOverflow); /* add exponent */
            s = Mathops.Inv_sqrt(s, pOverflow);
            L_temp = s << 9;
            i = ((L_temp + 0x00008000) >> 16) << 16 >> 16;

            /* g0 = i * (1-agc_fac) */
            temp = (32767 - agc_fac) << 16 >> 16;
            g0 = ((i * temp) >> 15) << 16 >> 16;
        }

        /* compute gain[n] = agc_fac*gain[n-1] + (1-agc_fac)*sqrt(gain_in/gain_out)
           sig_out[n] = gain[n] * sig_out[n] */
        gain = st.past_gain;
        int pSig = sig_outOff;
        for (i = 0; i < l_trm; i++) {
            gain = ((gain * agc_fac) >> 15) << 16 >> 16;
            gain = (gain + g0) << 16 >> 16; /* C Word16 += without saturation */
            L_temp = (sig_out[pSig] * gain) << 1;
            sig_out[pSig++] = (short) ((L_temp >> 13) << 16 >> 16);
        }
        st.past_gain = gain;
    }

    /** agc.cpp agc2 */
    public static void agc2(short[] sig_in, int sig_inOff, short[] sig_out, int sig_outOff,
                            int l_trm, int[] pOverflow) {
        int i;
        int exp;
        int gain_in;
        int gain_out;
        int g0;
        int s;
        int L_temp;
        int temp;

        /* calculate gain_out with exponent */
        s = energy_new(sig_out, sig_outOff, l_trm, pOverflow);
        if (s == 0) {
            return;
        }
        exp = (Basicop.norm_l(s) - 1) << 16 >> 16;
        L_temp = Basicop.L_shl(s, exp, pOverflow);
        gain_out = Basicop.pv_round(L_temp, pOverflow);

        /* calculate gain_in with exponent */
        s = energy_new(sig_in, sig_inOff, l_trm, pOverflow);
        if (s == 0) {
            g0 = 0;
        } else {
            i = Basicop.norm_l(s);
            L_temp = Basicop.L_shl(s, i, pOverflow);
            gain_in = Basicop.pv_round(L_temp, pOverflow);
            exp = (exp - i) << 16 >> 16;

            /* g0 = sqrt(gain_in/gain_out) */
            temp = Basicop.div_s(gain_out, gain_in);
            s = temp;

            if (s > 0x00ffffff) {
                s = Basicop.MAX_32;
            } else if (s < -16777216) {
                s = Basicop.MIN_32;
            } else {
                s = s << 7;
            }
            s = Basicop.L_shr(s, exp, pOverflow); /* add exponent */
            s = Mathops.Inv_sqrt(s, pOverflow);

            if (s > 0x003fffff) {
                L_temp = Basicop.MAX_32;
            } else if (s < -4194304) {
                L_temp = Basicop.MIN_32;
            } else {
                L_temp = s << 9;
            }
            g0 = Basicop.pv_round(L_temp, pOverflow);
        }

        /* sig_out(n) = gain(n) sig_out(n) */
        for (i = l_trm - 1; i >= 0; i--) {
            L_temp = Basicop.L_mult(sig_out[sig_outOff + i], g0, pOverflow);
            if (L_temp > 0x0fffffff) {
                sig_out[sig_outOff + i] = Basicop.MAX_16;
            } else if (L_temp < -268435456) {
                sig_out[sig_outOff + i] = Basicop.MIN_16;
            } else {
                sig_out[sig_outOff + i] = (short) ((L_temp >> 13) << 16 >> 16);
            }
        }
    }
}

/**
 * Background noise source characteristic detector, ported from
 * opencore-amr 0.1.6 dec/src/bgnscd.cpp
 * (via src/dec/bgnscd.js of the JS reference port).
 */
final class Bgnscd {
    private Bgnscd() {}

    public static final int L_ENERGYHIST = 60;
    public static final int FRAMEENERGYLIMIT = 17578; /* 150 */
    public static final int LOWERNOISELIMIT = 20;     /*   5 */
    public static final int UPPERNOISELIMIT = 1953;   /*  50 */

    /** bgnscd.h Bgn_scdState */
    public static final class State {
        public short[] frameEnergyHist;
        public int bgHangover;

        public State() {
            this.frameEnergyHist = new short[L_ENERGYHIST];
            this.bgHangover = 0;
            reset();
        }

        /** bgnscd.cpp Bgn_scd_reset */
        public int reset() {
            for (int i = 0; i < L_ENERGYHIST; i++) {
                frameEnergyHist[i] = 0;
            }
            this.bgHangover = 0;
            return 0;
        }
    }

    /**
     * bgnscd.cpp Bgn_scd: returns inbgNoise flag.
     * @param voicedHangover short[1] in/out
     */
    public static int Bgn_scd(State st, short[] ltpGainHist, int ltpGainHistOff,
                              short[] speech, int speechOff, short[] voicedHangover, int[] pOverflow) {
        int prevVoiced, inbgNoise;
        int temp;
        int ltpLimit, frameEnergyMin;
        int currEnergy, noiseFloor, maxEnergy, maxEnergyLastPart;
        int s, L_temp;

        /* Update the inBackgroundNoise flag (valid for use in next frame if BFI);
           it works as an energy detector floating on top, not as good as a VAD. */
        s = 0;
        for (int i = Cnst.L_FRAME - 1; i >= 0; i--) {
            L_temp = speech[speechOff + i] * speech[speechOff + i];
            if (L_temp != 0x40000000) {
                L_temp = L_temp << 1;
            } else {
                L_temp = Basicop.MAX_32;
            }
            s = Basicop.L_add(s, L_temp, pOverflow);
        }

        /* s is a sum of squares, so don't need to check for neg overflow */
        if (s > 0x1fffffff) {
            currEnergy = Basicop.MAX_16;
        } else {
            currEnergy = (s >> 14) << 16 >> 16;
        }

        frameEnergyMin = 32767;
        for (int i = L_ENERGYHIST - 1; i >= 0; i--) {
            if (st.frameEnergyHist[i] < frameEnergyMin) {
                frameEnergyMin = st.frameEnergyHist[i];
            }
        }

        /* Frame Energy Margin of 16 */
        L_temp = frameEnergyMin << 4;
        if (L_temp != ((L_temp << 16) >> 16)) {
            if (L_temp > 0) {
                noiseFloor = Basicop.MAX_16;
            } else {
                noiseFloor = Basicop.MIN_16;
            }
        } else {
            noiseFloor = (L_temp << 16) >> 16;
        }

        maxEnergy = st.frameEnergyHist[0];
        for (int i = L_ENERGYHIST - 5; i >= 1; i--) {
            if (maxEnergy < st.frameEnergyHist[i]) {
                maxEnergy = st.frameEnergyHist[i];
            }
        }

        maxEnergyLastPart = st.frameEnergyHist[(2 * L_ENERGYHIST / 3)];
        for (int i = (2 * L_ENERGYHIST / 3) + 1; i < L_ENERGYHIST; i++) {
            if (maxEnergyLastPart < st.frameEnergyHist[i]) {
                maxEnergyLastPart = st.frameEnergyHist[i];
            }
        }

        /* Mark as noise if under current noise limit
           OR if the maximum energy is below the upper limit */
        if (maxEnergy > LOWERNOISELIMIT
            && currEnergy < FRAMEENERGYLIMIT
            && currEnergy > LOWERNOISELIMIT
            && (currEnergy < noiseFloor || maxEnergyLastPart < UPPERNOISELIMIT)) {
            if (st.bgHangover + 1 > 30) {
                st.bgHangover = 30;
            } else {
                st.bgHangover += 1;
            }
        } else {
            st.bgHangover = 0;
        }

        /* make final decision about frame state, act somewhat cautiously */
        inbgNoise = st.bgHangover > 1 ? 1 : 0;

        for (int i = 0; i < L_ENERGYHIST - 1; i++) {
            st.frameEnergyHist[i] = st.frameEnergyHist[i + 1];
        }
        st.frameEnergyHist[L_ENERGYHIST - 1] = (short) currEnergy;

        /* prepare for voicing decision; tighten threshold after some time in noise */
        if (st.bgHangover > 15) {
            ltpLimit = 16383; /* 1.00 Q14 */
        } else if (st.bgHangover > 8) {
            ltpLimit = 15565; /* 0.95 Q14 */
        } else {
            ltpLimit = 13926; /* 0.85 Q14 */
        }

        /* weak sort of voicing indication */
        prevVoiced = 0;
        if (Mathops.gmed_n(ltpGainHist, ltpGainHistOff + 4, 5) > ltpLimit) {
            prevVoiced = 1;
        }

        if (st.bgHangover > 20) {
            if (Mathops.gmed_n(ltpGainHist, ltpGainHistOff, 9) > ltpLimit) {
                prevVoiced = 1;
            } else {
                prevVoiced = 0;
            }
        }

        if (prevVoiced != 0) {
            voicedHangover[0] = 0;
        } else {
            temp = voicedHangover[0] + 1;
            if (temp > 10) {
                voicedHangover[0] = 10;
            } else {
                voicedHangover[0] = (short) temp;
            }
        }

        return inbgNoise;
    }
}

/**
 * Codebook gain averaging, ported from opencore-amr 0.1.6 dec/src/c_g_aver.cpp
 * (via src/dec/c_g_aver.js of the JS reference port).
 */
final class CGaver {
    private CGaver() {}

    public static final int L_CBGAINHIST = 7;

    /** c_g_aver.h Cb_gain_averageState */
    public static final class State {
        public short[] cbGainHistory;
        public int hangVar;
        public int hangCount;

        public State() {
            this.cbGainHistory = new short[L_CBGAINHIST];
            reset();
        }

        /** c_g_aver.cpp Cb_gain_average_reset */
        public int reset() {
            for (int i = 0; i < L_CBGAINHIST; i++) {
                cbGainHistory[i] = 0;
            }
            this.hangVar = 0;
            this.hangCount = 0;
            return 0;
        }
    }

    private static final short[] cgTmp = new short[Cnst.M];

    /** c_g_aver.cpp Cb_gain_average: returns smoothed cb gain (Q1) */
    public static int Cb_gain_average(State st, int mode, int gain_code, short[] lsp, int lspOff,
                                      short[] lspAver, int lspAverOff, int bfi, int prev_bf, int pdfi,
                                      int prev_pdf, int inBackgroundNoise, int voicedHangover, int[] pOverflow) {
        int cbGainMix;
        int diff;
        int tmp_diff;
        int bgMix;
        int cbGainMean;
        int L_sum;
        final short[] tmp = cgTmp;
        int tmp1;
        int tmp2;
        int shift1;
        int shift2;
        int shift;

        /* set correct cbGainMix for MR74, MR795, MR122 */
        cbGainMix = gain_code;

        /* Store list of CB gain needed in the CB gain averaging */
        for (int i = 0; i < L_CBGAINHIST - 1; i++) {
            st.cbGainHistory[i] = st.cbGainHistory[i + 1];
        }
        st.cbGainHistory[L_CBGAINHIST - 1] = (short) gain_code;

        diff = 0;
        /* compute lsp difference */
        for (int i = 0; i < Cnst.M; i++) {
            tmp1 = Basicop.abs_s(Basicop.sub(lspAver[lspAverOff + i], lsp[lspOff + i], pOverflow)); /* Q15 */
            shift1 = (Basicop.norm_s(tmp1) - 1) << 16 >> 16;        /* Qn */
            tmp1 = Basicop.shl(tmp1, shift1, pOverflow);            /* Q15+Qn */
            shift2 = Basicop.norm_s(lspAver[lspAverOff + i]);       /* Qm */
            tmp2 = Basicop.shl(lspAver[lspAverOff + i], shift2, pOverflow); /* Q15+Qm */
            tmp[i] = (short) Basicop.div_s(tmp1, tmp2); /* Q15+(Q15+Qn)-(Q15+Qm) */

            shift = (2 + shift1 - shift2) << 16 >> 16;
            if (shift >= 0) {
                tmp[i] = (short) Basicop.shr(tmp[i], shift, pOverflow); /* Q15+Qn-Qm-Qx=Q13 */
            } else {
                tmp[i] = (short) Basicop.shl(tmp[i], Basicop.negate(shift), pOverflow);
            }

            diff = Basicop.add_16(diff, tmp[i], pOverflow); /* Q13 */
        }

        /* Compute hangover */
        if (diff > 5325) {
            /* 0.65 in Q11 */
            st.hangVar += 1;
        } else {
            st.hangVar = 0;
        }

        if (st.hangVar > 10) {
            /* Speech period, reset hangover variable */
            st.hangCount = 0;
        }

        /* Compute mix constant (bgMix) */
        bgMix = 8192; /* 1 in Q13 */
        if (mode <= Cnst.MR67 || mode == Cnst.MR102) {
            /* MR475, MR515, MR59, MR67, MR102 */
            /* if errors and presumed noise make smoothing probability stronger */
            if (((pdfi != 0 && prev_pdf != 0) || bfi != 0 || prev_bf != 0)
                && voicedHangover > 1
                && inBackgroundNoise != 0
                && (mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59)) {
                /* bgMix = min(0.25, max(0.0, diff-0.55)) / 0.25; */
                tmp_diff = (diff - 4506) << 16 >> 16; /* 0.55 in Q13 */
            } else {
                /* bgMix = min(0.25, max(0.0, diff-0.40)) / 0.25; */
                tmp_diff = (diff - 3277) << 16 >> 16; /* 0.4 in Q13 */
            }

            /* max(0.0, diff-0.55) or max(0.0, diff-0.40) */
            tmp1 = tmp_diff > 0 ? tmp_diff : 0;

            /* min(0.25, tmp1) */
            if (tmp1 > 2048) {
                bgMix = 8192;
            } else {
                bgMix = Basicop.shl(tmp1, 2, pOverflow);
            }

            if (st.hangCount < 40 || diff > 5325) {
                /* 0.65 in Q13: disable mix if too short time since */
                bgMix = 8192;
            }

            /* Smoothen the cb gain trajectory; smoothing depends on bgMix */
            L_sum = Basicop.L_mult(6554, st.cbGainHistory[2], pOverflow); /* 0.2 in Q15 */
            for (int i = 3; i < L_CBGAINHIST; i++) {
                L_sum = Basicop.L_mac(L_sum, 6554, st.cbGainHistory[i], pOverflow);
            }
            cbGainMean = Basicop.pv_round(L_sum, pOverflow); /* Q1 */

            /* more smoothing in error and bg noise (NB no DFI used here) */
            if ((bfi != 0 || prev_bf != 0) && inBackgroundNoise != 0
                && (mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59)) {
                /* 0.143 in Q15 */
                L_sum = Basicop.L_mult(4681, st.cbGainHistory[0], pOverflow);
                for (int i = 1; i < L_CBGAINHIST; i++) {
                    L_sum = Basicop.L_mac(L_sum, 4681, st.cbGainHistory[i], pOverflow);
                }
                cbGainMean = Basicop.pv_round(L_sum, pOverflow); /* Q1 */
            }

            /* cbGainMix = bgMix*cbGainMix + (1-bgMix)*cbGainMean; L_sum in Q15 */
            L_sum = Basicop.L_mult(bgMix, cbGainMix, pOverflow);
            L_sum = Basicop.L_mac(L_sum, 8192, cbGainMean, pOverflow);
            L_sum = Basicop.L_msu(L_sum, bgMix, cbGainMean, pOverflow);
            cbGainMix = Basicop.pv_round(Basicop.L_shl(L_sum, 2, pOverflow), pOverflow); /* Q1 */
        }

        st.hangCount += 1;
        return cbGainMix;
    }
}

/**
 * Error-concealment gains and LSP averaging, ported from opencore-amr 0.1.6
 * dec/src/ec_gains.cpp and dec/src/lsp_avg.cpp
 * (via src/dec/ec_gains.js of the JS reference port).
 */
final class EcGains {
    private EcGains() {}

    /** ec_gains.h ec_gain_pitchState */
    public static final class GainPitchState {
        public short[] pbuf;
        public int past_gain_pit;
        public int prev_gp;

        public GainPitchState() {
            this.pbuf = new short[5];
            reset();
        }

        /** ec_gains.cpp ec_gain_pitch_reset */
        public int reset() {
            for (int i = 0; i < 5; i++) {
                pbuf[i] = 1640;
            }
            this.past_gain_pit = 0;
            this.prev_gp = 16384;
            return 0;
        }
    }

    /** ec_gains.h ec_gain_codeState */
    public static final class GainCodeState {
        public short[] gbuf;
        public int past_gain_code;
        public int prev_gc;

        public GainCodeState() {
            this.gbuf = new short[5];
            reset();
        }

        /** ec_gains.cpp ec_gain_code_reset */
        public int reset() {
            for (int i = 0; i < 5; i++) {
                gbuf[i] = 1;
            }
            this.past_gain_code = 0;
            this.prev_gc = 1;
            return 0;
        }
    }

    public static final short[] cdown = { 32767, 32112, 32112, 32112, 32112, 32112, 22937 };
    public static final short[] pdown = { 32767, 32112, 32112, 26214, 9830, 6553, 6553 };

    private static final short[] ecQuaEnerMR122 = new short[1];
    private static final short[] ecQuaEner = new short[1];

    /**
     * ec_gains.cpp ec_gain_code.
     * @param gain_code short[1] out
     */
    public static void ec_gain_code(GainCodeState st, GcPred.State pred_state, int state,
                                    short[] gain_code, int[] pOverflow) {
        /* calculate median of last five gain values */
        int tmp = Mathops.gmed_n(st.gbuf, 0, 5);

        /* new gain = minimum(median, past_gain) * cdown[state] */
        if (Basicop.sub(tmp, st.past_gain_code, pOverflow) > 0) {
            tmp = st.past_gain_code;
        }
        tmp = Basicop.mult(tmp, cdown[state], pOverflow);
        gain_code[0] = (short) tmp;

        /* update table of past quantized energies with average of current values */
        GcPred.gc_pred_average_limited(pred_state, ecQuaEnerMR122, ecQuaEner, pOverflow);
        GcPred.gc_pred_update(pred_state, ecQuaEnerMR122[0], ecQuaEner[0]);
    }

    /**
     * ec_gains.cpp ec_gain_code_update.
     * @param gain_code short[1] in/out
     */
    public static void ec_gain_code_update(GainCodeState st, int bfi, int prev_bf,
                                           short[] gain_code, int[] pOverflow) {
        /* limit gain_code by previous good gain if previous frame was bad */
        if (bfi == 0) {
            if (prev_bf != 0) {
                if (Basicop.sub(gain_code[0], st.prev_gc, pOverflow) > 0) {
                    gain_code[0] = (short) st.prev_gc;
                }
            }
            st.prev_gc = gain_code[0];
        }

        /* update EC states: previous gain, gain buffer */
        st.past_gain_code = gain_code[0];
        for (int i = 1; i < 5; i++) {
            st.gbuf[i - 1] = st.gbuf[i];
        }
        st.gbuf[4] = gain_code[0];
    }

    /**
     * ec_gains.cpp ec_gain_pitch.
     * @param gain_pitch short[1] out (Q14)
     */
    public static void ec_gain_pitch(GainPitchState st, int state, short[] gain_pitch, int[] pOverflow) {
        /* calculate median of last five gains */
        int tmp = Mathops.gmed_n(st.pbuf, 0, 5);

        /* new gain = minimum(median, past_gain) * pdown[state] */
        if (Basicop.sub(tmp, st.past_gain_pit, pOverflow) > 0) {
            tmp = st.past_gain_pit;
        }
        gain_pitch[0] = (short) Basicop.mult(tmp, pdown[state], pOverflow);
    }

    /**
     * ec_gains.cpp ec_gain_pitch_update.
     * @param gain_pitch short[1] in/out
     */
    public static void ec_gain_pitch_update(GainPitchState st, int bfi, int prev_bf,
                                            short[] gain_pitch, int[] pOverflow) {
        if (bfi == 0) {
            if (prev_bf != 0) {
                if (Basicop.sub(gain_pitch[0], st.prev_gp, pOverflow) > 0) {
                    gain_pitch[0] = (short) st.prev_gp;
                }
            }
            st.prev_gp = gain_pitch[0];
        }

        st.past_gain_pit = gain_pitch[0];
        if (Basicop.sub(st.past_gain_pit, 16384, pOverflow) > 0) {
            /* if (st->past_gain_pit > 1.0) */
            st.past_gain_pit = 16384;
        }
        for (int i = 1; i < 5; i++) {
            st.pbuf[i - 1] = st.pbuf[i];
        }
        st.pbuf[4] = (short) st.past_gain_pit;
    }

    public static final int EXPCONST = 5243; /* 0.16 in Q15 */

    /** lsp_avg.h lsp_avgState */
    public static final class LspAvgState {
        public short[] lsp_meanSave; /* Averaged LSPs */

        public LspAvgState() {
            this.lsp_meanSave = new short[Cnst.M];
            reset();
        }

        /** lsp_avg.cpp lsp_avg_reset */
        public int reset() {
            System.arraycopy(Tables.mean_lsf_5, 0, lsp_meanSave, 0, Cnst.M);
            return 0;
        }
    }

    /** lsp_avg.cpp lsp_avg */
    public static void lsp_avg(LspAvgState st, short[] lsp, int lspOff, int[] pOverflow) {
        int L_tmp; /* Q31 */
        for (int i = 0; i < Cnst.M; i++) {
            /* mean = 0.84*mean */
            L_tmp = st.lsp_meanSave[i] << 16;
            L_tmp = Basicop.L_msu(L_tmp, EXPCONST, st.lsp_meanSave[i], pOverflow);
            /* Add 0.16 of newest LSPs to mean */
            L_tmp = Basicop.L_mac(L_tmp, EXPCONST, lsp[lspOff + i], pOverflow);
            /* Save means */
            st.lsp_meanSave[i] = (short) Basicop.pv_round(L_tmp, pOverflow); /* Q15 */
        }
    }
}

/**
 * LSF decoding, ported from opencore-amr 0.1.6 dec/src:
 *   d_plsf.cpp (D_plsfState, D_plsf_reset), d_plsf_3.cpp (D_plsf_3,
 *   Init_D_plsf_3), d_plsf_5.cpp (D_plsf_5), int_lsf.cpp (Int_lsf)
 * (via src/dec/d_plsf.js of the JS reference port).
 */
final class DPlsf {
    private DPlsf() {}

    /* d_plsf_3.cpp */
    public static final int ALPHA = 29491;    /* ALPHA    ->  0.9         */
    public static final int ONE_ALPHA = 3277; /* ONE_ALPHA-> (1.0-ALPHA)  */
    /* d_plsf_5.cpp uses different smoothing (0.95) */
    public static final int ALPHA_5 = 31128;
    public static final int ONE_ALPHA_5 = 1639;

    public static final int DICO1_SIZE = 256;
    public static final int DICO2_SIZE = 512;
    public static final int DICO3_SIZE = 512;
    public static final int MR515_3_SIZE = 128;
    public static final int MR795_1_SIZE = 512;

    /** d_plsf.h D_plsfState */
    public static final class State {
        public short[] past_r_q;   /* Past quantized prediction error, Q15 */
        public short[] past_lsf_q; /* Past dequantized lsfs,           Q15 */

        public State() {
            this.past_r_q = new short[Cnst.M];
            this.past_lsf_q = new short[Cnst.M];
            reset();
        }

        /** d_plsf.cpp D_plsf_reset */
        public int reset() {
            for (int i = 0; i < Cnst.M; i++) {
                past_r_q[i] = 0;
            }
            System.arraycopy(Tables.mean_lsf_5, 0, past_lsf_q, 0, Cnst.M);
            return 0;
        }
    }

    /** d_plsf_3.cpp Init_D_plsf_3: past_rq_init[] index [0, 7] */
    public static void Init_D_plsf_3(State st, int index) {
        System.arraycopy(Tables.past_rq_init, index * Cnst.M, st.past_r_q, 0, Cnst.M);
    }

    private static final short[] lsf1_r3 = new short[Cnst.M];
    private static final short[] lsf1_q3 = new short[Cnst.M];

    /** d_plsf_3.cpp D_plsf_3 */
    public static void D_plsf_3(State st, int mode, int bfi, short[] indice, int indiceOff,
                                short[] lsp1_q, int lsp1_qOff, int[] pOverflow) {
        int temp;
        int index;
        final short[] lsf1_r = lsf1_r3;
        final short[] lsf1_q = lsf1_q3;

        if (bfi != 0) {
            /* if bad frame: use the past LSFs slightly shifted towards their mean */
            for (int i = 0; i < Cnst.M; i++) {
                temp = Basicop.mult(st.past_lsf_q[i], ALPHA, pOverflow);
                index = Basicop.mult(Tables.mean_lsf_3[i], ONE_ALPHA, pOverflow);
                lsf1_q[i] = (short) Basicop.add_16(index, temp, pOverflow);
            }

            /* estimate past quantized residual to be used in next frame */
            if (mode != Cnst.MRDTX) {
                for (int i = 0; i < Cnst.M; i++) {
                    temp = Basicop.mult(st.past_r_q[i], Tables.pred_fac_3[i], pOverflow);
                    temp = Basicop.add_16(Tables.mean_lsf_3[i], temp, pOverflow);
                    st.past_r_q[i] = (short) Basicop.sub(lsf1_q[i], temp, pOverflow);
                }
            } else {
                for (int i = 0; i < Cnst.M; i++) {
                    temp = Basicop.add_16(Tables.mean_lsf_3[i], st.past_r_q[i], pOverflow);
                    st.past_r_q[i] = (short) Basicop.sub(lsf1_q[i], temp, pOverflow);
                }
            }
        } else {
            /* if good LSFs received */
            int index_limit_1 = 0;
            final int index_limit_2 = (DICO2_SIZE - 1) * 3;
            int index_limit_3 = 0;
            short[] p_cb1;
            short[] p_cb3;
            final short[] p_cb2 = Tables.dico2_lsf_3;

            if (mode == Cnst.MR475 || mode == Cnst.MR515) {
                p_cb1 = Tables.dico1_lsf_3;
                p_cb3 = Tables.mr515_3_lsf;
                index_limit_1 = (DICO1_SIZE - 1) * 3;
                index_limit_3 = (MR515_3_SIZE - 1) * 4;
            } else if (mode == Cnst.MR795) {
                p_cb1 = Tables.mr795_1_lsf;
                p_cb3 = Tables.dico3_lsf_3;
                index_limit_1 = (MR795_1_SIZE - 1) * 3;
                index_limit_3 = (DICO3_SIZE - 1) * 4;
            } else {
                /* MR59, MR67, MR74, MR102, MRDTX */
                p_cb1 = Tables.dico1_lsf_3;
                p_cb3 = Tables.dico3_lsf_3;
                index_limit_1 = (DICO1_SIZE - 1) * 3;
                index_limit_3 = (DICO3_SIZE - 1) * 4;
            }

            /* decode prediction residuals from 3 received indices */
            int pInd = indiceOff;
            index = indice[pInd++];
            temp = index + (index << 1); /* 3*index */
            if (temp > index_limit_1) {
                temp = index_limit_1; /* avoid buffer overrun */
            }
            lsf1_r[0] = p_cb1[temp];
            lsf1_r[1] = p_cb1[temp + 1];
            lsf1_r[2] = p_cb1[temp + 2];

            index = indice[pInd++];
            if (mode == Cnst.MR475 || mode == Cnst.MR515) {
                /* MR475, MR515 only using every second entry */
                index <<= 1;
            }
            temp = index + (index << 1); /* 3*index */
            if (temp > index_limit_2) {
                temp = index_limit_2;
            }
            lsf1_r[3] = p_cb2[temp];
            lsf1_r[4] = p_cb2[temp + 1];
            lsf1_r[5] = p_cb2[temp + 2];

            index = indice[pInd++];
            temp = index << 2;
            if (temp > index_limit_3) {
                temp = index_limit_3;
            }
            lsf1_r[6] = p_cb3[temp];
            lsf1_r[7] = p_cb3[temp + 1];
            lsf1_r[8] = p_cb3[temp + 2];
            lsf1_r[9] = p_cb3[temp + 3];

            /* Compute quantized LSFs and update the past quantized residual */
            if (mode != Cnst.MRDTX) {
                for (int i = 0; i < Cnst.M; i++) {
                    temp = Basicop.mult(st.past_r_q[i], Tables.pred_fac_3[i], pOverflow);
                    temp = Basicop.add_16(Tables.mean_lsf_3[i], temp, pOverflow);
                    lsf1_q[i] = (short) Basicop.add_16(lsf1_r[i], temp, pOverflow);
                    st.past_r_q[i] = lsf1_r[i];
                }
            } else {
                for (int i = 0; i < Cnst.M; i++) {
                    temp = Basicop.add_16(Tables.mean_lsf_3[i], st.past_r_q[i], pOverflow);
                    lsf1_q[i] = (short) Basicop.add_16(lsf1_r[i], temp, pOverflow);
                    st.past_r_q[i] = lsf1_r[i];
                }
            }
        }

        /* verification that LSFs has minimum distance of LSF_GAP Hz */
        LspFns.Reorder_lsf(lsf1_q, 0, Cnst.LSF_GAP, Cnst.M, pOverflow);
        System.arraycopy(lsf1_q, 0, st.past_lsf_q, 0, Cnst.M);

        /* convert LSFs to the cosine domain */
        LspFns.Lsf_lsp(lsf1_q, 0, lsp1_q, lsp1_qOff, Cnst.M, pOverflow);
    }

    private static final short[] lsf1_r5 = new short[Cnst.M];
    private static final short[] lsf2_r5 = new short[Cnst.M];
    private static final short[] lsf1_q5 = new short[Cnst.M];
    private static final short[] lsf2_q5 = new short[Cnst.M];

    /** d_plsf_5.cpp D_plsf_5 (MR122) */
    public static void D_plsf_5(State st, int bfi, short[] indice, int indiceOff,
                                short[] lsp1_q, int lsp1_qOff, short[] lsp2_q, int lsp2_qOff,
                                int[] pOverflow) {
        int temp;
        int sign;
        int i;
        final short[] lsf1_r = lsf1_r5;
        final short[] lsf2_r = lsf2_r5;
        final short[] lsf1_q = lsf1_q5;
        final short[] lsf2_q = lsf2_q5;

        if (bfi != 0) {
            /* if bad frame: use the past LSFs slightly shifted towards their mean */
            for (i = 0; i < Cnst.M; i++) {
                temp = ((st.past_lsf_q[i] * ALPHA_5) >> 15) << 16 >> 16;
                sign = ((Tables.mean_lsf_5[i] * ONE_ALPHA_5) >> 15) << 16 >> 16;
                lsf1_q[i] = (short) Basicop.add_16(sign, temp, pOverflow);
                lsf2_q[i] = lsf1_q[i];

                /* estimate past quantized residual to be used in next frame */
                temp = ((st.past_r_q[i] * Cnst.LSP_PRED_FAC_MR122) >> 15) << 16 >> 16;
                temp = Basicop.add_16(Tables.mean_lsf_5[i], temp, pOverflow);
                st.past_r_q[i] = (short) Basicop.sub(lsf2_q[i], temp, pOverflow);
            }
        } else {
            /* if good LSFs received: decode prediction residuals from 5 indices */
            temp = Basicop.shl(indice[indiceOff], 2, pOverflow);
            lsf1_r[0] = Tables.dico1_lsf_5[temp];
            lsf1_r[1] = Tables.dico1_lsf_5[temp + 1];
            lsf2_r[0] = Tables.dico1_lsf_5[temp + 2];
            lsf2_r[1] = Tables.dico1_lsf_5[temp + 3];

            temp = Basicop.shl(indice[indiceOff + 1], 2, pOverflow);
            lsf1_r[2] = Tables.dico2_lsf_5[temp];
            lsf1_r[3] = Tables.dico2_lsf_5[temp + 1];
            lsf2_r[2] = Tables.dico2_lsf_5[temp + 2];
            lsf2_r[3] = Tables.dico2_lsf_5[temp + 3];

            sign = indice[indiceOff + 2] & 1;
            if (indice[indiceOff + 2] < 0) {
                i = ~(~indice[indiceOff + 2] >> 1);
            } else {
                i = indice[indiceOff + 2] >> 1;
            }
            temp = Basicop.shl(i, 2, pOverflow);
            if (sign == 0) {
                lsf1_r[4] = Tables.dico3_lsf_5[temp];
                lsf1_r[5] = Tables.dico3_lsf_5[temp + 1];
                lsf2_r[4] = Tables.dico3_lsf_5[temp + 2];
                lsf2_r[5] = Tables.dico3_lsf_5[temp + 3];
            } else {
                lsf1_r[4] = (short) Basicop.negate(Tables.dico3_lsf_5[temp]);
                lsf1_r[5] = (short) Basicop.negate(Tables.dico3_lsf_5[temp + 1]);
                lsf2_r[4] = (short) Basicop.negate(Tables.dico3_lsf_5[temp + 2]);
                lsf2_r[5] = (short) Basicop.negate(Tables.dico3_lsf_5[temp + 3]);
            }

            temp = Basicop.shl(indice[indiceOff + 3], 2, pOverflow);
            lsf1_r[6] = Tables.dico4_lsf_5[temp];
            lsf1_r[7] = Tables.dico4_lsf_5[temp + 1];
            lsf2_r[6] = Tables.dico4_lsf_5[temp + 2];
            lsf2_r[7] = Tables.dico4_lsf_5[temp + 3];

            temp = Basicop.shl(indice[indiceOff + 4], 2, pOverflow);
            lsf1_r[8] = Tables.dico5_lsf_5[temp];
            lsf1_r[9] = Tables.dico5_lsf_5[temp + 1];
            lsf2_r[8] = Tables.dico5_lsf_5[temp + 2];
            lsf2_r[9] = Tables.dico5_lsf_5[temp + 3];

            /* Compute quantized LSFs and update the past quantized residual */
            for (i = 0; i < Cnst.M; i++) {
                temp = Basicop.mult(st.past_r_q[i], Cnst.LSP_PRED_FAC_MR122, pOverflow);
                temp = Basicop.add_16(Tables.mean_lsf_5[i], temp, pOverflow);
                lsf1_q[i] = (short) Basicop.add_16(lsf1_r[i], temp, pOverflow);
                lsf2_q[i] = (short) Basicop.add_16(lsf2_r[i], temp, pOverflow);
                st.past_r_q[i] = lsf2_r[i];
            }
        }

        /* verification that LSFs have minimum distance of LSF_GAP Hz */
        LspFns.Reorder_lsf(lsf1_q, 0, Cnst.LSF_GAP, Cnst.M, pOverflow);
        LspFns.Reorder_lsf(lsf2_q, 0, Cnst.LSF_GAP, Cnst.M, pOverflow);
        System.arraycopy(lsf2_q, 0, st.past_lsf_q, 0, Cnst.M);

        /* convert LSFs to the cosine domain */
        LspFns.Lsf_lsp(lsf1_q, 0, lsp1_q, lsp1_qOff, Cnst.M, pOverflow);
        LspFns.Lsf_lsp(lsf2_q, 0, lsp2_q, lsp2_qOff, Cnst.M, pOverflow);
    }

    /** int_lsf.cpp Int_lsf: interpolate LSF for subframe i_subfr (0,40,80,120) */
    public static void Int_lsf(short[] lsf_old, int lsf_oldOff, short[] lsf_new, int lsf_newOff,
                               int i_subfr, short[] lsf_out, int lsf_outOff, int[] pOverflow) {
        int temp1;
        int temp2;

        if (i_subfr == 0) {
            for (int i = Cnst.M - 1; i >= 0; i--) {
                if (lsf_old[lsf_oldOff + i] < 0) {
                    temp1 = ~(~lsf_old[lsf_oldOff + i] >> 2);
                } else {
                    temp1 = lsf_old[lsf_oldOff + i] >> 2;
                }
                if (lsf_new[lsf_newOff + i] < 0) {
                    temp2 = ~(~lsf_new[lsf_newOff + i] >> 2);
                } else {
                    temp2 = lsf_new[lsf_newOff + i] >> 2;
                }
                lsf_out[lsf_outOff + i] = (short) Basicop.add_16(
                    (lsf_old[lsf_oldOff + i] - temp1) << 16 >> 16,
                    temp2 << 16 >> 16, pOverflow);
            }
        } else if (i_subfr == 40) {
            for (int i = Cnst.M - 1; i >= 0; i--) {
                if (lsf_old[lsf_oldOff + i] < 0) {
                    temp1 = ~(~lsf_old[lsf_oldOff + i] >> 1);
                } else {
                    temp1 = lsf_old[lsf_oldOff + i] >> 1;
                }
                if (lsf_new[lsf_newOff + i] < 0) {
                    temp2 = ~(~lsf_new[lsf_newOff + i] >> 1);
                } else {
                    temp2 = lsf_new[lsf_newOff + i] >> 1;
                }
                lsf_out[lsf_outOff + i] = (short) (temp1 + temp2);
            }
        } else if (i_subfr == 80) {
            for (int i = Cnst.M - 1; i >= 0; i--) {
                if (lsf_old[lsf_oldOff + i] < 0) {
                    temp1 = ~(~lsf_old[lsf_oldOff + i] >> 2);
                } else {
                    temp1 = lsf_old[lsf_oldOff + i] >> 2;
                }
                if (lsf_new[lsf_newOff + i] < 0) {
                    temp2 = ~(~lsf_new[lsf_newOff + i] >> 2);
                } else {
                    temp2 = lsf_new[lsf_newOff + i] >> 2;
                }
                lsf_out[lsf_outOff + i] = (short) Basicop.add_16(
                    temp1 << 16 >> 16,
                    (lsf_new[lsf_newOff + i] - temp2) << 16 >> 16, pOverflow);
            }
        } else if (i_subfr == 120) {
            for (int i = Cnst.M - 1; i >= 0; i--) {
                lsf_out[lsf_outOff + i] = lsf_new[lsf_newOff + i];
            }
        }
    }
}

/**
 * Algebraic codebook pulse decoders, ported from opencore-amr 0.1.6 dec/src:
 *   d2_9pf.cpp (decode_2i40_9bits), d2_11pf.cpp (decode_2i40_11bits),
 *   d3_14pf.cpp (decode_3i40_14bits), d4_17pf.cpp (decode_4i40_17bits),
 *   d8_31pf.cpp (decompress10, decompress_code, dec_8i40_31bits),
 *   d1035pf.cpp (dec_10i40_35bits)
 * (via src/dec/d_pulse.js of the JS reference port).
 */
final class DPulse {
    private DPulse() {}

    public static final int POS_CODE = 8191;
    public static final int NEG_CODE = 8191;

    /** d2_9pf.cpp decode_2i40_9bits */
    public static void decode_2i40_9bits(int subNr, int sign, int index, short[] cod, int codOff, int[] pOverflow) {
        final short[] pos = new short[2];
        int i, j, k;

        /* Decode the positions; table bit is the MSB */
        j = (index & 64) << 16 >> 16;
        j >>= 3;
        i = index & 7;

        k = Basicop.shl(subNr, 1, pOverflow);
        k = (k + j) << 16 >> 16;
        /* pos0 = i*5 + startPos[j*8 + subNr*2] */
        pos[0] = (short) (i * 5 + Tables.startPos[k++]);

        index >>= 3;
        i = index & 7;
        /* pos1 = i*5 + startPos[j*8 + subNr*2 + 1] */
        pos[1] = (short) (i * 5 + Tables.startPos[k]);

        /* decode the signs and build the codeword */
        for (i = Cnst.L_SUBFR - 1; i >= 0; i--) {
            cod[codOff + i] = 0;
        }
        for (j = 0; j < 2; j++) {
            i = sign & 0x1;
            cod[codOff + pos[j]] = (short) (i * 16383 - 8192);
            sign >>= 1;
        }
    }

    /** d2_11pf.cpp decode_2i40_11bits */
    public static void decode_2i40_11bits(int sign, int index, short[] cod, int codOff) {
        final short[] pos = new short[2];
        int i, j;

        /* Decode the positions */
        j = index & 0x1;
        index >>= 1;
        i = index & 0x7;
        pos[0] = (short) (i * 5 + j * 2 + 1);

        index >>= 3;
        j = index & 0x3;
        index >>= 2;
        i = index & 0x7;
        if (j == 3) {
            pos[1] = (short) (i * 5 + 4);
        } else {
            pos[1] = (short) (i * 5 + j);
        }

        /* decode the signs and build the codeword */
        for (i = 0; i < Cnst.L_SUBFR; i++) {
            cod[codOff + i] = 0;
        }
        for (j = 0; j < 2; j++) {
            i = sign & 1;
            cod[codOff + pos[j]] = (short) (i * 16383 - 8192);
            sign >>= 1;
        }
    }

    /** d3_14pf.cpp decode_3i40_14bits */
    public static void decode_3i40_14bits(int sign, int index, short[] cod, int codOff) {
        final short[] pos = new short[3];
        int i, j;

        /* Decode the positions */
        i = index & 0x7;
        pos[0] = (short) (i * 5);

        index >>= 3;
        j = index & 0x1;
        index >>= 1;
        i = index & 0x7;
        pos[1] = (short) (i * 5 + j * 2 + 1);

        index >>= 3;
        j = index & 0x1;
        index >>= 1;
        i = index & 0x7;
        pos[2] = (short) (i * 5 + j * 2 + 2);

        /* decode the signs and build the codeword */
        for (i = 0; i < Cnst.L_SUBFR; i++) {
            cod[codOff + i] = 0;
        }
        for (j = 0; j < 3; j++) {
            i = sign & 1;
            cod[codOff + pos[j]] = (short) (i * 16383 - 8192);
            sign >>= 1;
        }
    }

    /** d4_17pf.cpp decode_4i40_17bits */
    public static void decode_4i40_17bits(int sign, int index, short[] cod, int codOff) {
        final short[] pos = new short[4];
        int i, j;

        /* Decode the positions */
        i = index & 0x7;
        i = Tables.dgray[i];
        pos[0] = (short) (i * 5); /* pos0 = i*5 */

        index >>= 3;
        i = index & 0x7;
        i = Tables.dgray[i];
        pos[1] = (short) (i * 5 + 1); /* pos1 = i*5+1 */

        index >>= 3;
        i = index & 0x7;
        i = Tables.dgray[i];
        pos[2] = (short) (i * 5 + 2); /* pos2 = i*5+2 */

        index >>= 3;
        j = index & 0x1;
        index >>= 1;
        i = index & 0x7;
        i = Tables.dgray[i];
        pos[3] = (short) (i * 5 + 3 + j); /* pos3 = i*5+3+j */

        /* decode the signs and build the codeword */
        for (i = 0; i < Cnst.L_SUBFR; i++) {
            cod[codOff + i] = 0;
        }
        for (j = 0; j < 4; j++) {
            i = sign & 0x1;
            cod[codOff + pos[j]] = (short) (i * 16383 - 8192);
            sign >>= 1;
        }
    }

    /** d8_31pf.cpp decompress10 (static) */
    private static void decompress10(int MSBs, int LSBs, int index1, int index2, int index3,
                                     short[] pos_indx, int[] pOverflow) {
        int ia, ib, ic;
        int tempWord32;

        if (MSBs > 124) {
            MSBs = 124;
        }
        ia = Basicop.mult(MSBs, 1311, pOverflow);
        tempWord32 = Basicop.L_mult(ia, 25, pOverflow);
        ia = ((MSBs - (tempWord32 >> 1)) << 16) >> 16;
        ib = Basicop.mult(ia, 6554, pOverflow);
        tempWord32 = Basicop.L_mult(ib, 5, pOverflow);
        ib = (ia - (((tempWord32 >> 1) << 16) >> 16)) << 16 >> 16;
        ib = Basicop.shl(ib, 1, pOverflow);

        ic = (LSBs - ((LSBs >> 2) << 2)) << 16 >> 16;
        pos_indx[index1] = (short) ((ib + (ic & 1)) << 16 >> 16);

        ib = Basicop.mult(ia, 6554, pOverflow);
        ib = Basicop.shl(ib, 1, pOverflow);

        pos_indx[index2] = (short) ((ib + (ic >> 1)) << 16 >> 16);

        ib = LSBs >> 2;
        ic = Basicop.mult(MSBs, 1311, pOverflow);
        ic = Basicop.shl(ic, 1, pOverflow);
        pos_indx[index3] = (short) Basicop.add_16(ib, ic, pOverflow);
    }

    private static final short[] dcSignIndx = new short[Cnst.NB_TRACK_MR102];
    private static final short[] dcPosIndx = new short[8];

    /** d8_31pf.cpp decompress_code (static) */
    private static void decompress_code(short[] indx, int indxOff, short[] sign_indx,
                                        short[] pos_indx, int[] pOverflow) {
        int ia, ib;
        int MSBs, LSBs, MSBs0_24;
        int tempWord32;

        for (int i = 0; i < Cnst.NB_TRACK_MR102; i++) {
            sign_indx[i] = indx[indxOff + i];
        }

        /* First index: 7+1x3 bits */
        MSBs = indx[indxOff + Cnst.NB_TRACK_MR102] >> 3;
        LSBs = indx[indxOff + Cnst.NB_TRACK_MR102] & 0x7;
        decompress10(MSBs, LSBs, 0, 4, 1, pos_indx, pOverflow);

        /* Second index: 7+1x3 bits */
        MSBs = indx[indxOff + Cnst.NB_TRACK_MR102 + 1] >> 3;
        LSBs = indx[indxOff + Cnst.NB_TRACK_MR102 + 1] & 0x7;
        decompress10(MSBs, LSBs, 2, 6, 5, pos_indx, pOverflow);

        /* Third index: 5+1x2 bits */
        MSBs = indx[indxOff + Cnst.NB_TRACK_MR102 + 2] >> 2;
        LSBs = indx[indxOff + Cnst.NB_TRACK_MR102 + 2] & 0x3;
        tempWord32 = Basicop.L_mult(MSBs, 25, pOverflow);
        ia = (Basicop.L_shr(tempWord32, 1, pOverflow) << 16) >> 16;
        ia = (ia + 12) << 16 >> 16;
        MSBs0_24 = ia >> 5;

        ia = Basicop.mult(MSBs0_24, 6554, pOverflow);
        ia &= 1;

        ib = Basicop.mult(MSBs0_24, 6554, pOverflow);
        tempWord32 = Basicop.L_mult(ib, 5, pOverflow);
        ib = (MSBs0_24 - (((tempWord32 >> 1) << 16) >> 16)) << 16 >> 16;

        if (ia == 1) {
            ib = (4 - ib) << 16 >> 16;
        }
        ib = Basicop.shl(ib, 1, pOverflow);

        ia = LSBs & 0x1;
        pos_indx[3] = (short) Basicop.add_16(ib, ia, pOverflow);

        ia = Basicop.mult(MSBs0_24, 6554, pOverflow);
        ia = Basicop.shl(ia, 1, pOverflow);
        pos_indx[7] = (short) ((ia + (LSBs >> 1)) << 16 >> 16);
    }

    /** d8_31pf.cpp dec_8i40_31bits (MR102) */
    public static void dec_8i40_31bits(short[] index, int indexOff, short[] cod, int codOff, int[] pOverflow) {
        int pos1, pos2, sign;
        final short[] linear_signs = dcSignIndx;
        final short[] linear_codewords = dcPosIndx;

        for (int i = 0; i < Cnst.L_CODE; i++) {
            cod[codOff + i] = 0;
        }

        decompress_code(index, indexOff, linear_signs, linear_codewords, pOverflow);

        /* decode the positions and signs of pulses and build the codeword */
        for (int j = 0; j < Cnst.NB_TRACK_MR102; j++) {
            /* position of pulse "j" */
            pos1 = ((linear_codewords[j] << 2) + j) << 16 >> 16;
            if (linear_signs[j] == 0) {
                sign = POS_CODE; /* +1.0 */
            } else {
                sign = -NEG_CODE; /* -1.0 */
            }

            if (pos1 < Cnst.L_SUBFR) {
                cod[codOff + pos1] = (short) sign; /* avoid buffer overflow */
            }

            /* position of pulse "j+4" */
            pos2 = ((linear_codewords[j + 4] << 2) + j) << 16 >> 16;
            if (pos2 < pos1) {
                sign = Basicop.negate(sign);
            }
            if (pos2 < Cnst.L_SUBFR) {
                cod[codOff + pos2] = (short) ((cod[codOff + pos2] + sign) << 16 >> 16); /* += */
            }
        }
    }

    /** d1035pf.cpp dec_10i40_35bits (MR122) */
    public static void dec_10i40_35bits(short[] index, int indexOff, short[] cod, int codOff) {
        int pos1, pos2, sign, tmp, i;

        for (i = 0; i < Cnst.L_CODE; i++) {
            cod[codOff + i] = 0;
        }

        /* decode the positions and signs of pulses and build the codeword */
        for (int j = 0; j < Cnst.NB_TRACK; j++) {
            /* compute index i */
            tmp = index[indexOff + j];
            i = tmp & 7;
            i = Tables.dgray[i];
            i = (i * 5) << 16 >> 16;
            pos1 = (i + j) << 16 >> 16; /* position of pulse "j" */

            i = (tmp >> 3) & 1;
            sign = i == 0 ? 4096 : -4096;

            cod[codOff + pos1] = (short) sign;

            /* compute index i for pulse "j+5" */
            i = index[indexOff + j + 5] & 7;
            i = Tables.dgray[i];
            i = (i * 5) << 16 >> 16;
            pos2 = (i + j) << 16 >> 16;

            if (pos2 < pos1) {
                sign = Basicop.negate(sign);
            }
            cod[codOff + pos2] = (short) ((cod[codOff + pos2] + sign) << 16 >> 16); /* += */
        }
    }
}

/**
 * Gain and lag decoding, ported from opencore-amr 0.1.6 dec/src:
 *   d_gain_p.cpp (d_gain_pitch), d_gain_c.cpp (d_gain_code),
 *   dec_gain.cpp (Dec_gain), dec_lag3.cpp (Dec_lag3), dec_lag6.cpp (Dec_lag6)
 * (via src/dec/dec_gain.js of the JS reference port).
 */
final class DecGain {
    private DecGain() {}

    public static final int MR475_VQ_SIZE = 256;

    /** d_gain_p.cpp d_gain_pitch: returns gain (Q14) */
    public static int d_gain_pitch(int mode, int index) {
        int gain = Tables.qua_gain_pitch[index];
        if (mode == Cnst.MR122) {
            /* clear 2 LSBits */
            gain = (gain & 0xfffc) << 16 >> 16;
        }
        return gain;
    }

    private static final short[] dgExp = new short[1];
    private static final short[] dgFrac = new short[1];
    private static final short[] dgExpEn = new short[1];
    private static final short[] dgFracEn = new short[1];

    /**
     * d_gain_c.cpp d_gain_code (MR795/MR122).
     * @param gain_code short[1] out
     */
    public static void d_gain_code(GcPred.State pred_state, int mode, int index,
                                   short[] code, int codeOff, short[] gain_code, int[] pOverflow) {
        int gcode0;
        int L_tmp;

        /* predict codebook gain */
        GcPred.gc_pred(pred_state, mode, code, codeOff, dgExp, dgFrac, dgExpEn, dgFracEn, pOverflow);
        final int exp = dgExp[0];
        final int frac = dgFrac[0];

        index &= 31; /* index < 32, to avoid buffer overflow */
        final int tbl_tmp = index + (index << 1);
        int p = tbl_tmp; /* into qua_gain_code */

        /* Different scalings between MR122 and the other modes */
        final int temp = Basicop.sub(mode, Cnst.MR122, pOverflow);
        if (temp == 0) {
            gcode0 = (Mathops.Pow2(exp, frac, pOverflow) << 16) >> 16; /* predicted gain */
            gcode0 = Basicop.shl(gcode0, 4, pOverflow);
            gain_code[0] = (short) Basicop.shl(Basicop.mult(gcode0, Tables.qua_gain_code[p++], pOverflow), 1, pOverflow);
        } else {
            gcode0 = (Mathops.Pow2(14, frac, pOverflow) << 16) >> 16;
            L_tmp = Basicop.L_mult(Tables.qua_gain_code[p++], gcode0, pOverflow);
            L_tmp = Basicop.L_shr(L_tmp, Basicop.sub(9, exp, pOverflow), pOverflow);
            gain_code[0] = (short) ((L_tmp >> 16) << 16 >> 16); /* Q1 */
        }

        /* update table of past quantized energies */
        final int qua_ener_MR122 = Tables.qua_gain_code[p++];
        final int qua_ener = Tables.qua_gain_code[p++];
        GcPred.gc_pred_update(pred_state, qua_ener_MR122, qua_ener);
    }

    /**
     * dec_gain.cpp Dec_gain: decode pitch and codebook gains.
     * @param gain_pit short[1] out
     * @param gain_cod short[1] out
     */
    public static void Dec_gain(GcPred.State pred_state, int mode, int index, short[] code, int codeOff,
                                int evenSubfr, short[] gain_pit, short[] gain_cod, int[] pOverflow) {
        int p;
        short[] tbl;
        int g_code;
        int qua_ener;
        int qua_ener_MR122;
        int L_tmp;
        int temp1;
        int temp2;

        /* Read the quantized gains (table depends on mode) */
        index = Basicop.shl(index, 2, pOverflow);

        if (mode == Cnst.MR102 || mode == Cnst.MR74 || mode == Cnst.MR67) {
            tbl = Tables.table_gain_highrates;
            p = index;
            gain_pit[0] = tbl[p++];
            g_code = tbl[p++];
            qua_ener_MR122 = tbl[p++];
            qua_ener = tbl[p];
        } else if (mode == Cnst.MR475) {
            index += (1 ^ evenSubfr) << 1; /* evenSubfr is 0 or 1 */
            if (index > MR475_VQ_SIZE * 4 - 2) {
                index = MR475_VQ_SIZE * 4 - 2; /* avoid possible buffer overflow */
            }
            tbl = Tables.table_gain_MR475;
            p = index;
            gain_pit[0] = tbl[p++];
            g_code = tbl[p++];

            /* calculate predictor update values:
               qua_ener = log2(g), qua_ener_MR122 = 20*log10(g) */
            /* Log2(x Q12) = log2(x) + 12 */
            temp1 = g_code;
            Mathops.Log2(temp1, dgExp, dgFrac, pOverflow);
            final int exp475 = (dgExp[0] - 12) << 16 >> 16;
            temp1 = Basicop.shr_r(dgFrac[0], 5, pOverflow);
            temp2 = Basicop.shl(exp475, 10, pOverflow);
            qua_ener_MR122 = Basicop.add_16(temp1, temp2, pOverflow);

            /* 24660 Q12 ~= 6.0206 = 20*log10(2) */
            L_tmp = Basicop.Mpy_32_16(exp475, dgFrac[0], 24660, pOverflow);
            L_tmp = Basicop.L_shl(L_tmp, 13, pOverflow);
            qua_ener = Basicop.pv_round(L_tmp, pOverflow); /* Q12 * Q0 = Q13 -> Q10 */
        } else {
            tbl = Tables.table_gain_lowrates;
            p = index;
            gain_pit[0] = tbl[p++];
            g_code = tbl[p++];
            qua_ener_MR122 = tbl[p++];
            qua_ener = tbl[p];
        }

        /* predict codebook gain: gcode0 (Q14) = 2^14*2^frac = gc0 * 2^(14-exp) */
        GcPred.gc_pred(pred_state, mode, code, codeOff, dgExp, dgFrac, null, null, pOverflow);
        final int gcode0 = (Mathops.Pow2(14, dgFrac[0], pOverflow) << 16) >> 16;

        L_tmp = Basicop.L_mult(g_code, gcode0, pOverflow);
        temp1 = (10 - dgExp[0]) << 16 >> 16;
        L_tmp = Basicop.L_shr(L_tmp, temp1, pOverflow);
        gain_cod[0] = (short) ((L_tmp >> 16) << 16 >> 16);

        /* update table of past quantized energies */
        GcPred.gc_pred_update(pred_state, qua_ener_MR122, qua_ener);
    }

    /**
     * dec_lag3.cpp Dec_lag3.
     * @param T0 short[1] in/out
     * @param T0_frac short[1] out
     */
    public static void Dec_lag3(int index, int t0_min, int t0_max, int i_subfr, int T0_prev,
                                short[] T0, short[] T0_frac, int flag4, int[] pOverflow) {
        int i;
        int tmp_lag;

        if (i_subfr == 0) {
            /* if 1st or 3rd subframe */
            if (index < 197) {
                tmp_lag = (index + 2) << 16 >> 16;
                tmp_lag = Basicop.mult(tmp_lag, 10923, pOverflow);
                i = (tmp_lag + 19) << 16 >> 16;
                T0[0] = (short) i;

                /* i = 3 * (*T0) */
                i = (i << 1) << 16 >> 16;
                i = (i + T0[0]) << 16 >> 16;

                tmp_lag = (index - i) << 16 >> 16;
                T0_frac[0] = (short) ((tmp_lag + 58) << 16 >> 16);
            } else {
                T0[0] = (short) ((index - 112) << 16 >> 16);
                T0_frac[0] = 0;
            }
        } else {
            /* 2nd or 4th subframe */
            if (flag4 == 0) {
                /* 'normal' decoding: either with 5 or 6 bit resolution */
                i = (index + 2) << 16 >> 16;
                i = (i * 10923 >> 15) << 16 >> 16;
                i = (i - 1) << 16 >> 16;
                T0[0] = (short) ((i + t0_min) << 16 >> 16);

                /* i = 3* (*T0) */
                i = (i + ((i << 1) << 16 >> 16)) << 16 >> 16;

                tmp_lag = (index - 2) << 16 >> 16;
                T0_frac[0] = (short) ((tmp_lag - i) << 16 >> 16);
            } else {
                /* decoding with 4 bit resolution */
                tmp_lag = T0_prev;
                i = Basicop.sub(tmp_lag, t0_min, pOverflow);
                if (i > 5) {
                    tmp_lag = (t0_min + 5) << 16 >> 16;
                }
                i = (t0_max - tmp_lag) << 16 >> 16;
                if (i > 4) {
                    tmp_lag = (t0_max - 4) << 16 >> 16;
                }

                if (index < 4) {
                    i = (tmp_lag - 5) << 16 >> 16;
                    T0[0] = (short) ((i + index) << 16 >> 16);
                    T0_frac[0] = 0;
                } else if (index < 12) {
                    /* 4 <= index < 12 */
                    i = (index - 5) << 16 >> 16;
                    i = (i * 10923 >> 15) << 16 >> 16;
                    i = (i - 1) << 16 >> 16;
                    T0[0] = (short) ((i + tmp_lag) << 16 >> 16);

                    i = (i + ((i << 1) << 16 >> 16)) << 16 >> 16;

                    tmp_lag = (index - 9) << 16 >> 16;
                    T0_frac[0] = (short) ((tmp_lag - i) << 16 >> 16);
                } else {
                    i = (index - 12) << 16 >> 16;
                    i = (i + tmp_lag) << 16 >> 16;
                    T0[0] = (short) ((i + 1) << 16 >> 16);
                    T0_frac[0] = 0;
                }
            }
        }
    }

    /**
     * dec_lag6.cpp Dec_lag6.
     * @param T0 short[1] in/out
     * @param T0_frac short[1] out
     */
    public static void Dec_lag6(int index, int pit_min, int pit_max, int i_subfr,
                                short[] T0, short[] T0_frac, int[] pOverflow) {
        int i;
        int T0_min;
        int T0_max;
        int k;

        if (i_subfr == 0) {
            /* if 1st or 3rd subframe */
            if (index < 463) {
                /* T0 = (index+5)/6 + 17 */
                i = (index + 5) << 16 >> 16;
                i = (i * 5462 >> 15) << 16 >> 16;
                i = (i + 17) << 16 >> 16;
                T0[0] = (short) i;

                /* i = 3* (*T0) */
                i = (i << 1) << 16 >> 16;
                i = (i + T0[0]) << 16 >> 16;

                /* *T0_frac = index - T0*6 + 105 */
                i = (i << 1) << 16 >> 16;
                i = (index - i) << 16 >> 16;
                T0_frac[0] = (short) ((i + 105) << 16 >> 16);
            } else {
                T0[0] = (short) ((index - 368) << 16 >> 16);
                T0_frac[0] = 0;
            }
        } else {
            /* second or fourth subframe */
            /* find T0_min and T0_max for 2nd (or 4th) subframe */
            T0_min = (T0[0] - 5) << 16 >> 16;
            if (T0_min < pit_min) {
                T0_min = pit_min;
            }
            T0_max = (T0_min + 9) << 16 >> 16;
            if (T0_max > pit_max) {
                T0_max = pit_max;
                T0_min = (T0_max - 9) << 16 >> 16;
            }

            /* i = (index+5)/6 - 1 */
            i = (index + 5) << 16 >> 16;
            i = (i * 5462 >> 15) << 16 >> 16;
            i = (i - 1) << 16 >> 16;
            T0[0] = (short) ((i + T0_min) << 16 >> 16);

            /* i = 3* (*T0) */
            i = (i + ((i << 1) << 16 >> 16)) << 16 >> 16;
            i = (i << 1) << 16 >> 16;

            k = (index - 3) << 16 >> 16;
            T0_frac[0] = (short) ((k - i) << 16 >> 16);
        }
    }
}

/**
 * Phase dispersion, ported from opencore-amr 0.1.6 dec/src/ph_disp.cpp
 * (via src/dec/ph_disp.js of the JS reference port).
 */
final class PhDisp {
    private PhDisp() {}

    public static final int PHDGAINMEMSIZE = 5;
    public static final int PHDTHR1LTP = 9830;  /* 0.6 in Q14 */
    public static final int PHDTHR2LTP = 14746; /* 0.9 in Q14 */
    public static final int ONFACTPLUS1 = 16384; /* 2.0 in Q13 */
    public static final int ONLENGTH = 2;

    /** ph_disp.h ph_dispState */
    public static final class State {
        public short[] gainMem;
        public int prevState;
        public int prevCbGain;
        public int lockFull;
        public int onset;

        public State() {
            this.gainMem = new short[PHDGAINMEMSIZE];
            reset();
        }

        /** ph_disp.cpp ph_disp_reset */
        public int reset() {
            for (int i = 0; i < PHDGAINMEMSIZE; i++) {
                gainMem[i] = 0;
            }
            this.prevState = 0;
            this.prevCbGain = 0;
            this.lockFull = 0;
            this.onset = 0; /* assume no onset in start */
            return 0;
        }
    }

    /** ph_disp.cpp ph_disp_lock */
    public static void ph_disp_lock(State state) {
        state.lockFull = 1;
    }

    /** ph_disp.cpp ph_disp_release */
    public static void ph_disp_release(State state) {
        state.lockFull = 0;
    }

    private static final short[] innoSav = new short[Cnst.L_SUBFR];
    private static final short[] psPoss = new short[Cnst.L_SUBFR];

    /** ph_disp.cpp ph_disp */
    public static void ph_disp(State state, int mode, short[] x, int xOff, int cbGain, int ltpGain,
                               short[] inno, int innoOff, int pitch_fac, int tmp_shift, int[] pOverflow) {
        int i, i1;
        int tmp1;
        int L_temp;
        int L_temp2;
        int impNr; /* indicator for amount of dispersion/filter used */
        final short[] inno_sav = innoSav;
        final short[] ps_poss = psPoss;
        int nze, nPulse;
        int ppos;
        short[] ph_imp; /* phase dispersion filter table */
        int c_inno_sav;

        /* Update LTP gain memory */
        state.gainMem[4] = state.gainMem[3];
        state.gainMem[3] = state.gainMem[2];
        state.gainMem[2] = state.gainMem[1];
        state.gainMem[1] = state.gainMem[0];
        state.gainMem[0] = (short) ltpGain;

        /* basic adaption of phase dispersion */
        if (ltpGain < PHDTHR2LTP) {
            /* if (ltpGain < 0.9) */
            if (ltpGain > PHDTHR1LTP) {
                /* if (ltpGain > 0.6) */
                impNr = 1; /* medium dispersion */
            } else {
                impNr = 0; /* maximum dispersion */
            }
        } else {
            impNr = 2; /* no dispersion */
        }

        /* onset indicator: onset = (cbGain > onFact * cbGainMem[0]) */
        L_temp = (state.prevCbGain * ONFACTPLUS1) << 1;
        /* (L_temp << 2) calculation with saturation check */
        if (L_temp > 0x1fffffff) {
            pOverflow[0] = 1;
            L_temp = Basicop.MAX_32;
        } else if (L_temp < -536870912) {
            pOverflow[0] = 1;
            L_temp = Basicop.MIN_32;
        } else {
            L_temp <<= 2;
        }
        tmp1 = Basicop.pv_round(L_temp, pOverflow);
        if (cbGain > tmp1) {
            state.onset = ONLENGTH;
        } else if (state.onset > 0) {
            state.onset -= 1;
        }

        /* if not onset, check ltpGain buffer and use max phase dispersion if
           half or more of the ltpGain-parameters say so */
        if (state.onset == 0) {
            i1 = 0;
            for (i = 0; i < PHDGAINMEMSIZE; i++) {
                if (state.gainMem[i] < PHDTHR1LTP) {
                    i1 += 1;
                }
            }
            if (i1 > 2) {
                impNr = 0;
            }
        }

        /* Restrict decrease in phase dispersion to one step if not onset */
        if (impNr > state.prevState + 1 && state.onset == 0) {
            impNr -= 1;
        }
        /* if onset, use one step less phase dispersion */
        if (impNr < 2 && state.onset > 0) {
            impNr += 1;
        }
        /* disable for very low levels */
        if (cbGain < 10) {
            impNr = 2;
        }
        if (state.lockFull == 1) {
            impNr = 0;
        }

        /* update static memory */
        state.prevState = impNr;
        state.prevCbGain = cbGain;

        /* do phase dispersion for all modes but 12.2, 10.2 and 7.4;
           don't modify the innovation if impNr >= 2 (= no phase disp) */
        if (mode != Cnst.MR122 && mode != Cnst.MR102 && mode != Cnst.MR74 && impNr < 2) {
            /* track pulse positions, save innovation, initialize new innovation */
            nze = 0;
            for (i = 0; i < Cnst.L_SUBFR; i++) {
                if (inno[innoOff + i] != 0) {
                    ps_poss[nze] = (short) i;
                    nze += 1;
                }
                inno_sav[i] = inno[innoOff + i];
                inno[innoOff + i] = 0;
            }

            /* Choose filter corresponding to codec mode and dispersion criterium */
            if (mode == Cnst.MR795) {
                ph_imp = impNr == 0 ? Tables.ph_imp_low_MR795 : Tables.ph_imp_mid_MR795;
            } else {
                ph_imp = impNr == 0 ? Tables.ph_imp_low : Tables.ph_imp_mid;
            }

            /* Do phase dispersion of innovation */
            for (nPulse = 0; nPulse < nze; nPulse++) {
                ppos = ps_poss[nPulse];

                /* circular convolution with impulse response */
                c_inno_sav = inno_sav[ppos];
                int pImp = 0;
                for (i = ppos; i < Cnst.L_SUBFR; i++) {
                    /* inno[i] += inno_sav[ppos] * ph_imp[i-ppos] */
                    L_temp = (c_inno_sav * ph_imp[pImp++]) >> 15;
                    tmp1 = (L_temp << 16) >> 16;
                    inno[innoOff + i] = (short) Basicop.add_16(inno[innoOff + i], tmp1, pOverflow);
                }
                for (i = 0; i < ppos; i++) {
                    /* inno[i] += inno_sav[ppos] * ph_imp[L_SUBFR-ppos+i] */
                    L_temp = (c_inno_sav * ph_imp[pImp++]) >> 15;
                    tmp1 = (L_temp << 16) >> 16;
                    inno[innoOff + i] = (short) Basicop.add_16(inno[innoOff + i], tmp1, pOverflow);
                }
            }
        }

        /* compute total excitation for synthesis part of decoder
           (using modified innovation if phase dispersion is active) */
        for (i = 0; i < Cnst.L_SUBFR; i++) {
            /* x[i] = gain_pit*x[i] + cbGain*code[i]; */
            L_temp = Basicop.L_mult(x[xOff + i], pitch_fac, pOverflow);
            L_temp2 = (inno[innoOff + i] * cbGain) << 1;
            L_temp = Basicop.L_add(L_temp, L_temp2, pOverflow);
            L_temp = Basicop.L_shl(L_temp, tmp_shift, pOverflow); /* Q16 */
            x[xOff + i] = (short) Basicop.pv_round(L_temp, pOverflow);
        }
    }
}

/**
 * Pre/post processing helpers, ported from opencore-amr 0.1.6 dec/src:
 *   preemph.cpp (preemphasisState, preemphasis),
 *   post_pro.cpp (Post_ProcessState, Post_Process),
 *   a_refl.cpp (A_Refl),
 *   b_cn_cod.cpp (pseudonoise, build_CN_code, build_CN_param)
 * (via src/dec/post_pre.js of the JS reference port).
 */
final class PostPre {
    private PostPre() {}

    /** preemph.h preemphasisState */
    public static final class PreemphasisState {
        public int mem_pre;

        public PreemphasisState() {
            reset();
        }

        public int reset() {
            this.mem_pre = 0; /* preemphasis filter state */
            return 0;
        }
    }

    /** preemph.cpp preemphasis */
    public static void preemphasis(PreemphasisState st, short[] signal, int signalOff,
                                   int g, int L, int[] pOverflow) {
        int temp2;

        int p1 = signalOff + L - 1;
        int p2 = p1 - 1;
        final int temp = signal[p1];

        for (int i = 0; i <= L - 2; i++) {
            temp2 = Basicop.mult(g, signal[p2--], pOverflow);
            signal[p1] = (short) Basicop.sub(signal[p1], temp2, pOverflow);
            p1--;
        }

        temp2 = Basicop.mult(g, st.mem_pre, pOverflow);
        signal[p1] = (short) Basicop.sub(signal[p1], temp2, pOverflow);

        st.mem_pre = temp;
    }

    /* post_pro.cpp HP filter coefficients */
    public static final short[] pp_b = { 7699, -15398, 7699 };
    public static final short[] pp_a = { 8192, 15836, -7667 };

    /** post_pro.h Post_ProcessState */
    public static final class PostProcessState {
        public int y2_hi;
        public int y2_lo;
        public int y1_hi;
        public int y1_lo;
        public int x0;
        public int x1;

        public PostProcessState() {
            reset();
        }

        /** post_pro.cpp Post_Process_reset */
        public int reset() {
            this.y2_hi = 0;
            this.y2_lo = 0;
            this.y1_hi = 0;
            this.y1_lo = 0;
            this.x0 = 0;
            this.x1 = 0;
            return 0;
        }
    }

    /** post_pro.cpp Post_Process: HP filter + upscaling of output speech */
    public static void Post_Process(PostProcessState st, short[] signal, int signalOff, int lg, int[] pOverflow) {
        int x2;
        int L_tmp;
        final int c_a1 = pp_a[1];
        final int c_a2 = pp_a[2];
        final int c_b0 = pp_b[0];
        final int c_b1 = pp_b[1];
        final int c_b2 = pp_b[2];

        int p = signalOff;
        for (int i = 0; i < lg; i++) {
            x2 = st.x1;
            st.x1 = st.x0;
            st.x0 = signal[p];

            /* y[i] = b[0]*x[i]*2 + b[1]*x[i-1]*2 + b[2]*x[i-2]/2
                      + a[1]*y[i-1] + a[2]*y[i-2]; */
            L_tmp = st.y1_hi * c_a1;
            L_tmp += (st.y1_lo * c_a1) >> 15;
            L_tmp += st.y2_hi * c_a2;
            L_tmp += (st.y2_lo * c_a2) >> 15;
            L_tmp += st.x0 * c_b0;
            L_tmp += st.x1 * c_b1;
            L_tmp += x2 * c_b2;
            /* int accumulation wraps mod 2^32 == the JS `| 0` before L_shl */
            L_tmp = Basicop.L_shl(L_tmp, 3, pOverflow);

            /* Multiplication by two of output speech with saturation. */
            signal[p++] = (short) Basicop.pv_round(Basicop.L_shl(L_tmp, 1, pOverflow), pOverflow);

            st.y2_hi = st.y1_hi;
            st.y2_lo = st.y1_lo;
            st.y1_hi = (L_tmp >> 16) << 16 >> 16;
            st.y1_lo = ((L_tmp >> 1) - (st.y1_hi << 15)) << 16 >> 16;
        }
    }

    private static final short[] aReflAState = new short[Cnst.M];
    private static final short[] aReflBState = new short[Cnst.M];

    /** a_refl.cpp A_Refl: convert direct-form coefficients to reflection coeffs */
    public static void A_Refl(short[] a, int aOff, short[] refl, int reflOff, int[] pOverflow) {
        final short[] aState = aReflAState;
        final short[] bState = aReflBState;
        int normShift;
        int normProd;
        int L_acc;
        int scale;
        int L_temp;
        int temp;
        int multFac;

        /* initialize states */
        for (int i = 0; i < Cnst.M; i++) {
            aState[i] = a[aOff + i];
        }

        /* backward Levinson recursion */
        for (int i = Cnst.M - 1; i >= 0; i--) {
            if (Basicop.abs_s(aState[i]) >= 4096) {
                for (int j = 0; j < Cnst.M; j++) {
                    refl[reflOff + j] = 0;
                }
                break;
            }

            refl[reflOff + i] = (short) Basicop.shl(aState[i], 3, pOverflow);

            L_temp = Basicop.L_mult(refl[reflOff + i], refl[reflOff + i], pOverflow);
            L_acc = Basicop.L_sub(Basicop.MAX_32, L_temp, pOverflow);

            normShift = Basicop.norm_l(L_acc);
            scale = (15 - normShift) << 16 >> 16;
            L_acc = Basicop.L_shl(L_acc, normShift, pOverflow);

            normProd = Basicop.pv_round(L_acc, pOverflow);
            multFac = Basicop.div_s(16384, normProd);

            boolean aborted = false;
            for (int j = 0; j < i; j++) {
                L_acc = aState[j] << 16;
                L_acc = Basicop.L_msu(L_acc, refl[reflOff + i], aState[i - j - 1], pOverflow);

                temp = Basicop.pv_round(L_acc, pOverflow);
                L_temp = Basicop.L_mult(multFac, temp, pOverflow);
                L_temp = Basicop.L_shr_r(L_temp, scale, pOverflow);

                int L_tmp_abs = L_temp - (L_temp < 0 ? 1 : 0);
                L_tmp_abs = L_tmp_abs ^ (L_tmp_abs >> 31);
                if (L_tmp_abs > 32767) {
                    for (int k = 0; k < Cnst.M; k++) {
                        refl[reflOff + k] = 0;
                    }
                    aborted = true;
                    break;
                }

                bState[j] = (short) ((L_temp << 16) >> 16);
            }
            if (aborted) {
                break;
            }

            for (int j = 0; j < i; j++) {
                aState[j] = bState[j];
            }
        }
    }

    public static final int NB_PULSE_DTX = 10; /* number of random pulses in DTX operation */

    /**
     * b_cn_cod.cpp pseudonoise.
     * @param pShift_reg int[1] in/out CN generator state
     */
    public static int pseudonoise(int[] pShift_reg, int no_bits) {
        int noise_bits = 0;
        int Sn;
        int temp;

        for (int i = 0; i < no_bits; i++) {
            /* State n == 31 */
            if ((pShift_reg[0] & 0x00000001) != 0) {
                Sn = 1;
            } else {
                Sn = 0;
            }
            /* State n == 3 */
            if ((pShift_reg[0] & 0x10000000) != 0) {
                Sn ^= 1;
            } else {
                Sn ^= 0;
            }

            noise_bits = (noise_bits << 1) << 16 >> 16;
            temp = (pShift_reg[0] & 1) << 16 >> 16;
            noise_bits = (noise_bits | temp) << 16 >> 16;

            pShift_reg[0] >>= 1;
            if ((Sn & 1) != 0) {
                pShift_reg[0] |= 0x40000000;
            }
        }
        return noise_bits;
    }

    /**
     * b_cn_cod.cpp build_CN_code.
     * @param pSeed int[1] in/out CN generator state
     */
    public static void build_CN_code(int[] pSeed, short[] cod, int codOff, int[] pOverflow) {
        int i, j, temp;

        for (i = 0; i < Cnst.L_SUBFR; i++) {
            cod[codOff + i] = 0;
        }

        for (int k = 0; k < NB_PULSE_DTX; k++) {
            i = pseudonoise(pSeed, 2); /* generate pulse position */
            temp = (Basicop.L_mult(i, 10, pOverflow) << 16) >> 16;
            i = temp >> 1;
            i = Basicop.add_16(i, k, pOverflow);

            j = pseudonoise(pSeed, 1); /* generate sign */
            if (j > 0) {
                cod[codOff + i] = 4096;
            } else {
                cod[codOff + i] = -4096;
            }
        }
    }

    /**
     * b_cn_cod.cpp build_CN_param.
     * @param pSeed short[1] in/out (Word16 seed!)
     */
    public static void build_CN_param(short[] pSeed, int n_param, short[] param_size_table,
                                      short[] parm, int parmOff, int[] pOverflow) {
        int L_temp;
        int temp;

        L_temp = Basicop.L_mult(pSeed[0], 31821, pOverflow);
        L_temp >>= 1;
        pSeed[0] = (short) ((Basicop.L_add(L_temp, 13849, pOverflow) << 16) >> 16);

        int pTemp = pSeed[0] & 0x7f; /* index into window_200_40 */

        for (int i = 0; i < n_param; i++) {
            temp = (~(0xffff << param_size_table[i])) << 16 >> 16;
            parm[parmOff + i] = (short) (Tables.window_200_40[pTemp++] & temp);
        }
    }
}

/**
 * Post filter and excitation control, ported from opencore-amr 0.1.6
 * dec/src/pstfilt.cpp and dec/src/ex_ctrl.cpp
 * (via src/dec/pstfilt.js of the JS reference port).
 */
final class Pstfilt {
    private Pstfilt() {}

    public static final int L_H = 22; /* size of truncated impulse response of A(z/g1)/A(z/g2) */

    public static final short[] gamma3_MR122 = {
        22938, 16057, 11240, 7868, 5508, 3856, 2699, 1889, 1322, 925,
    };
    public static final short[] gamma3 = {
        18022, 9912, 5451, 2998, 1649, 907, 499, 274, 151, 83,
    };
    public static final short[] gamma4_MR122 = {
        24576, 18432, 13824, 10368, 7776, 5832, 4374, 3281, 2461, 1846,
    };
    public static final short[] gamma4 = {
        22938, 16057, 11240, 7868, 5508, 3856, 2699, 1889, 1322, 925,
    };

    /** pstfilt.h Post_FilterState */
    public static final class State {
        public short[] res2;
        public short[] mem_syn_pst;
        public PostPre.PreemphasisState preemph_state;
        public Agc.State agc_state;
        public short[] synth_buf;

        public State() {
            this.res2 = new short[Cnst.L_SUBFR];
            this.mem_syn_pst = new short[Cnst.M];
            this.preemph_state = new PostPre.PreemphasisState();
            this.agc_state = new Agc.State();
            this.synth_buf = new short[Cnst.M + Cnst.L_FRAME];
            reset();
        }

        /** pstfilt.cpp Post_Filter_reset */
        public int reset() {
            for (int i = 0; i < Cnst.M; i++) {
                mem_syn_pst[i] = 0;
            }
            for (int i = 0; i < Cnst.L_SUBFR; i++) {
                res2[i] = 0;
            }
            for (int i = 0; i < Cnst.M + Cnst.L_FRAME; i++) {
                synth_buf[i] = 0;
            }
            this.agc_state.reset();
            this.preemph_state.reset();
            return 0;
        }
    }

    private static final short[] pfAp3 = new short[Cnst.MP1];
    private static final short[] pfAp4 = new short[Cnst.MP1];
    private static final short[] pfH = new short[L_H];

    /** pstfilt.cpp Post_Filter */
    public static void Post_Filter(State st, int mode, short[] syn, int synOff,
                                   short[] Az_4, int Az_4Off, int[] pOverflow) {
        final short[] Ap3 = pfAp3;
        final short[] Ap4 = pfAp4; /* bandwidth expanded LP parameters */
        final short[] h = pfH;
        int temp1;
        int temp2;
        int L_tmp;
        int L_tmp2;
        final short[] syn_work = st.synth_buf; /* syn_work = &synth_buf[M] */
        final int SW = Cnst.M; /* offset of syn_work inside synth_buf */

        /* Post filtering */
        for (int i = 0; i < Cnst.L_FRAME; i++) {
            syn_work[SW + i] = syn[synOff + i];
        }

        int Az = Az_4Off;
        for (int i_subfr = 0; i_subfr < Cnst.L_FRAME; i_subfr += Cnst.L_SUBFR) {
            /* Find weighted filter coefficients Ap3[] and Ap4[] */
            if (mode == Cnst.MR122 || mode == Cnst.MR102) {
                Filters.Weight_Ai(Az_4, Az, gamma3_MR122, 0, Ap3, 0);
                Filters.Weight_Ai(Az_4, Az, gamma4_MR122, 0, Ap4, 0);
            } else {
                Filters.Weight_Ai(Az_4, Az, gamma3, 0, Ap3, 0);
                Filters.Weight_Ai(Az_4, Az, gamma4, 0, Ap4, 0);
            }

            /* filtering of synthesis speech by A(z/0.7) to find res2[] */
            Filters.Residu(Ap3, 0, syn_work, SW + i_subfr, st.res2, 0, Cnst.L_SUBFR);

            /* tilt compensation filter: impulse response of A(z/0.7)/A(z/0.75) */
            for (int i = 0; i <= Cnst.M; i++) {
                h[i] = Ap3[i];
            }
            for (int i = Cnst.M + 1; i < L_H; i++) {
                h[i] = 0;
            }
            Filters.Syn_filt(Ap4, 0, h, 0, h, 0, L_H, h, Cnst.M + 1, 0);

            /* 1st correlation of h[] */
            L_tmp = 0;
            for (int i = L_H - 1; i >= 0; i--) {
                L_tmp2 = h[i] * h[i];
                if (L_tmp2 != 0x40000000) {
                    L_tmp2 = L_tmp2 << 1;
                } else {
                    /* C: sets pOverflow and breaks without accumulating */
                    pOverflow[0] = 1;
                    break;
                }
                L_tmp = Basicop.L_add(L_tmp, L_tmp2, pOverflow);
            }
            temp1 = (L_tmp >> 16) << 16 >> 16;

            L_tmp = 0;
            for (int i = L_H - 2; i >= 0; i--) {
                L_tmp2 = h[i] * h[i + 1];
                if (L_tmp2 != 0x40000000) {
                    L_tmp2 = L_tmp2 << 1;
                } else {
                    pOverflow[0] = 1;
                    break;
                }
                L_tmp = Basicop.L_add(L_tmp, L_tmp2, pOverflow);
            }
            temp2 = (L_tmp >> 16) << 16 >> 16;

            if (temp2 <= 0) {
                temp2 = 0;
            } else {
                L_tmp = (temp2 * Cnst.MU) >> 15;
                /* Sign-extend product */
                if ((L_tmp & 0x00010000) != 0) {
                    L_tmp = L_tmp | 0xffff0000;
                }
                temp2 = (L_tmp << 16) >> 16;
                temp2 = Basicop.div_s(temp2, temp1);
            }

            PostPre.preemphasis(st.preemph_state, st.res2, 0, temp2, Cnst.L_SUBFR, pOverflow);

            /* filtering through 1/A(z/0.75) */
            Filters.Syn_filt(Ap4, 0, st.res2, 0, syn, synOff + i_subfr, Cnst.L_SUBFR, st.mem_syn_pst, 0, 1);

            /* scale output to input */
            Agc.agc(st.agc_state, syn_work, SW + i_subfr, syn, synOff + i_subfr,
                Cnst.AGC_FAC, Cnst.L_SUBFR, pOverflow);

            Az += Cnst.MP1;
        }

        /* update syn_work[] buffer: syn_work[-M..-1] = syn_work[L_FRAME-M..L_FRAME-1] */
        for (int i = 0; i < Cnst.M; i++) {
            st.synth_buf[i] = st.synth_buf[Cnst.L_FRAME + i];
        }
    }

    /** ex_ctrl.cpp Ex_ctrl: excitation scaling for error concealment */
    public static int Ex_ctrl(short[] excitation, int excitationOff, int excEnergy, short[] exEnergyHist,
                              int exEnergyHistOff, int voicedHangover, int prevBFI, int carefulFlag,
                              int[] pOverflow) {
        int exp;
        int testEnergy, scaleFactor, avgEnergy, prevEnergy;
        int t0;

        /* get target level */
        avgEnergy = Mathops.gmed_n(exEnergyHist, exEnergyHistOff, 9);

        prevEnergy = (exEnergyHist[exEnergyHistOff + 7] + exEnergyHist[exEnergyHistOff + 8]) >> 1;
        if (exEnergyHist[exEnergyHistOff + 8] < prevEnergy) {
            prevEnergy = exEnergyHist[exEnergyHistOff + 8];
        }

        /* upscaling to avoid too rapid energy rises for some cases */
        if (excEnergy < avgEnergy && excEnergy > 5) {
            testEnergy = Basicop.shl(prevEnergy, 2, pOverflow); /* 4*prevEnergy */

            if (voicedHangover < 7 || prevBFI != 0) {
                /* testEnergy = 3*prevEnergy */
                testEnergy = Basicop.sub(testEnergy, prevEnergy, pOverflow);
            }

            if (avgEnergy > testEnergy) {
                avgEnergy = testEnergy;
            }

            /* scaleFactor = avgEnergy/excEnergy in Q0 */
            exp = Basicop.norm_s(excEnergy);
            excEnergy = Basicop.shl(excEnergy, exp, pOverflow);
            excEnergy = Basicop.div_s(16383, excEnergy);
            t0 = Basicop.L_mult(avgEnergy, excEnergy, pOverflow);
            t0 = Basicop.L_shr(t0, Basicop.sub(20, exp, pOverflow), pOverflow); /* 20 for Q10 */
            if (t0 > 32767) {
                t0 = 32767; /* saturate */
            }
            scaleFactor = (t0 << 16) >> 16;

            /* test if scaleFactor > 3.0 */
            if (carefulFlag != 0 && scaleFactor > 3072) {
                scaleFactor = 3072;
            }

            /* scale the excitation by scaleFactor */
            for (int i = 0; i < Cnst.L_SUBFR; i++) {
                t0 = Basicop.L_mult(scaleFactor, excitation[excitationOff + i], pOverflow);
                t0 = Basicop.L_shr(t0, 11, pOverflow);
                excitation[excitationOff + i] = (short) ((t0 << 16) >> 16);
            }
        }
        return 0;
    }
}

/**
 * DTX decoder, ported from opencore-amr 0.1.6 dec/src/dtx_dec.cpp
 * (+ dtx_dec.h, common/include/dtx_common_def.h)
 * (via src/dec/dtx_dec.js of the JS reference port).
 */
final class DtxDec {
    private DtxDec() {}

    /* dtx_dec.h enum DTXStateType */
    public static final int SPEECH = 0;
    public static final int DTX = 1;
    public static final int DTX_MUTE = 2;

    public static final int DTX_MAX_EMPTY_THRESH = 50;
    public static final int DTX_HIST_SIZE = 8;
    public static final int DTX_ELAPSED_FRAMES_THRESH = 24 + 7 - 1;
    public static final int DTX_HANG_CONST = 7; /* yields eight frames of SP HANGOVER */
    public static final int PN_INITIAL_SEED = 0x70816958; /* Pseudo noise generator seed value */

    /* Scaling factors for the lsp variability operation */
    public static final short[] lsf_hist_mean_scale = {
        20000, 20000, 20000, 20000, 20000, 18000, 16384, 8192, 0, 0,
    };

    /* level adjustment for different modes Q11 */
    public static final short[] dtx_log_en_adjust = {
        -1023, /* MR475 */
        -878,  /* MR515 */
        -732,  /* MR59  */
        -586,  /* MR67  */
        -440,  /* MR74  */
        -294,  /* MR795 */
        -148,  /* MR102 */
        0,     /* MR122 */
        0,     /* MRDTX */
    };

    /** dtx_dec.h dtx_decState */
    public static final class State {
        public int since_last_sid;
        public int true_sid_period_inv;
        public int log_en;
        public int old_log_en;
        public int[] L_pn_seed_rx;
        public short[] lsp;
        public short[] lsp_old;
        public short[] lsf_hist;
        public int lsf_hist_ptr;
        public short[] lsf_hist_mean;
        public int log_pg_mean;
        public short[] log_en_hist;
        public int log_en_hist_ptr;
        public int log_en_adjust;
        public int dtxHangoverCount;
        public int decAnaElapsedCount;
        public int sid_frame;
        public int valid_data;
        public int dtxHangoverAdded;
        public int dtxGlobalState; /* contains previous state */
        public int data_updated;   /* marker to know if CNI data is ever renewed */

        public State() {
            this.L_pn_seed_rx = new int[1];
            this.lsp = new short[Cnst.M];
            this.lsp_old = new short[Cnst.M];
            this.lsf_hist = new short[Cnst.M * DTX_HIST_SIZE];
            this.lsf_hist_mean = new short[Cnst.M * DTX_HIST_SIZE];
            this.log_en_hist = new short[DTX_HIST_SIZE];
            this.dtxGlobalState = DTX;
            reset();
        }

        /** dtx_dec.cpp dtx_dec_reset */
        public int reset() {
            this.since_last_sid = 0;
            this.true_sid_period_inv = 1 << 13;
            this.log_en = 3500;
            this.old_log_en = 3500;
            /* low level noise for better performance in DTX handover cases */
            this.L_pn_seed_rx[0] = PN_INITIAL_SEED;

            final short[] lspInit = { 30000, 26000, 21000, 15000, 8000, 0, -8000, -15000, -21000, -26000 };
            System.arraycopy(lspInit, 0, this.lsp, 0, Cnst.M);
            System.arraycopy(lspInit, 0, this.lsp_old, 0, Cnst.M);

            this.lsf_hist_ptr = 0;
            this.log_pg_mean = 0;
            this.log_en_hist_ptr = 0;

            /* initialize decoder lsf history */
            final short[] lsfInit = { 1384, 2077, 3420, 5108, 6742, 8122, 9863, 11092, 12714, 13701 };
            System.arraycopy(lsfInit, 0, this.lsf_hist, 0, Cnst.M);
            for (int i = 1; i < DTX_HIST_SIZE; i++) {
                System.arraycopy(this.lsf_hist, 0, this.lsf_hist, Cnst.M * i, Cnst.M);
            }
            for (int i = 0; i < Cnst.M * DTX_HIST_SIZE; i++) {
                this.lsf_hist_mean[i] = 0;
            }

            /* initialize decoder log frame energy */
            for (int i = 0; i < DTX_HIST_SIZE; i++) {
                this.log_en_hist[i] = (short) this.log_en;
            }

            this.log_en_adjust = 0;
            this.dtxHangoverCount = DTX_HANG_CONST;
            this.decAnaElapsedCount = 32767;
            this.sid_frame = 0;
            this.valid_data = 0;
            this.dtxHangoverAdded = 0;
            this.dtxGlobalState = DTX;
            this.data_updated = 0;
            return 0;
        }
    }

    private static final short[] ddLspInt = new short[Cnst.M];
    private static final short[] ddAcoeff = new short[Cnst.M + 1];
    private static final short[] ddRefl = new short[Cnst.M];
    private static final short[] ddEx = new short[Cnst.L_SUBFR];
    private static final short[] ddLsfInt = new short[Cnst.M];
    private static final short[] ddLsfIntVariab = new short[Cnst.M];
    private static final short[] ddLspIntVariab = new short[Cnst.M];
    private static final short[] ddAcoeffVariab = new short[Cnst.M + 1];
    private static final short[] ddLsf = new short[Cnst.M];
    private static final int[] ddLlsf = new int[Cnst.M];
    private static final short[] ddExp = new short[1];
    private static final short[] ddFrac = new short[1];

    /** dtx_dec.cpp dtx_dec */
    public static void dtx_dec(State st, short[] mem_syn, int mem_synOff, DPlsf.State lsfState,
                               GcPred.State predState, CGaver.State averState, int new_state, int mode,
                               short[] parm, int parmOff, short[] synth, int synthOff,
                               short[] A_t, int A_tOff, int[] pOverflow) {
        int log_en_index;
        int i, j;
        int int_fac;
        int L_log_en_int;
        final short[] lsp_int = ddLspInt;
        int log_en_int_e;
        int log_en_int_m;
        int level;
        final short[] acoeff = ddAcoeff;
        final short[] refl = ddRefl;
        int pred_err;
        final short[] ex = ddEx;
        int ma_pred_init;
        int log_pg;
        int negative;
        int lsf_mean;
        int L_lsf_mean;
        int lsf_variab_index;
        int lsf_variab_factor;
        final short[] lsf_int = ddLsfInt;
        final short[] lsf_int_variab = ddLsfIntVariab;
        final short[] lsp_int_variab = ddLspIntVariab;
        final short[] acoeff_variab = ddAcoeffVariab;
        final short[] lsf = ddLsf;
        final int[] L_lsf = ddLlsf;
        int ptr;
        int tmp_int_length;
        int L_temp;
        int temp;

        if (st.dtxHangoverAdded != 0 && st.sid_frame != 0) {
            /* sid_first after dtx hangover period, or sid_upd after dtxhangover */

            /* set log_en_adjust to correct value */
            st.log_en_adjust = dtx_log_en_adjust[mode];

            ptr = st.lsf_hist_ptr + Cnst.M;
            if (ptr == 80) {
                ptr = 0;
            }
            System.arraycopy(st.lsf_hist, st.lsf_hist_ptr, st.lsf_hist, ptr, Cnst.M);

            ptr = st.log_en_hist_ptr + 1;
            if (ptr == DTX_HIST_SIZE) {
                ptr = 0;
            }
            st.log_en_hist[ptr] = st.log_en_hist[st.log_en_hist_ptr]; /* Q11 */

            /* compute mean log energy and lsp from decoded signal (SID_FIRST) */
            st.log_en = 0;
            for (i = Cnst.M - 1; i >= 0; i--) {
                L_lsf[i] = 0;
            }

            /* average energy and lsp */
            for (i = DTX_HIST_SIZE - 1; i >= 0; i--) {
                if (st.log_en_hist[i] < 0) {
                    temp = ~(~st.log_en_hist[i] >> 3);
                } else {
                    temp = st.log_en_hist[i] >> 3;
                }
                st.log_en = Basicop.add_16(st.log_en, temp, pOverflow);
                for (j = Cnst.M - 1; j >= 0; j--) {
                    L_lsf[j] = Basicop.L_add(L_lsf[j], st.lsf_hist[i * Cnst.M + j], pOverflow);
                }
            }

            for (j = Cnst.M - 1; j >= 0; j--) {
                if (L_lsf[j] < 0) {
                    lsf[j] = (short) ((~(~L_lsf[j] >> 3) << 16) >> 16);
                } else {
                    lsf[j] = (short) ((L_lsf[j] >> 3) << 16 >> 16);
                }
            }

            LspFns.Lsf_lsp(lsf, 0, st.lsp, 0, Cnst.M, pOverflow);

            /* make log_en speech coder mode independent; added again before synth */
            st.log_en = Basicop.sub(st.log_en, st.log_en_adjust, pOverflow);

            /* compute lsf variability vector */
            System.arraycopy(st.lsf_hist, 0, st.lsf_hist_mean, 0, 80);

            for (i = Cnst.M - 1; i >= 0; i--) {
                L_lsf_mean = 0;
                /* compute mean lsf */
                for (j = 8 - 1; j >= 0; j--) {
                    L_lsf_mean = Basicop.L_add(L_lsf_mean, st.lsf_hist_mean[i + j * Cnst.M], pOverflow);
                }

                if (L_lsf_mean < 0) {
                    lsf_mean = (~(~L_lsf_mean >> 3) << 16) >> 16;
                } else {
                    lsf_mean = (L_lsf_mean >> 3) << 16 >> 16;
                }

                /* subtract mean and limit to within reasonable limits;
                   the upper lsf's are attenuated */
                for (j = 8 - 1; j >= 0; j--) {
                    /* subtract mean */
                    st.lsf_hist_mean[i + j * Cnst.M] =
                        (short) Basicop.sub(st.lsf_hist_mean[i + j * Cnst.M], lsf_mean, pOverflow);

                    /* attenuate deviation from mean, especially for upper lsf's */
                    st.lsf_hist_mean[i + j * Cnst.M] =
                        (short) Basicop.mult(st.lsf_hist_mean[i + j * Cnst.M], lsf_hist_mean_scale[i], pOverflow);

                    /* limit the deviation */
                    if (st.lsf_hist_mean[i + j * Cnst.M] < 0) {
                        negative = 1;
                    } else {
                        negative = 0;
                    }
                    st.lsf_hist_mean[i + j * Cnst.M] =
                        (short) Basicop.abs_s(st.lsf_hist_mean[i + j * Cnst.M]);

                    /* apply soft limit */
                    if (st.lsf_hist_mean[i + j * Cnst.M] > 655) {
                        st.lsf_hist_mean[i + j * Cnst.M] = (short) (
                            655 + ((st.lsf_hist_mean[i + j * Cnst.M] - 655) >> 2));
                    }

                    /* apply hard limit */
                    if (st.lsf_hist_mean[i + j * Cnst.M] > 1310) {
                        st.lsf_hist_mean[i + j * Cnst.M] = 1310;
                    }

                    if (negative != 0) {
                        st.lsf_hist_mean[i + j * Cnst.M] = (short) -st.lsf_hist_mean[i + j * Cnst.M];
                    }
                }
            }
        }

        if (st.sid_frame != 0) {
            /* Set old SID parameters, always shift even if no new valid_data */
            System.arraycopy(st.lsp, 0, st.lsp_old, 0, Cnst.M);
            st.old_log_en = st.log_en;

            if (st.valid_data != 0) {
                /* new data available (no CRC) */
                /* Compute interpolation factor; limit to 32 frames */
                tmp_int_length = st.since_last_sid;
                st.since_last_sid = 0;

                if (tmp_int_length >= 32) {
                    tmp_int_length = 32;
                }

                L_temp = tmp_int_length << 10;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pOverflow[0] = 1;
                    L_temp = tmp_int_length > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                }
                temp = (L_temp << 16) >> 16;

                if (tmp_int_length >= 2) {
                    st.true_sid_period_inv = Basicop.div_s(1 << 10, temp);
                } else {
                    st.true_sid_period_inv = 1 << 14; /* 0.5 in Q15 */
                }

                DPlsf.Init_D_plsf_3(lsfState, parm[parmOff]);
                DPlsf.D_plsf_3(lsfState, Cnst.MRDTX, 0, parm, parmOff + 1, st.lsp, 0, pOverflow);

                /* reset for next speech frame */
                for (i = 0; i < Cnst.M; i++) {
                    lsfState.past_r_q[i] = 0;
                }

                log_en_index = parm[parmOff + 4];
                /* Q11 and divide by 4 */
                if (log_en_index > 63 || log_en_index < -64) {
                    st.log_en = log_en_index > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    st.log_en = (log_en_index << (11 - 2)) << 16 >> 16;
                }

                /* Subtract 2.5 in Q11 */
                st.log_en = (st.log_en - 2560 * 2) << 16 >> 16;

                /* Index 0 is reserved for silence */
                if (log_en_index == 0) {
                    st.log_en = Basicop.MIN_16;
                }

                /* no interpolation at startup after coder reset
                   or when SID_UPD has been received right after SPEECH */
                if (st.data_updated == 0 || st.dtxGlobalState == SPEECH) {
                    System.arraycopy(st.lsp, 0, st.lsp_old, 0, Cnst.M);
                    st.old_log_en = st.log_en;
                }
            } /* endif valid_data */

            /* initialize gain predictor memory of other modes */
            if (st.log_en < 0) {
                temp = ~(~st.log_en >> 1);
            } else {
                temp = st.log_en >> 1;
            }
            ma_pred_init = (temp - 9000) << 16 >> 16;
            if (ma_pred_init > 0) {
                ma_pred_init = 0;
            } else if (ma_pred_init < -14436) {
                ma_pred_init = -14436;
            }

            predState.past_qua_en[0] = (short) ma_pred_init;
            predState.past_qua_en[1] = (short) ma_pred_init;
            predState.past_qua_en[2] = (short) ma_pred_init;
            predState.past_qua_en[3] = (short) ma_pred_init;

            /* past_qua_en for other modes than MR122 */
            ma_pred_init = (ma_pred_init * 5443 >> 15) << 16 >> 16;
            /* scale down by factor 20*log10(2) in Q15 */
            predState.past_qua_en_MR122[0] = (short) ma_pred_init;
            predState.past_qua_en_MR122[1] = (short) ma_pred_init;
            predState.past_qua_en_MR122[2] = (short) ma_pred_init;
            predState.past_qua_en_MR122[3] = (short) ma_pred_init;
        } /* endif sid_frame */

        /* CN generation: recompute level adjustment factor Q11
           st->log_en_adjust = 0.9*st->log_en_adjust + 0.1*dtx_log_en_adjust[mode] */
        if (dtx_log_en_adjust[mode] > 1023) {
            temp = Basicop.MAX_16;
        } else if (dtx_log_en_adjust[mode] < -1024) {
            temp = Basicop.MIN_16;
        } else {
            temp = ((dtx_log_en_adjust[mode] << 5) * 3277 >> 15) << 16 >> 16;
        }

        if (temp < 0) {
            temp = ~(~temp >> 5);
        } else {
            temp >>= 5;
        }
        st.log_en_adjust = Basicop.add_16(
            (st.log_en_adjust * 29491 >> 15) << 16 >> 16, temp, pOverflow);

        /* Interpolate SID info */
        int_fac = Basicop.shl((st.since_last_sid + 1) << 16 >> 16, 10, pOverflow); /* Q10 */
        int_fac = Basicop.mult(int_fac, st.true_sid_period_inv, pOverflow); /* Q10*Q15->Q10 */

        /* Maximize to 1.0 in Q10 */
        if (int_fac > 1024) {
            int_fac = 16384;
        } else if (int_fac < -2048) {
            int_fac = Basicop.MIN_16;
        } else {
            int_fac = (int_fac << 4) << 16 >> 16; /* Q10 -> Q14 */
        }

        L_log_en_int = Basicop.L_mult(int_fac, st.log_en, pOverflow); /* Q14 * Q11 -> Q26 */
        for (i = Cnst.M - 1; i >= 0; i--) {
            lsp_int[i] = (short) Basicop.mult(int_fac, st.lsp[i], pOverflow); /* Q14 * Q15 -> Q14 */
        }

        int_fac = (16384 - int_fac) << 16 >> 16; /* 1-k in Q14 */

        /* (Q14 * Q11 -> Q26) + Q26 -> Q26 */
        L_log_en_int = Basicop.L_mac(L_log_en_int, int_fac, st.old_log_en, pOverflow);
        for (i = Cnst.M - 1; i >= 0; i--) {
            /* Q14 + (Q14 * Q15 -> Q14) -> Q14 */
            lsp_int[i] = (short) Basicop.add_16(lsp_int[i],
                Basicop.mult(int_fac, st.lsp_old[i], pOverflow), pOverflow);
            L_temp = lsp_int[i] << 1; /* Q14 -> Q15 */
            if (L_temp != ((L_temp << 16) >> 16)) {
                pOverflow[0] = 1;
                L_temp = lsp_int[i] > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
            }
            lsp_int[i] = (short) ((L_temp << 16) >> 16);
        }

        /* compute the amount of lsf variability */
        lsf_variab_factor = (st.log_pg_mean - 2457) << 16 >> 16; /* -0.6 in Q12 */
        /* *0.3 Q12*Q15 -> Q12 */
        lsf_variab_factor = (4096 - Basicop.mult(lsf_variab_factor, 9830, pOverflow)) << 16 >> 16;

        /* limit to values between 0..1 in Q12 */
        if (lsf_variab_factor > 4095) {
            lsf_variab_factor = Basicop.MAX_16;
        } else if (lsf_variab_factor < 0) {
            lsf_variab_factor = 0;
        } else {
            lsf_variab_factor = (lsf_variab_factor << 3) << 16 >> 16; /* -> Q15 */
        }

        /* get index of vector to do variability with */
        lsf_variab_index = PostPre.pseudonoise(st.L_pn_seed_rx, 3);

        /* convert to lsf */
        LspFns.Lsp_lsf(lsp_int, 0, lsf_int, 0, Cnst.M, pOverflow);

        /* apply lsf variability */
        System.arraycopy(lsf_int, 0, lsf_int_variab, 0, Cnst.M);
        for (i = Cnst.M - 1; i >= 0; i--) {
            lsf_int_variab[i] = (short) Basicop.add_16(lsf_int_variab[i],
                Basicop.mult(lsf_variab_factor, st.lsf_hist_mean[i + lsf_variab_index * Cnst.M], pOverflow),
                pOverflow);
        }

        /* make sure that LSP's are ordered */
        LspFns.Reorder_lsf(lsf_int, 0, Cnst.LSF_GAP, Cnst.M, pOverflow);
        LspFns.Reorder_lsf(lsf_int_variab, 0, Cnst.LSF_GAP, Cnst.M, pOverflow);

        /* copy lsf to speech decoders lsf state */
        System.arraycopy(lsf_int, 0, lsfState.past_lsf_q, 0, Cnst.M);

        /* convert to lsp */
        LspFns.Lsf_lsp(lsf_int, 0, lsp_int, 0, Cnst.M, pOverflow);
        LspFns.Lsf_lsp(lsf_int_variab, 0, lsp_int_variab, 0, Cnst.M, pOverflow);

        /* Compute acoeffs Q12: acoeff for level normalization and postfilter,
           acoeff_variab for synthesis filter */
        LspFns.Lsp_Az(lsp_int, 0, acoeff, 0, pOverflow);
        LspFns.Lsp_Az(lsp_int_variab, 0, acoeff_variab, 0, pOverflow);

        /* For use in postfilter */
        for (i = 0; i <= Cnst.M; i++) {
            A_t[A_tOff + i] = acoeff[i];
            A_t[A_tOff + Cnst.M + 1 + i] = acoeff[i];
            A_t[A_tOff + 2 * (Cnst.M + 1) + i] = acoeff[i];
            A_t[A_tOff + 3 * (Cnst.M + 1) + i] = acoeff[i];
        }

        /* Compute reflection coefficients Q15 */
        PostPre.A_Refl(acoeff, 1, refl, 0, pOverflow);

        /* Compute prediction error in Q15 */
        pred_err = Basicop.MAX_16; /* 0.99997 in Q15 */
        for (i = 0; i < Cnst.M; i++) {
            L_temp = (refl[i] * refl[i]) >> 15;
            if (L_temp <= 0x00007fff) {
                temp = (Basicop.MAX_16 - L_temp) << 16 >> 16;
            } else {
                pOverflow[0] = 1;
                temp = 0;
            }
            pred_err = Basicop.mult(pred_err, temp, pOverflow);
        }

        /* compute logarithm of prediction gain */
        Mathops.Log2(pred_err, ddExp, ddFrac, pOverflow);
        final int log_pg_e = ddExp[0];
        final int log_pg_m = ddFrac[0];

        /* convert exponent and mantissa to Word16 Q12 */
        log_pg = Basicop.shl((log_pg_e - 15) << 16 >> 16, 12, pOverflow); /* Q12 */
        log_pg = Basicop.shr(Basicop.sub(0, Basicop.add_16(log_pg,
            Basicop.shr(log_pg_m, 15 - 12, pOverflow), pOverflow), pOverflow), 1, pOverflow);
        st.log_pg_mean = Basicop.add_16(Basicop.mult(29491, st.log_pg_mean, pOverflow),
            Basicop.mult(3277, log_pg, pOverflow), pOverflow);

        /* Compute interpolated log energy */
        L_log_en_int = Basicop.L_shr(L_log_en_int, 10, pOverflow); /* Q26 -> Q16 */

        /* Add 4 in Q16 */
        L_log_en_int = Basicop.L_add(L_log_en_int, 4 * 65536, pOverflow);

        /* subtract prediction gain */
        L_log_en_int = Basicop.L_sub(L_log_en_int, Basicop.L_shl(log_pg, 4, pOverflow), pOverflow);

        /* adjust level to speech coder mode */
        L_log_en_int = Basicop.L_add(L_log_en_int, Basicop.L_shl(st.log_en_adjust, 5, pOverflow), pOverflow);

        log_en_int_e = (L_log_en_int >> 16) << 16 >> 16;
        log_en_int_m = (Basicop.L_shr(Basicop.L_sub(L_log_en_int, log_en_int_e << 16, pOverflow),
            1, pOverflow) << 16) >> 16;
        level = (Mathops.Pow2(log_en_int_e, log_en_int_m, pOverflow) << 16) >> 16; /* Q4 */

        for (i = 0; i < 4; i++) {
            /* Compute innovation vector */
            PostPre.build_CN_code(st.L_pn_seed_rx, ex, 0, pOverflow);
            for (j = Cnst.L_SUBFR - 1; j >= 0; j--) {
                ex[j] = (short) Basicop.mult(level, ex[j], pOverflow);
            }
            /* Synthesize */
            Filters.Syn_filt(acoeff_variab, 0, ex, 0, synth, synthOff + i * Cnst.L_SUBFR, Cnst.L_SUBFR,
                mem_syn, mem_synOff, 1);
        }

        /* reset codebook averaging variables */
        averState.hangVar = 20;
        averState.hangCount = 0;

        if (new_state == DTX_MUTE) {
            /* mute comfort noise as it has been quite a long time since
               last SID update was performed */
            tmp_int_length = st.since_last_sid;
            if (tmp_int_length > 32) {
                tmp_int_length = 32;
            } else if (tmp_int_length <= 0) {
                /* safety guard against division by zero */
                tmp_int_length = 8;
            }

            L_temp = tmp_int_length << 10;
            if (L_temp != ((L_temp << 16) >> 16)) {
                pOverflow[0] = 1;
                L_temp = tmp_int_length > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
            }
            temp = (L_temp << 16) >> 16;
            st.true_sid_period_inv = Basicop.div_s(1 << 10, temp);

            st.since_last_sid = 0;
            System.arraycopy(st.lsp, 0, st.lsp_old, 0, Cnst.M);
            st.old_log_en = st.log_en;
            /* subtract 1/8 in Q11 i.e -6/8 dB */
            st.log_en = (st.log_en - 256) << 16 >> 16;
            if (st.log_en < 0) {
                st.log_en = 0;
            }
        }

        /* reset interpolation length timer if data has been updated */
        if (st.sid_frame != 0
            && (st.valid_data != 0 || (st.valid_data == 0 && st.dtxHangoverAdded != 0))) {
            st.since_last_sid = 0;
            st.data_updated = 1;
        }
    }

    private static final short[] dauExp = new short[1];
    private static final short[] dauFrac = new short[1];

    /** dtx_dec.cpp dtx_dec_activity_update */
    public static void dtx_dec_activity_update(State st, short[] lsf, int lsfOff,
                                               short[] frame, int frameOff, int[] pOverflow) {
        int L_frame_en;
        int L_temp;
        int log_en_e;
        int log_en_m;
        int log_en;

        /* update lsp history */
        st.lsf_hist_ptr += Cnst.M;
        if (st.lsf_hist_ptr == 80) {
            st.lsf_hist_ptr = 0;
        }
        for (int i = 0; i < Cnst.M; i++) {
            st.lsf_hist[st.lsf_hist_ptr + i] = lsf[lsfOff + i];
        }

        /* compute log energy based on frame energy */
        L_frame_en = 0; /* Q0 */
        for (int i = Cnst.L_FRAME - 1; i >= 0; i--) {
            L_temp = frame[frameOff + i] * frame[frameOff + i];
            if (L_temp != 0x40000000) {
                L_temp = L_temp << 1;
            } else {
                L_temp = Basicop.MAX_32;
            }
            L_frame_en = Basicop.L_add(L_frame_en, L_temp, pOverflow);
        }

        Mathops.Log2(L_frame_en, dauExp, dauFrac, pOverflow);
        log_en_e = dauExp[0];
        log_en_m = dauFrac[0];

        /* convert exponent and mantissa to Word16 Q10 */
        L_temp = log_en_e << 10;
        if (L_temp != ((L_temp << 16) >> 16)) {
            pOverflow[0] = 1;
            L_temp = log_en_e > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
        }
        log_en_e = (L_temp << 16) >> 16;

        if (log_en_m < 0) {
            log_en_m = ~(~log_en_m >> 5);
        } else {
            log_en_m >>= 5;
        }
        log_en = (log_en_e + log_en_m) << 16 >> 16;

        /* divide with L_FRAME i.e subtract with log2(L_FRAME) = 7.32193 */
        log_en = (log_en - (7497 + 1024)) << 16 >> 16;

        /* insert into log energy buffer; log_en in decoder is Q11 */
        st.log_en_hist_ptr += 1;
        if (st.log_en_hist_ptr == DTX_HIST_SIZE) {
            st.log_en_hist_ptr = 0;
        }
        st.log_en_hist[st.log_en_hist_ptr] = (short) log_en; /* Q11 */
    }

    /** dtx_dec.cpp rx_dtx_handler: returns new DTXStateType */
    public static int rx_dtx_handler(State st, int frame_type, int[] pOverflow) {
        int newState;
        int encState;

        /* DTX if SID frame or previously in DTX{_MUTE} and (NO_RX OR BAD_SPEECH) */
        /* RXFrameType values (frame.h): RX_SID_FIRST=4, RX_SID_UPDATE=5,
           RX_SID_BAD=6, RX_NO_DATA=7, RX_SPEECH_BAD=3, RX_ONSET=2 */
        if (frame_type == 4 /* RX_SID_FIRST */
            || frame_type == 5 /* RX_SID_UPDATE */
            || frame_type == 6 /* RX_SID_BAD */
            || ((st.dtxGlobalState == DTX || st.dtxGlobalState == DTX_MUTE)
                && (frame_type == 7 /* RX_NO_DATA */ || frame_type == 3 /* RX_SPEECH_BAD */
                    || frame_type == 2 /* RX_ONSET */))) {
            newState = DTX;

            /* stay in mute for these input types */
            if (st.dtxGlobalState == DTX_MUTE
                && (frame_type == 6 || frame_type == 4
                    || frame_type == 2 || frame_type == 7)) {
                newState = DTX_MUTE;
            }

            /* evaluate if noise parameters are too old;
               since_last_sid is reset when CN parameters have been updated */
            st.since_last_sid += 1;

            /* no update of sid parameters in DTX for a long while;
               SID_UPDATE frames handled separately to avoid entering DTX_MUTE
               for late SID_UPDATE frames */
            if (frame_type != 5 && st.since_last_sid > DTX_MAX_EMPTY_THRESH) {
                newState = DTX_MUTE;
            }
        } else {
            newState = SPEECH;
            st.since_last_sid = 0;
        }

        /* reset the decAnaElapsed Counter when receiving CNI data the first time,
           to robustify counter mismatch after handover */
        if (st.data_updated == 0 && frame_type == 5) {
            st.decAnaElapsedCount = 0;
        }

        /* update the SPE-SPD DTX hangover synchronization */
        st.decAnaElapsedCount = Basicop.add_16(st.decAnaElapsedCount, 1, pOverflow);
        st.dtxHangoverAdded = 0;

        if (frame_type == 4 || frame_type == 5 || frame_type == 6
            || frame_type == 2 || frame_type == 7) {
            encState = DTX;
            if (frame_type == 7 && newState == SPEECH) {
                encState = SPEECH;
            }
        } else {
            encState = SPEECH;
        }

        if (encState == SPEECH) {
            st.dtxHangoverCount = DTX_HANG_CONST;
        } else if (st.decAnaElapsedCount > DTX_ELAPSED_FRAMES_THRESH) {
            st.dtxHangoverAdded = 1;
            st.decAnaElapsedCount = 0;
            st.dtxHangoverCount = 0;
        } else if (st.dtxHangoverCount == 0) {
            st.decAnaElapsedCount = 0;
        } else {
            st.dtxHangoverCount -= 1;
        }

        if (newState != SPEECH) {
            /* DTX or DTX_MUTE: CN data is not in a first SID, first SIDs are marked
               as SID_BAD but will do backwards analysis if a hangover period has
               been added according to the state machine above */
            st.sid_frame = 0;
            st.valid_data = 0;

            if (frame_type == 4) {
                st.sid_frame = 1;
            } else if (frame_type == 5) {
                st.sid_frame = 1;
                st.valid_data = 1;
            } else if (frame_type == 6) {
                st.sid_frame = 1;
                st.dtxHangoverAdded = 0; /* use old data */
            }
        }

        /* newState is used by both SPEECH AND DTX synthesis routines */
        return newState;
    }
}

/**
 * Main AMR-NB decoder, ported from opencore-amr 0.1.6 dec/src/dec_amr.cpp
 * (Decoder_amrState, Decoder_amr_init/reset, Decoder_amr)
 * (via src/dec/dec_amr.js of the JS reference port).
 *
 * C pointer st->exc = st->old_exc + PIT_MAX + L_INTERPOL becomes the constant
 * offset EXC into st.old_exc.
 */
final class DecAmr {
    private DecAmr() {}

    public static final int EXC_ENERGY_HIST_LEN = 9;
    public static final int LTP_GAIN_HISTORY_LEN = 9;
    public static final int EXC = Cnst.PIT_MAX + Cnst.L_INTERPOL; /* st->exc offset into old_exc */

    /* bitno[mode][i] — bit counts per parameter, mirrors JS tables/index bitno */
    public static final short[][] BITNO = {
        Tables.bitno_MR475, Tables.bitno_MR515, Tables.bitno_MR59, Tables.bitno_MR67,
        Tables.bitno_MR74, Tables.bitno_MR795, Tables.bitno_MR102, Tables.bitno_MR122,
        Tables.bitno_MRDTX,
    };

    /** dec_amr.h Decoder_amrState */
    public static final class State {
        public short[] old_exc;
        public short[] lsp_old;
        public short[] mem_syn;
        public int sharp;
        public int old_T0;
        public int prev_bf;
        public int prev_pdf;
        public int state;
        public short[] excEnergyHist;
        public int T0_lagBuff;
        public int inBackgroundNoise;
        public short[] voicedHangover; /* C Word16, passed by address */
        public short[] ltpGainHistory;
        public Bgnscd.State background_state;
        public short[] nodataSeed; /* C Word16, passed by address */
        public CGaver.State Cb_gain_averState;
        public EcGains.LspAvgState lsp_avg_st;
        public DPlsf.State lsfState;
        public EcGains.GainPitchState ec_gain_p_st;
        public EcGains.GainCodeState ec_gain_c_st;
        public GcPred.State pred_state;
        public PhDisp.State ph_disp_st;
        public DtxDec.State dtxDecoderState;
        public int[] overflow;

        public State() {
            this.old_exc = new short[Cnst.L_SUBFR + Cnst.PIT_MAX + Cnst.L_INTERPOL];
            this.lsp_old = new short[Cnst.M];
            this.mem_syn = new short[Cnst.M];
            this.sharp = 0;
            this.old_T0 = 0;
            this.prev_bf = 0;
            this.prev_pdf = 0;
            this.state = 0;
            this.excEnergyHist = new short[EXC_ENERGY_HIST_LEN];
            this.T0_lagBuff = 0;
            this.inBackgroundNoise = 0;
            this.voicedHangover = new short[1];
            this.ltpGainHistory = new short[LTP_GAIN_HISTORY_LEN];
            this.background_state = new Bgnscd.State();
            this.nodataSeed = new short[1];
            this.Cb_gain_averState = new CGaver.State();
            this.lsp_avg_st = new EcGains.LspAvgState();
            this.lsfState = new DPlsf.State();
            this.ec_gain_p_st = new EcGains.GainPitchState();
            this.ec_gain_c_st = new EcGains.GainCodeState();
            this.pred_state = new GcPred.State();
            this.ph_disp_st = new PhDisp.State();
            this.dtxDecoderState = new DtxDec.State();
            this.overflow = new int[1];
            init();
        }

        /** dec_amr.cpp Decoder_amr_init */
        public int init() {
            this.T0_lagBuff = 40;
            this.inBackgroundNoise = 0;
            this.voicedHangover[0] = 0;
            this.overflow[0] = 0;
            for (int i = 0; i < LTP_GAIN_HISTORY_LEN; i++) {
                this.ltpGainHistory[i] = 0;
            }

            this.lsfState.reset();
            this.ec_gain_p_st.reset();
            this.ec_gain_c_st.reset();
            this.Cb_gain_averState.reset();
            this.lsp_avg_st.reset();
            this.background_state.reset();
            this.ph_disp_st.reset();
            this.dtxDecoderState.reset();
            this.pred_state.reset();

            Decoder_amr_reset(this, Cnst.MR475);
            return 0;
        }
    }

    /** dec_amr.cpp Decoder_amr_reset */
    public static int Decoder_amr_reset(State state, int mode) {
        /* Static vectors to zero (only old_exc head, like C memset of
           PIT_MAX + L_INTERPOL entries) */
        for (int i = 0; i < Cnst.PIT_MAX + Cnst.L_INTERPOL; i++) {
            state.old_exc[i] = 0;
        }

        if (mode != Cnst.MRDTX) {
            for (int i = 0; i < Cnst.M; i++) {
                state.mem_syn[i] = 0;
            }
        }

        /* initialize pitch sharpening */
        state.sharp = Cnst.SHARPMIN;
        state.old_T0 = 40;

        /* Initialize overflow Flag */
        state.overflow[0] = 0;

        if (mode != Cnst.MRDTX) {
            final short[] init = { 30000, 26000, 21000, 15000, 8000, 0, -8000, -15000, -21000, -26000 };
            System.arraycopy(init, 0, state.lsp_old, 0, Cnst.M);
        }

        /* Initialize memories of bad frame handling */
        state.prev_bf = 0;
        state.prev_pdf = 0;
        state.state = 0;

        state.T0_lagBuff = 40;
        state.inBackgroundNoise = 0;
        state.voicedHangover[0] = 0;
        if (mode != Cnst.MRDTX) {
            for (int i = 0; i < EXC_ENERGY_HIST_LEN; i++) {
                state.excEnergyHist[i] = 0;
            }
        }
        for (int i = 0; i < LTP_GAIN_HISTORY_LEN; i++) {
            state.ltpGainHistory[i] = 0;
        }
        state.Cb_gain_averState.reset();
        if (mode != Cnst.MRDTX) {
            state.lsp_avg_st.reset();
        }

        state.lsfState.reset();
        state.ec_gain_p_st.reset();
        state.ec_gain_c_st.reset();
        if (mode != Cnst.MRDTX) {
            state.pred_state.reset();
        }
        state.background_state.reset();
        state.nodataSeed[0] = 21845;
        state.ph_disp_st.reset();
        if (mode != Cnst.MRDTX) {
            state.dtxDecoderState.reset();
        }
        return 0;
    }

    /* scratch buffers (single-threaded decoder, mirrors C stack arrays) */
    private static final short[] daLspNew = new short[Cnst.M];
    private static final short[] daLspMid = new short[Cnst.M];
    private static final short[] daPrevLsf = new short[Cnst.M];
    private static final short[] daLsfI = new short[Cnst.M];
    private static final short[] daCode = new short[Cnst.L_SUBFR];
    private static final short[] daExcp = new short[Cnst.L_SUBFR];
    private static final short[] daExcEnhanced = new short[Cnst.L_SUBFR];
    private static final short[] daT0 = new short[1];
    private static final short[] daT0frac = new short[1];
    private static final short[] daGainPit = new short[1];
    private static final short[] daGainCode = new short[1];
    private static final short[] daSqrtExp = new short[1];

    /** dec_amr.cpp Decoder_amr */
    public static void Decoder_amr(State st, int mode, short[] parm, int parmOff, int frame_type,
                                   short[] synth, int synthOff, short[] A_t, int A_tOff) {
        final short[] lsp_new = daLspNew;
        final short[] lsp_mid = daLspMid;
        final short[] prev_lsf = daPrevLsf;
        final short[] lsf_i = daLsfI;
        final short[] code = daCode;
        final short[] excp = daExcp;
        final short[] exc_enhanced = daExcEnhanced;

        int i;
        int T0 = 0;
        int T0_frac;
        int index;
        int index_mr475 = 0;
        int gain_pit;
        int gain_code;
        int gain_code_mix;
        int pit_sharp;
        int pit_flag;
        int pitch_fac;
        int t0_min;
        int t0_max;
        int delta_frc_low;
        int delta_frc_range;
        int tmp_shift;
        int temp;
        int L_temp;
        int flag4;
        int carefulFlag;
        int excEnergy;
        int subfrNr;
        int evenSubfr = 0;
        int bfi = 0;  /* bad frame indication flag */
        int pdfi = 0; /* potential degraded bad frame flag */
        final int[] pOverflow = st.overflow;
        int pParm = parmOff;

        /* find the new DTX state: SPEECH OR DTX */
        final int newDTXState = DtxDec.rx_dtx_handler(st.dtxDecoderState, frame_type, pOverflow);

        /* DTX actions */
        if (newDTXState != DtxDec.SPEECH) {
            Decoder_amr_reset(st, Cnst.MRDTX);

            DtxDec.dtx_dec(st.dtxDecoderState, st.mem_syn, 0, st.lsfState, st.pred_state,
                st.Cb_gain_averState, newDTXState, mode, parm, pParm,
                synth, synthOff, A_t, A_tOff, pOverflow);

            /* update average lsp */
            LspFns.Lsf_lsp(st.lsfState.past_lsf_q, 0, st.lsp_old, 0, Cnst.M, pOverflow);
            EcGains.lsp_avg(st.lsp_avg_st, st.lsfState.past_lsf_q, 0, pOverflow);

            st.dtxDecoderState.dtxGlobalState = newDTXState;
            return;
        }

        /* SPEECH action state machine */
        if (frame_type == Cnst.RX_SPEECH_BAD || frame_type == Cnst.RX_NO_DATA
            || frame_type == Cnst.RX_ONSET) {
            bfi = 1;
            if (frame_type == Cnst.RX_NO_DATA || frame_type == Cnst.RX_ONSET) {
                PostPre.build_CN_param(st.nodataSeed, Tables.prmno[mode], BITNO[mode],
                    parm, pParm, pOverflow);
            }
        } else if (frame_type == Cnst.RX_SPEECH_DEGRADED) {
            pdfi = 1;
        }

        if (bfi != 0) {
            st.state += 1;
        } else if (st.state == 6) {
            st.state = 5;
        } else {
            st.state = 0;
        }

        if (st.state > 6) {
            st.state = 6;
        }

        /* If this frame is the first speech frame after CNI period, set the BFH
           state machine to an appropriate state depending on whether there was
           DTX muting before start of speech or not. */
        if (st.dtxDecoderState.dtxGlobalState == DtxDec.DTX) {
            st.state = 5;
            st.prev_bf = 0;
        } else if (st.dtxDecoderState.dtxGlobalState == DtxDec.DTX_MUTE) {
            st.state = 5;
            st.prev_bf = 1;
        }

        /* save old LSFs for CB gain smoothing */
        System.arraycopy(st.lsfState.past_lsf_q, 0, prev_lsf, 0, Cnst.M);

        /* decode LSF parameters and generate interpolated lpc coefficients
           for the 4 subframes */
        if (mode != Cnst.MR122) {
            DPlsf.D_plsf_3(st.lsfState, mode, bfi, parm, pParm, lsp_new, 0, pOverflow);
            pParm += 3;
            IntLpc.Int_lpc_1to3(st.lsp_old, 0, lsp_new, 0, A_t, A_tOff, pOverflow);
        } else {
            DPlsf.D_plsf_5(st.lsfState, bfi, parm, pParm, lsp_mid, 0, lsp_new, 0, pOverflow);
            pParm += 5;
            IntLpc.Int_lpc_1and3(st.lsp_old, 0, lsp_mid, 0, lsp_new, 0, A_t, A_tOff, pOverflow);
        }

        /* update the LSPs for the next frame */
        for (i = 0; i < Cnst.M; i++) {
            st.lsp_old[i] = lsp_new[i];
        }

        /*--------------------------------------------------------------------*
         * Loop for every subframe in the analysis frame                      *
         *--------------------------------------------------------------------*/
        int Az = A_tOff; /* pointer to interpolated LPC parameters */

        evenSubfr = 0;
        subfrNr = -1;
        for (int i_subfr = 0; i_subfr < Cnst.L_FRAME; i_subfr += Cnst.L_SUBFR) {
            subfrNr += 1;
            evenSubfr = 1 - evenSubfr;

            /* flag for first and 3th subframe */
            pit_flag = i_subfr;
            if (i_subfr == Cnst.L_FRAME_BY2) {
                if (mode != Cnst.MR475 && mode != Cnst.MR515) {
                    pit_flag = 0;
                }
            }

            /* pitch index */
            index = parm[pParm++];

            /* decode pitch lag and find adaptive codebook vector */
            if (mode != Cnst.MR122) {
                /* flag4 indicates encoding with 4 bit resolution
                   (MR475, MR515, MR59, MR67) */
                flag4 = 0;
                if (mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59 || mode == Cnst.MR67) {
                    flag4 = 1;
                }

                /* get ranges for t0_min and t0_max (only needed in delta decoding) */
                delta_frc_low = 5;
                delta_frc_range = 9;
                if (mode == Cnst.MR795) {
                    delta_frc_low = 10;
                    delta_frc_range = 19;
                }

                t0_min = (st.old_T0 - delta_frc_low) << 16 >> 16;
                if (t0_min < Cnst.PIT_MIN) {
                    t0_min = Cnst.PIT_MIN;
                }
                t0_max = (t0_min + delta_frc_range) << 16 >> 16;
                if (t0_max > Cnst.PIT_MAX) {
                    t0_max = Cnst.PIT_MAX;
                    t0_min = (t0_max - delta_frc_range) << 16 >> 16;
                }

                DecGain.Dec_lag3(index, t0_min, t0_max, pit_flag, st.old_T0,
                    daT0, daT0frac, flag4, pOverflow);
                T0 = daT0[0];
                T0_frac = daT0frac[0];

                st.T0_lagBuff = T0;

                if (bfi != 0) {
                    if (st.old_T0 < Cnst.PIT_MAX) {
                        /* Graceful pitch degradation */
                        st.old_T0 += 1;
                    }
                    T0 = st.old_T0;
                    T0_frac = 0;

                    if (st.inBackgroundNoise != 0 && st.voicedHangover[0] > 4
                        && (mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59)) {
                        T0 = st.T0_lagBuff;
                    }
                }

                Filters.Pred_lt_3or6(st.old_exc, EXC, T0, T0_frac, Cnst.L_SUBFR, 1, pOverflow);
            } else {
                DecGain.Dec_lag6(index, Cnst.PIT_MIN_MR122, Cnst.PIT_MAX, pit_flag, daT0, daT0frac, pOverflow);
                T0 = daT0[0];
                T0_frac = daT0frac[0];

                if (!(bfi == 0 && (pit_flag == 0 || index < 61))) {
                    st.T0_lagBuff = T0;
                    T0 = st.old_T0;
                    T0_frac = 0;
                }

                Filters.Pred_lt_3or6(st.old_exc, EXC, T0, T0_frac, Cnst.L_SUBFR, 0, pOverflow);
            }
            daT0[0] = (short) T0; /* keep scratch in sync for Dec_lag6 2nd/4th subframe input */

            /* (MR122 only: decode pitch gain), decode innovative codebook,
               set pitch sharpening factor */
            gain_pit = 0;
            if (mode == Cnst.MR475 || mode == Cnst.MR515) {
                index = parm[pParm++]; /* index of position */
                i = parm[pParm++];     /* signs */

                DPulse.decode_2i40_9bits(subfrNr, i, index, code, 0, pOverflow);

                L_temp = st.sharp << 1;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pit_sharp = st.sharp > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    pit_sharp = (L_temp << 16) >> 16;
                }
            } else if (mode == Cnst.MR59) {
                index = parm[pParm++];
                i = parm[pParm++];

                DPulse.decode_2i40_11bits(i, index, code, 0);

                L_temp = st.sharp << 1;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pit_sharp = st.sharp > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    pit_sharp = (L_temp << 16) >> 16;
                }
            } else if (mode == Cnst.MR67) {
                index = parm[pParm++];
                i = parm[pParm++];

                DPulse.decode_3i40_14bits(i, index, code, 0);

                L_temp = st.sharp << 1;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pit_sharp = st.sharp > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    pit_sharp = (L_temp << 16) >> 16;
                }
            } else if (mode <= Cnst.MR795) {
                /* MR74, MR795 */
                index = parm[pParm++];
                i = parm[pParm++];

                DPulse.decode_4i40_17bits(i, index, code, 0);

                L_temp = st.sharp << 1;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pit_sharp = st.sharp > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    pit_sharp = (L_temp << 16) >> 16;
                }
            } else if (mode == Cnst.MR102) {
                DPulse.dec_8i40_31bits(parm, pParm, code, 0, pOverflow);
                pParm += 7;

                L_temp = st.sharp << 1;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pit_sharp = st.sharp > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    pit_sharp = (L_temp << 16) >> 16;
                }
            } else {
                /* MR122 */
                index = parm[pParm++];
                if (bfi != 0) {
                    EcGains.ec_gain_pitch(st.ec_gain_p_st, st.state, daGainPit, pOverflow);
                } else {
                    daGainPit[0] = (short) DecGain.d_gain_pitch(mode, index);
                }
                EcGains.ec_gain_pitch_update(st.ec_gain_p_st, bfi, st.prev_bf, daGainPit, pOverflow);
                gain_pit = daGainPit[0];

                DPulse.dec_10i40_35bits(parm, pParm, code, 0);
                pParm += 10;

                /* pit_sharp = gain_pit; if (pit_sharp > 1.0) pit_sharp = 1.0 */
                L_temp = gain_pit << 1;
                if (L_temp != ((L_temp << 16) >> 16)) {
                    pit_sharp = gain_pit > 0 ? Basicop.MAX_16 : Basicop.MIN_16;
                } else {
                    pit_sharp = (L_temp << 16) >> 16;
                }
            }

            /* Add the pitch contribution to code[] */
            for (i = T0; i < Cnst.L_SUBFR; i++) {
                temp = Basicop.mult(code[i - T0], pit_sharp, pOverflow);
                code[i] = (short) Basicop.add_16(code[i], temp, pOverflow);
            }

            /* Decode codebook gain (MR122) or both pitch gain and codebook gain
               (all others); update pitch sharpening "sharp" with quantized gain_pit */
            if (mode == Cnst.MR475) {
                /* read and decode pitch and code gain */
                if (evenSubfr != 0) {
                    index_mr475 = parm[pParm++]; /* index of gain(s) */
                }

                if (bfi == 0) {
                    DecGain.Dec_gain(st.pred_state, mode, index_mr475, code, 0, evenSubfr,
                        daGainPit, daGainCode, pOverflow);
                } else {
                    EcGains.ec_gain_pitch(st.ec_gain_p_st, st.state, daGainPit, pOverflow);
                    EcGains.ec_gain_code(st.ec_gain_c_st, st.pred_state, st.state, daGainCode, pOverflow);
                }
                EcGains.ec_gain_pitch_update(st.ec_gain_p_st, bfi, st.prev_bf, daGainPit, pOverflow);
                EcGains.ec_gain_code_update(st.ec_gain_c_st, bfi, st.prev_bf, daGainCode, pOverflow);
                gain_pit = daGainPit[0];
                gain_code = daGainCode[0];

                pit_sharp = gain_pit;
                if (pit_sharp > Cnst.SHARPMAX) {
                    pit_sharp = Cnst.SHARPMAX;
                }
            } else if (mode <= Cnst.MR74 || mode == Cnst.MR102) {
                /* read and decode pitch and code gain */
                index = parm[pParm++]; /* index of gain(s) */

                if (bfi == 0) {
                    DecGain.Dec_gain(st.pred_state, mode, index, code, 0, evenSubfr,
                        daGainPit, daGainCode, pOverflow);
                } else {
                    EcGains.ec_gain_pitch(st.ec_gain_p_st, st.state, daGainPit, pOverflow);
                    EcGains.ec_gain_code(st.ec_gain_c_st, st.pred_state, st.state, daGainCode, pOverflow);
                }
                EcGains.ec_gain_pitch_update(st.ec_gain_p_st, bfi, st.prev_bf, daGainPit, pOverflow);
                EcGains.ec_gain_code_update(st.ec_gain_c_st, bfi, st.prev_bf, daGainCode, pOverflow);
                gain_pit = daGainPit[0];
                gain_code = daGainCode[0];

                pit_sharp = gain_pit;
                if (pit_sharp > Cnst.SHARPMAX) {
                    pit_sharp = Cnst.SHARPMAX;
                }

                if (mode == Cnst.MR102) {
                    if (st.old_T0 > Cnst.L_SUBFR + 5) {
                        if (pit_sharp < 0) {
                            pit_sharp = ~(~pit_sharp >> 2);
                        } else {
                            pit_sharp = pit_sharp >> 2;
                        }
                    }
                }
            } else {
                /* read and decode pitch gain */
                index = parm[pParm++]; /* index of gain(s) */

                if (mode == Cnst.MR795) {
                    /* decode pitch gain */
                    if (bfi != 0) {
                        EcGains.ec_gain_pitch(st.ec_gain_p_st, st.state, daGainPit, pOverflow);
                    } else {
                        daGainPit[0] = (short) DecGain.d_gain_pitch(mode, index);
                    }
                    EcGains.ec_gain_pitch_update(st.ec_gain_p_st, bfi, st.prev_bf, daGainPit, pOverflow);
                    gain_pit = daGainPit[0];

                    /* read and decode code gain */
                    index = parm[pParm++];
                    if (bfi == 0) {
                        DecGain.d_gain_code(st.pred_state, mode, index, code, 0, daGainCode, pOverflow);
                    } else {
                        EcGains.ec_gain_code(st.ec_gain_c_st, st.pred_state, st.state, daGainCode, pOverflow);
                    }
                    EcGains.ec_gain_code_update(st.ec_gain_c_st, bfi, st.prev_bf, daGainCode, pOverflow);
                    gain_code = daGainCode[0];

                    pit_sharp = gain_pit;
                    if (pit_sharp > Cnst.SHARPMAX) {
                        pit_sharp = Cnst.SHARPMAX;
                    }
                } else {
                    /* MR122 */
                    if (bfi == 0) {
                        DecGain.d_gain_code(st.pred_state, mode, index, code, 0, daGainCode, pOverflow);
                    } else {
                        EcGains.ec_gain_code(st.ec_gain_c_st, st.pred_state, st.state, daGainCode, pOverflow);
                    }
                    EcGains.ec_gain_code_update(st.ec_gain_c_st, bfi, st.prev_bf, daGainCode, pOverflow);
                    gain_code = daGainCode[0];

                    pit_sharp = gain_pit;
                }
            }

            /* store pitch sharpening for next subframe (do not update sharpening
               in even subframes for MR475) */
            if (mode != Cnst.MR475 || evenSubfr == 0) {
                st.sharp = gain_pit;
                if (st.sharp > Cnst.SHARPMAX) {
                    st.sharp = Cnst.SHARPMAX;
                }
            }

            pit_sharp = Basicop.shl(pit_sharp, 1, pOverflow);
            if (pit_sharp > 16384) {
                for (i = 0; i < Cnst.L_SUBFR; i++) {
                    temp = Basicop.mult(st.old_exc[EXC + i], pit_sharp, pOverflow);
                    L_temp = Basicop.L_mult(temp, gain_pit, pOverflow);
                    if (mode == Cnst.MR122) {
                        if (L_temp < 0) {
                            L_temp = ~(~L_temp >> 1);
                        } else {
                            L_temp = L_temp >> 1;
                        }
                    }
                    excp[i] = (short) Basicop.pv_round(L_temp, pOverflow);
                }
            }

            /* Store list of LTP gains needed in the SCD */
            if (bfi == 0) {
                for (i = 0; i < 8; i++) {
                    st.ltpGainHistory[i] = st.ltpGainHistory[i + 1];
                }
                st.ltpGainHistory[8] = (short) gain_pit;
            }

            /* Limit gain_pit if in background noise and BFI for MR475, MR515, MR59 */
            if ((st.prev_bf != 0 || bfi != 0) && st.inBackgroundNoise != 0
                && (mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59)) {
                if (gain_pit > 12288) {
                    /* if (gain_pit > 0.75) in Q14 */
                    gain_pit = ((((gain_pit - 12288) >> 1) + 12288) << 16) >> 16;
                    /* gain_pit = (gain_pit-0.75)/2.0 + 0.75; */
                }
                if (gain_pit > 14745) {
                    /* if (gain_pit > 0.90) in Q14 */
                    gain_pit = 14745;
                }
            }

            /* Calculate CB mixed gain */
            DPlsf.Int_lsf(prev_lsf, 0, st.lsfState.past_lsf_q, 0, i_subfr, lsf_i, 0, pOverflow);
            gain_code_mix = CGaver.Cb_gain_average(st.Cb_gain_averState, mode, gain_code,
                lsf_i, 0, st.lsp_avg_st.lsp_meanSave, 0, bfi, st.prev_bf, pdfi,
                st.prev_pdf, st.inBackgroundNoise, st.voicedHangover[0], pOverflow);

            /* make sure that MR74, MR795, MR122 have original code_gain */
            if (mode > Cnst.MR67 && mode != Cnst.MR102) {
                /* MR74, MR795, MR122 */
                gain_code_mix = gain_code;
            }

            /* Find the total excitation; find synthesis speech for st->exc[] */
            if (mode <= Cnst.MR102) {
                /* MR475, MR515, MR59, MR67, MR74, MR795, MR102 */
                pitch_fac = gain_pit;
                tmp_shift = 1;
            } else {
                /* MR122 */
                if (gain_pit < 0) {
                    pitch_fac = ~(~gain_pit >> 1);
                } else {
                    pitch_fac = gain_pit >> 1;
                }
                tmp_shift = 2;
            }

            /* copy unscaled LTP excitation to exc_enhanced (used in phase
               dispersion below) and compute total excitation for LTP feedback */
            for (i = 0; i < Cnst.L_SUBFR; i++) {
                exc_enhanced[i] = st.old_exc[EXC + i];
                /* st->exc[i] = gain_pit*st->exc[i] + gain_code*code[i]; */
                L_temp = Basicop.L_mult(st.old_exc[EXC + i], pitch_fac, pOverflow);
                L_temp = Basicop.L_mac(L_temp, code[i], gain_code, pOverflow);
                L_temp = Basicop.L_shl(L_temp, tmp_shift, pOverflow); /* Q16 */
                st.old_exc[EXC + i] = (short) Basicop.pv_round(L_temp, pOverflow);
            }

            /* Adaptive phase dispersion */
            PhDisp.ph_disp_release(st.ph_disp_st); /* free phase dispersion adaption */

            if ((mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59)
                && st.voicedHangover[0] > 3 && st.inBackgroundNoise != 0 && bfi != 0) {
                PhDisp.ph_disp_lock(st.ph_disp_st); /* always use full phase disp. */
            }

            /* apply phase dispersion to innovation (if enabled) and
               compute total excitation for synthesis part */
            PhDisp.ph_disp(st.ph_disp_st, mode, exc_enhanced, 0, gain_code_mix, gain_pit,
                code, 0, pitch_fac, tmp_shift, pOverflow);

            /* The excitation control module is active during BFI;
               conceal drops in signal energy if in bg noise. */
            L_temp = 0;
            for (i = 0; i < Cnst.L_SUBFR; i++) {
                L_temp = Basicop.L_mac(L_temp, exc_enhanced[i], exc_enhanced[i], pOverflow);
            }

            /* excEnergy = sqrt(L_temp) in Q0 */
            if (L_temp < 0) {
                L_temp = ~(~L_temp >> 1);
            } else {
                L_temp = L_temp >> 1;
            }
            L_temp = Mathops.sqrt_l_exp(L_temp, daSqrtExp, pOverflow);
            temp = daSqrtExp[0];
            /* To cope with 16-bit and scaling in ex_ctrl() */
            L_temp = Basicop.L_shr(L_temp, ((temp >> 1) + 15) << 16 >> 16, pOverflow);
            if (L_temp < 0) {
                excEnergy = (~(~L_temp >> 2) << 16) >> 16;
            } else {
                excEnergy = ((L_temp >> 2) << 16) >> 16;
            }

            if ((mode == Cnst.MR475 || mode == Cnst.MR515 || mode == Cnst.MR59)
                && st.voicedHangover[0] > 5 && st.inBackgroundNoise != 0 && st.state < 4
                && ((pdfi != 0 && st.prev_pdf != 0) || bfi != 0 || st.prev_bf != 0)) {
                carefulFlag = 0;
                if (pdfi != 0 && bfi == 0) {
                    carefulFlag = 1;
                }

                Pstfilt.Ex_ctrl(exc_enhanced, 0, excEnergy, st.excEnergyHist, 0,
                    st.voicedHangover[0], st.prev_bf, carefulFlag, pOverflow);
            }

            if (!(st.inBackgroundNoise != 0 && (bfi != 0 || st.prev_bf != 0)
                && st.state < 4)) {
                /* Update energy history for all modes */
                for (i = 0; i < 8; i++) {
                    st.excEnergyHist[i] = st.excEnergyHist[i + 1];
                }
                st.excEnergyHist[8] = (short) excEnergy;
            }
            /* Excitation control module end */

            if (pit_sharp > 16384) {
                for (i = 0; i < Cnst.L_SUBFR; i++) {
                    excp[i] = (short) Basicop.add_16(excp[i], exc_enhanced[i], pOverflow);
                }
                Agc.agc2(exc_enhanced, 0, excp, 0, Cnst.L_SUBFR, pOverflow);
                pOverflow[0] = 0;
                Filters.Syn_filt(A_t, Az, excp, 0, synth, synthOff + i_subfr, Cnst.L_SUBFR,
                    st.mem_syn, 0, 0);
            } else {
                pOverflow[0] = 0;
                Filters.Syn_filt(A_t, Az, exc_enhanced, 0, synth, synthOff + i_subfr, Cnst.L_SUBFR,
                    st.mem_syn, 0, 0);
            }

            if (pOverflow[0] != 0) {
                /* Test for overflow */
                for (i = Cnst.PIT_MAX + Cnst.L_INTERPOL + Cnst.L_SUBFR - 1; i >= 0; i--) {
                    if (st.old_exc[i] < 0) {
                        st.old_exc[i] = (short) (~(~st.old_exc[i] >> 2));
                    } else {
                        st.old_exc[i] = (short) (st.old_exc[i] >> 2);
                    }
                }

                for (i = Cnst.L_SUBFR - 1; i >= 0; i--) {
                    if (exc_enhanced[i] < 0) {
                        exc_enhanced[i] = (short) (~(~exc_enhanced[i] >> 2));
                    } else {
                        exc_enhanced[i] = (short) (exc_enhanced[i] >> 2);
                    }
                }

                Filters.Syn_filt(A_t, Az, exc_enhanced, 0, synth, synthOff + i_subfr, Cnst.L_SUBFR,
                    st.mem_syn, 0, 1);
            } else {
                for (i = 0; i < Cnst.M; i++) {
                    st.mem_syn[i] = synth[synthOff + i_subfr + Cnst.L_SUBFR - Cnst.M + i];
                }
            }

            /* Update signal for next frame: shift st->old_exc[] left by L_SUBFR */
            System.arraycopy(st.old_exc, Cnst.L_SUBFR, st.old_exc, 0,
                Cnst.PIT_MAX + Cnst.L_INTERPOL);

            /* interpolated LPC parameters for next subframe */
            Az += Cnst.MP1;

            /* store T0 for next subframe */
            st.old_T0 = T0;
        }

        /* Call the Source Characteristic Detector which updates
           st->inBackgroundNoise and st->voicedHangover */
        st.inBackgroundNoise = Bgnscd.Bgn_scd(st.background_state, st.ltpGainHistory, 0,
            synth, synthOff, st.voicedHangover, pOverflow);

        DtxDec.dtx_dec_activity_update(st.dtxDecoderState, st.lsfState.past_lsf_q, 0,
            synth, synthOff, pOverflow);

        /* store bfi for next subframe */
        st.prev_bf = bfi;
        st.prev_pdf = pdfi;

        /* Calculate the LSF averages on the eight previous frames */
        EcGains.lsp_avg(st.lsp_avg_st, st.lsfState.past_lsf_q, 0, pOverflow);

        st.dtxDecoderState.dtxGlobalState = newDTXState;
    }
}

/**
 * Frame decode assembly, ported from opencore-amr 0.1.6 dec/src/sp_dec.cpp
 * (Bin2int, Bits2prm, Speech_Decode_FrameState, GSMInitDecode,
 * Speech_Decode_Frame_reset, GSMFrameDecode)
 * (via src/dec/sp_dec.js of the JS reference port).
 */
final class SpDec {
    private SpDec() {}

    /** sp_dec.cpp Bin2int (static) */
    private static int Bin2int(int no_of_bits, short[] bitstream, int bitstreamOff) {
        int value = 0;
        for (int i = 0; i < no_of_bits; i++) {
            value <<= 1;
            value |= bitstream[bitstreamOff + i];
        }
        return value;
    }

    /** sp_dec.cpp Bits2prm */
    public static void Bits2prm(int mode, short[] bits, int bitsOff, short[] prm, int prmOff) {
        int pBits = bitsOff;
        final short[] bitno = DecAmr.BITNO[mode];
        for (int i = 0; i < Tables.prmno[mode]; i++) {
            prm[prmOff + i] = (short) Bin2int(bitno[i], bits, pBits);
            pBits += bitno[i];
        }
    }

    /** sp_dec.h Speech_Decode_FrameState */
    public static final class State {
        public DecAmr.State decoder_amrState;
        public Pstfilt.State post_state;
        public PostPre.PostProcessState postHP_state;
        public int prev_mode;

        /** sp_dec.cpp GSMInitDecode */
        public State() {
            this.decoder_amrState = new DecAmr.State();
            this.post_state = new Pstfilt.State();
            this.postHP_state = new PostPre.PostProcessState();
            this.prev_mode = Cnst.MR475;
            reset();
        }

        /** sp_dec.cpp Speech_Decode_Frame_reset */
        public int reset() {
            DecAmr.Decoder_amr_reset(this.decoder_amrState, Cnst.MR475);
            this.post_state.reset();
            this.postHP_state.reset();
            this.prev_mode = Cnst.MR475;
            return 0;
        }
    }

    private static final short[] sdParm = new short[Cnst.MAX_PRM_SIZE + 1];
    private static final short[] sdAzDec = new short[Cnst.AZ_SIZE];

    /** sp_dec.cpp GSMFrameDecode */
    public static void GSMFrameDecode(State st, int mode, short[] serial, int serialOff,
                                      int frame_type, short[] synth, int synthOff) {
        final short[] parm = sdParm;    /* synthesis parameters */
        final short[] Az_dec = sdAzDec; /* decoded Az for post-filter in 4 subframes */
        final int[] pOverflow = st.decoder_amrState.overflow;

        /* Serial to parameters */
        if (frame_type == Cnst.RX_SID_BAD || frame_type == Cnst.RX_SID_UPDATE) {
            /* Override mode to MRDTX */
            Bits2prm(Cnst.MRDTX, serial, serialOff, parm, 0);
        } else {
            Bits2prm(mode, serial, serialOff, parm, 0);
        }

        /* Synthesis */
        DecAmr.Decoder_amr(st.decoder_amrState, mode, parm, 0, frame_type,
            synth, synthOff, Az_dec, 0);

        /* Post-filter */
        Pstfilt.Post_Filter(st.post_state, mode, synth, synthOff, Az_dec, 0, pOverflow);

        /* post HP filter, and 15->16 bits */
        PostPre.Post_Process(st.postHP_state, synth, synthOff, Cnst.L_FRAME, pOverflow);

        /* Truncate to 13 bits (C builds without NO13BIT defined) */
        for (int i = 0; i < Cnst.L_FRAME; i++) {
            synth[synthOff + i] = (short) (synth[synthOff + i] & 0xfff8);
        }
    }
}

/**
 * Frame unpacking + decode entry, ported from opencore-amr 0.1.6 dec/src:
 *   wmf_to_ets.cpp, amrdecode.cpp (MIME_IETF path), amrnb/wrapper.cpp
 *   (Decoder_Interface_*)
 * (via src/dec/amrdecode.js of the JS reference port).
 */
final class AmrDecode {
    private AmrDecode() {}

    public static final int NUM_AMRSID_RXMODE_BITS = 3;
    public static final int AMRSID_RXMODE_BIT_OFFSET = 36;
    public static final int AMRSID_RXTYPE_BIT_OFFSET = 35;

    /** wmf_to_ets.cpp wmf_to_ets */
    public static void wmf_to_ets(int frame_type_3gpp, byte[] wmf_input, int wmfOff, short[] ets_output) {
        /* Each bit gets its own slot in ets_output; for speech frames the bits
           are reordered via reorderBits[][]. */
        if (frame_type_3gpp < Cnst.AMR_SID) {
            final short[] reorder = REORDER_BITS[frame_type_3gpp];
            for (int i = Tables.numOfBits[frame_type_3gpp] - 1; i >= 0; i--) {
                ets_output[reorder[i]] =
                    (short) ((wmf_input[wmfOff + (i >> 3)] >> (~i & 0x7)) & 0x01);
            }
        } else {
            for (int i = Tables.numOfBits[frame_type_3gpp] - 1; i >= 0; i--) {
                ets_output[i] =
                    (short) ((wmf_input[wmfOff + (i >> 3)] >> (~i & 0x7)) & 0x01);
            }
        }
    }

    /* reorderBits[mode] — mirrors JS tables/index reorderBits */
    public static final short[][] REORDER_BITS = {
        Tables.reorderBits_MR475, Tables.reorderBits_MR515, Tables.reorderBits_MR59,
        Tables.reorderBits_MR67, Tables.reorderBits_MR74, Tables.reorderBits_MR795,
        Tables.reorderBits_MR102, Tables.reorderBits_MR122,
    };

    private static final short[] adEtsBuf = new short[Cnst.MAX_SERIAL_SIZE];

    /**
     * amrdecode.cpp AMRDecode (MIME_IETF input format only).
     * speech_bits is a byte[] of the frame payload (after the ToC byte).
     * Returns byte_offset (bytes consumed) or -1 on invalid frame type.
     */
    public static int AMRDecode(SpDec.State decoder_state, int frame_type,
                                byte[] speech_bits, int speechBitsOff,
                                short[] raw_pcm, int raw_pcmOff) {
        int mode = Cnst.MR475;
        int rx_type = Cnst.RX_NO_DATA;
        final short[] dec_ets_input_bfr = adEtsBuf;
        int byte_offset = -1;

        for (int i = 0; i < Cnst.MAX_SERIAL_SIZE; i++) {
            dec_ets_input_bfr[i] = 0;
        }

        /* Convert incoming packetized raw WMF data to ETS format */
        wmf_to_ets(frame_type, speech_bits, speechBitsOff, dec_ets_input_bfr);
        /* Address offset of the start of next frame */
        byte_offset = Tables.WmfDecBytesPerFrame[frame_type];

        /* Determine AMR codec mode and AMR RX frame type */
        if (frame_type <= Cnst.AMR_122) {
            mode = frame_type;
            rx_type = Cnst.RX_SPEECH_GOOD;
        } else if (frame_type == Cnst.AMR_SID) {
            /* read mode info from input buffer */
            int modeStore = 0;
            for (int i = 0; i < NUM_AMRSID_RXMODE_BITS; i++) {
                modeStore |= dec_ets_input_bfr[AMRSID_RXMODE_BIT_OFFSET + i] << i;
            }
            mode = modeStore;

            /* Get RX frame type */
            if (dec_ets_input_bfr[AMRSID_RXTYPE_BIT_OFFSET] == 0) {
                rx_type = Cnst.RX_SID_FIRST;
            } else {
                rx_type = Cnst.RX_SID_UPDATE;
            }
        } else if (frame_type < Cnst.AMR_NO_DATA) {
            /* Invalid frame_type, return error code */
            byte_offset = -1;
        } else {
            mode = decoder_state.prev_mode;
            /* RX_NO_DATA: exponential decay from latest valid frame for the first
               6 frames, after that silent frames */
            rx_type = Cnst.RX_NO_DATA;
        }

        /* Proceed with decoding frame, if there are no errors */
        if (byte_offset != -1) {
            /* Decode a 20 ms frame */
            SpDec.GSMFrameDecode(decoder_state, mode, dec_ets_input_bfr, 0, rx_type,
                raw_pcm, raw_pcmOff);

            /* Save mode for next frame */
            decoder_state.prev_mode = mode;
        }

        return byte_offset;
    }

    /** wrapper.cpp Decoder_Interface_init */
    public static SpDec.State Decoder_Interface_init() {
        return new SpDec.State();
    }

    /**
     * wrapper.cpp Decoder_Interface_Decode.
     * @param state Speech_Decode_FrameState
     * @param input one IETF frame incl. ToC byte
     * @param output 160 PCM samples
     * @param bfi bad frame indicator
     */
    public static void Decoder_Interface_Decode(SpDec.State state, byte[] input,
                                                short[] output, int bfi) {
        int type = (input[0] >> 3) & 0x0f;
        if (bfi != 0) {
            type = Cnst.AMR_NO_DATA;
        }
        AMRDecode(state, type, input, 1, output, 0);
    }
}
