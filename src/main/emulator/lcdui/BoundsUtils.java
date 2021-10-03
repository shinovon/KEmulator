package emulator.lcdui;

public final class BoundsUtils {
    public BoundsUtils() {
    }

    public static boolean collides(int[] arr, int x, int y) {
        if (arr[0] <= x && 
        		arr[0] + arr[2] >= x && 
        		arr[1] <= y && 
        		arr[1] + arr[3] >= y) {
            return true;
        }
        return false;
    }

    public static boolean collides(int[] array, int[] array2) {
        if (BoundsUtils.collides(array, array2[0], array2[1]) && BoundsUtils.collides(array, array2[0] + array2[2], array2[1]) && BoundsUtils.collides(array, array2[0], array2[1] + array2[3]) && BoundsUtils.collides(array, array2[0] + array2[2], array2[1] + array2[3])) {
            return true;
        }
        return false;
    }
}
