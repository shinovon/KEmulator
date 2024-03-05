package emulator.lcdui;

import java.util.*;
import javax.microedition.lcdui.*;

public final class c {
    public c() {
        super();
    }

    private static String[] split(String s, char c) {
        char[] arr = s.toCharArray();
        int i = 0;
        ArrayList list = null;

        for (int j = 0; j < arr.length; ++j) {
            if (arr[j] == c) {
                if (list == null) {
                    list = new ArrayList();
                }

                list.add(new String(arr, i, j - i));
                i = j + 1;
            }
        }

        if (list == null) {
            return new String[]{s};
        } else {
            if (i < arr.length) {
                list.add(new String(arr, i, arr.length - i));
            }

            return (String[]) ((String[]) list.toArray(new String[list.size()]));
        }
    }

    public static String[] textArr(String s, Font font, int max, int var3, int[] w) {
        w[0] = 0;
        if (s == null) return new String[0];
        if (max > 0 && var3 > 0) {
            boolean var4 = s.indexOf(10) != -1;
            if (font.stringWidth(s) <= max) {
                setWidth(w, font.stringWidth(s));
                return var4 ? split(s, '\n') : new String[]{s};
            } else {
                ArrayList list = new ArrayList();
                if (!var4) {
                    splitToWidth(s, font, max, var3, list);
                } else {
                    char[] var7 = s.toCharArray();
                    int var8 = 0;

                    for (int var9 = 0; var9 < var7.length; ++var9) {
                        if (var7[var9] == 10 || var9 == var7.length - 1) {
                            String var11 = var9 == var7.length - 1 ? new String(var7, var8, var9 + 1 - var8) : new String(var7, var8, var9 - var8);
                            if (setWidth(w, font.stringWidth(var11)) <= max) {
                                list.add(var11);
                            } else {
                                splitToWidth(var11, font, max, var3, list);
                            }

                            var8 = var9 + 1;
                            max = var3;
                        }
                    }
                }

                return (String[]) ((String[]) list.toArray(new String[list.size()]));
            }
        } else {
            return new String[]{s};
        }
    }

    private static int setWidth(int[] w, int stringWidth) {
        if (stringWidth > w[0]) w[0] = stringWidth;
        return stringWidth;
    }

    public static String[] textArr(String s, Font font, int x1, int x2) {
        if (s == null) return new String[0];
        if (x1 > 0 && x2 > 0) {
            boolean var4 = s.indexOf('\n') != -1;
            if (font.stringWidth(s) <= x1) {
                return var4 ? split(s, '\n') : new String[]{s};
            } else {
                ArrayList list = new ArrayList();
                if (!var4) {
                    splitToWidth(s, font, x1, x2, list);
                } else {
                    char[] var7 = s.toCharArray();
                    int var8 = 0;

                    for (int var9 = 0; var9 < var7.length; ++var9) {
                        if (var7[var9] == 10 || var9 == var7.length - 1) {
                            String var11 = var9 == var7.length - 1 ? new String(var7, var8, var9 + 1 - var8) : new String(var7, var8, var9 - var8);
                            if (font.stringWidth(var11) <= x1) {
                                list.add(var11);
                            } else {
                                splitToWidth(var11, font, x1, x2, list);
                            }

                            var8 = var9 + 1;
                            x1 = x2;
                        }
                    }
                }

                return (String[]) ((String[]) list.toArray(new String[list.size()]));
            }
        } else {
            return new String[]{s};
        }
    }

    private static void splitToWidth(String s, Font font, int x1, int x2, ArrayList list) {
        char[] arr = s.toCharArray();
        int k = 0;
        int i = 0;
        int w = 0;

        while (true) {
            while (i < arr.length) {
                if ((w += font.charWidth(arr[i])) > x1) {
                    int j = i;

                    while (arr[j] != ' ') {
                        --j;
                        if (j < k) {
                            j = i;
                            break;
                        }
                    }

                    list.add(new String(arr, k, j - k));
                    k = arr[j] != ' ' && arr[j] != '\n' ? j : j + 1;
                    w = 0;
                    i = k;
                    x1 = x2;
                } else {
                    ++i;
                }
            }

            list.add(new String(arr, k, i - k));
            return;
        }
    }

}
