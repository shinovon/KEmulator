package emulator.sensor;

public final class ConditionHelpers {
	public ConditionHelpers() {
		super();
	}

	public static boolean method241(final String s) {
		return s.equals("eq") || s.equals("gt") || s.equals("ge") || s.equals("lt") || s.equals("le");
	}

	public static boolean method244(final String s) {
		return s.equals("gt") || s.equals("ge");
	}

	public static boolean method245(final String s) {
		return s.equals("lt") || s.equals("le");
	}

	public static boolean method242(final String s, final double n, final double n2) {
		if (s.equals("eq")) {
			return Double.doubleToLongBits(n) == Double.doubleToLongBits(n2);
		}
		if (s.equals("gt")) {
			return n2 > n;
		}
		if (s.equals("ge")) {
			return method242("eq", n, n2) || method242("gt", n, n2);
		}
		if (s.equals("lt")) {
			return n2 < n;
		}
		return s.equals("le") && (method242("eq", n, n2) || method242("lt", n, n2));
	}

	public static double resolve(final double n, final int n2) {
		double n3 = 1.0;
		for (int i = 0; i < Math.abs(n2); ++i) {
			n3 *= 10.0;
		}
		if (n2 > 0) {
			return n * n3;
		}
		return n / n3;
	}
}
