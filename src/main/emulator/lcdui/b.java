package emulator.lcdui;

public final class b {
    public b() {
    }

    public static boolean method172(int[] array, int n, int n2) {
        if (array[0] <= n && array[0] + array[2] >= n && array[1] <= n2 && array[1] + array[3] >= n2) {
            return true;
        }
        return false;
    }

    public static boolean method173(int[] array, int[] array2) {
        if (b.method172(array, array2[0], array2[1]) && b.method172(array, array2[0] + array2[2], array2[1]) && b.method172(array, array2[0], array2[1] + array2[3]) && b.method172(array, array2[0] + array2[2], array2[1] + array2[3])) {
            return true;
        }
        return false;
    }
}
