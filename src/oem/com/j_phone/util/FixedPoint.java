package com.j_phone.util;

public class FixedPoint {
	private static final int PI = fromDouble((double) Math.PI);
	private static final int MAX = (32767 << 16) | 0xFFFF;
	private static final int MIN = -32768 << 16;

	int n;
	boolean infinite;

	public FixedPoint() {
	}

	public FixedPoint(int n) {
		this.n = n;
	}

	public int getInteger() {
		int i = n >> 16;
		if (i < 0 && (n & 0xFFFF) != 0) {
			++i;
		}
		return i;
	}

	public int getDecimal() {
		int i = n & 0xFFFF;
		if (n >> 16 < 0) {
			i = -i;
		}
		return i;
	}

	public void setValue(int n) {
		this.n = n;
	}

	public FixedPoint add(FixedPoint other) {
		n = add(n, other.n);
		return this;
	}

	public FixedPoint add(int other) {
		n = add(n, other);
		return this;
	}

	public FixedPoint subtract(FixedPoint other) {
		n = subtract(n, other.n);
		return this;
	}

	public FixedPoint subtract(int other) {
		n = subtract(n, other);
		return this;
	}

	public FixedPoint multiply(FixedPoint other) {
		setDouble(toDouble() * other.toDouble());
		return this;
	}

	public FixedPoint multiply(int other) {
		setDouble(toDouble() * toDouble(other));
		return this;
	}

	public FixedPoint divide(FixedPoint other) {
		setDouble(toDouble() / other.toDouble());
		return this;
	}

	public FixedPoint divide(int other) {
		setDouble(toDouble() / toDouble(other));
		return this;
	}

	public FixedPoint sin(FixedPoint paramFixedPoint) {
		setDouble((double) Math.sin(paramFixedPoint.toDouble()));
		return this;
	}

	public FixedPoint cos(FixedPoint paramFixedPoint) {
		setDouble((double) Math.cos(paramFixedPoint.toDouble()));
		return this;
	}

	public FixedPoint tan(FixedPoint paramFixedPoint) {
		setDouble((double) Math.tan(paramFixedPoint.toDouble()));
		return this;
	}

	public FixedPoint asin(FixedPoint paramFixedPoint) {
		setDouble((double) Math.asin(paramFixedPoint.toDouble()));
		return this;
	}

	public FixedPoint acos(FixedPoint paramFixedPoint) {
		setDouble((double) Math.acos(paramFixedPoint.toDouble()));
		return this;
	}

	public FixedPoint atan(FixedPoint paramFixedPoint) {
		setDouble((double) Math.atan(paramFixedPoint.toDouble()));
		return this;
	}

	public FixedPoint sqrt() {
		setDouble((double) Math.sqrt(toDouble()));
		return this;
	}

	public FixedPoint inverse() {
		setDouble(1f / toDouble());
		return this;
	}

	public FixedPoint pow() {
		double a = toDouble();
		setDouble(a * a);
		return this;
	}

	public boolean isInfinite() {
		return infinite;
	}

	public FixedPoint clone() {
		return new FixedPoint(n);
	}

	public static FixedPoint getPI() {
		return new FixedPoint(PI);
	}

	public static FixedPoint getMaximum() {
		return new FixedPoint(MAX);
	}

	public static FixedPoint getMinimum() {
		return new FixedPoint(MIN);
	}

	double toDouble() {
		return toDouble(n);
	}

	static double toDouble(int n) {
		return (n >> 16) + ((n & 0xFFFF) / 65536D);
	}

	static int fromDouble(double f) {
		if (f % 1 == 0) {
			return (((int) f) % 0xFFFF) << 16;
		}
		if (f < 0) {
			return ((int) f - 1) << 16 | (((int) ((f % 1) * 65536D)) & 0xFFFF);
		}
		return (((int) f) % 0xFFFF) << 16 | (((int) ((f % 1) * 65536D)) & 0xFFFF);
	}

	void setDouble(double f) {
		infinite = Double.isInfinite(f);
		n = fromDouble(f);
	}

	static int add(int a, int b) {
		return (((a >> 16) + (b >> 16)) << 16) | ((a & 0xFFFF) + (b & 0xFFFF));
	}

	static int subtract(int a, int b) {
		return fromDouble(toDouble(a) - toDouble(b));
	}
}
