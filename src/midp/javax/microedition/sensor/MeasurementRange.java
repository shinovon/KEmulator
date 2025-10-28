package javax.microedition.sensor;

public class MeasurementRange {
	private double smallest;
	private double largest;
	private double resolution;

	public MeasurementRange(final double smallest, final double largest, final double resolution) {
		super();
		if (resolution < 0.0 || smallest > largest) {
			throw new IllegalArgumentException();
		}
		this.smallest = smallest;
		this.largest = largest;
		this.resolution = resolution;
	}

	public double getLargestValue() {
		return this.largest;
	}

	public double getResolution() {
		return this.resolution;
	}

	public double getSmallestValue() {
		return this.smallest;
	}
}
