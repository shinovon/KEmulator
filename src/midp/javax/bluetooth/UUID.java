package javax.bluetooth;

/**
 * Full implementation of Bluetooth UUID.
 * Supports 16-bit, 32-bit, and 128-bit UUIDs.
 * 
 * 16-bit and 32-bit UUIDs are represented as 128-bit UUIDs with Bluetooth base:
 * xxxxxxxx-0000-1000-8000-00805F9B34FB
 */
public class UUID {

    private static final String BASE_UUID_SUFFIX = "-0000-1000-8000-00805F9B34FB";
    private static final long BASE_MSB = 0x0000000000001000L;
    private static final long BASE_LSB = 0x800000805F9B34FBL;

    private final String uuidString; // normalized 128-bit string
    private final long shortUuid; // for 16/32-bit, else -1
    private final boolean isShort;

    /**
     * Creates UUID from 16-bit or 32-bit short value.
     * Value is unsigned.
     */
    public UUID(final long uuidValue) {
        if (uuidValue < 0 || uuidValue > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Short UUID out of range: " + uuidValue);
        }
        this.shortUuid = uuidValue;
        this.isShort = true;
        // Format as 128-bit with base
        if (uuidValue <= 0xFFFFL) {
            // 16-bit
            this.uuidString = String.format("%08X-0000-1000-8000-00805F9B34FB", uuidValue);
        } else {
            // 32-bit
            this.uuidString = String.format("%08X-0000-1000-8000-00805F9B34FB", uuidValue);
        }
    }

    /**
     * Creates UUID from 128-bit string.
     * If shortUUID=true, string is 16-bit or 32-bit hex without dashes.
     */
    public UUID(final String uuidValue, final boolean shortUUID) {
        if (uuidValue == null) throw new NullPointerException();
        String v = uuidValue.trim();
        if (shortUUID) {
            // v is hex string for 16/32-bit UUID
            if (v.length() > 8) throw new IllegalArgumentException("Short UUID too long");
            long val;
            try {
                val = Long.parseLong(v, 16);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid short UUID: " + v);
            }
            if (val < 0 || val > 0xFFFFFFFFL) throw new IllegalArgumentException("Short UUID out of range");
            this.shortUuid = val;
            this.isShort = true;
            this.uuidString = String.format("%08X-0000-1000-8000-00805F9B34FB", val);
        } else {
            // 128-bit UUID string: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx or without dashes? Spec says with dashes
            String normalized = v.toUpperCase();
            // Validate format
            if (normalized.length() == 32) {
                // No dashes, insert dashes
                normalized = normalized.substring(0, 8) + "-" + normalized.substring(8, 12) + "-" +
                        normalized.substring(12, 16) + "-" + normalized.substring(16, 20) + "-" + normalized.substring(20);
            }
            if (normalized.length() != 36) {
                throw new IllegalArgumentException("Invalid 128-bit UUID length: " + v);
            }
            // Check dashes positions
            if (normalized.charAt(8) != '-' || normalized.charAt(13) != '-' ||
                    normalized.charAt(18) != '-' || normalized.charAt(23) != '-') {
                throw new IllegalArgumentException("Invalid UUID format: " + v);
            }
            // Check if it is actually a short UUID in 128-bit form (base UUID).
            // Assign the final fields once after parsing: assignments in both the
            // try and catch blocks are rejected by javac's definite-assignment check.
            long parsedShortUuid = -1;
            boolean parsedIsShort = false;
            String suffix = normalized.substring(8);
            if (suffix.equalsIgnoreCase(BASE_UUID_SUFFIX)) {
                String shortPart = normalized.substring(0, 8);
                try {
                    parsedShortUuid = Long.parseLong(shortPart, 16);
                    parsedIsShort = true;
                } catch (NumberFormatException ignored) {
                    // Keep the default values for a non-short UUID.
                }
            }
            this.shortUuid = parsedShortUuid;
            this.isShort = parsedIsShort;
            this.uuidString = normalized;
        }
    }

    @Override
    public String toString() {
        // For short UUIDs, return short form without base? Spec says toString returns UUID string
        // We'll return full 128-bit string, but for short we return 8-digit hex? Let's return full for consistency
        // However many MIDlets expect short form for 16-bit UUIDs like "1101"
        // We'll return short form if isShort and value <= 0xFFFF, else full
        if (isShort) {
            if (shortUuid <= 0xFFFFL) {
                return String.format("%04X", shortUuid);
            } else {
                return String.format("%08X", shortUuid);
            }
        }
        return uuidString;
    }

    /**
     * Returns 128-bit UUID string.
     */
    public String toString128() {
        return uuidString;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof UUID) {
            UUID other = (UUID) obj;
            return this.uuidString.equalsIgnoreCase(other.uuidString);
        }
        if (obj instanceof String) {
            return this.uuidString.equalsIgnoreCase((String) obj) ||
                    this.toString().equalsIgnoreCase((String) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuidString.toUpperCase().hashCode();
    }

    public long getShortUuid() {
        return shortUuid;
    }

    public boolean isShort() {
        return isShort;
    }
}
