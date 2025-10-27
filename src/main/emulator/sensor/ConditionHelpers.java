package emulator.sensor;
//https://github.com/hbao/phonemefeaturedevices/blob/master/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/ConditionHelpers.java
public final class ConditionHelpers {
	public ConditionHelpers() {
		super();
	}

	public static boolean checkOperator(final String s) {
		return s.equals("eq") || s.equals("gt") || s.equals("ge") || s.equals("lt") || s.equals("le");
	}

	public static boolean checkLowerOperator(final String s) {
		return s.equals("gt") || s.equals("ge");
	}

	public static boolean checkUpperOperator(final String s) {
		return s.equals("lt") || s.equals("le");
	}

	public static boolean checkValue(final String s, final double n, final double n2) {
		if (s.equals("eq")) {
			return Double.doubleToLongBits(n) == Double.doubleToLongBits(n2);
		}
		if (s.equals("gt")) {
			return n2 > n;
		}
		if (s.equals("ge")) {
			return checkValue("eq", n, n2) || checkValue("gt", n, n2);
		}
		if (s.equals("lt")) {
			return n2 < n;
		}
		return s.equals("le") && (checkValue("eq", n, n2) || checkValue("lt", n, n2));
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
