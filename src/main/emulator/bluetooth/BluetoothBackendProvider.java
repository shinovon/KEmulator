package emulator.bluetooth;

import javax.bluetooth.BluetoothStateException;

/**
 * Selects and owns the process-wide Bluetooth backend.
 *
 * <p>The default is the LAN {@link BluetoothStack}. A future backend can be
 * selected with {@code -Dkemulator.bluetooth.backend=fully.qualified.Class};
 * it must implement {@link BluetoothBackend} and expose a public no-argument
 * constructor. This selection is intentionally host-side configuration, not a
 * MIDlet-visible JSR-82 property.</p>
 */
public final class BluetoothBackendProvider {

    private static BluetoothBackend backend;

    private BluetoothBackendProvider() {}

    public static synchronized BluetoothBackend getInstance() throws BluetoothStateException {
        if (backend == null) {
            // Do not create a second backend if a legacy caller initialized
            // the historical LAN singleton before this provider was reached.
            BluetoothStack existingLanBackend = BluetoothStack.getInstanceIfExists();
            if (existingLanBackend != null) {
                backend = existingLanBackend;
            } else {
                String configuredClass = BluetoothConfiguration.getBackendClassName();
                if (configuredClass == null || configuredClass.equalsIgnoreCase("lan") ||
                        configuredClass.equals(BluetoothStack.class.getName())) {
                    backend = BluetoothStack.getInstance();
                } else {
                    backend = createConfiguredBackend(configuredClass);
                }
            }
        }
        return backend;
    }

    /** Returns the selected backend without forcing Bluetooth initialization. */
    public static synchronized BluetoothBackend getInstanceIfExists() {
        if (backend != null) return backend;
        // Preserve compatibility with callers that initialized the historical
        // BluetoothStack singleton before the provider was reached.
        return BluetoothStack.getInstanceIfExists();
    }

    /** Stops the selected backend and releases this provider's lifecycle reference. */
    public static void shutdown() {
        BluetoothBackend toShutdown;
        synchronized (BluetoothBackendProvider.class) {
            toShutdown = backend;
            backend = null;
        }
        if (toShutdown == null) {
            toShutdown = BluetoothStack.getInstanceIfExists();
        }
        if (toShutdown != null) {
            toShutdown.shutdown();
        }
    }

    /** Removes a backend that was stopped through a legacy direct lifecycle. */
    static synchronized void release(BluetoothBackend stoppedBackend) {
        if (backend == stoppedBackend) {
            backend = null;
        }
    }

    private static BluetoothBackend createConfiguredBackend(String className) throws BluetoothStateException {
        try {
            Class<?> type = Class.forName(className);
            if (!BluetoothBackend.class.isAssignableFrom(type)) {
                throw new BluetoothStateException("Configured Bluetooth backend does not implement BluetoothBackend: " + className);
            }
            return (BluetoothBackend) type.getDeclaredConstructor().newInstance();
        } catch (BluetoothStateException e) {
            throw e;
        } catch (Exception e) {
            throw new BluetoothStateException("Failed to create Bluetooth backend " + className + ": " + e.getMessage());
        }
    }
}
