package javax.bluetooth;

public class DeviceClass {
	private static final int SERVICE_MASK = 0xffe000;
	private static final int MAJOR_MASK = 0x001f00;
	private static final int MINOR_MASK = 0x0000fc;

	private int record;
	public DeviceClass(int record) {
		this.record = record;

		if ((record & 0xff000000) != 0)
			throw new IllegalArgumentException();
	}

	public int getServiceClasses() {
		return record & SERVICE_MASK;
	}

	public int getMajorDeviceClass() {
		return record & MAJOR_MASK;
	}

	public int getMinorDeviceClass() {
		return record & MINOR_MASK;
	}
}
