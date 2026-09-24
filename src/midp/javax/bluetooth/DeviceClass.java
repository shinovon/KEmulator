package javax.bluetooth;

/**
 * Represents Bluetooth Device Class (CoD) as defined by Bluetooth spec.
 * 
 * CoD is 24-bit: 
 * - bits 0-1: format type
 * - bits 2-7: minor device class
 * - bits 8-12: major device class
 * - bits 13-23: service classes
 */
public class DeviceClass {

    private final int record;

    public DeviceClass(final int record) {
        this.record = record;
    }

    /**
     * Returns service classes (bits 13-23).
     */
    public int getServiceClasses() {
        return record & 0xFFE000; // 0b111111111110000000000000
    }

    /**
     * Returns major device class (bits 8-12).
     */
    public int getMajorDeviceClass() {
        return record & 0x1F00;
    }

    /**
     * Returns minor device class (bits 2-7).
     */
    public int getMinorDeviceClass() {
        return record & 0xFC;
    }

    public int getRecord() {
        return record;
    }

    @Override
    public String toString() {
        return String.format("DeviceClass[0x%06X major=0x%02X minor=0x%02X service=0x%06X]",
                record, getMajorDeviceClass(), getMinorDeviceClass(), getServiceClasses());
    }
}
