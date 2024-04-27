package emulator.lcdui;

public final class BoundsUtils {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int W = 2;
    public static final int H = 3;

    public BoundsUtils() {
    }

    public static boolean collides(int[] arr, int x, int y) {
        return arr[X] <= x &&
                arr[X] + arr[W] >= x &&
                arr[Y] <= y &&
                arr[Y] + arr[H] >= y;
    }

    public static boolean collides(int[] a, int[] b) {
        return !(((a[Y] + a[H]) < (b[Y])) || (a[Y] > (b[Y] + b[H])) || ((a[X] + a[W]) < b[X]) || (a[X] > (b[X] + b[W])));
    }

    public static boolean collides2(int[] a, int[] b) {
        return collides(a, b[X], b[Y])
                && collides(a, b[X] + b[W], b[Y])
                && collides(a, b[X], b[Y] + b[H])
                && collides(a, b[X] + b[W], b[Y] + b[H]);
    }
}
